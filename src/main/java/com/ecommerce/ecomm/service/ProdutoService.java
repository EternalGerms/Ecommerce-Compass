package com.ecommerce.ecomm.service;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.rest.webmvc.ResourceNotFoundException;
import org.springframework.stereotype.Service;

import com.ecommerce.ecomm.entities.Produto;
import com.ecommerce.ecomm.repository.ProdutoRepository;

import jakarta.validation.Valid;

@Service
public class ProdutoService {

    @Autowired
    private ProdutoRepository produtoRepository;

    public Produto criarProduto(@Valid Produto produto) {
        if (produto.getEstoque() < 0) {
            throw new IllegalArgumentException("O estoque não pode ser negativo");
        }

        if (produto.getPreco() < 0) {
            throw new IllegalArgumentException("O preço não pode ser negativo");
        }

        return produtoRepository.save(produto);
    }

    public List<Produto> listarProdutos() {
        return produtoRepository.findAll();
    }

    public Produto atualizarProduto(Long id, @Valid Produto produto) {
        Produto produtoExistente = produtoRepository.findById(id)
            .orElseThrow(() -> new ResourceNotFoundException("Produto não encontrado"));

        if (produto.getEstoque() < 0) {
            throw new IllegalArgumentException("O estoque não pode ser negativo");
        }

        if (produto.getPreco() < 0) {
            throw new IllegalArgumentException("O preço não pode ser negativo");
        }

        produtoExistente.setNome(produto.getNome());
        produtoExistente.setEstoque(produto.getEstoque());
        produtoExistente.setPreco(produto.getPreco());
        produtoExistente.setAtivo(produto.isAtivo());

        return produtoRepository.save(produtoExistente);
    }

    public void deletarProduto(Long id) {
        Produto produto = produtoRepository.findById(id)
            .orElseThrow(() -> new ResourceNotFoundException("Produto não encontrado"));
        produtoRepository.delete(produto);
    }
}
