package com.sistema.botica.Repository;

import java.time.LocalDateTime;
import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.sistema.botica.entity.DetalleVenta;

public interface DetalleVentaRepository extends JpaRepository<DetalleVenta, Long> {
	// Obtener la página para la tabla de la vista del reporte, ósea el filtrado por
	// fechas
	Page<DetalleVenta> findByVentaFechaBetween(LocalDateTime inicio, LocalDateTime fin, Pageable pageable);

	// La lista completa de todos los detalles, para hacer los cálculos de los
	// indicadores
	// y scar los 10 sup e inf
	List<DetalleVenta> findByVentaFechaBetween(LocalDateTime inicio, LocalDateTime fin);

	// Obtener el producto más vendido en un rango de fechas
	@Query("""
			SELECT dv.producto.nombre, SUM(dv.cantidad)
			FROM DetalleVenta dv
			WHERE dv.venta.fecha >= :inicio AND dv.venta.fecha <= :fin
			GROUP BY dv.producto.id, dv.producto.nombre
			ORDER BY SUM(dv.cantidad) DESC
			LIMIT 1
			""")
	List<Object[]> obtenerProductoMasVendido(@Param("inicio") LocalDateTime inicio, @Param("fin") LocalDateTime fin);

	// Obtener mes con más ventas (más unidades vendidas)
	@Query("""
			SELECT MONTH(dv.venta.fecha), YEAR(dv.venta.fecha), SUM(dv.cantidad)
			FROM DetalleVenta dv
			WHERE dv.venta.fecha >= :inicio AND dv.venta.fecha <= :fin
			GROUP BY MONTH(dv.venta.fecha), YEAR(dv.venta.fecha)
			ORDER BY SUM(dv.cantidad) DESC
			LIMIT 1
			""")
	List<Object[]> obtenerMesConMasVentas(@Param("inicio") LocalDateTime inicio, @Param("fin") LocalDateTime fin);
}
