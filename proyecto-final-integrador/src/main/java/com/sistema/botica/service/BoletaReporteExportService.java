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

import com.sistema.botica.DTO.BoletaVentaDTO;
import com.sistema.botica.entity.DetalleVenta;

@Service
public class BoletaReporteExportService {

    private static final String EMPRESA = "BOTICA\nCONQUISTADORES\nFARMA";
    private final DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm:ss");

    private static final Color AZUL_INTENSO = new Color(0, 0, 255);
    private static final Color GRIS_FONDO = new Color(241, 245, 249);
    private static final Color GRIS_BORDES = new Color(226, 232, 240);
    private static final Color TEXTO_OSCURO = new Color(0, 0, 153);

    public byte[] generarPdf(BoletaVentaDTO boleta) throws IOException {
        try (ByteArrayOutputStream out = new ByteArrayOutputStream()) {
            Document document = new Document(PageSize.A6);
            PdfWriter.getInstance(document, out);
            document.open();
            document.setMargins(15, 15, 15, 15);

            // 1. ENCABEZADO: NOMBRE VS CAJA DE BOLETA
            PdfPTable headerTable = new PdfPTable(2);
            headerTable.setWidthPercentage(100);
            headerTable.setWidths(new float[]{1.8f, 1.2f});

            Font companyFont = FontFactory.getFont(FontFactory.HELVETICA_BOLD, 12, AZUL_INTENSO);
            Paragraph companyPara = new Paragraph(EMPRESA, companyFont);
            companyPara.setLeading(14);
            PdfPCell leftCell = new PdfPCell(companyPara);
            leftCell.setBorder(PdfPCell.NO_BORDER);
            leftCell.setVerticalAlignment(Element.ALIGN_MIDDLE);
            headerTable.addCell(leftCell);

            PdfPTable boxTable = new PdfPTable(1);
            boxTable.setWidthPercentage(100);
            
            Font boxTitleFont = FontFactory.getFont(FontFactory.HELVETICA_BOLD, 8, AZUL_INTENSO);
            PdfPCell boxTitleCell = new PdfPCell(new Phrase("BOLETA", boxTitleFont));
            boxTitleCell.setBorder(PdfPCell.NO_BORDER);
            boxTitleCell.setHorizontalAlignment(Element.ALIGN_CENTER);
            boxTitleCell.setPaddingTop(6);
            boxTable.addCell(boxTitleCell);

            Font boxNumFont = FontFactory.getFont(FontFactory.HELVETICA_BOLD, 7, TEXTO_OSCURO);
            PdfPCell boxNumCell = new PdfPCell(new Phrase("N°: " + boleta.getIdVenta(), boxNumFont));
            boxNumCell.setBorder(PdfPCell.NO_BORDER);
            boxNumCell.setHorizontalAlignment(Element.ALIGN_CENTER);
            boxNumCell.setPaddingBottom(6);
            boxTable.addCell(boxNumCell);

            PdfPCell rightCell = new PdfPCell(boxTable);
            rightCell.setBorderColor(AZUL_INTENSO);
            rightCell.setBorderWidth(1.2f);
            rightCell.setPadding(0);
            rightCell.setVerticalAlignment(Element.ALIGN_MIDDLE);
            headerTable.addCell(rightCell);

            document.add(headerTable);

            Font fechaGenFont = FontFactory.getFont(FontFactory.HELVETICA, 5f, new Color(100, 116, 139));
            Paragraph fechaGeneracion = new Paragraph("Generado: " + LocalDateTime.now().format(formatter), fechaGenFont);
            fechaGeneracion.setSpacingBefore(4);
            fechaGeneracion.setSpacingAfter(10);
            document.add(fechaGeneracion);

            // 2. BLOQUE INFORMATIVO
            PdfPTable contenedorInfo = new PdfPTable(1);
            contenedorInfo.setWidthPercentage(100);
            contenedorInfo.setSpacingAfter(12);

            PdfPCell celdaBase = new PdfPCell();
            celdaBase.setBackgroundColor(GRIS_FONDO);
            celdaBase.setBorderColor(GRIS_BORDES);
            celdaBase.setBorderWidth(0.8f);
            celdaBase.setPadding(8);

            // Anchos de columna optimizados para que no existan cortes de texto
            PdfPTable tablaInternaInvisible = new PdfPTable(4);
            tablaInternaInvisible.setWidthPercentage(100);
            tablaInternaInvisible.setWidths(new float[]{1.8f, 2.9f, 1.7f, 2.6f}); 

            Font lblFont = FontFactory.getFont(FontFactory.HELVETICA_BOLD, 5.5f, Font.NORMAL, new Color(15, 23, 42));
            Font valFont = FontFactory.getFont(FontFactory.HELVETICA, 5.5f, Color.DARK_GRAY);

            addInvisibleCell(tablaInternaInvisible, "FECHA:", lblFont, 1);
            addInvisibleCell(tablaInternaInvisible, boleta.getFecha().format(DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm:ss")), valFont, 1);
            addInvisibleCell(tablaInternaInvisible, "CAJERO:", lblFont, 1);
            addInvisibleCell(tablaInternaInvisible, boleta.getUsuarioNombre(), valFont, 1);

            addInvisibleCell(tablaInternaInvisible, "CLIENTE:", lblFont, 1);
            addInvisibleCell(tablaInternaInvisible, boleta.getClienteNombre() + " " + boleta.getClienteApellido(), valFont, 3);

            celdaBase.addElement(tablaInternaInvisible);
            contenedorInfo.addCell(celdaBase);
            document.add(contenedorInfo);

            // 3. TABLA PRINCIPAL DE PRODUCTOS
            PdfPTable table = new PdfPTable(4);
            table.setWidthPercentage(100);
            table.setWidths(new float[]{2.6f, 1.0f, 1.2f, 1.4f});

            Font tableHeaderFont = FontFactory.getFont(FontFactory.HELVETICA_BOLD, 5f, Color.WHITE);
            String[] headers = {"PRODUCTO", "CANT.", "P.UNIT", "SUBTOTAL"};
            for (String h : headers) {
                PdfPCell cell = new PdfPCell(new Phrase(h, tableHeaderFont));
                cell.setBackgroundColor(AZUL_INTENSO);
                cell.setBorder(PdfPCell.NO_BORDER);
                cell.setPadding(6);
                if (!h.equals("PRODUCTO")) {
                    cell.setHorizontalAlignment(Element.ALIGN_CENTER);
                }
                table.addCell(cell);
            }

            Font cellFont = FontFactory.getFont(FontFactory.HELVETICA, 5f, TEXTO_OSCURO);
            for (DetalleVenta detalle : boleta.getDetalles()) {
                PdfPCell pCell = new PdfPCell(new Phrase(detalle.getProducto().getNombre(), cellFont));
                styleDataCell(pCell, Element.ALIGN_LEFT);
                table.addCell(pCell);

                PdfPCell cantCell = new PdfPCell(new Phrase(String.valueOf(detalle.getCantidad()), cellFont));
                styleDataCell(cantCell, Element.ALIGN_CENTER);
                table.addCell(cantCell);

                PdfPCell precioCell = new PdfPCell(new Phrase(String.format("%.2f", detalle.getPrecioUnitario().doubleValue()), cellFont));
                styleDataCell(precioCell, Element.ALIGN_RIGHT);
                table.addCell(precioCell);

                PdfPCell subtotalCell = new PdfPCell(new Phrase(String.format("%.2f", detalle.getSubtotal().doubleValue()), cellFont));
                styleDataCell(subtotalCell, Element.ALIGN_RIGHT);
                table.addCell(subtotalCell);
            }
            document.add(table);

            // Separador superior al total controlado con tabla para evitar colisiones
            addBlueDashedLine(document);

            // 4. SECCIÓN DEL TOTAL GENERAL
            PdfPTable totalTable = new PdfPTable(2);
            totalTable.setWidthPercentage(100);
            totalTable.setWidths(new float[]{5.0f, 1.0f});

            Font totalLblFont = FontFactory.getFont(FontFactory.HELVETICA_BOLD, 7f, TEXTO_OSCURO);
            PdfPCell totalLblCell = new PdfPCell(new Phrase("TOTAL:", totalLblFont));
            totalLblCell.setBorder(PdfPCell.NO_BORDER);
            totalLblCell.setHorizontalAlignment(Element.ALIGN_RIGHT);
            totalLblCell.setVerticalAlignment(Element.ALIGN_MIDDLE);
            totalLblCell.setPaddingRight(4f);
			totalTable.addCell(totalLblCell);

            Font totalValFont = FontFactory.getFont(FontFactory.HELVETICA_BOLD, 7.5f, AZUL_INTENSO);
            PdfPCell totalValCell = new PdfPCell(new Phrase("S/ " + boleta.getTotal().setScale(2), totalValFont));
            totalValCell.setBorder(PdfPCell.NO_BORDER);
            totalValCell.setHorizontalAlignment(Element.ALIGN_RIGHT);
            totalValCell.setVerticalAlignment(Element.ALIGN_MIDDLE);
            totalTable.addCell(totalValCell);
            
            document.add(totalTable);

            // Separador inferior del total controlado con tabla
            addBlueDashedLine(document);

            // 5. PIE DE PÁGINA
            document.add(new Paragraph(" "));
            Font footerBoldFont = FontFactory.getFont(FontFactory.HELVETICA_BOLD, 6f, TEXTO_OSCURO);
            Paragraph footer = new Paragraph("¡Gracias por su compra!", footerBoldFont);
            footer.setAlignment(Element.ALIGN_CENTER);
            footer.setSpacingAfter(3);
            document.add(footer);

            Font footerItalicFont = FontFactory.getFont(FontFactory.HELVETICA_OBLIQUE, 5f, Color.GRAY);
            Paragraph footer2 = new Paragraph("Vuelva pronto.", footerItalicFont);
            footer2.setAlignment(Element.ALIGN_CENTER);
            document.add(footer2);

            document.close();
            return out.toByteArray();
        }
    }

    private void addInvisibleCell(PdfPTable table, String text, Font font, int colspan) {
        PdfPCell cell = new PdfPCell(new Phrase(text, font));
        cell.setBorder(PdfPCell.NO_BORDER);
        cell.setPaddingTop(2f);
        cell.setPaddingBottom(2f);
        cell.setPaddingLeft(2f);
        cell.setPaddingRight(2f);
        cell.setColspan(colspan);
        table.addCell(cell);
    }

    private void styleDataCell(PdfPCell cell, int alignment) {
        cell.setBorder(PdfPCell.BOTTOM);
        cell.setBorderColor(GRIS_BORDES);
        cell.setBorderWidthBottom(0.5f);
        cell.setPadding(6);
        cell.setHorizontalAlignment(alignment);
    }

    // Dibujamos una línea con guiones clásica agregando un padding transparente de seguridad
    private void addBlueDashedLine(Document document) throws IOException {
        PdfPTable dashTable = new PdfPTable(1);
        dashTable.setWidthPercentage(100);
        dashTable.setSpacingBefore(3); // Margen de separación antes de la línea
        dashTable.setSpacingAfter(3);  // Margen de separación después de la línea

        Font dashFont = FontFactory.getFont(FontFactory.HELVETICA_BOLD, 8, AZUL_INTENSO);
        // Construimos la hilera de guiones azules
        Phrase dashPhrase = new Phrase("- - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - -", dashFont);
        
        PdfPCell cell = new PdfPCell(dashPhrase);
        cell.setBorder(PdfPCell.NO_BORDER);
        cell.setHorizontalAlignment(Element.ALIGN_CENTER);
        cell.setPadding(0);
        
        dashTable.addCell(cell);
        document.add(dashTable);
	}
}