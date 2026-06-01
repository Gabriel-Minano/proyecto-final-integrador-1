package com.sistema.botica.service;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.time.Month;
import java.time.format.TextStyle;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.sistema.botica.DTO.ReporteVentasDTO;
import com.sistema.botica.Repository.VentaRepository;
import com.sistema.botica.entity.DetalleVenta;
import com.sistema.botica.entity.Venta;

@Service
public class ReporteService {
	@Autowired
	private VentaRepository ventaRepository;

	@Transactional(readOnly = true) // Evitar error de lazy
	public ReporteVentasDTO generarReporteVentas(LocalDateTime inicio, LocalDateTime fin) {

		// 1. Obtenemos la data pura de la base de datos
		List<Venta> ventas = ventaRepository.findByFechaBetween(inicio, fin);
		ReporteVentasDTO dto = new ReporteVentasDTO();
		dto.setListaVentas(ventas);

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
		
		// 4. KPI: Mes con más ventas
        // Agrupa las ventas por Mes y suma los totales de cada mes
        Map<Month, BigDecimal> ventasPorMes = ventas.stream()
                .collect(Collectors.groupingBy(
                        v -> v.getFecha().getMonth(),
                        Collectors.reducing(BigDecimal.ZERO, Venta::getTotal, BigDecimal::add)
                ));

        String topMes = ventasPorMes.entrySet().stream().max(Map.Entry.comparingByValue())
        		.map(e -> e.getKey().getDisplayName(TextStyle.FULL, new Locale("es","ES")))
        		.orElse("-");
        dto.setMesMasVentas(topMes.toUpperCase());
        
		
		return dto;
	}

}
