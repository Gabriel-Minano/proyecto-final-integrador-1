package com.sistema.botica.service;

import java.awt.Color;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.time.format.DateTimeFormatter;

import org.apache.poi.ss.usermodel.BorderStyle;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellStyle;
import org.apache.poi.ss.usermodel.FillPatternType;
import org.apache.poi.ss.usermodel.HorizontalAlignment;
import org.apache.poi.ss.usermodel.IndexedColors;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.openpdf.text.*;
import org.openpdf.text.pdf.PdfPCell;
import org.openpdf.text.pdf.PdfPTable;
import org.openpdf.text.pdf.PdfWriter;
import org.springframework.stereotype.Service;

import com.sistema.botica.DTO.ReporteVentasDTO;
import com.sistema.botica.entity.Venta;
import java.time.LocalDateTime;

@Service
public class VentaReporteExportService {
	// Nombre de la empresa
    private static final String EMPRESA = "BOTICA CONQUISTADORES FARMA";
    // Formatos de fecha
    private final DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm");
    private final DateTimeFormatter formatterFecha = DateTimeFormatter.ofPattern("dd/MM/yyyy");

	// ==========================================
    // EXPORTACIÓN A EXCEL
    // ==========================================
    public byte[] generarExcel(ReporteVentasDTO reporte, java.time.LocalDateTime inicio, java.time.LocalDateTime fin) throws IOException {
        try (Workbook workbook = new XSSFWorkbook(); ByteArrayOutputStream out = new ByteArrayOutputStream()) {
            Sheet sheet = workbook.createSheet("Ventas");
            
            // Mostrar líneas de cuadrícula de Excel de forma nativa
            sheet.setDisplayGridlines(true);

            // === PALETA DE COLORES Y FUENTES ===
            // 1. Fuente para el Título Principal (Grande)
            org.apache.poi.ss.usermodel.Font titleFont = workbook.createFont();
            titleFont.setFontHeightInPoints((short) 18);
            titleFont.setBold(true);
            titleFont.setColor(IndexedColors.BLUE.getIndex());

            // 2. Fuente para los Subtítulos de Sección (Mediana)
            org.apache.poi.ss.usermodel.Font sectionFont = workbook.createFont();
            sectionFont.setFontHeightInPoints((short) 12);
            sectionFont.setBold(true);
            sectionFont.setColor(IndexedColors.BLUE.getIndex());

            // 3. Fuentes para las Tarjetas KPI
            org.apache.poi.ss.usermodel.Font kpiStyleFont = workbook.createFont();
            kpiStyleFont.setFontHeightInPoints((short) 9);
            kpiStyleFont.setBold(true);
            kpiStyleFont.setColor(IndexedColors.GREY_50_PERCENT.getIndex());

            // 4. Fuentes para la Tabla y Totales
            org.apache.poi.ss.usermodel.Font headerFont = workbook.createFont();
            headerFont.setFontHeightInPoints((short) 10);
            headerFont.setBold(true);
            headerFont.setColor(IndexedColors.WHITE.getIndex());

            org.apache.poi.ss.usermodel.Font totalFont = workbook.createFont();
            totalFont.setFontHeightInPoints((short) 10);
            totalFont.setBold(true);
            totalFont.setColor(IndexedColors.BLUE.getIndex());

            // === ESTILOS DE CELDA ===
            CellStyle titleStyle = workbook.createCellStyle();
            titleStyle.setFont(titleFont);

            CellStyle sectionStyle = workbook.createCellStyle();
            sectionStyle.setFont(sectionFont);

            CellStyle kpiStyle = workbook.createCellStyle();
            kpiStyle.setFillForegroundColor(IndexedColors.GREY_25_PERCENT.getIndex()); // Fondo gris claro sutil
            kpiStyle.setFillPattern(FillPatternType.SOLID_FOREGROUND);
            kpiStyle.setBorderBottom(BorderStyle.THIN);
            kpiStyle.setBorderTop(BorderStyle.THIN);
            kpiStyle.setBorderLeft(BorderStyle.THIN);
            kpiStyle.setBorderRight(BorderStyle.THIN);
            kpiStyle.setWrapText(true);

            CellStyle headerStyle = workbook.createCellStyle();
            headerStyle.setFillForegroundColor(IndexedColors.BLUE.getIndex()); // Azul Eléctrico
            headerStyle.setFillPattern(FillPatternType.SOLID_FOREGROUND);
            headerStyle.setFont(headerFont);
            headerStyle.setAlignment(HorizontalAlignment.CENTER);

            CellStyle totalStyle = workbook.createCellStyle();
            totalStyle.setFont(totalFont);
            totalStyle.setFillForegroundColor(IndexedColors.LIGHT_TURQUOISE.getIndex()); // Fondo sutil para el total consolidado
            totalStyle.setFillPattern(FillPatternType.SOLID_FOREGROUND);
            totalStyle.setBorderTop(BorderStyle.MEDIUM);
            totalStyle.setBorderBottom(BorderStyle.DOUBLE);

            // === 1. ENCABEZADO ===
            Row r0 = sheet.createRow(0);
            Cell c0 = r0.createCell(0);
            c0.setCellValue(EMPRESA);
            c0.setCellStyle(titleStyle); // Tamaño Grande (18pt)

            Row r1 = sheet.createRow(1);
            r1.createCell(0).setCellValue("Reporte de Ventas");
            
            // Fecha de generación
            String fechaReporte = LocalDateTime.now().format(formatter);
            Row r2 = sheet.createRow(2);
            r2.createCell(0).setCellValue("Fecha de generación: " + fechaReporte);

            // NUEVO: Periodo del reporte justo debajo de la fecha de generación
            String textoPeriodo = "Periodo del reporte: "
            + inicio.format(formatterFecha)
            + " al "
            + fin.format(formatterFecha);
            Row r3 = sheet.createRow(3);
            r3.createCell(0).setCellValue(textoPeriodo);

            // === 2. SECCIÓN: RESUMEN DEL PERIODO (KPIs) ===
            // Desplazamos las filas de los KPIs una posición hacia abajo para dar espacio al periodo
            Row r5 = sheet.createRow(5);
            Cell cSec1 = r5.createCell(0);
            cSec1.setCellValue("| RESUMEN DEL PERIODO");
            cSec1.setCellStyle(sectionStyle); // Tamaño Mediano (12pt)

            // Fila 6: Títulos de las tarjetas KPI 
            Row r6 = sheet.createRow(6);
            Cell k1 = r6.createCell(0); k1.setCellValue("TOTAL RECAUDADO");     k1.setCellStyle(kpiStyle);
            Cell k2 = r6.createCell(2); k2.setCellValue("PRODUCTOS VENDIDOS");  k2.setCellStyle(kpiStyle);
            Cell k3 = r6.createCell(4); k3.setCellValue("PRODUCTO MÁS VENDIDO");k3.setCellStyle(kpiStyle);
            Cell k4 = r6.createCell(6); k4.setCellValue("MES CON MÁS VENTAS");  k4.setCellStyle(kpiStyle);

            // Fila 7: Valores de las tarjetas KPI
            Row r7 = sheet.createRow(7);
            Cell v1 = r7.createCell(0); v1.setCellValue("S/ " + reporte.getTotalVentas()); v1.setCellStyle(kpiStyle);
            Cell v2 = r7.createCell(2); v2.setCellValue(reporte.getProductosVendidos() + " unidades"); v2.setCellStyle(kpiStyle);
            Cell v3 = r7.createCell(4); v3.setCellValue(reporte.getProductoMasVendido()); v3.setCellStyle(kpiStyle);
            Cell v4 = r7.createCell(6); v4.setCellValue(reporte.getMesMasVentas()); v4.setCellStyle(kpiStyle);

            // Rellenar con estilo las celdas ocultas del merge para mantener bordes continuos
            for (int i = 0; i <= 6; i++) {
                if (i != 0 && i != 2 && i != 4 && i != 6) {
                    r6.createCell(i).setCellStyle(kpiStyle);
                    r7.createCell(i).setCellStyle(kpiStyle);
                }
            }

            // Aplicar el combinado (Merge) de 2 columnas para las tarjetas KPI (ajustado a filas 6 y 7)
            sheet.addMergedRegion(new org.apache.poi.ss.util.CellRangeAddress(6, 6, 0, 1)); // KPI 1 Título (Col A y B)
            sheet.addMergedRegion(new org.apache.poi.ss.util.CellRangeAddress(7, 7, 0, 1)); // KPI 1 Valor (Col A y B)

            sheet.addMergedRegion(new org.apache.poi.ss.util.CellRangeAddress(6, 6, 2, 3)); // KPI 2 Título (Col C y D)
            sheet.addMergedRegion(new org.apache.poi.ss.util.CellRangeAddress(7, 7, 2, 3)); // KPI 2 Valor (Col C y D)

            sheet.addMergedRegion(new org.apache.poi.ss.util.CellRangeAddress(6, 6, 4, 5)); // KPI 3 Título (Col E y F)
            sheet.addMergedRegion(new org.apache.poi.ss.util.CellRangeAddress(7, 7, 4, 5)); // KPI 3 Valor (Col E y F)

            // === 3. SECCIÓN: RESULTADOS DEL REPORTE ===
            // Desplazamos la tabla una fila hacia abajo debido al nuevo texto agregado arriba
            Row r9 = sheet.createRow(9);
            Cell cSec2 = r9.createCell(0);
            cSec2.setCellValue("| RESULTADOS DEL REPORTE");
            cSec2.setCellStyle(sectionStyle); // Tamaño Mediano (12pt)

            // Cabecera de la Tabla principal
            Row headerRow = sheet.createRow(10);
            String[] columns = { "N°", "FECHA", "CLIENTE", "CAJERO", "ITEMS", "UNIDADES", "TOTAL (S/.)" };
            for (int i = 0; i < columns.length; i++) {
                Cell cell = headerRow.createCell(i);
                cell.setCellValue(columns[i]);
                cell.setCellStyle(headerStyle);
            }

            // === 4. LLENAR FILAS DE DATOS ===
            int rowIdx = 11;
            int totalItems = 0;
            int totalUnidades = 0;
            double totalConsolidado = 0.0;

            for (Venta v : reporte.getPaginaVentas().getContent()) {
                Row row = sheet.createRow(rowIdx++);
                
                row.createCell(0).setCellValue(String.format("%02d", v.getIdVenta()));
                row.createCell(1).setCellValue(v.getFecha().format(DateTimeFormatter.ofPattern("dd/MM/yyyy")));
                row.createCell(2).setCellValue(v.getCliente().getNombre() + " " + v.getCliente().getApellido());
                row.createCell(3).setCellValue(v.getUsuario().getNombre());
                
                int itemsCount = v.getListaDetallesVenta().size();
                int unidadesFisicas = v.getListaDetallesVenta().stream().mapToInt(dv -> dv.getCantidad()).sum();
                double totalVenta = v.getTotal().doubleValue();

                totalItems += itemsCount;
                totalUnidades += unidadesFisicas;
                totalConsolidado += totalVenta;

                row.createCell(4).setCellValue(itemsCount);
                row.createCell(5).setCellValue(unidadesFisicas);
                row.createCell(6).setCellValue(totalVenta);
            }

            // === 5. FILA DE TOTAL CONSOLIDADO ===
            Row totalRow = sheet.createRow(rowIdx);
            
            // Combinar celdas de la columna 0 a la 3 para poner el texto de cierre
            Cell labelTotal = totalRow.createCell(0);
            labelTotal.setCellValue("TOTAL CONSOLIDADO:");
            labelTotal.setCellStyle(totalStyle);
            
            // Forzar el estilo en las celdas del Merge para que los bordes y fondos no se corten
            for (int i = 1; i <= 3; i++) {
                totalRow.createCell(i).setCellStyle(totalStyle);
            }
            sheet.addMergedRegion(new org.apache.poi.ss.util.CellRangeAddress(rowIdx, rowIdx, 0, 3));

            // Celda Total Items
            Cell cellTotItems = totalRow.createCell(4);
            cellTotItems.setCellValue(totalItems);
            cellTotItems.setCellStyle(totalStyle);

            // Celda Total Unidades
            Cell cellTotUnidades = totalRow.createCell(5);
            cellTotUnidades.setCellValue(totalUnidades);
            cellTotUnidades.setCellStyle(totalStyle);

            // Celda Total Dinero
            Cell cellTotDinero = totalRow.createCell(6);
            cellTotDinero.setCellValue(totalConsolidado);
            cellTotDinero.setCellStyle(totalStyle);

            // === 6. AJUSTE DE ANCHO DE COLUMNAS ===
            // Asigna un ancho fijo ajustado para la columna 0 (N°) evitando que se estire por los KPIs
            sheet.setColumnWidth(0, 5 * 256); 

            // Autoajustar el resto de las columnas dinámicamente (desde la 1 hasta la 6)
            for (int i = 1; i < columns.length; i++) {
                sheet.autoSizeColumn(i);
            }

            workbook.write(out);
            return out.toByteArray();
        }
    }

