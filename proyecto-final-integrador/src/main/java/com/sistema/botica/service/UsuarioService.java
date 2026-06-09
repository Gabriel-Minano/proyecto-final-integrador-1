package com.sistema.botica.service;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.sistema.botica.Repository.RolRepository;
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

	@Autowired
	private RolRepository rolRepository;

	public List<Usuario> listarActivos() {
		return usuarioRepository.findByEstadoTrue();
	}

	// Listar usuarios por palabra clave
	public List<Usuario> listarProductosClave(String palabraClave) {
		if (palabraClave != null && !palabraClave.isEmpty()) {
			return usuarioRepository.buscarPorCoincidencia(palabraClave);
		}
		return listarActivos();

	}

	public void guardar(UsuarioDTO dto) {

		Usuario usuario;

		if (dto.getIdUsuario() != null) {
			usuario = usuarioRepository.findById(dto.getIdUsuario())
					.orElse(new Usuario());
		} else {
			usuario = new Usuario();
		}

		usuario.setNombre(dto.getNombre());
		usuario.setUsername(dto.getUsername());
		usuario.setEstado(dto.getEstado());

		usuario.setRol(
				rolRepository.findById(dto.getIdRol())
						.orElseThrow(() -> new RuntimeException("Rol no encontrado")));

		if (dto.getPassword() != null && !dto.getPassword().isBlank()) {
			usuario.setPassword(passwordEncoder.encode(dto.getPassword()));
		}

		usuarioRepository.save(usuario);
	}

	public Usuario buscarPorId(Integer id) {
		return usuarioRepository.findById(id).orElse(null);
	}

	public void eliminar(Integer id) {
		usuarioRepository.deleteById(id);
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
