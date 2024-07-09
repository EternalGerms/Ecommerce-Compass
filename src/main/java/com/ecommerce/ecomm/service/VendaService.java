package com.ecommerce.ecomm.service;

import java.time.DayOfWeek;
import java.time.LocalDateTime;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.rest.webmvc.ResourceNotFoundException;
import org.springframework.stereotype.Service;

import com.ecommerce.ecomm.dto.VendaDTO;
import com.ecommerce.ecomm.entities.Produto;
import com.ecommerce.ecomm.entities.Venda;
import com.ecommerce.ecomm.repository.ProdutoRepository;
import com.ecommerce.ecomm.repository.VendaRepository;

import jakarta.validation.Valid;

@Service
public class VendaService {
	

    @Autowired
    private VendaRepository vendaRepository;

    @Autowired
    private ProdutoRepository produtoRepository;

    public Venda criarVenda(@Valid VendaDTO vendaDTO) {
        Produto produto = produtoRepository.findById(vendaDTO.getIdProduto())
            .orElseThrow(() -> new ResourceNotFoundException("Produto não encontrado"));

        if (produto.getEstoque() < vendaDTO.getQuantidade()) {
            throw new IllegalArgumentException("Estoque insuficiente para o produto: " + produto.getNome());
        }

        produto.setEstoque(produto.getEstoque() - vendaDTO.getQuantidade());
        produtoRepository.save(produto);

        Venda venda = new Venda();
        venda.setProduto(produto);
        venda.setQuantidade(vendaDTO.getQuantidade());
        venda.setDataVenda(vendaDTO.getDataVenda());
        venda.setIdProduto(produto.getId());

        return vendaRepository.save(venda);
    }

    public List<Venda> listarVendas() {
        return vendaRepository.findAll();
    }

    public Venda atualizarVenda(Long id, @Valid VendaDTO vendaDTO) {
        Venda vendaExistente = vendaRepository.findById(id)
            .orElseThrow(() -> new ResourceNotFoundException("Venda não encontrada"));

        Produto produto = produtoRepository.findById(vendaDTO.getIdProduto())
            .orElseThrow(() -> new ResourceNotFoundException("Produto não encontrado"));

        if (produto.getEstoque() + vendaExistente.getQuantidade() < vendaDTO.getQuantidade()) {
            throw new IllegalArgumentException("Estoque insuficiente para o produto: " + produto.getNome());
        }

        produto.setEstoque(produto.getEstoque() + vendaExistente.getQuantidade() - vendaDTO.getQuantidade());
        produtoRepository.save(produto);

        vendaExistente.setProduto(produto);
        vendaExistente.setQuantidade(vendaDTO.getQuantidade());
        vendaExistente.setDataVenda(vendaDTO.getDataVenda());
        vendaExistente.setIdProduto(produto.getId());

        return vendaRepository.save(vendaExistente);
    }

    public void deletarVenda(Long id) {
        Venda venda = vendaRepository.findById(id)
            .orElseThrow(() -> new ResourceNotFoundException("Venda não encontrada"));

        Produto produto = venda.getProduto();
        produto.setEstoque(produto.getEstoque() + venda.getQuantidade());
        produtoRepository.save(produto);

        vendaRepository.delete(venda);
    }

    public List<Venda> filtrarVendasPorData(LocalDateTime startDate, LocalDateTime endDate) {
        return vendaRepository.findByDataVendaBetween(startDate, endDate);
    }

    public List<Venda> gerarRelatorioMensal(LocalDateTime mesAno) {
    	LocalDateTime startDate = mesAno.withDayOfMonth(1);
    	LocalDateTime endDate = mesAno.withDayOfMonth(mesAno.getMonthValue());
        return filtrarVendasPorData(startDate, endDate);
    }

    public List<Venda> gerarRelatorioSemanal(LocalDateTime semana) {
    	LocalDateTime startDate = semana.with(DayOfWeek.MONDAY);
    	LocalDateTime endDate = semana.with(DayOfWeek.SUNDAY);
        return filtrarVendasPorData(startDate, endDate);
    }
}
