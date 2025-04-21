package main.java.management;

import main.java.entities.Product;
import main.java.entities.Transaction;
import main.java.exceptions.InvalidProductIdException;
import main.java.exceptions.InsufficientStockException; //ADDED
import main.java.storage.DatabaseConnection;

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

    public void createTransaction(String productId, int quantitySold, String userId)  {
        try {
            Product product = findProductById(productId);
            double unitPrice = product.getRate();
            double totalAmount = unitPrice * quantitySold;

            //Check for InsufficientStock and also update it, or return an appropriate Error
            int currentQuantity = getProductQuantity(productId);  //Get current quantity
            if (currentQuantity < quantitySold) {
              System.out.println("Not enough stock"); // just return in System. out, without throwing
              return;
            }

            int newQuantity = currentQuantity - quantitySold;

            String billId = UUID.randomUUID().toString();
            Date saleDate = new Date();

            String sql = "INSERT INTO bills (billId, productId, quantitySold, totalAmount, billDate, userId) VALUES (?, ?, ?, ?, ?, ?)";
            try (Connection conn = DatabaseConnection.getConnection();
                 PreparedStatement stmt = conn.prepareStatement(sql)) {

                stmt.setString(1, billId);
                stmt.setString(2, productId);
                stmt.setInt(3, quantitySold);
                stmt.setDouble(4, totalAmount);
                stmt.setTimestamp(5, new Timestamp(saleDate.getTime()));
                stmt.setString(6, userId);

                int affectedRows = stmt.executeUpdate();
                if (affectedRows > 0) {
                    // If adding transaction works then update product
                   boolean updateSuccessful = updateProductQuantity(productId, newQuantity); //Update quantity of proudcts

                   if(updateSuccessful){  //Check if successfully updated
                      System.out.println("Transaction created successfully. billId ID: " + billId + " and updated product");
                   } else{ //If the update doesn't work then do something
                       System.out.println("Create Transaction Success but not Update product");  //For now, just sysout
                   }



                } else {
                    System.out.println("Failed to create transaction.");
                }


            } catch (SQLException e) {
                System.err.println("Error adding transaction: " + e.getMessage());
            }

        } catch (InvalidProductIdException e) {
            System.out.println(e.getMessage());
        }
    }

     public boolean updateProductQuantity(String productId, int newQuantity) {  //added
        String sql = "UPDATE products SET quantity = ? WHERE productId = ?";
        try (Connection conn = DatabaseConnection.getConnection();
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

    public List<Transaction> listTransactions() {
        List<Transaction> transactions = new ArrayList<Transaction>(); // Diamond operator removed
        String sql = "SELECT billId, productId, quantitySold, billDate, userId FROM bills";  //Select from bills
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {

            while (rs.next()) {
                Transaction transaction = new Transaction(
                        rs.getString("billId"),
                        rs.getString("productId"),
                        rs.getInt("quantitySold"),
                        rs.getTimestamp("billDate"),
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
        try (Connection conn = DatabaseConnection.getConnection();
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

     public int getProductQuantity(String productId) {  //added
        String sql = "SELECT quantity FROM products WHERE productId = ?";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, productId);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return rs.getInt("quantity");
                } else {
                    return 0;  //Or something else. or throw exceptions
                }
            }
        } catch (SQLException e) {
            System.err.println("Error getting product quantity: " + e.getMessage());
           return 0;  //Or something else. or throw exceptions
        }
    }

}