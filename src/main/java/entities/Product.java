/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package entities;

/**
 *
 * @author abhyu
 */
/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
//package entities;

/**
 *
 * @author abhyu
 */

import exceptions.InsufficientStockException;

import exceptions.InsufficientStockException;

public class Product {
    private String productId;
    private String name;
    private String seller;
    private int quantity;
    private double rate;
    private String category;

    public Product(String productId, String name, String seller, int quantity, double rate, String category) {
        this.productId = productId;
        this.name = name;
        this.seller = seller;
        this.quantity = quantity;
        this.rate = rate;
        this.category = category;
    }

    public String getProductId() {
        return productId;
    }

    public String getName() {
        return name;
    }

    public String getSeller() {
        return seller;
    }

    public int getQuantity() {
        return quantity;
    }

    public void setQuantity(int quantity) {
        this.quantity = quantity;
    }

    public double getRate() {
        return rate;
    }

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    public boolean sell(int quantityToSell) throws InsufficientStockException {
        if (this.quantity >= quantityToSell) {
            this.quantity -= quantityToSell;
            return true;
        } else {
            throw new InsufficientStockException("Insufficient stock for product: " + this.name +
                    ". Available: " + this.quantity + ", Requested: " + quantityToSell);
        }
    }

    public String generateStockAlert(int lowStockThreshold) {
        if (this.quantity <= lowStockThreshold) {
            return "ALERT: Low stock for " + this.name +
                    " (ID: " + this.productId +
                    "). Quantity: " + this.quantity;
        }
        return null;
    }

    @Override
    public String toString() {
        return "Product{" +
                "productId='" + productId + '\'' +
                ", name='" + name + '\'' +
                ", seller='" + seller + '\'' +
                ", quantity=" + quantity +
                ", rate=" + rate +
                ", category='" + category + '\'' +
                '}';
    }
}