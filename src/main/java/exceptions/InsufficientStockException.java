/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package main.java.exceptions;

/**
 *
 * @author abhyu
 */

public class InsufficientStockException extends Exception {
    public InsufficientStockException(String message) {
        super(message);
    }
}