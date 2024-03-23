package com.pagination.javatechiepaginationsorting.repository;

import com.pagination.javatechiepaginationsorting.model.Product;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ProductRepository extends JpaRepository<Product, Long> {
}