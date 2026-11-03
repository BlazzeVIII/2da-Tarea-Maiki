package com.supermercado.dto;
import jakarta.validation.Valid;
import jakarta.validation.constraints.*;
import java.util.List;
public record VentaRequest(@NotNull Long clienteId, @NotEmpty List<@Valid LineaVentaRequest> detalles) {
 public record LineaVentaRequest(@NotNull Long productoId, @NotNull @Min(1) Integer cantidad) {}
}
