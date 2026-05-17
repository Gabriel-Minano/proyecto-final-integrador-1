package com.sistema.botica.Repository;

import java.math.BigDecimal;
import java.time.LocalDateTime;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.sistema.botica.entity.Venta;

@Repository
public interface VentaRepository extends JpaRepository<Venta, Integer>{
	@Query("SELECT COUNT(v) FROM Venta v WHERE v.fecha >= :inicio AND v.fecha <= :fin")
    long contarVentasPorPeriodo(@Param("inicio") LocalDateTime inicio, @Param("fin") LocalDateTime fin);

    @Query("SELECT SUM(v.total) FROM Venta v WHERE v.fecha >= :inicio AND v.fecha <= :fin")
    BigDecimal sumarIngresosPorPeriodo(@Param("inicio") LocalDateTime inicio, @Param("fin") LocalDateTime fin);

    @Query("SELECT COUNT(DISTINCT v.cliente) FROM Venta v WHERE v.fecha >= :inicio AND v.fecha <= :fin")
    long contarClientesPorPeriodo(@Param("inicio") LocalDateTime inicio, @Param("fin") LocalDateTime fin);
}
