package main.java.management;

import main.java.entities.User;
import main.java.exceptions.AuthenticationException;
import main.java.exceptions.RegistrationException;
import main.java.storage.DatabaseConnection;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.Map;

public class AuthenticationManager {

  public boolean registerAdmin(String username, String password) throws RegistrationException {  //Changed to admin for code standard
    // Check if username already exists
    if (adminExists(username)) {
      throw new RegistrationException("Admin username already exists.");
    }
    if (username == null || username.isEmpty() || password == null || password.isEmpty()) {
      throw new RegistrationException("Admin username and password cannot be empty.");
    }

    String sql = "INSERT INTO admins (username, password) VALUES (?, ?)";
    try (Connection conn = DatabaseConnection.getConnection();
         PreparedStatement stmt = conn.prepareStatement(sql)) {

      stmt.setString(1, username);
      stmt.setString(2, password); // In real app, hash password!

      int affectedRows = stmt.executeUpdate();
      if (affectedRows > 0) {
        return true;
      } else {
        throw new RegistrationException("Failed to register admin.");
      }
    } catch (SQLException e) {
      System.err.println("Error adding admin: " + e.getMessage());
      throw new RegistrationException("Database error: " + e.getMessage());
    }
  }

  public User loginAdmin(String username, String password) throws AuthenticationException {  //Changed to admin table
    Map<String, User> admins = getAdmins();
    User admin = admins.get(username);
    if (admin != null && admin.checkPassword(password)) {
      System.out.println("Admin login successful!");
      return admin;
    } else {
      throw new AuthenticationException("Invalid username or password for admin.");
    }
  }

  public Map<String, User> getAdmins() {   //Changed name to admins.

    Map<String, User> admins = new HashMap<>();
    String sql = "SELECT username, password FROM admins";  //Changed Table

    try (Connection conn = DatabaseConnection.getConnection();
         PreparedStatement stmt = conn.prepareStatement(sql);
         ResultSet rs = stmt.executeQuery()) {

      while (rs.next()) {
        User admin =
            new User(
                rs.getString("username"), rs.getString("password")); // Create new User based on the data from SQL tables
        admins.put(admin.getUsername(), admin); // Put them into the admins hashmap

      }
    } catch (SQLException e) {
      System.err.println("Error getting admins: " + e.getMessage());
    }
    return admins;
  }

  public boolean adminExists(String username) {   //Check name to adminexist to avoid confusion.

    return DatabaseConnection.adminExists(username);  //Access direct db table to check

  }
  //Sellers
  public boolean registerSeller(String sellerName) throws RegistrationException {  //register seller table.

    if (sellerExists(sellerName)) {
      throw new RegistrationException("Seller name already exists.");
    }
    if (sellerName == null || sellerName.isEmpty()) {
      throw new RegistrationException("Seller name cannot be empty.");
    }

    String sql = "INSERT INTO sellers (sellerName) VALUES (?)";  //Insert into seller list table
    try (Connection conn = DatabaseConnection.getConnection();
         PreparedStatement stmt = conn.prepareStatement(sql)) {

      stmt.setString(1, sellerName);

      int affectedRows = stmt.executeUpdate();
      if (affectedRows > 0) {
        return true;
      } else {
        throw new RegistrationException("Failed to register seller.");
      }
    } catch (SQLException e) {
      System.err.println("Error adding seller: " + e.getMessage());
      throw new RegistrationException("Database error: " + e.getMessage());
    }
  }

  public boolean sellerExists(String sellerName) {   //Check with the sellers

    return DatabaseConnection.sellerExists(sellerName);  //Return the database check for users

  }
}