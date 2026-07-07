package com.sistema.botica.service;

import java.awt.Color;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

import org.openpdf.text.*;
import org.openpdf.text.pdf.*;
import org.springframework.stereotype.Service;

import com.sistema.botica.DTO.ReporteIndicadoresDTO;

@Service
public class IndicadoresReporteExportService {

    private static final String EMPRESA = "BOTICA CONQUISTADORES FARMA";
    
    // === PALETA DE COLORES===
    private static final Color AZUL_INTENSO = new Color(0, 0, 255);
    private static final Color GRIS_OSCURO = new Color(51, 65, 85);
    private static final Color GRIS_CLARO_TEXTO = new Color(100, 116, 139);
    private static final Color GRIS_FONDO_FILA = new Color(248, 250, 252); // Gris muy claro para intercalar
    private static final Color GRIS_LINEA = new Color(226, 232, 240);

    private final DateTimeFormatter dateFormatter = DateTimeFormatter.ofPattern("dd/MM/yyyy");
    private final DateTimeFormatter dateTimeFormatter = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm");

    public byte[] generarPdf(ReporteIndicadoresDTO reporte) throws IOException {
        try (ByteArrayOutputStream out = new ByteArrayOutputStream()) {
            Document document = new Document(PageSize.A4, 40, 40, 40, 40);
            PdfWriter.getInstance(document, out);
            document.open();
        

            Font mainTitleFont = FontFactory.getFont(FontFactory.HELVETICA_BOLD, 22, new Color(0, 0, 255)); // Azul_Intenso
            Font subTitleFont = FontFactory.getFont(FontFactory.HELVETICA_BOLD, 14, new Color(51, 65, 85)); // Gris_Oscuro
            

            // === 1. CABECERA PRINCIPAL ===
            Paragraph title = new Paragraph(EMPRESA, mainTitleFont);
            title.setAlignment(Element.ALIGN_CENTER);
            title.setSpacingAfter(4);
            document.add(title);

            Paragraph sub = new Paragraph("Reporte de Ventas", subTitleFont);
            sub.setAlignment(Element.ALIGN_CENTER);
            sub.setSpacingAfter(4);
            document.add(sub);

            // Metadatos (Fecha y Período)
            Font metaFont = FontFactory.getFont(FontFactory.HELVETICA, 9, GRIS_CLARO_TEXTO);
            Paragraph fechaGen = new Paragraph("Fecha de generación: " + LocalDateTime.now().format(dateTimeFormatter), metaFont);
            fechaGen.setAlignment(Element.ALIGN_CENTER);
            document.add(fechaGen);

            String textoPeriodo = "Período del reporte: " + reporte.getFechaInicio().format(dateFormatter) + " al " + reporte.getFechaFin().format(dateFormatter);
            Paragraph periodo = new Paragraph(textoPeriodo, metaFont);
            periodo.setAlignment(Element.ALIGN_CENTER);
            periodo.setSpacingAfter(15);
            document.add(periodo);

            // Línea separadora sutil
            PdfPTable lineTable = new PdfPTable(1);
            lineTable.setWidthPercentage(100);
            PdfPCell lineCell = new PdfPCell();
            lineCell.setBorder(Rectangle.BOTTOM);
            lineCell.setBorderColor(GRIS_LINEA);
            lineCell.setBorderWidth(1f);
            lineCell.setPadding(0);
            lineTable.addCell(lineCell);
            lineTable.setSpacingAfter(25);
            document.add(lineTable);

            // === 2. SECCIÓN: INDICADORES DE INVENTARIO ===
            addSectionTitle(document, "| INDICADORES DE INVENTARIO", AZUL_INTENSO);
            
            PdfPTable tableInv = createTable();
            // Headers
            Font hFont = FontFactory.getFont(FontFactory.HELVETICA_BOLD, 10, Color.WHITE);
            tableInv.addCell(createHeaderCell("INDICADOR", hFont, AZUL_INTENSO, Element.ALIGN_LEFT));
            tableInv.addCell(createHeaderCell("VALOR", hFont, AZUL_INTENSO, Element.ALIGN_CENTER));

            // Data Rows (Intercalando blanco y gris)
            addDataRow(tableInv, "Total de Productos Activos", String.valueOf(reporte.getTotalProductosActivos()), true);
            addDataRow(tableInv, "Productos Disponibles", String.format("%.2f%%", reporte.getPorcentajeDisponibles()), false);
            addDataRow(tableInv, "Productos Agotados", String.format("%.2f%%", reporte.getPorcentajeAgotados()), true);
            addDataRow(tableInv, "Productos con Stock Crítico", String.format("%.2f%%", reporte.getPorcentajeStockCritico()), false);
            addDataRow(tableInv, "Productos con Sobrestock", String.valueOf(reporte.getTotalSobrestock()), true);
            
            tableInv.setSpacingAfter(30);
            document.add(tableInv);

            // === 3. SECCIÓN: RESULTADOS DEL PERÍODO ===
            addSectionTitle(document, "| RESULTADOS DEL PERÍODO", AZUL_INTENSO);
            
            PdfPTable tableVentas = createTable();
            // Headers
            tableVentas.addCell(createHeaderCell("MÉTRICA", hFont, AZUL_INTENSO, Element.ALIGN_LEFT));
            tableVentas.addCell(createHeaderCell("RESULTADO", hFont, AZUL_INTENSO, Element.ALIGN_CENTER));

            // Data Rows
            addDataRow(tableVentas, "Mes con Mayor Movimiento", reporte.getMesMasVentas(), true);
            
            String productoDisplay = reporte.getProductoMasVendido();
            if (productoDisplay != null && !productoDisplay.equals("N/A") && reporte.getVentasProductoMasVendido() > 0) {
                productoDisplay += " (" + reporte.getVentasProductoMasVendido() + " unidades)";
            }
            addDataRow(tableVentas, "Producto Más Vendido en Ese Mes", productoDisplay, false);
            
            tableVentas.setSpacingAfter(40);
            document.add(tableVentas);

            // === 4. PIE DE PÁGINA ===
            Font footerFont = FontFactory.getFont(FontFactory.HELVETICA_OBLIQUE, 9, GRIS_CLARO_TEXTO);
            Paragraph footer = new Paragraph("Este reporte proporciona un análisis integral de los indicadores de inventario y ventas para el período seleccionado.", footerFont);
            footer.setAlignment(Element.ALIGN_CENTER);
            document.add(footer);

            document.close();
            return out.toByteArray();
        }
    }

