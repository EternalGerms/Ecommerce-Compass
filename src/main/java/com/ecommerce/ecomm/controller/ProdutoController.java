package com.ecommerce.ecomm.controller;

import java.util.List;

import org.springframework.data.rest.webmvc.ResourceNotFoundException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
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
import com.ecommerce.ecomm.exception.InvalidProductException;
import com.ecommerce.ecomm.exception.NoContentException;
import com.ecommerce.ecomm.exception.ProdutoInativoException;
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
    public ResponseEntity<?> criarProduto(@Valid @RequestBody Produto produto) {
        try {
            Produto novoProduto = produtoService.criarProduto(produto);
            return new ResponseEntity<>(novoProduto, HttpStatus.CREATED);
        } catch (InvalidProductException e) {
            return new ResponseEntity<>(new ErrorResponse(400, "Bad Request", e.getMessage()), HttpStatus.BAD_REQUEST);
        }
    }
    
    @GetMapping
    public ResponseEntity<?> listarProdutos() {
        try {
            List<Produto> produtos = produtoService.listarProdutos();
            return new ResponseEntity<>(produtos, HttpStatus.OK);
        } catch (NoContentException e) {
            return new ResponseEntity<>(new ErrorResponse(204, "No Content", e.getMessage()), HttpStatus.OK);
        }
    }

    @PutMapping("/{id}")
    public ResponseEntity<?> atualizarProduto(@PathVariable Long id, @Valid @RequestBody ProdutoDTO produtoUpdateDTO) {
        try {
            Produto produtoAtualizado = produtoService.atualizarProduto(id, produtoUpdateDTO);
            return new ResponseEntity<>(produtoAtualizado, HttpStatus.OK);
        } catch (ResourceNotFoundException e) {
            return new ResponseEntity<>(new ErrorResponse(404, "Not Found", e.getMessage()), HttpStatus.NOT_FOUND);
        } catch (InvalidProductException e) {
            return new ResponseEntity<>(new ErrorResponse(400, "Bad Request", e.getMessage()), HttpStatus.BAD_REQUEST);
        }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> deletarProduto(@PathVariable Long id) {
        try {
            Produto produtoAtualizado = produtoService.deletarProduto(id);
            return new ResponseEntity<>(produtoAtualizado, HttpStatus.OK);
        } catch (ResourceNotFoundException e) {
            return new ResponseEntity<>(new ErrorResponse(404, "Not Found", e.getMessage()), HttpStatus.NOT_FOUND);
        } catch (ProdutoInativoException e) {
            return new ResponseEntity<>(new ErrorResponse(400, "Bad Request", e.getMessage()), HttpStatus.BAD_REQUEST);
        }
    }
}