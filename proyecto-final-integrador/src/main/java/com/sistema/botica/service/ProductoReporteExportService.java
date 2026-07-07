package com.sistema.botica.service;

import java.awt.Color;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

import org.apache.poi.ss.usermodel.*;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.util.CellRangeAddress;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.openpdf.text.*;
import org.openpdf.text.Font;
import org.openpdf.text.pdf.PdfPCell;
import org.openpdf.text.pdf.PdfPTable;
import org.openpdf.text.pdf.PdfWriter;
import org.springframework.stereotype.Service;

import com.sistema.botica.DTO.ReporteProductosDTO;
import com.sistema.botica.entity.DetalleVenta;

@Service
public class ProductoReporteExportService {

    private static final String EMPRESA = "BOTICA CONQUISTADORES FARMA";
    private final DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm");
    private final DateTimeFormatter dateFormatter = DateTimeFormatter.ofPattern("dd/MM/yyyy");

    // Paleta de colores corporativos
    private static final Color AZUL_INTENSO = new Color(0, 0, 255);
    private static final Color GRIS_FONDO = new Color(245, 247, 250);
    private static final Color GRIS_BORDES = new Color(220, 225, 230);
    private static final Color TEXTO_OSCURO = new Color(40, 44, 52);
    
    // Colores de las Tarjetas KPI
    private static final Color KPI_BG = new Color(239, 246, 255); // Azul muy claro
    private static final Color KPI_BORDER = new Color(191, 219, 254);

    // Colores semánticos para los Tops
    private static final Color GREEN_BG = new Color(209, 231, 221);
    private static final Color GREEN_TEXT = new Color(15, 81, 50);
    private static final Color RED_BG = new Color(248, 215, 218);
    private static final Color RED_TEXT = new Color(132, 32, 41);

