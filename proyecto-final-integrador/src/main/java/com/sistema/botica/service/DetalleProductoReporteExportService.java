package com.sistema.botica.service;

import java.awt.Color;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

import org.openpdf.text.Document;
import org.openpdf.text.Element;
import org.openpdf.text.Font;
import org.openpdf.text.FontFactory;
import org.openpdf.text.PageSize;
import org.openpdf.text.Paragraph;
import org.openpdf.text.Phrase;
import org.openpdf.text.pdf.PdfPCell;
import org.openpdf.text.pdf.PdfPTable;
import org.openpdf.text.pdf.PdfWriter;
import org.springframework.stereotype.Service;

import com.sistema.botica.DTO.DetalleProductoDTO;

@Service
public class DetalleProductoReporteExportService {
        private static final String EMPRESA = "BOTICA\nCONQUISTADORES\nFARMA";
        private final DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy");
        private final DateTimeFormatter formatterFull = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm:ss");

        private static final Color AZUL_INTENSO = new Color(0, 0, 255);
        private static final Color GRIS_FONDO = new Color(241, 245, 249);
        private static final Color GRIS_BORDES = new Color(226, 232, 240);
        private static final Color TEXTO_OSCURO = new Color(0, 0, 153);

        public byte[] generarPdf(DetalleProductoDTO detalle) throws IOException {
                try (ByteArrayOutputStream out = new ByteArrayOutputStream()) {
                        Document document = new Document(PageSize.A4);
                        PdfWriter.getInstance(document, out);
                        document.open();
                        document.setMargins(20, 20, 20, 20);

                        // 1. ENCABEZADO: NOMBRE VS TITULO DEL REPORTE
                        PdfPTable headerTable = new PdfPTable(2);
                        headerTable.setWidthPercentage(100);
                        headerTable.setWidths(new float[] { 1.8f, 1.2f });

                        Font companyFont = FontFactory.getFont(FontFactory.HELVETICA_BOLD, 14, AZUL_INTENSO);
                        Paragraph companyPara = new Paragraph(EMPRESA, companyFont);
                        companyPara.setLeading(16);
                        PdfPCell leftCell = new PdfPCell(companyPara);
                        leftCell.setBorder(PdfPCell.NO_BORDER);
                        leftCell.setVerticalAlignment(Element.ALIGN_MIDDLE);
                        headerTable.addCell(leftCell);

                        PdfPTable boxTable = new PdfPTable(1);
                        boxTable.setWidthPercentage(100);

                        Font boxTitleFont = FontFactory.getFont(FontFactory.HELVETICA_BOLD, 10, AZUL_INTENSO);
                        PdfPCell boxTitleCell = new PdfPCell(new Phrase("DETALLE DE PRODUCTO", boxTitleFont));
                        boxTitleCell.setBorder(PdfPCell.NO_BORDER);
                        boxTitleCell.setHorizontalAlignment(Element.ALIGN_CENTER);
                        boxTitleCell.setPaddingTop(8);
                        boxTable.addCell(boxTitleCell);

                        Font boxNumFont = FontFactory.getFont(FontFactory.HELVETICA_BOLD, 9, TEXTO_OSCURO);
                        PdfPCell boxNumCell = new PdfPCell(new Phrase("Código: " + detalle.getCodigo(), boxNumFont));
                        boxNumCell.setBorder(PdfPCell.NO_BORDER);
                        boxNumCell.setHorizontalAlignment(Element.ALIGN_CENTER);
                        boxNumCell.setPaddingBottom(8);
                        boxTable.addCell(boxNumCell);

                        PdfPCell rightCell = new PdfPCell(boxTable);
                        rightCell.setBorderColor(AZUL_INTENSO);
                        rightCell.setBorderWidth(1.5f);
                        rightCell.setPadding(0);
                        rightCell.setVerticalAlignment(Element.ALIGN_MIDDLE);
                        headerTable.addCell(rightCell);

                        document.add(headerTable);

                        Font fechaGenFont = FontFactory.getFont(FontFactory.HELVETICA, 7f, new Color(100, 116, 139));
                        Paragraph fechaGeneracion = new Paragraph(
                                        "Generado: " + LocalDateTime.now().format(formatterFull), fechaGenFont);
                        fechaGeneracion.setSpacingBefore(6);
                        fechaGeneracion.setSpacingAfter(12);
                        document.add(fechaGeneracion);

                        // 2. INFORMACIÓN BÁSICA DEL PRODUCTO
                        PdfPTable infoTable = new PdfPTable(1);
                        infoTable.setWidthPercentage(100);
                        infoTable.setSpacingAfter(14);

                        PdfPCell infoCell = new PdfPCell();
                        infoCell.setBackgroundColor(GRIS_FONDO);
                        infoCell.setBorderColor(GRIS_BORDES);
                        infoCell.setBorderWidth(1f);
                        infoCell.setPadding(10);

                        PdfPTable infoInternaTable = new PdfPTable(2);
                        infoInternaTable.setWidthPercentage(100);
                        infoInternaTable.setWidths(new float[] { 1f, 2f });

                        Font lblFont = FontFactory.getFont(FontFactory.HELVETICA_BOLD, 9f, new Color(15, 23, 42));
                        Font valFont = FontFactory.getFont(FontFactory.HELVETICA, 9f, Color.DARK_GRAY);

                        addRow(infoInternaTable, "NOMBRE:", detalle.getNombre(), lblFont, valFont);
                        addRow(infoInternaTable, "CATEGORÍA:", detalle.getCategoriaNombre(), lblFont, valFont);
                        addRow(infoInternaTable, "PROVEEDOR:", detalle.getProveedorNombre(), lblFont, valFont);

                        infoCell.addElement(infoInternaTable);
                        infoTable.addCell(infoCell);
                        document.add(infoTable);

                        // 3. PRECIOS Y STOCK
                        PdfPTable preciosTable = new PdfPTable(2);
                        preciosTable.setWidthPercentage(100);
                        preciosTable.setSpacingAfter(14);

                        PdfPCell preciosCell = new PdfPCell();
                        preciosCell.setBackgroundColor(GRIS_FONDO);
                        preciosCell.setBorderColor(GRIS_BORDES);
                        preciosCell.setBorderWidth(1f);
                        preciosCell.setPadding(10);

                        PdfPTable preciosInternaTable = new PdfPTable(4);
                        preciosInternaTable.setWidthPercentage(100);
                        preciosInternaTable.setWidths(new float[] { 1.2f, 1f, 1.2f, 1f });

                        Font precioLblFont = FontFactory.getFont(FontFactory.HELVETICA_BOLD, 8f, new Color(15, 23, 42));
                        Font precioValFont = FontFactory.getFont(FontFactory.HELVETICA, 8f, Color.DARK_GRAY);

                        addPrecioCell(preciosInternaTable, "P. Compra:", "S/ " + detalle.getPrecioCompra().setScale(2),
                                        precioLblFont, precioValFont);
                        addPrecioCell(preciosInternaTable, "P. Venta:", "S/ " + detalle.getPrecioVenta().setScale(2),
                                        precioLblFont, precioValFont);
                        addPrecioCell(preciosInternaTable, "Stock Actual:",
                                        String.valueOf(detalle.getStockActual()), precioLblFont, precioValFont);
                        addPrecioCell(preciosInternaTable, "Stock Mín:",
                                        String.valueOf(detalle.getStockMinimo()), precioLblFont, precioValFont);

                        preciosCell.addElement(preciosInternaTable);
                        infoTable.addCell(preciosCell);
                        document.add(preciosTable);

                        // 4. FECHAS Y ESTADO
                        PdfPTable fechasTable = new PdfPTable(1);
                        fechasTable.setWidthPercentage(100);
                        fechasTable.setSpacingAfter(14);

                        PdfPCell fechasCell = new PdfPCell();
                        fechasCell.setBackgroundColor(GRIS_FONDO);
                        fechasCell.setBorderColor(GRIS_BORDES);
                        fechasCell.setBorderWidth(1f);
                        fechasCell.setPadding(10);

                        PdfPTable fechasInternaTable = new PdfPTable(2);
                        fechasInternaTable.setWidthPercentage(100);
                        fechasInternaTable.setWidths(new float[] { 1.2f, 1.2f });

                        Font fechasLblFont = FontFactory.getFont(FontFactory.HELVETICA_BOLD, 8f, new Color(15, 23, 42));
                        Font fechasValFont = FontFactory.getFont(FontFactory.HELVETICA, 8f, Color.DARK_GRAY);

                        addFechaCell(fechasInternaTable, "Vencimiento:",
                                        detalle.getFechaVencimiento().format(formatter),
                                        fechasLblFont, fechasValFont);
                        addFechaCell(fechasInternaTable, "Estado:",
                                        detalle.getEstado() ? "Activo" : "Inactivo", fechasLblFont, fechasValFont);

                        fechasCell.addElement(fechasInternaTable);
                        fechasTable.addCell(fechasCell);
                        document.add(fechasTable);

                        // 5. PIE DE PÁGINA
                        document.add(new Paragraph(" "));
                        addBlueDashedLine(document);

                        document.close();
                        return out.toByteArray();
                }
        }

