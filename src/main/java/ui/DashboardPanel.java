package main.java.ui;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class DashboardPanel extends JPanel {

    private JLabel totalProductsLabel;
    private JLabel totalSalesLabel;
    private JLabel activeCustomersLabel;
    private JLabel inventoryValueLabel;
    private JButton inventoryButton, stockTrackingButton, billingButton, logoutButton;  //Navigation Button

    private MainFrame mainFrame;

    public DashboardPanel(MainFrame mainFrame) {
        this.mainFrame = mainFrame;  //Set the mainframe
        setLayout(new BorderLayout());

        // 1. Sidebar (Navigation)
        JPanel sidebarPanel = new JPanel();
        sidebarPanel.setLayout(new BoxLayout(sidebarPanel, BoxLayout.Y_AXIS));
        sidebarPanel.setBackground(Color.BLACK);
        sidebarPanel.setPreferredSize(new Dimension(150, 0)); // Fixed width

        JLabel techTroveLabel = new JLabel("TechTrove");
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


        // 2. Main Content Area (Statistics)
        JPanel contentPanel = new JPanel();
        contentPanel.setLayout(new BoxLayout(contentPanel, BoxLayout.Y_AXIS));  //Vertical Layout
        contentPanel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20)); // Add some padding

        JLabel dashboardTitleLabel = new JLabel("Dashboard");
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


        add(contentPanel, BorderLayout.CENTER);

        //Navigation Button action
        inventoryButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
               // mainFrame.showInventoryPanel(null); //Need to check for the use of login user or not
                mainFrame.showInventoryPanel(null, mainFrame);
            }
        });

        logoutButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                mainFrame.showLoginPanel();  //Navigate back to the Login Panel
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
        button.setPreferredSize(new Dimension(150, 30)); // Fixed size for the sidebar buttons
         button.setMaximumSize(new Dimension(150, 30));
        return button;
    }

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
}