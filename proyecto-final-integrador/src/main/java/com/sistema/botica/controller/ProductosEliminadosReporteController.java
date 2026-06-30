package com.sistema.botica.controller;

import java.io.IOException;

import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import com.sistema.botica.DTO.ReporteProductosEliminadosDTO;
import com.sistema.botica.service.ProductosEliminadosReporteExportService;
import com.sistema.botica.service.ProductosEliminadosReporteService;

@Controller
@RequestMapping("/reportes/productos-eliminados")
public class ProductosEliminadosReporteController {
    private final ProductosEliminadosReporteService productoReporteService;
    private final ProductosEliminadosReporteExportService exportService;

    ProductosEliminadosReporteController(ProductosEliminadosReporteService productoReporteService,
            ProductosEliminadosReporteExportService exportService) {
        this.productoReporteService = productoReporteService;
        this.exportService = exportService;
    }

    @GetMapping
    public String verReporteProductosEliminados(@RequestParam(defaultValue = "0") int page, Model model) {
        Pageable pageRequest = PageRequest.of(page, 10, Sort.by("idProducto").descending());

        ReporteProductosEliminadosDTO reporte = productoReporteService.generarReporteProductosEliminados(pageRequest);

        model.addAttribute("reporte", reporte);
        model.addAttribute("currentPage", page);

        return "reportes_productos_eliminados";
    }

    @GetMapping("/exportar/pdf")
    public ResponseEntity<byte[]> exportarPdf() throws IOException {
        Pageable pageRequest = PageRequest.of(0, Integer.MAX_VALUE, Sort.by("idProducto").descending());
        ReporteProductosEliminadosDTO reporte = productoReporteService.generarReporteProductosEliminados(pageRequest);

        byte[] pdfContent = exportService.generarPdf(reporte);

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_PDF);
        headers.setContentDispositionFormData("attachment", "Reporte_Productos_Eliminados.pdf");

        return ResponseEntity.ok().headers(headers).body(pdfContent);
    }

    @GetMapping("/exportar/excel")
    public ResponseEntity<byte[]> exportarExcel() throws IOException {
        Pageable pageRequest = PageRequest.of(0, Integer.MAX_VALUE, Sort.by("idProducto").descending());
        ReporteProductosEliminadosDTO reporte = productoReporteService.generarReporteProductosEliminados(pageRequest);

        byte[] excelContent = exportService.generarExcel(reporte);

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(
                MediaType.parseMediaType("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet"));
        headers.setContentDispositionFormData("attachment", "Reporte_Productos_Eliminados.xlsx");

        return ResponseEntity.ok().headers(headers).body(excelContent);
    }
}
