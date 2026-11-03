package com.supermercado.controller;
import com.supermercado.dto.*; import com.supermercado.dto.Respuestas.*; import com.supermercado.service.GestionService;
import jakarta.validation.Valid; import org.springframework.http.*; import org.springframework.web.bind.annotation.*; import java.util.List;
@RestController @RequestMapping("/api/proveedores")
public class ProveedorController { private final GestionService s; public ProveedorController(GestionService s){this.s=s;}
 @GetMapping public List<ProveedorDto> listar(){return s.proveedores();} @GetMapping("/{id}") public ProveedorDto uno(@PathVariable Long id){return s.proveedor(id);} @PostMapping public ResponseEntity<ProveedorDto> crear(@Valid @RequestBody ProveedorRequest r){return ResponseEntity.status(HttpStatus.CREATED).body(s.crearProveedor(r));}
 @PutMapping("/{id}") public ProveedorDto actualizar(@PathVariable Long id,@Valid @RequestBody ProveedorRequest r){return s.actualizarProveedor(id,r);} @DeleteMapping("/{id}") public ResponseEntity<Void> borrar(@PathVariable Long id){s.borrarProveedor(id);return ResponseEntity.noContent().build();} }
