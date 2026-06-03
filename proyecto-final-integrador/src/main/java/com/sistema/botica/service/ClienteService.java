package com.sistema.botica.service;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
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

	// Listar clientes por palabra clave
	public List<Cliente> listarProductosClave(String palabraClave) {
		if (palabraClave != null && !palabraClave.isEmpty()) {
			return clienteRepository.buscarPorCoincidencia(palabraClave);
		}
		return listarActivos();

	}

	public void guardar(Cliente cliente) {
		clienteRepository.save(cliente);
	}

	public Cliente buscarPorId(Integer id) {
		return clienteRepository.findById(id).orElse(null);
	}

	public void eliminarLogico(Integer id) {
		Cliente cliente = clienteRepository.findById(id).orElse(null);
		if (cliente != null) {
			cliente.setEstado(false);
			clienteRepository.save(cliente);
		}
	}
	
	//Método nuevo para filtrar con paginación.
	public Page<Cliente> listarPaginadosYFiltrados(String palabraClave, Boolean estado, Pageable pageable) {
		return clienteRepository.buscarPaginadosYFiltrados(palabraClave, estado, pageable);
	}
}
