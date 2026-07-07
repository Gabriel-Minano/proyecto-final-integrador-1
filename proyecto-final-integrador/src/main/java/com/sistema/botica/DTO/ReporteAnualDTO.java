package com.sistema.botica.DTO;

import java.math.BigDecimal;
import java.util.List;

public class ReporteAnualDTO {
    private List<DatoMensualDTO> datosMonthly;
    private BigDecimal promedioAnual;
    private BigDecimal totalAnual;
    private String mesMaximo;
    private BigDecimal ventasMaximo;
    private String mesMinimo;
    private BigDecimal ventasMinimo;
    private Integer anio;

    public ReporteAnualDTO() {
    }

    public ReporteAnualDTO(List<DatoMensualDTO> datosMonthly, BigDecimal promedioAnual, BigDecimal totalAnual,
            String mesMaximo, BigDecimal ventasMaximo, String mesMinimo, BigDecimal ventasMinimo, Integer anio) {
        this.datosMonthly = datosMonthly;
        this.promedioAnual = promedioAnual;
        this.totalAnual = totalAnual;
        this.mesMaximo = mesMaximo;
        this.ventasMaximo = ventasMaximo;
        this.mesMinimo = mesMinimo;
        this.ventasMinimo = ventasMinimo;
        this.anio = anio;
    }

    public List<DatoMensualDTO> getDatosMonthly() {
        return datosMonthly;
    }

    public void setDatosMonthly(List<DatoMensualDTO> datosMonthly) {
        this.datosMonthly = datosMonthly;
    }

    public BigDecimal getPromedioAnual() {
        return promedioAnual;
    }

    public void setPromedioAnual(BigDecimal promedioAnual) {
        this.promedioAnual = promedioAnual;
    }

    public BigDecimal getTotalAnual() {
        return totalAnual;
    }

    public void setTotalAnual(BigDecimal totalAnual) {
        this.totalAnual = totalAnual;
    }

    public String getMesMaximo() {
        return mesMaximo;
    }

    public void setMesMaximo(String mesMaximo) {
        this.mesMaximo = mesMaximo;
    }

    public BigDecimal getVentasMaximo() {
        return ventasMaximo;
    }

    public void setVentasMaximo(BigDecimal ventasMaximo) {
        this.ventasMaximo = ventasMaximo;
    }

    public String getMesMinimo() {
        return mesMinimo;
    }

    public void setMesMinimo(String mesMinimo) {
        this.mesMinimo = mesMinimo;
    }

    public BigDecimal getVentasMinimo() {
        return ventasMinimo;
    }

    public void setVentasMinimo(BigDecimal ventasMinimo) {
        this.ventasMinimo = ventasMinimo;
    }

    public Integer getAnio() {
        return anio;
    }

    public void setAnio(Integer anio) {
        this.anio = anio;
    }
}