    // ==========================================
    // EXPORTACIÓN A EXCEL (DISEÑO CORPORATIVO)
    // ==========================================
    public byte[] generarExcel(ReporteProductosDTO reporte, LocalDateTime inicio, LocalDateTime fin) throws IOException {
        try (Workbook workbook = new XSSFWorkbook(); ByteArrayOutputStream out = new ByteArrayOutputStream()) {
            Sheet sheet = workbook.createSheet("Movimientos de Inventario");
            sheet.setDisplayGridlines(true);

            // === FUENTES Y ESTILOS ===
            org.apache.poi.ss.usermodel.Font titleFont = workbook.createFont();
            titleFont.setFontHeightInPoints((short) 16);
            titleFont.setBold(true);
            titleFont.setColor(IndexedColors.BLUE.getIndex());

            org.apache.poi.ss.usermodel.Font sectionFont = workbook.createFont();
            sectionFont.setFontHeightInPoints((short) 11);
            sectionFont.setBold(true);
            sectionFont.setColor(IndexedColors.BLUE.getIndex());

            org.apache.poi.ss.usermodel.Font headerFont = workbook.createFont();
            headerFont.setFontHeightInPoints((short) 10);
            headerFont.setBold(true);
            headerFont.setColor(IndexedColors.WHITE.getIndex());

            org.apache.poi.ss.usermodel.Font totalFont = workbook.createFont();
            totalFont.setFontHeightInPoints((short) 10);
            totalFont.setBold(true);
            totalFont.setColor(IndexedColors.BLUE.getIndex());

            // Estilos
            CellStyle titleStyle = workbook.createCellStyle();
            titleStyle.setFont(titleFont);

            CellStyle sectionStyle = workbook.createCellStyle();
            sectionStyle.setFont(sectionFont);

            CellStyle kpiStyle = workbook.createCellStyle();
            kpiStyle.setFillForegroundColor(IndexedColors.LIGHT_CORNFLOWER_BLUE.getIndex());
            kpiStyle.setFillPattern(FillPatternType.SOLID_FOREGROUND);
            kpiStyle.setBorderBottom(BorderStyle.THIN);
            kpiStyle.setBorderTop(BorderStyle.THIN);
            kpiStyle.setBorderLeft(BorderStyle.THIN);
            kpiStyle.setBorderRight(BorderStyle.THIN);

            CellStyle headerStyle = workbook.createCellStyle();
            headerStyle.setFillForegroundColor(IndexedColors.BLUE.getIndex());
            headerStyle.setFillPattern(FillPatternType.SOLID_FOREGROUND);
            headerStyle.setFont(headerFont);
            headerStyle.setAlignment(HorizontalAlignment.CENTER);

            CellStyle totalStyle = workbook.createCellStyle();
            totalStyle.setFont(totalFont);
            totalStyle.setFillForegroundColor(IndexedColors.LIGHT_TURQUOISE.getIndex());
            totalStyle.setFillPattern(FillPatternType.SOLID_FOREGROUND);
            totalStyle.setBorderTop(BorderStyle.MEDIUM);
            totalStyle.setBorderBottom(BorderStyle.DOUBLE);

            // === 1. ENCABEZADO ===
            Row r0 = sheet.createRow(0);
            Cell c0 = r0.createCell(0);
            c0.setCellValue(EMPRESA);
            c0.setCellStyle(titleStyle);

            Row r1 = sheet.createRow(1);
            r1.createCell(0).setCellValue("Reporte de Movimientos de Inventario");

            Row r2 = sheet.createRow(2);
            r2.createCell(0).setCellValue("Fecha de generación: " + LocalDateTime.now().format(formatter));

            Row r3 = sheet.createRow(3);
            r3.createCell(0).setCellValue("Periodo del reporte: " + inicio.format(dateFormatter) + " al " + fin.format(dateFormatter));

            // === 2. KPI CARDS ===
            Row r5 = sheet.createRow(5);
            Cell k1 = r5.createCell(0); k1.setCellValue("UNIDADES FÍSICAS DESPACHADAS"); k1.setCellStyle(kpiStyle);
            Cell k2 = r5.createCell(4); k2.setCellValue("VALORIZACIÓN DE ROTACIÓN"); k2.setCellStyle(kpiStyle);

            Row r6 = sheet.createRow(6);
            Cell v1 = r6.createCell(0); v1.setCellValue(reporte.getTotalUnidadesDespachadas() + " unds"); v1.setCellStyle(kpiStyle);
            Cell v2 = r6.createCell(4); v2.setCellValue("S/ " + reporte.getValorizacionTotal()); v2.setCellStyle(kpiStyle);

            // Merges para simular tarjetas
            sheet.addMergedRegion(new CellRangeAddress(5, 5, 0, 2));
            sheet.addMergedRegion(new CellRangeAddress(6, 6, 0, 2));
            sheet.addMergedRegion(new CellRangeAddress(5, 5, 4, 6));
            sheet.addMergedRegion(new CellRangeAddress(6, 6, 4, 6));

            // Rellenar bordes de celdas combinadas de los KPIs
            for (int i = 1; i <= 2; i++) {
                r5.createCell(i).setCellStyle(kpiStyle);
                r6.createCell(i).setCellStyle(kpiStyle);
            }
            for (int i = 5; i <= 6; i++) {
                r5.createCell(i).setCellStyle(kpiStyle);
                r6.createCell(i).setCellStyle(kpiStyle);
            }

            // === 3. SECCIÓN: TABLA HISTÓRICA KARDEX ===
            Row r8 = sheet.createRow(8);
            Cell cSec2 = r8.createCell(0);
            cSec2.setCellValue("| DETALLE HISTÓRICO DE MOVIMIENTOS (KARDEX)");
            cSec2.setCellStyle(sectionStyle);

            // Cabecera Tabla
            Row headerRow = sheet.createRow(9);
            String[] columns = { "ID", "FECHA Y HORA", "VENTA", "CÓDIGO", "PRODUCTO", "CATEGORÍA", "CANT.", "P. UNIT", "SUBTOTAL" };
            for (int i = 0; i < columns.length; i++) {
                Cell cell = headerRow.createCell(i);
                cell.setCellValue(columns[i]);
                cell.setCellStyle(headerStyle);
            }

            // Datos
            int rowIdx = 10;
            int totalCant = 0;
            double totalSubtotal = 0.0;

            for (DetalleVenta dv : reporte.getPaginaMovimientos().getContent()) {
                Row row = sheet.createRow(rowIdx++);
                row.createCell(0).setCellValue(dv.getIdDetalle());
                row.createCell(1).setCellValue(dv.getVenta().getFecha().format(formatter));
                row.createCell(2).setCellValue(dv.getVenta().getIdVenta());
                row.createCell(3).setCellValue(dv.getProducto().getCodigo());
                row.createCell(4).setCellValue(dv.getProducto().getNombre());
                row.createCell(5).setCellValue(dv.getProducto().getCategoria().getNombre());
                row.createCell(6).setCellValue(dv.getCantidad());
                row.createCell(7).setCellValue(dv.getPrecioUnitario().doubleValue());
                row.createCell(8).setCellValue(dv.getSubtotal().doubleValue());

                totalCant += dv.getCantidad();
                totalSubtotal += dv.getSubtotal().doubleValue();
            }

            // Fila de Total Consolidado
            Row totalRow = sheet.createRow(rowIdx);
            Cell labelTotal = totalRow.createCell(0);
            labelTotal.setCellValue("TOTAL CONSOLIDADO:");
            labelTotal.setCellStyle(totalStyle);

            for (int i = 1; i <= 5; i++) {
                totalRow.createCell(i).setCellStyle(totalStyle);
            }
            sheet.addMergedRegion(new CellRangeAddress(rowIdx, rowIdx, 0, 5));

            Cell cellTotCant = totalRow.createCell(6);
            cellTotCant.setCellValue(totalCant);
            cellTotCant.setCellStyle(totalStyle);

            // Celda vacía para P.UNIT
            Cell cellEmptyUnit = totalRow.createCell(7);
            cellEmptyUnit.setCellStyle(totalStyle);

            Cell cellTotDinero = totalRow.createCell(8);
            cellTotDinero.setCellValue(totalSubtotal);
            cellTotDinero.setCellStyle(totalStyle);

            // Autoajustar anchos
            for (int i = 0; i < columns.length; i++) {
                sheet.autoSizeColumn(i);
            }

            workbook.write(out);
            return out.toByteArray();
        }
    }

