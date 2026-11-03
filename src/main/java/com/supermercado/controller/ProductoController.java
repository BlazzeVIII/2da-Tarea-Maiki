package com.supermercado.controller;
import com.supermercado.dto.*; import com.supermercado.dto.Respuestas.*; import com.supermercado.service.GestionService;
import jakarta.validation.Valid; import org.springframework.http.*; import org.springframework.web.bind.annotation.*; import java.util.List;
@RestController @RequestMapping("/api/productos")
public class ProductoController { private final GestionService s; public ProductoController(GestionService s){this.s=s;}
 @GetMapping public List<ProductoDto> listar(){return s.productos();} @GetMapping("/{id}") public ProductoDto uno(@PathVariable Long id){return s.producto(id);}
 @GetMapping("/categoria/{categoriaId}") public List<ProductoDto> categoria(@PathVariable Long categoriaId){return s.porCategoria(categoriaId);}
 @GetMapping("/buscar") public List<ProductoDto> buscar(@RequestParam String nombre){return s.buscar(nombre);}
 @GetMapping("/stock-bajo") public List<ProductoDto> stockBajo(@RequestParam(defaultValue="10") int limite){return s.stockBajo(limite);}
 @PostMapping public ResponseEntity<ProductoDto> crear(@Valid @RequestBody ProductoRequest r){return ResponseEntity.status(HttpStatus.CREATED).body(s.crearProducto(r));}
 @PutMapping("/{id}") public ProductoDto actualizar(@PathVariable Long id,@Valid @RequestBody ProductoRequest r){return s.actualizarProducto(id,r);}
 @DeleteMapping("/{id}") public ResponseEntity<Void> borrar(@PathVariable Long id){s.borrarProducto(id);return ResponseEntity.noContent().build();} }
