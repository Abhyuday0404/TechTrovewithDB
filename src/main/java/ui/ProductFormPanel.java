package main.java.ui;

import main.java.management.InventoryManager;
import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.function.Supplier;
import main.java.management.AuthenticationManager;  //Import Authentication Manager
import main.java.storage.DatabaseConnection;  //Import Database

public class ProductFormPanel extends JPanel {

    private JTextField productIdField, nameField, sellerField, quantityField, rateField, categoryField;
    private JButton saveButton;
    private InventoryManager inventoryManager;
    private Runnable refreshCallback; // Callback to refresh the inventory list

    public ProductFormPanel(InventoryManager inventoryManager, Runnable refreshCallback) {
        this.inventoryManager = inventoryManager;
        this.refreshCallback = refreshCallback;

        setLayout(new GridLayout(0, 2));

        add(new JLabel("Product ID:"));
        productIdField = new JTextField();
        add(productIdField);

        add(new JLabel("Name:"));
        nameField = new JTextField();
        add(nameField);

        add(new JLabel("Seller:"));
        sellerField = new JTextField();
        add(sellerField);

        add(new JLabel("Quantity:"));
        quantityField = new JTextField();
        add(quantityField);

        add(new JLabel("Rate:"));
        rateField = new JTextField();
        add(rateField);

        add(new JLabel("Category:"));
        categoryField = new JTextField();
        add(categoryField);

        saveButton = new JButton("Save");
        saveButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                String productId = productIdField.getText();
                String name = nameField.getText();
                String seller = sellerField.getText();
                int quantity = 0;
                double rate = 0.0;

                try {
                    quantity = Integer.parseInt(quantityField.getText());
                    try {
                        rate = Double.parseDouble(rateField.getText());
                    }
                    catch (NumberFormatException ne) {  //If Number Form
                        JOptionPane.showMessageDialog(ProductFormPanel.this, "Rate cannot be an empty space", "Error", JOptionPane.ERROR_MESSAGE);
                        return;
                    }

                } catch (NumberFormatException err) {   //If its empty
                    JOptionPane.showMessageDialog(ProductFormPanel.this, "Quantity cannot be an empty space", "Error", JOptionPane.ERROR_MESSAGE);
                    return; // dont proceed if is error or is number problem
                }

                String category = categoryField.getText();

                try {
                    //Add the User so that there is no conflict on the table.
                    AuthenticationManager authManager = new AuthenticationManager();
                    if(!authManager.sellerExists(seller)){ // Check that user
                        try{  //Try to register to not encounter existing username
                            authManager.registerSeller(seller);
                        } catch(Exception ex) {  //Try to register to not encounter existing username
                            JOptionPane.showMessageDialog(ProductFormPanel.this, "Failed: Can't save it. Problem with the Database Connection " + ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);  //There is a dupe in the username already so catch so it can print.
                            return;
                        }

                    }
                   if(!DatabaseConnection.sellerExists(seller)){ //DoubleCheck if database exists since i have had problems with SQL

                   }
                    inventoryManager.addProduct(productId, name, seller, quantity, rate, category);  //Finally Add
                    JOptionPane.showMessageDialog(ProductFormPanel.this, "Product added successfully!");
                    refreshCallback.run(); // Refresh the inventory list
                } catch (IllegalArgumentException ex) {
                    JOptionPane.showMessageDialog(ProductFormPanel.this, "Error adding product: " + ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
                }
            }
        });
        add(saveButton);
    }
}