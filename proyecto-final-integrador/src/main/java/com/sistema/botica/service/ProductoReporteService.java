package com.sistema.botica.service;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.sistema.botica.DTO.ReporteProductosDTO;
import com.sistema.botica.Repository.DetalleVentaRepository;
import com.sistema.botica.Repository.ProductoRepository;
import com.sistema.botica.entity.DetalleVenta;

@Service
public class ProductoReporteService {
	@Autowired
	private ProductoRepository productoRepository;
	@Autowired
	private DetalleVentaRepository detalleVentaRepository;

	@Transactional(readOnly = true)
	public ReporteProductosDTO generarReporteProductos(LocalDateTime inicio, LocalDateTime fin, Pageable pageable) {
		ReporteProductosDTO dto = new ReporteProductosDTO();

		// indicadores del 1.2.2.1 al 1.2.2.4
		long totalActivosLong = productoRepository.countByEstadoTrue();
		int totalActivos = (int) totalActivosLong;
		dto.setTotalProductosActivos(totalActivos);

		if (totalActivos > 0) {
			// MySQL cuenta directamente, ahorrando muchísima memoria RAM
			long disponibles = productoRepository.countByStockActualGreaterThanAndEstadoTrue(0);
			long criticos = productoRepository.contarStockCritico();
			long agotados = productoRepository.countByStockActualEqualsAndEstadoTrue(0);
			long sobrestock = productoRepository.contarSobrestock();

			// Cálculo de porcentajes (*) x 100
			dto.setPorcentajeDisponibles((double) disponibles / totalActivos * 100);
			dto.setPorcentajeStockCritico((double) criticos / totalActivos * 100);
			dto.setPorcentajeAgotados((double) agotados / totalActivos * 100);
			dto.setPorcentajeSobrestock((double) sobrestock / totalActivos * 100);
		} else {
			// Prevención de división por cero
			dto.setPorcentajeDisponibles(0.0);
			dto.setPorcentajeStockCritico(0.0);
			dto.setPorcentajeAgotados(0.0);
			dto.setPorcentajeSobrestock(0.0);
		}

		// Indicadores nuevos
		List<DetalleVenta> movimientos = detalleVentaRepository.findByVentaFechaBetween(inicio, fin);
		Page<DetalleVenta> pagina = detalleVentaRepository.findByVentaFechaBetween(inicio, fin, pageable);
		dto.setPaginaMovimientos(pagina);

		if (movimientos.isEmpty()) {
			dto.setTotalUnidadesDespachadas(0);
			dto.setValorizacionTotal(BigDecimal.ZERO);
			dto.setTop10MasVendidos(new ArrayList<>());
			dto.setTop10MenosVendidos(new ArrayList<>());
			return dto;
		}

		// Suma total de unidades físicas que salieron
		int unidadesDespachadas = movimientos.stream().mapToInt(DetalleVenta::getCantidad).sum();
		dto.setTotalUnidadesDespachadas(unidadesDespachadas);

		// Valorización (Precio Unitario x Cantidad de cada línea de detalle)
		BigDecimal valorizacion = movimientos.stream()
				.map(dv -> dv.getProducto().getPrecioVenta().multiply(new BigDecimal(dv.getCantidad())))
				.reduce(BigDecimal.ZERO, BigDecimal::add);
		dto.setValorizacionTotal(valorizacion);

		// Agrupamos las ventas por "Nombre del Producto" y sumamos sus cantidades
		Map<String, Integer> ranking = movimientos.stream().collect(Collectors
				.groupingBy(dv -> dv.getProducto().getNombre(), Collectors.summingInt(DetalleVenta::getCantidad)));

		// Top 10 Más Vendidos (Ordenado de mayor a menor)
		List<Map.Entry<String, Integer>> topMas = ranking.entrySet().stream()
				.sorted(Map.Entry.<String, Integer>comparingByValue().reversed()).limit(10)
				.collect(Collectors.toList());
		dto.setTop10MasVendidos(topMas);

		// Top 10 Menos Vendidos (Ordenado de menor a mayor)
		List<Map.Entry<String, Integer>> topMenos = ranking.entrySet().stream().sorted(Map.Entry.comparingByValue())
				.limit(10).collect(Collectors.toList());
		dto.setTop10MenosVendidos(topMenos);

		return dto;
	}
}
