package com.supermercado.repository;
import com.supermercado.entity.Categoria;
import org.springframework.data.jpa.repository.JpaRepository;
public interface CategoriaRepository extends JpaRepository<Categoria,Long> { boolean existsByNombreIgnoreCase(String nombre); }
