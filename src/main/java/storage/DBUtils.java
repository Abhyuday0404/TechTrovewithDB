package main.java.storage;

import java.sql.*;
import java.util.Scanner;

public class DBUtils {
    private static final String DB_HOST;
    private static final String DB_NAME = "techtrove_db";
    private static final String DB_USER;
    private static final String DB_PASSWORD;
    private static final String JDBC_DRIVER = "com.mysql.cj.jdbc.Driver";
    private static final Scanner scanner = new Scanner(System.in);

    static {
        try {
            System.out.println("Enter database host (e.g., localhost:3306):");
            DB_HOST = scanner.nextLine();

            System.out.println("Enter database username:");
            DB_USER = scanner.nextLine();

            System.out.println("Enter database password:");
            DB_PASSWORD = scanner.nextLine();
            
            // Load JDBC driver
            Class.forName(JDBC_DRIVER);
            
            // Ensure the database is created before proceeding
            createDatabaseIfNotExists();
            
        } catch (ClassNotFoundException e) {
            System.err.println("JDBC Driver not found: " + e.getMessage());
            throw new RuntimeException("Failed to load JDBC driver", e);
        } catch (Exception e) {
            System.err.println("Initialization error: " + e.getMessage());
            throw new RuntimeException("Database initialization failed", e);
        }
    }

    private static void createDatabaseIfNotExists() {
        // Connection without specifying database
        String connectionUrl = String.format("jdbc:mysql://%s?useSSL=false&allowPublicKeyRetrieval=true", DB_HOST);
        
        try (Connection conn = DriverManager.getConnection(connectionUrl, DB_USER, DB_PASSWORD);
             Statement stmt = conn.createStatement()) {
            
            // Create database if not exists
            String createDbQuery = String.format("CREATE DATABASE IF NOT EXISTS %s", DB_NAME);
            stmt.executeUpdate(createDbQuery);
            System.out.println("Database '" + DB_NAME + "' ensured.");
            
            // Create tables
            createTables();
            
        } catch (SQLException ex) {
            System.err.println("Database creation error: " + ex.getMessage());
            throw new RuntimeException("Failed to create database", ex);
        }
    }

    public static Connection getConnection() throws SQLException {
        String connectionUrl = String.format("jdbc:mysql://%s/%s?useSSL=false&allowPublicKeyRetrieval=true", DB_HOST, DB_NAME);
        return DriverManager.getConnection(connectionUrl, DB_USER, DB_PASSWORD);
    }

    public static void createTables() {
        try (Connection conn = getConnection(); Statement stmt = conn.createStatement()) {
            // Ensure Foreign Key Checks are enabled
            stmt.execute("SET FOREIGN_KEY_CHECKS = 1");
            
            // Create users table FIRST
            String createUserTableSQL = "CREATE TABLE IF NOT EXISTS users (" +
                    "username VARCHAR(255) PRIMARY KEY, " +
                    "password VARCHAR(255) NOT NULL)";
            stmt.executeUpdate(createUserTableSQL);
            System.out.println("Table 'users' created or already exists.");
            
            // Create products table with foreign key
            String createProductTableSQL = "CREATE TABLE IF NOT EXISTS products (" +
                    "productId VARCHAR(255) PRIMARY KEY, " +
                    "name VARCHAR(255) NOT NULL, " +
                    "seller VARCHAR(255) NOT NULL, " +
                    "quantity INT NOT NULL CHECK (quantity >= 0), " +
                    "rate DECIMAL(10, 2) NOT NULL CHECK (rate >= 0), " +
                    "category VARCHAR(255) NOT NULL, " +
                    "FOREIGN KEY (seller) REFERENCES users(username) ON DELETE CASCADE)";
            stmt.executeUpdate(createProductTableSQL);
            System.out.println("Table 'products' created or already exists.");
            
            // Create transactions table
            String createTransactionTableSQL = "CREATE TABLE IF NOT EXISTS transactions (" +
                    "transactionId VARCHAR(255) PRIMARY KEY, " +
                    "productId VARCHAR(255) NOT NULL, " +
                    "quantitySold INT NOT NULL CHECK (quantitySold > 0), " +
                    "saleDate DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP, " +
                    "userId VARCHAR(255) NOT NULL, " +
                    "FOREIGN KEY (productId) REFERENCES products(productId) ON DELETE CASCADE, " +
                    "FOREIGN KEY (userId) REFERENCES users(username) ON DELETE CASCADE)";
            stmt.executeUpdate(createTransactionTableSQL);
            System.out.println("Table 'transactions' created or already exists.");
            
        } catch (SQLException e) {
            System.err.println("Error creating tables: " + e.getMessage());
            throw new RuntimeException("Failed to create tables", e);
        }
    }

    public static boolean userExists(String username) {
        if (username == null || username.trim().isEmpty()) {
            return false;
        }

        try (Connection conn = getConnection();
             PreparedStatement pstmt = conn.prepareStatement("SELECT 1 FROM users WHERE username = ?")) {
            pstmt.setString(1, username);
            try (ResultSet rs = pstmt.executeQuery()) {
                return rs.next();
            }
        } catch (SQLException e) {
            System.err.println("Error checking user existence: " + e.getMessage());
            return false;
        }
    }

    public static boolean addProduct(String productId, String name, String seller, 
                                   int quantity, double rate, String category) {
        if (productId == null || productId.trim().isEmpty() || 
            name == null || name.trim().isEmpty() ||
            seller == null || seller.trim().isEmpty() ||
            category == null || category.trim().isEmpty() ||
            quantity < 0 || rate < 0) {
            return false;
        }

        // First, check if the seller exists
        if (!userExists(seller)) {
            System.err.println("Error: Seller '" + seller + "' does not exist.");
            return false;
        }
        
        try (Connection conn = getConnection();
             PreparedStatement pstmt = conn.prepareStatement(
                 "INSERT INTO products (productId, name, seller, quantity, rate, category) " +
                 "VALUES (?, ?, ?, ?, ?, ?)")) {
            
            pstmt.setString(1, productId);
            pstmt.setString(2, name);
            pstmt.setString(3, seller);
            pstmt.setInt(4, quantity);
            pstmt.setDouble(5, rate);
            pstmt.setString(6, category);
            
            return pstmt.executeUpdate() > 0;
            
        } catch (SQLException e) {
            System.err.println("Error adding product: " + e.getMessage());
            return false;
        }
    }

    
}