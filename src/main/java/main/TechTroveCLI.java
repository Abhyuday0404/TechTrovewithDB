/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 */

package main;

/**
 *
 * @author abhyu
 */
/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */

/**
 *
 * @author abhyu
 */
import entities.User;
import management.AuthenticationManager;
import management.BillingManager;
import management.InventoryManager;
import java.util.Set;
import java.util.Scanner;
import exceptions.*;
import storage.InMemoryStorage; // Import InMemoryStorage
import entities.Product;

public class TechTroveCLI {

    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);

        // Initialize InMemoryStorage
        InMemoryStorage storage = new InMemoryStorage();

        // Pre-populate with sample data
        Product p1 = new Product("LAP001", "Dell XPS 13", "Dell", 5, 1299.99, "Laptop");
        Product p2 = new Product("ACC001", "Laptop Charger", "Dell", 10, 39.99, "Accessory");
        Product p3 = new Product("PHO001", "Samsung Galaxy S23", "Samsung", 3, 999.00, "Phone");
        storage.getProducts().add(p1);
        storage.getProducts().add(p2);
        storage.getProducts().add(p3);

        User u1 = new User("admin", "password");
        storage.getUsers().put("admin",u1);

        // Inject InMemoryStorage into Managers
        AuthenticationManager authManager = new AuthenticationManager(storage);
        InventoryManager inventoryManager = new InventoryManager(storage);
        BillingManager billingManager = new BillingManager(storage);

        User loggedInUser = null;

        // --- Sign In / Register Loop ---
        while (loggedInUser == null) {
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
                    inventoryManager.sellProduct(productIdToSell, quantityToSell);
                    break;
                case "4":
                    System.out.print("Enter product ID to update: ");
                    String productIdToUpdate = scanner.nextLine();
                    System.out.print("Enter new quantity: ");
                    int newQuantity = Integer.parseInt(scanner.nextLine());
                    inventoryManager.updateProductQuantity(productIdToUpdate, newQuantity);
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