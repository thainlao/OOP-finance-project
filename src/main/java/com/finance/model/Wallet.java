package com.finance.model;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class Wallet {
    private List<Transaction> transactions;
    private List<Budget> budgets;

    public Wallet() {
        this.transactions = new ArrayList<>();
        this.budgets = new ArrayList<>();
    }

    public void addTransaction(Transaction transaction) {
        transactions.add(transaction);
    }

    public void addBudget(Budget budget) {
        // Remove existing budget for the same category
        budgets.removeIf(b -> b.getCategory().equals(budget.getCategory()));
        budgets.add(budget);
    }

    public double getBalance() {
        double income = transactions.stream()
                .filter(t -> t.getType() == Transaction.Type.INCOME)
                .mapToDouble(Transaction::getAmount)
                .sum();
        double expenses = transactions.stream()
                .filter(t -> t.getType() == Transaction.Type.EXPENSE)
                .mapToDouble(Transaction::getAmount)
                .sum();
        return income - expenses;
    }

    public double getTotalIncome() {
        return transactions.stream()
                .filter(t -> t.getType() == Transaction.Type.INCOME)
                .mapToDouble(Transaction::getAmount)
                .sum();
    }

    public double getTotalExpenses() {
        return transactions.stream()
                .filter(t -> t.getType() == Transaction.Type.EXPENSE)
                .mapToDouble(Transaction::getAmount)
                .sum();
    }

    public List<Transaction> getTransactions() { return transactions; }
    public List<Budget> getBudgets() { return budgets; }

    public Optional<Budget> getBudgetForCategory(String category) {
        return budgets.stream()
                .filter(b -> b.getCategory().equals(category))
                .findFirst();
    }

    public double getExpensesForCategory(String category) {
        return transactions.stream()
                .filter(t -> t.getType() == Transaction.Type.EXPENSE)
                .filter(t -> t.getCategory().equals(category))
                .mapToDouble(Transaction::getAmount)
                .sum();
    }
}