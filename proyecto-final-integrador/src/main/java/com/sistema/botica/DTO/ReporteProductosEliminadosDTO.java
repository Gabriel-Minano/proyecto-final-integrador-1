package com.sistema.botica.DTO;

import org.springframework.data.domain.Page;

import com.sistema.botica.entity.Producto;

public class ReporteProductosEliminadosDTO {
    private Page<Producto> productosEliminados;
    private Integer totalProductosEliminados;

    public Page<Producto> getProductosEliminados() {
        return productosEliminados;
    }

    public void setProductosEliminados(Page<Producto> productosEliminados) {
        this.productosEliminados = productosEliminados;
    }

    public Integer getTotalProductosEliminados() {
        return totalProductosEliminados;
    }

    public void setTotalProductosEliminados(Integer totalProductosEliminados) {
        this.totalProductosEliminados = totalProductosEliminados;
    }
}
