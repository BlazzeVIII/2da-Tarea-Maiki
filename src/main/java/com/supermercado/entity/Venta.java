package com.supermercado.entity;

import jakarta.persistence.*;
import lombok.*;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity @Table(name="ventas") @Getter @Setter @NoArgsConstructor
public class Venta {
    @Id @GeneratedValue(strategy=GenerationType.IDENTITY) private Long id;
    @Column(nullable=false) private LocalDateTime fecha;
    @Column(nullable=false, precision=12, scale=2) private BigDecimal total;
    @ManyToOne(fetch=FetchType.LAZY, optional=false) @JoinColumn(name="cliente_id", nullable=false) private Cliente cliente;
    @OneToMany(mappedBy="venta", cascade=CascadeType.ALL, orphanRemoval=true) private List<DetalleVenta> detalles=new ArrayList<>();
}
