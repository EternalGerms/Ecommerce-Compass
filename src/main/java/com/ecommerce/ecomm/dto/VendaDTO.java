package com.ecommerce.ecomm.dto;

import java.time.LocalDate;

public class VendaDTO {
	private Long id;
    private Long idProduto;
    private Integer quantidade;
    private LocalDate dataVenda;
    
    
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
	public LocalDate getDataVenda() {
		return dataVenda;
	}
	public void setDataVenda(LocalDate dataVenda) {
		this.dataVenda = dataVenda;
		
	}
	
	public Long getId() {
		return id;
	}
	public void setId(Long id) {
		this.id = id;
	}
	public VendaDTO(Long idProduto, Integer quantidade, LocalDate dataVenda) {
		this.idProduto = idProduto;
		this.quantidade = quantidade;
		this.dataVenda = dataVenda;
	}
	
	public VendaDTO() {

	}

	

    
}