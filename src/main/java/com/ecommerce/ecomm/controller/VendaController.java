package com.ecommerce.ecomm.controller;

import com.ecommerce.ecomm.dto.VendaDTO;
import com.ecommerce.ecomm.entities.Venda;
import com.ecommerce.ecomm.exception.InsufficientStockException;
import com.ecommerce.ecomm.exception.InvalidDateException;
import com.ecommerce.ecomm.exception.InvalidQuantityException;
import com.ecommerce.ecomm.exception.NoContentException;
import com.ecommerce.ecomm.exception.NoSalesInPeriodException;
import com.ecommerce.ecomm.exception.ProdutoInativoException;
import com.ecommerce.ecomm.exception.VendaNotFoundException;
import com.ecommerce.ecomm.model.ErrorResponse;
import com.ecommerce.ecomm.service.VendaService;
import com.fasterxml.jackson.databind.exc.InvalidFormatException;
import jakarta.validation.Valid;
import org.springframework.data.rest.webmvc.ResourceNotFoundException;
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
        } catch (InvalidDateException e) {
            return new ResponseEntity<>(new ErrorResponse(400, "Bad Request", e.getMessage()), HttpStatus.BAD_REQUEST);
        } catch (ResourceNotFoundException e) {
            return new ResponseEntity<>(new ErrorResponse(404, "Not Found", e.getMessage()), HttpStatus.NOT_FOUND);
        } catch (InsufficientStockException e) {
            return new ResponseEntity<>(new ErrorResponse(400, "Bad Request", e.getMessage()), HttpStatus.BAD_REQUEST);
        } catch (ProdutoInativoException e) {
            return new ResponseEntity<>(new ErrorResponse(400, "Bad Request", e.getMessage()), HttpStatus.BAD_REQUEST);
        } catch (Exception e) {
            return new ResponseEntity<>(new ErrorResponse(500, "Internal Server Error", "Ocorreu um erro inesperado"), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @ExceptionHandler(InsufficientStockException.class)
    public ResponseEntity<ErrorResponse> handleInsufficientStockException(InsufficientStockException ex) {
        return new ResponseEntity<>(new ErrorResponse(400, "Bad Request", ex.getMessage()), HttpStatus.BAD_REQUEST);
    }

    @GetMapping
    public ResponseEntity<?> listarVendas() {
        try {
            List<Venda> vendas = vendaService.listarVendas();
            return new ResponseEntity<>(vendas, HttpStatus.OK);
        } catch (NoContentException e) {
            return new ResponseEntity<>(new ErrorResponse(204, "No Content", e.getMessage()), HttpStatus.OK);
        }
    }

    @PutMapping("/{id}")
    public ResponseEntity<?> atualizarVenda(@PathVariable Long id, @Valid @RequestBody VendaDTO vendaDTO) {
        try {
            Venda vendaAtualizada = vendaService.atualizarVenda(id, vendaDTO);
            return new ResponseEntity<>(vendaAtualizada, HttpStatus.OK);
        } catch (ResourceNotFoundException e) {
            return new ResponseEntity<>(new ErrorResponse(404, "Not Found", e.getMessage()), HttpStatus.NOT_FOUND);
        } catch (InvalidQuantityException e) {
            return new ResponseEntity<>(new ErrorResponse(400, "Bad Request", e.getMessage()), HttpStatus.BAD_REQUEST);
        }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deletarVenda(@PathVariable Long id) {
        try {
            vendaService.deletarVenda(id);
            return new ResponseEntity<>(HttpStatus.NO_CONTENT);
        } catch (VendaNotFoundException e) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
    }

    @ExceptionHandler(VendaNotFoundException.class)
    public ResponseEntity<ErrorResponse> handleVendaNotFoundException(VendaNotFoundException ex) {
        return new ResponseEntity<>(new ErrorResponse(404, "Not Found", ex.getMessage()), HttpStatus.NOT_FOUND);
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
        } catch (NoSalesInPeriodException e) {
            return new ResponseEntity<>(new ErrorResponse(404, "Not Found", e.getMessage()), HttpStatus.NOT_FOUND);
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
        } catch (NoSalesInPeriodException e) {
            return new ResponseEntity<>(new ErrorResponse(404, "Not Found", e.getMessage()), HttpStatus.NOT_FOUND);
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
        } catch (NoSalesInPeriodException e) {
            return new ResponseEntity<>(new ErrorResponse(404, "Not Found", e.getMessage()), HttpStatus.NOT_FOUND);
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