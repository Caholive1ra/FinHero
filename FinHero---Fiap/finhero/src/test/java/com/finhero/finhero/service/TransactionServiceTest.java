package com.finhero.finhero.service;

import com.finhero.finhero.dto.CreateTransactionDTO;
import com.finhero.finhero.dto.TransactionResponse;
import com.finhero.finhero.exception.InvalidCategoryException;
import com.finhero.finhero.exception.UserNotInDuplaException;
import com.finhero.finhero.model.Category;
import com.finhero.finhero.model.Dupla;
import com.finhero.finhero.model.Transaction;
import com.finhero.finhero.repository.CategoryRepository;
import com.finhero.finhero.repository.DuplaRepository;
import com.finhero.finhero.repository.TransactionRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class TransactionServiceTest {

    @Mock
    private TransactionRepository transactionRepository;

    @Mock
    private CategoryRepository categoryRepository;

    @Mock
    private DuplaRepository duplaRepository;

    @InjectMocks
    private TransactionService transactionService;

    private Category category;
    private Dupla dupla;
    private Transaction transaction;
    private CreateTransactionDTO createDTO;
    private static final Long USER_ID = 1L;
    private static final Long CATEGORY_ID = 1L;

    @BeforeEach
    void setUp() {
        category = new Category();
        category.setId(CATEGORY_ID);
        category.setName("Alimentação");
        category.setUserId(USER_ID);
        category.setCreatedAt(LocalDateTime.now());

        dupla = new Dupla();
        dupla.setId(1L);
        dupla.setUserAId(USER_ID);
        dupla.setUserBId(2L);
        dupla.setCreatedAt(LocalDateTime.now());

        transaction = new Transaction();
        transaction.setId(1L);
        transaction.setType(Transaction.TransactionType.DESPESA);
        transaction.setAmount(BigDecimal.valueOf(100.50));
        transaction.setDescription("Supermercado");
        transaction.setCategoryId(CATEGORY_ID);
        transaction.setUserId(USER_ID);
        transaction.setCreatedAt(LocalDateTime.now());
        transaction.setUpdatedAt(LocalDateTime.now());

        createDTO = new CreateTransactionDTO();
        createDTO.setType(Transaction.TransactionType.DESPESA);
        createDTO.setAmount(BigDecimal.valueOf(100.50));
        createDTO.setDescription("Supermercado");
        createDTO.setCategoryId(CATEGORY_ID);
    }

    @Test
    void testCreateTransaction_WithValidData_ShouldCreateTransaction() {
        when(duplaRepository.existsByUserId(anyLong())).thenReturn(true);
        when(categoryRepository.findById(anyLong())).thenReturn(Optional.of(category));
        when(transactionRepository.save(any(Transaction.class))).thenReturn(transaction);

        TransactionResponse response = transactionService.createTransaction(USER_ID, createDTO);

        assertNotNull(response);
        assertEquals(transaction.getId(), response.getId());
        assertEquals(transaction.getType(), response.getType());
        assertEquals(transaction.getAmount(), response.getAmount());
        verify(transactionRepository, times(1)).save(any(Transaction.class));
    }

    @Test
    void testCreateTransaction_UserNotInDupla_ShouldThrowException() {
        when(duplaRepository.existsByUserId(anyLong())).thenReturn(false);

        assertThrows(
            UserNotInDuplaException.class,
            () -> transactionService.createTransaction(USER_ID, createDTO)
        );

        verify(transactionRepository, never()).save(any(Transaction.class));
    }

    @Test
    void testCreateTransaction_CategoryNotFound_ShouldThrowException() {
        when(duplaRepository.existsByUserId(anyLong())).thenReturn(true);
        when(categoryRepository.findById(anyLong())).thenReturn(Optional.empty());

        assertThrows(
            InvalidCategoryException.class,
            () -> transactionService.createTransaction(USER_ID, createDTO)
        );

        verify(transactionRepository, never()).save(any(Transaction.class));
    }

    @Test
    void testCreateTransaction_CategoryNotBelongsToUser_ShouldThrowException() {
        Category otherUserCategory = new Category();
        otherUserCategory.setId(CATEGORY_ID);
        otherUserCategory.setUserId(2L);

        when(duplaRepository.existsByUserId(anyLong())).thenReturn(true);
        when(categoryRepository.findById(anyLong())).thenReturn(Optional.of(otherUserCategory));

        assertThrows(
            InvalidCategoryException.class,
            () -> transactionService.createTransaction(USER_ID, createDTO)
        );

        verify(transactionRepository, never()).save(any(Transaction.class));
    }

    @Test
    void testCreateTransaction_ReceitaType_ShouldWork() {
        createDTO.setType(Transaction.TransactionType.RECEITA);
        transaction.setType(Transaction.TransactionType.RECEITA);

        when(duplaRepository.existsByUserId(anyLong())).thenReturn(true);
        when(categoryRepository.findById(anyLong())).thenReturn(Optional.of(category));
        when(transactionRepository.save(any(Transaction.class))).thenReturn(transaction);

        TransactionResponse response = transactionService.createTransaction(USER_ID, createDTO);

        assertNotNull(response);
        assertEquals(Transaction.TransactionType.RECEITA, response.getType());
    }

    @Test
    void testGetTransactions_ShouldReturnPageableTransactions() {
        Pageable pageable = PageRequest.of(0, 10);
        Page<Transaction> transactionPage = new PageImpl<>(Arrays.asList(transaction));

        when(transactionRepository.findByUserIdOrderByCreatedAtDesc(anyLong(), any(Pageable.class)))
            .thenReturn(transactionPage);

        Page<TransactionResponse> result = transactionService.getTransactions(USER_ID, pageable);

        assertNotNull(result);
        assertEquals(1, result.getTotalElements());
        verify(transactionRepository, times(1)).findByUserIdOrderByCreatedAtDesc(anyLong(), any(Pageable.class));
    }

    @Test
    void testGetAllTransactions_ShouldReturnAllTransactions() {
        List<Transaction> transactions = Arrays.asList(transaction);

        when(transactionRepository.findByUserIdOrderByCreatedAtDesc(anyLong())).thenReturn(transactions);

        List<TransactionResponse> result = transactionService.getAllTransactions(USER_ID);

        assertNotNull(result);
        assertEquals(1, result.size());
        assertEquals(transaction.getId(), result.get(0).getId());
        verify(transactionRepository, times(1)).findByUserIdOrderByCreatedAtDesc(anyLong());
    }

    @Test
    void testCreateTransaction_NullDescription_ShouldWork() {
        createDTO.setDescription(null);

        when(duplaRepository.existsByUserId(anyLong())).thenReturn(true);
        when(categoryRepository.findById(anyLong())).thenReturn(Optional.of(category));
        when(transactionRepository.save(any(Transaction.class))).thenReturn(transaction);

        TransactionResponse response = transactionService.createTransaction(USER_ID, createDTO);

        assertNotNull(response);
    }
}

