package com.sistema.botica.service;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.sistema.botica.Repository.RolRepository;
import com.sistema.botica.entity.Rol;

@Service
public class RolService {

    @Autowired
    private RolRepository rolRepository;

    public List<Rol> listarActivos() {
        return rolRepository.findByEstadoTrue();
    }

    public void eliminar(Integer id) {
        rolRepository.deleteById(id);
    }
    public void eliminarLogico(Integer id){
        Rol rol = rolRepository.findById(id).orElse(null);
        if(rol != null){
            rol.setEstado(false);
            rolRepository.save(rol);
        }
    }
    public void editar(Rol r){
        rolRepository.save(r);
    }
}