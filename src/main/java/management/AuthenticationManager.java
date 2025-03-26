package main.java.management;

import main.java.entities.User;
import main.java.exceptions.AuthenticationException;
import main.java.exceptions.RegistrationException;
import main.java.storage.DBUtils;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.Map;

public class AuthenticationManager {

    public boolean registerUser(String username, String password) throws RegistrationException {
        // Check if username already exists
        if (getUsers().containsKey(username)) {
            throw new RegistrationException("Username already exists.");
        }
        if (username == null || username.isEmpty() || password == null || password.isEmpty()) {
            throw new RegistrationException("Username and password cannot be empty.");
        }

        String sql = "INSERT INTO users (username, password) VALUES (?, ?)";
        try (Connection conn = DBUtils.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, username);
            stmt.setString(2, password); // In real app, hash password!

            int affectedRows = stmt.executeUpdate();
            if (affectedRows > 0) {
                System.out.println("User registered successfully.");
                return true;
            } else {
                throw new RegistrationException("Failed to register user.");
            }
        } catch (SQLException e) {
            System.err.println("Error adding user: " + e.getMessage());
            throw new RegistrationException("Database error: " + e.getMessage());
        }
    }

    public User loginUser(String username, String password) throws AuthenticationException {
        Map<String, User> users = getUsers();
        User user = users.get(username);
        if (user != null && user.checkPassword(password)) {
            System.out.println("Login successful!");
            return user;
        } else {
            throw new AuthenticationException("Invalid username or password.");
        }
    }

    public Map<String, User> getUsers() {
        Map<String, User> users = new HashMap<>();
        String sql = "SELECT username, password FROM users";
        try (Connection conn = DBUtils.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {

            while (rs.next()) {
                User user = new User(
                        rs.getString("username"),
                        rs.getString("password")
                );
                users.put(user.getUsername(), user);
            }
        } catch (SQLException e) {
            System.err.println("Error getting users: " + e.getMessage());
        }
        return users;
    }
}