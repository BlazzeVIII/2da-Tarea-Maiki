package com.supermercado.dto;
import com.supermercado.entity.*;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
public final class Respuestas {
 private Respuestas(){}
 public record CategoriaDto(Long id,String nombre,String descripcion) { public static CategoriaDto of(Categoria x){return new CategoriaDto(x.getId(),x.getNombre(),x.getDescripcion());} }
 public record ProveedorDto(Long id,String nombre,String telefono,String correo) { public static ProveedorDto of(Proveedor x){return new ProveedorDto(x.getId(),x.getNombre(),x.getTelefono(),x.getCorreo());} }
 public record ProductoDto(Long id,String nombre,BigDecimal precio,Integer stock,String codigoBarras,Long categoriaId,String categoria,Long proveedorId,String proveedor) { public static ProductoDto of(Producto x){return new ProductoDto(x.getId(),x.getNombre(),x.getPrecio(),x.getStock(),x.getCodigoBarras(),x.getCategoria().getId(),x.getCategoria().getNombre(),x.getProveedor().getId(),x.getProveedor().getNombre());} }
 public record ClienteDto(Long id,String nombre,String documento,String telefono) { public static ClienteDto of(Cliente x){return new ClienteDto(x.getId(),x.getNombre(),x.getDocumento(),x.getTelefono());} }
 public record DetalleDto(Long productoId,String producto,Integer cantidad,BigDecimal precioUnitario,BigDecimal subtotal) { public static DetalleDto of(DetalleVenta x){return new DetalleDto(x.getProducto().getId(),x.getProducto().getNombre(),x.getCantidad(),x.getPrecioUnitario(),x.getSubtotal());} }
 public record VentaDto(Long id,LocalDateTime fecha,BigDecimal total,Long clienteId,String cliente,List<DetalleDto> detalles) { public static VentaDto of(Venta x){return new VentaDto(x.getId(),x.getFecha(),x.getTotal(),x.getCliente().getId(),x.getCliente().getNombre(),x.getDetalles().stream().map(DetalleDto::of).toList());} }
}
