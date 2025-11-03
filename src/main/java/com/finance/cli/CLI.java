package com.finance.cli;

import com.finance.service.AuthService;
import com.finance.service.FinanceService;
import com.finance.repository.UserRepository;
import com.finance.model.Transaction;
import com.finance.model.Budget;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.*;

public class CLI {
    private AuthService authService;
    private FinanceService financeService;
    private Scanner scanner;
    private boolean running;

    public CLI() {
        UserRepository userRepository = UserRepository.getInstance();
        this.authService = new AuthService(userRepository);
        this.financeService = new FinanceService(authService);
        this.scanner = new Scanner(System.in);
        this.running = true;
    }

    public void start() {
        System.out.println("=== Система управления личными финансами ===");
        
        while (running) {
            if (!authService.isLoggedIn()) {
                showLoginMenu();
            } else {
                showMainMenu();
            }
        }
        
        scanner.close();
    }

    private void showLoginMenu() {
        System.out.println("\n--- Авторизация ---");
        System.out.println("1. Войти");
        System.out.println("2. Выйти из приложения");
        System.out.print("Выберите действие: ");
        
        String choice = scanner.nextLine();
        switch (choice) {
            case "1":
                login();
                break;
            case "2":
                running = false;
                break;
            default:
                System.out.println("❌ Неверный выбор. Попробуйте снова.");
        }
    }

    private void login() {
        System.out.print("Введите логин: ");
        String username = scanner.nextLine();
        System.out.print("Введите пароль: ");
        String password = scanner.nextLine();
        
        if (authService.login(username, password)) {
            System.out.println("✅ Успешный вход! Добро пожаловать, " + username + "!");
        } else {
            System.out.println("❌ Неверный логин или пароль.");
        }
    }

    private void showMainMenu() {
        System.out.println("\n--- Главное меню ---");
        System.out.println("1. Добавить доход");
        System.out.println("2. Добавить расход");
        System.out.println("3. Установить бюджет");
        System.out.println("4. Показать статистику");
        System.out.println("5. Показать оповещения");
        System.out.println("6. Экспорт данных");
        System.out.println("7. Выйти из системы");
        System.out.println("8. Выйти из приложения");
        System.out.print("Выберите действие: ");
        
        String choice = scanner.nextLine();
        switch (choice) {
            case "1":
                addIncome();
                break;
            case "2":
                addExpense();
                break;
            case "3":
                setBudget();
                break;
            case "4":
                showStatistics();
                break;
            case "5":
                showAlerts();
                break;
            case "6":
                exportData();
                break;
            case "7":
                logout();
                break;
            case "8":
                logout();
                running = false;
                break;
            default:
                System.out.println("❌ Неверный выбор. Попробуйте снова.");
        }
    }

    private void addIncome() {
        System.out.println("\n--- Добавление дохода ---");
        System.out.print("Введите категорию: ");
        String category = scanner.nextLine().trim();
        
        if (category.isEmpty()) {
            System.out.println("❌ Категория не может быть пустой.");
            return;
        }
        
        double amount = readDouble("Введите сумму: ");
        if (amount <= 0) return;
        
        System.out.print("Введите описание: ");
        String description = scanner.nextLine();
        
        if (financeService.addIncome(category, amount, description)) {
            System.out.println("✅ Доход успешно добавлен!");
        } else {
            System.out.println("❌ Ошибка при добавлении дохода.");
        }
    }

    private void addExpense() {
        System.out.println("\n--- Добавление расхода ---");
        System.out.print("Введите категорию: ");
        String category = scanner.nextLine().trim();
        
        if (category.isEmpty()) {
            System.out.println("❌ Категория не может быть пустой.");
            return;
        }
        
        double amount = readDouble("Введите сумму: ");
        if (amount <= 0) return;
        
        System.out.print("Введите описание: ");
        String description = scanner.nextLine();
        
        if (financeService.addExpense(category, amount, description)) {
            System.out.println("✅ Расход успешно добавлен!");
        } else {
            System.out.println("❌ Ошибка при добавлении расхода.");
        }
    }

    private void setBudget() {
        System.out.println("\n--- Установка бюджета ---");
        System.out.print("Введите категорию: ");
        String category = scanner.nextLine().trim();
        
        if (category.isEmpty()) {
            System.out.println("❌ Категория не может быть пустой.");
            return;
        }
        
        double limit = readDouble("Введите лимит: ");
        if (limit <= 0) return;
        
        if (financeService.setBudget(category, limit)) {
            System.out.println("✅ Бюджет успешно установлен!");
        } else {
            System.out.println("❌ Ошибка при установке бюджета.");
        }
    }

