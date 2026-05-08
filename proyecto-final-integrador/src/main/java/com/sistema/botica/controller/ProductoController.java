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

import com.sistema.botica.entity.Producto;
import com.sistema.botica.service.CategoriaService;
import com.sistema.botica.service.ProductoService;
import com.sistema.botica.service.ProveedorService;

@Controller
@RequestMapping("/productos")
public class ProductoController {
	@Autowired
	private ProductoService productoService;
	@Autowired
	private CategoriaService categoriaService;
	@Autowired
	private ProveedorService proveedorService;

	@GetMapping
	public String listar(@RequestParam(name = "palabraClave", required = false) String palabraClave, Model modelo) {
		List<Producto> lista = productoService.listarProductosClave(palabraClave);
		modelo.addAttribute("lista", lista);
		modelo.addAttribute("palabraClave", palabraClave);
		return "productos";
	}

	@GetMapping("/nuevo")
	public String mostrarFormularioNuevo(Model modelo) {
		modelo.addAttribute("producto", new Producto());
		cargarListasParaFormulario(modelo);
		return "productos_formulario";
	}

	@GetMapping("/editar/{id}")
	public String mostrarFormularioEditar(@PathVariable("id") Integer id, Model modelo) {
		Producto producto = productoService.buscarPorId(id);
		if (producto == null) {
			return "redirect:/productos";
		}
		modelo.addAttribute("producto", producto);
		cargarListasParaFormulario(modelo);
		return "productos_formulario";
	}

	@PostMapping("/guardar")
	public String guardar(@Validated @ModelAttribute("producto") Producto producto, BindingResult result, Model model) {
		if (result.hasErrors()) {
			cargarListasParaFormulario(model);
			return "productos_formulario";
		}
		productoService.guardar(producto);
		return "redirect:/productos";
	}

	@GetMapping("/eliminar/{id}")
	public String eliminar(@PathVariable("id") Integer id) {
		Producto producto = productoService.buscarPorId(id);
		if (producto == null) {
			return "redirect:/productos";
		}
		productoService.eliminarLogico(id);
		return "redirect:/productos";
	}

	// Método privado para cargar los modelos de categoria y proveedor (Listas)
	private void cargarListasParaFormulario(Model modelo) {
		modelo.addAttribute("listaCategorias", categoriaService.listarTodas());
		modelo.addAttribute("listaProveedores", proveedorService.listarTodos());
	}
}
