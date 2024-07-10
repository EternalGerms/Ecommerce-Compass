package com.ecommerce.ecomm.service;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.List;

import org.springframework.data.rest.webmvc.ResourceNotFoundException;
import org.springframework.stereotype.Service;

import com.ecommerce.ecomm.dto.VendaDTO;
import com.ecommerce.ecomm.entities.Produto;
import com.ecommerce.ecomm.entities.Venda;
import com.ecommerce.ecomm.exception.InsufficientStockException;
import com.ecommerce.ecomm.exception.InvalidDateException;
import com.ecommerce.ecomm.exception.InvalidQuantityException;
import com.ecommerce.ecomm.exception.NoContentException;
import com.ecommerce.ecomm.exception.NoSalesInPeriodException;
import com.ecommerce.ecomm.exception.ProdutoInativoException;
import com.ecommerce.ecomm.exception.VendaNotFoundException;
import com.ecommerce.ecomm.repository.ProdutoRepository;
import com.ecommerce.ecomm.repository.VendaRepository;

@Service
public class VendaService {

    private final VendaRepository vendaRepository;
    private final ProdutoRepository produtoRepository;
    private final DateTimeFormatter dateTimeFormatter = DateTimeFormatter.ISO_DATE_TIME;

    public VendaService(VendaRepository vendaRepository, ProdutoRepository produtoRepository) {
        this.vendaRepository = vendaRepository;
        this.produtoRepository = produtoRepository;
    }

    public Venda criarVenda(VendaDTO vendaDTO) {
        Produto produto = produtoRepository.findById(vendaDTO.getIdProduto())
            .orElseThrow(() -> new ResourceNotFoundException("Produto com ID " + vendaDTO.getIdProduto() + " não encontrado."));

        if (!produto.isAtivo()) {
            throw new ProdutoInativoException("Não é possível criar uma venda para um produto inativo.");
        }

        if (vendaDTO.getQuantidade() <= 0) {
            throw new InvalidQuantityException("A quantidade da venda deve ser maior que zero.");
        }

        if (produto.getEstoque() < vendaDTO.getQuantidade()) {
            throw new InsufficientStockException("Estoque insuficiente para o produto: " + produto.getNome());
        }

        try {
            LocalDateTime dataVenda = LocalDateTime.parse(vendaDTO.getDataVenda().toString(), dateTimeFormatter);
        } catch (DateTimeParseException e) {
            throw new InvalidDateException("Data da venda inválida ou no formato errado. O formato esperado é: " + dateTimeFormatter.toString());
        }

        produto.setEstoque(produto.getEstoque() - vendaDTO.getQuantidade());
        produtoRepository.save(produto);

        Venda venda = new Venda();
        venda.setProduto(produto);
        venda.setQuantidade(vendaDTO.getQuantidade());
        venda.setDataVenda(LocalDateTime.parse(vendaDTO.getDataVenda().toString(), dateTimeFormatter));

        return vendaRepository.save(venda);
    }

    public List<Venda> listarVendas() {
        List<Venda> vendas = vendaRepository.findAll();
        if (vendas.isEmpty()) {
            throw new NoContentException("Nenhuma venda disponível.");
        }
        return vendas;
    }

    public Venda atualizarVenda(Long id, VendaDTO vendaDTO) {
        Venda vendaExistente = vendaRepository.findById(id)
            .orElseThrow(() -> new ResourceNotFoundException("Venda não encontrada"));

        Produto produto = produtoRepository.findById(vendaDTO.getIdProduto())
            .orElseThrow(() -> new ResourceNotFoundException("Produto não encontrado"));

        if (vendaDTO.getQuantidade() <= 0) {
            throw new InvalidQuantityException("A quantidade da venda deve ser maior que zero.");
        }

        if (produto.getEstoque() + vendaExistente.getQuantidade() < vendaDTO.getQuantidade()) {
            throw new InvalidQuantityException("Estoque insuficiente para o produto: " + produto.getNome());
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
            .orElseThrow(() -> new VendaNotFoundException("Venda com ID " + id + " não encontrada."));

        Produto produto = venda.getProduto();
        produto.setEstoque(produto.getEstoque() + venda.getQuantidade());
        produtoRepository.save(produto);

        vendaRepository.delete(venda);
    }

    public List<Venda> filtrarVendasPorData(LocalDateTime startDate, LocalDateTime endDate) {
        List<Venda> vendas = vendaRepository.findByDataVendaBetween(startDate, endDate);
        if (vendas.isEmpty()) {
            throw new NoSalesInPeriodException("Nenhuma venda encontrada no período selecionado.");
        }
        return vendas;
    }

    public List<Venda> gerarRelatorioMensal(LocalDateTime mesAno) {
        LocalDateTime startDate = mesAno.withDayOfMonth(1).withHour(0).withMinute(0).withSecond(0);
        LocalDateTime endDate = mesAno.withDayOfMonth(mesAno.toLocalDate().lengthOfMonth()).withHour(23).withMinute(59).withSecond(59);
        List<Venda> vendas = vendaRepository.findByDataVendaBetween(startDate, endDate);
        if (vendas.isEmpty()) {
            throw new NoSalesInPeriodException("Nenhuma venda encontrada no período mensal selecionado.");
        }
        return vendas;
    }

    public List<Venda> gerarRelatorioSemanal(LocalDateTime semana) {
        LocalDateTime startDate = semana.with(java.time.DayOfWeek.MONDAY).withHour(0).withMinute(0).withSecond(0);
        LocalDateTime endDate = semana.with(java.time.DayOfWeek.SUNDAY).withHour(23).withMinute(59).withSecond(59);
        List<Venda> vendas = vendaRepository.findByDataVendaBetween(startDate, endDate);
        if (vendas.isEmpty()) {
            throw new NoSalesInPeriodException("Nenhuma venda encontrada no período semanal selecionado.");
        }
        return vendas;
    }
}