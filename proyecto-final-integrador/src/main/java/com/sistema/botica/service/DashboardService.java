package com.sistema.botica.service;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.YearMonth;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.stereotype.Service;

import com.sistema.botica.DTO.VentaPorDiaDTO;
import com.sistema.botica.Repository.ProductoRepository;
import com.sistema.botica.Repository.VentaRepository;
import com.sistema.botica.entity.Producto;

@Service
public class DashboardService {
    private final ProductoRepository productoRepo;
    private final VentaRepository ventaRepo;

    DashboardService(ProductoRepository productoRepo, VentaRepository ventaRepo) {
        this.productoRepo = productoRepo;
        this.ventaRepo = ventaRepo;
    }

    public Map<String, Object> calcularIndicadores(Integer mes, Integer anio) {
        Map<String, Object> indicadores = new HashMap<>();

        // 1. CÁLCULOS DE VENTAS (Afectados por el filtro de fecha)
        YearMonth anioMes = YearMonth.of(anio, mes);
        LocalDateTime inicioMes = anioMes.atDay(1).atStartOfDay();
        LocalDateTime finMes = anioMes.atEndOfMonth().atTime(LocalTime.MAX);

        long totalVentas = ventaRepo.contarVentasPorPeriodo(inicioMes, finMes);
        BigDecimal ingresos = ventaRepo.sumarIngresosPorPeriodo(inicioMes, finMes);
        long totalClientes = ventaRepo.contarClientesPorPeriodo(inicioMes, finMes);

        indicadores.put("ventasMes", totalVentas);
        indicadores.put("ingresosMes", ingresos != null ? ingresos : BigDecimal.ZERO);
        indicadores.put("clientesMes", totalClientes);

        // 2. CÁLCULOS DE INVENTARIO (Estado Actual en tiempo real)
        long criticos = productoRepo.contarStockCritico();
        indicadores.put("cantCriticos", criticos);

        // 3. LISTAS PARA ALERTAS
        List<Producto> productosAgotados = productoRepo.findByEstadoTrueAndStockActualEquals(0);
        List<Producto> productosVencidos = productoRepo.findByEstadoTrueAndFechaVencimientoBefore(LocalDate.now());

        indicadores.put("alertasAgotados", productosAgotados);
        indicadores.put("alertasVencidos", productosVencidos);

        return indicadores;
    }

    public List<VentaPorDiaDTO> obtenerVentasPorDia(Integer mes, Integer anio) {
        return ventaRepo.obtenerVentasPorDia(mes, anio);
    }
}
