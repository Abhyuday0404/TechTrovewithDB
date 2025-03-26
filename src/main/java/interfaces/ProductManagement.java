/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package interfaces;

/**
 *
 * @author abhyu
 */
import entities.Product;
import exceptions.CategoryNotFoundException;
import exceptions.InvalidProductIdException;
import exceptions.InsufficientStockException;
import java.util.List;

public interface ProductManagement {
    List<Product> getProducts();
    void listProducts(String category) throws CategoryNotFoundException;
    Product findProductById(String productId) throws InvalidProductIdException;
    void sellProduct(String productId, int quantityToSell) throws InvalidProductIdException, InsufficientStockException;
}