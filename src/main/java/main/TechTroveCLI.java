package main.java.main;

import main.java.entities.User;
import main.java.management.AuthenticationManager;
import main.java.management.BillingManager;
import main.java.management.InventoryManager;
import main.java.management.ReviewsManager;  // Import ReviewsManager
import java.util.Scanner;
import main.java.exceptions.*;
import main.java.storage.DBUtils; // Import DBUtils
import main.java.entities.Product;
import main.java.entities.Review;
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
        ReviewsManager reviewsManager = new ReviewsManager(); // Initialize ReviewsManager


        //Optionally, populate database with initial data
        if (inventoryManager.getProducts().isEmpty()) {
            // Pre-populate with sample data
            try{
            //First create an Admin user to associate product to
            authManager.registerUser("Dell", "password");
            authManager.registerUser("Samsung", "password");

            inventoryManager.addProduct("LAP001", "Dell XPS 13", "Dell", 5, 1299.99, "Laptop");
             inventoryManager.addProduct("ACC001", "Laptop Charger", "Dell", 10, 39.99, "Accessory");
             inventoryManager.addProduct("PHO001", "Samsung Galaxy S23", "Samsung", 3, 999.00, "Phone");
            } catch (IllegalArgumentException | RegistrationException ex){
                System.out.println(ex.getMessage());
            }
        }

        if (authManager.getUsers().isEmpty()) {
              try {
                  authManager.registerUser("admin", "password");
                   authManager.registerUser("user1", "password");  //Sample USer for demo
              } catch (RegistrationException e) {
                  System.out.println(e.getMessage());
              }

        }

        populateDummyData(authManager, inventoryManager, billingManager, reviewsManager); // Populate dummy data

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
           // System.out.println("7. Logout");  <--- Removed Logout Option
            System.out.println("7. Exit");       // Shifted Exit up one
            System.out.println("8. List Categories");     // Shifted List Categories up one
            System.out.println("9. Check Stock Alerts");  // Shifted Stock Alerts up one
             System.out.println("10. Add Review for a Seller");  // add review seller option
            System.out.println("11. List Reviews for a Seller"); // List of seller reviews
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
                    // check if the seller already exist or not if not create a user with the provided username
                    // and random password and add him to the database.
                    if(DBUtils.userExists(seller)){
                       System.out.println("User "+seller+" already exist.");
                    } else {
                        try{
                            authManager.registerUser(seller, "random_password"); // you can use a random password.
                        } catch (RegistrationException ex){
                          System.out.println(ex.getMessage());
                        }

                    }
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
                         // Add transaction to list after successful sale <-----Add Tranactions now
                         billingManager.createTransaction(productIdToSell, quantityToSell, loggedInUser.getUsername());

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
                    List<main.java.entities.Transaction> transactions = billingManager.listTransactions();

                     if (transactions.isEmpty()) {
                        System.out.println("No transactions found.");
                         } else {
                          System.out.println("--- Transactions ---");
                           for (main.java.entities.Transaction transaction : transactions) {
                            System.out.println(transaction); // Assuming Transaction has a meaningful toString()
                            }
                          System.out.println("--- End of Transactions ---");
                             }
                    break;
                 //Shifted
                case "7":
                    System.out.println("Exiting TechTrove.");
                    System.exit(0);
                 //Shifted
                case "8":
                  Set<String> categories = inventoryManager.getAllCategories();
                    System.out.println("Available Categories: " + categories);
                    break;
                 //Shifted
                case "9":
                    inventoryManager.checkStockLevels();
                    break;

       case "10":  // Add Review
                    System.out.print("Enter seller's username to review: ");
                    String sellerToReview = scanner.nextLine();
                    System.out.print("Enter your rating (1-5): ");
                    int rating = Integer.parseInt(scanner.nextLine());
                    System.out.print("Enter your comment: ");
                    String comment = scanner.nextLine();

                    boolean added = reviewsManager.addReview(sellerToReview, loggedInUser.getUsername(), rating, comment);
                    if(added){
                        System.out.println("Review added successfully!");
                    } else {
                        System.out.println("Failed to add review. Check console for errors.");
                    }
                    break;

                case "11": // View Reviews
                    System.out.print("Enter seller's username to view reviews: ");
                    String sellerToView = scanner.nextLine();
                    List<Review> reviews = reviewsManager.getReviewsForSeller(sellerToView);

                     if (reviews.isEmpty()) {
                        System.out.println("No reviews found for " + sellerToView);
                    } else {
                        System.out.println("--- Reviews for " + sellerToView + " ---");
                         for (Review review : reviews) {
                            System.out.println(review);
                         }
                        System.out.println("--- End of Reviews ---");
                    }
                    break;


                default:
                    System.out.println("Invalid choice. Please try again.");
            }
        }
    }


     private static void populateDummyData(AuthenticationManager authManager, InventoryManager inventoryManager, BillingManager billingManager, ReviewsManager reviewsManager) {
        try {
            // Add some sample users (sellers)
            authManager.registerUser("Apple", "password"); // sample seller
            authManager.registerUser("Sony", "password");  // another sample seller
            authManager.registerUser("Bob", "password");

             //Add some products from other user so you can see other transactions and reviews
            authManager.registerUser("Microsoft", "password");  // <--- ADD THESE LINES

            inventoryManager.addProduct("TAB001", "Apple iPad", "Apple", 8, 799.00, "Tablet");
             inventoryManager.addProduct("TV001", "Sony Bravia OLED", "Sony", 4, 1799.00, "TV");

            //Add products and transaction for user Bob
            inventoryManager.addProduct("BOB001", "Sample Product of Bob", "Bob", 4, 100, "Sample Category");

            // Create dummy transactions
             billingManager.createTransaction("LAP001", 1, "admin"); // Sell 1 Dell laptop
             billingManager.createTransaction("PHO001", 1, "user1"); // Sell 1 Phone

            //Add a new transation with Bob
              billingManager.createTransaction("BOB001", 1, "user1");

            // Add some dummy reviews
            reviewsManager.addReview("Apple", "user1", 4, "Great tablets!");
            reviewsManager.addReview("Sony", "admin", 5, "Excellent picture quality.");
               reviewsManager.addReview("Bob", "admin", 5, "Bob does great business");

        } catch (RegistrationException | IllegalArgumentException e) {
            System.err.println("Error populating dummy data: " + e.getMessage());
        }
    }
}