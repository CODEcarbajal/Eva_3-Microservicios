package com.tecsup.model;

import jakarta.persistence.*;

@Entity
@Table(name = "plato")
public class Plato {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, length = 100)
    private String nombre;

    @Column(length = 200)
    private String descripcion;

    @Column(nullable = false)
    private Double precio;

    @Column(length = 50)
    private String categoria; // Ej. Entradas, Fondos, Bebidas, Postres

    @Column(length = 1)
    private String estado = "A";

    public Plato() {}

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
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
