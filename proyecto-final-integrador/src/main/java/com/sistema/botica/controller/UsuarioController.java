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

import com.sistema.botica.DTO.UsuarioDTO;
import com.sistema.botica.entity.Usuario;
import com.sistema.botica.service.RolService;
import com.sistema.botica.service.UsuarioService;

@Controller
@RequestMapping("/usuarios")
public class UsuarioController {
	private final UsuarioService usuarioService;
	private final RolService rolService;

	private static final String PASSWORD_REGEX = "^(?=.*[a-z])(?=.*[A-Z])(?=.*\\d)(?=.*[!@#$%^&*(),.?\":{}|<>]).{8,20}$";

	UsuarioController(UsuarioService usuarioService, RolService rolService) {
		this.usuarioService = usuarioService;
		this.rolService = rolService;
	}

	@GetMapping
	public String listar(@RequestParam(required = false) String palabraClave, Model modelo) {
		List<Usuario> lista = usuarioService.listarProductosClave(palabraClave);
		modelo.addAttribute("lista", lista);
		modelo.addAttribute("palabraClave", palabraClave);
		return "usuarios";
	}

	@GetMapping("/nuevo")
	public String nuevo(Model modelo) {
		modelo.addAttribute("usuario", new UsuarioDTO());
		modelo.addAttribute("listaRoles", rolService.listarActivos());
		modelo.addAttribute("modoEdicion", false);
		return "usuarios_formulario";
	}

	@GetMapping("/editar/{id}")
	public String editar(@PathVariable("id") Integer id, Model modelo) {

		Usuario usuario = usuarioService.buscarPorId(id);

		if (usuario == null) {
			return "redirect:/usuarios";
		}

		UsuarioDTO dto = new UsuarioDTO();

		dto.setIdUsuario(usuario.getIdUsuario());
		dto.setNombre(usuario.getNombre());
		dto.setUsername(usuario.getUsername());

		// Condierando que se mantiene el hash actual para evitar perder contraseña en
		// caso entre una cadena vacia
		dto.setPassword("");

		dto.setIdRol(usuario.getRol().getIdRol());
		dto.setEstado(usuario.getEstado());

		modelo.addAttribute("usuario", dto);
		modelo.addAttribute("listaRoles", rolService.listarActivos());
		modelo.addAttribute("modoEdicion", true);

		return "usuarios_formulario";
	}

	@PostMapping("/guardar")
	public String guardar(
			@Validated @ModelAttribute("usuario") UsuarioDTO usuarioDTO,
			BindingResult result, Model modelo) {

		boolean esNuevo = usuarioDTO.getIdUsuario() == null;
		boolean escribioPassword = usuarioDTO.getPassword() != null && !usuarioDTO.getPassword().isBlank();

		if (esNuevo || escribioPassword) {
			if (usuarioDTO.getPassword() != null && !usuarioDTO.getPassword().matches(PASSWORD_REGEX)) {
				result.rejectValue("password", "error.password",
						"La contraseña debe tener mínimo 8 caracteres, incluir una mayúscula, una minúscula, un número y un carácter especial (!@#$%^&*).");
			}
		}
		if (result.hasErrors()) {
			modelo.addAttribute("listaRoles", rolService.listarActivos());
			return "usuarios_formulario";
		}
		usuarioService.guardar(usuarioDTO);
		return "redirect:/usuarios";
	}

	@PostMapping("/editarUsuario")
	public String editar(
			@Validated @ModelAttribute("usuario") UsuarioDTO usuarioDTO,
			BindingResult result,
			Model modelo) {

		boolean escribioPassword = usuarioDTO.getPassword() != null &&
				!usuarioDTO.getPassword().isBlank();

		if (escribioPassword &&
				!usuarioDTO.getPassword().matches(PASSWORD_REGEX)) {

			result.rejectValue(
					"password",
					"error.password",
					"La contraseña debe tener mínimo 8 caracteres, incluir una mayúscula, una minúscula, un número y un carácter especial (!@#$%^&*).");
		}

		if (result.hasErrors()) {
			modelo.addAttribute("modoEdicion", true);
			modelo.addAttribute("listaRoles", rolService.listarActivos());
			return "usuarios_formulario";
		}

		usuarioService.guardar(usuarioDTO);

		return "redirect:/usuarios";
	}

	@GetMapping("/eliminar-fisico/{id}")
	public String eliminarFisico(@PathVariable("id") Integer id) {
		Usuario usuario = usuarioService.buscarPorId(id);
		if (usuario == null) {
			return "redirect:/usuarios";
		}
		usuarioService.eliminar(id);
		return "redirect:/usuarios";
	}

	@GetMapping("/eliminar/{id}")
	public String eliminar(@PathVariable("id") Integer id) {
		Usuario usuario = usuarioService.buscarPorId(id);
		if (usuario == null) {
			return "redirect:/usuarios";
		}
		usuarioService.eliminarLogico(id);
		return "redirect:/usuarios";
	}
}
