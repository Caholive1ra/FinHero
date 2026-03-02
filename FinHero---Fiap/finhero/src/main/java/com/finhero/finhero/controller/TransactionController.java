package com.finhero.finhero.controller;

import com.finhero.finhero.dto.CreateTransactionDTO;
import com.finhero.finhero.dto.TransactionResponse;
import com.finhero.finhero.service.TransactionService;
import com.finhero.finhero.util.SecurityUtil;
import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/transactions")
@Slf4j
public class TransactionController {
    
    @Autowired
    private TransactionService transactionService;
    
    @PostMapping
    public ResponseEntity<TransactionResponse> createTransaction(@Valid @RequestBody CreateTransactionDTO dto) {
        log.info("Recebida requisição de criação de transação");
        
        Long userId = SecurityUtil.getCurrentUserId();
        TransactionResponse response = transactionService.createTransaction(userId, dto);
        
        log.info("Transação criada com sucesso");
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }
    
    @GetMapping
    public ResponseEntity<Page<TransactionResponse>> getTransactions(
            @RequestParam(required = false, defaultValue = "0") int page,
            @RequestParam(required = false, defaultValue = "20") int size) {
        
        log.info("Recebida requisição de listagem de transações");
        
        Long userId = SecurityUtil.getCurrentUserId();
        Pageable pageable = PageRequest.of(page, size);
        Page<TransactionResponse> transactions = transactionService.getTransactions(userId, pageable);
        
        log.info("Retornando {} transações para usuário: {}", transactions.getContent().size(), userId);
        return ResponseEntity.ok(transactions);
    }
}

