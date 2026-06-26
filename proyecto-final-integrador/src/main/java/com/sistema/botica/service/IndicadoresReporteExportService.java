package com.sistema.botica.service;

import java.awt.Color;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.time.format.DateTimeFormatter;

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

import com.sistema.botica.DTO.ReporteIndicadoresDTO;

@Service
public class IndicadoresReporteExportService {
    private final DateTimeFormatter dateFormatter = DateTimeFormatter.ofPattern("dd/MM/yyyy");

    public byte[] generarPdf(ReporteIndicadoresDTO reporte) throws IOException {
        try (ByteArrayOutputStream out = new ByteArrayOutputStream()) {

            // Página A4 en formato Horizontal (Landscape)
            Document document = new Document(PageSize.A4.rotate());
            PdfWriter.getInstance(document, out);
            document.open();

            // Título
            Font titleFont = FontFactory.getFont(FontFactory.HELVETICA_BOLD, 20, Color.BLUE);
            Paragraph title = new Paragraph("Reporte de Indicadores de Productos", titleFont);
            title.setAlignment(Element.ALIGN_CENTER);
            title.setSpacingAfter(5);
            document.add(title);

            // Periodo
            Font periodFont = FontFactory.getFont(FontFactory.HELVETICA_OBLIQUE, 11, Color.DARK_GRAY);
            String textoPeriodo = "Período: " + reporte.getFechaInicio().format(dateFormatter) + " al "
                    + reporte.getFechaFin().format(dateFormatter);
            Paragraph periodo = new Paragraph(textoPeriodo, periodFont);
            periodo.setAlignment(Element.ALIGN_CENTER);
            periodo.setSpacingAfter(25);
            document.add(periodo);

            // ========== SECCIÓN 1: INDICADORES DE INVENTARIO ==========
            Font sectionTitleFont = FontFactory.getFont(FontFactory.HELVETICA_BOLD, 14, new Color(13, 110, 253));
            Paragraph seccion1 = new Paragraph("Indicadores de Inventario", sectionTitleFont);
            seccion1.setSpacingBefore(10);
            seccion1.setSpacingAfter(15);
            document.add(seccion1);

            // Tabla de Indicadores de Inventario
            PdfPTable tablaInventario = new PdfPTable(2);
            tablaInventario.setWidthPercentage(100);
            tablaInventario.setWidths(new float[] { 2f, 1f });

            Font headerFont = FontFactory.getFont(FontFactory.HELVETICA_BOLD, 11, Color.WHITE);
            Font cellFont = FontFactory.getFont(FontFactory.HELVETICA, 10);

            // Headers
            PdfPCell headerIndicador = new PdfPCell(new org.openpdf.text.Phrase("Indicador", headerFont));
            headerIndicador.setBackgroundColor(new Color(13, 110, 253));
            headerIndicador.setPadding(8);
            tablaInventario.addCell(headerIndicador);

            PdfPCell headerValor = new PdfPCell(new org.openpdf.text.Phrase("Valor", headerFont));
            headerValor.setBackgroundColor(new Color(13, 110, 253));
            headerValor.setPadding(8);
            headerValor.setHorizontalAlignment(Element.ALIGN_RIGHT);
            tablaInventario.addCell(headerValor);

            // Filas de datos
            addInventoryRow(tablaInventario, "Total de Productos Activos", reporte.getTotalProductosActivos() + "",
                    cellFont);
            addInventoryRow(tablaInventario, "Productos Disponibles",
                    String.format("%.2f%%", reporte.getPorcentajeDisponibles()), cellFont);
            addInventoryRow(tablaInventario, "Productos Agotados",
                    String.format("%.2f%%", reporte.getPorcentajeAgotados()), cellFont);
            addInventoryRow(tablaInventario, "Productos con Stock Crítico",
                    String.format("%.2f%%", reporte.getPorcentajeStockCritico()), cellFont);
            addInventoryRow(tablaInventario, "Productos con Sobrestock", reporte.getTotalSobrestock() + "", cellFont);

            tablaInventario.setSpacingAfter(20);
            document.add(tablaInventario);

            // ========== SECCIÓN 2: ANÁLISIS DE VENTAS ==========
            Paragraph seccion2 = new Paragraph("Análisis de Ventas en el Período", sectionTitleFont);
            seccion2.setSpacingBefore(20);
            seccion2.setSpacingAfter(15);
            document.add(seccion2);

            // Tabla de Análisis de Ventas
            PdfPTable tablaVentas = new PdfPTable(2);
            tablaVentas.setWidthPercentage(100);
            tablaVentas.setWidths(new float[] { 2f, 1f });

            // Headers
            PdfPCell headerVentasIndicador = new PdfPCell(
                    new org.openpdf.text.Phrase("Métrica", headerFont));
            headerVentasIndicador.setBackgroundColor(new Color(40, 167, 69));
            headerVentasIndicador.setPadding(8);
            tablaVentas.addCell(headerVentasIndicador);

            PdfPCell headerVentasValor = new PdfPCell(new org.openpdf.text.Phrase("Resultado", headerFont));
            headerVentasValor.setBackgroundColor(new Color(40, 167, 69));
            headerVentasValor.setPadding(8);
            headerVentasValor.setHorizontalAlignment(Element.ALIGN_RIGHT);
            tablaVentas.addCell(headerVentasValor);

            // Filas de datos
            addSalesRow(tablaVentas, "Mes con Mayor Movimiento", reporte.getMesMasVentas(), cellFont);

            // Producto más vendido con salta de línea si es N/A
            String productoDisplay = reporte.getProductoMasVendido();
            if (!productoDisplay.equals("N/A")) {
                productoDisplay += " (" + reporte.getVentasProductoMasVendido() + " unidades)";
            }
            addSalesRow(tablaVentas, "Producto Más Vendido en Ese Mes", productoDisplay, cellFont);

            tablaVentas.setSpacingAfter(20);
            document.add(tablaVentas);

            // Notas finales
            Font notasFont = FontFactory.getFont(FontFactory.HELVETICA_OBLIQUE, 9, Color.GRAY);
            Paragraph notas = new Paragraph(
                    "Este reporte proporciona un análisis integral de los indicadores de inventario y ventas para el período seleccionado.",
                    notasFont);
            notas.setAlignment(Element.ALIGN_CENTER);
            notas.setSpacingBefore(20);
            document.add(notas);

            document.close();
            return out.toByteArray();
        }
    }

