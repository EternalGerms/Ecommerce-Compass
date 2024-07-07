package com.ecommerce.ecomm.service;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.ecommerce.ecomm.entities.Produto;
import com.ecommerce.ecomm.repository.ProdutoRepository;

@Service
public class ProdutoService {
	private final ProdutoRepository produtoRepository;

	public ProdutoService(ProdutoRepository produtoRepository) {
		this.produtoRepository = produtoRepository;
	}

	@Cacheable("produtos")
	public List<Produto> findAll() {
		return produtoRepository.findAll();
	}

	@Cacheable(value = "produtos", key = "#id")
	public Optional<Produto> findById(Long id) {
		return produtoRepository.findById(id);
	}

	@Transactional
	@CacheEvict(value = "produtos", allEntries = true)
	public Produto save(Produto produto) {
		if (produto.getPreco().compareTo(BigDecimal.ZERO) <= 0) {
			throw new IllegalArgumentException("Preço do produto deve ser positivo");
		}
		return produtoRepository.save(produto);
	}

	@Transactional
	@CacheEvict(value = "produtos", allEntries = true)
	public void delete(Long id) {
		Produto produto = produtoRepository.findById(id)
				.orElseThrow(() -> new IllegalArgumentException("Produto não encontrado"));
		if (!produto.isAtivo()) {
			throw new IllegalArgumentException("Produto já está inativo");
		}
		else if (produto.getVendas().isEmpty()) {
			produtoRepository.delete(produto);
		}
		else {
			produto.setAtivo(false);
			produtoRepository.save(produto);
		}
	}

	@Transactional
	@CacheEvict(value = "produtos", allEntries = true)
	public Produto update(Long id, Produto produtoDetails) {
		Produto produto = produtoRepository.findById(id)
				.orElseThrow(() -> new IllegalArgumentException("Produto não encontrado"));
		produto.setNome(produtoDetails.getNome());
		produto.setPreco(produtoDetails.getPreco());
		produto.setEstoque(produtoDetails.getEstoque());
		return produtoRepository.save(produto);
	}
}
