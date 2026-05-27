package com.sistema.botica.service;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.sistema.botica.Repository.UsuarioRepository;
import com.sistema.botica.DTO.UsuarioDTO;
import com.sistema.botica.entity.Usuario;
import org.springframework.security.crypto.password.PasswordEncoder;

@Service
public class UsuarioService {

	@Autowired
	private UsuarioRepository usuarioRepository;

	@Autowired
	private PasswordEncoder passwordEncoder;

	public List<Usuario> listarActivos() {
		return usuarioRepository.findByEstadoTrue();
	}

	public void guardar(UsuarioDTO dto) {

		Usuario usuario;

		// EDITAR
		if (dto.getIdUsuario() != null) {

			usuario = usuarioRepository
					.findById(dto.getIdUsuario())
					.orElse(new Usuario());

		} else {

			// NUEVO
			usuario = new Usuario();
		}

		usuario.setNombre(dto.getNombre());
		usuario.setUsername(dto.getUsername());
		usuario.setRol(dto.getRol());
		usuario.setEstado(dto.getEstado());

		// SOLO actualizar contraseña si escribieron una nueva
		if (dto.getPassword() != null &&
				!dto.getPassword().isBlank()) {

			usuario.setPassword(
					passwordEncoder.encode(dto.getPassword()));
		}

		usuarioRepository.save(usuario);
	}

	public Usuario buscarPorId(Integer id) {
		return usuarioRepository.findById(id).orElse(null);
	}

	public void eliminarLogico(Integer id) {
		Usuario usuario = usuarioRepository.findById(id).orElse(null);
		if (usuario != null) {
			usuario.setEstado(false);
			usuarioRepository.save(usuario);
		}
	}

	public Usuario buscarPorUsername(String username) {
		return usuarioRepository.findByUsernameAndEstadoTrue(username)
				.orElseThrow(() -> new RuntimeException("Usuario logueado no válido"));
	}
}
