# GUÍA DE LABORATORIO: DESARROLLO DE APLICACIONES WEB AVANZADO
## TEMA: Arquitectura de Microservicios — Sistema de Pedidos de Restaurante

---

### Capacidades
* Diseñar, estructurar e implementar microservicios desacoplados utilizando Spring Boot.
* Aplicar el patrón *Database-per-Service* gestionando esquemas MySQL independientes.
* Implementar modelos de datos, DTOs de transferencia, repositorios JPA, validaciones y servicios REST.
* Configurar perfiles de ejecución (`dev` y `prod`) mediante archivos YAML.
* Probar la integración de microservicios usando Postman.

---

### Seguridad y Normas del Taller
1. Queda prohibida la manipulación indebida de hardware, redes o conexiones eléctricas.
2. No ingerir alimentos ni bebidas dentro del entorno de laboratorio.
3. Mantener limpia y ordenada el área de trabajo.

---

## PROCEDIMIENTO PASO A PASO

### Paso 1: Creación de las Bases de Datos en MySQL (Ya estan creadas en DBeaver con servidor de Xampp MySql)
Ejecutar el script SQL para aprovisionar los esquemas independientes correspondientes a cada microservicio:

```sql
CREATE DATABASE IF NOT EXISTS bd_pedido_restaurante;
CREATE DATABASE IF NOT EXISTS bd_cliente_restaurante;
CREATE DATABASE IF NOT EXISTS bd_plato;
```

---

