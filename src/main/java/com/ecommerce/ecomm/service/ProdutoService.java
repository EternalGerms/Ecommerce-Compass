package com.ecommerce.ecomm.service;

import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.ecommerce.ecomm.dto.ProdutoDTO;
import com.ecommerce.ecomm.entities.Produto;
import com.ecommerce.ecomm.entities.Venda;
import com.ecommerce.ecomm.exception.EcommException;
import com.ecommerce.ecomm.exception.ErrorCode;
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
            throw new EcommException(ErrorCode.INVALID_PRODUCT_STOCK);
        }

        if (produto.getPreco() < 0) {
            throw new EcommException(ErrorCode.INVALID_PRODUCT_PRICE);
        }

        Produto produtoSalvo = produtoRepository.save(produto);
        logger.info("Product created with ID: " + produtoSalvo.getId());
        return produtoSalvo;
    }

    public List<Produto> listarProdutos() {
        List<Produto> produtos = produtoRepository.findAll();
        if (produtos.isEmpty()) {
            throw new EcommException(ErrorCode.NO_PRODUCTS_AVAILABLE);
        }
        return produtos;
    }

    public Produto atualizarProduto(Long id, @Valid ProdutoDTO produtoUpdateDTO) {
        Produto produtoExistente = produtoRepository.findById(id)
                .orElseThrow(() -> new EcommException(ErrorCode.PRODUCT_NOT_FOUND));

        if (produtoUpdateDTO.getEstoque() != null && produtoUpdateDTO.getEstoque() < 0) {
            throw new EcommException(ErrorCode.INVALID_PRODUCT_STOCK);
        }

        if (produtoUpdateDTO.getPreco() != null && produtoUpdateDTO.getPreco() < 0) {
            throw new EcommException(ErrorCode.INVALID_PRODUCT_PRICE);
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

        Produto produtoAtualizado = produtoRepository.save(produtoExistente);
        logger.info("Product updated with ID: " + produtoAtualizado.getId());
        return produtoAtualizado;
    }

    public void deletarProduto(Long id) {
        Produto produto = produtoRepository.findById(id)
                .orElseThrow(() -> new EcommException(ErrorCode.PRODUCT_NOT_FOUND));

        List<Venda> vendas = vendaRepository.findByProdutoId(id);
        logger.debug("Vendas associadas: " + vendas.size());

        if (produto.isAtivo()) {
            if (vendas.isEmpty()) {
                logger.info("Deleting product with ID: " + produto.getId());
                produtoRepository.delete(produto);
            } else {
                logger.info("Deactivating product with ID: " + produto.getId());
                produto.setAtivo(false);
                produtoRepository.save(produto);
                throw new EcommException(ErrorCode.PRODUTO_INATIVO);
            }
        } else {
            if (vendas.isEmpty()) {
                logger.info("Deleting product with ID: " + produto.getId());
                produtoRepository.delete(produto);
            } else {
                throw new EcommException(ErrorCode.PRODUTO_INATIVO);
            }
        }
    }
}