/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package entities;

/**
 *
 * @author abhyu
 */
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