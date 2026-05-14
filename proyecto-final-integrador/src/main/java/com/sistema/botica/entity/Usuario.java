package com.sistema.botica.entity;

import java.util.List;

import com.fasterxml.jackson.annotation.JsonIgnore;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
@Entity
@Table(name = "usuario")
public class Usuario {
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "id_usuario")
	private Integer idUsuario;

	@NotBlank(message = "El nombre es obligatorio")
	@Size(max = 100)
	@Column(name = "nombre", length = 100)
	private String nombre;

	@NotBlank(message = "El username es obligatorio")
	@Size(max = 50)
	@Column(name = "username", length = 50)
	private String username;

	@NotBlank(message = "La constraseña es obligatoria")
	@Size(max = 255)
	@Column(name = "password", length = 255)
	private String password;

	@NotBlank(message = "Debes asignar un rol")
	@Size(max = 50)
	@Column(name = "rol", length = 50)
	private String rol;

	@Column(name = "estado", columnDefinition = "BOOLEAN DEFAULT TRUE")
	private Boolean estado = true;
	
	// Un usuario (empleado) puede realizar muchas ventas
	@JsonIgnore
	@OneToMany(mappedBy = "usuario")
	private List<Venta> listaVentas;

	public Integer getIdUsuario() {
		return idUsuario;
	}

	public void setIdUsuario(Integer idUsuario) {
		this.idUsuario = idUsuario;
	}

	public String getNombre() {
		return nombre;
	}

	public void setNombre(String nombre) {
		this.nombre = nombre;
	}

	public String getUsername() {
		return username;
	}

	public void setUsername(String username) {
		this.username = username;
	}

	public String getPassword() {
		return password;
	}

	public void setPassword(String password) {
		this.password = password;
	}

	public String getRol() {
		return rol;
	}

	public void setRol(String rol) {
		this.rol = rol;
	}

	public List<Venta> getListaVentas() {
		return listaVentas;
	}

	public void setListaVentas(List<Venta> listaVentas) {
		this.listaVentas = listaVentas;
	}

	public Boolean getEstado() {
		return estado;
	}

	public void setEstado(Boolean estado) {
		this.estado = estado;
	}
	
	
}
