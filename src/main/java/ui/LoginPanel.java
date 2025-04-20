package main.java.ui;

import main.java.entities.User;
import main.java.management.AuthenticationManager;
import main.java.exceptions.AuthenticationException;
import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class LoginPanel extends JPanel {

    private JTextField usernameField;
    private JPasswordField passwordField;
    private JButton loginButton;
    private JButton registerButton;
    private AuthenticationManager authManager = new AuthenticationManager();
    private MainFrame mainFrame; // Reference to the MainFrame

    public LoginPanel(MainFrame mainFrame) {
        this.mainFrame = mainFrame;
        setLayout(new BorderLayout());

        JPanel inputPanel = new JPanel(new GridLayout(0, 2)); // GridLayout for labels and fields
        inputPanel.add(new JLabel("Username:"));
        usernameField = new JTextField();
        inputPanel.add(usernameField);

        inputPanel.add(new JLabel("Password:"));
        passwordField = new JPasswordField();
        inputPanel.add(passwordField);

        add(inputPanel, BorderLayout.NORTH); // Input fields at the top

        loginButton = new JButton("Login");
        registerButton = new JButton("Register");

        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER)); // Center the buttons
        buttonPanel.add(loginButton);
        buttonPanel.add(registerButton);

        add(buttonPanel, BorderLayout.CENTER);

        loginButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                String username = usernameField.getText();
                String password = new String(passwordField.getPassword());
                try {
                    User loggedInUser = authManager.loginUser(username, password);
                    JOptionPane.showMessageDialog(LoginPanel.this, "Login successful!");
                    mainFrame.showDashboardPanel();
                } catch (AuthenticationException ex) {
                    JOptionPane.showMessageDialog(LoginPanel.this, "Login failed: " + ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
                }
            }
        });

        registerButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                String newUsername = usernameField.getText();
                String newPassword = new String(passwordField.getPassword());
                try {
                    authManager.registerUser(newUsername, newPassword);
                    JOptionPane.showMessageDialog(LoginPanel.this, "Registration successful!");
                } catch (Exception ex) {
                    JOptionPane.showMessageDialog(LoginPanel.this, "Registration failed: " + ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
                }
            }
        });
    }
}