package com.sistema.botica.service;

import java.awt.Color;
import java.io.ByteArrayOutputStream;
import java.io.IOException;

import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellStyle;
import org.apache.poi.ss.usermodel.FillPatternType;
import org.apache.poi.ss.usermodel.IndexedColors;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.openpdf.text.Element;
import org.openpdf.text.Font;
import org.openpdf.text.FontFactory;
import org.openpdf.text.Document;
import org.openpdf.text.PageSize;
import org.openpdf.text.Paragraph;
import org.openpdf.text.pdf.PdfPCell;
import org.openpdf.text.pdf.PdfPTable;
import org.openpdf.text.pdf.PdfWriter;

import com.sistema.botica.DTO.ReporteProductosEliminadosDTO;
import com.sistema.botica.entity.Producto;

import org.springframework.stereotype.Service;

@Service
public class ProductosEliminadosReporteExportService {
    // ==========================================
    // EXPORTACIÓN A EXCEL
    // ==========================================
    public byte[] generarExcel(ReporteProductosEliminadosDTO reporte) throws IOException {
        try (Workbook workbook = new XSSFWorkbook(); ByteArrayOutputStream out = new ByteArrayOutputStream()) {

            Sheet sheet = workbook.createSheet("Productos Eliminados");

            // Estilos
            CellStyle headerStyle = workbook.createCellStyle();
            headerStyle.setFillForegroundColor(IndexedColors.ROYAL_BLUE.getIndex());
            headerStyle.setFillPattern(FillPatternType.SOLID_FOREGROUND);
            org.apache.poi.ss.usermodel.Font font = workbook.createFont();
            font.setColor(IndexedColors.WHITE.getIndex());
            font.setBold(true);
            headerStyle.setFont(font);

            // Cabecera
            Row headerRow = sheet.createRow(0);
            String[] columns = { "ID", "Código", "Nombre", "Categoría", "P. Compra (S/.)", "P. Venta (S/.)",
                    "Stock Actual", "Fecha Vencimiento", "Proveedor" };
            for (int i = 0; i < columns.length; i++) {
                Cell cell = headerRow.createCell(i);
                cell.setCellValue(columns[i]);
                cell.setCellStyle(headerStyle);
            }

            // Datos
            int rowIdx = 1;
            for (Producto p : reporte.getProductosEliminados().getContent()) {
                Row row = sheet.createRow(rowIdx++);
                row.createCell(0).setCellValue(p.getIdProducto());
                row.createCell(1).setCellValue(p.getCodigo());
                row.createCell(2).setCellValue(p.getNombre());
                row.createCell(3).setCellValue(p.getCategoria().getNombre());
                row.createCell(4).setCellValue(p.getPrecioCompra().doubleValue());
                row.createCell(5).setCellValue(p.getPrecioVenta().doubleValue());
                row.createCell(6).setCellValue(p.getStockActual());
                row.createCell(7).setCellValue(p.getFechaVencimiento().toString());
                row.createCell(8).setCellValue(p.getProveedor().getNombre());
            }

            // Autoajustar columnas
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
    public byte[] generarPdf(ReporteProductosEliminadosDTO reporte) throws IOException {
        try (ByteArrayOutputStream out = new ByteArrayOutputStream()) {

            // Página A4 en formato Horizontal (Landscape)
            Document document = new Document(PageSize.A4.rotate());
            PdfWriter.getInstance(document, out);
            document.open();

            // Título
            Font titleFont = FontFactory.getFont(FontFactory.HELVETICA_BOLD, 18, Color.BLUE);
            Paragraph title = new Paragraph("Reporte de Productos Eliminados", titleFont);
            title.setAlignment(Element.ALIGN_CENTER);
            title.setSpacingAfter(5);
            document.add(title);

            // Estadísticas
            Font kpiFont = FontFactory.getFont(FontFactory.HELVETICA, 11);
            document.add(new Paragraph("Total de Productos Eliminados: " + reporte.getTotalProductosEliminados(),
                    FontFactory.getFont(FontFactory.HELVETICA_BOLD, 12)));
            document.add(new Paragraph("", kpiFont));

            // Tabla con 9 columnas
            PdfPTable table = new PdfPTable(9);
            table.setWidthPercentage(100);
            table.setSpacingBefore(15f);
            // Proporciones de ancho para cada columna
            table.setWidths(new float[] { 0.8f, 1.2f, 2.2f, 1.5f, 1.2f, 1.2f, 1f, 1.5f, 1.5f });

            Font cellFont = FontFactory.getFont(FontFactory.HELVETICA, 9);

            // Cabeceras
            String[] headers = { "ID", "Código", "Nombre", "Categoría", "P. Compra", "P. Venta", "Stock",
                    "Vencimiento", "Proveedor" };
            Font headerFont = FontFactory.getFont(FontFactory.HELVETICA_BOLD, 10, Color.WHITE);
            for (String h : headers) {
                PdfPCell cell = new PdfPCell(new org.openpdf.text.Phrase(h, headerFont));
                cell.setBackgroundColor(new Color(13, 110, 253));
                cell.setPadding(6);
                cell.setHorizontalAlignment(Element.ALIGN_CENTER);
                table.addCell(cell);
            }

            // Filas
            if (reporte.getProductosEliminados() != null && reporte.getProductosEliminados().getContent() != null) {
                for (Producto p : reporte.getProductosEliminados().getContent()) {
                    table.addCell(new PdfPCell(new org.openpdf.text.Phrase(p.getIdProducto().toString(), cellFont)));
                    table.addCell(new PdfPCell(new org.openpdf.text.Phrase(p.getCodigo(), cellFont)));
                    table.addCell(new PdfPCell(new org.openpdf.text.Phrase(p.getNombre(), cellFont)));
                    table.addCell(new PdfPCell(new org.openpdf.text.Phrase(p.getCategoria().getNombre(), cellFont)));

                    PdfPCell precioCompraCell = new PdfPCell(
                            new org.openpdf.text.Phrase("S/ " + p.getPrecioCompra().toString(), cellFont));
                    precioCompraCell.setHorizontalAlignment(Element.ALIGN_RIGHT);
                    table.addCell(precioCompraCell);

                    PdfPCell precioVentaCell = new PdfPCell(
                            new org.openpdf.text.Phrase("S/ " + p.getPrecioVenta().toString(), cellFont));
                    precioVentaCell.setHorizontalAlignment(Element.ALIGN_RIGHT);
                    table.addCell(precioVentaCell);

                    PdfPCell stockCell = new PdfPCell(
                            new org.openpdf.text.Phrase(p.getStockActual().toString(), cellFont));
                    stockCell.setHorizontalAlignment(Element.ALIGN_CENTER);
                    table.addCell(stockCell);

                    table.addCell(new PdfPCell(
                            new org.openpdf.text.Phrase(p.getFechaVencimiento().toString(), cellFont)));
                    table.addCell(new PdfPCell(
                            new org.openpdf.text.Phrase(p.getProveedor().getNombre(), cellFont)));
                }
            } else {
                PdfPCell emptyCell = new PdfPCell(new org.openpdf.text.Phrase("Sin datos disponibles", cellFont));
                emptyCell.setColspan(9);
                emptyCell.setHorizontalAlignment(Element.ALIGN_CENTER);
                table.addCell(emptyCell);
            }

            document.add(table);
            document.close();
            return out.toByteArray();
        }
    }
}
