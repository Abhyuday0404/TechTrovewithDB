/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package management;

/**
 *
 * @author abhyu
 */
import entities.User;
import exceptions.AuthenticationException;
import exceptions.RegistrationException;
import storage.InMemoryStorage; // Import InMemoryStorage

import java.util.Map;

public class AuthenticationManager {
    private Map<String, User> users;
    private final InMemoryStorage storage;

    public AuthenticationManager(InMemoryStorage storage) {
        this.storage = storage;
        this.users = this.storage.getUsers();
    }

    public boolean registerUser(String username, String password) throws RegistrationException {
        if (users.containsKey(username)) {
            throw new RegistrationException("Username already exists.");
        }
        // You should add more robust password validation here!
        if (username == null || username.isEmpty() || password == null || password.isEmpty()) {
            throw new RegistrationException("Username and password cannot be empty.");
        }
        User newUser = new User(username, password);
        users.put(username, newUser);
        System.out.println("User registered successfully.");
        return true;
    }

    public User loginUser(String username, String password) throws AuthenticationException {
        User user = users.get(username);
        if (user != null && user.checkPassword(password)) {
            System.out.println("Login successful!");
            return user;
        } else {
            throw new AuthenticationException("Invalid username or password.");
        }
    }
}