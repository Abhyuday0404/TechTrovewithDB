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
import java.sql.SQLException;

public class DatabaseConfigPanel extends JPanel {

    private JTextField hostField;
    private JTextField userField;
    private JPasswordField passwordField;
    private JButton connectButton;
    private MainFrame mainFrame;

    private static final Color BACKGROUND_COLOR = new Color(240, 248, 255); // Light Cyan
    private static final Color BUTTON_COLOR = new Color(70, 130, 180);  // Steel Blue
    private static final Color TEXT_COLOR = new Color(25, 25, 112); // Midnight Blue
    private static final Font LABEL_FONT = new Font("Arial", Font.BOLD, 16); // Font for labels
    private static final Font INPUT_FONT = new Font("Arial", Font.PLAIN, 16); // Font for input boxes
    private static final Font HEADING_FONT = new Font("Arial", Font.BOLD, 20); // Font for heading

    public DatabaseConfigPanel(MainFrame mainFrame) {
        this.mainFrame = mainFrame;
        setLayout(new BorderLayout());  // Use BorderLayout for overall structure
        setBackground(BACKGROUND_COLOR);

        // 1. Logo Area (Top - Left Aligned)
        JPanel logoPanel = new JPanel();
        logoPanel.setLayout(new FlowLayout(FlowLayout.LEFT));  // Left Alignment!
        logoPanel.setBackground(BACKGROUND_COLOR);

        JLabel logoLabel = new JLabel("<html><font face=\"Arial Black\" size=\"6\" color=\"" + TEXT_COLOR.toString().substring(14, 23) + "\">TT</font> <font face=\"Arial\" size=\"5\" color=\"black\">TechTrove</font></html>");  //Style Font
        logoLabel.setHorizontalAlignment(SwingConstants.LEFT); //Align
        logoPanel.add(logoLabel); //Add Text

        add(logoPanel, BorderLayout.NORTH);

        // 2. Input Field Area (Centered)
        JPanel inputPanel = new JPanel();
        inputPanel.setLayout(new GridLayout(0, 1, 5, 15)); // Single column, increased spacing
        inputPanel.setBackground(BACKGROUND_COLOR);

        // Add some padding
        inputPanel.setBorder(new EmptyBorder(30, 70, 30, 70));

        //Database Connection Setup Heading
        JLabel setupHeading = new JLabel("Setup for Database Connection:");
        setupHeading.setForeground(TEXT_COLOR);
        setupHeading.setFont(HEADING_FONT);
        setupHeading.setHorizontalAlignment(SwingConstants.CENTER); //Center the text
        inputPanel.add(setupHeading);

        JLabel hostLabel = new JLabel("Database Host:(e.g., localhost:3306)");
        hostLabel.setForeground(TEXT_COLOR);
        hostLabel.setFont(LABEL_FONT);
        inputPanel.add(hostLabel);
        hostField = new JTextField();
        hostField.setFont(INPUT_FONT);
        hostField.setPreferredSize(new Dimension(300, 40)); // Increased size
        inputPanel.add(hostField);

        JLabel userLabel = new JLabel("Username:");
        userLabel.setForeground(TEXT_COLOR);
        userLabel.setFont(LABEL_FONT);
        inputPanel.add(userLabel);
        userField = new JTextField();
        userField.setFont(INPUT_FONT);
        userField.setPreferredSize(new Dimension(300, 40));  // Increased size
        inputPanel.add(userField);

        JLabel passwordLabel = new JLabel("Password:");
        passwordLabel.setForeground(TEXT_COLOR);
        passwordLabel.setFont(LABEL_FONT);
        inputPanel.add(passwordLabel);
        passwordField = new JPasswordField();
        passwordField.setFont(INPUT_FONT);
        passwordField.setPreferredSize(new Dimension(300, 40));  // Increased size
        inputPanel.add(passwordField);

        // Set input panel to the center to provide an easier viewing process (Also edit/change layout
        JPanel inputHolder = new JPanel(new FlowLayout(FlowLayout.CENTER));
        inputHolder.setBackground(BACKGROUND_COLOR);
        inputHolder.add(inputPanel);

       //Center layout is created first. All data comes to
        add(inputHolder, BorderLayout.CENTER);  //Adding to the centre

        // 3. Connect Button (Bottom)
        connectButton = new JButton("Connect");
        connectButton.setBackground(BUTTON_COLOR);
        connectButton.setForeground(Color.WHITE);
        connectButton.setFocusPainted(false);
        connectButton.setFont(LABEL_FONT);  // Beautify

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
        connectPanel.setBackground(BACKGROUND_COLOR);
        connectPanel.add(connectButton);

        add(connectPanel, BorderLayout.SOUTH);   // center to bottom of the whole page
        this.setBackground(BACKGROUND_COLOR);  //Same background
    }
    private void initialDataLoad() {

        // Initialize Managers
        AuthenticationManager authManager = new AuthenticationManager();
        InventoryManager inventoryManager = new InventoryManager();

        // Pre-populate with sample data
        try {
            // **Ensure default "admin" user is present**
            if (!DatabaseConnection.adminExists("admin")) {
                try {
                    DatabaseConnection.addAdmin("admin", "password");
                    System.out.println("Added default admin user 'admin'");
                } catch (Exception ex) {
                    System.out.println("Error adding admin " + ex.getMessage()); //Avoid potential null messages or stack trace on this console
                }
            }

            // First register the users
            authManager.registerSeller("Dell");
            authManager.registerSeller("Samsung");
            authManager.registerSeller("user1");
            authManager.registerSeller("Microsoft");

            // Then, if the products list is empty, add the sample products
            if (inventoryManager.getProducts().isEmpty()) {
                inventoryManager.addProduct("LAP001", "Dell XPS 13", "Dell", 1, 1299.99, "Laptop");
                inventoryManager.addProduct("ACC001", "Laptop Charger", "Dell", 1, 39.99, "Accessory");
                inventoryManager.addProduct("PHO001", "Samsung Galaxy S23", "Samsung", 1, 999.00, "Phone");
            }
        } catch (Exception ex) {
            System.out.println(ex.getMessage());
        }
    }
}