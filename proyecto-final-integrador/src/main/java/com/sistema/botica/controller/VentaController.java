package com.sistema.botica.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.sistema.botica.entity.Venta;
import com.sistema.botica.service.ClienteService;
import com.sistema.botica.service.ProductoService;
import com.sistema.botica.service.UsuarioService;
import com.sistema.botica.service.VentaService;

@Controller
@RequestMapping("/ventas")
public class VentaController {

	@Autowired
	private VentaService ventaService;
	
	@Autowired
    private ClienteService clienteService;

    @Autowired
    private UsuarioService usuarioService;

    @Autowired
    private ProductoService productoService;
    
    @GetMapping
    public String listar(Model modelo) {
        modelo.addAttribute("listaVentas", ventaService.listarTodas());
        return "ventas";
    }
    
    @GetMapping("/nuevo")
    public String mostrarFormularioVenta(Model modelo) {
        Venta venta = new Venta();

        modelo.addAttribute("venta", venta);
        modelo.addAttribute("listaClientes", clienteService.listarActivos());
        modelo.addAttribute("listaUsuarios", usuarioService.listarActivos());
        modelo.addAttribute("listaProductos", productoService.listarActivos());
        return "ventas_formulario";
    }
    
    @PostMapping("/guardar")
    public String guardarVenta(@ModelAttribute("venta") Venta venta, RedirectAttributes flash) {
        try {
            ventaService.registrarVenta(venta);
            flash.addFlashAttribute("success", "¡Venta registrada con éxito!");
        } catch (RuntimeException e) {
            flash.addFlashAttribute("error", e.getMessage());
            return "redirect:/ventas/nuevo";
        }
        return "redirect:/ventas";
    }
}
