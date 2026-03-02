package com.finhero.finhero.dto;

import com.finhero.finhero.model.Transaction;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class TransactionResponse {
    
    private Long id;
    private Transaction.TransactionType type;
    private BigDecimal amount;
    private String description;
    private Long categoryId;
    private Long userId;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}

