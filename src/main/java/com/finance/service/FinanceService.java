package com.finance.service;

import com.finance.model.*;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

public class FinanceService {
    private AuthService authService;

    public FinanceService(AuthService authService) {
        this.authService = authService;
    }

    public boolean addIncome(String category, double amount, String description) {
        // Добавляем проверку на null и пустую категорию
        if (!authService.isLoggedIn() || amount <= 0 || category == null || category.trim().isEmpty()) {
            return false;
        }
        
        User user = authService.getCurrentUser();
        Transaction transaction = new Transaction(
            Transaction.Type.INCOME, category.trim(), amount, description, user.getUsername()
        );
        user.getWallet().addTransaction(transaction);
        return true;
    }

    public boolean addExpense(String category, double amount, String description) {
        // Добавляем проверку на null и пустую категорию
        if (!authService.isLoggedIn() || amount <= 0 || category == null || category.trim().isEmpty()) {
            return false;
        }
        
        User user = authService.getCurrentUser();
        Transaction transaction = new Transaction(
            Transaction.Type.EXPENSE, category.trim(), amount, description, user.getUsername()
        );
        user.getWallet().addTransaction(transaction);
        
        // Check budget alert
        checkBudgetAlert(category);
        checkOverallBudgetAlert();
        
        return true;
    }

    public boolean setBudget(String category, double limit) {
        // Также добавляем проверку для setBudget для консистентности
        if (!authService.isLoggedIn() || limit <= 0 || category == null || category.trim().isEmpty()) {
            return false;
        }
        
        User user = authService.getCurrentUser();
        Budget budget = new Budget(category.trim(), limit, user.getUsername());
        user.getWallet().addBudget(budget);
        return true;
    }

    public double getBalance() {
        return authService.isLoggedIn() ? 
            authService.getCurrentUser().getWallet().getBalance() : 0;
    }

    public double getTotalIncome() {
        return authService.isLoggedIn() ? 
            authService.getCurrentUser().getWallet().getTotalIncome() : 0;
    }

    public double getTotalExpenses() {
        return authService.isLoggedIn() ? 
            authService.getCurrentUser().getWallet().getTotalExpenses() : 0;
    }

    public Map<String, Double> getIncomeByCategory() {
        if (!authService.isLoggedIn()) return new HashMap<>();
        
        return authService.getCurrentUser().getWallet().getTransactions().stream()
            .filter(t -> t.getType() == Transaction.Type.INCOME)
            .collect(Collectors.groupingBy(
                Transaction::getCategory,
                Collectors.summingDouble(Transaction::getAmount)
            ));
    }

    public Map<String, Double> getExpensesByCategory() {
        if (!authService.isLoggedIn()) return new HashMap<>();
        
        return authService.getCurrentUser().getWallet().getTransactions().stream()
            .filter(t -> t.getType() == Transaction.Type.EXPENSE)
            .collect(Collectors.groupingBy(
                Transaction::getCategory,
                Collectors.summingDouble(Transaction::getAmount)
            ));
    }

    public double getExpensesForCategories(List<String> categories) {
        if (!authService.isLoggedIn()) return 0;
        
        return authService.getCurrentUser().getWallet().getTransactions().stream()
            .filter(t -> t.getType() == Transaction.Type.EXPENSE)
            .filter(t -> categories.contains(t.getCategory()))
            .mapToDouble(Transaction::getAmount)
            .sum();
    }

    public List<String> getBudgetAlerts() {
        List<String> alerts = new ArrayList<>();
        if (!authService.isLoggedIn()) return alerts;
        
        User user = authService.getCurrentUser();
        Wallet wallet = user.getWallet();
        
        for (Budget budget : wallet.getBudgets()) {
            double spent = wallet.getExpensesForCategory(budget.getCategory());
            double remaining = budget.getLimit() - spent;
            
            if (spent > budget.getLimit()) {
                alerts.add(String.format(
                    "⚠️ Превышен бюджет для категории '%s': потрачено %.2f из %.2f (превышение: %.2f)",
                    budget.getCategory(), spent, budget.getLimit(), -remaining
                ));
            } else if (spent >= budget.getLimit() * 0.8) {
                alerts.add(String.format(
                    "⚠️ Близко к превышению бюджета для '%s': потрачено %.2f из %.2f (осталось: %.2f)",
                    budget.getCategory(), spent, budget.getLimit(), remaining
                ));
            }
        }
        
        return alerts;
    }

    private void checkBudgetAlert(String category) {
        // Budget alerts are checked in getBudgetAlerts()
    }

    private void checkOverallBudgetAlert() {
        double balance = getBalance();
        if (balance < 0) {
            System.out.println("🚨 Внимание: отрицательный баланс! Расходы превысили доходы.");
        } else if (balance < 1000) {
            System.out.println("💡 Предупреждение: низкий баланс (" + balance + ")");
        }
    }

    public List<Transaction> getTransactionsByPeriod(LocalDate start, LocalDate end) {
        if (!authService.isLoggedIn()) return new ArrayList<>();
        
        return authService.getCurrentUser().getWallet().getTransactions().stream()
            .filter(t -> !t.getDate().toLocalDate().isBefore(start) && 
                        !t.getDate().toLocalDate().isAfter(end))
            .collect(Collectors.toList());
    }

    public List<Budget> getBudgets() {
        return authService.isLoggedIn() ? 
            authService.getCurrentUser().getWallet().getBudgets() : 
            new ArrayList<>();
    }
}