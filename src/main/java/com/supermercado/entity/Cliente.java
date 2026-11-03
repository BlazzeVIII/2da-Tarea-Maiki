package com.supermercado.entity;

import jakarta.persistence.*;
import lombok.*;

@Entity @Table(name="clientes") @Getter @Setter @NoArgsConstructor
public class Cliente {
    @Id @GeneratedValue(strategy=GenerationType.IDENTITY) private Long id;
    @Column(nullable=false, length=140) private String nombre;
    @Column(nullable=false, unique=true, length=40) private String documento;
    @Column(length=30) private String telefono;
}
