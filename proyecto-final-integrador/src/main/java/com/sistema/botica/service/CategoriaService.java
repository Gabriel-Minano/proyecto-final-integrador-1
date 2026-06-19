package com.sistema.botica.service;

import java.util.List;

import org.springframework.stereotype.Service;

import com.sistema.botica.Repository.CategoriaRepository;
import com.sistema.botica.entity.Categoria;

@Service
public class CategoriaService {
	private final CategoriaRepository categoriaRepository;

	CategoriaService(CategoriaRepository categoriaRepository) {
		this.categoriaRepository = categoriaRepository;
	}

	// Lo deje por si alguno quiere poner un filtro para ver todas
	// las categorias, incluso las desactivadas
	public List<Categoria> listarTodas() {
		return categoriaRepository.findAll();
	}

	public List<Categoria> listarActivas() {
		return categoriaRepository.findByEstadoTrue();
	}

	// Listar categorias por palabra clave
	public List<Categoria> listarProductosClave(String palabraClave) {
		if (palabraClave != null && !palabraClave.isEmpty()) {
			return categoriaRepository.buscarPorCoincidencia(palabraClave);
		}
		return listarActivas();

	}

	public void guardar(Categoria cat) {
		categoriaRepository.save(cat);
	}
	public Categoria guardar2(Categoria cat) {
		return categoriaRepository.save(cat);
	}

	public Categoria buscarPorId(Integer id) {
		return categoriaRepository.findById(id).orElse(null);
	}

	// Eliminación por base de datos
	public void eliminar(Integer id) {
		categoriaRepository.deleteById(id);
	}

	// Eliminación por lógica, solo oculta el producto, evita errores de punteros
	public void eliminarLogico(Integer id) {
		Categoria categoria = categoriaRepository.findById(id).orElse(null);
		if (categoria != null) {
			categoria.setEstado(false);
			categoriaRepository.save(categoria);
		}
	}

	public void editar(Categoria c) {
		categoriaRepository.save(c);
	}
}
