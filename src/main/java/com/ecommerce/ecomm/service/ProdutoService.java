package com.ecommerce.ecomm.service;

import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.rest.webmvc.ResourceNotFoundException;
import org.springframework.stereotype.Service;

import com.ecommerce.ecomm.entities.Produto;
import com.ecommerce.ecomm.entities.Venda;
import com.ecommerce.ecomm.repository.ProdutoRepository;
import com.ecommerce.ecomm.repository.VendaRepository;

import jakarta.validation.Valid;

@Service
public class ProdutoService {
	private static final Logger logger = LoggerFactory.getLogger(ProdutoService.class);

    @Autowired
    private ProdutoRepository produtoRepository;

    @Autowired
    private VendaRepository vendaRepository;

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

        List<Venda> vendas = vendaRepository.findByProdutoId(id);
        logger.info("Vendas encontradas para o produto {}: {}", id, vendas.size());

        if (!vendas.isEmpty()) {
            produto.setAtivo(false);
            produtoRepository.save(produto);
            logger.warn("Produto {} não pode ser excluído pois possui vendas associadas. Produto inativado.", id);
            throw new IllegalArgumentException("Produto não pode ser excluído pois possui vendas associadas. Produto inativado.");
        }

        produtoRepository.delete(produto);
    }
}