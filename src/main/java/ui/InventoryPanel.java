package main.java.ui;

import main.java.entities.Product;
import main.java.entities.User;
import main.java.exceptions.CategoryNotFoundException;
import main.java.management.InventoryManager;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.List;

public class InventoryPanel extends JPanel {

    private JTable productTable;
    private DefaultTableModel tableModel;
    private InventoryManager inventoryManager = new InventoryManager();
    private User loggedInUser;
    private MainFrame mainFrame; // Reference to MainFrame
    private static final String APP_NAME = "TechTrove";
    private JButton inventoryButton, stockTrackingButton, billingButton, logoutButton;  //Navigation Button

    public InventoryPanel(User loggedInUser, MainFrame mainFrame) {
        this.loggedInUser = loggedInUser;
        this.mainFrame = mainFrame; // Set the reference to MainFrame
        setLayout(new BorderLayout());


        JPanel sidebarPanel = new JPanel();
        sidebarPanel.setLayout(new BoxLayout(sidebarPanel, BoxLayout.Y_AXIS));
        sidebarPanel.setBackground(Color.BLACK);
        sidebarPanel.setPreferredSize(new Dimension(150, 0)); // Fixed width

        JLabel techTroveLabel = new JLabel(APP_NAME);
        techTroveLabel.setForeground(Color.WHITE);
        techTroveLabel.setFont(new Font("Arial", Font.BOLD, 16));
        techTroveLabel.setAlignmentX(Component.CENTER_ALIGNMENT);

        sidebarPanel.add(Box.createVerticalStrut(20));  //Add space
        sidebarPanel.add(techTroveLabel);

        inventoryButton = createSidebarButton("Inventory");
        stockTrackingButton = createSidebarButton("Stock Tracking");
        billingButton = createSidebarButton("Billing");
        logoutButton = createSidebarButton("Logout");

        sidebarPanel.add(Box.createVerticalStrut(20));
        sidebarPanel.add(inventoryButton);
        sidebarPanel.add(Box.createVerticalStrut(10));
        sidebarPanel.add(stockTrackingButton);
        sidebarPanel.add(Box.createVerticalStrut(10));
        sidebarPanel.add(billingButton);
        sidebarPanel.add(Box.createVerticalGlue());  //Push Logout to bottom
        sidebarPanel.add(Box.createVerticalStrut(10));
        sidebarPanel.add(logoutButton);

        add(sidebarPanel, BorderLayout.WEST);

        JPanel contentPanel = new JPanel();
        contentPanel.setLayout(new BoxLayout(contentPanel, BoxLayout.Y_AXIS));  //Vertical Layout
        contentPanel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20)); // Add some padding


        JLabel dashboardTitleLabel = new JLabel("Inventory Management");
        dashboardTitleLabel.setFont(new Font("Arial", Font.BOLD, 24));
        dashboardTitleLabel.setAlignmentX(Component.LEFT_ALIGNMENT);  //Keep the alignment to the left
        contentPanel.add(dashboardTitleLabel);

        JPanel statsPanel = new JPanel(new FlowLayout(FlowLayout.LEFT)); // FlowLayout for cards
        statsPanel.setAlignmentX(Component.LEFT_ALIGNMENT);
        statsPanel.add(createStatCard("Total Products", "1,284", "+20% from last month"));
        statsPanel.add(createStatCard("Total Sales", "$45,231.89", "+15% from last month"));
        statsPanel.add(createStatCard("Active Customers", "2,350", "+180 new customers"));
        statsPanel.add(createStatCard("Inventory Value", "$128,790", "+12% from last month"));
        contentPanel.add(statsPanel);

        tableModel = new DefaultTableModel();
        tableModel.addColumn("ID");
        tableModel.addColumn("Name");
        tableModel.addColumn("Seller");
        tableModel.addColumn("Quantity");
        tableModel.addColumn("Rate");
        tableModel.addColumn("Category");

        productTable = new JTable(tableModel);
        JScrollPane tableScrollPane = new JScrollPane(productTable);

        contentPanel.add(tableScrollPane); // Add the table scroll pane to the content panel
        add(contentPanel, BorderLayout.CENTER);  // adding statsPanel in to the centre of the page

        //2. Button Panel
        JPanel buttonPanel = new JPanel();
        JButton addProductButton = new JButton("Add Product");

        JButton reviewsButton = new JButton("Manage Reviews");

        buttonPanel.add(addProductButton);
        buttonPanel.add(reviewsButton);
        add(buttonPanel, BorderLayout.SOUTH);

        loadProductsIntoTable(""); // Load all products initially
        // 3. Load Data & Button Actions

        //Button Actions

        addProductButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                JDialog productDialog = new JDialog((JFrame) SwingUtilities.getWindowAncestor(InventoryPanel.this), "Add New Product", true);
                ProductFormPanel productFormPanel = new ProductFormPanel(inventoryManager, () -> {
                    loadProductsIntoTable(""); // Refresh
                    productDialog.dispose();
                });
                productDialog.add(productFormPanel);
                productDialog.pack();
                productDialog.setLocationRelativeTo(InventoryPanel.this);
                productDialog.setVisible(true);
            }
        });

        reviewsButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                // Open the ReviewsPanel in a dialog
                JDialog reviewsDialog = new JDialog((JFrame) SwingUtilities.getWindowAncestor(InventoryPanel.this), "Manage Reviews", true);
                ReviewsPanel reviewsPanel = new ReviewsPanel(loggedInUser);  //Pass loggedInUser as well for reviews

                reviewsDialog.add(reviewsPanel);
                reviewsDialog.pack();
                reviewsDialog.setLocationRelativeTo(InventoryPanel.this); // Center relative to InventoryPanel
                reviewsDialog.setVisible(true);
            }
        });

        logoutButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                mainFrame.showLoginPanel(); //Navigate back to Login panel
            }
        });

        stockTrackingButton.addActionListener(new ActionListener() { //Adding side menu functionality of stockalert
            @Override
            public void actionPerformed(ActionEvent e) {
                String stockLevels = inventoryManager.checkStockLevels();
                JOptionPane.showMessageDialog(InventoryPanel.this, stockLevels, "Stock Levels", JOptionPane.INFORMATION_MESSAGE);
            }
        });


    }

    //Button on sideBar
    private JButton createSidebarButton(String text) {
        JButton button = new JButton(text);
        button.setForeground(Color.WHITE);
        button.setBackground(Color.BLACK);
        button.setBorderPainted(false);
        button.setFocusPainted(false);
        button.setAlignmentX(Component.CENTER_ALIGNMENT);
        button.setPreferredSize(new Dimension(150, 30)); // Fixed size for the sidebar buttons
        button.setMaximumSize(new Dimension(150, 30));
        return button;
    }

    //Card
    private JPanel createStatCard(String title, String value, String change) {
        JPanel card = new JPanel();
        card.setLayout(new BoxLayout(card, BoxLayout.Y_AXIS));
        card.setBorder(BorderFactory.createEmptyBorder(15, 15, 15, 15)); // Padding
        card.setPreferredSize(new Dimension(180, 120)); // Fixed size
        card.setMaximumSize(new Dimension(180, 120)); // Fixed size
        card.setMinimumSize(new Dimension(180, 120)); // Fixed size
        card.setAlignmentX(Component.LEFT_ALIGNMENT);  //Align everything to the left

        JLabel titleLabel = new JLabel(title);
        titleLabel.setFont(new Font("Arial", Font.BOLD, 14));
        titleLabel.setAlignmentX(Component.LEFT_ALIGNMENT);
        card.add(titleLabel);

        JLabel valueLabel = new JLabel(value);
        valueLabel.setFont(new Font("Arial", Font.BOLD, 20));
        valueLabel.setAlignmentX(Component.LEFT_ALIGNMENT);
        card.add(valueLabel);

        JLabel changeLabel = new JLabel(change);
        changeLabel.setFont(new Font("Arial", Font.PLAIN, 12));
        changeLabel.setForeground(Color.GRAY);
        changeLabel.setAlignmentX(Component.LEFT_ALIGNMENT);
        card.add(changeLabel);

        return card;
    }

    // Use this when the data is not dierectly outputted by inventoryManager.checkStockLevels()
    private String getStockLevels() {
        return inventoryManager.checkStockLevels();
    }

    private void loadProductsIntoTable(String category) {
        try {
            List<Product> products = inventoryManager.getProducts();

            // Clear the existing table data
            tableModel.setRowCount(0);

            for (Product product : products) {
                if (category == null || category.isEmpty() || product.getCategory().equalsIgnoreCase(category)) {
                    Object[] rowData = {
                            product.getProductId(),
                            product.getName(),
                            product.getSeller(),
                            product.getQuantity(),
                            product.getRate(),
                            product.getCategory()
                    };
                    tableModel.addRow(rowData);
                }
            }
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, "Error loading products: " + e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void displayProducts(String category) {
        List<Product> products = inventoryManager.getProducts();
        StringBuilder sb = new StringBuilder();
        String format = "| %-8s | %-20s | %-15s | %-8s | %-10s | %-12s |%n";
        sb.append(String.format(format, "ID", "Name", "Seller", "Quantity", "Rate", "Category"));
        sb.append("-------------------------------------------------------------------------------------------------\n");

        for (Product product : products) {
            if (category == null || category.isEmpty() || product.getCategory().equalsIgnoreCase(category)) {
                sb.append(String.format(format,
                        product.getProductId(),
                        product.getName(),
                        product.getSeller(),
                        product.getQuantity(),
                        product.getRate(),
                        product.getCategory()));
            }
        }
        // displayArea.setText(sb.toString());
    }

    private void displayStockLevels() {
        inventoryManager.checkStockLevels();

        java.io.ByteArrayOutputStream out = new java.io.ByteArrayOutputStream();
        java.io.PrintStream ps = new java.io.PrintStream(out);
        System.setOut(ps);

        inventoryManager.checkStockLevels();

        System.setOut(System.out);

        // displayArea.setText(out.toString());
    }

}