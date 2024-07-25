package com.ecommerce.ecomm.controller;

import com.ecommerce.ecomm.dto.ProdutoDTO;
import com.ecommerce.ecomm.entities.Produto;
import com.ecommerce.ecomm.exception.EcommException;
import com.ecommerce.ecomm.model.ErrorResponse;
import com.ecommerce.ecomm.service.ProdutoService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/produtos")
public class ProdutoController {

    private final ProdutoService produtoService;

    public ProdutoController(ProdutoService produtoService) {
        this.produtoService = produtoService;
    }

    @PostMapping
    public ResponseEntity<?> criarProduto(@Valid @RequestBody Produto produto) {
        return handleResponse(() -> produtoService.criarProduto(produto), HttpStatus.CREATED);
    }

    @GetMapping
    public ResponseEntity<?> listarProdutos() {
        return handleResponse(produtoService::listarProdutos, HttpStatus.OK);
    }

    @PutMapping("/{id}")
    public ResponseEntity<?> atualizarProduto(@PathVariable Long id, @Valid @RequestBody ProdutoDTO produtoUpdateDTO) {
        return handleResponse(() -> produtoService.atualizarProduto(id, produtoUpdateDTO), HttpStatus.OK);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> deletarProduto(@PathVariable Long id) {
        return handleResponse(() -> {
            produtoService.deletarProduto(id);
            return null;
        }, HttpStatus.OK);
    }

    private ResponseEntity<?> handleResponse(SupplierWithException<?> supplier, HttpStatus successStatus) {
        try {
            Object result = supplier.get();
            return new ResponseEntity<>(result, successStatus);
        } catch (EcommException e) {
            return new ResponseEntity<>(
                    new ErrorResponse(e.getErrorCode().getCode(), e.getErrorCode().getStatus(), e.getMessage()),
                    HttpStatus.valueOf(e.getErrorCode().getStatus()));
        }
    }

    @FunctionalInterface
    private interface SupplierWithException<T> {
        T get() throws EcommException;
    }
}