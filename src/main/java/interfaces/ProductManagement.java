package main.java.interfaces;

import main.java.entities.Product;
import main.java.exceptions.CategoryNotFoundException;
import main.java.exceptions.InvalidProductIdException;
import main.java.exceptions.InsufficientStockException;
import java.util.List;

public interface ProductManagement {
    List<Product> getProducts();
    void listProducts(String category) throws CategoryNotFoundException;
    Product findProductById(String productId) throws InvalidProductIdException;
    void sellProduct(String productId, int quantityToSell) throws InvalidProductIdException, InsufficientStockException;
}