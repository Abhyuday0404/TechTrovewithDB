package main.java.ui;

import main.java.entities.User;

import javax.swing.*;
import java.awt.*;

public class MainFrame extends JFrame {

    private CardLayout cardLayout;
    private JPanel mainPanel;
    private DatabaseConfigPanel dbConfigPanel;
    private LoginPanel loginPanel;
    private InventoryPanel inventoryPanel;
    private BillingPanel billingPanel;
    private StockPanel stockPanel;
    private DashboardPanel dashboardPanel;  // Add this line

    public MainFrame() {
        setTitle("TechTrove");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(800, 600);
        setLocationRelativeTo(null);

        cardLayout = new CardLayout();
        mainPanel = new JPanel(cardLayout);

        dbConfigPanel = new DatabaseConfigPanel(this);
        loginPanel = new LoginPanel(this);

        mainPanel.add(dbConfigPanel, "DBConfig");
        mainPanel.add(loginPanel, "Login");

        add(mainPanel);

        cardLayout.show(mainPanel, "DBConfig");
        setVisible(true);
    }

    public void showLoginPanel() {
        cardLayout.show(mainPanel, "Login");
    }

    public void showInventoryPanel(User loggedInUser, MainFrame mainFrame) {
        inventoryPanel = new InventoryPanel(loggedInUser, mainFrame);
        mainPanel.add(inventoryPanel, "Inventory");
        cardLayout.show(mainPanel, "Inventory");
    }

    public void showBillingPanel(User loggedInUser, MainFrame mainFrame) {
        billingPanel = new BillingPanel(loggedInUser, this);
        mainPanel.add(billingPanel, "Billing");
        cardLayout.show(mainPanel, "Billing");
    }

    public void showStockPanel(User loggedInUser, MainFrame mainFrame) {
        stockPanel = new StockPanel(loggedInUser, mainFrame);
        mainPanel.add(stockPanel, "Stock");
        cardLayout.show(mainPanel, "Stock");
    }

    public void showDashboardPanel(User loggedInUser) {
        dashboardPanel = new DashboardPanel(this, loggedInUser); // Correct instantiation
        mainPanel.add(dashboardPanel, "Dashboard");
        cardLayout.show(mainPanel, "Dashboard");
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> new MainFrame());
    }
}