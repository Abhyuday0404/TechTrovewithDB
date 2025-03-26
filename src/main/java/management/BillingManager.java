package management;

import entities.Product;
import entities.Transaction;
import exceptions.InsufficientStockException;
import exceptions.InvalidProductIdException;
import storage.InMemoryStorage;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.UUID;

public class BillingManager {
    private List<Transaction> transactions;
    private final InMemoryStorage storage;

    public BillingManager(InMemoryStorage storage) {
        this.storage = storage;
        this.transactions = this.storage.getTransactions();
    }

    public void createTransaction(String productId, int quantitySold, String userId) {
        // Check product exists and has enough stock
        InMemoryStorage storage = new InMemoryStorage();
        InventoryManager inventoryManager = new InventoryManager(storage);
        boolean productFound = false;
        List<Product> products = inventoryManager.getProducts();
        for (Product product : products) {
            if (product.getProductId().equals(productId)) {
                productFound = true;
                if (product.getQuantity() >= quantitySold) {
                    // Create the transaction
                    String transactionId = UUID.randomUUID().toString();
                    Transaction transaction = new Transaction(transactionId, productId, quantitySold, new Date(), userId);
                    transactions.add(transaction);

                    try {
                        product.sell(quantitySold); // Sell directly through the product
                        System.out.println("Transaction created successfully. Transaction ID: " + transactionId);
                        inventoryManager.checkStockLevels();
                        return;
                    } catch (InsufficientStockException e) {
                        System.out.println(e.getMessage()); // Display the exception message
                        transactions.remove(transaction); // Rollback the transaction
                        return;
                    }
                } else {
                    System.out.println("Insufficient stock for product: " + product.getName());
                    return;
                }
            }
        }

        if (!productFound) {
            System.out.println("Product with ID " + productId + " not found.");
        }
    }

    public void listTransactions() {
        if (transactions.isEmpty()) {
            System.out.println("No transactions found.");
            return;
        }
        for (Transaction transaction : transactions) {
            System.out.println(transaction);
        }
    }
}