package com.sistema.botica.service;

import com.sistema.botica.DTO.DetalleProductoDTO;
import com.sistema.botica.Repository.ProductoRepository;
import com.sistema.botica.entity.Producto;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class DetalleProductoReporteService {
    private final ProductoRepository productoRepository;

    public DetalleProductoReporteService(ProductoRepository productoRepository) {
        this.productoRepository = productoRepository;
    }

    @Transactional(readOnly = true)
    public DetalleProductoDTO obtenerDetalleProducto(Integer idProducto) {
        Producto producto = productoRepository.findById(idProducto).orElse(null);

        if (producto == null) {
            return null;
        }

        DetalleProductoDTO detalle = new DetalleProductoDTO();
        detalle.setIdProducto(producto.getIdProducto());
        detalle.setCodigo(producto.getCodigo());
        detalle.setNombre(producto.getNombre());
        detalle.setCategoriaNombre(producto.getCategoria().getNombre());
        detalle.setProveedorNombre(producto.getProveedor().getNombre());
        detalle.setPrecioCompra(producto.getPrecioCompra());
        detalle.setPrecioVenta(producto.getPrecioVenta());
        detalle.setStockActual(producto.getStockActual());
        detalle.setStockMinimo(producto.getStockMinimo());
        detalle.setStockMaximo(producto.getStockMaximo());
        detalle.setFechaVencimiento(producto.getFechaVencimiento());
        detalle.setEstado(producto.getEstado());

        return detalle;
    }
}
