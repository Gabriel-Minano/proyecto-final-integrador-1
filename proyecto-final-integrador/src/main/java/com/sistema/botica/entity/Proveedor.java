package com.sistema.botica.entity;

import java.util.List;

import com.fasterxml.jackson.annotation.JsonIgnore;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

@Entity
@Table(name = "proveedor")
public class Proveedor {
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "id_proveedor")
	private Integer idProveedor;

	@NotBlank(message = "El nombre del proveedor es obligatorio")
	@Size(max = 100, message = "El nombre no puede exceder los 100 caracteres")
	@Column(name = "nombre", length = 100)
	private String nombre;

	@NotBlank(message = "El RUC es obligatorio")
	@Pattern(regexp = "^[0-9]{11}$", message = "El RUC debe contener exactamente 11 dígitos numéricos")
	@Column(name = "ruc", length = 20)
	private String ruc;

	@Size(max = 9, message = "El teléfono no puede exceder los 9 caracteres")
	@Pattern(regexp = "^[0-9]{6,9}$", message = "El telefono debe contener por lo menos 6 dígitos numéricos")
	@Column(name = "telefono", length = 20)
	private String telefono;

	@Size(max = 255, message = "La dirección no puede exceder los 255 caracteres")
	@Column(name = "direccion", length = 255)
	private String direccion;

	// Eliminación lógica, osea desactiva el producto
	@Column(name = "estado", columnDefinition = "BOOLEAN DEFAULT TRUE")
	private Boolean estado = true;

	// Un proveedor puede distribuir muchos productos
	@JsonIgnore
	@OneToMany(mappedBy = "proveedor", cascade = CascadeType.ALL, orphanRemoval = true)
	private List<Producto> listaProductos;

	public Integer getIdProveedor() {
		return idProveedor;
	}

	public void setIdProveedor(Integer idProveedor) {
		this.idProveedor = idProveedor;
	}

	public String getNombre() {
		return nombre;
	}

	public void setNombre(String nombre) {
		this.nombre = nombre;
	}

	public String getRuc() {
		return ruc;
	}

	public void setRuc(String ruc) {
		this.ruc = ruc;
	}

	public String getTelefono() {
		return telefono;
	}

	public void setTelefono(String telefono) {
		this.telefono = telefono;
	}

	public String getDireccion() {
		return direccion;
	}

	public void setDireccion(String direccion) {
		this.direccion = direccion;
	}

	public List<Producto> getListaProductos() {
		return listaProductos;
	}

	public void setListaProductos(List<Producto> listaProductos) {
		this.listaProductos = listaProductos;
	}

	public Boolean getEstado() {
		return estado;
	}

	public void setEstado(Boolean estado) {
		this.estado = estado;
	}

}
