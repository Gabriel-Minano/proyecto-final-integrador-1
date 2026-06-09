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
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

@Entity
@Table(name = "cliente")
public class Cliente {
	// Nota
	/*
	 * Luego cambiaremos las notaciones, puesto que el único que importa es el del
	 * DNI
	 */
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "id_cliente")
	private Integer idCliente;

	@NotBlank(message = "El nombre es obligatorio")
	@Size(max = 100)
	@Column(name = "nombre", length = 100)
	private String nombre;

	@NotBlank(message = "El apellido es obligatorio")
	@Size(max = 100)
	@Column(name = "apellido", length = 100)
	private String apellido;

	@NotBlank(message = "El documento es obligatorio")
	@Size(max = 20, message = "El documento no puede exceder 20 caracteres")
	@Column(name = "documento", length = 20)
	private String documento;

	@Size(max = 20, message = "El tamaño debe ser entre 0 y 20")
	@Column(name = "telefono", length = 20)
	private String telefono;

	@Email(message = "El correo debe ser uno válido")
	@Size(max = 100)
	@Column(name = "correo", length = 100)
	private String correo;

	@Size(max = 255)
	@Column(name = "direccion", length = 255)
	private String direccion;

	@Column(name = "estado", columnDefinition = "BOOLEAN DEFAULT TRUE")
	private Boolean estado = true;
	// Un cliente puede realizar muchas compras (ventas)
	@JsonIgnore
	@OneToMany(mappedBy = "cliente", cascade = CascadeType.ALL, orphanRemoval = true)
	private List<Venta> listaVentas;

	public Integer getIdCliente() {
		return idCliente;
	}

	public void setIdCliente(Integer idCliente) {
		this.idCliente = idCliente;
	}

	public String getNombre() {
		return nombre;
	}

	public void setNombre(String nombre) {
		this.nombre = nombre;
	}

	public String getApellido() {
		return apellido;
	}

	public void setApellido(String apellido) {
		this.apellido = apellido;
	}

	public String getDocumento() {
		return documento;
	}

	public void setDocumento(String documento) {
		this.documento = documento;
	}

	public String getTelefono() {
		return telefono;
	}

	public void setTelefono(String telefono) {
		this.telefono = telefono;
	}

	public String getCorreo() {
		return correo;
	}

	public void setCorreo(String correo) {
		this.correo = correo;
	}

	public String getDireccion() {
		return direccion;
	}

	public void setDireccion(String direccion) {
		this.direccion = direccion;
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
