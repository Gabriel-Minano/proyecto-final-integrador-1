package com.sistema.botica.controller;

import java.io.IOException;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.temporal.TemporalAdjusters;

import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import com.sistema.botica.DTO.ReporteIndicadoresDTO;
import com.sistema.botica.service.IndicadoresReporteExportService;
import com.sistema.botica.service.IndicadoresReporteService;

@Controller
@RequestMapping("/reportes/indicadores")
public class IndicadoresReporteController {
    private final IndicadoresReporteService indicadoresReporteService;
    private final IndicadoresReporteExportService exportService;

    IndicadoresReporteController(IndicadoresReporteService indicadoresReporteService,
            IndicadoresReporteExportService exportService) {
        this.indicadoresReporteService = indicadoresReporteService;
        this.exportService = exportService;
    }

    @GetMapping
    public String verReporteIndicadores(
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate fechaDesde,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate fechaHasta,
            Model model) {

        // Manejo de fechas por defecto (Mes actual)
        if (fechaDesde == null) {
            fechaDesde = LocalDate.now().withDayOfMonth(1);
        }
        if (fechaHasta == null) {
            fechaHasta = LocalDate.now().with(TemporalAdjusters.lastDayOfMonth());
        }

        LocalDateTime inicio = fechaDesde.atStartOfDay();
        LocalDateTime fin = fechaHasta.atTime(LocalTime.MAX);

        // Generación del reporte
        ReporteIndicadoresDTO reporte = indicadoresReporteService.generarReporteIndicadores(inicio, fin);

        // Inyección de datos a la vista
        model.addAttribute("reporte", reporte);
        model.addAttribute("fechaDesde", fechaDesde);
        model.addAttribute("fechaHasta", fechaHasta);

        return "reportes_indicadores";
    }

    @GetMapping("/exportar/pdf")
    public ResponseEntity<byte[]> exportarPdf(
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate fechaDesde,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate fechaHasta) throws IOException {

        LocalDateTime inicio = fechaDesde.atStartOfDay();
        LocalDateTime fin = fechaHasta.atTime(LocalTime.MAX);

        ReporteIndicadoresDTO reporte = indicadoresReporteService.generarReporteIndicadores(inicio, fin);
        byte[] pdfContent = exportService.generarPdf(reporte);

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_PDF);
        headers.setContentDispositionFormData("attachment", "Reporte_Indicadores_" + fechaDesde + ".pdf");

        return ResponseEntity.ok().headers(headers).body(pdfContent);
    }
}