    // ==========================================
    // EXPORTACIÓN A PDF
    // ==========================================
    public byte[] generarPdf(ReporteProductosDTO reporte, LocalDateTime inicio, LocalDateTime fin) throws IOException {
        try (ByteArrayOutputStream out = new ByteArrayOutputStream()) {
            // Página horizontal (Landscape)
            Document document = new Document(PageSize.A4.rotate(), 36, 36, 36, 36);
            PdfWriter.getInstance(document, out);
            document.open();

            // === FUENTES ===
            Font mainTitleFont = FontFactory.getFont(FontFactory.HELVETICA_BOLD, 22, AZUL_INTENSO);
            Font subTitleFont = FontFactory.getFont(FontFactory.HELVETICA_BOLD, 13, new Color(51, 65, 85));
            Font metaFont = FontFactory.getFont(FontFactory.HELVETICA, 8.5f, new Color(100, 116, 139));
            Font sectionTitleFont = FontFactory.getFont(FontFactory.HELVETICA_BOLD, 11, AZUL_INTENSO);
            
            Font kpiTitleFont = FontFactory.getFont(FontFactory.HELVETICA_BOLD, 8, new Color(100, 116, 139));
            Font kpiValueFont = FontFactory.getFont(FontFactory.HELVETICA_BOLD, 18, TEXTO_OSCURO);
            Font kpiBlueValueFont = FontFactory.getFont(FontFactory.HELVETICA_BOLD, 18, AZUL_INTENSO);

            Font topTitleFont = FontFactory.getFont(FontFactory.HELVETICA_BOLD, 8.5f);
            Font cellFont = FontFactory.getFont(FontFactory.HELVETICA, 8.5f, TEXTO_OSCURO);
            Font headerFont = FontFactory.getFont(FontFactory.HELVETICA_BOLD, 8.5f, Color.WHITE);
            Font totalFont = FontFactory.getFont(FontFactory.HELVETICA_BOLD, 9.5f, AZUL_INTENSO);

            // === ENCABEZADO ===
            Paragraph title = new Paragraph(EMPRESA, mainTitleFont);
            title.setAlignment(Element.ALIGN_LEFT);
            document.add(title);
            
            Paragraph sub = new Paragraph("Reporte de Movimientos de Inventario", subTitleFont);
            sub.setSpacingBefore(2);
            document.add(sub);

            String fechaReporte = LocalDateTime.now().format(formatter);
            Paragraph meta = new Paragraph("Fecha de generación: " + fechaReporte + "  |  Periodo de control: " + inicio.format(dateFormatter) + " al " + fin.format(dateFormatter), metaFont);
            meta.setSpacingBefore(4);
            meta.setSpacingAfter(15);
            document.add(meta);

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

            // PÁGINA 1: DASHBOARD (KPIs + TOPs)
            // --- 1. TARJETAS KPI (Side by Side) ---
            PdfPTable kpiContainer = new PdfPTable(2);
            kpiContainer.setWidthPercentage(100);
            kpiContainer.setWidths(new float[] { 1f, 1f });
            kpiContainer.setSpacingAfter(15);

            // KPI 1: Unidades Físicas
            PdfPCell kpi1Cell = createKPICard("UNIDADES FÍSICAS DESPACHADAS", reporte.getTotalUnidadesDespachadas() + " unds", kpiTitleFont, kpiValueFont);
            kpiContainer.addCell(kpi1Cell);

            // KPI 2: Valorización
            PdfPCell kpi2Cell = createKPICard("VALORIZACIÓN DE ROTACIÓN", "S/ " + reporte.getValorizacionTotal(), kpiTitleFont, kpiBlueValueFont);
            kpiContainer.addCell(kpi2Cell);

            document.add(kpiContainer);

            // --- 2. TOP 10 TABLES (Side by Side) ---
            PdfPTable topsContainer = new PdfPTable(2);
            topsContainer.setWidthPercentage(100);
            topsContainer.setWidths(new float[]{1f, 1f});

            // Top Mayor Rotación (Verde)
            PdfPTable topMasTable = new PdfPTable(2);
            topMasTable.setWidthPercentage(98);
            topMasTable.setWidths(new float[]{3.2f, 1f});

            PdfPCell masHeader = new PdfPCell(new Phrase("TOP 10 - MAYOR ROTACIÓN", topTitleFont));
            masHeader.setBackgroundColor(GREEN_BG);
            masHeader.getPhrase().getFont().setColor(GREEN_TEXT);
            masHeader.setHorizontalAlignment(Element.ALIGN_CENTER);
            masHeader.setColspan(2);
            masHeader.setPadding(6);
            masHeader.setBorderColor(GRIS_BORDES);
            topMasTable.addCell(masHeader);

            for (var item : reporte.getTop10MasVendidos()) {
                topMasTable.addCell(createTopDataCell(item.getKey(), cellFont, Element.ALIGN_LEFT));
                topMasTable.addCell(createTopDataCell(item.getValue() + " unds", FontFactory.getFont(FontFactory.HELVETICA_BOLD, 8.5f, GREEN_TEXT), Element.ALIGN_RIGHT));
            }
            if (reporte.getTop10MasVendidos().isEmpty()) {
                PdfPCell vacio = new PdfPCell(new Phrase("Sin movimientos", cellFont));
                vacio.setColspan(2);
                vacio.setPadding(6);
                vacio.setHorizontalAlignment(Element.ALIGN_CENTER);
                vacio.setBorderColor(GRIS_BORDES);
                topMasTable.addCell(vacio);
            }

            // Top Riesgo Estancamiento (Rojo)
            PdfPTable topMenosTable = new PdfPTable(2);
            topMenosTable.setWidthPercentage(98);
            topMenosTable.setWidths(new float[]{3.2f, 1f});

            PdfPCell menosHeader = new PdfPCell(new Phrase("TOP 10 - RIESGO ESTANCAMIENTO", topTitleFont));
            menosHeader.setBackgroundColor(RED_BG);
            menosHeader.getPhrase().getFont().setColor(RED_TEXT);
            menosHeader.setHorizontalAlignment(Element.ALIGN_CENTER);
            menosHeader.setColspan(2);
            menosHeader.setPadding(6);
            menosHeader.setBorderColor(GRIS_BORDES);
            topMenosTable.addCell(menosHeader);

            for (var item : reporte.getTop10MenosVendidos()) {
                topMenosTable.addCell(createTopDataCell(item.getKey(), cellFont, Element.ALIGN_LEFT));
                topMenosTable.addCell(createTopDataCell(item.getValue() + " unds", FontFactory.getFont(FontFactory.HELVETICA_BOLD, 8.5f, RED_TEXT), Element.ALIGN_RIGHT));
            }
            if (reporte.getTop10MenosVendidos().isEmpty()) {
                PdfPCell vacio = new PdfPCell(new Phrase("Sin movimientos", cellFont));
                vacio.setColspan(2);
                vacio.setPadding(6);
                vacio.setHorizontalAlignment(Element.ALIGN_CENTER);
                vacio.setBorderColor(GRIS_BORDES);
                topMenosTable.addCell(vacio);
            }

            PdfPCell leftContainer = new PdfPCell(topMasTable);
            leftContainer.setBorder(Rectangle.NO_BORDER);
            leftContainer.setHorizontalAlignment(Element.ALIGN_LEFT);

            PdfPCell rightContainer = new PdfPCell(topMenosTable);
            rightContainer.setBorder(Rectangle.NO_BORDER);
            rightContainer.setHorizontalAlignment(Element.ALIGN_RIGHT);

            topsContainer.addCell(leftContainer);
            topsContainer.addCell(rightContainer);

            document.add(topsContainer);

            // PÁGINA 2: DETALLE HISTÓRICO (PAGINACIÓN LIMPIA)

            // Título de Sección con línea de acento izquierda
            Paragraph secTitle = new Paragraph("| RESULTADOS DEL REPORTE", sectionTitleFont);
            secTitle.setSpacingBefore(10);
			secTitle.setSpacingAfter(12);
            document.add(secTitle);

            // Tabla Kardex de 9 Columnas
            PdfPTable table = new PdfPTable(9);
            table.setWidthPercentage(100);
            table.setWidths(new float[]{0.8f, 1.8f, 1.0f, 1.0f, 3.2f, 2.2f, 0.8f, 1.1f, 1.3f});

            // Headers
            String[] headers = { "ID", "FECHA Y HORA", "VENTA", "CÓDIGO", "PRODUCTO", "CATEGORÍA", "CANT.", "P. UNIT", "SUBTOTAL" };
            for (String h : headers) {
                PdfPCell cell = new PdfPCell(new Phrase(h, headerFont));
                cell.setBackgroundColor(AZUL_INTENSO);
                cell.setBorder(Rectangle.NO_BORDER);
                cell.setPadding(6);
                cell.setHorizontalAlignment(Element.ALIGN_CENTER);
                table.addCell(cell);
            }

            // Filas de Datos
            int cont = 0;
            int totalCant = 0;
            double totalSubtotal = 0.0;

            for (DetalleVenta dv : reporte.getPaginaMovimientos().getContent()) {
                Color bgRow = (cont % 2 == 0) ? Color.WHITE : new Color(248, 250, 252);

                table.addCell(createTableCell(dv.getIdDetalle().toString(), cellFont, bgRow, Element.ALIGN_CENTER));
                table.addCell(createTableCell(dv.getVenta().getFecha().format(formatter), cellFont, bgRow, Element.ALIGN_CENTER));
                table.addCell(createTableCell(dv.getVenta().getIdVenta().toString(), cellFont, bgRow, Element.ALIGN_CENTER));
                table.addCell(createTableCell(dv.getProducto().getCodigo(), cellFont, bgRow, Element.ALIGN_CENTER));
                table.addCell(createTableCell(dv.getProducto().getNombre(), cellFont, bgRow, Element.ALIGN_LEFT));
                table.addCell(createTableCell(dv.getProducto().getCategoria().getNombre(), cellFont, bgRow, Element.ALIGN_LEFT));
                table.addCell(createTableCell(String.valueOf(dv.getCantidad()), cellFont, bgRow, Element.ALIGN_CENTER));
                table.addCell(createTableCell(String.format("%.2f", dv.getPrecioUnitario().doubleValue()), cellFont, bgRow, Element.ALIGN_RIGHT));
                table.addCell(createTableCell(String.format("%.2f", dv.getSubtotal().doubleValue()), cellFont, bgRow, Element.ALIGN_RIGHT));

                totalCant += dv.getCantidad();
                totalSubtotal += dv.getSubtotal().doubleValue();
                cont++;
            }

            // Fila de Totales Consolidados
            PdfPCell labelConsolidado = new PdfPCell(new Phrase("TOTAL CONSOLIDADO:", totalFont));
            labelConsolidado.setColspan(6);
            labelConsolidado.setHorizontalAlignment(Element.ALIGN_RIGHT);
            labelConsolidado.setVerticalAlignment(Element.ALIGN_MIDDLE);
            labelConsolidado.setPadding(8);
            labelConsolidado.setBackgroundColor(new Color(239, 246, 255));
            labelConsolidado.setBorder(Rectangle.TOP | Rectangle.BOTTOM);
            labelConsolidado.setBorderColor(AZUL_INTENSO);
            table.addCell(labelConsolidado);

            // Total Cantidad
            PdfPCell totCantCell = new PdfPCell(new Phrase(String.valueOf(totalCant), totalFont));
            totCantCell.setHorizontalAlignment(Element.ALIGN_CENTER);
            totCantCell.setVerticalAlignment(Element.ALIGN_MIDDLE);
            totCantCell.setBackgroundColor(new Color(239, 246, 255));
            totCantCell.setBorder(Rectangle.TOP | Rectangle.BOTTOM);
            totCantCell.setBorderColor(AZUL_INTENSO);
            table.addCell(totCantCell);

            // Vacío para P. Unit
            PdfPCell totEmptyCell = new PdfPCell(new Phrase("", totalFont));
            totEmptyCell.setBackgroundColor(new Color(239, 246, 255));
            totEmptyCell.setBorder(Rectangle.TOP | Rectangle.BOTTOM);
            totEmptyCell.setBorderColor(AZUL_INTENSO);
            table.addCell(totEmptyCell);

            // Total Subtotal Dinero
            PdfPCell totDineroCell = new PdfPCell(new Phrase("S/ " + String.format("%.2f", totalSubtotal), totalFont));
            totDineroCell.setHorizontalAlignment(Element.ALIGN_RIGHT);
            totDineroCell.setVerticalAlignment(Element.ALIGN_MIDDLE);
            totDineroCell.setBackgroundColor(new Color(239, 246, 255));
            totDineroCell.setBorder(Rectangle.TOP | Rectangle.BOTTOM);
            totDineroCell.setBorderColor(AZUL_INTENSO);
            table.addCell(totDineroCell);

            document.add(table);
            document.close();
            return out.toByteArray();
        }
    }

