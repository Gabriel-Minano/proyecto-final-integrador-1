package com.sistema.botica.Repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.sistema.botica.entity.Categoria;

public interface CategoriaRepository extends JpaRepository<Categoria, Integer> {
	List<Categoria> findByEstadoTrue();

	@Query("SELECT c FROM Categoria c WHERE c.estado = true AND c.nombre LIKE %:palabraClave%")
	List<Categoria> buscarPorCoincidencia(@Param("palabraClave") String palabraClave);
}
