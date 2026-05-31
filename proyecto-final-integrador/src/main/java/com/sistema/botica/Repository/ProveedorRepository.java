package com.sistema.botica.Repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.sistema.botica.entity.Proveedor;

@Repository
public interface ProveedorRepository extends JpaRepository<Proveedor, Integer>{
	List<Proveedor> findByEstadoTrue();
	@Query("SELECT p FROM Proveedor p WHERE p.estado = true AND (p.ruc LIKE %:palabraClave% OR p.nombre LIKE %:palabraClave%)")
	List<Proveedor> buscarPorCoincidencia(@Param("palabraClave") String palabraClave);
}
