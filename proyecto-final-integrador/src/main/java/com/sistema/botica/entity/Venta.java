package com.sistema.botica.entity;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonManagedReference;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.PositiveOrZero;
@Entity
@Table(name = "venta")
public class Venta {
	@Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_venta")
    private Integer idVenta;

	@NotNull(message = "La fecha no puede ser nula")
    @Column(name = "fecha")
    private LocalDateTime fecha;

	@NotNull(message = "El total es obligatorio")
	@PositiveOrZero(message = "El total no puede ser negativo")
    @Column(name = "total", precision = 12, scale = 2)
    private BigDecimal total;

    // Muchas ventas son realizadas por un usuario
	@NotNull(message = "Se debe especificar el usuario que realiza la venta")
    @ManyToOne
    @JoinColumn(name = "id_usuario")
    private Usuario usuario;

    // Muchas ventas pertenecen a un cliente
	@NotNull(message = "Debe asignar un cliente")
    @ManyToOne
    @JoinColumn(name = "id_cliente")
    private Cliente cliente;

    // Una venta tiene muchos detalles (Cascade para guardar la venta y sus detalles juntos)
    @JsonManagedReference
	@OneToMany(mappedBy = "venta", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<DetalleVenta> listaDetallesVenta = new ArrayList<>();

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

	public Usuario getUsuario() {
		return usuario;
	}

	public void setUsuario(Usuario usuario) {
		this.usuario = usuario;
	}

	public Cliente getCliente() {
		return cliente;
	}

	public void setCliente(Cliente cliente) {
		this.cliente = cliente;
	}

	public List<DetalleVenta> getListaDetallesVenta() {
		return listaDetallesVenta;
	}

	public void setListaDetallesVenta(List<DetalleVenta> listaDetallesVenta) {
		this.listaDetallesVenta = listaDetallesVenta;
	}
    
    
}
