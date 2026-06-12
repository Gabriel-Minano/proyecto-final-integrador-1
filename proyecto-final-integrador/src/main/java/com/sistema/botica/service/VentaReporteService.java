package com.sistema.botica.service;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.time.YearMonth;
import java.time.format.TextStyle;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.stream.Collectors;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.sistema.botica.DTO.ReporteVentasDTO;
import com.sistema.botica.Repository.VentaRepository;
import com.sistema.botica.entity.DetalleVenta;
import com.sistema.botica.entity.Venta;

@Service
public class VentaReporteService {
	private final VentaRepository ventaRepository;

	VentaReporteService(VentaRepository ventaRepository) {
		this.ventaRepository = ventaRepository;
	}

	@Transactional(readOnly = true) // Evitar error de lazy
	public ReporteVentasDTO generarReporteVentas(LocalDateTime inicio, LocalDateTime fin, Pageable pageable) {

		// 1. Obtenemos paginas para mandar a la vista y no sobrecargar la app
		Page<Venta> pagina = ventaRepository.findByFechaBetween(inicio, fin, pageable);
		ReporteVentasDTO dto = new ReporteVentasDTO();
		dto.setPaginaVentas(pagina);

		// 1.1. Obtenemos una lista para los cálculos, puesto que es muy dificil hacerlo
		// con
		// paginación, por su fragmentación
		List<Venta> ventas = ventaRepository.findByFechaBetween(inicio, fin);
		// En caso no existan dichas ventas dentro de ese rango devolvemos vacio
		if (ventas.isEmpty()) {
			dto.setTotalVentas(BigDecimal.ZERO);
			dto.setProductosVendidos(0);
			dto.setProductoMasVendido("-");
			dto.setMesMasVentas("-");
			return dto;
		}

		// 2. Obtener el total de ventas, es decir, la sumatoria de todos los totales en
		// ese mes
		BigDecimal totalSoles = ventas.stream().map(Venta::getTotal).reduce(BigDecimal.ZERO, BigDecimal::add);
		dto.setTotalVentas(totalSoles);

		// 3. Obtener productos vendidos y el más vendido
		Map<String, Integer> rankingProductos = new HashMap<>();
		int totalUnidades = 0;

		for (Venta v : ventas) {
			for (DetalleVenta dv : v.getListaDetallesVenta()) {
				totalUnidades += dv.getCantidad();
				// Acumula la cantidad vendida por el nombre del producto
				rankingProductos.put(dv.getProducto().getNombre(),
						rankingProductos.getOrDefault(dv.getProducto().getNombre(), 0) + dv.getCantidad());
			}
		}
		dto.setProductosVendidos(totalUnidades);

		// Encuentra el producto con el valor más alto en el mapa
		String topProducto = rankingProductos.entrySet().stream().max(Map.Entry.comparingByValue())
				.map(Map.Entry::getKey).orElse("-");
		dto.setProductoMasVendido(topProducto);

		// 4. Mes con más ventas
		// Agrupa las ventas por Mes y suma los totales de cada mes
		Map<YearMonth, BigDecimal> ventasPorMes = ventas.stream()
				.collect(Collectors.groupingBy(v -> YearMonth.from(v.getFecha()), // para obtener año y mes
						Collectors.reducing(BigDecimal.ZERO, Venta::getTotal, BigDecimal::add)));

		String topMes = ventasPorMes.entrySet().stream().max(Map.Entry.comparingByValue()).map(e -> {
			// Obtener el mes junto al año que tuvo más ventas
			String nombreMes = e.getKey().getMonth().getDisplayName(TextStyle.FULL, Locale.forLanguageTag("es-ES"));
			int anio = e.getKey().getYear();
			return nombreMes + " " + anio; // ejemplo marzo 2026
		}).orElse("-");

		dto.setMesMasVentas(topMes.toUpperCase());

		return dto;
	}

}
