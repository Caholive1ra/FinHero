package model;

import java.math.BigDecimal;
import java.time.LocalDate;

public class Transaction {
    private Long id;
    private Long accountId;
    private Long categoryId;
    private TransactionType transactionType;
    private BigDecimal amount;
    private LocalDate date;
    private String description;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getAccountId() {
        return accountId;
    }

    public void setAccountId(Long accountId) {
        this.accountId = accountId;
    }

    public Long getCategoryId() {
        return categoryId;
    }

    public void setCategoryId(Long categoryId) {
        this.categoryId = categoryId;
    }

    public TransactionType getTransactionType() {
        return transactionType;
    }

    public void setTransactionType(TransactionType transactionType) {
        this.transactionType = transactionType;
    }

    public BigDecimal getAmount() {
        return amount;
    }

    public void setAmount(BigDecimal amount) {
        this.amount = amount;
    }

    public LocalDate getDate() {
        return date;
    }

    public void setDate(LocalDate date) {
        this.date = date;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public Transaction createTransaction() {
        System.out.println("Criando Transação!");
        return null;
    }

    public void deleteTransaction() {
        System.out.println("Deletando Transação!");
    }

    public Transaction updateTransaction() {
        System.out.println("Atualizando Transação!");
        return null;
    }

}