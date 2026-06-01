package com.sistema.botica.service;

import java.awt.Color;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.time.format.DateTimeFormatter;

import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellStyle;
import org.apache.poi.ss.usermodel.FillPatternType;
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

@Service
public class ReporteExportService {
	private final DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm");

	// ==========================================
	// EXPORTACIÓN A EXCEL
	// ==========================================
	public byte[] generarExcel(ReporteVentasDTO reporte) throws IOException {
		try (Workbook workbook = new XSSFWorkbook(); ByteArrayOutputStream out = new ByteArrayOutputStream()) {
			Sheet sheet = workbook.createSheet("Ventas");

			// Estilo para la cabecera
			CellStyle headerStyle = workbook.createCellStyle();
			headerStyle.setFillForegroundColor(IndexedColors.PALE_BLUE.getIndex());
			headerStyle.setFillPattern(FillPatternType.SOLID_FOREGROUND);
			org.apache.poi.ss.usermodel.Font font = workbook.createFont();
			font.setBold(true);
			headerStyle.setFont(font);

			// Crear fila de cabecera
			Row headerRow = sheet.createRow(0);
			String[] columns = { "N° Venta", "Fecha", "Cliente", "Cajero", "N° Items", "Total (S/.)" };
			for (int i = 0; i < columns.length; i++) {
				Cell cell = headerRow.createCell(i);
				cell.setCellValue(columns[i]);
				cell.setCellStyle(headerStyle);
			}

			// Llenar datos de ventas
			int rowIdx = 1;
			for (var v : reporte.getListaVentas()) {
				Row row = sheet.createRow(rowIdx++);
				row.createCell(0).setCellValue(v.getIdVenta());
				row.createCell(1).setCellValue(v.getFecha().format(formatter));
				row.createCell(2).setCellValue(v.getCliente().getNombre() + " " + v.getCliente().getApellido());
				row.createCell(3).setCellValue(v.getUsuario().getNombre());
				row.createCell(4).setCellValue(v.getListaDetallesVenta().size());
				row.createCell(5).setCellValue(v.getTotal().doubleValue());
			}

			// Autoajustar el ancho de las columnas
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
	public byte[] generarPdf(ReporteVentasDTO reporte) throws IOException {
        try (ByteArrayOutputStream out = new ByteArrayOutputStream()) {
            Document document = new Document(PageSize.A4);
            PdfWriter.getInstance(document, out);
            document.open();

            // Título principal
            Font titleFont = FontFactory.getFont(FontFactory.HELVETICA_BOLD, 18, Color.BLUE);
            Paragraph title = new Paragraph("Reporte de Ventas e Inventario", titleFont);
            title.setAlignment(Element.ALIGN_CENTER);
            title.setSpacingAfter(20);
            document.add(title);

            // Resumen de KPIs
            Font kpiFont = FontFactory.getFont(FontFactory.HELVETICA, 11);
            document.add(new Paragraph("Resumen del Periodo:", FontFactory.getFont(FontFactory.HELVETICA_BOLD, 12)));
            document.add(new Paragraph("- Total Recaudado: S/ " + reporte.getTotalVentas(), kpiFont));
            document.add(new Paragraph("- Productos Vendidos: " + reporte.getProductosVendidos() + " unidades", kpiFont));
            document.add(new Paragraph("- Producto Más Vendido: " + reporte.getProductoMasVendido(), kpiFont));
            document.add(new Paragraph("- Mes con Más Ventas: " + reporte.getMesMasVentas(), kpiFont));
            document.add(Chunk.NEWLINE);

            // Crear tabla (6 columnas) con anchos relativos
            PdfPTable table = new PdfPTable(6);
            table.setWidthPercentage(100);
            table.setWidths(new float[]{1f, 2f, 3.5f, 2.5f, 1f, 1.5f});

            // Cabeceras de la tabla
            String[] headers = {"N°", "Fecha", "Cliente", "Cajero", "Items", "Total (S/.)"};
            Font headerFont = FontFactory.getFont(FontFactory.HELVETICA_BOLD, 10, Color.WHITE);
            for (String h : headers) {
                PdfPCell cell = new PdfPCell(new Phrase(h, headerFont));
                cell.setBackgroundColor(new Color(13, 110, 253)); // Azul tipo Bootstrap
                cell.setPadding(5);
                cell.setHorizontalAlignment(Element.ALIGN_CENTER);
                table.addCell(cell);
            }

            // Filas de datos
            Font cellFont = FontFactory.getFont(FontFactory.HELVETICA, 9);
            for (var v : reporte.getListaVentas()) {
                table.addCell(new PdfPCell(new Phrase(String.valueOf(v.getIdVenta()), cellFont)));
                table.addCell(new PdfPCell(new Phrase(v.getFecha().format(formatter), cellFont)));
                table.addCell(new PdfPCell(new Phrase(v.getCliente().getNombre() + " " + v.getCliente().getApellido(), cellFont)));
                table.addCell(new PdfPCell(new Phrase(v.getUsuario().getNombre(), cellFont)));
                
                PdfPCell itemCell = new PdfPCell(new Phrase(String.valueOf(v.getListaDetallesVenta().size()), cellFont));
                itemCell.setHorizontalAlignment(Element.ALIGN_CENTER);
                table.addCell(itemCell);
                
                PdfPCell totalCell = new PdfPCell(new Phrase(v.getTotal().toString(), cellFont));
                totalCell.setHorizontalAlignment(Element.ALIGN_RIGHT);
                table.addCell(totalCell);
            }

            document.add(table);
            document.close();
            return out.toByteArray();
        }
    }
}
