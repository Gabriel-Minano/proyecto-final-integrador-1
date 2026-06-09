package com.sistema.botica.entity;

import java.math.BigDecimal;

import org.hibernate.annotations.OnDelete;
import org.hibernate.annotations.OnDeleteAction;

import com.fasterxml.jackson.annotation.JsonBackReference;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
@Entity
@Table(name = "detalle_venta")
public class DetalleVenta {
	@Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_detalle")
    private Integer idDetalle;

	@NotNull(message = "La cantidad es obligatoria")
	@Min(value = 1, message = "La cantidad mínima a vender es 1")
    @Column(name = "cantidad")
    private Integer cantidad;

	@NotNull(message = "El precio unitario es obligatorio")
	@Positive(message = "El precio debe ser mayor a cero")
	@Column(name = "precio_unitario", precision = 10, scale = 2)
    private BigDecimal precioUnitario;

	@NotNull(message = "El subtotal es obligatorio")
	@Min(value = 0)
    @Column(name = "subtotal", precision = 12, scale = 2)
    private BigDecimal subtotal;

    // Muchos detalles pertenecen a una venta
	@JsonBackReference
    @ManyToOne
    @JoinColumn(name = "id_venta")
	@OnDelete(action = OnDeleteAction.CASCADE)
    private Venta venta;

    // Muchos detalles corresponden a un producto
	@NotNull(message = "El producto es obligatorio")
    @ManyToOne
    @JoinColumn(name = "id_producto")
	@OnDelete(action = OnDeleteAction.CASCADE)
    private Producto producto;

	public Integer getIdDetalle() {
		return idDetalle;
	}

	public void setIdDetalle(Integer idDetalle) {
		this.idDetalle = idDetalle;
	}

	public Integer getCantidad() {
		return cantidad;
	}

	public void setCantidad(Integer cantidad) {
		this.cantidad = cantidad;
	}

	public BigDecimal getPrecioUnitario() {
		return precioUnitario;
	}

	public void setPrecioUnitario(BigDecimal precioUnitario) {
		this.precioUnitario = precioUnitario;
	}

	public BigDecimal getSubtotal() {
		return subtotal;
	}

	public void setSubtotal(BigDecimal subtotal) {
		this.subtotal = subtotal;
	}

	public Venta getVenta() {
		return venta;
	}

	public void setVenta(Venta venta) {
		this.venta = venta;
	}

	public Producto getProducto() {
		return producto;
	}

	public void setProducto(Producto producto) {
		this.producto = producto;
	}
    
    
}
