package com.sistema.botica.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;

//import org.springframework.security.crypto.password.NoOpPasswordEncoder;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
@EnableWebSecurity
public class SecurityConfig {
	@Bean
	SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
		http.authorizeHttpRequests(auth -> auth
				// 1. Recursos públicos y login
				.requestMatchers("/login", "/css/**", "/js/**").permitAll()

				// 2. PERMISOS COMPARTIDOS (ADMINISTRADOR y CAJERO)
				// Facturación y búsqueda AJAX (El núcleo del cajero)
				.requestMatchers("/ventas/nuevo", "/ventas/guardar").hasAnyRole("ADMINISTRADOR", "CAJERO")
				.requestMatchers("/clientes/api/buscar", "/productos/api/buscar", "/clientes/nuevo").hasAnyRole("ADMINISTRADOR", "CAJERO")

				// Permitir ver los listados principales (Lectura)
				.requestMatchers("/ventas", "/productos", "/clientes", "/categorias", "/proveedores")
				.hasAnyRole("ADMINISTRADOR", "CAJERO")

				// 3. PERMISOS EXCLUSIVOS (Solo ADMINISTRADOR)
				// Paneles gerenciales
				.requestMatchers("/dashboard", "/usuarios/**", "/reportes/**").hasRole("ADMINISTRADOR")

				// Rutas de modificación (El muro de seguridad en el backend)
				.requestMatchers("/productos/nuevo", "/productos/editar/**", "/productos/eliminar/**",
						"/productos/guardar")
				.hasRole("ADMINISTRADOR")
				.requestMatchers("/clientes/nuevo", "/clientes/editar/**", "/clientes/eliminar/**", "/clientes/guardar")
				.hasRole("ADMINISTRADOR")
				.requestMatchers("/categorias/nuevo", "/categorias/editar/**", "/categorias/eliminar/**",
						"/categorias/guardar")
				.hasRole("ADMINISTRADOR")
				.requestMatchers("/proveedores/nuevo", "/proveedores/editar/**", "/proveedores/eliminar/**",
						"/proveedores/guardar")
				.hasRole("ADMINISTRADOR")

				// Cualquier otra petición requiere estar autenticado
				.anyRequest().authenticated()).formLogin(form -> form.loginPage("/login").defaultSuccessUrl("/", true) // Redirige
																														// al
																														// RaizController
						.permitAll())
				.logout(logout -> logout.logoutUrl("/logout").logoutSuccessUrl("/login?logout").permitAll());

		return http.build();
	}

	// @SuppressWarnings("deprecation")
	// @Bean
	// PasswordEncoder passwordEncoder() {
	// // Obliga al sistema a verificar las contraseñas usando encriptación segura
	// // BCrypt
	// return NoOpPasswordEncoder.getInstance();
	// }

	@Bean
	PasswordEncoder passwordEncoder() {
		return new BCryptPasswordEncoder();
	}

}
