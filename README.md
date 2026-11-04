# Microservicios de restaurante

Implementación de la guía de laboratorio con Java 21, Spring Boot 4.1.2 y Maven. Cada servicio es una aplicación independiente y administra exclusivamente su base de datos; el POM de la raíz permite compilar los tres juntos.

| Servicio | Puerto dev | Puerto prod | Base de datos dev | Ruta REST |
| --- | --- | --- | --- | --- |
| Cliente | 8081 | 9081 | bd_cliente_restaurante | /api/clientes |
| Plato | 8082 | 9082 | bd_plato | /api/platos |
| Pedido | 8083 | 9083 | bd_pedido_restaurante | /api/pedidos |

## Pasos implementados

1. Se verificaron las tres bases ya creadas en MySQL de XAMPP.
2. Se conservaron los tres proyectos existentes y se añadió el agregador Maven `microservicios-restaurante` en la raíz.
3. Las clases de arranque están en `com.tecsup`; las capas se encuentran en `controller`, `dto`, `exception`, `model`, `repository` y `services`.
4. Se reemplazaron los archivos properties por YAML, corrigiendo la URL de pedidos.
5. Clientes: entidad, DTO validado, repositorio, servicio, controlador CRUD y excepciones 400/404.
6. Platos: las mismas capas, con precio positivo y estado predeterminado A.
7. Pedidos: cabecera y detalles relacionados, DTOs con validación anidada, CRUD y eliminación de detalles huérfanos al actualizar. El servidor calcula el total sumando cantidad × precio; no se envía el total en la solicitud. PUT reemplaza todos los detalles.
8. Los tres servicios tienen perfiles dev y prod. Dev es el predeterminado y usa las bases existentes del paso 1. Para usar los nombres alternativos del paso 8, crea esos esquemas y configura DB_URL por servicio.

## Ejecutar

Requisitos: JDK 21, Maven y MySQL de XAMPP encendido en el puerto 3306. En Windows configura JAVA_HOME si todavía no existe:

```powershell
$env:JAVA_HOME = 'C:\Program Files\Java\jdk-21.0.12.1'
$env:PATH = "$env:JAVA_HOME\bin;$env:PATH"
mvn test
```

Abre tres terminales en la carpeta raíz y ejecuta un comando en cada una:

```powershell
mvn -f microservicios-cliente/pom.xml spring-boot:run
mvn -f microservicios-plato/pom.xml spring-boot:run
mvn -f microservicios-pedido/pom.xml spring-boot:run
```

Dev usa root con contraseña vacía, como la guía. Puedes configurar DB_URL, DB_USERNAME, DB_PASSWORD y SERVER_PORT mediante variables de entorno. Hibernate crea/actualiza las tablas al iniciar en dev.

Para prod define DB_URL, DB_USERNAME y DB_PASSWORD y activa SPRING_PROFILES_ACTIVE=prod. Las tablas deben existir: prod usa `ddl-auto: validate` y no modifica automáticamente el esquema. Los puertos son 9081, 9082 y 9083.

## Probar con Postman

Importa `postman/restaurante.postman_collection.json` y ejecuta la colección en orden. Crea cliente, plato y pedido, guarda sus IDs en variables de colección, comprueba consultas y actualizaciones, rechaza datos inválidos y elimina los registros de prueba. Los puertos se configuran con las variables clienteUrl, platoUrl y pedidoUrl.

Cada API ofrece GET y POST en su ruta base, y GET, PUT y DELETE en `/{id}`. POST devuelve 201, datos inválidos 400 y un ID inexistente 404. Clientes requiere estado A o I; platos permite omitirlo. Pedidos permite RECIBIDO, EN_PREPARACION, LISTO, ENTREGADO y CANCELADO.

Los IDs de cliente y plato en pedidos son referencias lógicas, como indica la guía: no hay relaciones JPA entre bases ni llamadas HTTP entre servicios. El precio del detalle lo proporciona el consumidor. La colección encadena los IDs obtenidos para probar el flujo completo.

`mvn test` ejecuta pruebas HTTP de CRUD, validación y 404 con H2 en memoria, sin alterar MySQL. En pedidos también comprueba el cálculo del total y la serialización de detalles. La colección Postman permite repetir el flujo con MySQL real.
