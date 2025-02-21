import java.util.*;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

class Product {
    private String name;
    private double price;

    public Product(String name, double price) {
        this.name = name;
        this.price = price;
    }

    public String getName() { return name; }
    public double getPrice() { return price; }
}

interface DiscountStrategy {
    double applyDiscount(double price);
}

class SeasonalDiscount implements DiscountStrategy {
    public double applyDiscount(double price) {
        return price * 0.9;
    }
}

class Cart {
    private List<Product> items = new ArrayList<>();

    public void addProduct(Product product) {
        items.add(product);
    }

    public double calculateTotal(DiscountStrategy discountStrategy) {
        return items.stream().mapToDouble(p -> discountStrategy.applyDiscount(p.getPrice())).sum();
    }
}

class Order implements Runnable {
    private static final Lock lock = new ReentrantLock();
    private Cart cart;

    public Order(Cart cart) { this.cart = cart; }

    @Override
    public void run() {
        lock.lock();
        try {
            System.out.println("Processing order...");
            Thread.sleep(2000);
            System.out.println("Order processed. Total Amount: " + cart.calculateTotal(new SeasonalDiscount()));
        } catch (InterruptedException e) {
            System.out.println("Order processing interrupted: " + e.getMessage());
        } finally {
            lock.unlock();
        }
    }
}

public class OnlineShoppingSystem {
    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);
        Cart cart = new Cart();

        try {
            System.out.println("Welcome to the Online Shopping System");
            System.out.println("1. Laptop ($1000)\n2. T-Shirt ($50)");
            System.out.print("Choose a product (1-2): ");
            int choice = scanner.nextInt();

            if (choice == 1) cart.addProduct(new Product("Laptop", 1000));
            else if (choice == 2) cart.addProduct(new Product("T-Shirt", 50));
            else throw new IllegalArgumentException("Invalid product selection");

            Thread orderThread = new Thread(new Order(cart));
            orderThread.start();
        } catch (InputMismatchException e) {
            System.out.println("Invalid input. Please enter a valid number.");
        } catch (IllegalArgumentException e) {
            System.out.println(e.getMessage());
        } finally {
            scanner.close();
        }
    }
}
