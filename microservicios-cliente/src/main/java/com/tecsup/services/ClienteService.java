package com.tecsup.services;

import com.tecsup.exception.ResourceNotFoundException;
import com.tecsup.model.Cliente;
import com.tecsup.repository.ClienteRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class ClienteService {

    private final ClienteRepository repo;

    public ClienteService(ClienteRepository repo) {
        this.repo = repo;
    }

    public List<Cliente> listar() {
        return repo.findAll();
    }

    public Cliente obtener(Long id) {
        return repo.findById(id).orElseThrow(
            () -> new ResourceNotFoundException("Cliente no encontrado con id: " + id)
        );
    }

    public Cliente guardar(Cliente c) {
        return repo.save(c);
    }

    public void eliminar(Long id) {
        Cliente cliente = repo.findById(id).orElseThrow(
            () -> new ResourceNotFoundException("Cliente no encontrado con id: " + id)
        );
        repo.delete(cliente);
    }
}
