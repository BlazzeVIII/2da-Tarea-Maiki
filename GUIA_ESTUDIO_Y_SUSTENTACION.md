# Guía fácil de estudio y sustentación

La meta de esta guía es que puedas comprender el código y explicar con tus propias palabras qué construiste. Practica mirando los archivos y probando los endpoints; no memorices una explicación que no puedas relacionar con el código.

## 1. La idea en una frase

> Es una aplicación para gestionar el inventario y las ventas de un supermercado de barrio. El navegador y Postman consumen una API REST hecha con Spring Boot; la API valida reglas y guarda los datos en MySQL mediante JPA.

## 2. Las piezas con una analogía

Piensa en un restaurante:

- **Controller (recepción):** recibe la solicitud HTTP y entrega una respuesta con el código adecuado.
- **DTO (formato del pedido/recibo):** define qué datos se aceptan y cuáles se devuelven.
- **Service (encargado):** aplica reglas del negocio; por ejemplo, comprueba stock y calcula una venta.
- **Repository (persona que busca en archivo):** consulta/guarda entidades en la base.
- **Entity (ficha persistente):** representa una tabla y sus relaciones.
- **MySQL (archivo permanente):** conserva la información aunque se cierre la aplicación.
- **Exception handler (mesa de ayuda):** convierte errores en respuestas JSON entendibles.

Una petición típica sigue este recorrido:

```text
Formulario o Postman → Controller → DTO/validación → Service → Repository → MySQL
                                                        ↓
Navegador recibe JSON ← Controller ← DTO de respuesta ← Service
```

## 3. Qué estudiar en cada archivo

### `SupermercadoApplication.java`

Contiene `main`, el punto donde arranca Java. `@SpringBootApplication` activa la configuración y el escaneo de componentes Spring.

### Entidades

Abre `entity/Producto.java`, `Venta.java` y `DetalleVenta.java`:

- `@Entity` indica que la clase se guarda como entidad JPA.
- `@Id` y `@GeneratedValue` declaran una clave primaria autogenerada.
- `@Column(nullable=false)` impone una columna obligatoria.
- `@ManyToOne` expresa que varios productos pueden usar una categoría o proveedor.
- `@OneToMany` expresa que una venta contiene varios detalles.
- `@JoinColumn` nombra la columna de clave foránea.
- `BigDecimal` se usa para dinero, evitando errores de precisión típicos de `float`/`double`.

Lombok (`@Getter`, `@Setter`, `@NoArgsConstructor`) genera métodos repetitivos al compilar; por eso se necesita la dependencia de Lombok indicada en `pom.xml`.

### DTOs

`ProductoRequest`, `ClienteRequest` y `VentaRequest` son datos que el cliente puede enviar. `@NotBlank`, `@NotNull`, `@Min`, `@DecimalMin` y `@Email` expresan reglas de entrada. El controlador usa `@Valid` para ejecutarlas.

`Respuestas.java` agrupa DTOs de salida. Sus métodos `of(...)` convierten entidades a respuestas. Así no se envían entidades JPA directamente ni se filtran relaciones internas sin control.

### Repositorios

Cada interfaz extiende `JpaRepository<Entidad, Long>`. Spring Data genera operaciones comunes (`findAll`, `findById`, `save`, `deleteById`) y también interpreta nombres como `findByNombreContainingIgnoreCase` o `findByStockLessThanEqual` para crear consultas.

### Servicio

`GestionService.java` es el centro de la lógica. Los controladores no calculan ventas ni acceden directamente a MySQL; llaman al servicio. El constructor recibe los repositorios por inyección de dependencias.

`@Transactional` hace que una operación compuesta se confirme junta o se revierta si falla. Lo más importante para sustentar es `crearVenta`: busca cliente/productos, valida existencias, calcula importes, resta stock y guarda venta con detalles.

### Controladores

Por ejemplo, `ProductoController.java` declara `@RequestMapping("/api/productos")` y métodos con `@GetMapping`, `@PostMapping`, `@PutMapping` y `@DeleteMapping`. Cada anotación conecta un verbo HTTP y una ruta con un método Java.

El controlador usa `ResponseEntity.status(HttpStatus.CREATED)` para crear con `201` y `ResponseEntity.noContent()` para eliminar con `204`.

### Manejo de errores

`GlobalExceptionHandler.java` lleva `@RestControllerAdvice`. Captura errores de validación, recursos ausentes, conflictos y restricciones SQL, y devuelve `ApiError` como JSON. Así los errores mantienen una forma coherente.

### Frontend

- `index.html` define secciones, tabla y formularios.
- `styles.css` define el aspecto, colores, tipografía y adaptación móvil.
- `app.js` llama la API usando `fetch`, convierte formularios a JSON, actualiza la tabla y muestra errores/respuestas.

La línea `const api='/api'` usa rutas relativas: el navegador se conecta al mismo Spring Boot que sirve la página.

### `pom.xml` y configuración

`pom.xml` declara Spring Web, Spring Data JPA, Validation, el controlador JDBC de MySQL y Lombok. `application.properties` indica que la web escucha en 8080 y que MySQL está en 3306. El puerto web y el de base de datos tienen funciones distintas.

## 4. Código más importante: la venta

La idea del código en `GestionService.crearVenta` puede explicarse así:

```java
// 1. Comprobar que exista el cliente.
// 2. Sumar las cantidades si el mismo producto aparece más de una vez.
// 3. Cargar cada producto y comprobar que alcance el stock.
// 4. Calcular precioUnitario × cantidad y sumar el total.
// 5. Restar stock y guardar venta con sus detalles.
```

