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
import com.sistema.botica.entity.Venta;

@Service
public class VentaReporteExportService {
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

			// Crear fila de cabecera (Agregamos "Unidades Totales")
			Row headerRow = sheet.createRow(0);
			String[] columns = { "N° Venta", "Fecha", "Cliente", "Cajero", "N° Items", "Unidades Totales",
					"Total (S/.)" };
			for (int i = 0; i < columns.length; i++) {
				Cell cell = headerRow.createCell(i);
				cell.setCellValue(columns[i]);
				cell.setCellStyle(headerStyle);
			}

			// Llenar datos de ventas
			int rowIdx = 1;
			for (Venta v : reporte.getPaginaVentas().getContent()) {
				Row row = sheet.createRow(rowIdx++);
				row.createCell(0).setCellValue(v.getIdVenta());
				row.createCell(1).setCellValue(v.getFecha().format(formatter));
				row.createCell(2).setCellValue(v.getCliente().getNombre() + " " + v.getCliente().getApellido());
				row.createCell(3).setCellValue(v.getUsuario().getNombre());
				row.createCell(4).setCellValue(v.getListaDetallesVenta().size());

				// NUEVO: Sumar la cantidad física de todos los productos en este ticket
				int unidadesFisicas = v.getListaDetallesVenta().stream().mapToInt(dv -> dv.getCantidad()).sum();
				row.createCell(5).setCellValue(unidadesFisicas);

				row.createCell(6).setCellValue(v.getTotal().doubleValue());
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
	public byte[] generarPdf(ReporteVentasDTO reporte, java.time.LocalDateTime inicio, java.time.LocalDateTime fin)
			throws IOException {
		try (ByteArrayOutputStream out = new ByteArrayOutputStream()) {
			Document document = new Document(PageSize.A4);
			PdfWriter.getInstance(document, out);
			document.open();

			// Título principal
			Font titleFont = FontFactory.getFont(FontFactory.HELVETICA_BOLD, 18, Color.BLUE);
			Paragraph title = new Paragraph("Reporte de Ventas", titleFont);
			title.setAlignment(Element.ALIGN_CENTER);
			title.setSpacingAfter(5); // reducir el espacio
			document.add(title);

			// Subtítulo con el periodo del reporte
			Font periodFont = FontFactory.getFont(FontFactory.HELVETICA_OBLIQUE, 11, Color.DARK_GRAY);
			DateTimeFormatter dateFormatter = DateTimeFormatter.ofPattern("dd/MM/yyyy");
			String textoPeriodo = "Periodo: " + inicio.format(dateFormatter) + " al " + fin.format(dateFormatter);

			Paragraph periodo = new Paragraph(textoPeriodo, periodFont);
			periodo.setAlignment(Element.ALIGN_CENTER);
			periodo.setSpacingAfter(20);
			document.add(periodo);

			// Resumen de indicadores
			Font kpiFont = FontFactory.getFont(FontFactory.HELVETICA, 11);
			document.add(new Paragraph("Resumen del Periodo:", FontFactory.getFont(FontFactory.HELVETICA_BOLD, 12)));
			document.add(new Paragraph("- Total Recaudado: S/ " + reporte.getTotalVentas(), kpiFont));
			document.add(
					new Paragraph("- Productos Vendidos: " + reporte.getProductosVendidos() + " unidades", kpiFont));
			document.add(new Paragraph("- Producto Más Vendido: " + reporte.getProductoMasVendido(), kpiFont));
			document.add(new Paragraph("- Mes con Más Ventas: " + reporte.getMesMasVentas(), kpiFont));
			document.add(Chunk.NEWLINE);

			// Crear tabla 7 columnas con anchos relativos ajustados
			PdfPTable table = new PdfPTable(7);
			table.setWidthPercentage(100);
			// Reducimos un poco el ancho del cliente y cajero para que quepa la nueva
			// columna
			table.setWidths(new float[] { 1f, 1.8f, 3f, 2f, 1f, 1.2f, 1.5f });

			// Nombre de las columnas
			String[] headers = { "N°", "Fecha", "Cliente", "Cajero", "Items", "Unidades", "Total (S/.)" };
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
			for (Venta v : reporte.getPaginaVentas().getContent()) {
				table.addCell(new PdfPCell(new Phrase(String.valueOf(v.getIdVenta()), cellFont)));
				table.addCell(new PdfPCell(new Phrase(v.getFecha().format(formatter), cellFont)));
				table.addCell(new PdfPCell(
						new Phrase(v.getCliente().getNombre() + " " + v.getCliente().getApellido(), cellFont)));
				table.addCell(new PdfPCell(new Phrase(v.getUsuario().getNombre(), cellFont)));

				// N° Items distintos
				PdfPCell itemCell = new PdfPCell(
						new Phrase(String.valueOf(v.getListaDetallesVenta().size()), cellFont));
				itemCell.setHorizontalAlignment(Element.ALIGN_CENTER);
				table.addCell(itemCell);

				// Unidades Totales Físicas
				int unidadesFisicas = v.getListaDetallesVenta().stream().mapToInt(dv -> dv.getCantidad()).sum();
				PdfPCell unidadesCell = new PdfPCell(new Phrase(String.valueOf(unidadesFisicas), cellFont));
				unidadesCell.setHorizontalAlignment(Element.ALIGN_CENTER);
				table.addCell(unidadesCell);

				// Total
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
