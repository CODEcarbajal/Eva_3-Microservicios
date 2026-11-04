package com.tecsup.repository;

import com.tecsup.model.Pedido;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.EntityGraph;
import java.util.List;
import java.util.Optional;

public interface PedidoRepository extends JpaRepository<Pedido, Long> {
    @Override
    @EntityGraph(attributePaths = "detalles")
    List<Pedido> findAll();

    @Override
    @EntityGraph(attributePaths = "detalles")
    Optional<Pedido> findById(Long id);
}
