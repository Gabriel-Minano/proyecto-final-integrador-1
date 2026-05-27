package com.sistema.botica.controller;

import java.time.LocalDate;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

import com.sistema.botica.DTO.VentaPorDiaDTO;
import com.sistema.botica.service.DashboardService;

@Controller
public class DashboardController {

    @Autowired
    private DashboardService dashboardService;

    @GetMapping({ "/dashboard" })
    public String verDashboard(
            @RequestParam(name = "mes", required = false) Integer mes,
            @RequestParam(name = "anio", required = false) Integer anio,
            Model model) {

        // Si no hay filtro, por defecto usamos el mes y año actual
        LocalDate hoy = LocalDate.now();
        if (mes == null)
            mes = hoy.getMonthValue();
        if (anio == null)
            anio = hoy.getYear();
        model.addAttribute("indicadores", dashboardService.calcularIndicadores(mes, anio));

        List<VentaPorDiaDTO> ventasPorDia = dashboardService.obtenerVentasPorDia(mes,
                anio);
        model.addAttribute("ventasPorDia", ventasPorDia);

        model.addAttribute("ventasPorDia",
                dashboardService.obtenerVentasPorDia(mes, anio));
        model.addAttribute("mesSeleccionado", mes);
        model.addAttribute("anioSeleccionado", anio);

        return "dashboard";
    }
}
