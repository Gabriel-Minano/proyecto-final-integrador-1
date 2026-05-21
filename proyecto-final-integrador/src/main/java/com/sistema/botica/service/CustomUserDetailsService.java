package com.sistema.botica.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import com.sistema.botica.Repository.UsuarioRepository;
import com.sistema.botica.entity.Usuario;

@Service
public class CustomUserDetailsService implements UserDetailsService{

	@Autowired
	private UsuarioRepository repository;
	@Override
	public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
		Usuario usuario = repository.findByUsernameAndEstadoTrue(username)
                .orElseThrow(() -> new UsernameNotFoundException("Usuario no encontrado o inactivo"));
		return User.withUsername(usuario.getUsername())
                .password(usuario.getPassword()) 
                .roles(usuario.getRol()) // Spring añadirá automáticamente el prefijo 'ROLE_' (ROLE_ADMINISTRADOR)
                .build();
	}

}
