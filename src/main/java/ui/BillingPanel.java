package main.java.ui;

import main.java.entities.Product;
import main.java.entities.Transaction;
import main.java.entities.User;
import main.java.exceptions.InvalidProductIdException;
import main.java.management.BillingManager;
import main.java.management.InventoryManager;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.sql.Timestamp;
import java.util.List;

public class BillingPanel extends JPanel {

  private JTable transactionTable;
  private DefaultTableModel tableModel;
  private BillingManager billingManager = new BillingManager();
  private InventoryManager inventoryManager = new InventoryManager(); // To get product rates
  private MainFrame mainFrame; // Reference to MainFrame
  private User loggedInUser; // Track current user
  private JButton inventoryButton, stockTrackingButton, billingButton,
      logoutButton; // Navigation Button
  private static final String APP_NAME = "TechTrove";

  public BillingPanel(User loggedInUser, MainFrame mainFrame) {
    this.loggedInUser = loggedInUser;
    this.mainFrame = mainFrame;
    setLayout(new BorderLayout());

    // Sidebar (Navigation)
    JPanel sidebarPanel = new JPanel();
    sidebarPanel.setLayout(new BoxLayout(sidebarPanel, BoxLayout.Y_AXIS));
    sidebarPanel.setBackground(Color.BLACK);
    sidebarPanel.setPreferredSize(new Dimension(150, 0));

    JLabel techTroveLabel = new JLabel(APP_NAME);
    techTroveLabel.setForeground(Color.WHITE);
    techTroveLabel.setFont(new Font("Arial", Font.BOLD, 16));
    techTroveLabel.setAlignmentX(Component.CENTER_ALIGNMENT);

    sidebarPanel.add(Box.createVerticalStrut(20));
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
    sidebarPanel.add(Box.createVerticalGlue());
    sidebarPanel.add(Box.createVerticalStrut(10));
    sidebarPanel.add(logoutButton);

    add(sidebarPanel, BorderLayout.WEST);

    // Content Area
    JPanel contentPanel = new JPanel();
    contentPanel.setLayout(new BoxLayout(contentPanel, BoxLayout.Y_AXIS));
    contentPanel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));

    JLabel dashboardTitleLabel = new JLabel("Billing Management");
    dashboardTitleLabel.setFont(new Font("Arial", Font.BOLD, 24));
    dashboardTitleLabel.setAlignmentX(Component.LEFT_ALIGNMENT);
    contentPanel.add(dashboardTitleLabel);

    // Table Setup
    tableModel = new DefaultTableModel();
    tableModel.addColumn("Bill ID");
    tableModel.addColumn("Product ID");
    tableModel.addColumn("Quantity Sold");
    tableModel.addColumn("Total Amount");
    tableModel.addColumn("Sale Date");
    tableModel.addColumn("User ID");

    transactionTable = new JTable(tableModel);
    JScrollPane tableScrollPane = new JScrollPane(transactionTable);
    contentPanel.add(tableScrollPane);

    add(contentPanel, BorderLayout.CENTER);

    // Create Bill Button
    JPanel buttonPanel = new JPanel();
    JButton createBillButton = new JButton("Create Bill");
    buttonPanel.add(createBillButton);
    add(buttonPanel, BorderLayout.SOUTH);

    loadTransactionsIntoTable();

    // Action Listeners
    createBillButton.addActionListener(
        new ActionListener() {
          @Override
          public void actionPerformed(ActionEvent e) {
            String productId =
                JOptionPane.showInputDialog(BillingPanel.this, "Enter Product ID:");
            if (productId == null || productId.trim().isEmpty()) return;

            String quantityStr =
                JOptionPane.showInputDialog(BillingPanel.this, "Enter Quantity Sold:");
            if (quantityStr == null || quantityStr.trim().isEmpty()) return;

            try {
              int quantitySold = Integer.parseInt(quantityStr);
              Product product = inventoryManager.findProductById(productId);
              double unitPrice = product.getRate();
              double totalPrice = unitPrice * quantitySold;

              int confirm =
                  JOptionPane.showConfirmDialog(
                      BillingPanel.this,
                      "Product ID: " + productId + "\n"
                          + "Quantity: " + quantitySold + "\n"
                          + "Total Price: $" + totalPrice + "\n"
                          + "Confirm Transaction?",
                      "Confirm Transaction",
                      JOptionPane.YES_NO_OPTION);

              if (confirm == JOptionPane.YES_OPTION) {
                billingManager.createTransaction(productId, quantitySold, loggedInUser.getUsername());
                loadTransactionsIntoTable();
              }

            } catch (NumberFormatException ex) {
              JOptionPane.showMessageDialog(
                  BillingPanel.this, "Invalid quantity format.", "Error", JOptionPane.ERROR_MESSAGE);
            } catch (InvalidProductIdException ex) {
              JOptionPane.showMessageDialog(
                  BillingPanel.this, ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
            }
          }
        });

    stockTrackingButton.addActionListener(new ActionListener() {
      @Override
      public void actionPerformed(ActionEvent e) {
        String stockLevels = inventoryManager.checkStockLevels();
        JOptionPane.showMessageDialog(BillingPanel.this, stockLevels, "Stock Levels", JOptionPane.INFORMATION_MESSAGE);
      }
    });

    billingButton.addActionListener(new ActionListener() {
      @Override
      public void actionPerformed(ActionEvent e) {
        mainFrame.showBillingPanel(loggedInUser, mainFrame);
      }
    });

    inventoryButton.addActionListener(new ActionListener() {
      @Override
      public void actionPerformed(ActionEvent e) {
        mainFrame.showInventoryPanel(loggedInUser, mainFrame);
      }
    });

    logoutButton.addActionListener(new ActionListener() {
      @Override
      public void actionPerformed(ActionEvent e) {
        mainFrame.showLoginPanel();
      }
    });
  }

  private JButton createSidebarButton(String text) {
    JButton button = new JButton(text);
    button.setForeground(Color.WHITE);
    button.setBackground(Color.BLACK);
    button.setBorderPainted(false);
    button.setFocusPainted(false);
    button.setAlignmentX(Component.CENTER_ALIGNMENT);
    button.setPreferredSize(new Dimension(150, 30));
    button.setMaximumSize(new Dimension(150, 30));
    return button;
  }

  private void loadTransactionsIntoTable() {
    try {
      List<Transaction> transactions = billingManager.listTransactions();
      tableModel.setRowCount(0);

      for (Transaction transaction : transactions) {
        Product product = inventoryManager.findProductById(transaction.getProductId());
        double totalAmount = product.getRate() * transaction.getQuantitySold();
        Object[] rowData = {
            transaction.getTransactionId(),
            transaction.getProductId(),
            transaction.getQuantitySold(),
            String.format("%.2f", totalAmount),
            transaction.getSaleDate(),
            transaction.getUserId()
        };
        tableModel.addRow(rowData);
      }
    } catch (Exception e) {
      JOptionPane.showMessageDialog(this, "Error loading transactions: " + e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
    }
  }
}
