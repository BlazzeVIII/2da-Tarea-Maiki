package com.supermercado.entity;

import jakarta.persistence.*;
import lombok.*;

@Entity @Table(name="categorias") @Getter @Setter @NoArgsConstructor
public class Categoria {
    @Id @GeneratedValue(strategy=GenerationType.IDENTITY) private Long id;
    @Column(nullable=false, unique=true, length=100) private String nombre;
    @Column(length=500) private String descripcion;
}
