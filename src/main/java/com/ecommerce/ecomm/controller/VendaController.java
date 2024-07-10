package com.ecommerce.ecomm.controller;

import java.time.LocalDateTime;
import java.time.OffsetDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.ecommerce.ecomm.dto.VendaDTO;
import com.ecommerce.ecomm.entities.Venda;
import com.ecommerce.ecomm.exception.EcommException;
import com.ecommerce.ecomm.model.ErrorResponse;
import com.ecommerce.ecomm.service.VendaService;
import com.fasterxml.jackson.databind.exc.InvalidFormatException;

import jakarta.validation.Valid;

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
        } catch (EcommException e) {
            return new ResponseEntity<>(new ErrorResponse(e.getErrorCode().getCode(), e.getErrorCode().getStatus(), e.getMessage()), HttpStatus.valueOf(e.getErrorCode().getCode()));
        } catch (Exception e) {
            return new ResponseEntity<>(new ErrorResponse(500, "Internal Server Error", "Ocorreu um erro inesperado"), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @GetMapping
    public ResponseEntity<?> listarVendas() {
        try {
            List<Venda> vendas = vendaService.listarVendas();
            return new ResponseEntity<>(vendas, HttpStatus.OK);
        } catch (EcommException e) {
            return new ResponseEntity<>(new ErrorResponse(e.getErrorCode().getCode(), e.getErrorCode().getStatus(), e.getMessage()), HttpStatus.valueOf(e.getErrorCode().getCode()));
        }
    }

    @PutMapping("/{id}")
    public ResponseEntity<?> atualizarVenda(@PathVariable Long id, @Valid @RequestBody VendaDTO vendaDTO) {
        try {
            Venda vendaAtualizada = vendaService.atualizarVenda(id, vendaDTO);
            return new ResponseEntity<>(vendaAtualizada, HttpStatus.OK);
        } catch (EcommException e) {
            return new ResponseEntity<>(new ErrorResponse(e.getErrorCode().getCode(), e.getErrorCode().getStatus(), e.getMessage()), HttpStatus.valueOf(e.getErrorCode().getCode()));
        }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deletarVenda(@PathVariable Long id) {
        try {
            vendaService.deletarVenda(id);
            return new ResponseEntity<>(HttpStatus.NO_CONTENT);
        } catch (EcommException e) {
            return new ResponseEntity<>(HttpStatus.valueOf(e.getErrorCode().getCode()));
        }
    }

    @GetMapping("/filtrar")
    public ResponseEntity<?> filtrarVendasPorData(
            @RequestParam String startDate,
            @RequestParam String endDate) {
        try {
            LocalDateTime startDateTime = LocalDateTime.parse(startDate, dateTimeFormatter);
            LocalDateTime endDateTime = LocalDateTime.parse(endDate, dateTimeFormatter);
            List<Venda> vendas = vendaService.filtrarVendasPorData(startDateTime, endDateTime);
            return new ResponseEntity<>(vendas, HttpStatus.OK);
        } catch (DateTimeParseException e) {
            return new ResponseEntity<>(new ErrorResponse(400, "Bad Request", "Data inválida ou no formato errado. O formato esperado é: yyyy-MM-dd'T'HH:mm:ss'Z'"), HttpStatus.BAD_REQUEST);
        } catch (EcommException e) {
            return new ResponseEntity<>(new ErrorResponse(e.getErrorCode().getCode(), e.getErrorCode().getStatus(), e.getMessage()), HttpStatus.valueOf(e.getErrorCode().getCode()));
        } catch (Exception e) {
            return new ResponseEntity<>(new ErrorResponse(500, "Internal Server Error", "Ocorreu um erro inesperado"), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @GetMapping("/relatorio/mensal")
    public ResponseEntity<?> gerarRelatorioMensal(
            @RequestParam String mesAno) {
        try {
            OffsetDateTime mesAnoOffsetDateTime = OffsetDateTime.parse(mesAno, dateTimeFormatter);
            LocalDateTime mesAnoLocalDateTime = mesAnoOffsetDateTime.toLocalDateTime();
            List<Venda> vendas = vendaService.gerarRelatorioMensal(mesAnoLocalDateTime);
            return new ResponseEntity<>(vendas, HttpStatus.OK);
        } catch (DateTimeParseException e) {
            return new ResponseEntity<>(new ErrorResponse(400, "Bad Request", "Data inválida ou no formato errado. O formato esperado é: yyyy-MM-dd'T'HH:mm:ss'Z'"), HttpStatus.BAD_REQUEST);
        } catch (EcommException e) {
            return new ResponseEntity<>(new ErrorResponse(e.getErrorCode().getCode(), e.getErrorCode().getStatus(), e.getMessage()), HttpStatus.valueOf(e.getErrorCode().getCode()));
        } catch (Exception e) {
            return new ResponseEntity<>(new ErrorResponse(500, "Internal Server Error", "Ocorreu um erro inesperado"), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @GetMapping("/relatorio/semanal")
    public ResponseEntity<?> gerarRelatorioSemanal(
            @RequestParam String semana) {
        try {
            OffsetDateTime semanaOffsetDateTime = OffsetDateTime.parse(semana, dateTimeFormatter);
            LocalDateTime semanaLocalDateTime = semanaOffsetDateTime.toLocalDateTime();
            List<Venda> vendas = vendaService.gerarRelatorioSemanal(semanaLocalDateTime);
            return new ResponseEntity<>(vendas, HttpStatus.OK);
        } catch (DateTimeParseException e) {
            return new ResponseEntity<>(new ErrorResponse(400, "Bad Request", "Data inválida ou no formato errado. O formato esperado é: yyyy-MM-dd'T'HH:mm:ss'Z'"), HttpStatus.BAD_REQUEST);
        } catch (EcommException e) {
            return new ResponseEntity<>(new ErrorResponse(e.getErrorCode().getCode(), e.getErrorCode().getStatus(), e.getMessage()), HttpStatus.valueOf(e.getErrorCode().getCode()));
        } catch (Exception e) {
            return new ResponseEntity<>(new ErrorResponse(500, "Internal Server Error", "Ocorreu um erro inesperado"), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @ExceptionHandler({InvalidFormatException.class, DateTimeParseException.class})
    public ResponseEntity<ErrorResponse> handleInvalidFormatException(Exception ex) {
        if (ex instanceof InvalidFormatException && ((InvalidFormatException) ex).getTargetType().equals(LocalDateTime.class)) {
            return new ResponseEntity<>(new ErrorResponse(400, "Bad Request", "Data inválida ou no formato errado. O formato esperado é: yyyy-MM-dd'T'HH:mm:ss'Z'"), HttpStatus.BAD_REQUEST);
        } else if (ex instanceof DateTimeParseException) {
            return new ResponseEntity<>(new ErrorResponse(400, "Bad Request", "Data inválida ou no formato errado. O formato esperado é: yyyy-MM-dd'T'HH:mm:ss'Z'"), HttpStatus.BAD_REQUEST);
        }
        return new ResponseEntity<>(new ErrorResponse(400, "Bad Request", "Formato inválido"), HttpStatus.BAD_REQUEST);
    }
}