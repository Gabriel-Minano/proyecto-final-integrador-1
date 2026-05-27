package com.sistema.botica.DTO;

import java.math.BigDecimal;

public class VentaPorDiaDTO {

    private Integer dia;
    private BigDecimal total;

    public VentaPorDiaDTO(Integer dia, BigDecimal total) {
        this.dia = dia;
        this.total = total;
    }

    public Integer getDia() {
        return dia;
    }

    public void setDia(Integer dia) {
        this.dia = dia;
    }

    public BigDecimal getTotal() {
        return total;
    }

    public void setTotal(BigDecimal total) {
        this.total = total;
    }
}