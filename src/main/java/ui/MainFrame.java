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
    private DashboardPanel dashboardPanel;

    public MainFrame() {
        System.out.println("MainFrame() constructor called");
        setTitle("TechTrove");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(800, 600);
        setLocationRelativeTo(null);

        cardLayout = new CardLayout();
        mainPanel = new JPanel(cardLayout);

        dbConfigPanel = new DatabaseConfigPanel(this);
        loginPanel = new LoginPanel(this);
         dashboardPanel = new DashboardPanel(this);  //Added for menu
        // inventoryPanel = new InventoryPanel(user, this);


        mainPanel.add(dbConfigPanel, "DBConfig");
        mainPanel.add(loginPanel, "Login");
         mainPanel.add(dashboardPanel, "Dashboard");  //Added for menu
       //  mainPanel.add(inventoryPanel, "Inventory");

        add(mainPanel);

        // Initially show the DatabaseConfigPanel
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

    public void showDashboardPanel() {
        System.out.println("showDashboardPanel() called");
        if (dashboardPanel == null) {
            dashboardPanel = new DashboardPanel(this);
        }
        mainPanel.add(dashboardPanel, "Dashboard");
        cardLayout.show(mainPanel, "Dashboard");
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> new MainFrame());
    }
}