package com.sistema.botica.entity;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

import org.hibernate.annotations.OnDelete;
import org.hibernate.annotations.OnDeleteAction;
import org.springframework.format.annotation.DateTimeFormat;

import com.fasterxml.jackson.annotation.JsonIgnore;

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
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.Future;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

@Entity
@Table(name = "producto")
public class Producto {
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "id_producto")
	private Integer idProducto;

	@NotBlank(message = "El nombre del producto es obligatorio")
	@Size(max = 100, message = "El nombre no debe exceder los 100 caracteres")
	@Pattern(regexp = "^[a-zA-Z0-9áéíóúÁÉÍÓÚñÑ\\s.+\\-%]+$", message = "El nombre del producto solo puede contener letras, números y caracteres válidos (. + - %)")
	@Column(name = "nombre", length = 100)
	private String nombre;

	@NotBlank(message = "El código es obligatorio")
	@Size(max = 50)
	@Pattern(regexp = "^[a-zA-Z0-9áéíóúÁÉÍÓÚñÑ\\-]+$", message = "Solo se permiten letras, números y guiones")
	@Column(name = "codigo", length = 50)
	private String codigo;

	@NotNull(message = "El precio de compra es obligatorio")
	@DecimalMin(value = "0.01", message = "El precio de compra debe ser mayor a 0")
	@Column(name = "precio_compra", precision = 10, scale = 2)
	private BigDecimal precioCompra;

	@NotNull(message = "El precio de venta es obligatorio")
	@DecimalMin(value = "0.01", message = "El precio de venta debe ser mayor a 0")
	@Column(name = "precio_venta", precision = 10, scale = 2)
	private BigDecimal precioVenta;

	@NotNull(message = "El stock actual es obligatorio")
	@Min(value = 0, message = "El stock no puede ser negativo")
	@Column(name = "stock_actual")
	private Integer stockActual;

	@NotNull(message = "El stock mínimo es obligatorio")
	@Min(value = 0, message = "El stock mínimo no puede ser negativo")
	@Column(name = "stock_minimo")
	private Integer stockMinimo;

	@NotNull(message = "El stock máximo es obligatorio")
	@Min(value = 1, message = "El stock máximo debe ser mayor a cero")
	@Column(name = "stock_maximo")
	private Integer stockMaximo;

	@NotNull(message = "La fecha de vencimiento es obligatoria")
	@Future(message = "La fecha de vencimiento debe ser en el futuro")
	@DateTimeFormat(pattern = "yyyy-MM-dd")
	@Column(name = "fecha_vencimiento")
	private LocalDate fechaVencimiento;

	// Muchos productos pertenecen a una categoría
	@NotNull(message = "Debe seleccionar una categoría")
	@ManyToOne
	@JoinColumn(name = "id_categoria")
	@OnDelete(action = OnDeleteAction.CASCADE)
	private Categoria categoria;

	// Muchos productos son distribuidos por un proveedor
	@NotNull(message = "Debe seleccionar un proveedor")
	@ManyToOne
	@JoinColumn(name = "id_proveedor")
	@OnDelete(action = OnDeleteAction.CASCADE)
	private Proveedor proveedor;

	// Eliminación lógica, osea desactiva el producto
	@Column(name = "estado", columnDefinition = "BOOLEAN DEFAULT TRUE")
	private Boolean estado = true;

	// Un producto puede estar en muchos detalles de venta
	@JsonIgnore
	@OneToMany(mappedBy = "producto", cascade = CascadeType.ALL, orphanRemoval = true)
	private List<DetalleVenta> listaDetallesVenta;

	public Integer getIdProducto() {
		return idProducto;
	}

	public void setIdProducto(Integer idProducto) {
		this.idProducto = idProducto;
	}

	public String getNombre() {
		return nombre;
	}

	public void setNombre(String nombre) {
		this.nombre = nombre;
	}

	public String getCodigo() {
		return codigo;
	}

	public void setCodigo(String codigo) {
		this.codigo = codigo;
	}

	public BigDecimal getPrecioCompra() {
		return precioCompra;
	}

	public void setPrecioCompra(BigDecimal precioCompra) {
		this.precioCompra = precioCompra;
	}

	public BigDecimal getPrecioVenta() {
		return precioVenta;
	}

	public void setPrecioVenta(BigDecimal precioVenta) {
		this.precioVenta = precioVenta;
	}

	public Integer getStockActual() {
		return stockActual;
	}

	public void setStockActual(Integer stockActual) {
		this.stockActual = stockActual;
	}

	public Integer getStockMinimo() {
		return stockMinimo;
	}

	public void setStockMinimo(Integer stockMinimo) {
		this.stockMinimo = stockMinimo;
	}

	public LocalDate getFechaVencimiento() {
		return fechaVencimiento;
	}

	public void setFechaVencimiento(LocalDate fechaVencimiento) {
		this.fechaVencimiento = fechaVencimiento;
	}

	public Categoria getCategoria() {
		return categoria;
	}

	public void setCategoria(Categoria categoria) {
		this.categoria = categoria;
	}

	public Proveedor getProveedor() {
		return proveedor;
	}

	public void setProveedor(Proveedor proveedor) {
		this.proveedor = proveedor;
	}

	public List<DetalleVenta> getListaDetallesVenta() {
		return listaDetallesVenta;
	}

	public void setListaDetallesVenta(List<DetalleVenta> listaDetallesVenta) {
		this.listaDetallesVenta = listaDetallesVenta;
	}

	public Boolean getEstado() {
		return estado;
	}

	public void setEstado(Boolean estado) {
		this.estado = estado;
	}

	public Integer getStockMaximo() {
		return stockMaximo;
	}

	public void setStockMaximo(Integer stockMaximo) {
		this.stockMaximo = stockMaximo;
	}

}