        private void addRow(PdfPTable table, String label, String value, Font lblFont, Font valFont) {
                PdfPCell lblCell = new PdfPCell(new Phrase(label, lblFont));
                lblCell.setBorder(PdfPCell.BOTTOM);
                lblCell.setBorderColor(GRIS_BORDES);
                lblCell.setBorderWidthBottom(0.5f);
                lblCell.setPaddingTop(6);
                lblCell.setPaddingBottom(6);
                lblCell.setPaddingLeft(4);
                table.addCell(lblCell);

                PdfPCell valCell = new PdfPCell(new Phrase(value, valFont));
                valCell.setBorder(PdfPCell.BOTTOM);
                valCell.setBorderColor(GRIS_BORDES);
                valCell.setBorderWidthBottom(0.5f);
                valCell.setPaddingTop(6);
                valCell.setPaddingBottom(6);
                valCell.setPaddingRight(4);
                table.addCell(valCell);
        }

        private void addPrecioCell(PdfPTable table, String label, String value, Font lblFont, Font valFont) {
                PdfPCell lblCell = new PdfPCell(new Phrase(label, lblFont));
                lblCell.setBorder(PdfPCell.BOTTOM);
                lblCell.setBorderColor(GRIS_BORDES);
                lblCell.setBorderWidthBottom(0.5f);
                lblCell.setPaddingTop(6);
                lblCell.setPaddingBottom(6);
                lblCell.setPaddingLeft(2);
                table.addCell(lblCell);

                PdfPCell valCell = new PdfPCell(new Phrase(value, valFont));
                valCell.setBorder(PdfPCell.BOTTOM);
                valCell.setBorderColor(GRIS_BORDES);
                valCell.setBorderWidthBottom(0.5f);
                valCell.setPaddingTop(6);
                valCell.setPaddingBottom(6);
                valCell.setHorizontalAlignment(Element.ALIGN_RIGHT);
                table.addCell(valCell);
        }

