# Guía definitiva de funcionamiento

Esta guía explica cómo preparar, ejecutar, probar y entender el proyecto **Supermercado de Barrio**. El proyecto es una aplicación local de aprendizaje: una API REST en Java/Spring Boot, una base de datos MySQL y una página web que consume esa API.

## 1. ¿Qué hace el sistema?

Permite mantener el catálogo del supermercado y registrar ventas:

- **Categorías:** agrupan productos, por ejemplo Abarrotes o Bebidas.
- **Proveedores:** identifican quién suministra los productos.
- **Productos:** guardan nombre, precio, existencias, código de barras, categoría y proveedor.
- **Clientes:** guardan nombre, documento y teléfono.
- **Ventas:** relacionan un cliente con uno o varios detalles de venta.
- **Detalles de venta:** guardan el producto, cantidad, precio unitario usado y subtotal.

El panel facilita revisar productos y stock bajo, crear categorías, proveedores, productos y clientes, y registrar ventas con varios productos. Al crear una categoría o proveedor, aparece inmediatamente en los selectores del formulario de producto. Postman sigue disponible para las mismas operaciones de la API y para las funciones que no tienen formulario en la página, como actualizar y borrar.

No hay cuentas, inicio de sesión ni roles: es un ejemplo de administración local, no un sistema multiusuario protegido.

## 2. Cómo se conectan las partes

```text
Navegador (HTML/CSS/JS) ──HTTP/JSON──> Controladores Spring Boot
                                         │
                                         v
                                      Servicios
                                   (reglas de negocio)
                                         │
                                         v
                                      Repositorios
                                   (Spring Data JPA)
                                         │
                                         v
                                    Hibernate/JDBC
                                         │
                                         v
                                  MySQL (puerto 3306)
```

- **Navegador, `localhost:8080`:** Spring Boot sirve `index.html`, `styles.css` y `app.js`, y también recibe solicitudes a `/api/...`.
- **MySQL, `localhost:3306`:** guarda los datos de manera persistente. No es el puerto de la página.
- **JSON:** formato con el que la página y Postman envían y reciben información.
- **JPA/Hibernate:** convierte objetos Java a filas de tablas y viceversa.
- **Maven:** descarga dependencias y ejecuta/compila la aplicación.

La página y la API usan el mismo servidor/origen, por eso el frontend hace peticiones como `/api/productos` sin configurar otra dirección.

## 3. Preparar y arrancar

### Requisitos

1. Instala Java 17 o superior.
2. Instala Maven.
3. Instala e inicia MySQL Server. MySQL Workbench es la herramienta gráfica para conectarse y ejecutar SQL; Workbench no reemplaza al servidor.
4. En Workbench comprueba la conexión local. El proyecto espera por defecto `localhost`, puerto `3306`, usuario `root` y contraseña vacía.

Comprueba Java y Maven en una terminal nueva:

```powershell
java -version
mvn -version
```

Ambos deben mostrar Java 17 o superior.

### Crear la base de datos

En MySQL Workbench, abre una pestaña de consulta, abre/pega el contenido de `database/crear_base.sql` y ejecuta el script. También la URL JDBC pide crear la base si no existe y el usuario tiene permisos.

El script crea `supermercado_barrio`. Al iniciar, Hibernate crea/actualiza las tablas por `spring.jpa.hibernate.ddl-auto=update`.

### Configurar credenciales

Desde PowerShell, en la carpeta del proyecto, establece las credenciales para la sesión actual:

```powershell
$env:DB_URL="jdbc:mysql://localhost:3306/supermercado_barrio?createDatabaseIfNotExist=true&useSSL=false&serverTimezone=UTC&allowPublicKeyRetrieval=true"
$env:DB_USER="root"
$env:DB_PASSWORD="TU_CONTRASEÑA_MYSQL"
```

Usa la contraseña que funciona en Workbench. No la publiques ni la subas al repositorio. Estas variables desaparecen al cerrar esa terminal; vuelve a establecerlas si abres otra.

### Iniciar y abrir

En la terminal de VS Code, desde la carpeta donde está `pom.xml`:

```powershell
mvn spring-boot:run
```

Espera el mensaje de inicio de Spring Boot y deja esa terminal abierta. Luego visita `http://localhost:8080`. Para parar el servidor, vuelve a la terminal y presiona `Ctrl+C`.

## 4. Cómo se prueban las funciones

Importa `postman/Supermercado-Barrio.postman_collection.json` en Postman. Dirección base: `http://localhost:8080`.

### Orden recomendado de datos

Las relaciones exigen que existan primero los registros referenciados:

1. Crea categoría con `POST /api/categorias`.
2. Crea proveedor con `POST /api/proveedores`.
3. Crea producto usando los IDs reales de categoría y proveedor.
4. Crea cliente.
5. Registra la venta usando IDs reales de cliente y producto.

El ID es el campo `id` devuelto en la respuesta JSON. No asumas que será `1` si la base ya tuvo registros.

### Ejemplos de JSON

Categoría (`POST /api/categorias`):

```json
{"nombre":"Abarrotes","descripcion":"Productos de despensa"}
```

Proveedor (`POST /api/proveedores`):

```json
{"nombre":"Distribuciones Central","telefono":"3001234567","correo":"ventas@central.com"}
```

Producto (`POST /api/productos`):

```json
{"nombre":"Arroz 1 kg","precio":4500,"stock":30,"codigoBarras":"770000000001","categoriaId":1,"proveedorId":1}
```

Cliente (`POST /api/clientes`):

