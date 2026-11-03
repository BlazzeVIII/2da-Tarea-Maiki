package com.supermercado.repository;
import com.supermercado.entity.Venta;
import org.springframework.data.jpa.repository.JpaRepository;
import java.time.LocalDateTime;
import java.util.List;
public interface VentaRepository extends JpaRepository<Venta,Long> {
 List<Venta> findByClienteId(Long id);
 List<Venta> findByFechaBetween(LocalDateTime inicio, LocalDateTime fin);
}
