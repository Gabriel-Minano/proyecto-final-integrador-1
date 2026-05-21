package com.sistema.botica.service;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.crypto.password.NoOpPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
@EnableWebSecurity
public class SecurityConfig {
	@Bean
	SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
		http.authorizeHttpRequests(auth -> auth
				// Permitir acceso libre a los recursos estáticos y a la pantalla de login
				.requestMatchers("/login", "/css/**", "/js/**").permitAll()

				// Restricción estricta: Solo el Administrador ve la gestión base y el Dashboard
				.requestMatchers("/dashboard", "/productos/**", "/categorias/**", "/proveedores/**", "/usuarios/**")
				.hasRole("ADMINISTRADOR")

				// Acceso compartido o específico para Ventas
				.requestMatchers("/ventas/nuevo", "/ventas/guardar").hasAnyRole("ADMINISTRADOR", "CAJERO")
				.requestMatchers("/ventas").hasRole("ADMINISTRADOR") // Historial para auditoría del admin

				// Cualquier otra petición requiere estar autenticado
				.anyRequest().authenticated()).formLogin(form -> form.loginPage("/login") // Ruta de nuestra vista HTML
																							// personalizada
						.defaultSuccessUrl("/", true) // Redirige a la raíz para evaluar el rol tras iniciar sesión
						.permitAll())
				.logout(logout -> logout.logoutUrl("/logout").logoutSuccessUrl("/login?logout").permitAll());

		return http.build();
	}

	@SuppressWarnings("deprecation")
	@Bean
	PasswordEncoder passwordEncoder() {
		// Obliga al sistema a verificar las contraseñas usando encriptación segura
		// BCrypt
		return NoOpPasswordEncoder.getInstance();
	}
}
