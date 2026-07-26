package com.acacioswork.service;

import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.acacioswork.model.Rol;
import com.acacioswork.repository.RolRepository;

/** servicio para gestionar roles. @author RADJ */
@Service
@Transactional
public class RolService {

    public RolService(RolRepository rolRepository) {
        this.rolRepository = rolRepository;
    }



private final RolRepository rolRepository;

    /** recupera todos los roles del sistema. @author RADJ */
    public List<Rol> findAll() {
        return rolRepository.findAll();
    }
}
