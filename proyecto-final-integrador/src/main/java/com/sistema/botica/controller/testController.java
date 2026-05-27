package com.sistema.botica.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import com.sistema.botica.DTO.VentaPorDiaDTO;
import com.sistema.botica.Repository.VentaRepository;

// controller para probar los datos que devuelve la consulta de ventas por dia, para luego mostrarlo en el dashboard
@Controller
public class testController {
    @Autowired
    private VentaRepository ventaRepo;

    @GetMapping("/test")
    @ResponseBody
    public List<VentaPorDiaDTO> test() {
        return ventaRepo.obtenerVentasPorDia(5, 2026);
    }
}
