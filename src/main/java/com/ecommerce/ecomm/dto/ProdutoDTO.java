package com.ecommerce.ecomm.dto;

import com.ecommerce.ecomm.entities.Produto;

public class ProdutoDTO {
    private String nome;
    private Double preco;
    private Integer estoque;
    private Boolean ativo; // Campo opcional para ativo/inativo
    
   
    public ProdutoDTO(String nome, Double preco, Integer estoque, Boolean ativo) {
		super();
		this.nome = nome;
		this.preco = preco;
		this.estoque = estoque;
		this.ativo = ativo;
	}

	public ProdutoDTO(Produto produto) {
		// TODO Auto-generated constructor stub
	}

	// Getters e Setters
    public String getNome() {
        return nome;
    }

    public void setNome(String nome) {
        this.nome = nome;
    }

    public Double getPreco() {
        return preco;
    }

    public void setPreco(Double preco) {
        this.preco = preco;
    }

    public Integer getEstoque() {
        return estoque;
    }

    public void setEstoque(Integer estoque) {
        this.estoque = estoque;
    }

    public Boolean isAtivo() {
        return ativo;
    }

    public void setAtivo(Boolean ativo) {
        this.ativo = ativo;
    }
}