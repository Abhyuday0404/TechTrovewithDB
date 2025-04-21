package main.java.storage;

import java.sql.*;
import java.util.Scanner;

public class DatabaseConnection {
  private static String DB_HOST;
  private static final String DB_NAME = "techtrove_db";
  private static String DB_USER;
  private static String DB_PASSWORD;
  private static final String JDBC_DRIVER = "com.mysql.cj.jdbc.Driver";
  private static final Scanner scanner = new Scanner(System.in);

  public static void setDbHost(String dbHost) {
    DB_HOST = dbHost;
  }

  public static void setDbUser(String dbUser) {
    DB_USER = dbUser;
  }

  public static void setDbPassword(String dbPassword) {
    DB_PASSWORD = dbPassword;
  }

  public static void initializeDatabase() {
    try {
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
    String connectionUrl =
        String.format(
            "jdbc:mysql://%s?useSSL=false&allowPublicKeyRetrieval=true", DB_HOST);

    try (Connection conn =
            DriverManager.getConnection(connectionUrl, DB_USER, DB_PASSWORD);
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
    String connectionUrl =
        String.format("jdbc:mysql://%s/%s?useSSL=false&allowPublicKeyRetrieval=true", DB_HOST, DB_NAME);
    return DriverManager.getConnection(connectionUrl, DB_USER, DB_PASSWORD);
  }

  public static void createTables() {
    try (Connection conn = getConnection();
        Statement stmt = conn.createStatement()) {
      // Ensure Foreign Key Checks are enabled
      stmt.execute("SET FOREIGN_KEY_CHECKS = 1");

      // Create admins table
      String createAdminsTableSQL =
          "CREATE TABLE IF NOT EXISTS admins ("
              + "username VARCHAR(255) PRIMARY KEY, "
              + "password VARCHAR(255) NOT NULL)";
      stmt.executeUpdate(createAdminsTableSQL);
      System.out.println("Table 'admins' created or already exists.");

      // Create sellers table
      String createSellersTableSQL =
          "CREATE TABLE IF NOT EXISTS sellers ("
              + "sellerName VARCHAR(255) PRIMARY KEY)";
      stmt.executeUpdate(createSellersTableSQL);
      System.out.println("Table 'sellers' created or already exists.");

      // Create products table with foreign key referencing sellers
      String createProductTableSQL =
          "CREATE TABLE IF NOT EXISTS products ("
              + "productId VARCHAR(255) PRIMARY KEY, "
              + "name VARCHAR(255) NOT NULL, "
              + "seller VARCHAR(255) NOT NULL, "
              + "quantity INT NOT NULL CHECK (quantity >= 0), "
              + "rate DECIMAL(10, 2) NOT NULL CHECK (rate >= 0), "
              + "category VARCHAR(255) NOT NULL, "
              + "FOREIGN KEY (seller) REFERENCES sellers(sellerName) ON DELETE CASCADE)";
      stmt.executeUpdate(createProductTableSQL);
      System.out.println("Table 'products' created or already exists.");

      // Create transactions table
      String createTransactionTableSQL =
          "CREATE TABLE IF NOT EXISTS transactions ("
              + "transactionId VARCHAR(255) PRIMARY KEY, "
              + "productId VARCHAR(255) NOT NULL, "
              + "quantitySold INT NOT NULL CHECK (quantitySold > 0), "
              + "saleDate DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP, "
              + "adminUsername VARCHAR(255) NOT NULL, " // Changed userId to adminUsername to reference ADMIN table for easy use, you can also create SELLERs for it if you want.
              + "FOREIGN KEY (productId) REFERENCES products(productId) ON DELETE CASCADE, "
              + "FOREIGN KEY (adminUsername) REFERENCES admins(username) ON DELETE CASCADE)";
      stmt.executeUpdate(createTransactionTableSQL);
      System.out.println("Table 'transactions' created or already exists.");

      String createReviewsTableSQL =
          "CREATE TABLE IF NOT EXISTS reviews ("
              + "reviewId INT AUTO_INCREMENT PRIMARY KEY, "
              + "seller VARCHAR(255) NOT NULL, " // The seller being reviewed
              + "reviewer VARCHAR(255) NOT NULL, " // User who wrote the review
              + "rating INT NOT NULL CHECK (rating BETWEEN 1 AND 5), "
              + "comment TEXT, "
              + "reviewDate DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP, "
              + "FOREIGN KEY (seller) REFERENCES sellers(sellerName) ON DELETE CASCADE, "
              + "FOREIGN KEY (reviewer) REFERENCES admins(username) ON DELETE CASCADE" // Enforce existing users
              + ")";

      stmt.executeUpdate(createReviewsTableSQL);
      System.out.println("Table 'reviews' created or already exists.");

      String createBillsTableSQL =
          "CREATE TABLE IF NOT EXISTS bills ("
              + "billId VARCHAR(255) PRIMARY KEY, "
              + "productId VARCHAR(255) NOT NULL, "
              + "quantitySold INT NOT NULL CHECK (quantitySold > 0), "
              + "totalAmount DECIMAL(10, 2) NOT NULL, "
              + "billDate DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP, "
              + "adminUsername VARCHAR(255) NOT NULL, "
              + "FOREIGN KEY (productId) REFERENCES products(productId) ON DELETE CASCADE, "
              + "FOREIGN KEY (adminUsername) REFERENCES admins(username) ON DELETE CASCADE)";
      stmt.executeUpdate(createBillsTableSQL);
      System.out.println("Table 'bills' created or already exists.");

      // Add default admin user (Only add if it is not there before)
      String insertAdminSQL = "INSERT INTO admins (username, password) SELECT * FROM (SELECT 'admin', 'password') AS tmp WHERE NOT EXISTS (SELECT username FROM admins WHERE username = 'admin')";
      stmt.executeUpdate(insertAdminSQL);
      System.out.println("Default admin user ensured.");

    } catch (SQLException e) {
      System.err.println("Error creating tables: " + e.getMessage());
      throw new RuntimeException("Failed to create tables", e);
    }
  }

  public static boolean adminExists(String username) {
    if (username == null || username.trim().isEmpty()) {
      return false;
    }

    try (Connection conn = getConnection();
        PreparedStatement pstmt = conn.prepareStatement("SELECT 1 FROM admins WHERE username = ?")) {
      pstmt.setString(1, username);
      try (ResultSet rs = pstmt.executeQuery()) {
        return rs.next();
      }
    } catch (SQLException e) {
      System.err.println("Error checking admin existence: " + e.getMessage());
      return false;
    }
  }

  public static boolean sellerExists(String sellerName) {
    if (sellerName == null || sellerName.trim().isEmpty()) {
      return false;
    }

    try (Connection conn = getConnection();
        PreparedStatement pstmt = conn.prepareStatement("SELECT 1 FROM sellers WHERE sellerName = ?")) {
      pstmt.setString(1, sellerName);
      try (ResultSet rs = pstmt.executeQuery()) {
        return rs.next();
      }
    } catch (SQLException e) {
      System.err.println("Error checking seller existence: " + e.getMessage());
      return false;
    }
  }

  public static boolean addProduct(
      String productId, String name, String seller, int quantity, double rate, String category) {
    if (productId == null
        || productId.trim().isEmpty()
        || name == null
        || name.trim().isEmpty()
        || seller == null
        || seller.trim().isEmpty()
        || category == null
        || category.trim().isEmpty()
        || quantity < 0
        || rate < 0) {
      return false;
    }

    // First, check if the seller exists
    if (!sellerExists(seller)) {
      System.err.println("Error: Seller '" + seller + "' does not exist.");
      return false;
    }

    try (Connection conn = getConnection();
        PreparedStatement pstmt =
            conn.prepareStatement(
                "INSERT INTO products (productId, name, seller, quantity, rate, category) "
                    + "VALUES (?, ?, ?, ?, ?, ?)")) {

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

  public static boolean addSeller(String sellerName) {
    if (sellerName == null || sellerName.trim().isEmpty()) {
      return false;
    }

    try (Connection conn = getConnection();
        PreparedStatement pstmt =
            conn.prepareStatement("INSERT INTO sellers (sellerName) VALUES (?)")) {
      pstmt.setString(1, sellerName);
      return pstmt.executeUpdate() > 0;
    } catch (SQLException e) {
      System.err.println("Error adding seller: " + e.getMessage());
      return false;
    }
  }

  public static boolean addAdmin(String username, String password) {
    if (username == null || username.trim().isEmpty() || password == null || password.trim().isEmpty()) {
      return false;
    }
    try (Connection conn = getConnection();
         PreparedStatement pstmt = conn.prepareStatement("INSERT INTO admins (username, password) VALUES (?, ?)")) {
      pstmt.setString(1, username);
      pstmt.setString(2, password);
      return pstmt.executeUpdate() > 0;
    } catch (SQLException e) {
      System.err.println("Error adding admin: " + e.getMessage());
      return false;
    }
  }

}