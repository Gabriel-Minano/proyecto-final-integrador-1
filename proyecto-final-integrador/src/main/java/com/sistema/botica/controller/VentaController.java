package com.sistema.botica.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.sistema.botica.entity.Usuario;
import com.sistema.botica.entity.Venta;
import com.sistema.botica.service.ClienteService;
import com.sistema.botica.service.ProductoService;
import com.sistema.botica.service.UsuarioService;
import com.sistema.botica.service.VentaService;

import jakarta.validation.ConstraintViolationException;
import org.springframework.transaction.TransactionSystemException;

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
	public String listar(Model model) {
		model.addAttribute("listaVentas", ventaService.listarTodas());
		return "ventas";
	}

	@GetMapping("/nuevo")
	public String mostrarFormularioVenta(Model model, Authentication authentication) {
		// Inicializamos una entidad limpia
		model.addAttribute("venta", new Venta());

		// Cargamos catálogos base activos para los selectores y modales
		model.addAttribute("listaClientes", clienteService.listarActivos());
		model.addAttribute("listaProductos", productoService.listarActivos());

		// Auditoría segura: capturamos el usuario directamente del núcleo de seguridad
		String nombreUsuario = (authentication != null) ? authentication.getName() : "Usuario no autenticado";
		model.addAttribute("usernameLogueado", nombreUsuario);

		return "ventas_formulario";
	}

	@PostMapping("/guardar")
	public String guardarVenta(@ModelAttribute("venta") Venta venta, Authentication authentication,
			RedirectAttributes flash) {
		try {
			// 1. Validación de seguridad del Cliente
			if (venta.getCliente() == null || venta.getCliente().getIdCliente() == null) {
				flash.addFlashAttribute("error", "Debe abrir el buscador y seleccionar un cliente válido.");
				return "redirect:/ventas/nuevo";
			}

			// 2. Limpieza de índices rotos (Evita nulls si el cajero agrega y elimina filas
			// intercaladas)
			if (venta.getListaDetallesVenta() != null) {
				venta.getListaDetallesVenta().removeIf(detalle -> detalle == null || detalle.getProducto() == null
						|| detalle.getProducto().getIdProducto() == null);
			}

			// 3. Validación de contenido mínimo
			if (venta.getListaDetallesVenta() == null || venta.getListaDetallesVenta().isEmpty()) {
				flash.addFlashAttribute("error", "No se puede registrar una venta sin productos en el detalle.");
				return "redirect:/ventas/nuevo";
			}

			// 4. Asignación automática e irrevocable del cajero logueado
			String usernameLogueado = authentication.getName();
			Usuario usuarioCajero = usuarioService.buscarPorUsername(usernameLogueado);
			venta.setUsuario(usuarioCajero);

			// 5. Persistencia transaccional (Validación interna de stock y cascada)
			ventaService.registrarVenta(venta);
			flash.addFlashAttribute("success", "Venta registrada correctamente.");

		} catch (TransactionSystemException e) {
			// Ver error de JPA
			Throwable causaRaiz = e.getRootCause();
			if (causaRaiz instanceof ConstraintViolationException) {
				ConstraintViolationException cve = (ConstraintViolationException) causaRaiz;
				StringBuilder errores = new StringBuilder("Error de validación en BD: ");
				cve.getConstraintViolations().forEach(cv -> errores.append("[").append(cv.getPropertyPath())
						.append(" = ").append(cv.getMessage()).append("] "));
				flash.addFlashAttribute("error", errores.toString());
			} else {
				flash.addFlashAttribute("error", "Error transaccional BD: " + e.getMessage());
			}
			return "redirect:/ventas/nuevo";

		} catch (RuntimeException e) {
			flash.addFlashAttribute("error", "Alerta operativa: " + e.getMessage());
			return "redirect:/ventas/nuevo";

		} catch (Exception e) {
			flash.addFlashAttribute("error", "Error crítico inesperado: " + e.getMessage());
			return "redirect:/ventas/nuevo";
		}

		return "redirect:/ventas/nuevo";
	}
}
