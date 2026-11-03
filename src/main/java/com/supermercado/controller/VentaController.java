package com.supermercado.controller;
import com.supermercado.dto.*; import com.supermercado.dto.Respuestas.*; import com.supermercado.service.GestionService;
import jakarta.validation.Valid; import org.springframework.http.*; import org.springframework.web.bind.annotation.*; import java.util.List; import java.time.LocalDate;
@RestController @RequestMapping("/api/ventas")
public class VentaController { private final GestionService s; public VentaController(GestionService s){this.s=s;}
 @PostMapping public ResponseEntity<VentaDto> crear(@Valid @RequestBody VentaRequest r){return ResponseEntity.status(HttpStatus.CREATED).body(s.crearVenta(r));}
 @GetMapping public List<VentaDto> listar(){return s.ventas();} @GetMapping("/{id}") public VentaDto uno(@PathVariable Long id){return s.venta(id);}
 @GetMapping("/cliente/{clienteId}") public List<VentaDto> cliente(@PathVariable Long clienteId){return s.ventasCliente(clienteId);}
 @GetMapping("/fecha") public List<VentaDto> fecha(@RequestParam LocalDate inicio,@RequestParam LocalDate fin){return s.ventasFecha(inicio,fin);}
 @DeleteMapping("/{id}") public ResponseEntity<Void> borrar(@PathVariable Long id){s.borrarVenta(id);return ResponseEntity.noContent().build();} }
