package com.supermercado.repository;
import com.supermercado.entity.Producto;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;
public interface ProductoRepository extends JpaRepository<Producto,Long> {
 List<Producto> findByCategoriaId(Long id);
 List<Producto> findByNombreContainingIgnoreCase(String nombre);
 List<Producto> findByStockLessThanEqual(Integer limite);
 boolean existsByCategoriaId(Long id);
 boolean existsByProveedorId(Long id);
}
