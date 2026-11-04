package com.tecsup.controller;

import com.tecsup.dto.PedidoDTO;
import com.tecsup.model.Pedido;
import com.tecsup.services.PedidoService;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
@RequestMapping("/api/pedidos")
public class PedidoController {
    private final PedidoService service;
    public PedidoController(PedidoService service) { this.service = service; }

    @GetMapping
    public List<Pedido> listar() { return service.listar(); }

    @GetMapping("/{id}")
    public Pedido buscar(@PathVariable Long id) { return service.obtener(id); }

    @PostMapping
    public ResponseEntity<Pedido> guardar(@Valid @RequestBody PedidoDTO dto) {
        return ResponseEntity.status(201).body(service.guardar(dto));
    }

    @PutMapping("/{id}")
    public Pedido actualizar(@PathVariable Long id, @Valid @RequestBody PedidoDTO dto) {
        return service.actualizar(id, dto);
    }

    @DeleteMapping("/{id}")
    public String eliminar(@PathVariable Long id) {
        service.eliminar(id);
        return "Pedido eliminado correctamente";
    }
}
