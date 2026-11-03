package com.supermercado.controller;
import com.supermercado.dto.*; import com.supermercado.dto.Respuestas.*; import com.supermercado.service.GestionService;
import jakarta.validation.Valid; import org.springframework.http.*; import org.springframework.web.bind.annotation.*; import java.util.List;
@RestController @RequestMapping("/api/categorias")
public class CategoriaController { private final GestionService s; public CategoriaController(GestionService s){this.s=s;}
 @GetMapping public List<CategoriaDto> listar(){return s.categorias();} @GetMapping("/{id}") public CategoriaDto uno(@PathVariable Long id){return s.categoria(id);} @PostMapping public ResponseEntity<CategoriaDto> crear(@Valid @RequestBody CategoriaRequest r){return ResponseEntity.status(HttpStatus.CREATED).body(s.crearCategoria(r));}
 @PutMapping("/{id}") public CategoriaDto actualizar(@PathVariable Long id,@Valid @RequestBody CategoriaRequest r){return s.actualizarCategoria(id,r);} @DeleteMapping("/{id}") public ResponseEntity<Void> borrar(@PathVariable Long id){s.borrarCategoria(id);return ResponseEntity.noContent().build();} }
