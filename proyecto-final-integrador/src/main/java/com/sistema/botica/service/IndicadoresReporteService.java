package com.sistema.botica.service;

import java.time.LocalDateTime;
import java.time.YearMonth;
import java.time.format.TextStyle;
import java.util.List;
import java.util.Locale;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.sistema.botica.DTO.ReporteIndicadoresDTO;
import com.sistema.botica.Repository.DetalleVentaRepository;
import com.sistema.botica.Repository.ProductoRepository;

@Service
public class IndicadoresReporteService {
    private final ProductoRepository productoRepository;
    private final DetalleVentaRepository detalleVentaRepository;

    IndicadoresReporteService(ProductoRepository productoRepository, DetalleVentaRepository detalleVentaRepository) {
        this.productoRepository = productoRepository;
        this.detalleVentaRepository = detalleVentaRepository;
    }

    @Transactional(readOnly = true)
    public ReporteIndicadoresDTO generarReporteIndicadores(LocalDateTime inicio, LocalDateTime fin) {
        ReporteIndicadoresDTO dto = new ReporteIndicadoresDTO();

        // Establecer fechas
        dto.setFechaInicio(inicio);
        dto.setFechaFin(fin);

        // 1. Total de productos activos
        long totalActivosLong = productoRepository.countByEstadoTrue();
        int totalActivos = (int) totalActivosLong;
        dto.setTotalProductosActivos(totalActivos);

        if (totalActivos > 0) {
            // 2. Porcentaje de Productos Disponibles (stock > 0)
            long disponibles = productoRepository.countByStockActualGreaterThanAndEstadoTrue(0);
            dto.setPorcentajeDisponibles((double) disponibles / totalActivos * 100);

            // 3. Porcentaje de Productos Agotados (stock = 0)
            long agotados = productoRepository.countByStockActualEqualsAndEstadoTrue(0);
            dto.setPorcentajeAgotados((double) agotados / totalActivos * 100);

            // 4. Porcentaje de Stock Crítico (stock <= stock_minimo)
            long criticos = productoRepository.contarStockCritico();
            dto.setPorcentajeStockCritico((double) criticos / totalActivos * 100);

            // 5. Total de Productos con Sobrestock (stock > stock_maximo)
            long sobrestock = productoRepository.contarSobrestock();
            dto.setTotalSobrestock((int) sobrestock);
        } else {
            // Prevención de división por cero
            dto.setPorcentajeDisponibles(0.0);
            dto.setPorcentajeAgotados(0.0);
            dto.setPorcentajeStockCritico(0.0);
            dto.setTotalSobrestock(0);
        }

        // 6. Mes con más ventas
        List<Object[]> mesConMasVentas = detalleVentaRepository.obtenerMesConMasVentas(inicio, fin);
        if (!mesConMasVentas.isEmpty()) {
            Object[] result = mesConMasVentas.get(0);
            Integer mes = ((Number) result[0]).intValue();
            Integer anio = ((Number) result[1]).intValue();

            // Formato: "Enero 2026"
            YearMonth ym = YearMonth.of(anio, mes);
            String mesMasVentasStr = ym.getMonth().getDisplayName(TextStyle.FULL, Locale.forLanguageTag("es-ES"))
                    + " " + anio;
            dto.setMesMasVentas(mesMasVentasStr);

            // 7. Obtener producto más vendido en ese mes
            LocalDateTime inicioMes = ym.atDay(1).atStartOfDay();
            LocalDateTime finMes = ym.atEndOfMonth().atTime(23, 59, 59);

            List<Object[]> productoMasVendido = detalleVentaRepository.obtenerProductoMasVendido(inicioMes, finMes);
            if (!productoMasVendido.isEmpty()) {
                Object[] prodResult = productoMasVendido.get(0);
                String nombreProducto = (String) prodResult[0];
                Integer cantidadVendida = ((Number) prodResult[1]).intValue();

                dto.setProductoMasVendido(nombreProducto);
                dto.setVentasProductoMasVendido(cantidadVendida);
            } else {
                dto.setProductoMasVendido("N/A");
                dto.setVentasProductoMasVendido(0);
            }
        } else {
            dto.setMesMasVentas("Sin ventas en el período");
            dto.setProductoMasVendido("N/A");
            dto.setVentasProductoMasVendido(0);
        }

        return dto;
    }
}
