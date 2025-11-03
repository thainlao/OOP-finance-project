package com.finance.model;

import java.util.Objects;

public class Budget {
    private String category;
    private double limit;
    private String username;

    public Budget() {}

    public Budget(String category, double limit, String username) {
        this.category = category;
        this.limit = limit;
        this.username = username;
    }

    // Getters and setters
    public String getCategory() { return category; }
    public void setCategory(String category) { this.category = category; }

    public double getLimit() { return limit; }
    public void setLimit(double limit) { this.limit = limit; }

    public String getUsername() { return username; }
    public void setUsername(String username) { this.username = username; }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Budget budget = (Budget) o;
        return Objects.equals(category, budget.category) && 
               Objects.equals(username, budget.username);
    }

    @Override
    public int hashCode() {
        return Objects.hash(category, username);
    }
}