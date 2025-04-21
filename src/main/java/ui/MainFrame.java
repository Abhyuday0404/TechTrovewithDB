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
    private BillingPanel billingPanel;  //add for new panel.
    private StockPanel stockPanel;

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


        mainPanel.add(dbConfigPanel, "DBConfig");
        mainPanel.add(loginPanel, "Login");

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

     public void showBillingPanel(User loggedInUser, MainFrame mainFrame) {
         billingPanel = new BillingPanel(loggedInUser, this);
         mainPanel.add(billingPanel, "Billing");
         cardLayout.show(mainPanel, "Billing");
    }

    public void showDashboardPanel(User loggedInUser) {  //To account for Login, pass the user that is logged in as argument
        inventoryPanel = new InventoryPanel(loggedInUser, this);  //Update the InventoryPanel and pass the user as parameter
        mainPanel.add(inventoryPanel, "Inventory");
        cardLayout.show(mainPanel, "Inventory");
    }

    public void showStockPanel(User user, MainFrame frame) {
    setContentPane(new StockPanel(user, frame));
    revalidate();
    repaint();
}



    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> new MainFrame());
    }
}