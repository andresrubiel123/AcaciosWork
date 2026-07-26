package com.acacioswork.service;

import java.util.List;
import java.util.Optional;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.acacioswork.model.Cliente;
import com.acacioswork.repository.ClienteRepository;

/** servicio para la gestión de clientes. @author RADJ */
@Service
@Transactional
public class ClienteService {

    public ClienteService(ClienteRepository clienteRepository) {
        this.clienteRepository = clienteRepository;
    }



private final ClienteRepository clienteRepository;

    /** obtiene todos los clientes. @author RADJ */
    public List<Cliente> findAll() {
        return clienteRepository.findAll();
    }

    /** busca un cliente por id. @author RADJ */
    public Optional<Cliente> findById(Long id) {
        return clienteRepository.findById(id);
    }

    /** guarda un cliente. @author RADJ */
    public Cliente save(Cliente cliente) {
        return clienteRepository.save(cliente);
    }

    /** elimina un cliente por id. @author RADJ */
    public void deleteById(Long id) {
        clienteRepository.deleteById(id);
    }
}
