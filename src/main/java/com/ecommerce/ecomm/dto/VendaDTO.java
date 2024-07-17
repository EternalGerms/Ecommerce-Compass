package com.ecommerce.ecomm.dto;

import java.time.LocalDateTime;

import com.fasterxml.jackson.annotation.JsonFormat;

public class VendaDTO {
	private Long id;
	private Long idProduto;
	private Integer quantidade;

	@JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd'T'HH:mm:ss'Z'")
	private LocalDateTime dataVenda;

	// Getters and Setters

	public Long getIdProduto() {
		return idProduto;
	}

	public void setIdProduto(Long idProduto) {
		this.idProduto = idProduto;
	}

	public Integer getQuantidade() {
		return quantidade;
	}

	public void setQuantidade(Integer quantidade) {
		this.quantidade = quantidade;
	}

	public LocalDateTime getDataVenda() {
		return dataVenda;
	}

	public void setDataVenda(LocalDateTime dataVenda) {
		this.dataVenda = dataVenda;
	}

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public VendaDTO(Long idProduto, Integer quantidade, LocalDateTime dataVenda) {
		this.idProduto = idProduto;
		this.quantidade = quantidade;
		this.dataVenda = dataVenda;
	}

	public VendaDTO() {
	}
}