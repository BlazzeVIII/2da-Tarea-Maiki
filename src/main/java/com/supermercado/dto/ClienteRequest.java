package com.supermercado.dto;
import jakarta.validation.constraints.NotBlank;
public record ClienteRequest(@NotBlank String nombre, @NotBlank String documento, String telefono) {}
