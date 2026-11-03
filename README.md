# Supermercado de barrio

API REST y panel web sencillo para administrar categorías, proveedores, productos, clientes y ventas. Está pensado para Java 17, Spring Boot 3, Maven y MySQL.

## Requisitos

- Java 17 o superior
- Maven 3.6+
- MySQL 8 (en ejecución)

## Ejecutar

1. Crea la base de datos ejecutando `database/crear_base.sql` en MySQL. La aplicación también usa `createDatabaseIfNotExist=true`.
2. En la carpeta del proyecto configura la contraseña si tu usuario local la tiene:

   **PowerShell**
   ```powershell
   $env:DB_USER="root"
   $env:DB_PASSWORD="tu_clave"
   ```

   Si tu configuración difiere, establece `DB_URL`, `DB_USER` y `DB_PASSWORD`. Los valores por defecto apuntan a `localhost:3306`, usuario `root` sin contraseña y base `supermercado_barrio`.

3. Inicia la aplicación:
   ```powershell
   mvn spring-boot:run
   ```
4. Abre [http://localhost:8080](http://localhost:8080). La página principal sirve el panel web y consume la API en el mismo origen.

Hibernate crea/actualiza las tablas al arrancar (`spring.jpa.hibernate.ddl-auto=update`). Para un entorno real, reemplaza esto por migraciones versionadas.

## Estructura

```text
src/main/java/com/supermercado/
  controller/  Rutas HTTP y códigos de respuesta
  dto/         Objetos de entrada y salida; no se devuelven entidades
  entity/      Modelo JPA y relaciones
  exception/   Errores JSON centralizados
  repository/  Consultas Spring Data JPA
  service/     Reglas y transacciones de negocio
src/main/resources/
  application.properties
  static/      index.html, styles.css y app.js
database/      Script de creación de base
postman/       Colección de ejemplo
```

## API

| Recurso | Rutas principales |
|---|---|
| Categorías | `GET/POST /api/categorias`, `PUT/DELETE /api/categorias/{id}` |
| Proveedores | `GET/POST /api/proveedores`, `PUT/DELETE /api/proveedores/{id}` |
| Productos | `GET/POST /api/productos`, `GET/PUT/DELETE /api/productos/{id}` |
| Productos extra | `/api/productos/categoria/{categoriaId}`, `/api/productos/buscar?nombre=arroz`, `/api/productos/stock-bajo?limite=10` |
| Clientes | `GET/POST /api/clientes`, `PUT/DELETE /api/clientes/{id}`, `GET /api/clientes/documento/{documento}` |
| Ventas | `GET/POST /api/ventas`, `GET/DELETE /api/ventas/{id}`, `/api/ventas/cliente/{clienteId}`, `/api/ventas/fecha?inicio=2026-01-01&fin=2026-01-31` |

Las altas devuelven `201`, consultas y actualizaciones `200`, eliminaciones `204`. Validaciones devuelven `400`, recursos inexistentes `404` y restricciones/conflictos `409`; los errores usan JSON con `fecha`, `estado`, `error`, `mensaje` y `detalles`.

### Orden sugerido para Postman

Importa `postman/Supermercado-Barrio.postman_collection.json`. Crea primero categoría y proveedor, luego producto, cliente y venta. Los ejemplos usan identificadores `1`, ajústalos a la respuesta que recibas.

Ejemplo de alta de venta:

```json
{
  "clienteId": 1,
  "detalles": [
    {"productoId": 1, "cantidad": 2}
  ]
}
```

La venta usa el precio actual de cada producto, calcula subtotales y total y descuenta stock dentro de una transacción. Líneas repetidas para un producto se suman antes de validar el stock. Al eliminar una venta se restituye el stock. No se permite borrar categorías o proveedores que tengan productos asociados.

## Panel web

El panel de `/` muestra el inventario, las existencias bajas y conteos. Permite crear categorías, proveedores, productos y clientes; al crear categorías/proveedores quedan disponibles inmediatamente en los selectores de productos. El formulario de venta permite agregar varias líneas de productos y calcula una estimación antes de enviar la venta. La misma API sigue disponible en Postman.

## Alcance

No incluye autenticación ni autorización, según el alcance del proyecto. La configuración de conexión debe ajustarse a las credenciales MySQL locales antes de iniciar si difieren de los valores por defecto.