    private void addInventoryRow(PdfPTable table, String label, String valor, Font font) {
        PdfPCell labelCell = new PdfPCell(new org.openpdf.text.Phrase(label, font));
        labelCell.setPadding(7);
        labelCell.setBackgroundColor(new Color(240, 245, 250));
        table.addCell(labelCell);

        PdfPCell valorCell = new PdfPCell(new org.openpdf.text.Phrase(valor, font));
        valorCell.setPadding(7);
        valorCell.setBackgroundColor(new Color(240, 245, 250));
        valorCell.setHorizontalAlignment(Element.ALIGN_RIGHT);
        table.addCell(valorCell);
    }

    private void addSalesRow(PdfPTable table, String label, String valor, Font font) {
        PdfPCell labelCell = new PdfPCell(new org.openpdf.text.Phrase(label, font));
        labelCell.setPadding(7);
        labelCell.setBackgroundColor(new Color(240, 255, 240));
        table.addCell(labelCell);

        PdfPCell valorCell = new PdfPCell(new org.openpdf.text.Phrase(valor, font));
        valorCell.setPadding(7);
        valorCell.setBackgroundColor(new Color(240, 255, 240));
        valorCell.setHorizontalAlignment(Element.ALIGN_RIGHT);
        table.addCell(valorCell);
    }
}
