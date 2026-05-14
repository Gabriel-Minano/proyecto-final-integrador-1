package com.sistema.botica.service;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.sistema.botica.Repository.ClienteRepository;
import com.sistema.botica.entity.Cliente;

@Service
public class ClienteService {
	@Autowired
	private ClienteRepository clienteRepository;

	public List<Cliente> listarActivos() {
		return clienteRepository.findByEstadoTrue();
	}
	public void guardar(Cliente cliente) {
		clienteRepository.save(cliente);
	}
	public Cliente buscarPorId(Integer id) {
		return clienteRepository.findById(id).orElse(null);
	}
	
	public void eliminarLogico(Integer id) {
		Cliente cliente = clienteRepository.findById(id).orElse(null);
		if(cliente != null) {
			cliente.setEstado(false);
			clienteRepository.save(cliente);
		}
	}
}
