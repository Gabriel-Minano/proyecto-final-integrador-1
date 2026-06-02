package com.sistema.botica.DTO;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;

import org.springframework.data.domain.Page;

import com.sistema.botica.entity.DetalleVenta;

public class ReporteProductosDTO {
	private Double porcentajeDisponibles;
    private Double porcentajeStockCritico;
    private Double porcentajeAgotados;
    private Double porcentajeSobrestock;
    private Integer totalProductosActivos;
    
    private Integer totalUnidadesDespachadas;
    private BigDecimal valorizacionTotal;
    
    private List<Map.Entry<String, Integer>> top10MasVendidos;
    private List<Map.Entry<String, Integer>> top10MenosVendidos;
    
    private Page<DetalleVenta> paginaMovimientos;

	public Double getPorcentajeDisponibles() {
		return porcentajeDisponibles;
	}

	public void setPorcentajeDisponibles(Double porcentajeDisponibles) {
		this.porcentajeDisponibles = porcentajeDisponibles;
	}

	public Double getPorcentajeStockCritico() {
		return porcentajeStockCritico;
	}

	public void setPorcentajeStockCritico(Double porcentajeStockCritico) {
		this.porcentajeStockCritico = porcentajeStockCritico;
	}

	public Double getPorcentajeAgotados() {
		return porcentajeAgotados;
	}

	public void setPorcentajeAgotados(Double porcentajeAgotados) {
		this.porcentajeAgotados = porcentajeAgotados;
	}

	public Double getPorcentajeSobrestock() {
		return porcentajeSobrestock;
	}

	public void setPorcentajeSobrestock(Double porcentajeSobrestock) {
		this.porcentajeSobrestock = porcentajeSobrestock;
	}

	public Integer getTotalProductosActivos() {
		return totalProductosActivos;
	}

	public void setTotalProductosActivos(Integer totalProductosActivos) {
		this.totalProductosActivos = totalProductosActivos;
	}

	public Integer getTotalUnidadesDespachadas() {
		return totalUnidadesDespachadas;
	}

	public void setTotalUnidadesDespachadas(Integer totalUnidadesDespachadas) {
		this.totalUnidadesDespachadas = totalUnidadesDespachadas;
	}

	public BigDecimal getValorizacionTotal() {
		return valorizacionTotal;
	}

	public void setValorizacionTotal(BigDecimal valorizacionTotal) {
		this.valorizacionTotal = valorizacionTotal;
	}

	public List<Map.Entry<String, Integer>> getTop10MasVendidos() {
		return top10MasVendidos;
	}

	public void setTop10MasVendidos(List<Map.Entry<String, Integer>> top10MasVendidos) {
		this.top10MasVendidos = top10MasVendidos;
	}

	public List<Map.Entry<String, Integer>> getTop10MenosVendidos() {
		return top10MenosVendidos;
	}

	public void setTop10MenosVendidos(List<Map.Entry<String, Integer>> top10MenosVendidos) {
		this.top10MenosVendidos = top10MenosVendidos;
	}

	public Page<DetalleVenta> getPaginaMovimientos() {
		return paginaMovimientos;
	}

	public void setPaginaMovimientos(Page<DetalleVenta> paginaMovimientos) {
		this.paginaMovimientos = paginaMovimientos;
	}
    
}
