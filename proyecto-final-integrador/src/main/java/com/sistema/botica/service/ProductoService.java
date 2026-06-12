package com.sistema.botica.service;

import java.time.LocalDate;
import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import com.sistema.botica.Repository.ProductoRepository;
import com.sistema.botica.entity.Producto;

@Service
public class ProductoService {
	private final ProductoRepository productoRepository;

	ProductoService(ProductoRepository productoRepository) {
		this.productoRepository = productoRepository;
	}

	// Lo deje por si alguno quiere poner un filtro para ver todos
	// Los productos, incluso los desactivados
	public List<Producto> listarTodos() {
		return productoRepository.findAll();
	}

	// Listar solo los productos activos
	public List<Producto> listarActivos() {
		return productoRepository.findByEstadoTrue();
	}

	// Lista y también busca por coincidencia de palabra clave
	public List<Producto> listarProductosClave(String palabraClave) {
		if (palabraClave != null && !palabraClave.isEmpty()) {
			return productoRepository.buscarPorCoincidencia(palabraClave);
		}
		return listarActivos();

	}

	// Filtrado de productos
	public List<Producto> filtrarProductos(String filtro) {

		switch (filtro) {

			case "stock":
				return productoRepository
						.findByEstadoTrueAndStockActualGreaterThan(0);

			case "sinstock":
				return productoRepository
						.findByEstadoTrueAndStockActual(0);

			case "vencer":
				return productoRepository
						.listarProductosProximosAVencer(
								LocalDate.now(),
								LocalDate.now().plusDays(30));

			case "vencido":
				return productoRepository
						.findByEstadoTrueAndFechaVencimientoBefore(
								LocalDate.now());

			case "eliminados":
				return productoRepository.findByEstadoFalse();

			default:
				return productoRepository.findByEstadoTrue();
		}
	}

	public void guardar(Producto producto) {
		productoRepository.save(producto);
	}

	public Producto buscarPorId(Integer id) {
		return productoRepository.findById(id).orElse(null);
	}

	public void eliminar(Integer id) {
		productoRepository.deleteById(id);
	}

	// Eliminación lógica, desactiva el producto
	public void eliminarLogico(Integer id) {
		Producto producto = productoRepository.findById(id).orElse(null);
		if (producto != null) {
			producto.setEstado(false);
			productoRepository.save(producto);
		}
	}

	public Page<Producto> listarPaginadosYFiltrados(String palabraClave, String filtro, Pageable pageable) {
		LocalDate hoy = LocalDate.now();
		LocalDate fechaLimite = hoy.plusDays(30);

		if (filtro == null || filtro.isEmpty()) {
			filtro = "activos";
		}

		return productoRepository.buscarPaginadosYFiltrados(palabraClave, filtro, hoy, fechaLimite, pageable);
	}

	public void editar(Producto p){
		productoRepository.save(p);
	}
}
