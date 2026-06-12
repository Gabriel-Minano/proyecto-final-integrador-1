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
@Table(name = "categoria")
public class Categoria {
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "id_categoria")
	private Integer idCategoria;

	@NotBlank(message = "El nombre de la categoría es obligatorio")
	@Size(max = 50, min = 3, message = "Mínimo 3 caracteres, máximo 50")
	@Pattern(regexp = "^[a-zA-Z0-9áéíóúÁÉÍÓÚñÑ\\-]+$", message = "Solo se permiten letras, números y guiones")
	@Column(name = "nombre", length = 50)
	private String nombre;

	// Eliminación lógica, osea desactiva el producto
	@Column(name = "estado", columnDefinition = "BOOLEAN DEFAULT TRUE")
	private Boolean estado = true;

	// Una categoría puede tener muchos productos
	@JsonIgnore
	@OneToMany(mappedBy = "categoria", cascade = CascadeType.ALL, orphanRemoval = true)
	private List<Producto> listaProductos;

	public Integer getIdCategoria() {
		return idCategoria;
	}

	public void setIdCategoria(Integer idCategoria) {
		this.idCategoria = idCategoria;
	}

	public String getNombre() {
		return nombre;
	}

	public void setNombre(String nombre) {
		this.nombre = nombre;
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
