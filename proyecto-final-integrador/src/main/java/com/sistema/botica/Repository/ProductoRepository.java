package com.sistema.botica.Repository;

import java.time.LocalDate;
import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.sistema.botica.entity.Producto;

@Repository
public interface ProductoRepository extends JpaRepository<Producto, Integer> {

    // Esto es un Query, Encontrar solo productos con estado activo = true
    // Utiliza una sintaxis sencilla.
    List<Producto> findByEstadoTrue();

    // Busqueda por coincidencia
    // Query explícito, para consultas complejas
    @Query("SELECT p FROM Producto p WHERE p.estado = true AND p.nombre LIKE %:palabraClave%")
    List<Producto> buscarPorCoincidencia(@Param("palabraClave") String palabraClave);

    // 1. Total de productos activos registrados en sistema (Denominador para todas
    // las fórmulas)
    long countByEstadoTrue();

    // 2. Porcentaje de Productos Disponibles (stock > 0) [cite: 8]
    long countByStockActualGreaterThanAndEstadoTrue(Integer stock); // Se pasará 0 como parámetro

    // 3. Porcentaje de Productos Agotados (stock = 0) [cite: 26]
    long countByStockActualEqualsAndEstadoTrue(Integer stock); // Se pasará 0 como parámetro

    // 4. Productos con Stock Crítico (stock <= stock_minimo) [cite: 17]
    @Query("SELECT COUNT(p) FROM Producto p WHERE p.estado = true AND p.stockActual <= p.stockMinimo")
    long contarStockCritico();

    // 5. Productos con Sobrestock (stock > stock_maximo)
    @Query("SELECT COUNT(p) FROM Producto p WHERE p.estado = true AND p.stockActual > p.stockMaximo")
    long contarSobrestock();

    // Para la lista de alertas: Nombres de productos agotados
    List<Producto> findByEstadoTrueAndStockActualEquals(Integer stock);

    // Para la lista de alertas: Nombres de productos vencidos (fecha anterior a
    // hoy)
    List<Producto> findByEstadoTrueAndFechaVencimientoBefore(LocalDate fechaActual);

    // para los filtros de los botones de producto
    // Productos eliminados
    List<Producto> findByEstadoFalse();

    // Productos con stock
    List<Producto> findByEstadoTrueAndStockActualGreaterThan(Integer stock);

    // Productos sin stock
    List<Producto> findByEstadoTrueAndStockActual(Integer stock);

    // Productos próximos a vencer (30 días)
    @Query("""
            SELECT p FROM Producto p
            WHERE p.estado = true
            AND p.fechaVencimiento BETWEEN :hoy AND :fechaLimite
            """)
    List<Producto> listarProductosProximosAVencer(
            @Param("hoy") LocalDate hoy,
            @Param("fechaLimite") LocalDate fechaLimite);

}