package com.sistema.botica.service;

import java.time.LocalDateTime;
import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.sistema.botica.Repository.ProductoRepository;
import com.sistema.botica.Repository.VentaRepository;
import com.sistema.botica.entity.DetalleVenta;
import com.sistema.botica.entity.Producto;
import com.sistema.botica.entity.Venta;

@Service
public class VentaService {

	private final VentaRepository ventaRepository;

	private final ProductoRepository productoRepository;

	VentaService(VentaRepository ventaRepository, ProductoRepository productoRepository) {
		this.ventaRepository = ventaRepository;
		this.productoRepository = productoRepository;
	}

	public List<Venta> listarTodas() {
		return ventaRepository.findAll();
	}

	@Transactional
	public void registrarVenta(Venta venta) {
		venta.setFecha(LocalDateTime.now());

		for (DetalleVenta detalle : venta.getListaDetallesVenta()) {
			Producto producto = productoRepository.findById(detalle.getProducto().getIdProducto())
					.orElseThrow(() -> new RuntimeException("Producto no encontrado"));
			if (producto.getStockActual() < detalle.getCantidad()) {
				throw new RuntimeException("Stock insuficiente para el producto: " + producto.getNombre());
			}

			producto.setStockActual(producto.getStockActual() - detalle.getCantidad());
			productoRepository.save(producto);

			detalle.setVenta(venta);
		}
		// JPA guarda en cascada todos los detalles de esa venta
		ventaRepository.save(venta);
	}

	public Page<Venta> listarPaginadasPorFecha(LocalDateTime inicio, LocalDateTime fin, Pageable pageable) {
		return ventaRepository.findByFechaBetween(inicio, fin, pageable);
	}

	public void eliminar(Integer id) {
		ventaRepository.deleteById(id);
	}

	public void eliminarLogico(Integer id){
		Venta venta = ventaRepository.findById(id).orElse(null);
		if (venta != null) {
			ventaRepository.deleteById(id);
		}
	}
	public Venta buscarPorId(Integer id){
		return ventaRepository.findById(id).orElse(null);
	}

	public void editar(Venta v){
		ventaRepository.save(v);
	}
	// Este es diferente al de arriba, puesto que añade busqueda por clave si es
	// necesario, si alguien va a cambiar esto, dirijase al repository
	// Y modifique o agregue la consulta que necesite, ya sea por DNI, nombre de
	// empleado, username, datos de cliente etc.
	public Page<Venta> listarPaginadasPorFechaYClave(LocalDateTime inicio, LocalDateTime fin, String palabraClave,
			Pageable pageable) {
		return ventaRepository.buscarVentasPaginadasYFiltradas(inicio, fin, palabraClave, pageable);
	}
}
