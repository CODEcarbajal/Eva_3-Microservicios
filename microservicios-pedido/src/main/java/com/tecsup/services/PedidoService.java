package com.tecsup.services;

import com.tecsup.dto.PedidoDTO;
import com.tecsup.exception.ResourceNotFoundException;
import com.tecsup.model.DetallePedido;
import com.tecsup.model.Pedido;
import com.tecsup.repository.PedidoRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

@Service
@Transactional
public class PedidoService {
    private final PedidoRepository repository;

    public PedidoService(PedidoRepository repository) {
        this.repository = repository;
    }

    @Transactional(readOnly = true)
    public List<Pedido> listar() { return repository.findAll(); }

    @Transactional(readOnly = true)
    public Pedido obtener(Long id) {
        return repository.findById(id).orElseThrow(
            () -> new ResourceNotFoundException("Pedido no encontrado con id: " + id));
    }

    public Pedido guardar(PedidoDTO dto) {
        Pedido pedido = new Pedido();
        aplicar(pedido, dto);
        return repository.save(pedido);
    }

    public Pedido actualizar(Long id, PedidoDTO dto) {
        Pedido pedido = obtener(id);
        aplicar(pedido, dto);
        return repository.save(pedido);
    }

    private void aplicar(Pedido pedido, PedidoDTO dto) {
        pedido.setFecha(dto.fecha());
        pedido.setIdCliente(dto.idCliente());
        if (dto.estado() != null) pedido.setEstado(dto.estado());
        if (pedido.getDetalles() == null) pedido.setDetalles(new ArrayList<>());
        pedido.getDetalles().clear();
        BigDecimal total = BigDecimal.ZERO;
        for (var item : dto.detalles()) {
            DetallePedido detalle = new DetallePedido();
            detalle.setIdPlato(item.idPlato());
            detalle.setCantidad(item.cantidad());
            detalle.setPrecio(item.precio());
            detalle.setPedido(pedido);
            pedido.getDetalles().add(detalle);
            total = total.add(BigDecimal.valueOf(item.precio())
                .multiply(BigDecimal.valueOf(item.cantidad())));
        }
        pedido.setTotal(total.doubleValue());
    }

    public void eliminar(Long id) { repository.delete(obtener(id)); }
}