Es importante porque es la regla de negocio con más pasos y modifica varias filas relacionadas. La transacción ayuda a evitar que quede una venta guardada a medias: o se confirma la operación completa, o se revierte ante un error.

Al eliminar una venta, `borrarVenta` recorre sus detalles, devuelve la cantidad al stock y elimina la venta. Los detalles se eliminan por la configuración `cascade`/`orphanRemoval` de la relación.

## 5. Cómo demostrarlo en una sustentación

Prepara una demostración corta con MySQL iniciado y el servidor Spring Boot ya corriendo:

1. Muestra `pom.xml` y explica que Maven descarga dependencias.
2. Muestra en Workbench la base `supermercado_barrio` y las tablas creadas por Hibernate.
3. En Postman crea categoría y proveedor, y enseña que la respuesta incluye sus IDs.
4. Crea producto usando esos IDs y consulta `GET /api/productos`.
5. Registra un cliente.
6. Registra una venta con cantidad disponible y muestra el total devuelto.
7. Consulta productos de nuevo y muestra que el stock bajó.
8. Intenta vender más unidades que las disponibles y enseña el `409` con el mensaje JSON.
9. Si el tiempo alcanza, elimina una venta y confirma que las unidades vuelven al inventario.
10. Abre el panel en `localhost:8080` para mostrar que la interfaz consulta la misma API.

Evita depender de IDs asumidos. Copia los IDs de las respuestas reales de tu base.

## 6. Guion breve para explicar el proyecto

Adáptalo a tu manera de hablar y asegúrate de poder señalar cada parte en el código:

> “Mi proyecto es un sistema básico para un supermercado de barrio. Está hecho con Java 17 y Spring Boot. Tiene una API REST organizada en controladores, servicios y repositorios. Los controladores reciben peticiones HTTP, los servicios contienen las reglas y los repositorios se encargan de persistir los datos en MySQL con JPA.
>
> El modelo incluye categorías, proveedores, productos, clientes y ventas. Un producto pertenece a una categoría y un proveedor. Una venta pertenece a un cliente y tiene detalles que guardan producto, cantidad, precio unitario y subtotal.
>
> La regla principal está en el servicio de ventas: primero verifica cliente, producto y stock; usa el precio vigente para calcular el subtotal y el total; descuenta las existencias y persiste todo en una transacción. Si el stock no alcanza, responde con un conflicto y no completa la venta. Al borrar una venta, devuelve las unidades al inventario.
>
> Los DTOs controlan los datos que entran y salen, las validaciones rechazan entradas incorrectas y un manejador central devuelve errores JSON. También preparé una interfaz web sencilla que consume la misma API, y la colección de Postman permite probar las rutas.”

Si usaste esta guía para estudiar, puedes decir honestamente que desarrollaste el proyecto con apoyo de documentación/herramientas y explicar qué partes comprendes y cómo funciona cada una. No afirmes haber implementado funciones que no están en el código.

## 7. Preguntas probables y respuestas sencillas

**¿Qué diferencia hay entre controller, service y repository?**  
Controller recibe HTTP; service aplica reglas; repository consulta y guarda datos.

**¿Por qué no devuelves directamente las entidades?**  
Uso DTOs para controlar los campos enviados, validar las entradas y no acoplar la respuesta HTTP a las relaciones internas de JPA.

**¿Qué hace JPA?**  
Es la capa estándar de persistencia de Java. Hibernate implementa JPA y traduce entidades/operaciones a SQL.

**¿Qué es una clave foránea?**  
Es una columna que referencia la fila de otra tabla. Por ejemplo, `productos.categoria_id` apunta a una categoría.

**¿Por qué `BigDecimal` para el precio?**  
Porque representa decimales de dinero con precisión decimal, apropiado para cálculos monetarios.

**¿Qué pasa si intento vender más de lo que hay?**  
El servicio compara cantidad solicitada contra stock disponible y lanza un conflicto `409`; no debe guardarse la venta.

**¿Cómo se conserva la venta con sus detalles?**  
La venta tiene una relación uno a muchos con detalles y `cascade = ALL`, por lo que guardar la venta guarda también sus detalles.

**¿Qué hace `@Transactional`?**  
Agrupa las escrituras de la operación. Si ocurre una excepción, la transacción puede revertirlas para evitar cambios parciales.

**¿Cuál es la diferencia entre 3306 y 8080?**  
3306 es el puerto de MySQL; 8080 es el servidor Spring Boot que entrega la página y la API.

**¿Qué papel tiene Workbench?**  
Es una interfaz gráfica para conectarse a MySQL, ejecutar SQL y revisar tablas; la base la sirve MySQL Server.

**¿Qué falta para producción?**  
Autenticación/autorización, migraciones formales, gestión segura de secretos, pruebas automatizadas, paginación y manejo más avanzado de concurrencia y auditoría.

**¿La interfaz administra todo?**  
No. El panel permite crear categorías, proveedores, productos y clientes, registrar ventas con varios productos y consultar inventario. La API ofrece además operaciones de actualización y eliminación que puedes probar desde Postman.

## 8. Rutina de repaso

1. Explica el diagrama sin leerlo.
2. Abre `ProductoController`, `GestionService.crearVenta`, `Venta` y `VentaRequest`; describe cómo viaja la petición.
3. Prueba una venta correcta y otra con stock insuficiente.
4. Explica qué tablas y claves foráneas cambian.
5. Practica las preguntas de la sección anterior usando ejemplos del proyecto.

La mejor sustentación es una explicación fiel al código, acompañada de una demostración que puedas ejecutar y corregir si cambian los IDs.
