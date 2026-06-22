package com.sistema.botica.service;

import com.sistema.botica.DTO.BoletaVentaDTO;
import com.sistema.botica.Repository.VentaRepository;
import com.sistema.botica.entity.Venta;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
@Service
public class BoletaReporteService {
    private final VentaRepository ventaRepository;

    BoletaReporteService(VentaRepository ventaRepository) {
        this.ventaRepository = ventaRepository;

    }

    @Transactional(readOnly = true)
	public BoletaVentaDTO obtenerBoletaVenta(Integer idVenta) {
		Venta venta = ventaRepository.findById(idVenta).orElse(null);

		if (venta == null) {
			return null;
		}

		BoletaVentaDTO boleta = new BoletaVentaDTO();
		boleta.setIdVenta(venta.getIdVenta());
		boleta.setFecha(venta.getFecha());
		boleta.setTotal(venta.getTotal());
		boleta.setClienteNombre(venta.getCliente().getNombre());
		boleta.setClienteApellido(venta.getCliente().getApellido());
		boleta.setUsuarioNombre(venta.getUsuario().getNombre());
		boleta.setDetalles(venta.getListaDetallesVenta());

		return boleta;
	}

}
