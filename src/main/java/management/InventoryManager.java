package management;

import entities.Product;
import interfaces.ProductManagement; // Importing interface
import java.util.ArrayList;
import java.util.List;
import java.util.HashSet;
import java.util.Set;
import exceptions.InsufficientStockException;
import exceptions.InvalidProductIdException;
import exceptions.InvalidQuantityException;
import exceptions.CategoryNotFoundException;
import storage.InMemoryStorage; // Import InMemoryStorage

public class InventoryManager implements ProductManagement {
    private List<Product> products;
    private static final int LOW_STOCK_THRESHOLD = 3;
    private final InMemoryStorage storage; // Reference to InMemoryStorage

    public InventoryManager(InMemoryStorage storage) {
        this.storage = storage;
        this.products = this.storage.getProducts(); // Set the products list
    }

    public List<Product> getProducts() {
        return products;
    }


    public void addProduct(String productId, String name, String seller, int quantity, double rate, String category) throws IllegalArgumentException {
        if (quantity < 0) {
            throw new IllegalArgumentException("Quantity cannot be negative.");
        }
        Product product = new Product(productId, name, seller, quantity, rate, category);
        this.products.add(product);
        System.out.println("Product added successfully.");
    }

    public Set<String> getAllCategories() {
        Set<String> categories = new HashSet<>();
        for (Product product : products) {
            categories.add(product.getCategory());
        }
        return categories;
    }

//    public void listProducts() {
//        listProducts(null);
//    }

    public void listProducts(String category) throws CategoryNotFoundException {
        if (products.isEmpty()) {
            System.out.println("No products found.");
            return;
        }
        String format = "| %-8s | %-20s | %-15s | %-8s | %-10s | %-12s |%n";
        System.out.format(format, "ID", "Name", "Seller", "Quantity", "Rate", "Category");
        System.out.println("-------------------------------------------------------------------------------------------------");

        if (category != null && !category.isEmpty()) {
            boolean found = false;
            for (Product product : products) {
                if (product.getCategory().equalsIgnoreCase(category)) {
                     System.out.format(format,
                            product.getProductId(),
                            product.getName(),
                            product.getSeller(),
                            product.getQuantity(),
                            product.getRate(),
                            product.getCategory());
                    found = true;
                }
            }
            if (!found) {
                throw new CategoryNotFoundException("Category " + category + " was not found.");
            }
        } else {
            for (Product product : products) {
                  System.out.format(format,
                            product.getProductId(),
                            product.getName(),
                            product.getSeller(),
                            product.getQuantity(),
                            product.getRate(),
                            product.getCategory());
            }
        }
    }

     public Product findProductById(String productId) throws InvalidProductIdException {
        for (Product product : products) {
            if (product.getProductId().equals(productId)) {
                return product;
            }
        }
        throw new InvalidProductIdException("Product with ID " + productId + " not found.");
    }

    public void sellProduct(String productId, int quantityToSell) {
        try {
            Product product = findProductById(productId);
            product.sell(quantityToSell);
            System.out.println("Sold " + quantityToSell + " of " + product.getName());
            checkStockLevels();
        } catch (InvalidProductIdException e) {
            System.out.println(e.getMessage());
        }  catch (InsufficientStockException e) {
            System.out.println(e.getMessage());
        }
    }

    public void updateProductQuantity(String productId, int newQuantity) {
         try {
             if (newQuantity < 0) {
                 throw new InvalidQuantityException("Quantity cannot be negative.");
             }
             Product product = findProductById(productId);
             product.setQuantity(newQuantity);
             System.out.println("Updated quantity of " + product.getName() + " to " + newQuantity);
             checkStockLevels();
        } catch (InvalidProductIdException e) {
            System.out.println(e.getMessage());
        }  catch (InvalidQuantityException e) {
            System.out.println(e.getMessage());
        }
    }


    public void checkStockLevels() {
        System.out.println("\n--- Stock Level Alerts ---");
        boolean alertGenerated = false;
        for (Product product : products) {
            String alert = product.generateStockAlert(LOW_STOCK_THRESHOLD);
            if (alert != null) {
                System.out.println(alert);
                alertGenerated = true;
            }
        }
        if (!alertGenerated) {
            System.out.println("No low stock alerts.");
        }
        System.out.println("--- End of Stock Level Alerts ---\n");
    }

}