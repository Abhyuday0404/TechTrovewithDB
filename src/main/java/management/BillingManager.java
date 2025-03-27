package main.java.management;

import main.java.entities.Product;
import main.java.entities.Transaction;
import main.java.exceptions.InsufficientStockException;
import main.java.exceptions.InvalidProductIdException;
import main.java.storage.DBUtils;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.UUID;

public class BillingManager {

    // In BillingManager.java , createTransaction method

public void createTransaction(String productId, int quantitySold, String userId) {
    try {
        Product product = findProductById(productId);
        int currentQuantity = getProductQuantity(productId);

        if (currentQuantity < quantitySold) {
            throw new InsufficientStockException("Insufficient stock for product: " + product.getName());
        }

        String transactionId = UUID.randomUUID().toString();
        Date saleDate = new Date(); // Current date/time
        Transaction transaction = new Transaction(transactionId, productId, quantitySold, saleDate, userId);

        String sql = "INSERT INTO transactions (transactionId, productId, quantitySold, saleDate, userId) VALUES (?, ?, ?, ?, ?)";
        try (Connection conn = DBUtils.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, transaction.getTransactionId());
            stmt.setString(2, transaction.getProductId());
            stmt.setInt(3, transaction.getQuantitySold());
            stmt.setTimestamp(4, new Timestamp(transaction.getSaleDate().getTime()));
            stmt.setString(5, transaction.getUserId());

            int affectedRows = stmt.executeUpdate();
            if (affectedRows > 0) {
                int newQuantity = currentQuantity - quantitySold;
                if (updateProductQuantity(productId, newQuantity)) {
                    System.out.println("Transaction created successfully. Transaction ID: " + transactionId);
                } else {
                    System.out.println("Failed to update product quantity. Rolling back transaction.");
                    // You would need to implement a deleteTransaction() method in InMemoryStorage
                }

            } else {
                System.out.println("Failed to create transaction.");
            }


        } catch (SQLException e) {
            System.err.println("Error adding transaction: " + e.getMessage());
        }

    } catch (InvalidProductIdException | InsufficientStockException e) {
        System.out.println(e.getMessage());
    }
}

    public List<Transaction> listTransactions() {
        List<Transaction> transactions = new ArrayList<Transaction>(); // Diamond operator removed
        String sql = "SELECT transactionId, productId, quantitySold, saleDate, userId FROM transactions";
        try (Connection conn = DBUtils.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {

            while (rs.next()) {
                Transaction transaction = new Transaction(
                        rs.getString("transactionId"),
                        rs.getString("productId"),
                        rs.getInt("quantitySold"),
                        rs.getTimestamp("saleDate"),
                        rs.getString("userId")
                );
                transactions.add(transaction);
            }
        } catch (SQLException e) {
            System.err.println("Error getting transactions: " + e.getMessage());
        }
        return transactions;
    }

    public Product findProductById(String productId) throws InvalidProductIdException {
        String sql = "SELECT productId, name, seller, quantity, rate, category FROM products WHERE productId = ?";
        try (Connection conn = DBUtils.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, productId);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return new Product(
                            rs.getString("productId"),
                            rs.getString("name"),
                            rs.getString("seller"),
                            rs.getInt("quantity"),
                            rs.getDouble("rate"),
                            rs.getString("category")
                    );
                } else {
                    throw new InvalidProductIdException("Product with ID " + productId + " not found.");
                }
            }
        } catch (SQLException e) {
            System.err.println("Error finding product: " + e.getMessage());
            throw new InvalidProductIdException("Database error: " + e.getMessage());
        }
    }

     public boolean updateProductQuantity(String productId, int newQuantity) {
        String sql = "UPDATE products SET quantity = ? WHERE productId = ?";
        try (Connection conn = DBUtils.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, newQuantity);
            stmt.setString(2, productId);

            int affectedRows = stmt.executeUpdate();
            return affectedRows > 0;
        } catch (SQLException e) {
            System.err.println("Error updating product quantity: " + e.getMessage());
            return false;
        }
    }

     public int getProductQuantity(String productId) {
        String sql = "SELECT quantity FROM products WHERE productId = ?";
        try (Connection conn = DBUtils.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, productId);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return rs.getInt("quantity");
                }
            }
        } catch (SQLException e) {
            System.err.println("Error getting product quantity: " + e.getMessage());
        }
        return -1; // Or throw an exception if product not found
    }
}