package main.java.ui;

import main.java.storage.DatabaseConnection;
import javax.swing.*;
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

        JPanel inputPanel = new JPanel(new GridLayout(0, 2));  // Input fields in GridLayout
        inputPanel.add(new JLabel("Database Host:(eg.:localhost:3306)"));
        hostField = new JTextField();
        inputPanel.add(hostField);

        inputPanel.add(new JLabel("Username:"));
        userField = new JTextField();
        inputPanel.add(userField);

        inputPanel.add(new JLabel("Password:"));
        passwordField = new JPasswordField();
        inputPanel.add(passwordField);

        add(inputPanel, BorderLayout.NORTH); // Put input fields at the top

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

                } catch (Exception ex) {
                    JOptionPane.showMessageDialog(DatabaseConfigPanel.this, "Database connection failed: " + ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
                }
            }
        });

        JPanel connectPanel = new JPanel(new FlowLayout(FlowLayout.CENTER)); // Center the button
        connectPanel.add(connectButton);

        add(connectPanel, BorderLayout.CENTER);   // center to bottom of the whole page
    }
}