    // --- MÉTODOS AUXILIARES ---

    private void addSectionTitle(Document doc, String text, Color color) throws DocumentException {
        Font font = FontFactory.getFont(FontFactory.HELVETICA_BOLD, 12, color);
        Paragraph p = new Paragraph(text, font);
        p.setSpacingAfter(10);
        doc.add(p);
    }

    private PdfPTable createTable() {
        PdfPTable table = new PdfPTable(2);
        table.setWidthPercentage(100);
        try { 
            table.setWidths(new float[]{3f, 1.5f}); 
        } catch (Exception ignored) {}
        return table;
    }

    private PdfPCell createHeaderCell(String text, Font font, Color bg, int alignment) {
        PdfPCell cell = new PdfPCell(new Phrase(text, font));
        cell.setBackgroundColor(bg);
        cell.setPadding(10);
        cell.setPaddingLeft(15);
        cell.setHorizontalAlignment(alignment);
        cell.setVerticalAlignment(Element.ALIGN_MIDDLE);
        cell.setBorder(Rectangle.NO_BORDER);
        return cell;
    }

    private void addDataRow(PdfPTable table, String label, String value, boolean isWhite) {
        Color bgColor = isWhite ? Color.WHITE : GRIS_FONDO_FILA;
        Font font = FontFactory.getFont(FontFactory.HELVETICA, 10, GRIS_OSCURO);

        // Celda Izquierda (Métrica/Indicador)
        PdfPCell cellLabel = new PdfPCell(new Phrase(label, font));
        cellLabel.setBackgroundColor(bgColor);
        cellLabel.setPadding(10);
        cellLabel.setPaddingLeft(15);
        cellLabel.setHorizontalAlignment(Element.ALIGN_LEFT);
        cellLabel.setVerticalAlignment(Element.ALIGN_MIDDLE);
        cellLabel.setBorder(Rectangle.NO_BORDER); // Sin bordes
        table.addCell(cellLabel);

        // Celda Derecha (Valor)
        PdfPCell cellValue = new PdfPCell(new Phrase(value, font));
        cellValue.setBackgroundColor(bgColor);
        cellValue.setPadding(10);
        cellValue.setHorizontalAlignment(Element.ALIGN_CENTER);
        cellValue.setVerticalAlignment(Element.ALIGN_MIDDLE);
        cellValue.setBorder(Rectangle.NO_BORDER); // Sin bordes
        table.addCell(cellValue);
    }
}