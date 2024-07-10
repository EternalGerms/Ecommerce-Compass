package com.ecommerce.ecomm.service;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.rest.webmvc.ResourceNotFoundException;
import org.springframework.stereotype.Service;

import com.ecommerce.ecomm.dto.ProdutoDTO;
import com.ecommerce.ecomm.entities.Produto;
import com.ecommerce.ecomm.entities.Venda;
import com.ecommerce.ecomm.exception.InvalidProductException;
import com.ecommerce.ecomm.exception.NoContentException;
import com.ecommerce.ecomm.exception.ProductInactivatedException;
import com.ecommerce.ecomm.repository.ProdutoRepository;
import com.ecommerce.ecomm.repository.VendaRepository;

import jakarta.validation.Valid;

@Service
public class ProdutoService {

    @Autowired
    private ProdutoRepository produtoRepository;

    @Autowired
    private VendaRepository vendaRepository;

    public Produto criarProduto(@Valid Produto produto) {
        if (produto.getEstoque() < 0) {
            throw new InvalidProductException("O estoque não pode ser negativo");
        }

        if (produto.getPreco() < 0) {
            throw new InvalidProductException("O preço não pode ser negativo");
        }

        return produtoRepository.save(produto);
    }

    public List<Produto> listarProdutos() {
        List<Produto> produtos = produtoRepository.findAll();
        if (produtos.isEmpty()) {
            throw new NoContentException("Nenhum produto disponível.");
        }
        return produtos;
    }

    public Produto atualizarProduto(Long id, @Valid ProdutoDTO produtoUpdateDTO) {
        Produto produtoExistente = produtoRepository.findById(id)
            .orElseThrow(() -> new ResourceNotFoundException("Produto não encontrado"));

        if (produtoUpdateDTO.getEstoque() != null && produtoUpdateDTO.getEstoque() < 0) {
            throw new InvalidProductException("O estoque não pode ser negativo");
        }

        if (produtoUpdateDTO.getPreco() != null && produtoUpdateDTO.getPreco() < 0) {
            throw new InvalidProductException("O preço não pode ser negativo");
        }

        if (produtoUpdateDTO.getNome() != null) {
            produtoExistente.setNome(produtoUpdateDTO.getNome());
        }

        if (produtoUpdateDTO.getEstoque() != null) {
            produtoExistente.setEstoque(produtoUpdateDTO.getEstoque());
        }

        if (produtoUpdateDTO.getPreco() != null) {
            produtoExistente.setPreco(produtoUpdateDTO.getPreco());
        }

        if (produtoUpdateDTO.isAtivo() != null) {
            produtoExistente.setAtivo(produtoUpdateDTO.isAtivo());
        }

        return produtoRepository.save(produtoExistente);
    }

    public void deletarProduto(Long id) {
        Produto produto = produtoRepository.findById(id)
            .orElseThrow(() -> new ResourceNotFoundException("Produto não encontrado"));

        List<Venda> vendas = vendaRepository.findByProdutoId(id);

        if (!vendas.isEmpty()) {
            produto.setAtivo(false);
            produtoRepository.save(produto);
            throw new ProductInactivatedException("Produto não pode ser excluído pois possui vendas associadas. Produto inativado.");
        }

        produtoRepository.delete(produto);
    }
}