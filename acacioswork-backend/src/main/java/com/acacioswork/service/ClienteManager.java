package com.acacioswork.service;

import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.acacioswork.model.Cliente;
import com.acacioswork.repository.ClienteRepository;

/** servicio para gestionar clientes. @author RADJ */
@Service
@Transactional
public class ClienteManager {

    public ClienteManager(ClienteRepository clienteRepository) {
        this.clienteRepository = clienteRepository;
    }



private final ClienteRepository clienteRepository;

    /** crea un nuevo cliente. @author RADJ */
    public boolean crearCliente(Cliente cliente) {
        try {
            clienteRepository.save(cliente);
            return true;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    /** lee un cliente por id. @author RADJ */
    public Cliente leerCliente(Long idCliente) {
        return clienteRepository.findById(idCliente).orElse(null);
    }

    /** lee todos los clientes. @author RADJ */
    public List<Cliente> leerTodosClientes() {
        return clienteRepository.findAll();
    }

    /** actualiza un cliente. @author RADJ */
    public boolean actualizarCliente(Long idCliente, Cliente nuevoCliente) {
        try {
            if (clienteRepository.existsById(idCliente)) {
                nuevoCliente.setId(idCliente);
                clienteRepository.save(nuevoCliente);
                return true;
            }
            return false;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    /** elimina un cliente por id. @author RADJ */
    public boolean eliminarCliente(Long idCliente) {
        try {
            if (clienteRepository.existsById(idCliente)) {
                clienteRepository.deleteById(idCliente);
                return true;
            }
            return false;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }
}