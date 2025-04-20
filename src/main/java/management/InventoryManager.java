package main.java.management;

import main.java.entities.Product;
import main.java.interfaces.ProductManagement;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.HashSet;
import java.util.Set;
import main.java.exceptions.*;
import main.java.storage.DatabaseConnection;

public class InventoryManager implements ProductManagement {
    private static final int LOW_STOCK_THRESHOLD = 3;

    @Override
    public List<Product> getProducts() {
        List<Product> products = new ArrayList<>();
        String sql = "SELECT productId, name, seller, quantity, rate, category FROM products";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {

            while (rs.next()) {
                Product product = new Product(
                        rs.getString("productId"),
                        rs.getString("name"),
                        rs.getString("seller"),
                        rs.getInt("quantity"),
                        rs.getDouble("rate"),
                        rs.getString("category")
                );
                products.add(product);
            }
        } catch (SQLException e) {
            System.err.println("Error getting products: " + e.getMessage());
            // Consider re-throwing as a custom exception for better error handling at a higher level
        }
        return products;
    }


    public void addProduct(String productId, String name, String seller, int quantity, double rate, String category) throws IllegalArgumentException {
        if (quantity < 0) {
            throw new IllegalArgumentException("Quantity cannot be negative.");
        }
        String sql = "INSERT INTO products (productId, name, seller, quantity, rate, category) VALUES (?, ?, ?, ?, ?, ?)";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, productId);
            stmt.setString(2, name);
            stmt.setString(3, seller);
            stmt.setInt(4, quantity);
            stmt.setDouble(5, rate);
            stmt.setString(6, category);

            int affectedRows = stmt.executeUpdate();
            if (affectedRows > 0) {
                // System.out.println("Product added successfully.");  //Comment this line
            } else {
                System.out.println("Failed to add product.");
                // Consider throwing an exception here for better error handling at a higher level
            }
        } catch (SQLException e) {
            System.err.println("Error adding product: " + e.getMessage());
            throw new IllegalArgumentException("Database error: " + e.getMessage());
        }
    }

    public Set<String> getAllCategories() {
        Set<String> categories = new HashSet<>();
        List<Product> products = getProducts();
        for (Product product : products) {
            categories.add(product.getCategory());
        }
        return categories;
    }

    @Override
    public void listProducts(String category) throws CategoryNotFoundException {
        List<Product> products = getProducts();
        if (products.isEmpty()) {
            System.out.println("No products found.");
            return;
        }
        String format = "| %-8s | %-20s | %-15s | %-8s | %-10s | %-12s |%n";
        System.out.format(format, "ID", "Name", "Seller", "Quantity", "Rate", "Category");
        System.out.println("-------------------------------------------------------------------------------------------------");

        boolean found = false;
        for (Product product : products) {
            if (category == null || category.isEmpty() || product.getCategory().equalsIgnoreCase(category)) {
                System.out.format(format,
                        product.getProductId(),
                        product.getName(),
                        product.getSeller(),
                        product.getQuantity(),
                        product.getRate(),
                        product.getCategory());
                found = true;
            }
        }

        if (!found && (category != null && !category.isEmpty())) {
            throw new CategoryNotFoundException("Category " + category + " was not found.");
        }
    }

    @Override
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

    @Override
    public void sellProduct(String productId, int quantityToSell) throws InvalidProductIdException, InsufficientStockException {
        Product product = findProductById(productId); // This line can throw InvalidProductIdException
        int currentQuantity = getProductQuantity(productId); // This line doesn't throw InvalidProductIdException

        if (currentQuantity < quantityToSell) {
            throw new InsufficientStockException("Insufficient stock for product: " + product.getName());
        }

        int newQuantity = currentQuantity - quantityToSell;
        boolean updateSuccessful = updateProductQuantityInDatabase(productId, newQuantity);
        if (updateSuccessful) {
            System.out.println("Sold " + quantityToSell + " of " + product.getName());
            checkStockLevels();
        } else {
            System.out.println("Failed to update product quantity after sale.");
        }
    }

    public void updateProductQuantity(String productId, int newQuantity) throws InvalidProductIdException, InvalidQuantityException {
        Product product = findProductById(productId);
        if (newQuantity < 0) {
            throw new InvalidQuantityException("Quantity cannot be negative.");
        }

        boolean updateSuccessful = updateProductQuantityInDatabase(productId, newQuantity);
        if (updateSuccessful) {
            System.out.println("Updated quantity of " + product.getName() + " to " + newQuantity);
            checkStockLevels();
        } else {
            System.out.println("Failed to update quantity.");
        }
    }

    public boolean updateProductQuantityInDatabase(String productId, int newQuantity) {
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

    public int getProductQuantity(String productId) throws InvalidProductIdException {
        String sql = "SELECT quantity FROM products WHERE productId = ?";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, productId);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return rs.getInt("quantity");
                } else {
                    throw new InvalidProductIdException("Product with ID " + productId + " not found.");
                }
            }
        } catch (SQLException e) {
            System.err.println("Error getting product quantity: " + e.getMessage());
            throw new InvalidProductIdException("Database error: " + e.getMessage()); // Re-throw as InvalidProductIdException
        }
    }


    public String checkStockLevels() {
        StringBuilder sb = new StringBuilder();
        sb.append("\n--- Stock Level Alerts ---\n");
        boolean alertGenerated = false;
        List<Product> products = getProducts();
        for (Product product : products) {
            String alert = product.generateStockAlert(LOW_STOCK_THRESHOLD);
            if (alert != null) {
                sb.append(alert).append("\n");
                alertGenerated = true;
            }
        }
        if (!alertGenerated) {
            sb.append("No low stock alerts.\n");
        }
        sb.append("--- End of Stock Level Alerts ---\n");
        return sb.toString();
    }
}