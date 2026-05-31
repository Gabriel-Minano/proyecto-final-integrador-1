package com.sistema.botica.service;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.sistema.botica.Repository.ProveedorRepository;
import com.sistema.botica.entity.Proveedor;

@Service
public class ProveedorService {
	@Autowired
	private ProveedorRepository proveedorRepository;

	public List<Proveedor> listarTodos() {
		return proveedorRepository.findAll();
	}

	// Listado de proveedores activos, intenten solo usar este.
	public List<Proveedor> listarActivos() {
		return proveedorRepository.findByEstadoTrue();
	}

	// Listar proveedores por palabra clave
	public List<Proveedor> listarProductosClave(String palabraClave) {
		if (palabraClave != null && !palabraClave.isEmpty()) {
			return proveedorRepository.buscarPorCoincidencia(palabraClave);
		}
		return listarActivos();

	}

	public void guardar(Proveedor proveedor) {
		proveedorRepository.save(proveedor);
	}

	public Proveedor buscarPorId(Integer id) {
		return proveedorRepository.findById(id).orElse(null);
	}

	public void eliminar(Integer id) {
		proveedorRepository.deleteById(id);
	}

	// Eliminación lógica
	public void eliminarLogico(Integer id) {
		Proveedor proveedor = proveedorRepository.findById(id).orElse(null);
		if (proveedor != null) {
			proveedor.setEstado(false);
			proveedorRepository.save(proveedor);
		}
	}

}