    // === METODOS AUXILIARES DE ESTILIZACIÓN ===
    private PdfPCell createKPICard(String title, String value, Font titleFont, Font valFont) {
        // Celda Maestra que contiene una subtabla para pintar la barra de acento izquierda
        PdfPCell masterCell = new PdfPCell();
        masterCell.setBorder(Rectangle.NO_BORDER);
        masterCell.setPaddingLeft(10);
        masterCell.setPaddingRight(10);

        PdfPTable cardLayout = new PdfPTable(2);
        cardLayout.setWidthPercentage(100);
        try {
            cardLayout.setWidths(new float[]{0.03f, 0.97f});
        } catch (DocumentException ignored) {}

        // 1. Barra de Acento Azul Eléctrico
        PdfPCell accentBar = new PdfPCell();
        accentBar.setBackgroundColor(AZUL_INTENSO);
        accentBar.setBorder(Rectangle.NO_BORDER);
        cardLayout.addCell(accentBar);

        // 2. Contenido de la tarjeta (Fondo celeste con bordes redondeados/limpios)
        PdfPCell contentCell = new PdfPCell();
        contentCell.setBackgroundColor(KPI_BG);
        contentCell.setBorderColor(KPI_BORDER);
        contentCell.setBorderWidth(1f);
        contentCell.setPadding(10);

        Paragraph pTitle = new Paragraph(title, titleFont);
        pTitle.setSpacingAfter(4);
        contentCell.addElement(pTitle);

        Paragraph pVal = new Paragraph(value, valFont);
        contentCell.addElement(pVal);

        cardLayout.addCell(contentCell);
        masterCell.addElement(cardLayout);
        return masterCell;
    }

    private PdfPCell createTopDataCell(String text, Font font, int alignment) {
        PdfPCell cell = new PdfPCell(new Phrase(text, font));
        cell.setPadding(6);
        cell.setBorderColor(GRIS_BORDES);
        cell.setHorizontalAlignment(alignment);
        cell.setVerticalAlignment(Element.ALIGN_MIDDLE);
        return cell;
    }

    private PdfPCell createTableCell(String text, Font font, Color bg, int alignment) {
        PdfPCell cell = new PdfPCell(new Phrase(text, font));
        cell.setBackgroundColor(bg);
        cell.setPadding(6);
        cell.setHorizontalAlignment(alignment);
        cell.setVerticalAlignment(Element.ALIGN_MIDDLE);
        cell.setBorder(Rectangle.BOTTOM);
        cell.setBorderColor(new Color(241, 245, 249));
        return cell;
    }
}