package main.java.main;

import main.java.entities.User;
import main.java.management.AuthenticationManager;
import main.java.management.BillingManager;
import main.java.management.InventoryManager;
import java.util.Scanner;
import main.java.exceptions.*;
import main.java.storage.DBUtils; // Import DBUtils
import main.java.entities.Product;
import java.util.List;
import java.util.Set;

public class TechTroveCLI {

    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);

        DBUtils.createTables(); // Ensure tables exist before proceeding!

        // Initialize Managers directly (no InMemoryStorage)
        AuthenticationManager authManager = new AuthenticationManager();
        InventoryManager inventoryManager = new InventoryManager();
        BillingManager billingManager = new BillingManager();

        //Optionally, populate database with initial data
        if (inventoryManager.getProducts().isEmpty()) {
            // Pre-populate with sample data
            try{
            inventoryManager.addProduct("LAP001", "Dell XPS 13", "Dell", 5, 1299.99, "Laptop");
             inventoryManager.addProduct("ACC001", "Laptop Charger", "Dell", 10, 39.99, "Accessory");
             inventoryManager.addProduct("PHO001", "Samsung Galaxy S23", "Samsung", 3, 999.00, "Phone");
            } catch (IllegalArgumentException ex){
                System.out.println(ex.getMessage());
            }
        }

        if (authManager.getUsers().isEmpty()) {
              try {
                  authManager.registerUser("admin", "password");
              } catch (RegistrationException e) {
                  System.out.println(e.getMessage());
              }

        }


        User loggedInUser = null;

        while (loggedInUser == null) {
            // ... (Sign-in/Register loop as before) ...
            // Use authManager.registerUser(newUsername, newPassword)
            // Use loggedInUser = authManager.loginUser(username, password)

             System.out.println("Welcome to TechTrove!");
            System.out.println("1. Login");
            System.out.println("2. Register");
            System.out.println("3. Exit");

            System.out.print("Enter your choice: ");
            String choice = scanner.nextLine();

            switch (choice) {
                case "1":
                    System.out.print("Enter username: ");
                    String username = scanner.nextLine();
                    System.out.print("Enter password: ");
                    String password = scanner.nextLine();
                    try {
                        loggedInUser = authManager.loginUser(username, password);
                    } catch (AuthenticationException e) {
                        System.out.println(e.getMessage());
                    }
                    break;
                case "2":
                    System.out.print("Enter new username: ");
                    String newUsername = scanner.nextLine();
                    System.out.print("Enter new password: ");
                    String newPassword = scanner.nextLine();
                    try {
                        authManager.registerUser(newUsername, newPassword);
                    } catch (RegistrationException e) {
                        System.out.println(e.getMessage());
                    }
                    break;
                case "3":
                    System.out.println("Exiting TechTrove.");
                    System.exit(0);
                default:
                    System.out.println("Invalid choice. Please try again.");
            }
        }

        // --- Main Application Loop (After Login) ---
        System.out.println("\nWelcome, " + loggedInUser.getUsername() + "!");

        while (true) {
            System.out.println("\nTechTrove Inventory Management");
            System.out.println("1. List Products");
            System.out.println("2. Add Product");
            System.out.println("3. Sell Product");
            System.out.println("4. Update Product Quantity");
            System.out.println("5. Create Transaction");
            System.out.println("6. List Transactions");
            System.out.println("7. Logout");
            System.out.println("8. Exit");
            System.out.println("9. List Categories");
            System.out.println("10. Check Stock Alerts");

            System.out.print("Enter your choice: ");
            String choice = scanner.nextLine();

            switch (choice) {
                case "1":
                    System.out.print("Enter category to list (or leave blank for all): ");
                    String categoryToList = scanner.nextLine();
                    try {
                        inventoryManager.listProducts(categoryToList);
                    } catch (CategoryNotFoundException e) {
                        System.out.println(e.getMessage());
                    }
                    break;
                case "2":
                    System.out.print("Enter product ID: ");
                    String productId = scanner.nextLine();
                    System.out.print("Enter product name: ");
                    String name = scanner.nextLine();
                    System.out.print("Enter seller: ");
                    String seller = scanner.nextLine();
                    System.out.print("Enter quantity: ");
                    int quantity = Integer.parseInt(scanner.nextLine());
                    System.out.print("Enter rate: ");
                    double rate = Double.parseDouble(scanner.nextLine());
                    System.out.print("Enter category: ");
                    String category = scanner.nextLine();
                    try {
                        inventoryManager.addProduct(productId, name, seller, quantity, rate, category);
                    } catch (IllegalArgumentException e) {
                        System.out.println(e.getMessage());
                    }
                    break;
                case "3":
                    System.out.print("Enter product ID to sell: ");
                    String productIdToSell = scanner.nextLine();
                    System.out.print("Enter quantity to sell: ");
                    int quantityToSell = Integer.parseInt(scanner.nextLine());
                    try{
                         inventoryManager.sellProduct(productIdToSell, quantityToSell);
                    } catch (InvalidProductIdException | InsufficientStockException e) {
                        System.out.println(e.getMessage());
                     }

                    break;
                case "4":
                    System.out.print("Enter product ID to update: ");
                    String productIdToUpdate = scanner.nextLine();
                    System.out.print("Enter new quantity: ");
                    int newQuantity = Integer.parseInt(scanner.nextLine());
                    try{
                        inventoryManager.updateProductQuantity(productIdToUpdate, newQuantity);
                    } catch (InvalidProductIdException | InvalidQuantityException e){
                        System.out.println(e.getMessage());
                    }
                    break;
                case "5":
                    System.out.print("Enter product ID for the transaction: ");
                    String transactionProductId = scanner.nextLine();
                    System.out.print("Enter quantity sold: ");
                    int transactionQuantitySold = Integer.parseInt(scanner.nextLine());
                   billingManager.createTransaction(transactionProductId, transactionQuantitySold, loggedInUser.getUsername());
                    break;
                case "6":
                    billingManager.listTransactions();
                    break;
                case "7":
                    loggedInUser = null;
                    System.out.println("Logged out.");
                    break;
                case "8":
                    System.out.println("Exiting TechTrove.");
                    System.exit(0);
                case "9":
                  Set<String> categories = inventoryManager.getAllCategories();
                    System.out.println("Available Categories: " + categories);
                    break;
                case "10":
                    inventoryManager.checkStockLevels();
                    break;
                default:
                    System.out.println("Invalid choice. Please try again.");
            }
        }
    }
}