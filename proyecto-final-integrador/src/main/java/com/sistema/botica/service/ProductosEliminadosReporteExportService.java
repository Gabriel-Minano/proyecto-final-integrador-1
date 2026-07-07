package com.sistema.botica.service;

import java.awt.Color;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import org.apache.poi.ss.usermodel.BorderStyle;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellStyle;
import org.apache.poi.ss.usermodel.FillPatternType;
import org.apache.poi.ss.usermodel.Font;
import org.apache.poi.ss.usermodel.HorizontalAlignment;
import org.apache.poi.ss.usermodel.IndexedColors;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.openpdf.text.Document;
import org.openpdf.text.Element;
import org.openpdf.text.FontFactory;
import org.openpdf.text.PageSize;
import org.openpdf.text.Paragraph;
import org.openpdf.text.Phrase;
import org.openpdf.text.Rectangle;
import org.openpdf.text.pdf.PdfPCell;
import org.openpdf.text.pdf.PdfPTable;
import org.openpdf.text.pdf.PdfWriter;
import org.springframework.stereotype.Service;
import com.sistema.botica.DTO.ReporteProductosEliminadosDTO;
import com.sistema.botica.entity.Producto;

@Service
public class ProductosEliminadosReporteExportService {
    private static final String EMPRESA = "BOTICA CONQUISTADORES FARMA";
    private final DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm");

    public byte[] generarExcel(ReporteProductosEliminadosDTO reporte) throws IOException {
        try (Workbook workbook = new XSSFWorkbook(); ByteArrayOutputStream out = new ByteArrayOutputStream()) {
            Sheet sheet = workbook.createSheet("Productos Eliminados");
            sheet.setDisplayGridlines(false); 

            // Fuentes y Estilos
            Font titleFont = workbook.createFont();
            titleFont.setBold(true);
            titleFont.setFontHeightInPoints((short) 16);
            titleFont.setColor(IndexedColors.BLUE.getIndex());

            Font headerFont = workbook.createFont();
            headerFont.setBold(true);
            headerFont.setColor(IndexedColors.WHITE.getIndex());

            CellStyle titleStyle = workbook.createCellStyle();
            titleStyle.setFont(titleFont);

            CellStyle headerStyle = workbook.createCellStyle();
            headerStyle.setFillForegroundColor(IndexedColors.BLUE.getIndex());
            headerStyle.setFillPattern(FillPatternType.SOLID_FOREGROUND);
            headerStyle.setAlignment(HorizontalAlignment.CENTER);
            headerStyle.setFont(headerFont);
            headerStyle.setBorderBottom(BorderStyle.THIN);
            headerStyle.setWrapText(true); // Permite ajuste de texto en encabezados

            // Encabezado
            Row r0 = sheet.createRow(0);
            r0.createCell(0).setCellValue(EMPRESA);
            r0.getCell(0).setCellStyle(titleStyle);

            Row r1 = sheet.createRow(1);
            r1.createCell(0).setCellValue("Reporte de Productos Eliminados");

            Row r2 = sheet.createRow(2);
            r2.createCell(0).setCellValue("Fecha: " + LocalDateTime.now().format(formatter));

            // Resumen
            Row r4 = sheet.createRow(4);
            r4.createCell(0).setCellValue("TOTAL PRODUCTOS ELIMINADOS: " + reporte.getTotalProductosEliminados());

            // Tabla de Resultados
            String[] columnas = {"N°", "CÓDIGO", "NOMBRE", "CATEGORÍA", "P.COMPRA", "P.VENTA", "STOCK", "VENCIMIENTO", "PROVEEDOR"};
            Row headerRow = sheet.createRow(6);
            for (int i = 0; i < columnas.length; i++) {
                Cell cell = headerRow.createCell(i);
                cell.setCellValue(columnas[i]);
                cell.setCellStyle(headerStyle);
            }

            int fila = 7;
            for (Producto p : reporte.getProductosEliminados().getContent()) {
                Row row = sheet.createRow(fila++);
                row.createCell(0).setCellValue(p.getIdProducto());
                row.createCell(1).setCellValue(p.getCodigo());
                row.createCell(2).setCellValue(p.getNombre());
                row.createCell(3).setCellValue(p.getCategoria() != null ? p.getCategoria().getNombre() : "");
                row.createCell(4).setCellValue(p.getPrecioCompra() != null ? p.getPrecioCompra().doubleValue() : 0.0);
                row.createCell(5).setCellValue(p.getPrecioVenta() != null ? p.getPrecioVenta().doubleValue() : 0.0);
                row.createCell(6).setCellValue(p.getStockActual());
                row.createCell(7).setCellValue(p.getFechaVencimiento() != null ? p.getFechaVencimiento().toString() : "");
                row.createCell(8).setCellValue(p.getProveedor() != null ? p.getProveedor().getNombre() : "");
            }

            // --- CORRECCIÓN DE ANCHO DE COLUMNAS ---
            
            // 1. Columna "N°" (índice 0) con ancho fijo pequeño
            sheet.setColumnWidth(0, 2000); 

            // 2. Resto de columnas con autoSize + un "buffer" (margen) de 800 unidades
            for (int i = 1; i < columnas.length; i++) {
                sheet.autoSizeColumn(i);
                int anchoActual = sheet.getColumnWidth(i);
                sheet.setColumnWidth(i, anchoActual + 800); 
            }

            workbook.write(out);
            return out.toByteArray();
        }
    }
    
