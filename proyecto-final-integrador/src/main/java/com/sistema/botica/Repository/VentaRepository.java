package com.sistema.botica.Repository;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

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
    List<VentaPorDiaDTO> obtenerVentasPorDia(
            @Param("mes") Integer mes,
            @Param("anio") Integer anio);
}
