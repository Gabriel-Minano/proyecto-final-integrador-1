package com.sistema.botica.controller;

import java.util.List;

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
import org.springframework.web.bind.annotation.RequestParam;

import com.sistema.botica.entity.Proveedor;
import com.sistema.botica.service.ProveedorService;

@Controller
@RequestMapping("/proveedores")
public class ProveedorController {
	@Autowired
	private ProveedorService proveedorService;

	@GetMapping
	public String listar(@RequestParam(required = false) String palabraClave, Model modelo) {
		List<Proveedor> lista = proveedorService.listarProductosClave(palabraClave);
		modelo.addAttribute("lista", lista);
		modelo.addAttribute("palabraClave", palabraClave);
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

	@GetMapping("/eliminar-fisico/{id}")
	public String eliminarFisico(@PathVariable("id") Integer id) {
		Proveedor proveedor = proveedorService.buscarPorId(id);
		if(proveedor == null) {
			return "redirect:/productos";
		}
		proveedorService.eliminar(id);
		return "redirect:/proveedores";
	}

	@GetMapping("/eliminar/{id}")
	public String eliminarLogico(@PathVariable("id") Integer id) {
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
