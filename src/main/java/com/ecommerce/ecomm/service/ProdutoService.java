package com.ecommerce.ecomm.service;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.ecommerce.ecomm.dto.ProdutoDTO;
import com.ecommerce.ecomm.entities.Produto;
import com.ecommerce.ecomm.entities.User;
import com.ecommerce.ecomm.entities.Venda;
import com.ecommerce.ecomm.exception.EcommException;
import com.ecommerce.ecomm.exception.ErrorCode;
import com.ecommerce.ecomm.repository.ProdutoRepository;
import com.ecommerce.ecomm.repository.VendaRepository;

import jakarta.validation.Valid;

@Service
public class ProdutoService {

    @Autowired
    private ProdutoRepository produtoRepository;

    @Autowired
    private VendaRepository vendaRepository;

    @Autowired
    private PermissionService permissionService; // Corrigido para PermissionService

    public Produto criarProduto(@Valid Produto produto, User currentUser) {
    	if (!permissionService.checkUserPermissions(currentUser, "CREATE_PRODUCT")) {
            throw new EcommException(ErrorCode.USER_ROLE_NO_PERMISSION);
        }

        if (produto.getEstoque() < 0) {
            throw new EcommException(ErrorCode.INVALID_PRODUCT_STOCK);
        }

        if (produto.getPreco() < 0) {
            throw new EcommException(ErrorCode.INVALID_PRODUCT_PRICE);
        }

        return produtoRepository.save(produto);
    }

    public List<Produto> listarProdutos() {
        List<Produto> produtos = produtoRepository.findAll();
        if (produtos.isEmpty()) {
            throw new EcommException(ErrorCode.NO_PRODUCTS_AVAILABLE);
        }
        return produtos;
    }

    public Produto atualizarProduto(Long id, @Valid ProdutoDTO produtoUpdateDTO, User currentUser) {
        if (!permissionService.checkUserPermissions(currentUser, "UPDATE_PRODUCT")) {
            throw new EcommException(ErrorCode.USER_ROLE_NO_PERMISSION);
        }

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

        return produtoRepository.save(produtoExistente);
    }

    public void deletarProduto(Long id, User currentUser) {
        if (!permissionService.checkUserPermissions(currentUser, "DELETE_PRODUCT")) {
            throw new EcommException(ErrorCode.USER_ROLE_NO_PERMISSION);
        }

        Produto produto = produtoRepository.findById(id)
                .orElseThrow(() -> new EcommException(ErrorCode.PRODUCT_NOT_FOUND));

        List<Venda> vendas = vendaRepository.findByProdutoId(id);
        System.out.println("Vendas associadas: " + vendas.size()); // Adicione este log para depuração

        if (produto.isAtivo()) {
            if (vendas.isEmpty()) {
                System.out.println("Excluindo produto: " + produto.getId()); // Adicione este log para depuração
                produtoRepository.delete(produto);
            } else {
                System.out.println("Inativando produto: " + produto.getId()); // Adicione este log para depuração
                produto.setAtivo(false);
                produtoRepository.save(produto);
                throw new EcommException(ErrorCode.PRODUTO_INATIVO);
            }
        } else {
            if (vendas.isEmpty()) {
                System.out.println("Excluindo produto: " + produto.getId()); // Adicione este log para depuração
                produtoRepository.delete(produto);
            } else {
                throw new EcommException(ErrorCode.PRODUTO_INATIVO);
            }
        }
    }
}