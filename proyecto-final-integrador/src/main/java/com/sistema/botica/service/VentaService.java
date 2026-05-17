package com.sistema.botica.service;

import java.time.LocalDateTime;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.sistema.botica.Repository.ProductoRepository;
import com.sistema.botica.Repository.VentaRepository;
import com.sistema.botica.entity.DetalleVenta;
import com.sistema.botica.entity.Producto;
import com.sistema.botica.entity.Venta;

@Service
public class VentaService {
	
	@Autowired
	private VentaRepository ventaRepository;
	
	@Autowired
	private ProductoRepository productoRepository;
	
	public List<Venta> listarTodas() {
		return ventaRepository.findAll();
	}
	
	@Transactional
	public void registrarVenta(Venta venta) {
		venta.setFecha(LocalDateTime.now());
		
		for(DetalleVenta detalle : venta.getListaDetallesVenta()) {
			Producto producto = productoRepository.findById(detalle.getProducto().getIdProducto())
					.orElseThrow(() -> new RuntimeException("Producto no encontrado"));
			if(producto.getStockActual()<detalle.getCantidad()) {
				throw  new RuntimeException("Stock insuficiente para el producto: " + producto.getNombre());
			}
			
			producto.setStockActual(producto.getStockActual() - detalle.getCantidad());
			productoRepository.save(producto);
			
			detalle.setVenta(venta);
		}
		//JPA guarda en cascada todos los detalles de esa venta
		ventaRepository.save(venta);
	}
}
