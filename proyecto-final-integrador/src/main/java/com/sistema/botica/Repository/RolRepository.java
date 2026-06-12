package com.sistema.botica.Repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.sistema.botica.entity.Rol;

public interface RolRepository extends JpaRepository<Rol, Integer> {

    List<Rol> findByEstadoTrue();

}