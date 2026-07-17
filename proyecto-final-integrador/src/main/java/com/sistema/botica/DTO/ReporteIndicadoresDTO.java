package com.sistema.botica.DTO;

import java.time.LocalDateTime;

public class ReporteIndicadoresDTO {

    // =============================
    // INDICADORES DE INVENTARIO
    // =============================
    private Integer totalProductosActivos;

    private Double porcentajeDisponibles;
    private Double porcentajeAgotados;
    private Double porcentajeStockCritico;
    private Double porcentajeSobrestock;

    // =============================
    // INDICADORES DE VENTAS
    // =============================
    private Double porcentajeVentasMes;
    private Double porcentajeParticipacionProducto;

    // =============================
    // INFORMACIÓN COMPLEMENTARIA
    // =============================
    private String mesMasVentas;
    private String productoMasVendido;
    private Integer ventasProductoMasVendido;

    // =============================
    // PERÍODO DEL REPORTE
    // =============================
    private LocalDateTime fechaInicio;
    private LocalDateTime fechaFin;

    // =============================
    // GETTERS Y SETTERS
    // =============================

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

    public Double getPorcentajeSobrestock() {
        return porcentajeSobrestock;
    }

    public void setPorcentajeSobrestock(Double porcentajeSobrestock) {
        this.porcentajeSobrestock = porcentajeSobrestock;
    }

    public Double getPorcentajeVentasMes() {
        return porcentajeVentasMes;
    }

    public void setPorcentajeVentasMes(Double porcentajeVentasMes) {
        this.porcentajeVentasMes = porcentajeVentasMes;
    }

    public Double getPorcentajeParticipacionProducto() {
        return porcentajeParticipacionProducto;
    }

    public void setPorcentajeParticipacionProducto(Double porcentajeParticipacionProducto) {
        this.porcentajeParticipacionProducto = porcentajeParticipacionProducto;
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