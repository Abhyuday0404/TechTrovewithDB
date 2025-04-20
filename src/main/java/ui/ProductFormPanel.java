/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package main.java.ui;

import main.java.management.InventoryManager;
import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.function.Supplier;

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
                int quantity = Integer.parseInt(quantityField.getText());
                double rate = Double.parseDouble(rateField.getText());
                String category = categoryField.getText();

                try {
                    inventoryManager.addProduct(productId, name, seller, quantity, rate, category);
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