	// ==========================================
    // EXPORTACIÓN A PDF
    // ==========================================
    public byte[] generarPdf(ReporteVentasDTO reporte, java.time.LocalDateTime inicio, java.time.LocalDateTime fin) throws IOException {
        try (ByteArrayOutputStream out = new ByteArrayOutputStream()) {
            // Configurar documento con márgenes limpios
            Document document = new Document(PageSize.A4, 36, 36, 36, 36);
            PdfWriter.getInstance(document, out);
            document.open();

            // === FUENTES ===
            Font mainTitleFont = FontFactory.getFont(FontFactory.HELVETICA_BOLD, 22, new Color(0, 0, 255)); // Azul_Intenso
            Font subTitleFont = FontFactory.getFont(FontFactory.HELVETICA_BOLD, 14, new Color(51, 65, 85)); // Gris_Oscuro
            Font metaFont = FontFactory.getFont(FontFactory.HELVETICA, 9, new Color(100, 116, 139)); //Gris_Claro
            Font sectionTitleFont = FontFactory.getFont(FontFactory.HELVETICA_BOLD, 12, new Color(0, 0, 255)); // Azul_Intenso
            
            Font kpiTitleFont = FontFactory.getFont(FontFactory.HELVETICA_BOLD, 8, new Color(100, 116, 139));
            Font kpiValueFont = FontFactory.getFont(FontFactory.HELVETICA_BOLD, 12, new Color(0, 0, 153)); // Azul KPI
            Font kpiTextFont = FontFactory.getFont(FontFactory.HELVETICA_BOLD, 11, new Color(15, 23, 42));

            Font headerFont = FontFactory.getFont(FontFactory.HELVETICA_BOLD, 9, Color.WHITE);
            Font cellFont = FontFactory.getFont(FontFactory.HELVETICA, 9, new Color(51, 65, 85));
            Font totalFont = FontFactory.getFont(FontFactory.HELVETICA_BOLD, 10, new Color(0, 0, 255));

            // === ENCABEZADO ===
            Paragraph title = new Paragraph(EMPRESA, mainTitleFont);
            title.setAlignment(Element.ALIGN_CENTER);
            title.setSpacingAfter(4);
            document.add(title);
            
            Paragraph sub = new Paragraph("Reporte de Ventas", subTitleFont);
            sub.setAlignment(Element.ALIGN_CENTER);
            sub.setSpacingAfter(4);
            document.add(sub);

            String fechaReporte = LocalDateTime.now().format(formatter);
            Paragraph metaGen = new Paragraph("Fecha de generación: " + fechaReporte, metaFont);
            metaGen.setAlignment(Element.ALIGN_CENTER);
            document.add(metaGen);

            DateTimeFormatter dateFormatter = DateTimeFormatter.ofPattern("dd/MM/yyyy");
            String textoPeriodo = "Periodo del reporte: "
            + inicio.format(formatterFecha)
            + " al "
            + fin.format(formatterFecha);
            Paragraph periodo = new Paragraph(textoPeriodo, metaFont);
            periodo.setAlignment(Element.ALIGN_CENTER);
            periodo.setSpacingAfter(15);
            document.add(periodo);

            // Línea divisoria sutil bajo el encabezado
            PdfPTable lineTable = new PdfPTable(1);
            lineTable.setWidthPercentage(100);
            PdfPCell lineCell = new PdfPCell();
            lineCell.setBorder(Rectangle.BOTTOM);
            lineCell.setBorderColor(new Color(226, 232, 240));
            lineCell.setPadding(0);
            lineTable.addCell(lineCell);
            lineTable.setSpacingAfter(15);
            document.add(lineTable);

            // === SECCIÓN: RESUMEN DEL PERIODO ===
            Paragraph secResumen = new Paragraph("| RESUMEN DEL PERIODO", sectionTitleFont);
            secResumen.setSpacingAfter(10);
            document.add(secResumen);

            // === TARJETAS EN FILA (KPIs) ===
            // Tabla contenedora de 4 columnas (una por cada indicador)
            PdfPTable kpiContainer = new PdfPTable(4);
            kpiContainer.setWidthPercentage(100);
            kpiContainer.setSpacingAfter(20);
            // Espaciado relativo entre tarjetas simulado por celdas internas, le damos anchos iguales
            kpiContainer.setWidths(new float[] { 1f, 1.8f, 2f, 1.8f }); 

            Color bgCard = new Color(241, 245, 249); // Fondo gris muy claro azulado
            Color borderCard = new Color(226, 232, 240); // Borde sutil

            // 1. KPI: Total Recaudado
            PdfPCell card1 = createKPICard("TOTAL RECAUDADO", "S/ " + reporte.getTotalVentas(), kpiTitleFont, kpiValueFont, bgCard, borderCard);
            kpiContainer.addCell(card1);

            // 2. KPI: Productos Vendidos
            PdfPCell card2 = createKPICard("PRODUCTOS\nVENDIDOS", reporte.getProductosVendidos() + " unidades", kpiTitleFont, kpiTextFont, bgCard, borderCard);
            kpiContainer.addCell(card2);

            // 3. KPI: Producto más Vendido
            PdfPCell card3 = createKPICard("PRODUCTO MÁS\nVENDIDO", reporte.getProductoMasVendido(), kpiTitleFont, kpiTextFont, bgCard, borderCard);
            kpiContainer.addCell(card3);

            // 4. KPI: Mes con más Ventas
            PdfPCell card4 = createKPICard("MES CON MÁS VENTAS", reporte.getMesMasVentas(), kpiTitleFont, kpiTextFont, bgCard, borderCard);
            kpiContainer.addCell(card4);

            document.add(kpiContainer);

            // === SECCIÓN: RESULTADOS DEL REPORTE ===
            Paragraph secDetalle = new Paragraph("| RESULTADOS DEL REPORTE", sectionTitleFont);
            secDetalle.setSpacingAfter(10);
            document.add(secDetalle);

            // === TABLA DE DETALLES ===
            PdfPTable table = new PdfPTable(7);
            table.setWidthPercentage(100);
            table.setWidths(new float[] { 0.8f, 1.8f, 3.2f, 2.2f, 1f, 1.2f, 1.5f });

            // Cabecera de la tabla (Azul Eléctrico)
            String[] headers = { "N°", "FECHA", "CLIENTE", "CAJERO", "ITEMS", "SUM.UNIDADES", "TOTAL (S/.)" };
            for (String h : headers) {
                PdfPCell cell = new PdfPCell(new Phrase(h, headerFont));
                cell.setBackgroundColor(new Color(0, 0, 255)); // Azul idéntico a la imagen 2
                cell.setPaddingTop(8);
                cell.setPaddingBottom(8);
                cell.setHorizontalAlignment(Element.ALIGN_CENTER);
                cell.setVerticalAlignment(Element.ALIGN_MIDDLE);
                cell.setBorder(Rectangle.NO_BORDER); // Quitar bordes internos negros
                table.addCell(cell);
            }

            // Filas de datos
            int cont = 0;
            int totalItems = 0;
            int totalUnidades = 0;
            double totalConsolidado = 0.0;

            for (Venta v : reporte.getPaginaVentas().getContent()) {
                // Alternar color de fondo para filas pares/impares si lo deseas o mantener blanco con líneas sutiles
                Color bgRow = (cont % 2 == 0) ? Color.WHITE : new Color(248, 250, 252);
                
                table.addCell(createTableCell(String.format("%02d", v.getIdVenta()), cellFont, bgRow, Element.ALIGN_CENTER));
                table.addCell(createTableCell(v.getFecha().toLocalDate().format(DateTimeFormatter.ofPattern("dd/MM/yyyy")), cellFont, bgRow, Element.ALIGN_CENTER));
                table.addCell(createTableCell(v.getCliente().getNombre() + " " + v.getCliente().getApellido(), cellFont, bgRow, Element.ALIGN_LEFT));
                table.addCell(createTableCell(v.getUsuario().getNombre(), cellFont, bgRow, Element.ALIGN_LEFT));

                int itemsCount = v.getListaDetallesVenta().size();
                int unidadesFisicas = v.getListaDetallesVenta().stream().mapToInt(dv -> dv.getCantidad()).sum();
                double totalVenta = v.getTotal().doubleValue();

                totalItems += itemsCount;
                totalUnidades += unidadesFisicas;
                totalConsolidado += totalVenta;

                table.addCell(createTableCell(String.valueOf(itemsCount), cellFont, bgRow, Element.ALIGN_CENTER));
                table.addCell(createTableCell(String.valueOf(unidadesFisicas), cellFont, bgRow, Element.ALIGN_CENTER));
                table.addCell(createTableCell(String.format("%.2f", totalVenta), cellFont, bgRow, Element.ALIGN_RIGHT));
                
                cont++;
            }

            // === FILA DE TOTAL CONSOLIDADO ===
            // Celda vacía que abarca las primeras 4 columnas
            PdfPCell lblTotalCell = new PdfPCell(new Phrase("TOTAL CONSOLIDADO:", totalFont));
            lblTotalCell.setColspan(4);
            lblTotalCell.setHorizontalAlignment(Element.ALIGN_RIGHT);
            lblTotalCell.setVerticalAlignment(Element.ALIGN_MIDDLE);
            lblTotalCell.setPadding(8);
            lblTotalCell.setBackgroundColor(new Color(239, 246, 255)); // Fondo azul claro sutil
            lblTotalCell.setBorder(Rectangle.TOP | Rectangle.BOTTOM);
            lblTotalCell.setBorderColor(new Color(0, 0, 255));
            table.addCell(lblTotalCell);

            // Suma Items
            PdfPCell totItemsCell = new PdfPCell(new Phrase(String.valueOf(totalItems), totalFont));
            totItemsCell.setHorizontalAlignment(Element.ALIGN_CENTER);
            totItemsCell.setVerticalAlignment(Element.ALIGN_MIDDLE);
            totItemsCell.setBackgroundColor(new Color(239, 246, 255));
            totItemsCell.setBorder(Rectangle.TOP | Rectangle.BOTTOM);
            totItemsCell.setBorderColor(new Color(0, 0, 255));
            table.addCell(totItemsCell);

            // Suma Unidades
            PdfPCell totUnidadesCell = new PdfPCell(new Phrase(String.valueOf(totalUnidades), totalFont));
            totUnidadesCell.setHorizontalAlignment(Element.ALIGN_CENTER);
            totUnidadesCell.setVerticalAlignment(Element.ALIGN_MIDDLE);
            totUnidadesCell.setBackgroundColor(new Color(239, 246, 255));
            totUnidadesCell.setBorder(Rectangle.TOP | Rectangle.BOTTOM);
            totUnidadesCell.setBorderColor(new Color(0, 0, 255));
            table.addCell(totUnidadesCell);

            // Suma Total Dinero
            PdfPCell totDineroCell = new PdfPCell(new Phrase("S/ " + String.format("%.2f", totalConsolidado), totalFont));
            totDineroCell.setHorizontalAlignment(Element.ALIGN_RIGHT);
            totDineroCell.setVerticalAlignment(Element.ALIGN_MIDDLE);
            totDineroCell.setBackgroundColor(new Color(239, 246, 255));
            totDineroCell.setBorder(Rectangle.TOP | Rectangle.BOTTOM);
            totDineroCell.setBorderColor(new Color(0, 0, 255));
            table.addCell(totDineroCell);

            document.add(table);
            document.close();
            return out.toByteArray();
        }
    }

