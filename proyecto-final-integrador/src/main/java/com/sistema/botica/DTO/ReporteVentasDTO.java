package com.sistema.botica.DTO;

import java.math.BigDecimal;
import java.util.List;

import com.sistema.botica.entity.Venta;
//He creado esto para evitar mandar objetos complejos y llenar la vista
//Esto se usará para el módulo de reportes
public class ReporteVentasDTO {
	private BigDecimal totalVentas;
	private Integer productosVendidos;
	private String productoMasVendido;
	private String mesMasVentas;
	private List<Venta> listaVentas;

	public BigDecimal getTotalVentas() {
		return totalVentas;
	}

	public void setTotalVentas(BigDecimal totalVentas) {
		this.totalVentas = totalVentas;
	}

	public Integer getProductosVendidos() {
		return productosVendidos;
	}

	public void setProductosVendidos(Integer productosVendidos) {
		this.productosVendidos = productosVendidos;
	}

	public String getProductoMasVendido() {
		return productoMasVendido;
	}

	public void setProductoMasVendido(String productoMasVendido) {
		this.productoMasVendido = productoMasVendido;
	}

	public String getMesMasVentas() {
		return mesMasVentas;
	}

	public void setMesMasVentas(String mesMasVentas) {
		this.mesMasVentas = mesMasVentas;
	}

	public List<Venta> getListaVentas() {
		return listaVentas;
	}

	public void setListaVentas(List<Venta> listaVentas) {
		this.listaVentas = listaVentas;
	}

}
