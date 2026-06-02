package com.sistema.botica.service;

import java.awt.Color;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.time.LocalDateTime;
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

import com.sistema.botica.DTO.ReporteProductosDTO;
import com.sistema.botica.entity.DetalleVenta;

@Service
public class ProductoReporteExportService {
	private final DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm");
	private final DateTimeFormatter dateFormatter = DateTimeFormatter.ofPattern("dd/MM/yyyy");

	// ==========================================
	// EXPORTACIÓN A EXCEL
	// ==========================================
	public byte[] generarExcel(ReporteProductosDTO reporte, LocalDateTime inicio, LocalDateTime fin)
			throws IOException {
		try (Workbook workbook = new XSSFWorkbook(); ByteArrayOutputStream out = new ByteArrayOutputStream()) {

			Sheet sheet = workbook.createSheet("Movimientos de Inventario");

			// Estilos
			CellStyle headerStyle = workbook.createCellStyle();
			headerStyle.setFillForegroundColor(IndexedColors.ROYAL_BLUE.getIndex());
			headerStyle.setFillPattern(FillPatternType.SOLID_FOREGROUND);
			org.apache.poi.ss.usermodel.Font font = workbook.createFont();
			font.setColor(IndexedColors.WHITE.getIndex());
			font.setBold(true);
			headerStyle.setFont(font);

			CellStyle dateStyle = workbook.createCellStyle();
			dateStyle.setDataFormat(workbook.createDataFormat().getFormat("dd/MM/yyyy HH:mm"));

			// Cabecera
			Row headerRow = sheet.createRow(0);
			String[] columns = { "ID Detalle", "Fecha y Hora", "N° Venta", "Código", "Producto", "Categoría",
					"Cantidad", "P. Unit (S/.)", "Subtotal (S/.)" };
			for (int i = 0; i < columns.length; i++) {
				Cell cell = headerRow.createCell(i);
				cell.setCellValue(columns[i]);
				cell.setCellStyle(headerStyle);
			}

			// Datos
			int rowIdx = 1;
			for (DetalleVenta dv : reporte.getPaginaMovimientos().getContent()) {
				Row row = sheet.createRow(rowIdx++);
				row.createCell(0).setCellValue("#" + dv.getIdDetalle());

				Cell dateCell = row.createCell(1);
				dateCell.setCellValue(dv.getVenta().getFecha());
				dateCell.setCellStyle(dateStyle);

				row.createCell(2).setCellValue("V-" + dv.getVenta().getIdVenta());
				row.createCell(3).setCellValue(dv.getProducto().getCodigo());
				row.createCell(4).setCellValue(dv.getProducto().getNombre());
				row.createCell(5).setCellValue(dv.getProducto().getCategoria().getNombre());
				row.createCell(6).setCellValue(dv.getCantidad());
				row.createCell(7).setCellValue(dv.getPrecioUnitario().doubleValue());
				row.createCell(8).setCellValue(dv.getSubtotal().doubleValue());
			}

			// Autoajustar columnas
			for (int i = 0; i < columns.length; i++) {
				sheet.autoSizeColumn(i);
			}

			workbook.write(out);
			return out.toByteArray();
		}
	}

	public byte[] generarPdf(ReporteProductosDTO reporte, LocalDateTime inicio, LocalDateTime fin) throws IOException {
		try (ByteArrayOutputStream out = new ByteArrayOutputStream()) {

			// Página A4 en formato Horizontal (Landscape)
			Document document = new Document(PageSize.A4.rotate());
			PdfWriter.getInstance(document, out);
			document.open();

			// Título
			Font titleFont = FontFactory.getFont(FontFactory.HELVETICA_BOLD, 18, Color.BLUE);
			Paragraph title = new Paragraph("Reporte de Movimientos de Inventario", titleFont);
			title.setAlignment(Element.ALIGN_CENTER);
			title.setSpacingAfter(5);
			document.add(title);

			// Subtítulo con Fechas
			Font periodFont = FontFactory.getFont(FontFactory.HELVETICA_OBLIQUE, 11, Color.DARK_GRAY);
			String textoPeriodo = "Periodo: " + inicio.format(dateFormatter) + " al " + fin.format(dateFormatter);
			Paragraph periodo = new Paragraph(textoPeriodo, periodFont);
			periodo.setAlignment(Element.ALIGN_CENTER);
			periodo.setSpacingAfter(20);
			document.add(periodo);

			// Resumen de Indicadores
			Font kpiFont = FontFactory.getFont(FontFactory.HELVETICA, 11);
			document.add(new Paragraph("Resumen Dinámico del Periodo:",
					FontFactory.getFont(FontFactory.HELVETICA_BOLD, 12)));
			document.add(new Paragraph(
					"- Unidades Físicas Despachadas: " + reporte.getTotalUnidadesDespachadas() + " unds", kpiFont));
			document.add(new Paragraph("- Valorización de Rotación: S/ " + reporte.getValorizacionTotal(), kpiFont));
			document.add(Chunk.NEWLINE);

			// Tabla de 9 columnas
			PdfPTable table = new PdfPTable(9);
			table.setWidthPercentage(100);
			// Proporciones de ancho para cada columna
			table.setWidths(new float[] { 1f, 1.8f, 1.2f, 1.2f, 3f, 2f, 1f, 1.2f, 1.5f });

			// Cabeceras
			String[] headers = { "ID", "Fecha y Hora", "Venta", "Código", "Producto", "Categoría", "Cant.", "P. Unit",
					"Subtotal" };
			Font headerFont = FontFactory.getFont(FontFactory.HELVETICA_BOLD, 10, Color.WHITE);
			for (String h : headers) {
				PdfPCell cell = new PdfPCell(new Phrase(h, headerFont));
				cell.setBackgroundColor(new Color(13, 110, 253)); // Azul
				cell.setPadding(6);
				cell.setHorizontalAlignment(Element.ALIGN_CENTER);
				table.addCell(cell);
			}

			// Filas
			Font cellFont = FontFactory.getFont(FontFactory.HELVETICA, 9);
			for (DetalleVenta dv : reporte.getPaginaMovimientos().getContent()) {
				table.addCell(new PdfPCell(new Phrase("#" + dv.getIdDetalle(), cellFont)));
				table.addCell(new PdfPCell(new Phrase(dv.getVenta().getFecha().format(formatter), cellFont)));
				table.addCell(new PdfPCell(new Phrase("V-" + dv.getVenta().getIdVenta(), cellFont)));
				table.addCell(new PdfPCell(new Phrase(dv.getProducto().getCodigo(), cellFont)));
				table.addCell(new PdfPCell(new Phrase(dv.getProducto().getNombre(), cellFont)));
				table.addCell(new PdfPCell(new Phrase(dv.getProducto().getCategoria().getNombre(), cellFont)));

				PdfPCell cantCell = new PdfPCell(new Phrase(String.valueOf(dv.getCantidad()), cellFont));
				cantCell.setHorizontalAlignment(Element.ALIGN_CENTER);
				table.addCell(cantCell);

				PdfPCell precioCell = new PdfPCell(new Phrase(dv.getPrecioUnitario().toString(), cellFont));
				precioCell.setHorizontalAlignment(Element.ALIGN_RIGHT);
				table.addCell(precioCell);

				PdfPCell subtotalCell = new PdfPCell(new Phrase(dv.getSubtotal().toString(), cellFont));
				subtotalCell.setHorizontalAlignment(Element.ALIGN_RIGHT);
				table.addCell(subtotalCell);
			}

			document.add(table);
			document.close();
			return out.toByteArray();
		}
	}
}
