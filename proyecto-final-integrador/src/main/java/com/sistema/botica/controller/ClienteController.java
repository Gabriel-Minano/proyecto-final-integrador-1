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

import com.sistema.botica.entity.Cliente;
import com.sistema.botica.service.ClienteService;

@Controller
@RequestMapping("/clientes")
public class ClienteController {
	@Autowired
	private ClienteService clienteService;

	/*
	 * @GetMapping public String listar(Model modelo) { modelo.addAttribute("lista",
	 * clienteService.listarActivos()); return "clientes"; }
	 */

	@GetMapping
	public String listar(@RequestParam(required = false) String palabraClave,
			@RequestParam(required = false, defaultValue = "activos") String estadoFiltro,
			@RequestParam(defaultValue = "0") int page, Model modelo) {

		Boolean estadoABuscar = null;
		if ("activos".equals(estadoFiltro)) {
			estadoABuscar = true;
		} else if ("inactivos".equals(estadoFiltro)) {
			estadoABuscar = false;
		}

		PageRequest pageRequest = PageRequest.of(page, 15, Sort.by("idCliente").descending());
		Page<Cliente> paginaClientes = clienteService.listarPaginadosYFiltrados(palabraClave, estadoABuscar,
				pageRequest);

		modelo.addAttribute("paginaClientes", paginaClientes);
		modelo.addAttribute("palabraClave", palabraClave);
		modelo.addAttribute("estadoFiltro", estadoFiltro);

		return "clientes";
	}

	@GetMapping("/nuevo")
	public String nuevo(Model modelo) {
		modelo.addAttribute("cliente", new Cliente());
		return "clientes_formulario";
	}

	@GetMapping("/editar/{id}")
	public String editar(@PathVariable("id") Integer id, Model modelo) {
		Cliente cliente = clienteService.buscarPorId(id);
		if (cliente == null) {
			return "redirect:/clientes";
		}
		modelo.addAttribute("cliente", cliente);
		return "clientes_formulario";
	}

	@PostMapping("/guardar")
	public String guardar(@Validated @ModelAttribute("cliente") Cliente cliente, BindingResult result) {
		if (result.hasErrors()) {
			return "clientes_formulario";
		}
		clienteService.guardar(cliente);
		return "redirect:/clientes";
	}

	@GetMapping("/eliminar-fisico/{id}")
	public String eliminarFisico(@PathVariable("id") Integer id) {
		Cliente cliente = clienteService.buscarPorId(id);
		if (cliente == null) {
			return "redirect:/clientes";
		}
		clienteService.eliminar(id);
		return "redirect:/clientes";
	}
	
	@GetMapping("/eliminar/{id}")
	public String eliminarLogico(@PathVariable("id") Integer id) {
		Cliente cliente = clienteService.buscarPorId(id);
		if (cliente == null) {
			return "redirect:/clientes";
		}
		clienteService.eliminarLogico(id);
		return "redirect:/clientes";
	}
	
	@GetMapping("/api/buscar")
	@ResponseBody
	public List<Map<String, Object>> buscarClientesAjax(@RequestParam("q") String palabraClave) {
		// Buscamos las coincidencias
		List<Cliente> clientes = clienteService.listarProductosClave(palabraClave);
		
		// Donde dice 20 se le puede cambiar para que busque más digamos 50 0 70 resultados
		return clientes.stream().limit(20).map(c -> {
			Map<String, Object> map = new HashMap<>();
			map.put("idCliente", c.getIdCliente());
			map.put("documento", c.getDocumento());
			map.put("nombreCompleto", c.getNombre() + " " + c.getApellido());
			return map;
		}).collect(Collectors.toList());
	}
}
