package com.sistema.botica.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import com.sistema.botica.entity.Proveedor;
import com.sistema.botica.service.ProveedorService;

@Controller
@RequestMapping("/proveedores")
public class ProveedorController {
	@Autowired
	private ProveedorService proveedorService;

	@GetMapping
	public String listar(Model modelo) {
		modelo.addAttribute("lista", proveedorService.listarActivos());
		return "proveedores";
	}

	@GetMapping("/nuevo")
	public String mostrarFormularioNuevo(Model modelo) {
		modelo.addAttribute("proveedor", new Proveedor());
		return "proveedores_formulario";
	}

	@GetMapping("/editar/{id}")
	public String mostrarFormularioEditar(@PathVariable Integer id, Model modelo) {
		Proveedor proveedor = proveedorService.buscarPorId(id);
		if (proveedor == null) {
			return "redirect:/proveedores";
		}
		modelo.addAttribute("proveedor", proveedor);
		return "proveedores_formulario";
	}

	@GetMapping("/eliminar/{id}")
	public String eliminar(@PathVariable("id") Integer id) {
		Proveedor proveedor = proveedorService.buscarPorId(id);
		if(proveedor == null) {
			return "redirect:/productos";
		}
		proveedorService.eliminarLogico(id);
		return "redirect:/proveedores";
	}

	@PostMapping("/guardar")
	public String guardar(@Validated @ModelAttribute("proveedor") Proveedor proveedor, BindingResult result,
			Model modelo) {
		if (result.hasErrors()) {
			return "proveedores_formulario";
		}
		proveedorService.guardar(proveedor);
		return "redirect:/proveedores";
	}
}
