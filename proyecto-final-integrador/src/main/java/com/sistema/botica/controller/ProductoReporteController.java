package com.sistema.botica.controller;

import java.io.IOException;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.temporal.TemporalAdjusters;

import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import com.sistema.botica.DTO.ReporteProductosDTO;
import com.sistema.botica.service.ProductoReporteExportService;
import com.sistema.botica.service.ProductoReporteService;

@Controller
@RequestMapping("/reportes/productos")
public class ProductoReporteController {
	private final ProductoReporteService productoReporteService;
	private final ProductoReporteExportService exportService;

	ProductoReporteController(ProductoReporteService productoReporteService, ProductoReporteExportService exportService) {
		this.productoReporteService = productoReporteService;
		this.exportService = exportService;
	} // NUEVO SERVICIO

	@GetMapping
	public String verReporteProductos(
			@RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate fechaDesde,
			@RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate fechaHasta,
			@RequestParam(defaultValue = "0") int page, Model model) {
		// 1. Manejo de fechas por defecto (Mes actual)
		if (fechaDesde == null) {
			fechaDesde = LocalDate.now().withDayOfMonth(1); // Primer día del mes actual
		}
		if (fechaHasta == null) {
			fechaHasta = LocalDate.now().with(TemporalAdjusters.lastDayOfMonth()); // Último día del mes actual
		}

		LocalDateTime inicio = fechaDesde.atStartOfDay();
		LocalDateTime fin = fechaHasta.atTime(LocalTime.MAX);

		// 2. Configuración de la Paginación (10 registros por página)
		// Se usó venta.fecha por su relación integrada con DetalleVenta en el campo
		// Venta
		Pageable pageRequest = PageRequest.of(page, 10, Sort.by("venta.fecha").descending());

		// 3. Generación del reporte llamando al servicio
		ReporteProductosDTO reporte = productoReporteService.generarReporteProductos(inicio, fin, pageRequest);

		// 4. Inyección de datos a la vista
		model.addAttribute("reporte", reporte);
		model.addAttribute("fechaDesde", fechaDesde);
		model.addAttribute("fechaHasta", fechaHasta);

		return "reportes_productos";
	}

	@GetMapping("/exportar/pdf")
	public ResponseEntity<byte[]> exportarPdf(
			@RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate fechaDesde,
			@RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate fechaHasta) throws IOException {

		LocalDateTime inicio = fechaDesde.atStartOfDay();
		LocalDateTime fin = fechaHasta.atTime(LocalTime.MAX);

		// Obtenemos los datos (Para la exportación usamos un tamaño de página muy
		// grande para que traiga todos los registros de ese periodo)
		Pageable pageRequest = PageRequest.of(0, Integer.MAX_VALUE, Sort.by("venta.fecha").descending());
		ReporteProductosDTO reporte = productoReporteService.generarReporteProductos(inicio, fin, pageRequest);

		byte[] pdfContent = exportService.generarPdf(reporte, inicio, fin);

		HttpHeaders headers = new HttpHeaders();
		headers.setContentType(MediaType.APPLICATION_PDF);
		headers.setContentDispositionFormData("attachment", "Reporte_Movimientos_" + fechaDesde + ".pdf");

		return ResponseEntity.ok().headers(headers).body(pdfContent);
	}

	@GetMapping("/exportar/excel")
	public ResponseEntity<byte[]> exportarExcel(
			@RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate fechaDesde,
			@RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate fechaHasta) throws IOException {

		LocalDateTime inicio = fechaDesde.atStartOfDay();
		LocalDateTime fin = fechaHasta.atTime(LocalTime.MAX);

		// Obtenemos los datos completos del periodo
		Pageable pageRequest = PageRequest.of(0, Integer.MAX_VALUE, Sort.by("venta.fecha").descending());
		ReporteProductosDTO reporte = productoReporteService.generarReporteProductos(inicio, fin, pageRequest);

		byte[] excelContent = exportService.generarExcel(reporte, inicio, fin);

		HttpHeaders headers = new HttpHeaders();
		headers.setContentType(
				MediaType.parseMediaType("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet"));
		headers.setContentDispositionFormData("attachment", "Reporte_Movimientos_" + fechaDesde + ".xlsx");

		return ResponseEntity.ok().headers(headers).body(excelContent);
	}
}
