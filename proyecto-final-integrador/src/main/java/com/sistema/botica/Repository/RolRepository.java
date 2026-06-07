package com.sistema.botica.Repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.sistema.botica.entity.Rol;

@Repository
public interface RolRepository extends JpaRepository<Rol, Integer> {

    List<Rol> findByEstadoTrue();

}