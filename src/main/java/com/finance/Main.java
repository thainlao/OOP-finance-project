package com.finance;

import com.finance.cli.CLI;

public class Main {
    public static void main(String[] args) {
        System.out.println("=== Personal Finance Manager ===");
        System.out.println("Project by Nosko Aleksandr");
        System.out.println("Application is starting...");
        
        try {
            CLI cli = new CLI();
            cli.start();
        } catch (Exception e) {
            System.err.println("Error starting application: " + e.getMessage());
            e.printStackTrace();
        }
    }
}