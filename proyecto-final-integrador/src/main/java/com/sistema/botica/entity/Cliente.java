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
import jakarta.validation.constraints.Pattern;
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
	@Size(max = 50, min = 3, message = "El nombre no puede exceder 20 caracteres")
	@Pattern(regexp = "^[a-zA-ZáéíóúÁÉÍÓÚñÑ\\s]+$", message = "Solo se permiten letras y espacios")
	@Column(name = "nombre", length = 50)
	private String nombre;

	@NotBlank(message = "El apellido es obligatorio")
	@Size(max = 50, min = 3, message = "El apellido no puede exceder 50 caracteres")
	@Pattern(regexp = "^[a-zA-ZáéíóúÁÉÍÓÚñÑ\\s]+$", message = "Solo se permiten letras y espacios")
	@Column(name = "apellido", length = 50)
	private String apellido;

	@NotBlank(message = "El documento es obligatorio")
	@Size(max = 11, min = 8, message = "El documento debe tener entre 8 y 11 dígitos")
	@Pattern(regexp = "^[0-9]+$", message = "El campo debe contener únicamente números enteros positivos")
	@Column(name = "documento", length = 11)
	private String documento;

	@Size(max = 9, min = 9, message = "El tamaño debe ser de 9 dígitos")
	@Pattern(regexp = "^[0-9]+$", message = "El campo debe contener únicamente números enteros positivos")
	@Column(name = "telefono", length = 9)
	private String telefono;

	@Email(message = "El correo debe ser uno válido")
	@Size(max = 50, min = 10)
	@Pattern(regexp = "^[a-zA-Z0-9._%+-]+@[a-zA-Z0-9.-]+\\.[a-zA-Z]{2,}$", message = "El correo contiene caracteres no permitidos (no uses tildes, espacios ni la ñ)")
	@Column(name = "correo", length = 50)
	private String correo;

	@Size(max = 255, min = 10)
	@Pattern(regexp = "^[a-zA-Z0-9áéíóúÁÉÍÓÚñÑ\\s.,#\\-/]+$", message = "La dirección solo puede contener letras, números y caracteres válidos (., # - /)")
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