        private void addFechaCell(PdfPTable table, String label, String value, Font lblFont, Font valFont) {
                PdfPCell lblCell = new PdfPCell(new Phrase(label, lblFont));
                lblCell.setBorder(PdfPCell.BOTTOM);
                lblCell.setBorderColor(GRIS_BORDES);
                lblCell.setBorderWidthBottom(0.5f);
                lblCell.setPaddingTop(6);
                lblCell.setPaddingBottom(6);
                lblCell.setPaddingLeft(2);
                table.addCell(lblCell);

                PdfPCell valCell = new PdfPCell(new Phrase(value, valFont));
                valCell.setBorder(PdfPCell.BOTTOM);
                valCell.setBorderColor(GRIS_BORDES);
                valCell.setBorderWidthBottom(0.5f);
                valCell.setPaddingTop(6);
                valCell.setPaddingBottom(6);
                valCell.setHorizontalAlignment(Element.ALIGN_CENTER);
                table.addCell(valCell);
        }

        private void addBlueDashedLine(Document document) throws IOException {
                PdfPTable dashTable = new PdfPTable(1);
                dashTable.setWidthPercentage(100);
                dashTable.setSpacingBefore(6);
                dashTable.setSpacingAfter(6);

                Font dashFont = FontFactory.getFont(FontFactory.HELVETICA_BOLD, 10, AZUL_INTENSO);
                Phrase dashPhrase = new Phrase(
                                "- - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - -",
                                dashFont);

                PdfPCell cell = new PdfPCell(dashPhrase);
                cell.setBorder(PdfPCell.NO_BORDER);
                cell.setHorizontalAlignment(Element.ALIGN_CENTER);
                cell.setPadding(0);

                dashTable.addCell(cell);
                document.add(dashTable);
        }
}
