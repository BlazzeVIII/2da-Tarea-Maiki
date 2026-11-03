package com.supermercado.dto;
import jakarta.validation.constraints.*;
import java.math.BigDecimal;
public record ProductoRequest(@NotBlank String nombre, @NotNull @DecimalMin(value="0.0", inclusive=false) BigDecimal precio,
 @NotNull @Min(0) Integer stock, @NotBlank String codigoBarras, @NotNull Long categoriaId, @NotNull Long proveedorId) {}
