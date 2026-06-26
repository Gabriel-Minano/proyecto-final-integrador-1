package com.sistema.botica.DTO;

import java.time.LocalDateTime;

public class ReporteIndicadoresDTO {
    private Integer totalProductosActivos;
    private Double porcentajeDisponibles;
    private Double porcentajeAgotados;
    private Double porcentajeStockCritico;
    private Integer totalSobrestock;
    private String mesMasVentas;
    private String productoMasVendido;
    private Integer ventasProductoMasVendido;
    private LocalDateTime fechaInicio;
    private LocalDateTime fechaFin;

    public Integer getTotalProductosActivos() {
        return totalProductosActivos;
    }

    public void setTotalProductosActivos(Integer totalProductosActivos) {
        this.totalProductosActivos = totalProductosActivos;
    }

    public Double getPorcentajeDisponibles() {
        return porcentajeDisponibles;
    }

    public void setPorcentajeDisponibles(Double porcentajeDisponibles) {
        this.porcentajeDisponibles = porcentajeDisponibles;
    }

    public Double getPorcentajeAgotados() {
        return porcentajeAgotados;
    }

    public void setPorcentajeAgotados(Double porcentajeAgotados) {
        this.porcentajeAgotados = porcentajeAgotados;
    }

    public Double getPorcentajeStockCritico() {
        return porcentajeStockCritico;
    }

    public void setPorcentajeStockCritico(Double porcentajeStockCritico) {
        this.porcentajeStockCritico = porcentajeStockCritico;
    }

    public Integer getTotalSobrestock() {
        return totalSobrestock;
    }

    public void setTotalSobrestock(Integer totalSobrestock) {
        this.totalSobrestock = totalSobrestock;
    }

    public String getMesMasVentas() {
        return mesMasVentas;
    }

    public void setMesMasVentas(String mesMasVentas) {
        this.mesMasVentas = mesMasVentas;
    }

    public String getProductoMasVendido() {
        return productoMasVendido;
    }

    public void setProductoMasVendido(String productoMasVendido) {
        this.productoMasVendido = productoMasVendido;
    }

    public Integer getVentasProductoMasVendido() {
        return ventasProductoMasVendido;
    }

    public void setVentasProductoMasVendido(Integer ventasProductoMasVendido) {
        this.ventasProductoMasVendido = ventasProductoMasVendido;
    }

    public LocalDateTime getFechaInicio() {
        return fechaInicio;
    }

    public void setFechaInicio(LocalDateTime fechaInicio) {
        this.fechaInicio = fechaInicio;
    }

    public LocalDateTime getFechaFin() {
        return fechaFin;
    }

    public void setFechaFin(LocalDateTime fechaFin) {
        this.fechaFin = fechaFin;
    }

}