    // Métodos auxiliares para mantener limpio el código:

    private PdfPCell createKPICard(String title, String value, Font titleFont, Font valFont, Color bg, Color border) {
        PdfPCell cell = new PdfPCell();
        cell.setBackgroundColor(bg);
        cell.setBorderColor(border);
        cell.setBorderWidth(1f);
        cell.setPadding(8);
        
        // Usamos espaciado entre párrafos dentro de la celda
        Paragraph pTitle = new Paragraph(title, titleFont);
        pTitle.setSpacingAfter(6);
        cell.addElement(pTitle);
        
        Paragraph pValue = new Paragraph(value, valFont);
        cell.addElement(pValue);
        
        return cell;
    }

    private PdfPCell createTableCell(String text, Font font, Color bg, int alignment) {
        PdfPCell cell = new PdfPCell(new Phrase(text, font));
        cell.setBackgroundColor(bg);
        cell.setPaddingTop(8);
        cell.setPaddingBottom(8);
        cell.setHorizontalAlignment(alignment);
        cell.setVerticalAlignment(Element.ALIGN_MIDDLE);
        // Línea inferior sutil en lugar de cuadrícula completa
        cell.setBorder(Rectangle.BOTTOM); 
        cell.setBorderColor(new Color(241, 245, 249));
        return cell;
    }
}
