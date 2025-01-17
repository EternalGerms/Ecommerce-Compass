package com.ecommerce.ecomm.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.ecommerce.ecomm.entities.Produto;

@Repository
public interface ProdutoRepository extends JpaRepository<Produto, Long> {
}