package com.sistema.botica.service;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.sistema.botica.Repository.UsuarioRepository;
import com.sistema.botica.entity.Usuario;

@Service
public class UsuarioService {

	@Autowired
	private UsuarioRepository usuarioRepository;
	
	public List<Usuario> listarActivos() {
		return usuarioRepository.findByEstadoTrue();
	}
	public void guardar(Usuario usuario) {
		usuarioRepository.save(usuario);
	}
	public Usuario buscarPorId(Integer id) {
		return usuarioRepository.findById(id).orElse(null);
	}
	public void eliminarLogico(Integer id) {
		Usuario usuario = usuarioRepository.findById(id).orElse(null);
		if(usuario != null) {
			usuario.setEstado(false);
			usuarioRepository.save(usuario);
		}
	}
}