    private void showStatistics() {
        System.out.println("\n--- Статистика ---");
        System.out.printf("💰 Общий баланс: %.2f%n", financeService.getBalance());
        System.out.printf("📈 Общий доход: %.2f%n", financeService.getTotalIncome());
        System.out.printf("📉 Общий расход: %.2f%n", financeService.getTotalExpenses());
        
        System.out.println("\n📊 Доходы по категориям:");
        Map<String, Double> incomeByCategory = financeService.getIncomeByCategory();
        if (incomeByCategory.isEmpty()) {
            System.out.println("  Нет данных о доходах");
        } else {
            incomeByCategory.forEach((category, amount) -> 
                System.out.printf("  %s: %.2f%n", category, amount));
        }
        
        System.out.println("\n📊 Расходы по категориям:");
        Map<String, Double> expensesByCategory = financeService.getExpensesByCategory();
        if (expensesByCategory.isEmpty()) {
            System.out.println("  Нет данных о расходах");
        } else {
            expensesByCategory.forEach((category, amount) -> 
                System.out.printf("  %s: %.2f%n", category, amount));
        }
        
        System.out.println("\n🎯 Бюджеты:");
        List<Budget> budgets = financeService.getBudgets();
        if (budgets.isEmpty()) {
            System.out.println("  Бюджеты не установлены");
        } else {
            for (Budget budget : budgets) {
                double spent = financeService.getExpensesForCategories(List.of(budget.getCategory()));
                double remaining = budget.getLimit() - spent;
                String status = remaining >= 0 ? "✅" : "❌";
                System.out.printf("  %s %s: лимит %.2f, потрачено %.2f, осталось %.2f%n", 
                    status, budget.getCategory(), budget.getLimit(), spent, remaining);
            }
        }
    }

    private void showAlerts() {
        List<String> alerts = financeService.getBudgetAlerts();
        if (alerts.isEmpty()) {
            System.out.println("✅ Нет активных оповещений.");
        } else {
            System.out.println("\n🚨 Оповещения:");
            alerts.forEach(System.out::println);
        }
        
        // Check balance alerts
        double balance = financeService.getBalance();
        if (balance < 0) {
            System.out.println("🚨 Критическое оповещение: отрицательный баланс!");
        } else if (balance < 1000) {
            System.out.println("💡 Предупреждение: низкий баланс!");
        }
    }

    private void exportData() {
        System.out.println("\n--- Экспорт данных ---");
        System.out.println("1. Экспорт в CSV");
        System.out.println("2. Экспорт в JSON");
        System.out.print("Выберите формат: ");
        
        String choice = scanner.nextLine();
        switch (choice) {
            case "1":
                exportToCSV();
                break;
            case "2":
                exportToJSON();
                break;
            default:
                System.out.println("❌ Неверный выбор.");
        }
    }

    private void exportToCSV() {
        if (!authService.isLoggedIn()) {
            System.out.println("❌ Необходимо войти в систему");
            return;
        }
        
        try (PrintWriter writer = new PrintWriter("transactions.csv")) {
            writer.println("Тип,Категория,Сумма,Описание,Дата");
            for (Transaction t : authService.getCurrentUser().getWallet().getTransactions()) {
                writer.printf("%s,%s,%.2f,%s,%s%n",
                    t.getType(), t.getCategory(), t.getAmount(), 
                    t.getDescription(), t.getDate());
            }
            System.out.println("✅ Данные экспортированы в transactions.csv");
        } catch (IOException e) {
            System.out.println("❌ Ошибка при экспорте в CSV");
        }
    }

    private void exportToJSON() {
        authService.saveCurrentUserData();
        System.out.println("✅ Данные экспортированы в JSON формате");
    }

    private void logout() {
        authService.logout();
        System.out.println("✅ Вы вышли из системы.");
    }

    private double readDouble(String prompt) {
        while (true) {
            System.out.print(prompt);
            try {
                double value = Double.parseDouble(scanner.nextLine());
                if (value <= 0) {
                    System.out.println("❌ Сумма должна быть положительной.");
                    continue;
                }
                return value;
            } catch (NumberFormatException e) {
                System.out.println("❌ Неверный формат числа. Попробуйте снова.");
            }
        }
    }
}