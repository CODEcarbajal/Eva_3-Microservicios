package com.tecsup.dto;

import jakarta.validation.constraints.*;

public record DetallePedidoDTO(
    @NotNull @Positive Long idPlato,
    @NotNull @Positive Integer cantidad,
    @NotNull @DecimalMin("0.01") @Digits(integer = 10, fraction = 2) Double precio
) {}
