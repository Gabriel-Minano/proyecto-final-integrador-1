package com.sistema.botica.controller;

import java.io.IOException;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.temporal.TemporalAdjusters;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import com.sistema.botica.DTO.ReporteVentasDTO;
import com.sistema.botica.service.ReporteExportService;
import com.sistema.botica.service.ReporteService;

@Controller
@RequestMapping("/reportes")
public class ReporteController {

	@Autowired
	private ReporteService reporteService;
	@Autowired
    private ReporteExportService exportService;

	@GetMapping("/ventas")
	public String verReporteVentas(
			@RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate fechaDesde,
			@RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate fechaHasta,
			Model modelo) {
		// Si es la primera vez que entra, seteamos las fechas por defecto
        if (fechaDesde == null) {
            fechaDesde = LocalDate.now(); // Ejemplo: 01 de Junio de 2026
        }
        if (fechaHasta == null) {
            fechaHasta = LocalDate.now().with(TemporalAdjusters.lastDayOfMonth()); // Fin de mes (30 de Junio)
        }

        // Convertimos LocalDate (Fechas) a LocalDateTime (Fechas con hora 00:00:00 y 23:59:59)
        LocalDateTime inicio = fechaDesde.atStartOfDay();
        LocalDateTime fin = fechaHasta.atTime(LocalTime.MAX);

        // Procesamos el reporte
        ReporteVentasDTO reporte = reporteService.generarReporteVentas(inicio, fin);

        modelo.addAttribute("reporte", reporte);
        modelo.addAttribute("fechaDesde", fechaDesde);
        modelo.addAttribute("fechaHasta", fechaHasta);
		return "reportes_ventas";
	}
	// Ruta para descargar PDF
    @GetMapping("/ventas/exportar/pdf")
    public ResponseEntity<byte[]> exportarPdf(
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate fechaDesde,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate fechaHasta) throws IOException {

        // 1. Generar la matemática del reporte para esas fechas
        ReporteVentasDTO reporte = reporteService.generarReporteVentas(fechaDesde.atStartOfDay(), fechaHasta.atTime(LocalTime.MAX));
        
        // 2. Convertir el reporte en un archivo PDF binario
        byte[] pdfContent = exportService.generarPdf(reporte);

        // 3. Preparar las cabeceras para que el navegador inicie la descarga
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_PDF);
        headers.setContentDispositionFormData("attachment", "Reporte_Ventas_ConquistadoresFarma.pdf");

        return new ResponseEntity<>(pdfContent, headers, HttpStatus.OK);
    }

    // Ruta para descargar Excel
    @GetMapping("/ventas/exportar/excel")
    public ResponseEntity<byte[]> exportarExcel(
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate fechaDesde,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate fechaHasta) throws IOException {

        ReporteVentasDTO reporte = reporteService.generarReporteVentas(fechaDesde.atStartOfDay(), fechaHasta.atTime(LocalTime.MAX));
        byte[] excelContent = exportService.generarExcel(reporte);

        HttpHeaders headers = new HttpHeaders();
        // Tipo MIME oficial para archivos .xlsx
        headers.setContentType(MediaType.parseMediaType("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet")); 
        headers.setContentDispositionFormData("attachment", "Reporte_Ventas_ConquistadoresFarma.xlsx");

        return new ResponseEntity<>(excelContent, headers, HttpStatus.OK);
    }
}
