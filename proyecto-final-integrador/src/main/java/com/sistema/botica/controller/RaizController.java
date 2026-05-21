package com.sistema.botica.controller;

import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class RaizController {
	@GetMapping("/")
	public String redirigirPorRol(Authentication authentication) {
		if (authentication != null) {
			boolean isAdmin = authentication.getAuthorities().stream()
					.anyMatch(a -> a.getAuthority().equals("ROLE_ADMINISTRADOR"));

			if (isAdmin) {
				return "redirect:/dashboard"; // Administrador va al panel de control
			} else {
				return "redirect:/ventas/nuevo"; // Cajero va directo a facturar
			}
		}
		return "redirect:/login";
	}

	@GetMapping("/login")
	public String mostrarLogin() {
		return "login"; // Devuelve la plantilla login.html
	}
}
