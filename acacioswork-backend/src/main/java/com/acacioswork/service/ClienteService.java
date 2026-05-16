package com.acacioswork.service;

import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.acacioswork.model.Cliente;
import com.acacioswork.repository.ClienteRepository;

/** Servicio para la gestión de clientes. @author RADJ */
@Service
@Transactional
public class ClienteService {

    @Autowired
    private ClienteRepository clienteRepository;

    /** Obtiene todos los clientes. @author RADJ */
    public List<Cliente> findAll() {
        return clienteRepository.findAll();
    }

    /** Busca un cliente por ID. @author RADJ */
    public Optional<Cliente> findById(Long id) {
        return clienteRepository.findById(id);
    }

    /** Guarda un cliente. @author RADJ */
    public Cliente save(Cliente cliente) {
        return clienteRepository.save(cliente);
    }

    /** Elimina un cliente por ID. @author RADJ */
    public void deleteById(Long id) {
        clienteRepository.deleteById(id);
    }
}
