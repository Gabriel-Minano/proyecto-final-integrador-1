package com.sistema.botica.service;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.time.format.DateTimeFormatter;

import java.awt.Color;
import org.openpdf.text.Document;
import org.openpdf.text.Element;
import org.openpdf.text.Font;
import org.openpdf.text.FontFactory;
import org.openpdf.text.PageSize;
import org.openpdf.text.Paragraph;
import org.openpdf.text.pdf.PdfPCell;
import org.openpdf.text.pdf.PdfPTable;
import org.openpdf.text.pdf.PdfWriter;
import org.springframework.stereotype.Service;

import com.sistema.botica.DTO.BoletaVentaDTO;
import com.sistema.botica.entity.DetalleVenta;
@Service
public class BoletaReporteExportService {
    private final DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm:ss");
	// private final DateTimeFormatter dateFormatter = DateTimeFormatter.ofPattern("dd/MM/yyyy");
	// private final DateTimeFormatter timeFormatter = DateTimeFormatter.ofPattern("HH:mm:ss");

    // ==========================================
	// EXPORTACIÓN A PDF
	// ==========================================
	public byte[] generarPdf(BoletaVentaDTO boleta) throws IOException {
		try (ByteArrayOutputStream out = new ByteArrayOutputStream()) {
			Document document = new Document(PageSize.A6);
			PdfWriter.getInstance(document, out);
			document.open();
			document.setMargins(10, 10, 10, 10);

			// Encabezado
			Font titleFont = FontFactory.getFont(FontFactory.HELVETICA_BOLD, 14, Color.BLACK);
			Paragraph title = new Paragraph("CONQUISTADORES FARMA", titleFont);
			title.setAlignment(Element.ALIGN_CENTER);
			title.setSpacingAfter(2);
			document.add(title);


			// Línea divisoria
			Paragraph line1 = new Paragraph("════════════════════════");
			line1.setAlignment(Element.ALIGN_CENTER);
			document.add(line1);

			// Información de boleta
			Font infoFont = FontFactory.getFont(FontFactory.HELVETICA, 8);
			Paragraph bolNum = new Paragraph("BOLETA N°: " + boleta.getIdVenta(), infoFont);
			bolNum.setAlignment(Element.ALIGN_CENTER);
			document.add(bolNum);

			Paragraph fecha = new Paragraph("Fecha: " + boleta.getFecha().format(formatter), infoFont);
			fecha.setAlignment(Element.ALIGN_CENTER);
			document.add(fecha);

			Paragraph cliente = new Paragraph(
					"Cliente: " + boleta.getClienteNombre() + " " + boleta.getClienteApellido(), infoFont);
			cliente.setAlignment(Element.ALIGN_LEFT);
			document.add(cliente);

			Paragraph cajer = new Paragraph("Cajero: " + boleta.getUsuarioNombre(), infoFont);
			cajer.setAlignment(Element.ALIGN_LEFT);
			document.add(cajer);

			// Línea divisoria
			Paragraph line2 = new Paragraph("════════════════════════");
			line2.setAlignment(Element.ALIGN_CENTER);
			document.add(line2);

			// Tabla de detalles
			PdfPTable table = new PdfPTable(4);
			table.setWidthPercentage(100);
			table.setWidths(new float[] { 2f, 1.5f, 1.5f, 1.5f });

			Font headerFont = FontFactory.getFont(FontFactory.HELVETICA_BOLD, 7, Color.WHITE);
			String[] headers = { "Producto", "Cant.", "P.Unit", "Subtotal" };
			for (String h : headers) {
				PdfPCell cell = new PdfPCell(new org.openpdf.text.Phrase(h, headerFont));
				cell.setBackgroundColor(new Color(68, 114, 196));
				cell.setPadding(3);
				cell.setHorizontalAlignment(Element.ALIGN_CENTER);
				table.addCell(cell);
			}

			Font cellFont = FontFactory.getFont(FontFactory.HELVETICA, 7);
			for (DetalleVenta detalle : boleta.getDetalles()) {
				table.addCell(new PdfPCell(
						new org.openpdf.text.Phrase(detalle.getProducto().getNombre(), cellFont)));

				PdfPCell cantCell = new PdfPCell(
						new org.openpdf.text.Phrase(String.valueOf(detalle.getCantidad()), cellFont));
				cantCell.setHorizontalAlignment(Element.ALIGN_CENTER);
				table.addCell(cantCell);

				PdfPCell precioCell = new PdfPCell(
						new org.openpdf.text.Phrase(detalle.getPrecioUnitario().toString(), cellFont));
				precioCell.setHorizontalAlignment(Element.ALIGN_RIGHT);
				table.addCell(precioCell);

				PdfPCell subtotalCell = new PdfPCell(
						new org.openpdf.text.Phrase(detalle.getSubtotal().toString(), cellFont));
				subtotalCell.setHorizontalAlignment(Element.ALIGN_RIGHT);
				table.addCell(subtotalCell);
			}

			document.add(table);

			// Línea divisoria
			Paragraph line3 = new Paragraph("════════════════════════");
			line3.setAlignment(Element.ALIGN_CENTER);
			document.add(line3);

			// Total
			Font totalFont = FontFactory.getFont(FontFactory.HELVETICA_BOLD, 10);
			Paragraph totalP = new Paragraph("TOTAL: S/ " + boleta.getTotal().setScale(2), totalFont);
			totalP.setAlignment(Element.ALIGN_RIGHT);
			document.add(totalP);

			// Pie de página
			document.add(new Paragraph(" "));
			Font footerFont = FontFactory.getFont(FontFactory.HELVETICA, 8, Color.GRAY);
			Paragraph footer = new Paragraph("¡Gracias por su compra!", footerFont);
			footer.setAlignment(Element.ALIGN_CENTER);
			document.add(footer);

			Paragraph footer2 = new Paragraph("Vuelva pronto", footerFont);
			footer2.setAlignment(Element.ALIGN_CENTER);
			document.add(footer2);

			document.close();
			return out.toByteArray();
		}
	}
}
