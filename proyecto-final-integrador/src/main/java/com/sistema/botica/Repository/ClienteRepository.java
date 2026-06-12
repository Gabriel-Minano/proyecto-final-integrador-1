package com.sistema.botica.Repository;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.sistema.botica.entity.Cliente;

public interface ClienteRepository extends JpaRepository<Cliente, Integer> {
	List<Cliente> findByEstadoTrue();

	@Query("SELECT c FROM Cliente c WHERE c.estado = true AND ("
			+ "LOWER(c.documento) LIKE LOWER(CONCAT('%', :palabraClave, '%')) OR "
			+ "LOWER(c.nombre) LIKE LOWER(CONCAT('%', :palabraClave, '%')) OR "
			+ "LOWER(c.apellido) LIKE LOWER(CONCAT('%', :palabraClave, '%')))")
	List<Cliente> buscarPorCoincidencia(@Param("palabraClave") String palabraClave);

	// Busqueda combinada
	@Query("SELECT c FROM Cliente c WHERE (:estado IS NULL OR c.estado = :estado) AND "
			+ "(:palabraClave IS NULL OR :palabraClave = '' OR "
			+ "LOWER(c.documento) LIKE LOWER(CONCAT('%', :palabraClave, '%')) OR "
			+ "LOWER(c.nombre) LIKE LOWER(CONCAT('%', :palabraClave, '%')) OR "
			+ "LOWER(c.apellido) LIKE LOWER(CONCAT('%', :palabraClave, '%')))")
	Page<Cliente> buscarPaginadosYFiltrados(@Param("palabraClave") String palabraClave, @Param("estado") Boolean estado,
			Pageable pageable);
}
