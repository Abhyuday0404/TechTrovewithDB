package main.java.ui;

import main.java.entities.Product;
import main.java.entities.User;
import main.java.management.InventoryManager;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.List;

public class StockPanel extends JPanel {

    private JTable productTable;
    private DefaultTableModel tableModel;
    private InventoryManager inventoryManager = new InventoryManager();
    private JButton inventoryButton, stockTrackingButton, billingButton, logoutButton;  //Navigation Button
    private static final String APP_NAME = "TechTrove";
    private User loggedInUser;
    private MainFrame mainFrame;

    public StockPanel(User loggedInUser, MainFrame mainFrame) {
        this.loggedInUser = loggedInUser;
        this.mainFrame = mainFrame;
        setLayout(new BorderLayout());

        //Side Menu Bar
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

        //Main panel
        JPanel contentPanel = new JPanel();
        contentPanel.setLayout(new BoxLayout(contentPanel, BoxLayout.Y_AXIS));
        contentPanel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));

        JLabel titleLabel = new JLabel("Stock Tracking - Low Stock Products");
        titleLabel.setFont(new Font("Arial", Font.BOLD, 24));
        titleLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        contentPanel.add(titleLabel);

        // 1. Table Setup
        tableModel = new DefaultTableModel();
        tableModel.addColumn("ID");
        tableModel.addColumn("Name");
        tableModel.addColumn("Seller");
        tableModel.addColumn("Quantity");
        tableModel.addColumn("Rate");
        tableModel.addColumn("Category");

        productTable = new JTable(tableModel);
        JScrollPane tableScrollPane = new JScrollPane(productTable);
        contentPanel.add(tableScrollPane);  //Adding to scroll pane

        loadLowStockProducts();

       add(contentPanel, BorderLayout.CENTER); // Add to center of stock panel

        makeButtonWork();  //Add function down below
    }

    //Button Helper
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

    //Button action to the side bar
    public void makeButtonWork(){  //Put your button to create here
         //Button on sideBar
               logoutButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                mainFrame.showLoginPanel(); //Navigate back to Login panel
            }
        });
                //adding Action Listener into billing
        billingButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                mainFrame.showBillingPanel(loggedInUser, mainFrame); //Navigate to new page

            }
        });

        //Adding side menu function of stockalert
        stockTrackingButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                mainFrame.showStockPanel(loggedInUser, mainFrame);  //Load another code
            }
        });

          inventoryButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                mainFrame.showInventoryPanel(loggedInUser, mainFrame);
            }
        });
     }

  private void loadLowStockProducts() {
    try {
      List<Product> products = inventoryManager.getProducts();

      // Clear the existing table data
      tableModel.setRowCount(0);

      for (Product product : products) {
        if (product.getQuantity() < 2) {
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
      JOptionPane.showMessageDialog(
          this, "Error loading low stock products: " + e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
    }
  }
}