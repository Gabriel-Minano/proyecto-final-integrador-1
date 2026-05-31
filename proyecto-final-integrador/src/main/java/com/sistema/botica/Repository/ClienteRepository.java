package com.sistema.botica.Repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.sistema.botica.entity.Cliente;
@Repository
public interface ClienteRepository extends JpaRepository<Cliente, Integer>{
	List<Cliente> findByEstadoTrue();
	
	@Query("SELECT c FROM Cliente c WHERE c.estado = true AND c.documento LIKE %:palabraClave%")
	List<Cliente> buscarPorCoincidencia(@Param("palabraClave") String palabraClave);
}
