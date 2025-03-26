package main.java.entities;

import java.util.Date;

public class Transaction {
    private String transactionId;
    private String productId;
    private int quantitySold;
    private Date saleDate;
    private String userId;

    public Transaction(String transactionId, String productId, int quantitySold, Date saleDate, String userId) {
        this.transactionId = transactionId;
        this.productId = productId;
        this.quantitySold = quantitySold;
        this.saleDate = saleDate;
        this.userId = userId;
    }

     public Date getSaleDate() {
        return saleDate;
    }

    public String getTransactionId() {
        return transactionId;
    }

    public String getProductId() {
        return productId;
    }

    public int getQuantitySold() {
        return quantitySold;
    }

    public String getUserId() {
        return userId;
    }

    @Override
    public String toString() {
        return "Transaction{" +
                "transactionId='" + transactionId + '\'' +
                ", productId='" + productId + '\'' +
                ", quantitySold=" + quantitySold +
                ", saleDate=" + saleDate +
                ", userId='" + userId + '\'' +
                '}';
    }
}