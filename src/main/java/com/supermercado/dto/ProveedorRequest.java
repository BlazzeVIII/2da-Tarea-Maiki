package com.supermercado.dto;
import jakarta.validation.constraints.*;
public record ProveedorRequest(@NotBlank String nombre, String telefono, @Email String correo) {}