    // ==========================================
    // EXPORTACIÓN A PDF
    // ==========================================
    public byte[] generarPdf(ReporteProductosEliminadosDTO reporte) throws IOException {
        try (ByteArrayOutputStream out = new ByteArrayOutputStream()) {
            Document document = new Document(PageSize.A4.rotate(), 36, 36, 36, 36);
            PdfWriter.getInstance(document, out);
            document.open();

            // Fuentes
            org.openpdf.text.Font mainTitleFont = FontFactory.getFont(FontFactory.HELVETICA_BOLD, 22, new Color(0, 0, 255));
            org.openpdf.text.Font subTitleFont = FontFactory.getFont(FontFactory.HELVETICA_BOLD, 13, new Color(51, 65, 85));
            org.openpdf.text.Font metaFont = FontFactory.getFont(FontFactory.HELVETICA, 9, new Color(100, 116, 139));
            org.openpdf.text.Font sectionFont = FontFactory.getFont(FontFactory.HELVETICA_BOLD, 12, new Color(0, 0, 255));
            org.openpdf.text.Font headerFont = FontFactory.getFont(FontFactory.HELVETICA_BOLD, 9, Color.WHITE);
            org.openpdf.text.Font cellFont = FontFactory.getFont(FontFactory.HELVETICA, 9, new Color(51, 65, 85));
            org.openpdf.text.Font textoOscurofont = FontFactory.getFont(FontFactory.HELVETICA, 12, new Color(40, 44, 52));

            // Encabezado - CAMBIADO A ALIGN_LEFT
            Paragraph empresa = new Paragraph(EMPRESA, mainTitleFont);
            empresa.setAlignment(Element.ALIGN_LEFT); 
            document.add(empresa);

            Paragraph titulo = new Paragraph("Reporte de Productos Eliminados", subTitleFont);
            titulo.setSpacingBefore(4);
            document.add(titulo);

            String fecha = LocalDateTime.now().format(formatter);
            Paragraph fechaGen = new Paragraph("Fecha de generación: " + fecha, metaFont);
            fechaGen.setAlignment(Element.ALIGN_LEFT);
            fechaGen.setSpacingBefore(5);
            fechaGen.setSpacingAfter(15);
            document.add(fechaGen);

            // Línea divisoria sutil bajo el encabezado
            PdfPTable line = new PdfPTable(1);
            line.setWidthPercentage(100);
            PdfPCell lineCell = new PdfPCell();
            lineCell.setBorder(Rectangle.BOTTOM);
            lineCell.setBorderColor(new Color(226, 232, 240));
            line.addCell(lineCell);
            line.setSpacingAfter(15);
            document.add(line);

            Paragraph totalProd = new Paragraph("Total productos eliminados: " + reporte.getTotalProductosEliminados(), textoOscurofont);
            totalProd.setAlignment(Element.ALIGN_LEFT); // CAMBIADO A ALIGN_LEFT
            totalProd.setSpacingAfter(15);
            document.add(totalProd);

            // Tabla
            Paragraph detalle = new Paragraph("| RESULTADOS DEL REPORTE", sectionFont);
            detalle.setSpacingAfter(10);
            document.add(detalle);

            PdfPTable table = new PdfPTable(9);
            table.setWidthPercentage(100);
            // IMPORTANTE: Esta línea fuerza a que la tabla completa se alinee a la izquierda
            table.setHorizontalAlignment(Element.ALIGN_LEFT); 
            table.setWidths(new float[]{0.8f, 1.6f, 3.2f, 2.0f, 1.4f, 1.4f, 1.0f, 1.8f, 2.4f});

            String[] headers = {"N°", "CÓDIGO", "NOMBRE", "CATEGORÍA", "P.COMPRA", "P.VENTA", "STOCK", "VENCIMIENTO", "PROVEEDOR"};
            for (String h : headers) {
                PdfPCell cell = new PdfPCell(new Phrase(h, headerFont));
                cell.setBackgroundColor(new Color(0, 0, 255));
                cell.setPaddingTop(8);
                cell.setPaddingBottom(8);
                cell.setHorizontalAlignment(Element.ALIGN_CENTER); // Los encabezados pueden quedar centrados si prefieres
                cell.setBorder(Rectangle.NO_BORDER);
                table.addCell(cell);
            }

            int fila = 0;
            for (Producto p : reporte.getProductosEliminados().getContent()) {
                Color bg = (fila % 2 == 0) ? Color.WHITE : new Color(248, 250, 252);
                table.addCell(createTableCell(String.valueOf(p.getIdProducto()), cellFont, bg, Element.ALIGN_CENTER));
                table.addCell(createTableCell(p.getCodigo(), cellFont, bg, Element.ALIGN_CENTER));
                table.addCell(createTableCell(p.getNombre(), cellFont, bg, Element.ALIGN_LEFT));
                table.addCell(createTableCell(p.getCategoria().getNombre(), cellFont, bg, Element.ALIGN_LEFT));
                table.addCell(createTableCell(p.getPrecioCompra().toString(), cellFont, bg, Element.ALIGN_RIGHT));
                table.addCell(createTableCell(p.getPrecioVenta().toString(), cellFont, bg, Element.ALIGN_RIGHT));
                table.addCell(createTableCell(String.valueOf(p.getStockActual()), cellFont, bg, Element.ALIGN_CENTER));
                table.addCell(createTableCell(p.getFechaVencimiento().toString(), cellFont, bg, Element.ALIGN_CENTER));
                table.addCell(createTableCell(p.getProveedor().getNombre(), cellFont, bg, Element.ALIGN_LEFT));
                fila++;
            }
            document.add(table);
            
            document.close();
            return out.toByteArray();
        }
    }

    // MÉTODO AUXILIAR CORREGIDO
    private PdfPCell createTableCell(String text, org.openpdf.text.Font font, Color bg, int align) {
        // CORRECCIÓN: Se pasa el parámetro 'font' a la clase Phrase
        PdfPCell cell = new PdfPCell(new Phrase(text, font));
        cell.setBackgroundColor(bg);
        cell.setPaddingTop(8);
        cell.setPaddingBottom(8);
        cell.setHorizontalAlignment(align);
        cell.setVerticalAlignment(Element.ALIGN_MIDDLE);
        cell.setBorder(Rectangle.BOTTOM);
        cell.setBorderColor(new Color(241, 245, 249));
        return cell;
    }
}