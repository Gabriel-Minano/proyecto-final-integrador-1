package com.sistema.botica.service;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.YearMonth;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.sistema.botica.DTO.DatoMensualDTO;
import com.sistema.botica.DTO.ReporteAnualDTO;
import com.sistema.botica.Repository.VentaRepository;
import com.sistema.botica.entity.Venta;

@Service
public class ReporteAnualService {
    private final VentaRepository ventaRepository;

    private static final String[] MESES = { "Enero", "Febrero", "Marzo", "Abril", "Mayo", "Junio",
            "Julio", "Agosto", "Septiembre", "Octubre", "Noviembre", "Diciembre" };

    public ReporteAnualService(VentaRepository ventaRepository) {
        this.ventaRepository = ventaRepository;
    }

    @Transactional(readOnly = true)
    public ReporteAnualDTO generarReporteAnual(Integer anio) {
        // Obtener todas las ventas del año
        LocalDateTime inicioAnio = LocalDate.of(anio, 1, 1).atStartOfDay();
        LocalDateTime finAnio = LocalDate.of(anio, 12, 31).atTime(23, 59, 59);

        List<Venta> ventasAnio = ventaRepository.findByFechaBetween(inicioAnio, finAnio);

        // Mapa para acumular ingresos por mes
        Map<Integer, List<BigDecimal>> ingresoPorMes = new HashMap<>();
        for (int i = 1; i <= 12; i++) {
            ingresoPorMes.put(i, new ArrayList<>());
        }

        // Acumular ingresos por mes
        for (Venta venta : ventasAnio) {
            int mes = venta.getFecha().getMonthValue();
            ingresoPorMes.get(mes).add(venta.getTotal());
        }

        // Calcular datos mensuales
        List<DatoMensualDTO> datosMonthly = new ArrayList<>();
        BigDecimal totalAnual = BigDecimal.ZERO;
        BigDecimal ventasMaximo = BigDecimal.ZERO;
        BigDecimal ventasMinimo = null;
        String mesMaximo = "";
        String mesMinimo = "";

        for (int i = 1; i <= 12; i++) {
            List<BigDecimal> ventasMes = ingresoPorMes.get(i);

            // Calcular ingresos totales del mes
            BigDecimal ingresosTotales = ventasMes.stream()
                    .reduce(BigDecimal.ZERO, BigDecimal::add);

            // Calcular promedio mensual (solo si hay ventas)
            BigDecimal promedioMensual = BigDecimal.ZERO;
            if (!ventasMes.isEmpty()) {
                promedioMensual = ingresosTotales.divide(
                        new BigDecimal(ventasMes.size()),
                        2,
                        RoundingMode.HALF_UP);
            }

            DatoMensualDTO datoMensual = new DatoMensualDTO(
                    MESES[i - 1],
                    i,
                    ingresosTotales,
                    promedioMensual);

            datosMonthly.add(datoMensual);

            // Acumular total anual
            totalAnual = totalAnual.add(ingresosTotales);

            // Identificar máximo y mínimo
            if (ingresosTotales.compareTo(ventasMaximo) > 0) {
                ventasMaximo = ingresosTotales;
                mesMaximo = MESES[i - 1];
            }

            if (ventasMinimo == null || ingresosTotales.compareTo(ventasMinimo) < 0) {
                ventasMinimo = ingresosTotales;
                mesMinimo = MESES[i - 1];
            }
        }

        // Calcular promedio anual (sobre los meses con datos)
        BigDecimal promedioAnual = BigDecimal.ZERO;
        long mesesConDatos = datosMonthly.stream()
                .filter(d -> d.getIngresosTotales().compareTo(BigDecimal.ZERO) > 0)
                .count();

        if (mesesConDatos > 0) {
            promedioAnual = totalAnual.divide(
                    new BigDecimal(mesesConDatos),
                    2,
                    RoundingMode.HALF_UP);
        }

        ReporteAnualDTO reporte = new ReporteAnualDTO();
        reporte.setDatosMonthly(datosMonthly);
        reporte.setPromedioAnual(promedioAnual);
        reporte.setTotalAnual(totalAnual);
        reporte.setMesMaximo(mesMaximo);
        reporte.setVentasMaximo(ventasMaximo);
        reporte.setMesMinimo(mesMinimo);
        reporte.setVentasMinimo(ventasMinimo != null ? ventasMinimo : BigDecimal.ZERO);
        reporte.setAnio(anio);

        return reporte;
    }
}
