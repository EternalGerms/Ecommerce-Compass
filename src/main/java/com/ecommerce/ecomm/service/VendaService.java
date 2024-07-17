package com.ecommerce.ecomm.service;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import com.ecommerce.ecomm.dto.VendaDTO;
import com.ecommerce.ecomm.entities.Produto;
import com.ecommerce.ecomm.entities.User;
import com.ecommerce.ecomm.entities.Venda;
import com.ecommerce.ecomm.exception.EcommException;
import com.ecommerce.ecomm.exception.ErrorCode;
import com.ecommerce.ecomm.repository.ProdutoRepository;
import com.ecommerce.ecomm.repository.UserRepository;
import com.ecommerce.ecomm.repository.VendaRepository;

@Service
public class VendaService {

	private final VendaRepository vendaRepository;
	private final ProdutoRepository produtoRepository;
	private final DateTimeFormatter dateTimeFormatter = DateTimeFormatter.ISO_DATE_TIME;

	@Autowired
	private UserRepository userRepository; // Adicione esta dependência

	public VendaService(VendaRepository vendaRepository, ProdutoRepository produtoRepository) {
		this.vendaRepository = vendaRepository;
		this.produtoRepository = produtoRepository;
	}

	@CacheEvict(value = "vendas", allEntries = true)
	public Venda criarVenda(VendaDTO vendaDTO) {
		Produto produto = produtoRepository.findById(vendaDTO.getIdProduto())
				.orElseThrow(() -> new EcommException(ErrorCode.PRODUCT_NOT_FOUND));

		if (!produto.isAtivo()) {
			throw new EcommException(ErrorCode.PRODUTO_INATIVO_SALES);
		}

		if (vendaDTO.getQuantidade() <= 0) {
			throw new EcommException(ErrorCode.INVALID_SALES_QUANTITY);
		}

		if (produto.getEstoque() < vendaDTO.getQuantidade()) {
			throw new EcommException(ErrorCode.INSUFFICIENT_STOCK);
		}

		try {
		} catch (DateTimeParseException e) {
			throw new EcommException(ErrorCode.INVALID_DATE);
		}

		produto.setEstoque(produto.getEstoque() - vendaDTO.getQuantidade());
		produtoRepository.save(produto);

		Venda venda = new Venda();
		venda.setProduto(produto);
		venda.setQuantidade(vendaDTO.getQuantidade());
		venda.setDataVenda(LocalDateTime.parse(vendaDTO.getDataVenda().toString(), dateTimeFormatter));

		Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
		String username = authentication.getName();
		User user = userRepository.findByUsername(username);
		venda.setUser(user); // Registre o usuário que realizou a venda

		return vendaRepository.save(venda);
	}

	@Cacheable("vendas")
	public List<Venda> listarVendas() {
		List<Venda> vendas = vendaRepository.findAll();
		if (vendas.isEmpty()) {
			throw new EcommException(ErrorCode.NO_SALES_AVAILABLE);
		}
		return vendas;
	}

	@CacheEvict(value = "vendas", allEntries = true)
	public Venda atualizarVenda(Long id, VendaDTO vendaDTO) {
		Venda vendaExistente = vendaRepository.findById(id)
				.orElseThrow(() -> new EcommException(ErrorCode.VENDA_NOT_FOUND));

		Produto produto = produtoRepository.findById(vendaDTO.getIdProduto())
				.orElseThrow(() -> new EcommException(ErrorCode.VENDA_NOT_FOUND));

		if ((vendaDTO.getQuantidade() <= 0) || (produto.getEstoque() + vendaExistente.getQuantidade() < vendaDTO.getQuantidade())) {
			throw new EcommException(ErrorCode.INVALID_SALES_QUANTITY);
		}
		
		Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String username = authentication.getName();
        User user = userRepository.findByUsername(username);

        if (!vendaExistente.getUser().getId().equals(user.getId())) {
            throw new EcommException(ErrorCode.UNAUTHORIZED_ACCESS);
        }

		produto.setEstoque(produto.getEstoque() + vendaExistente.getQuantidade() - vendaDTO.getQuantidade());
		produtoRepository.save(produto);

		vendaExistente.setProduto(produto);
		vendaExistente.setQuantidade(vendaDTO.getQuantidade());
		vendaExistente.setDataVenda(vendaDTO.getDataVenda());
		vendaExistente.setIdProduto(produto.getId());

		return vendaRepository.save(vendaExistente);
	}

	@CacheEvict(value = "vendas", allEntries = true)
	public void deletarVenda(Long id) {
		Venda venda = vendaRepository.findById(id).orElseThrow(() -> new EcommException(ErrorCode.VENDA_NOT_FOUND));
		
		Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String username = authentication.getName();
        User user = userRepository.findByUsername(username);

        if (!venda.getUser().getId().equals(user.getId())) {
            throw new EcommException(ErrorCode.UNAUTHORIZED_ACCESS);
        }

		Produto produto = venda.getProduto();
		produto.setEstoque(produto.getEstoque() + venda.getQuantidade());
		produtoRepository.save(produto);

		vendaRepository.delete(venda);
	}

	@Cacheable("vendas")
	public List<Venda> filtrarVendasPorData(LocalDateTime startDate, LocalDateTime endDate) {
		List<Venda> vendas = vendaRepository.findByDataVendaBetween(startDate, endDate);
		if (vendas.isEmpty()) {
			throw new EcommException(ErrorCode.NO_SALES_IN_PERIOD);
		}
		return vendas;
	}

	@Cacheable("vendas")
	public List<Venda> gerarRelatorioMensal(LocalDateTime mesAno) {
		LocalDateTime startDate = mesAno.withDayOfMonth(1).withHour(0).withMinute(0).withSecond(0);
		LocalDateTime endDate = mesAno.withDayOfMonth(mesAno.toLocalDate().lengthOfMonth()).withHour(23).withMinute(59)
				.withSecond(59);
		List<Venda> vendas = vendaRepository.findByDataVendaBetween(startDate, endDate);
		if (vendas.isEmpty()) {
			throw new EcommException(ErrorCode.NO_SALES_IN_PERIOD);
		}
		return vendas;
	}

	@Cacheable("vendas")
	public List<Venda> gerarRelatorioSemanal(LocalDateTime semana) {
		LocalDateTime startDate = semana.with(java.time.DayOfWeek.MONDAY).withHour(0).withMinute(0).withSecond(0);
		LocalDateTime endDate = semana.with(java.time.DayOfWeek.SUNDAY).withHour(23).withMinute(59).withSecond(59);
		List<Venda> vendas = vendaRepository.findByDataVendaBetween(startDate, endDate);
		if (vendas.isEmpty()) {
			throw new EcommException(ErrorCode.NO_SALES_IN_PERIOD);
		}
		return vendas;
	}
}