package com.cydeo.repository;

import com.cydeo.entity.Product;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ProductRepository extends JpaRepository<Product,Long> {
    List<Product> findAllByCategoryIdOrderByNameAsc(Long id);

    boolean existsByName(String name);
}