### Paso 2: Creación de la Carpeta y Proyectos Maven
1. Crear una carpeta principal de trabajo llamada `microservicios-restaurante`.
2. Generar los tres proyectos en **[Spring Initializr](https://start.spring.io/)** con los siguientes metadatos:

#### A. Microservicio Cliente (`cliente-service`) --> Ya esta creado en la carpeta microservicios-cliente

#### B. Microservicio Plato (`plato-service`)  --> Ya esta creado en la carpeta microservicios-plato

#### C. Microservicio Pedido (`pedido-service`)  --> Ya esta creado en la carpeta microservicios-pedido

---

### Paso 3: Apertura y Estructura en IntelliJ IDEA
Abrir la carpeta `microservicios-restaurante` en IntelliJ IDEA y verificar que cada proyecto contenga la siguiente estructura de paquetes dentro de `src/main/java/com/tecsup`:

* `controller`
* `dto`
* `exception`
* `model`
* `repository`
* `services`

---

### Paso 4: Configuración de Archivos YAML por Microservicio

Eliminar el archivo `application.properties` en cada proyecto (`src/main/resources/`) y reemplazarlo por `application.yml`:

#### A. `cliente-service/src/main/resources/application.yml`
```yaml
server:
  port: 8081
spring:
  application:
    name: cliente-service
  datasource:
    url: jdbc:mysql://localhost:3306/bd_cliente_restaurante
    username: root
    password: 
  jpa:
    hibernate:
      ddl-auto: update
    show-sql: true
    database-platform: org.hibernate.dialect.MySQLDialect
```

#### B. `plato-service/src/main/resources/application.yml`
```yaml
server:
  port: 8082
spring:
  application:
    name: plato-service
  datasource:
    url: jdbc:mysql://localhost:3306/bd_plato
    username: root
    password: 
  jpa:
    hibernate:
      ddl-auto: update
    show-sql: true
    database-platform: org.hibernate.dialect.MySQLDialect
```

#### C. `pedido-service/src/main/resources/application.yml`
```yaml
server:
  port: 8083
spring:
  application:
    name: pedido-service
  datasource:
    url: jdbc:mysql://localhost:3306/bd_pedido_restaurante;
    username: root
    password: 
  jpa:
    hibernate:
      ddl-auto: update
    show-sql: true
    database-platform: org.hibernate.dialect.MySQLDialect
```

---

### Paso 5: Implementación del Microservicio `cliente-service` (Puerto 8081)

#### 5.1. Entidad: `com.tecsup.model.Cliente`
```java
package com.tecsup.model;

import jakarta.persistence.*;

@Entity
@Table(name = "cliente")
public class Cliente {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, length = 100)
    private String nombre;

    @Column(nullable = false, length = 100)
    private String apellidos;

    @Column(length = 15)
    private String telefono;

    @Column(length = 200)
    private String direccion;

    @Column(length = 1)
    private String estado = "A";

    public Cliente() {}

    public Cliente(Long id, String nombre, String apellidos, String telefono, String direccion, String estado) {
        this.id = id;
        this.nombre = nombre;
        this.apellidos = apellidos;
        this.telefono = telefono;
        this.direccion = direccion;
        this.estado = estado;
    }

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public String getNombre() { return nombre; }
    public void setNombre(String nombre) { this.nombre = nombre; }
    public String getApellidos() { return apellidos; }
    public void setApellidos(String apellidos) { this.apellidos = apellidos; }
    public String getTelefono() { return telefono; }
    public void setTelefono(String telefono) { this.telefono = telefono; }
    public String getDireccion() { return direccion; }
    public void setDireccion(String direccion) { this.direccion = direccion; }
    public String getEstado() { return estado; }
    public void setEstado(String estado) { this.estado = estado; }
}
```

#### 5.2. DTO: `com.tecsup.dto.ClienteDTO`
```java
package com.tecsup.dto;

import jakarta.validation.constraints.NotBlank;

public class ClienteDTO {

    @NotBlank(message = "El nombre es obligatorio")
    private String nombre;

    @NotBlank(message = "Los apellidos son obligatorios")
    private String apellidos;

    private String telefono;
    private String direccion;

    @NotBlank(message = "El estado es obligatorio")
    private String estado;

    public String getNombre() { return nombre; }
    public void setNombre(String nombre) { this.nombre = nombre; }
    public String getApellidos() { return apellidos; }
    public void setApellidos(String apellidos) { this.apellidos = apellidos; }
    public String getTelefono() { return telefono; }
    public void setTelefono(String telefono) { this.telefono = telefono; }
    public String getDireccion() { return direccion; }
    public void setDireccion(String direccion) { this.direccion = direccion; }
    public String getEstado() { return estado; }
    public void setEstado(String estado) { this.estado = estado; }
}
```

#### 5.3. Excepciones: `ResourceNotFoundException` y `GlobalExceptionHandler`
```java
package com.tecsup.exception;

public class ResourceNotFoundException extends RuntimeException {
    public ResourceNotFoundException(String mensaje) {
        super(mensaje);
    }
}
```

```java
package com.tecsup.exception;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.HashMap;
import java.util.Map;

@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<Map<String, String>> manejarErrores(MethodArgumentNotValidException ex) {
        Map<String, String> errores = new HashMap<>();
        ex.getBindingResult().getFieldErrors().forEach(error -> 
            errores.put(error.getField(), error.getDefaultMessage())
        );
        return ResponseEntity.badRequest().body(errores);
    }

    @ExceptionHandler(ResourceNotFoundException.class)
    public ResponseEntity<Map<String, String>> manejarNoEncontrado(ResourceNotFoundException ex) {
        Map<String, String> error = new HashMap<>();
        error.put("mensaje", ex.getMessage());
        error.put("codigo", "404");
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(error);
    }
}
```

#### 5.4. Repositorio: `com.tecsup.repository.ClienteRepository`
```java
package com.tecsup.repository;

import com.tecsup.model.Cliente;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ClienteRepository extends JpaRepository<Cliente, Long> {}
```

#### 5.5. Servicio: `com.tecsup.services.ClienteService`
```java
package com.tecsup.services;

import com.tecsup.exception.ResourceNotFoundException;
import com.tecsup.model.Cliente;
import com.tecsup.repository.ClienteRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class ClienteService {

    @Autowired
    private ClienteRepository repo;

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
```

#### 5.6. Controlador: `com.tecsup.controller.ClienteController`
```java
package com.tecsup.controller;

import com.tecsup.dto.ClienteDTO;
import com.tecsup.model.Cliente;
import com.tecsup.services.ClienteService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/clientes")
public class ClienteController {

    @Autowired
    private ClienteService service;

    @GetMapping
    public ResponseEntity<List<Cliente>> listar() {
        return ResponseEntity.ok(service.listar());
    }

    @PostMapping
    public ResponseEntity<?> guardar(@Valid @RequestBody ClienteDTO dto) {
        Cliente c = new Cliente();
        c.setNombre(dto.getNombre());
        c.setApellidos(dto.getApellidos());
        c.setTelefono(dto.getTelefono());
        c.setDireccion(dto.getDireccion());
        c.setEstado(dto.getEstado());
        Cliente guardado = service.guardar(c);
        return ResponseEntity.status(201).body(guardado);
    }

    @GetMapping("/{id}")
    public ResponseEntity<?> obtener(@PathVariable Long id) {
        Cliente c = service.obtener(id);
        return ResponseEntity.ok(c);
    }

    @PutMapping("/{id}")
    public ResponseEntity<?> actualizar(@PathVariable Long id, @Valid @RequestBody ClienteDTO dto) {
        Cliente existente = service.obtener(id);
        existente.setNombre(dto.getNombre());
        existente.setApellidos(dto.getApellidos());
        existente.setTelefono(dto.getTelefono());
        existente.setDireccion(dto.getDireccion());
        existente.setEstado(dto.getEstado());
        return ResponseEntity.ok(service.guardar(existente));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> eliminar(@PathVariable Long id) {
        service.eliminar(id);
        return ResponseEntity.ok("Eliminado correctamente");
    }
}
```

---

### Paso 6: Implementación del Microservicio `plato-service` (Puerto 8082)

#### 6.1. Entidad: `com.tecsup.model.Plato`
```java
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
```

#### 6.2. DTO: `com.tecsup.dto.PlatoDTO`
```java
package com.tecsup.dto;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public class PlatoDTO {

    @NotBlank(message = "El nombre del plato es obligatorio")
    private String nombre;

    private String descripcion;

    @NotNull(message = "El precio es obligatorio")
    @DecimalMin(value = "0.01", message = "El precio debe ser mayor a cero")
    private Double precio;

    @NotBlank(message = "La categoría es obligatoria")
    private String categoria;

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
```

#### 6.3. Repositorio: `com.tecsup.repository.PlatoRepository`
```java
package com.tecsup.repository;

import com.tecsup.model.Plato;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface PlatoRepository extends JpaRepository<Plato, Long> {}
```

#### 6.4. Servicio: `com.tecsup.services.PlatoService`
```java
package com.tecsup.services;

import com.tecsup.exception.ResourceNotFoundException;
import com.tecsup.model.Plato;
import com.tecsup.repository.PlatoRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class PlatoService {

    @Autowired
    private PlatoRepository repo;

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
```

#### 6.5. Controlador: `com.tecsup.controller.PlatoController`
```java
package com.tecsup.controller;

import com.tecsup.dto.PlatoDTO;
import com.tecsup.model.Plato;
import com.tecsup.services.PlatoService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/platos")
public class PlatoController {

    @Autowired
    private PlatoService service;

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
```

---

### Paso 7: Implementación del Microservicio `pedido-service` (Puerto 8083)

#### 7.1. Entidades: `Pedido` y `DetallePedido`
```java
package com.tecsup.model;

import jakarta.persistence.*;
import java.time.LocalDate;
import java.util.List;

@Entity
@Table(name = "pedido")
public class Pedido {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long idPedido;

    private LocalDate fecha;

    // Referencia lógica foránea al microservicio cliente
    @Column(name = "id_cliente", nullable = false)
    private Long idCliente;

    private Double total;

    @Column(length = 20)
    private String estado = "RECIBIDO"; // RECIBIDO, EN_PREPARACION, LISTO, ENTREGADO, CANCELADO

    @OneToMany(mappedBy = "pedido", cascade = CascadeType.ALL)
    private List<DetallePedido> detalles;

    public Pedido() {}

    public Long getIdPedido() { return idPedido; }
    public void setIdPedido(Long idPedido) { this.idPedido = idPedido; }
    public LocalDate getFecha() { return fecha; }
    public void setFecha(LocalDate fecha) { this.fecha = fecha; }
    public Long getIdCliente() { return idCliente; }
    public void setIdCliente(Long idCliente) { this.idCliente = idCliente; }
    public Double getTotal() { return total; }
    public void setTotal(Double total) { this.total = total; }
    public String getEstado() { return estado; }
    public void setEstado(String estado) { this.estado = estado; }
    public List<DetallePedido> getDetalles() { return detalles; }
    public void setDetalles(List<DetallePedido> detalles) { this.detalles = detalles; }
}
```

```java
package com.tecsup.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;

@Entity
@Table(name = "detalle_pedido")
public class DetallePedido {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long idDetalle;

    // Referencia lógica foránea al microservicio plato
    @Column(name = "id_plato", nullable = false)
    private Long idPlato;

    private Integer cantidad;
    private Double precio;

    @ManyToOne
    @JoinColumn(name = "id_pedido")
    @JsonIgnore
    private Pedido pedido;

    public DetallePedido() {}

    public Long getIdDetalle() { return idDetalle; }
    public void setIdDetalle(Long idDetalle) { this.idDetalle = idDetalle; }
    public Long getIdPlato() { return idPlato; }
    public void setIdPlato(Long idPlato) { this.idPlato = idPlato; }
    public Integer getCantidad() { return cantidad; }
    public void setCantidad(Integer cantidad) { this.cantidad = cantidad; }
    public Double getPrecio() { return precio; }
    public void setPrecio(Double precio) { this.precio = precio; }
    public Pedido getPedido() { return pedido; }
    public void setPedido(Pedido pedido) { this.pedido = pedido; }
}
```

#### 7.2. Repositorio: `com.tecsup.repository.PedidoRepository`
```java
package com.tecsup.repository;

import com.tecsup.model.Pedido;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface PedidoRepository extends JpaRepository<Pedido, Long> {}
```

#### 7.3. Servicio: `com.tecsup.services.PedidoService`
```java
package com.tecsup.services;

import com.tecsup.model.DetallePedido;
import com.tecsup.model.Pedido;
import com.tecsup.repository.PedidoRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class PedidoService {

    @Autowired
    private PedidoRepository repository;

    public List<Pedido> listar() {
        return repository.findAll();
    }

    public Optional<Pedido> buscarPorId(Long id) {
        return repository.findById(id);
    }

    public Pedido guardar(Pedido pedido) {
        if (pedido.getDetalles() != null) {
            for (DetallePedido detalle : pedido.getDetalles()) {
                detalle.setPedido(pedido);
            }
        }
        return repository.save(pedido);
    }

    public Pedido actualizar(Long id, Pedido pedido) {
        Pedido pedidoExistente = repository.findById(id)
                .orElseThrow(() -> new RuntimeException("Pedido no encontrado"));
        pedidoExistente.setFecha(pedido.getFecha());
        pedidoExistente.setIdCliente(pedido.getIdCliente());
        pedidoExistente.setTotal(pedido.getTotal());
        if (pedido.getEstado() != null) pedidoExistente.setEstado(pedido.getEstado());
        return repository.save(pedidoExistente);
    }

    public void eliminar(Long id) {
        repository.deleteById(id);
    }
}
```

#### 7.4. Controlador: `com.tecsup.controller.PedidoController`
```java
package com.tecsup.controller;

import com.tecsup.model.Pedido;
import com.tecsup.services.PedidoService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/pedidos")
public class PedidoController {

    @Autowired
    private PedidoService service;

    @GetMapping
    public List<Pedido> listar() {
        return service.listar();
    }

    @GetMapping("/{id}")
    public Pedido buscar(@PathVariable Long id) {
        return service.buscarPorId(id)
                .orElseThrow(() -> new RuntimeException("Pedido no encontrado"));
    }

    @PostMapping
    public Pedido guardar(@RequestBody Pedido pedido) {
        return service.guardar(pedido);
    }

    @PutMapping("/{id}")
    public Pedido actualizar(@PathVariable Long id, @RequestBody Pedido pedido) {
        return service.actualizar(id, pedido);
    }

    @DeleteMapping("/{id}")
    public String eliminar(@PathVariable Long id) {
        service.eliminar(id);
        return "Pedido eliminado correctamente";
    }
}
```

---

### Paso 8: Configuración de Perfiles `dev` y `prod`

Para replicar el laboratorio con perfiles múltiples:

1. Crear en MySQL las bases de datos de desarrollo y producción:
```sql
CREATE DATABASE IF NOT EXISTS bd_restaurante_cliente_dev;
CREATE DATABASE IF NOT EXISTS bd_restaurante_plato_dev;
CREATE DATABASE IF NOT EXISTS bd_restaurante_pedido_dev;
```

2. En `cliente-service/src/main/resources/`:
* **`application.yml`**:
```yaml
spring:
  profiles:
    active: dev
```
* **`application-dev.yml`**:
```yaml
server:
  port: 8081
spring:
  datasource:
    url: jdbc:mysql://localhost:3306/bd_restaurante_cliente_dev
    username: root
    password: 
  jpa:
    hibernate:
      ddl-auto: update
    show-sql: true
empresa:
  ambiente: desarrollo
```
* **`application-prod.yml`**:
```yaml
server:
  port: 9081
spring:
  datasource:
    url: jdbc:mysql://servidor-prod:3306/bd_restaurante_cliente
    username: root
    password: 
  jpa:
    hibernate:
      ddl-auto: update
    show-sql: false
empresa:
  ambiente: produccion
```
