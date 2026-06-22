package com.sistema.botica.DTO;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

import com.sistema.botica.entity.DetalleVenta;

public class BoletaVentaDTO {
    private Integer idVenta;
    private LocalDateTime fecha;
    private BigDecimal total;
    private String clienteNombre;
    private String clienteApellido;
    private String usuarioNombre;
    private List<DetalleVenta> detalles;

    public BoletaVentaDTO() {
    }

    public BoletaVentaDTO(Integer idVenta, LocalDateTime fecha, BigDecimal total, String clienteNombre,
            String clienteApellido, String usuarioNombre, List<DetalleVenta> detalles) {
        this.idVenta = idVenta;
        this.fecha = fecha;
        this.total = total;
        this.clienteNombre = clienteNombre;
        this.clienteApellido = clienteApellido;
        this.usuarioNombre = usuarioNombre;
        this.detalles = detalles;
    }

    public Integer getIdVenta() {
        return idVenta;
    }

    public void setIdVenta(Integer idVenta) {
        this.idVenta = idVenta;
    }

    public LocalDateTime getFecha() {
        return fecha;
    }

    public void setFecha(LocalDateTime fecha) {
        this.fecha = fecha;
    }

    public BigDecimal getTotal() {
        return total;
    }

    public void setTotal(BigDecimal total) {
        this.total = total;
    }

    public String getClienteNombre() {
        return clienteNombre;
    }

    public void setClienteNombre(String clienteNombre) {
        this.clienteNombre = clienteNombre;
    }

    public String getClienteApellido() {
        return clienteApellido;
    }

    public void setClienteApellido(String clienteApellido) {
        this.clienteApellido = clienteApellido;
    }

    public String getUsuarioNombre() {
        return usuarioNombre;
    }

    public void setUsuarioNombre(String usuarioNombre) {
        this.usuarioNombre = usuarioNombre;
    }

    public List<DetalleVenta> getDetalles() {
        return detalles;
    }

    public void setDetalles(List<DetalleVenta> detalles) {
        this.detalles = detalles;
    }

}
