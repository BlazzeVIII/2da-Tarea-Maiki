package com.supermercado.entity;

import jakarta.persistence.*;
import lombok.*;

@Entity @Table(name="proveedores") @Getter @Setter @NoArgsConstructor
public class Proveedor {
    @Id @GeneratedValue(strategy=GenerationType.IDENTITY) private Long id;
    @Column(nullable=false, length=120) private String nombre;
    @Column(length=30) private String telefono;
    @Column(length=150) private String correo;
}
