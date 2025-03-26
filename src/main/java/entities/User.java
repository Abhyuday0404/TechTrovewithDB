package main.java.entities;

public class User {
    private String username;
    private String password; // Store securely in a real application!

    public User(String username, String password) {
        this.username = username;
        this.password = password;
    }

    public String getUsername() {
        return username;
    }

    public String getPassword() {
        return password;
    }


    public boolean checkPassword(String password) {
        return this.password.equals(password); // DON'T DO THIS IN PRODUCTION!  Use proper hashing.
    }
}