package com.sistema.botica.controller;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
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
import org.springframework.web.bind.annotation.ResponseBody;

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

	// @GetMapping
	// public String listar(@RequestParam(name = "palabraClave", required = false)
	// String palabraClave, Model modelo) {
	// List<Producto> lista = productoService.listarProductosClave(palabraClave);
	// modelo.addAttribute("lista", lista);
	// modelo.addAttribute("palabraClave", palabraClave);
	// return "productos";
	// }
	/*
	 * @GetMapping public String listar(
	 * 
	 * @RequestParam(name = "palabraClave", required = false) String palabraClave,
	 * 
	 * @RequestParam(name = "filtro", required = false) String filtro,
	 * 
	 * Model modelo) {
	 * 
	 * List<Producto> lista;
	 * 
	 * // Aplicar filtros if (filtro != null && !filtro.isEmpty()) {
	 * 
	 * lista = productoService.filtrarProductos(filtro);
	 * 
	 * } else {
	 * 
	 * // Búsqueda normal lista =
	 * productoService.listarProductosClave(palabraClave); }
	 * 
	 * modelo.addAttribute("lista", lista);
	 * 
	 * modelo.addAttribute("palabraClave", palabraClave);
	 * 
	 * modelo.addAttribute("filtro", filtro);
	 * 
	 * return "productos"; }
	 */
	
	@GetMapping
	public String listar(
			@RequestParam(name = "palabraClave", required = false) String palabraClave,
			@RequestParam(name = "filtro", required = false) String filtro,
			@RequestParam(defaultValue = "0") int page,
			Model modelo) {

		// Paginación de 10 en 10, ordenando por ID descendente
		PageRequest pageRequest = PageRequest.of(page, 10, Sort.by("idProducto").descending());
		
		Page<Producto> paginaProductos = productoService.listarPaginadosYFiltrados(palabraClave, filtro, pageRequest);

		modelo.addAttribute("paginaProductos", paginaProductos);
		modelo.addAttribute("palabraClave", palabraClave);
		modelo.addAttribute("filtro", filtro);

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
	    System.out.println(producto.getFechaVencimiento());
		modelo.addAttribute("producto", producto);
		cargarListasParaFormulario(modelo);
		return "productos_formulario";
	}

	@PostMapping("/guardar")
	public String guardar(@Validated @ModelAttribute("producto") Producto producto, BindingResult result, Model model) {
		//Spring instancia los objetos, por eso usé !=null, porque el objeto existe en memoria, pero no tiene nada, todo es nulo dentro de este
		if (producto.getCategoria() != null && producto.getCategoria().getIdCategoria() == null) {
			result.rejectValue("categoria", "error.categoria", "Debe seleccionar una categoría válida");
		}
		if (producto.getProveedor() != null && producto.getProveedor().getIdProveedor() == null) {
			result.rejectValue("proveedor", "error.proveedor", "Debe seleccionar un proveedor válido");
		}
		if (result.hasErrors()) {
			cargarListasParaFormulario(model);
			return "productos_formulario";
		}
		productoService.guardar(producto);
		return "redirect:/productos";
	}

	@GetMapping("/eliminar-fisico/{id}")
	public String eliminarFisico(@PathVariable("id") Integer id) {
		Producto producto = productoService.buscarPorId(id);
		if (producto == null) {
			return "redirect:/productos";
		}
		productoService.eliminar(id);
		return "redirect:/productos";
	}

	@GetMapping("/eliminar/{id}")
	public String eliminarLogico(@PathVariable("id") Integer id) {
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
	
	@GetMapping("/api/buscar")
	@ResponseBody
	public List<Map<String, Object>> buscarProductosAjax(@RequestParam("q") String palabraClave) {
		
		List<Producto> productos = productoService.listarProductosClave(palabraClave);
		
		return productos.stream().limit(20).map(p -> {
			Map<String, Object> map = new HashMap<>();
			map.put("idProducto", p.getIdProducto());
			map.put("codigo", p.getCodigo());
			map.put("categoria", p.getCategoria() != null ? p.getCategoria().getNombre() : "");
			map.put("nombre", p.getNombre());
			map.put("stockActual", p.getStockActual());
			map.put("precioVenta", p.getPrecioVenta());
			return map;
		}).collect(Collectors.toList());
	}
}
