package com.finhero.finhero.service;

import com.finhero.finhero.dto.CreateTransactionDTO;
import com.finhero.finhero.dto.TransactionResponse;
import com.finhero.finhero.exception.InvalidCategoryException;
import com.finhero.finhero.exception.UserNotInDuplaException;
import com.finhero.finhero.model.Category;
import com.finhero.finhero.model.Transaction;
import com.finhero.finhero.repository.CategoryRepository;
import com.finhero.finhero.repository.DuplaRepository;
import com.finhero.finhero.repository.TransactionRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
@Slf4j
public class TransactionService {
    
    @Autowired
    private TransactionRepository transactionRepository;
    
    @Autowired
    private CategoryRepository categoryRepository;
    
    @Autowired
    private DuplaRepository duplaRepository;
    
    @Transactional
    public TransactionResponse createTransaction(Long userId, CreateTransactionDTO dto) {
        log.info("Criando transação para usuário: {}", userId);
        
        if (!duplaRepository.existsByUserId(userId)) {
            log.warn("Usuário {} não está em uma dupla", userId);
            throw new UserNotInDuplaException("Você precisa estar em uma dupla para registrar transações");
        }
        
        Category category = categoryRepository.findById(dto.getCategoryId())
            .orElseThrow(() -> {
                log.warn("Categoria não encontrada: {}", dto.getCategoryId());
                throw new InvalidCategoryException("Categoria inválida");
            });
        
        if (!category.getUserId().equals(userId)) {
            log.warn("Categoria {} não pertence ao usuário {}", dto.getCategoryId(), userId);
            throw new InvalidCategoryException("Categoria inválida");
        }
        
        Transaction transaction = new Transaction();
        transaction.setType(dto.getType());
        transaction.setAmount(dto.getAmount());
        transaction.setDescription(dto.getDescription());
        transaction.setCategoryId(dto.getCategoryId());
        transaction.setUserId(userId);
        transaction.setCreatedAt(LocalDateTime.now());
        transaction.setUpdatedAt(LocalDateTime.now());
        
        Transaction savedTransaction = transactionRepository.save(transaction);
        
        log.info("Transação criada com sucesso: id={}, userId={}, type={}, amount={}", 
                 savedTransaction.getId(), savedTransaction.getUserId(), 
                 savedTransaction.getType(), savedTransaction.getAmount());
        
        return new TransactionResponse(
            savedTransaction.getId(),
            savedTransaction.getType(),
            savedTransaction.getAmount(),
            savedTransaction.getDescription(),
            savedTransaction.getCategoryId(),
            savedTransaction.getUserId(),
            savedTransaction.getCreatedAt(),
            savedTransaction.getUpdatedAt()
        );
    }
    
    public Page<TransactionResponse> getTransactions(Long userId, Pageable pageable) {
        Page<Transaction> transactions = transactionRepository.findByUserIdOrderByCreatedAtDesc(userId, pageable);
        
        return transactions.map(t -> new TransactionResponse(
            t.getId(),
            t.getType(),
            t.getAmount(),
            t.getDescription(),
            t.getCategoryId(),
            t.getUserId(),
            t.getCreatedAt(),
            t.getUpdatedAt()
        ));
    }
    
    public List<TransactionResponse> getAllTransactions(Long userId) {
        List<Transaction> transactions = transactionRepository.findByUserIdOrderByCreatedAtDesc(userId);
        
        return transactions.stream()
            .map(t -> new TransactionResponse(
                t.getId(),
                t.getType(),
                t.getAmount(),
                t.getDescription(),
                t.getCategoryId(),
                t.getUserId(),
                t.getCreatedAt(),
                t.getUpdatedAt()
            ))
            .collect(Collectors.toList());
    }
}

