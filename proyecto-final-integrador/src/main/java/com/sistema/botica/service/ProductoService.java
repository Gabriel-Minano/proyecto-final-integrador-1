package com.sistema.botica.service;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.sistema.botica.Repository.ProductoRepository;
import com.sistema.botica.entity.Producto;

@Service
public class ProductoService {
	@Autowired
	private ProductoRepository productoRepository;
	
	//Lo deje por si alguno quiere poner un filtro para ver todos
	//Los productos, incluso los desactivados
	public List<Producto> listarTodos(){
		return productoRepository.findAll();
	}
	
	//Listar solo los productos activos
	public List<Producto> listarActivos(){
		return productoRepository.findByEstadoTrue();
	}
	
	//Lista y también busca por coincidencia de palabra clave
	public List<Producto> listarProductosClave(String palabraClave){
		if(palabraClave!=null && !palabraClave.isEmpty()) {
			return productoRepository.buscarPorCoincidencia(palabraClave);
		}
		return listarActivos();
	}
	
	public void guardar(Producto producto) {
		productoRepository.save(producto);
	}
	
	public Producto buscarPorId(Integer id) {
		return productoRepository.findById(id).orElse(null);
	}
	
	//Eliminación lógica, desactiva el producto
	public void eliminarLogico(Integer id) {
		Producto producto = productoRepository.findById(id).orElse(null);
		if (producto != null) {
			producto.setEstado(false);
			productoRepository.save(producto);
		}
	}
}
