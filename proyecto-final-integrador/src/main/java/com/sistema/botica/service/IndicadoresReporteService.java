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

    IndicadoresReporteService(
            ProductoRepository productoRepository,
            DetalleVentaRepository detalleVentaRepository) {

        this.productoRepository = productoRepository;
        this.detalleVentaRepository = detalleVentaRepository;
    }

    @Transactional(readOnly = true)
    public ReporteIndicadoresDTO generarReporteIndicadores(
            LocalDateTime inicio,
            LocalDateTime fin) {

        ReporteIndicadoresDTO dto = new ReporteIndicadoresDTO();

        dto.setFechaInicio(inicio);
        dto.setFechaFin(fin);

        // =====================================================
        // INDICADORES DE INVENTARIO
        // =====================================================

        long totalActivosLong = productoRepository.countByEstadoTrue();
        int totalActivos = (int) totalActivosLong;

        dto.setTotalProductosActivos(totalActivos);

        if (totalActivos > 0) {

            long disponibles = productoRepository
                    .countByStockActualGreaterThanAndEstadoTrue(0);

            dto.setPorcentajeDisponibles(
                    disponibles * 100.0 / totalActivos);

            long agotados = productoRepository
                    .countByStockActualEqualsAndEstadoTrue(0);

            dto.setPorcentajeAgotados(
                    agotados * 100.0 / totalActivos);

            long criticos = productoRepository
                    .contarStockCritico();

            dto.setPorcentajeStockCritico(
                    criticos * 100.0 / totalActivos);

            long sobrestock = productoRepository
                    .contarSobrestock();

            dto.setPorcentajeSobrestock(
                    sobrestock * 100.0 / totalActivos);

        } else {

            dto.setPorcentajeDisponibles(0.0);
            dto.setPorcentajeAgotados(0.0);
            dto.setPorcentajeStockCritico(0.0);
            dto.setPorcentajeSobrestock(0.0);
        }

        // =====================================================
        // INDICADORES DE VENTAS
        // =====================================================

                List<Object[]> mesConMasVentas =
                detalleVentaRepository.obtenerMesConMasVentas(inicio, fin);

        if (!mesConMasVentas.isEmpty()) {

            Object[] result = mesConMasVentas.get(0);

            Integer mes = ((Number) result[0]).intValue();
            Integer anio = ((Number) result[1]).intValue();
            Long unidadesMes = ((Number) result[2]).longValue();

            YearMonth ym = YearMonth.of(anio, mes);

            String nombreMes =
                    ym.getMonth().getDisplayName(
                            TextStyle.FULL,
                            Locale.forLanguageTag("es-ES"));

            dto.setMesMasVentas(nombreMes + " " + anio);

            // ============================================
            // INDICADOR 5
            // % DE VENTAS DEL MES
            // ============================================

            Long totalAnual =
                    detalleVentaRepository.obtenerTotalUnidadesVendidasAnio(anio);

            if (totalAnual != null && totalAnual > 0) {

                dto.setPorcentajeVentasMes(
                        unidadesMes * 100.0 / totalAnual);

            } else {

                dto.setPorcentajeVentasMes(0.0);

            }

            // ============================================
            // PRODUCTO MÁS VENDIDO DEL MES
            // ============================================

            LocalDateTime inicioMes =
                    ym.atDay(1).atStartOfDay();

            LocalDateTime finMes =
                    ym.atEndOfMonth().atTime(23,59,59);

            List<Object[]> productoMasVendido =
                    detalleVentaRepository.obtenerProductoMasVendido(
                            inicioMes,
                            finMes);

            if (!productoMasVendido.isEmpty()) {

                Object[] prod = productoMasVendido.get(0);

                String nombreProducto = (String) prod[0];

                Integer cantidad =
                        ((Number) prod[1]).intValue();

                dto.setProductoMasVendido(nombreProducto);
                dto.setVentasProductoMasVendido(cantidad);

                // ========================================
                // INDICADOR 6
                // % PARTICIPACIÓN DEL PRODUCTO
                // ========================================

                Long totalMes =
                        detalleVentaRepository.obtenerTotalUnidadesVendidasMes(
                                anio,
                                mes);

                if (totalMes != null && totalMes > 0) {

                    dto.setPorcentajeParticipacionProducto(
                            cantidad * 100.0 / totalMes);

                } else {

                    dto.setPorcentajeParticipacionProducto(0.0);

                }

            } else {

                dto.setProductoMasVendido("N/A");
                dto.setVentasProductoMasVendido(0);
                dto.setPorcentajeParticipacionProducto(0.0);

            }

        } else {

            dto.setMesMasVentas("Sin ventas");
            dto.setProductoMasVendido("N/A");
            dto.setVentasProductoMasVendido(0);

            dto.setPorcentajeVentasMes(0.0);
            dto.setPorcentajeParticipacionProducto(0.0);

        }

        return dto;
    }
}
