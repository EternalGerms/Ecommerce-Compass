package com.ecommerce.ecomm.service;

import java.time.LocalDateTime;
import java.util.List;

import org.springframework.data.rest.webmvc.ResourceNotFoundException;
import org.springframework.stereotype.Service;

import com.ecommerce.ecomm.dto.VendaDTO;
import com.ecommerce.ecomm.entities.Produto;
import com.ecommerce.ecomm.entities.Venda;
import com.ecommerce.ecomm.repository.ProdutoRepository;
import com.ecommerce.ecomm.repository.VendaRepository;

@Service
public class VendaService {

    private final VendaRepository vendaRepository;
    private final ProdutoRepository produtoRepository;

    public VendaService(VendaRepository vendaRepository, ProdutoRepository produtoRepository) {
        this.vendaRepository = vendaRepository;
        this.produtoRepository = produtoRepository;
    }

    public Venda criarVenda(VendaDTO vendaDTO) {
        Produto produto = produtoRepository.findById(vendaDTO.getIdProduto())
            .orElseThrow(() -> new ResourceNotFoundException("Produto não encontrado"));

        if (produto.getEstoque() < vendaDTO.getQuantidade()) {
            throw new IllegalArgumentException("Estoque insuficiente para o produto: " + produto.getNome());
        }

        produto.setEstoque(produto.getEstoque() - vendaDTO.getQuantidade());
        produtoRepository.save(produto);

        Venda venda = new Venda();
        venda.setProduto(produto); // Certifique-se de que a associação está sendo feita corretamente
        venda.setQuantidade(vendaDTO.getQuantidade());
        venda.setDataVenda(vendaDTO.getDataVenda());

        return vendaRepository.save(venda);
    }
    
    public List<Venda> listarVendas() {
        return vendaRepository.findAll();
    }

    public Venda atualizarVenda(Long id, VendaDTO vendaDTO) {
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
        LocalDateTime startDate = mesAno.withDayOfMonth(1).withHour(0).withMinute(0).withSecond(0);
        LocalDateTime endDate = mesAno.withDayOfMonth(mesAno.toLocalDate().lengthOfMonth()).withHour(23).withMinute(59).withSecond(59);
        return vendaRepository.findByDataVendaBetween(startDate, endDate);
    }

    public List<Venda> gerarRelatorioSemanal(LocalDateTime semana) {
        LocalDateTime startDate = semana.with(java.time.DayOfWeek.MONDAY).withHour(0).withMinute(0).withSecond(0);
        LocalDateTime endDate = semana.with(java.time.DayOfWeek.SUNDAY).withHour(23).withMinute(59).withSecond(59);
        return vendaRepository.findByDataVendaBetween(startDate, endDate);
    }
}
