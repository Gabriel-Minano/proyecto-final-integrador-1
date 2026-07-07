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

import com.sistema.botica.DTO.DatoMensualDTO;
import com.sistema.botica.DTO.ReporteAnualDTO;

@Service
public class ReporteAnualExportService {
    private static final String EMPRESA = "BOTICA\nCONQUISTADORES\nFARMA";
    private final DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm:ss");

    private static final Color AZUL_INTENSO = new Color(0, 0, 255);
    private static final Color GRIS_FONDO = new Color(241, 245, 249);
    private static final Color GRIS_BORDES = new Color(226, 232, 240);
    private static final Color TEXTO_OSCURO = new Color(0, 0, 153);

    public byte[] generarPdf(ReporteAnualDTO reporte) throws IOException {
        try (ByteArrayOutputStream out = new ByteArrayOutputStream()) {
            Document document = new Document(PageSize.A4.rotate());
            PdfWriter.getInstance(document, out);
            document.open();
            document.setMargins(20, 20, 20, 20);

            // 1. ENCABEZADO
            PdfPTable headerTable = new PdfPTable(2);
            headerTable.setWidthPercentage(100);
            headerTable.setWidths(new float[] { 2f, 1.2f });

            Font companyFont = FontFactory.getFont(FontFactory.HELVETICA_BOLD, 14, AZUL_INTENSO);
            Paragraph companyPara = new Paragraph(EMPRESA, companyFont);
            companyPara.setLeading(16);
            PdfPCell leftCell = new PdfPCell(companyPara);
            leftCell.setBorder(PdfPCell.NO_BORDER);
            leftCell.setVerticalAlignment(Element.ALIGN_MIDDLE);
            headerTable.addCell(leftCell);

            PdfPTable boxTable = new PdfPTable(1);
            boxTable.setWidthPercentage(100);

            Font boxTitleFont = FontFactory.getFont(FontFactory.HELVETICA_BOLD, 11, AZUL_INTENSO);
            PdfPCell boxTitleCell = new PdfPCell(new Phrase("REPORTE ANUAL DE VENTAS", boxTitleFont));
            boxTitleCell.setBorder(PdfPCell.NO_BORDER);
            boxTitleCell.setHorizontalAlignment(Element.ALIGN_CENTER);
            boxTitleCell.setPaddingTop(8);
            boxTable.addCell(boxTitleCell);

            Font boxNumFont = FontFactory.getFont(FontFactory.HELVETICA_BOLD, 9, TEXTO_OSCURO);
            PdfPCell boxNumCell = new PdfPCell(new Phrase("Año: " + reporte.getAnio(), boxNumFont));
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
                    "Generado: " + LocalDateTime.now().format(formatter), fechaGenFont);
            fechaGeneracion.setSpacingBefore(6);
            fechaGeneracion.setSpacingAfter(12);
            document.add(fechaGeneracion);

            // 2. TABLA PRINCIPAL CON 12 MESES
            PdfPTable tablaMonthly = new PdfPTable(3);
            tablaMonthly.setWidthPercentage(100);
            tablaMonthly.setWidths(new float[] { 2f, 2.5f, 2.5f });
            tablaMonthly.setSpacingAfter(14);

            // Headers
            Font tableHeaderFont = FontFactory.getFont(FontFactory.HELVETICA_BOLD, 9f, Color.WHITE);
            String[] headers = { "MES", "INGRESOS ACUMULADOS", "PROMEDIO MENSUAL" };
            for (String h : headers) {
                PdfPCell cell = new PdfPCell(new Phrase(h, tableHeaderFont));
                cell.setBackgroundColor(AZUL_INTENSO);
                cell.setBorder(PdfPCell.NO_BORDER);
                cell.setPadding(8);
                cell.setHorizontalAlignment(Element.ALIGN_CENTER);
                tablaMonthly.addCell(cell);
            }

            // Datos de meses
            Font cellFont = FontFactory.getFont(FontFactory.HELVETICA, 8f, TEXTO_OSCURO);
            for (DatoMensualDTO dato : reporte.getDatosMonthly()) {
                // Mes
                PdfPCell mesCell = new PdfPCell(new Phrase(dato.getMes(), cellFont));
                styleDataCell(mesCell, Element.ALIGN_LEFT);
                tablaMonthly.addCell(mesCell);

                // Ingresos totales
                PdfPCell ingresosCell = new PdfPCell(
                        new Phrase("S/ " + dato.getIngresosTotales().setScale(2), cellFont));
                styleDataCell(ingresosCell, Element.ALIGN_RIGHT);
                tablaMonthly.addCell(ingresosCell);

                // Promedio
                PdfPCell promedioCell = new PdfPCell(
                        new Phrase("S/ " + dato.getPromedioMensual().setScale(2), cellFont));
                styleDataCell(promedioCell, Element.ALIGN_RIGHT);
                tablaMonthly.addCell(promedioCell);
            }

            document.add(tablaMonthly);

            // 3. INDICADORES PRINCIPALES
            addBlueDashedLine(document);

            PdfPTable indicatoresTable = new PdfPTable(2);
            indicatoresTable.setWidthPercentage(100);
            indicatoresTable.setSpacingBefore(10);
            indicatoresTable.setSpacingAfter(14);

            // Promedio Anual
            PdfPCell indicCell1 = new PdfPCell();
            indicCell1.setBackgroundColor(GRIS_FONDO);
            indicCell1.setBorderColor(GRIS_BORDES);
            indicCell1.setBorderWidth(1f);
            indicCell1.setPadding(10);

            Font indicLblFont = FontFactory.getFont(FontFactory.HELVETICA_BOLD, 9f, new Color(15, 23, 42));
            Font indicValFont = FontFactory.getFont(FontFactory.HELVETICA, 9f, Color.DARK_GRAY);

            Paragraph lblPromedioAnual = new Paragraph("PROMEDIO ANUAL DE VENTAS", indicLblFont);
            indicCell1.addElement(lblPromedioAnual);

            Font indicValBigFont = FontFactory.getFont(FontFactory.HELVETICA_BOLD, 12f, AZUL_INTENSO);
            Paragraph valPromedioAnual = new Paragraph("S/ " + reporte.getPromedioAnual().setScale(2),
                    indicValBigFont);
            valPromedioAnual.setSpacingBefore(6);
            indicCell1.addElement(valPromedioAnual);
            indicatoresTable.addCell(indicCell1);

            // Total Anual
            PdfPCell indicCell2 = new PdfPCell();
            indicCell2.setBackgroundColor(GRIS_FONDO);
            indicCell2.setBorderColor(GRIS_BORDES);
            indicCell2.setBorderWidth(1f);
            indicCell2.setPadding(10);

            Paragraph lblTotalAnual = new Paragraph("TOTAL ANUAL DE VENTAS", indicLblFont);
            indicCell2.addElement(lblTotalAnual);

            Paragraph valTotalAnual = new Paragraph("S/ " + reporte.getTotalAnual().setScale(2), indicValBigFont);
            valTotalAnual.setSpacingBefore(6);
            indicCell2.addElement(valTotalAnual);
            indicatoresTable.addCell(indicCell2);

            document.add(indicatoresTable);

            // 4. MES CON MÁXIMO Y MÍNIMO
            PdfPTable maxMinTable = new PdfPTable(2);
            maxMinTable.setWidthPercentage(100);
            maxMinTable.setSpacingBefore(6);

            // Mes Máximo
            PdfPCell maxCell = new PdfPCell();
            maxCell.setBackgroundColor(new Color(200, 230, 201));
            maxCell.setBorderColor(new Color(76, 175, 80));
            maxCell.setBorderWidth(1.5f);
            maxCell.setPadding(10);

            Font maxLblFont = FontFactory.getFont(FontFactory.HELVETICA_BOLD, 9f, new Color(0, 100, 0));
            Paragraph lblMax = new Paragraph("MES CON MÁXIMAS VENTAS", maxLblFont);
            maxCell.addElement(lblMax);

            Font maxValFont = FontFactory.getFont(FontFactory.HELVETICA_BOLD, 10f, new Color(0, 100, 0));
            Paragraph valMax = new Paragraph(reporte.getMesMaximo() + " (S/ " +
                    reporte.getVentasMaximo().setScale(2) + ")", maxValFont);
            valMax.setSpacingBefore(6);
            maxCell.addElement(valMax);
            maxMinTable.addCell(maxCell);

            // Mes Mínimo
            PdfPCell minCell = new PdfPCell();
            minCell.setBackgroundColor(new Color(255, 205, 210));
            minCell.setBorderColor(new Color(244, 67, 54));
            minCell.setBorderWidth(1.5f);
            minCell.setPadding(10);

            Font minLblFont = FontFactory.getFont(FontFactory.HELVETICA_BOLD, 9f, new Color(100, 0, 0));
            Paragraph lblMin = new Paragraph("MES CON MÍNIMAS VENTAS", minLblFont);
            minCell.addElement(lblMin);

            Font minValFont = FontFactory.getFont(FontFactory.HELVETICA_BOLD, 10f, new Color(100, 0, 0));
            Paragraph valMin = new Paragraph(reporte.getMesMinimo() + " (S/ " +
                    reporte.getVentasMinimo().setScale(2) + ")", minValFont);
            valMin.setSpacingBefore(6);
            minCell.addElement(valMin);
            maxMinTable.addCell(minCell);

            document.add(maxMinTable);

            document.close();
            return out.toByteArray();
        }
    }

    private void styleDataCell(PdfPCell cell, int alignment) {
        cell.setBorder(PdfPCell.BOTTOM);
        cell.setBorderColor(GRIS_BORDES);
        cell.setBorderWidthBottom(0.5f);
        cell.setPadding(6);
        cell.setHorizontalAlignment(alignment);
    }

    private void addBlueDashedLine(Document document) throws IOException {
        PdfPTable dashTable = new PdfPTable(1);
        dashTable.setWidthPercentage(100);
        dashTable.setSpacingBefore(3);
        dashTable.setSpacingAfter(3);

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
