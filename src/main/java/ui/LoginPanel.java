package main.java.ui;

import main.java.entities.User;
import main.java.management.AuthenticationManager;
import main.java.exceptions.AuthenticationException;
import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class LoginPanel extends JPanel {

    private JTextField usernameField;
    private JPasswordField passwordField;
    private JButton loginButton;
    private JButton registerButton;
    private AuthenticationManager authManager = new AuthenticationManager();
    private MainFrame mainFrame; // Reference to the MainFrame

    public LoginPanel(MainFrame mainFrame) {
        this.mainFrame = mainFrame;
        setLayout(new BorderLayout()); // Use BorderLayout

        // 1. Main Content Box
        JPanel contentPanel = new JPanel();
        contentPanel.setLayout(new BoxLayout(contentPanel, BoxLayout.Y_AXIS));
        contentPanel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20)); // Add padding
        contentPanel.setBackground(Color.WHITE); // Set background to white

        // 2. Title
        JLabel titleLabel = new JLabel("User Login");
        titleLabel.setFont(new Font("Arial", Font.BOLD, 24));
        titleLabel.setAlignmentX(Component.CENTER_ALIGNMENT); // Center title
        contentPanel.add(titleLabel);
        contentPanel.add(Box.createVerticalStrut(20)); // Space after title

       //3. Username and Text Field
       JLabel usernameLabel = new JLabel("Username");
        usernameLabel.setAlignmentX(Component.CENTER_ALIGNMENT); // center username label
        usernameLabel.setFont(new Font("Arial", Font.PLAIN, 14));  // Beautify
        contentPanel.add(usernameLabel);
        usernameField = new JTextField();
        usernameField.setMaximumSize(new Dimension(250, 30)); //Fixed to only look a part
        usernameField.setAlignmentX(Component.CENTER_ALIGNMENT);
        contentPanel.add(usernameField);
        contentPanel.add(Box.createVerticalStrut(10));  //Add the box with some spaces

        // 4. Password and Text Field
        JLabel passwordLabel = new JLabel("Password");
        passwordLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        passwordLabel.setFont(new Font("Arial", Font.PLAIN, 14));
        contentPanel.add(passwordLabel);
        passwordField = new JPasswordField();
        passwordField.setMaximumSize(new Dimension(250, 30)); //Fixed to only look a part
        passwordField.setAlignmentX(Component.CENTER_ALIGNMENT);
        contentPanel.add(passwordField);
        contentPanel.add(Box.createVerticalStrut(20)); // Space before button

        // 5. Login Button
        loginButton = new JButton("Login");
        loginButton.setAlignmentX(Component.CENTER_ALIGNMENT);
        loginButton.setBackground(new Color(0, 123, 255)); // Blue color
        loginButton.setForeground(Color.WHITE);
        loginButton.setFocusPainted(false);
        loginButton.setMaximumSize(new Dimension(150, 40));  //Fixed button
        contentPanel.add(loginButton);
        contentPanel.add(Box.createVerticalStrut(10)); // Space above link


        // Place Holder to create all function for it later:

        JPanel centerPanel = new JPanel(new GridBagLayout()); //Center Content
        centerPanel.setBackground(new Color(240, 248, 255));

         centerPanel.add(contentPanel);   //Add the whole frame with code
        add(centerPanel, BorderLayout.CENTER);  //Adding to the centre
        this.setBackground(new Color(240, 248, 255)); //Setting all the layout


          loginButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                String username = usernameField.getText();
                String password = new String(passwordField.getPassword());
                try {
                    User loggedInUser = authManager.loginUser(username, password);
                    JOptionPane.showMessageDialog(LoginPanel.this, "Login successful!");
                    mainFrame.showDashboardPanel(loggedInUser);

                } catch (AuthenticationException ex) {
                    JOptionPane.showMessageDialog(LoginPanel.this, "Login failed: " + ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
                }
            }
        });
    }
}