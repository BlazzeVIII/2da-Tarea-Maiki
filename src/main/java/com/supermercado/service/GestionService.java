package com.supermercado.service;

import com.supermercado.dto.*;
import com.supermercado.dto.Respuestas.*;
import com.supermercado.entity.*;
import com.supermercado.exception.*;
import com.supermercado.repository.*;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.time.*;
import java.math.BigDecimal;
import java.util.*;

@Service @Transactional
public class GestionService {
 private final CategoriaRepository categorias; private final ProveedorRepository proveedores; private final ProductoRepository productos; private final ClienteRepository clientes; private final VentaRepository ventas;
 public GestionService(CategoriaRepository c,ProveedorRepository p,ProductoRepository pr,ClienteRepository cl,VentaRepository v){categorias=c;proveedores=p;productos=pr;clientes=cl;ventas=v;}
 private <T> T required(Optional<T> x,String tipo,Long id){return x.orElseThrow(()->new RecursoNoEncontradoException(tipo+" con id "+id+" no existe."));}
 public List<CategoriaDto> categorias(){return categorias.findAll().stream().map(CategoriaDto::of).toList();}
 public CategoriaDto categoria(Long id){return CategoriaDto.of(required(categorias.findById(id),"Categoría",id));}
 public CategoriaDto crearCategoria(CategoriaRequest r){Categoria x=new Categoria();x.setNombre(r.nombre().trim());x.setDescripcion(r.descripcion());return CategoriaDto.of(categorias.save(x));}
 public CategoriaDto actualizarCategoria(Long id,CategoriaRequest r){Categoria x=required(categorias.findById(id),"Categoría",id);x.setNombre(r.nombre().trim());x.setDescripcion(r.descripcion());return CategoriaDto.of(x);}
 public void borrarCategoria(Long id){required(categorias.findById(id),"Categoría",id);if(productos.existsByCategoriaId(id))throw new ConflictoException("No se puede eliminar la categoría porque tiene productos asociados.");categorias.deleteById(id);}
 public List<ProveedorDto> proveedores(){return proveedores.findAll().stream().map(ProveedorDto::of).toList();}
 public ProveedorDto proveedor(Long id){return ProveedorDto.of(required(proveedores.findById(id),"Proveedor",id));}
 public ProveedorDto crearProveedor(ProveedorRequest r){Proveedor x=new Proveedor();setProveedor(x,r);return ProveedorDto.of(proveedores.save(x));}
 public ProveedorDto actualizarProveedor(Long id,ProveedorRequest r){Proveedor x=required(proveedores.findById(id),"Proveedor",id);setProveedor(x,r);return ProveedorDto.of(x);}
 private void setProveedor(Proveedor x,ProveedorRequest r){x.setNombre(r.nombre().trim());x.setTelefono(r.telefono());x.setCorreo(r.correo());}
 public void borrarProveedor(Long id){required(proveedores.findById(id),"Proveedor",id);if(productos.existsByProveedorId(id))throw new ConflictoException("No se puede eliminar el proveedor porque tiene productos asociados.");proveedores.deleteById(id);}
 public List<ProductoDto> productos(){return productos.findAll().stream().map(ProductoDto::of).toList();}
 public ProductoDto producto(Long id){return ProductoDto.of(required(productos.findById(id),"Producto",id));}
 public List<ProductoDto> porCategoria(Long id){return productos.findByCategoriaId(id).stream().map(ProductoDto::of).toList();}
 public List<ProductoDto> buscar(String q){return productos.findByNombreContainingIgnoreCase(q).stream().map(ProductoDto::of).toList();}
 public List<ProductoDto> stockBajo(int limite){return productos.findByStockLessThanEqual(limite).stream().map(ProductoDto::of).toList();}
 public ProductoDto crearProducto(ProductoRequest r){Producto x=new Producto();setProducto(x,r);return ProductoDto.of(productos.save(x));}
 public ProductoDto actualizarProducto(Long id,ProductoRequest r){Producto x=required(productos.findById(id),"Producto",id);setProducto(x,r);return ProductoDto.of(x);}
 private void setProducto(Producto x,ProductoRequest r){x.setNombre(r.nombre().trim());x.setPrecio(r.precio());x.setStock(r.stock());x.setCodigoBarras(r.codigoBarras().trim());x.setCategoria(required(categorias.findById(r.categoriaId()),"Categoría",r.categoriaId()));x.setProveedor(required(proveedores.findById(r.proveedorId()),"Proveedor",r.proveedorId()));}
 public void borrarProducto(Long id){required(productos.findById(id),"Producto",id);productos.deleteById(id);}
 public List<ClienteDto> clientes(){return clientes.findAll().stream().map(ClienteDto::of).toList();}
 public ClienteDto cliente(Long id){return ClienteDto.of(required(clientes.findById(id),"Cliente",id));}
 public ClienteDto clienteDocumento(String doc){return ClienteDto.of(clientes.findByDocumento(doc).orElseThrow(()->new RecursoNoEncontradoException("Cliente con documento "+doc+" no existe.")));}
 public ClienteDto crearCliente(ClienteRequest r){Cliente x=new Cliente();setCliente(x,r);return ClienteDto.of(clientes.save(x));}
 public ClienteDto actualizarCliente(Long id,ClienteRequest r){Cliente x=required(clientes.findById(id),"Cliente",id);setCliente(x,r);return ClienteDto.of(x);}
 private void setCliente(Cliente x,ClienteRequest r){x.setNombre(r.nombre().trim());x.setDocumento(r.documento().trim());x.setTelefono(r.telefono());}
 public void borrarCliente(Long id){required(clientes.findById(id),"Cliente",id);clientes.deleteById(id);}
 public VentaDto crearVenta(VentaRequest r){Cliente c=required(clientes.findById(r.clienteId()),"Cliente",r.clienteId());Venta v=new Venta();v.setCliente(c);v.setFecha(LocalDateTime.now());v.setTotal(BigDecimal.ZERO);Map<Long,Integer> cantidades=new LinkedHashMap<>();for(var linea:r.detalles())cantidades.merge(linea.productoId(),linea.cantidad(),Integer::sum);BigDecimal total=BigDecimal.ZERO;for(var e:cantidades.entrySet()){Producto p=required(productos.findById(e.getKey()),"Producto",e.getKey());if(p.getStock()<e.getValue())throw new ConflictoException("Stock insuficiente para "+p.getNombre()+". Disponible: "+p.getStock()+".");DetalleVenta d=new DetalleVenta();d.setVenta(v);d.setProducto(p);d.setCantidad(e.getValue());d.setPrecioUnitario(p.getPrecio());d.setSubtotal(p.getPrecio().multiply(BigDecimal.valueOf(e.getValue())));total=total.add(d.getSubtotal());v.getDetalles().add(d);p.setStock(p.getStock()-e.getValue());}v.setTotal(total);return VentaDto.of(ventas.save(v));}
 public List<VentaDto> ventas(){return ventas.findAll().stream().map(VentaDto::of).toList();}
 public VentaDto venta(Long id){return VentaDto.of(required(ventas.findById(id),"Venta",id));}
 public List<VentaDto> ventasCliente(Long id){required(clientes.findById(id),"Cliente",id);return ventas.findByClienteId(id).stream().map(VentaDto::of).toList();}
 public List<VentaDto> ventasFecha(LocalDate inicio,LocalDate fin){if(fin.isBefore(inicio))throw new ConflictoException("La fecha fin debe ser igual o posterior a inicio.");return ventas.findByFechaBetween(inicio.atStartOfDay(),fin.plusDays(1).atStartOfDay().minusNanos(1)).stream().map(VentaDto::of).toList();}
 public void borrarVenta(Long id){Venta v=required(ventas.findById(id),"Venta",id);for(DetalleVenta d:v.getDetalles())d.getProducto().setStock(d.getProducto().getStock()+d.getCantidad());ventas.delete(v);}
}
