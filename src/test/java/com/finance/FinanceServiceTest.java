package com.finance.service;

import com.finance.model.User;
import com.finance.model.Wallet;
import com.finance.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

public class FinanceServiceTest {
    private AuthService authService;
    private FinanceService financeService;
    private UserRepository userRepository;

    @BeforeEach
    void setUp() {
        userRepository = UserRepository.getInstance();
        userRepository.clear(); // Clear before each test
        
        authService = new AuthService(userRepository);
        financeService = new FinanceService(authService);
        
        // Create and login test user directly
        User testUser = new User("test", "test");
        testUser.setWallet(new Wallet()); // Make sure wallet is initialized
        authService.setCurrentUser(testUser); // Set user directly for testing
    }

    @Test
    void testAddIncome() {
        boolean result = financeService.addIncome("Salary", 1000, "Monthly salary");
        assertTrue(result, "Income should be added successfully");
        assertEquals(1000, financeService.getTotalIncome(), "Total income should be 1000");
    }

    @Test
    void testAddExpense() {
        financeService.addIncome("Salary", 1000, "Monthly salary");
        boolean result = financeService.addExpense("Food", 200, "Groceries");
        assertTrue(result, "Expense should be added successfully");
        assertEquals(200, financeService.getTotalExpenses(), "Total expenses should be 200");
        assertEquals(800, financeService.getBalance(), "Balance should be 800");
    }

    @Test
    void testSetBudget() {
        boolean result = financeService.setBudget("Food", 500);
        assertTrue(result, "Budget should be set successfully");
        assertEquals(1, financeService.getBudgets().size(), "Should have 1 budget");
    }

    @Test
    void testBudgetAlerts() {
        financeService.setBudget("Food", 300);
        financeService.addExpense("Food", 400, "Restaurant");
        
        var alerts = financeService.getBudgetAlerts();
        assertFalse(alerts.isEmpty(), "Should have budget alerts");
        assertTrue(alerts.get(0).contains("превышен"), "Alert should mention budget exceeded");
    }

    @Test
    void testAddIncomeWithInvalidAmount() {
        boolean result = financeService.addIncome("Salary", -100, "Invalid amount");
        assertFalse(result, "Should not add income with negative amount");
    }

    @Test
    void testAddExpenseWithInvalidAmount() {
        boolean result = financeService.addExpense("Food", 0, "Zero amount");
        assertFalse(result, "Should not add expense with zero amount");
    }

    @Test
    void testAddIncomeWithEmptyCategory() {
        boolean result = financeService.addIncome("", 100, "Empty category");
        assertFalse(result, "Should not add income with empty category");
    }

    @Test
    void testAddExpenseWithNullCategory() {
        boolean result = financeService.addExpense(null, 100, "Null category");
        assertFalse(result, "Should not add expense with null category");
    }
}