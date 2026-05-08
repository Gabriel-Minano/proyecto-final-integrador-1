package com.sistema.botica.Repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.sistema.botica.entity.Producto;

@Repository
public interface ProductoRepository extends JpaRepository<Producto, Integer>{

	//Esto es un Query, Encontrar solo productos con estado activo = true
	//Utiliza una sintaxis sencilla.
	List<Producto> findByEstadoTrue();
	
	//Busqueda por coincidencia
	//Query explícito, para consultas complejas
	@Query("SELECT p FROM Producto p WHERE p.estado = true AND p.nombre LIKE %:palabraClave%")
	List<Producto> buscarPorCoincidencia(@Param("palabraClave") String palabraClave);
}
