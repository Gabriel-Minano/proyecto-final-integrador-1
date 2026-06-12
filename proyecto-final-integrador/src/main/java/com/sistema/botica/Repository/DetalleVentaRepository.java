package com.sistema.botica.Repository;

import java.time.LocalDateTime;
import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import com.sistema.botica.entity.DetalleVenta;

public interface DetalleVentaRepository extends JpaRepository<DetalleVenta, Long> {
	// Obtener la página para la tabla de la vista del reporte, ósea el filtrado por
	// fechas
	Page<DetalleVenta> findByVentaFechaBetween(LocalDateTime inicio, LocalDateTime fin, Pageable pageable);

	// La lista completa de todos los detalles, para hacer los cálculos de los
	// indicadores
	// y scar los 10 sup e inf
	List<DetalleVenta> findByVentaFechaBetween(LocalDateTime inicio, LocalDateTime fin);
}
