package com.sistema.botica.service;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.sistema.botica.DTO.ReporteProductosEliminadosDTO;
import com.sistema.botica.Repository.ProductoRepository;
import com.sistema.botica.entity.Producto;

@Service
public class ProductosEliminadosReporteService {
    private final ProductoRepository productoRepository;

    ProductosEliminadosReporteService(ProductoRepository productoRepository) {
        this.productoRepository = productoRepository;
    }

    @Transactional(readOnly = true)
    public ReporteProductosEliminadosDTO generarReporteProductosEliminados(Pageable pageable) {
        ReporteProductosEliminadosDTO dto = new ReporteProductosEliminadosDTO();

        // Obtener todos los productos eliminados (estado = false)
        List<Producto> productosEliminados = productoRepository.findByEstadoFalse();
        dto.setTotalProductosEliminados(productosEliminados.size());

        // Obtener productos paginados
        Page<Producto> paginados = productoRepository.findByEstadoFalse(pageable);
        dto.setProductosEliminados(paginados);

        return dto;
    }
}
