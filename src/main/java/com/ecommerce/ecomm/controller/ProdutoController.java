package com.ecommerce.ecomm.controller;

import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.ecommerce.ecomm.dto.ProdutoDTO;
import com.ecommerce.ecomm.entities.Produto;
import com.ecommerce.ecomm.entities.User;
import com.ecommerce.ecomm.exception.EcommException;
import com.ecommerce.ecomm.model.ErrorResponse;
import com.ecommerce.ecomm.service.ProdutoService;

import jakarta.validation.Valid;

@RestController
@RequestMapping("/api/produtos")
public class ProdutoController {

    private final ProdutoService produtoService;

    public ProdutoController(ProdutoService produtoService) {
        this.produtoService = produtoService;
    }

    @PostMapping
    public ResponseEntity<?> criarProduto(@Valid @RequestBody Produto produto, @AuthenticationPrincipal User currentUser) {
        try {
            Produto novoProduto = produtoService.criarProduto(produto, currentUser);
            return new ResponseEntity<>(novoProduto, HttpStatus.CREATED);
        } catch (EcommException e) {
            return new ResponseEntity<>(
                    new ErrorResponse(e.getErrorCode().getCode(), e.getErrorCode().getStatus(), e.getMessage()),
                    HttpStatus.valueOf(e.getErrorCode().getStatus()));
        }
    }

    @GetMapping
    public ResponseEntity<?> listarProdutos() {
        try {
            List<Produto> produtos = produtoService.listarProdutos();
            return new ResponseEntity<>(produtos, HttpStatus.OK);
        } catch (EcommException e) {
            return new ResponseEntity<>(
                    new ErrorResponse(e.getErrorCode().getCode(), e.getErrorCode().getStatus(), e.getMessage()),
                    HttpStatus.valueOf(e.getErrorCode().getStatus()));
        }
    }

    @PutMapping("/{id}")
    public ResponseEntity<?> atualizarProduto(@PathVariable Long id, @Valid @RequestBody ProdutoDTO produtoUpdateDTO, @AuthenticationPrincipal User currentUser) {
        try {
            Produto produtoAtualizado = produtoService.atualizarProduto(id, produtoUpdateDTO, currentUser);
            return new ResponseEntity<>(produtoAtualizado, HttpStatus.OK);
        } catch (EcommException e) {
            return new ResponseEntity<>(
                    new ErrorResponse(e.getErrorCode().getCode(), e.getErrorCode().getStatus(), e.getMessage()),
                    HttpStatus.valueOf(e.getErrorCode().getStatus()));
        }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> deletarProduto(@PathVariable Long id, @AuthenticationPrincipal User currentUser) {
        try {
            produtoService.deletarProduto(id, currentUser);
            return new ResponseEntity<>(HttpStatus.OK);
        } catch (EcommException e) {
            return new ResponseEntity<>(
                    new ErrorResponse(e.getErrorCode().getCode(), e.getErrorCode().getStatus(), e.getMessage()),
                    HttpStatus.valueOf(e.getErrorCode().getStatus()));
        }
    }
}