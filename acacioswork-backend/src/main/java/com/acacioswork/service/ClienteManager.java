package com.acacioswork.service;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.acacioswork.model.Cliente;
import com.acacioswork.repository.ClienteRepository;

/** Servicio para gestionar clientes. @author RADJ */
@Service
@Transactional
public class ClienteManager {

    @Autowired
    private ClienteRepository clienteRepository;

    /** Crea un nuevo cliente. @author RADJ */
    public boolean crearCliente(Cliente cliente) {
        try {
            clienteRepository.save(cliente);
            return true;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    /** Lee un cliente por ID. @author RADJ */
    public Cliente leerCliente(Long idCliente) {
        return clienteRepository.findById(idCliente).orElse(null);
    }

    /** Lee todos los clientes. @author RADJ */
    public List<Cliente> leerTodosClientes() {
        return clienteRepository.findAll();
    }

    /** Actualiza un cliente. @author RADJ */
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

    /** Elimina un cliente por ID. @author RADJ */
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