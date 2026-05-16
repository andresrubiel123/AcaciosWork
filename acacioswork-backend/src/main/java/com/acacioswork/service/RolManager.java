package com.acacioswork.service;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.acacioswork.model.Rol;
import com.acacioswork.repository.RolRepository;

/** Servicio para gestionar roles. @author RADJ */
@Service
@Transactional
public class RolManager {

    @Autowired
    private RolRepository rolRepository;

    /** Recupera todos los roles del sistema. @author RADJ */
    public List<Rol> leerTodosRoles() {
        return rolRepository.findAll();
    }

}