```json
{"nombre":"Ana Pérez","documento":"10203040","telefono":"3001234567"}
```

Venta (`POST /api/ventas`):

```json
{"clienteId":1,"detalles":[{"productoId":1,"cantidad":2}]}
```

Reemplaza esos IDs de ejemplo con los IDs que devolvió tu instancia.

### Consultar, actualizar y eliminar

- Listar recursos: `GET /api/productos`, `GET /api/clientes`, `GET /api/categorias`, `GET /api/proveedores`, `GET /api/ventas`.
- Consultar por ID: `GET /api/productos/{id}`, `GET /api/clientes/{id}`, `GET /api/categorias/{id}`, `GET /api/proveedores/{id}`, `GET /api/ventas/{id}`.
- Actualizar: `PUT /api/{recurso}/{id}` con el JSON del recurso. Productos, por ejemplo, requieren todos los campos del `ProductoRequest`.
- Eliminar: `DELETE /api/{recurso}/{id}`. La respuesta correcta es `204 No Content`, por eso puede no tener cuerpo.
- Búsquedas extra: `GET /api/productos/categoria/{categoriaId}`, `GET /api/productos/buscar?nombre=arroz`, `GET /api/productos/stock-bajo?limite=10`, `GET /api/clientes/documento/{documento}`, `GET /api/ventas/cliente/{clienteId}`, `GET /api/ventas/fecha?inicio=2026-01-01&fin=2026-01-31`.

## 5. Lógica de negocio

### Crear producto

El controlador valida el JSON y se lo pasa al servicio. El servicio busca categoría y proveedor usando los IDs. Si existen, los asigna al producto y el repositorio lo guarda. Si no existen, devuelve `404`. Precio debe ser mayor que cero, stock no puede ser negativo, y nombre/código son obligatorios. El código de barras es único en la base.

### Registrar venta

El método `crearVenta` de `GestionService` ejecuta estos pasos dentro de una transacción:

1. Busca al cliente; si no existe, lanza error `404`.
2. Junta cantidades de líneas repetidas del mismo producto.
3. Busca cada producto y comprueba que el stock alcance para el total solicitado.
4. Toma el precio actual del producto, calcula `subtotal = precio × cantidad` y suma los subtotales.
5. Prepara la venta y sus detalles, reduce existencias y guarda la venta. `cascade = ALL` guarda los detalles con ella.
6. Si se produce un error durante la transacción, Spring revierte los cambios de base de datos.

La venta guarda el precio unitario usado en ese momento; cambiar después el precio del producto no reescribe ventas antiguas.

### Eliminar venta

`borrarVenta` carga la venta, suma a cada producto las unidades que se vendieron y elimina la venta. Por la relación `orphanRemoval` y el cascade, se eliminan los detalles. Las modificaciones se confirman al finalizar la transacción.

### Restricciones y errores

- No se puede borrar una categoría o proveedor con productos asociados; responde `409 Conflict`.
- No se puede vender sin detalles ni cantidades menores que 1; responde `400 Bad Request`.
- Si el stock no alcanza, responde `409 Conflict` con explicación.
- Si no existe un recurso solicitado, responde `404 Not Found`.
- En errores de validación, la respuesta incluye los campos incorrectos en `detalles`.
- Los errores se construyen como JSON por `GlobalExceptionHandler`.

## 6. Endpoints y códigos HTTP

| Acción | Código típico |
|---|---:|
| Consultar o actualizar | `200 OK` |
| Crear | `201 Created` |
| Eliminar correctamente | `204 No Content` |
| Datos inválidos | `400 Bad Request` |
| ID inexistente | `404 Not Found` |
| Conflicto de negocio, duplicado o asociación | `409 Conflict` |

## 7. Dónde se guardan los datos

Los registros se escriben en MySQL en la base `supermercado_barrio`, en las tablas `categorias`, `proveedores`, `productos`, `clientes`, `ventas` y `detalles_venta`. Cerrar el navegador o detener Spring Boot no borra los registros. La base de datos se mantiene aparte del proyecto en el servicio MySQL.

`ddl-auto=update` es cómodo para este proyecto de clase porque sincroniza el esquema al arrancar. En producción se prefieren migraciones controladas. Para reiniciar datos de prueba, usa Workbench con cuidado y elimina filas/tablas según el orden de relaciones; no borres la base si necesitas conservar información.

## 8. Archivos principales

- `pom.xml`: Java, versión de Spring Boot y dependencias.
- `application.properties`: puerto web y configuración de conexión.
- `SupermercadoApplication.java`: punto de entrada.
- `entity/`: entidades/tablas y relaciones JPA.
- `repository/`: consultas y operaciones de persistencia.
- `service/GestionService.java`: reglas del sistema y transacciones.
- `controller/`: rutas HTTP y códigos de respuesta.
- `dto/`: formatos de entrada y salida; evita exponer directamente las entidades.
- `exception/`: respuestas JSON centralizadas para errores.
- `resources/static/`: interfaz web.
- `database/crear_base.sql`: creación de la base.
- `postman/`: colección para probar la API.

## 9. Límites conocidos del prototipo

- No implementa login, roles ni permisos.
- El panel permite crear categorías, proveedores, productos y clientes; no incluye formularios para editar o borrar esos registros.
- La venta permite varias líneas, pero no presenta una pantalla de historial o edición de ventas.
- Los IDs se asignan en MySQL y se usan internamente; la página presenta nombres en menús de selección.
- No hay paginación ni reportes avanzados.
- La propiedad `ddl-auto=update` es para desarrollo/aprendizaje, no una estrategia de despliegue real.
