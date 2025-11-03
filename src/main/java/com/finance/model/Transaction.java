package com.finance.model;

import java.time.LocalDateTime;
import java.util.Objects;

public class Transaction {
    public enum Type { INCOME, EXPENSE }
    
    private String id;
    private Type type;
    private String category;
    private double amount;
    private String description;
    private LocalDateTime date;
    private String username;

    public Transaction() {}

    public Transaction(Type type, String category, double amount, String description, String username) {
        this.id = java.util.UUID.randomUUID().toString();
        this.type = type;
        this.category = category;
        this.amount = amount;
        this.description = description;
        this.date = LocalDateTime.now();
        this.username = username;
    }

    // Getters and setters
    public String getId() { return id; }
    public void setId(String id) { this.id = id; }

    public Type getType() { return type; }
    public void setType(Type type) { this.type = type; }

    public String getCategory() { return category; }
    public void setCategory(String category) { this.category = category; }

    public double getAmount() { return amount; }
    public void setAmount(double amount) { this.amount = amount; }

    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }

    public LocalDateTime getDate() { return date; }
    public void setDate(LocalDateTime date) { this.date = date; }

    public String getUsername() { return username; }
    public void setUsername(String username) { this.username = username; }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Transaction that = (Transaction) o;
        return Objects.equals(id, that.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }
}