package com.tecsup.services;

import com.tecsup.exception.ResourceNotFoundException;
import com.tecsup.model.Plato;
import com.tecsup.repository.PlatoRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class PlatoService {

    private final PlatoRepository repo;

    public PlatoService(PlatoRepository repo) {
        this.repo = repo;
    }

    public List<Plato> listar() {
        return repo.findAll();
    }

    public Plato obtener(Long id) {
        return repo.findById(id).orElseThrow(
            () -> new ResourceNotFoundException("Plato no encontrado con id: " + id)
        );
    }

    public Plato guardar(Plato p) {
        return repo.save(p);
    }

    public void eliminar(Long id) {
        Plato plato = repo.findById(id).orElseThrow(
            () -> new ResourceNotFoundException("Plato no encontrado con id: " + id)
        );
        repo.delete(plato);
    }
}
