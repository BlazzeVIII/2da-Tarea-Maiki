package com.supermercado.controller;
import com.supermercado.dto.*; import com.supermercado.dto.Respuestas.*; import com.supermercado.service.GestionService;
import jakarta.validation.Valid; import org.springframework.http.*; import org.springframework.web.bind.annotation.*; import java.util.List;
@RestController @RequestMapping("/api/clientes")
public class ClienteController { private final GestionService s; public ClienteController(GestionService s){this.s=s;}
 @GetMapping public List<ClienteDto> listar(){return s.clientes();} @GetMapping("/{id}") public ClienteDto uno(@PathVariable Long id){return s.cliente(id);} @GetMapping("/documento/{documento}") public ClienteDto documento(@PathVariable String documento){return s.clienteDocumento(documento);}
 @PostMapping public ResponseEntity<ClienteDto> crear(@Valid @RequestBody ClienteRequest r){return ResponseEntity.status(HttpStatus.CREATED).body(s.crearCliente(r));}
 @PutMapping("/{id}") public ClienteDto actualizar(@PathVariable Long id,@Valid @RequestBody ClienteRequest r){return s.actualizarCliente(id,r);}
 @DeleteMapping("/{id}") public ResponseEntity<Void> borrar(@PathVariable Long id){s.borrarCliente(id);return ResponseEntity.noContent().build();} }
