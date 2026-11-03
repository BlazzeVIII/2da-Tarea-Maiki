package com.supermercado.entity;

import jakarta.persistence.*;
import lombok.*;
import java.math.BigDecimal;

@Entity @Table(name="productos") @Getter @Setter @NoArgsConstructor
public class Producto {
    @Id @GeneratedValue(strategy=GenerationType.IDENTITY) private Long id;
    @Column(nullable=false, length=140) private String nombre;
    @Column(nullable=false, precision=12, scale=2) private BigDecimal precio;
    @Column(nullable=false) private Integer stock;
    @Column(nullable=false, unique=true, length=60) private String codigoBarras;
    @ManyToOne(fetch=FetchType.LAZY, optional=false) @JoinColumn(name="categoria_id", nullable=false) private Categoria categoria;
    @ManyToOne(fetch=FetchType.LAZY, optional=false) @JoinColumn(name="proveedor_id", nullable=false) private Proveedor proveedor;
}
