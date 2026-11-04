package com.tecsup.dto;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.NotNull;

public class PlatoDTO {

    @NotBlank(message = "El nombre del plato es obligatorio")
    @Size(max = 100, message = "Máximo 100 caracteres")
    private String nombre;

    @Size(max = 200, message = "Máximo 200 caracteres")
    private String descripcion;

    @NotNull(message = "El precio es obligatorio")
    @DecimalMin(value = "0.01", message = "El precio debe ser mayor a cero")
    private Double precio;

    @NotBlank(message = "La categorÃ­a es obligatoria")
    @Size(max = 50, message = "Máximo 50 caracteres")
    private String categoria;

    @Pattern(regexp = "[AI]", message = "El estado debe ser A o I")
    private String estado;

    public String getNombre() { return nombre; }
    public void setNombre(String nombre) { this.nombre = nombre; }
    public String getDescripcion() { return descripcion; }
    public void setDescripcion(String descripcion) { this.descripcion = descripcion; }
    public Double getPrecio() { return precio; }
    public void setPrecio(Double precio) { this.precio = precio; }
    public String getCategoria() { return categoria; }
    public void setCategoria(String categoria) { this.categoria = categoria; }
    public String getEstado() { return estado; }
    public void setEstado(String estado) { this.estado = estado; }
}
