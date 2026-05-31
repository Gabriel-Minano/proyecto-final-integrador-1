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
	public String listar(@RequestParam(required = false) String palabraClave, Model modelo) {
		List<Cliente> lista = clienteService.listarProductosClave(palabraClave);
		modelo.addAttribute("lista", lista);
		modelo.addAttribute("palabraClave", palabraClave);
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

	@GetMapping("/eliminar/{id}")
	public String eliminar(@PathVariable("id") Integer id) {
		Cliente cliente = clienteService.buscarPorId(id);
		if (cliente == null) {
			return "redirect:/clientes";
		}
		clienteService.eliminarLogico(id);
		return "redirect:/clientes";
	}
}
