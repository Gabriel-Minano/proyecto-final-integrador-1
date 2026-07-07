package com.sistema.botica.DTO;

import java.math.BigDecimal;

public class DatoMensualDTO {
    private String mes;
    private Integer numeroMes;
    private BigDecimal ingresosTotales;
    private BigDecimal promedioMensual;

    public DatoMensualDTO() {
    }

    public DatoMensualDTO(String mes, Integer numeroMes, BigDecimal ingresosTotales, BigDecimal promedioMensual) {
        this.mes = mes;
        this.numeroMes = numeroMes;
        this.ingresosTotales = ingresosTotales;
        this.promedioMensual = promedioMensual;
    }

    public String getMes() {
        return mes;
    }

    public void setMes(String mes) {
        this.mes = mes;
    }

    public Integer getNumeroMes() {
        return numeroMes;
    }

    public void setNumeroMes(Integer numeroMes) {
        this.numeroMes = numeroMes;
    }

    public BigDecimal getIngresosTotales() {
        return ingresosTotales;
    }

    public void setIngresosTotales(BigDecimal ingresosTotales) {
        this.ingresosTotales = ingresosTotales;
    }

    public BigDecimal getPromedioMensual() {
        return promedioMensual;
    }

    public void setPromedioMensual(BigDecimal promedioMensual) {
        this.promedioMensual = promedioMensual;
    }
}
