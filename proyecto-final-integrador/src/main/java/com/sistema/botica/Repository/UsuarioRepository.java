package com.sistema.botica.Repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.sistema.botica.entity.Usuario;

@Repository
public interface UsuarioRepository extends JpaRepository<Usuario, Integer>{
	List<Usuario> findByEstadoTrue();
	
	Optional<Usuario> findByUsernameAndEstadoTrue(String username);
	
	@Query("SELECT u FROM Usuario u WHERE u.estado = true AND (u.nombre LIKE %:palabraClave% OR u.rol LIKE %:palabraClave%)")
	List<Usuario> buscarPorCoincidencia(@Param("palabraClave") String palabraClave);
}
