package com.sistema.botica.controller;

import java.util.List;

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

import com.sistema.botica.entity.Categoria;
import com.sistema.botica.service.CategoriaService;

@Controller
@RequestMapping("/categorias")
public class CategoriaController {

	private final CategoriaService categoriaService;

	CategoriaController(CategoriaService categoriaService) {
		this.categoriaService = categoriaService;
	}

	@GetMapping
	public String listar(@RequestParam(name = "palabraClave", required = false) String palabraClave, Model modelo) {
		List<Categoria> lista = categoriaService.listarProductosClave(palabraClave);
		modelo.addAttribute("lista", lista);
		modelo.addAttribute("palabraClave", palabraClave);
		return "categorias";
	}

	@GetMapping("/nuevo")
	public String mostrarFormularioNuevo(Model modelo) {
		modelo.addAttribute("categoria", new Categoria());
		return "categorias_formulario";
	}

	@GetMapping("/editar/{id}")
	public String mostrarFormularioEditar(@PathVariable Integer id, Model modelo) {
		Categoria categoria = categoriaService.buscarPorId(id);
		if (categoria == null) {
			return "redirect:/categorias";
		}
		modelo.addAttribute("categoria", categoria);
		return "categorias_formulario";
	}

	@GetMapping("/eliminar-fisico/{id}")
	public String eliminaFisico(@PathVariable("id") Integer id) {
		Categoria categoria = categoriaService.buscarPorId(id);
		if (categoria == null) {
			return "redirect:/categorias";
		}
		categoriaService.eliminar(id);
		return "redirect:/categorias";
	}

	@GetMapping("/eliminar/{id}")
	public String eliminarLogico(@PathVariable("id") Integer id) {
		Categoria categoria = categoriaService.buscarPorId(id);
		if (categoria == null) {
			return "redirect:/categorias";
		}
		categoriaService.eliminarLogico(id);
		return "redirect:/categorias";
	}

	@PostMapping("/guardar")
	public String guardar(@Validated @ModelAttribute("categoria") Categoria categoria, BindingResult result,
			Model modelo) {
		if (result.hasErrors()) {
			return "categorias_formulario";
		}
		categoriaService.guardar(categoria);
		return "redirect:/categorias";
	}

	@PostMapping("/editarCategoria")
	public String editar(@Validated @ModelAttribute("categoria") Categoria categoria, BindingResult result,
			Model modelo) {
		if (result.hasErrors()) {
			return "categorias_formulario";
		}
		categoriaService.editar(categoria);
		return "redirect:/categorias";
	}

}
