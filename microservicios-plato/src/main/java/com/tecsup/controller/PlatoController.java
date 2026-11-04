package com.tecsup.controller;

import com.tecsup.dto.PlatoDTO;
import com.tecsup.model.Plato;
import com.tecsup.services.PlatoService;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/platos")
public class PlatoController {

    private final PlatoService service;

    public PlatoController(PlatoService service) {
        this.service = service;
    }

    @GetMapping
    public ResponseEntity<List<Plato>> listar() {
        return ResponseEntity.ok(service.listar());
    }

    @PostMapping
    public ResponseEntity<?> guardar(@Valid @RequestBody PlatoDTO dto) {
        Plato p = new Plato();
        p.setNombre(dto.getNombre());
        p.setDescripcion(dto.getDescripcion());
        p.setPrecio(dto.getPrecio());
        p.setCategoria(dto.getCategoria());
        p.setEstado(dto.getEstado() != null ? dto.getEstado() : "A");
        return ResponseEntity.status(201).body(service.guardar(p));
    }

    @GetMapping("/{id}")
    public ResponseEntity<?> obtener(@PathVariable Long id) {
        return ResponseEntity.ok(service.obtener(id));
    }

    @PutMapping("/{id}")
    public ResponseEntity<?> actualizar(@PathVariable Long id, @Valid @RequestBody PlatoDTO dto) {
        Plato existente = service.obtener(id);
        existente.setNombre(dto.getNombre());
        existente.setDescripcion(dto.getDescripcion());
        existente.setPrecio(dto.getPrecio());
        existente.setCategoria(dto.getCategoria());
        if (dto.getEstado() != null) existente.setEstado(dto.getEstado());
        return ResponseEntity.ok(service.guardar(existente));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> eliminar(@PathVariable Long id) {
        service.eliminar(id);
        return ResponseEntity.ok("Plato eliminado correctamente");
    }
}
