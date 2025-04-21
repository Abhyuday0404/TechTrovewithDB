package main.java.ui;

import main.java.storage.DatabaseConnection;
import main.java.management.AuthenticationManager;
import main.java.management.InventoryManager;
import main.java.exceptions.RegistrationException;
import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class DatabaseConfigPanel extends JPanel {

    private JTextField hostField;
    private JTextField userField;
    private JPasswordField passwordField;
    private JButton connectButton;
    private MainFrame mainFrame;

    public DatabaseConfigPanel(MainFrame mainFrame) {
        this.mainFrame = mainFrame;
        setLayout(new BorderLayout());  // Use BorderLayout for overall structure

        // 1. Logo Area (Top - Left Aligned)
        JPanel logoPanel = new JPanel();
        logoPanel.setLayout(new FlowLayout(FlowLayout.LEFT));  // Left Alignment!
        logoPanel.setBackground(new Color(240, 248, 255)); // Light background color

        JLabel logoLabel = new JLabel("<html><font face=\"Arial Black\" size=\"6\" color=\"#00008B\">TT</font> <font face=\"Arial\" size=\"5\" color=\"black\">TechTrove</font></html>");  //Style Font
        logoLabel.setHorizontalAlignment(SwingConstants.LEFT); //Align
        logoPanel.add(logoLabel); //Add Text

        add(logoPanel, BorderLayout.NORTH);

        // 2. Input Field Area (Centered)
        JPanel inputPanel = new JPanel();
        inputPanel.setLayout(new GridLayout(0, 1, 5, 10)); // Single column, more spacing

        // Add some padding
        inputPanel.setBorder(new EmptyBorder(20, 50, 20, 50));

        inputPanel.add(new JLabel("Database Host:(e.g., localhost:3306)"));
        hostField = new JTextField();
        inputPanel.add(hostField);

        inputPanel.add(new JLabel("Username:"));
        userField = new JTextField();
        inputPanel.add(userField);

        inputPanel.add(new JLabel("Password:"));
        passwordField = new JPasswordField();
        inputPanel.add(passwordField);

        // Set input panel to the center to provide an easier viewing process (Also edit/change layout
        JPanel inputHolder = new JPanel(new FlowLayout(FlowLayout.CENTER));
        inputHolder.add(inputPanel);

       //Center layout is created first. All data comes to
        add(inputHolder, BorderLayout.CENTER);  //Adding to the centre

        // 3. Connect Button (Bottom)
        connectButton = new JButton("Connect");
        connectButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                String host = hostField.getText();
                String user = userField.getText();
                String password = new String(passwordField.getPassword());

                DatabaseConnection.setDbHost(host);
                DatabaseConnection.setDbUser(user);
                DatabaseConnection.setDbPassword(password);

                try {
                    DatabaseConnection.initializeDatabase();
                    JOptionPane.showMessageDialog(DatabaseConfigPanel.this, "Database connection successful!");
                    mainFrame.showLoginPanel();
                    initialDataLoad();  //Load in the login page
                } catch (Exception ex) {
                    JOptionPane.showMessageDialog(DatabaseConfigPanel.this, "Database connection failed: " + ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
                }
            }
        });

        JPanel connectPanel = new JPanel(new FlowLayout(FlowLayout.CENTER)); // Center the button
        connectPanel.add(connectButton);

        add(connectPanel, BorderLayout.SOUTH);   // center to bottom of the whole page
       this.setBackground(new Color(240, 248, 255));  //Same background
    }
    private void initialDataLoad() {

        // Initialize Managers
        AuthenticationManager authManager = new AuthenticationManager();
        InventoryManager inventoryManager = new InventoryManager();

         // Pre-populate with sample data
        if (inventoryManager.getProducts().isEmpty() && authManager.getUsers().isEmpty()) {   //Check if data exists
            try {
                //First create an Admin user to associate product to
                authManager.registerUser("Dell", "password");
                authManager.registerUser("Samsung", "password");
                authManager.registerUser("admin", "password");
                authManager.registerUser("user1", "password");
                authManager.registerUser("Microsoft", "password");

                inventoryManager.addProduct("LAP001", "Dell XPS 13", "Dell", 1, 1299.99, "Laptop");
                inventoryManager.addProduct("ACC001", "Laptop Charger", "Dell", 1, 39.99, "Accessory");
                inventoryManager.addProduct("PHO001", "Samsung Galaxy S23", "Samsung", 1, 999.00, "Phone");

            } catch (RegistrationException | IllegalArgumentException ex) {
                System.out.println(ex.getMessage());
            }
        }
    }
}