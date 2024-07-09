package com.ecommerce.ecomm.controller;

import com.ecommerce.ecomm.dto.VendaDTO;
import com.ecommerce.ecomm.entities.Venda;
import com.ecommerce.ecomm.service.VendaService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.time.OffsetDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.List;

@RestController
@RequestMapping("/api/vendas")
public class VendaController {

    private final VendaService vendaService;
    private final DateTimeFormatter dateTimeFormatter = DateTimeFormatter.ISO_DATE_TIME;

    public VendaController(VendaService vendaService) {
        this.vendaService = vendaService;
    }

    @PostMapping
    public ResponseEntity<?> criarVenda(@Valid @RequestBody VendaDTO vendaDTO) {
        try {
            Venda novaVenda = vendaService.criarVenda(vendaDTO);
            return new ResponseEntity<>(novaVenda, HttpStatus.CREATED);
        } catch (IllegalArgumentException e) {
            return new ResponseEntity<>(e.getMessage(), HttpStatus.BAD_REQUEST);
        }
    }

    @GetMapping
    public ResponseEntity<List<Venda>> listarVendas() {
        List<Venda> vendas = vendaService.listarVendas();
        return new ResponseEntity<>(vendas, HttpStatus.OK);
    }

    @PutMapping("/{id}")
    public ResponseEntity<?> atualizarVenda(@PathVariable Long id, @Valid @RequestBody VendaDTO vendaDTO) {
        try {
            Venda vendaAtualizada = vendaService.atualizarVenda(id, vendaDTO);
            return new ResponseEntity<>(vendaAtualizada, HttpStatus.OK);
        } catch (IllegalArgumentException e) {
            return new ResponseEntity<>(e.getMessage(), HttpStatus.BAD_REQUEST);
        }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deletarVenda(@PathVariable Long id) {
        vendaService.deletarVenda(id);
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }

    @GetMapping("/filtrar")
    public ResponseEntity<List<Venda>> filtrarVendasPorData(
            @RequestParam String startDate,
            @RequestParam String endDate) {
        try {
            OffsetDateTime startOffsetDateTime = OffsetDateTime.parse(startDate, dateTimeFormatter);
            OffsetDateTime endOffsetDateTime = OffsetDateTime.parse(endDate, dateTimeFormatter);
            LocalDateTime startLocalDateTime = startOffsetDateTime.toLocalDateTime();
            LocalDateTime endLocalDateTime = endOffsetDateTime.toLocalDateTime();
            List<Venda> vendas = vendaService.filtrarVendasPorData(startLocalDateTime, endLocalDateTime);
            return new ResponseEntity<>(vendas, HttpStatus.OK);
        } catch (DateTimeParseException e) {
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        }
    }

    @GetMapping("/relatorio/mensal")
    public ResponseEntity<List<Venda>> gerarRelatorioMensal(
            @RequestParam String mesAno) {
        try {
            OffsetDateTime mesAnoOffsetDateTime = OffsetDateTime.parse(mesAno, dateTimeFormatter);
            LocalDateTime mesAnoLocalDateTime = mesAnoOffsetDateTime.toLocalDateTime();
            List<Venda> vendas = vendaService.gerarRelatorioMensal(mesAnoLocalDateTime);
            return new ResponseEntity<>(vendas, HttpStatus.OK);
        } catch (DateTimeParseException e) {
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        }
    }

    @GetMapping("/relatorio/semanal")
    public ResponseEntity<List<Venda>> gerarRelatorioSemanal(
            @RequestParam String semana) {
        try {
            OffsetDateTime semanaOffsetDateTime = OffsetDateTime.parse(semana, dateTimeFormatter);
            LocalDateTime semanaLocalDateTime = semanaOffsetDateTime.toLocalDateTime();
            List<Venda> vendas = vendaService.gerarRelatorioSemanal(semanaLocalDateTime);
            return new ResponseEntity<>(vendas, HttpStatus.OK);
        } catch (DateTimeParseException e) {
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        }
    }
}