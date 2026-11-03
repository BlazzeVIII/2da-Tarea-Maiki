package com.supermercado.entity;

import jakarta.persistence.*;
import lombok.*;
import java.math.BigDecimal;

@Entity @Table(name="detalles_venta") @Getter @Setter @NoArgsConstructor
public class DetalleVenta {
    @Id @GeneratedValue(strategy=GenerationType.IDENTITY) private Long id;
    @Column(nullable=false) private Integer cantidad;
    @Column(nullable=false, precision=12, scale=2) private BigDecimal precioUnitario;
    @Column(nullable=false, precision=12, scale=2) private BigDecimal subtotal;
    @ManyToOne(fetch=FetchType.LAZY, optional=false) @JoinColumn(name="venta_id", nullable=false) private Venta venta;
    @ManyToOne(fetch=FetchType.LAZY, optional=false) @JoinColumn(name="producto_id", nullable=false) private Producto producto;
}
