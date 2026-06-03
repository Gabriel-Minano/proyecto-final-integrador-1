package com.sistema.botica.Repository;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.sistema.botica.DTO.VentaPorDiaDTO;
import com.sistema.botica.entity.Venta;

@Repository
public interface VentaRepository extends JpaRepository<Venta, Integer> {
	@Query("SELECT COUNT(v) FROM Venta v WHERE v.fecha >= :inicio AND v.fecha <= :fin")
	long contarVentasPorPeriodo(@Param("inicio") LocalDateTime inicio, @Param("fin") LocalDateTime fin);

	@Query("SELECT SUM(v.total) FROM Venta v WHERE v.fecha >= :inicio AND v.fecha <= :fin")
	BigDecimal sumarIngresosPorPeriodo(@Param("inicio") LocalDateTime inicio, @Param("fin") LocalDateTime fin);

	@Query("SELECT COUNT(DISTINCT v.cliente) FROM Venta v WHERE v.fecha >= :inicio AND v.fecha <= :fin")
	long contarClientesPorPeriodo(@Param("inicio") LocalDateTime inicio, @Param("fin") LocalDateTime fin);

	// Esto se usará en el modulo de reportes de Ventas, para el filtro de fechas
	List<Venta> findByFechaBetween(LocalDateTime inicio, LocalDateTime fin);

	// Opción para la paginación, puesto que cargar todo ocasiona lentitud y es
	// antiestético
	Page<Venta> findByFechaBetween(LocalDateTime inicio, LocalDateTime fin, Pageable pageable);

	// consulta exclusiva apara el grafico de barras de ventas por dia
	@Query("""
			    SELECT new com.sistema.botica.DTO.VentaPorDiaDTO(
			        DAY(v.fecha),
			        SUM(v.total)
			    )
			    FROM Venta v
			    WHERE MONTH(v.fecha) = :mes
			    AND YEAR(v.fecha) = :anio
			    GROUP BY DAY(v.fecha)
			    ORDER BY DAY(v.fecha)
			""")
	List<VentaPorDiaDTO> obtenerVentasPorDia(@Param("mes") Integer mes, @Param("anio") Integer anio);

	@Query("SELECT v FROM Venta v WHERE v.fecha >= :inicio AND v.fecha <= :fin AND "
			+ "(:palabraClave IS NULL OR :palabraClave = '' OR "
			+ "LOWER(v.cliente.nombre) LIKE LOWER(CONCAT('%', :palabraClave, '%')) OR "
			+ "LOWER(v.cliente.apellido) LIKE LOWER(CONCAT('%', :palabraClave, '%')))")
	Page<Venta> buscarVentasPaginadasYFiltradas(@Param("inicio") LocalDateTime inicio, @Param("fin") LocalDateTime fin,
			@Param("palabraClave") String palabraClave, Pageable pageable);
}
