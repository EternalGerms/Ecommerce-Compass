package com.ecommerce.ecomm.controller;

import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.ecommerce.ecomm.entities.Produto;
import com.ecommerce.ecomm.service.ProdutoService;

@RestController
@RequestMapping("/api/produtos")
public class ProdutoController {
	private final ProdutoService produtoService;

	public ProdutoController(ProdutoService produtoService) {
		this.produtoService = produtoService;
	}

	@GetMapping
	public List<Produto> findAll() {
		return produtoService.findAll();
	}

	@GetMapping("/{id}")
	public ResponseEntity<Produto> findById(@PathVariable Long id) {
		return produtoService.findById(id).map(ResponseEntity::ok).orElse(ResponseEntity.notFound().build());
	}

	@PostMapping
	public Produto save(@RequestBody Produto produto) {
		return produtoService.save(produto);
	}

	@PutMapping("/{id}")
	public Produto update(@PathVariable Long id, @RequestBody Produto produtoDetails) {
		return produtoService.update(id, produtoDetails);
	}

	@DeleteMapping("/{id}")
	public ResponseEntity<Void> delete(@PathVariable Long id) {
		produtoService.delete(id);
		return ResponseEntity.noContent().build();
	}
}
