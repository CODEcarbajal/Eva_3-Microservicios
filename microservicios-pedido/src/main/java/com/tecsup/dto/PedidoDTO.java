package com.tecsup.dto;

import jakarta.validation.Valid;
import jakarta.validation.constraints.*;
import java.time.LocalDate;
import java.util.List;

public record PedidoDTO(
    @NotNull(message = "La fecha es obligatoria") LocalDate fecha,
    @NotNull @Positive Long idCliente,
    @Pattern(regexp = "RECIBIDO|EN_PREPARACION|LISTO|ENTREGADO|CANCELADO",
        message = "Estado de pedido inválido") String estado,
    @NotEmpty(message = "El pedido debe tener al menos un detalle")
    List<@NotNull @Valid DetallePedidoDTO> detalles
) {}
