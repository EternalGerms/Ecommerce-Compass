package com.ecommerce.ecomm.repository;

import java.time.LocalDateTime;
import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.ecommerce.ecomm.entities.Venda;

@Repository
public interface VendaRepository extends JpaRepository<Venda, Long> {
	List<Venda> findByDataVendaBetween(LocalDateTime startDate, LocalDateTime endDate);

	List<Venda> findByProdutoId(Long produtoId);
}