package com.ecommerce.ecomm.controller;

import java.time.LocalDate;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.ecommerce.ecomm.dto.VendaDTO;
import com.ecommerce.ecomm.entities.Venda;
import com.ecommerce.ecomm.service.VendaService;

@RestController
@RequestMapping("/api/vendas")
public class VendaController {

    @Autowired
    private VendaService vendaService;

    @PostMapping
    public ResponseEntity<?> criarVenda(@RequestBody VendaDTO vendaDTO) {
        try {
            Venda venda = vendaService.criarVenda(vendaDTO);
            return new ResponseEntity<>(venda, HttpStatus.CREATED);
        } catch (IllegalArgumentException e) {
            return new ResponseEntity<>(e.getMessage(), HttpStatus.BAD_REQUEST);
        }
    }

    @GetMapping
    public ResponseEntity<List<Venda>> listarVendas() {
        List<Venda> vendas = vendaService.listarVendas();
        return new ResponseEntity<>(vendas, HttpStatus.OK);
    }

    @GetMapping("/filtrar")
    public ResponseEntity<List<Venda>> filtrarVendasPorData(
            @RequestParam("startDate") @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate startDate,
            @RequestParam("endDate") @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate endDate) {
        List<Venda> vendas = vendaService.filtrarVendasPorData(startDate, endDate);
        return new ResponseEntity<>(vendas, HttpStatus.OK);
    }

    @GetMapping("/relatorio-mensal")
    public ResponseEntity<List<Venda>> gerarRelatorioMensal(
            @RequestParam("mesAno") @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate mesAno) {
        List<Venda> vendas = vendaService.gerarRelatorioMensal(mesAno);
        return new ResponseEntity<>(vendas, HttpStatus.OK);
    }

    @GetMapping("/relatorio-semanal")
    public ResponseEntity<List<Venda>> gerarRelatorioSemanal(
            @RequestParam("semana") @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate semana) {
        List<Venda> vendas = vendaService.gerarRelatorioSemanal(semana);
        return new ResponseEntity<>(vendas, HttpStatus.OK);
    }
}