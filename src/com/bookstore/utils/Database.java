package com.bookstore.utils;

import com.bookstore.models.*;
import java.io.*;
import java.math.BigDecimal;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.time.LocalDate;
import java.util.*;

public class Database {
    private Map<String, Book> books = new HashMap<>();
    private Map<Integer, Customer> customers = new HashMap<>();
    private Map<Integer, Order> orders = new HashMap<>();
    private Map<String, Inventory> inventory = new HashMap<>();
    private Map<String, Discount> discounts = new HashMap<>();
    private int nextOrderId = 1;

    public Database() {
        loadData();
    }

    private void loadData() {
        loadBooks();
        loadCustomers();
        loadInventory();
        loadOrders();
    }

    private void loadBooks() {
        try {
            List<String> lines = Files.readAllLines(Paths.get("data/books.csv"));
            for (int i = 1; i < lines.size(); i++) {
                String[] parts = lines.get(i).split(",");
                if (parts.length >= 5) {
                    String isbn = parts[0];
                    String title = parts[1];
                    String author = parts[2];
                    BigDecimal price = new BigDecimal(parts[3]);
                    String category = parts[4];

                    Book book = new Book(isbn, title, author, price, category);
                    books.put(isbn, book);
                }
            }
        } catch (IOException e) {
            System.out.println("No books.csv found. Starting with empty catalog.");
        }
    }

    private void loadCustomers() {
        try {
            List<String> lines = Files.readAllLines(Paths.get("data/customers.csv"));
            for (int i = 1; i < lines.size(); i++) {
                String[] parts = lines.get(i).split(",");
                if (parts.length >= 4) {
                    int id = Integer.parseInt(parts[0]);
                    String email = parts[1];
                    String firstName = parts[2];
                    String lastName = parts[3];

                    Customer customer = new Customer(id, email, firstName, lastName);
                    customers.put(id, customer);
                }
            }
        } catch (IOException e) {
            System.out.println("No customers.csv found. Starting with empty customer base.");
        }
    }

    private void loadInventory() {
        try {
            List<String> lines = Files.readAllLines(Paths.get("data/inventory.csv"));
            for (int i = 1; i < lines.size(); i++) {
                String[] parts = lines.get(i).split(",");
                if (parts.length >= 2) {
                    String isbn = parts[0];
                    int quantity = Integer.parseInt(parts[1]);

                    Inventory inv = new Inventory(isbn, quantity);
                    inventory.put(isbn, inv);
                }
            }
        } catch (IOException e) {
            System.out.println("No inventory.csv found. Initializing inventory from books.");
            for (String isbn : books.keySet()) {
                inventory.put(isbn, new Inventory(isbn, 50));
            }
        }
    }

    private void loadOrders() {
        nextOrderId = 1;
    }

    // Book operations
    public Book getBook(String isbn) {
        return books.get(isbn);
    }

    public List<Book> getAllBooks() {
        return new ArrayList<>(books.values());
    }

    public void saveBook(Book book) {
        books.put(book.getIsbn(), book);
    }

    public List<Book> searchBooks(String keyword) {
        List<Book> results = new ArrayList<>();
        String lower = keyword.toLowerCase();
        for (Book book : books.values()) {
            if (book.getTitle().toLowerCase().contains(lower) ||
                book.getAuthor().toLowerCase().contains(lower)) {
                results.add(book);
            }
        }
        return results;
    }

    public List<Book> getBooksByCategory(String category) {
        List<Book> results = new ArrayList<>();
        for (Book book : books.values()) {
            if (book.getCategory().equals(category)) {
                results.add(book);
            }
        }
        return results;
    }

    // Customer operations
    public Customer getCustomer(int customerId) {
        return customers.get(customerId);
    }

    public List<Customer> getAllCustomers() {
        return new ArrayList<>(customers.values());
    }

    public void saveCustomer(Customer customer) {
        customers.put(customer.getId(), customer);
    }

    public Customer findCustomerByEmail(String email) {
        for (Customer customer : customers.values()) {
            if (customer.getEmail().equals(email)) {
                return customer;
            }
        }
        return null;
    }

    // Order operations
    public Order getOrder(int orderId) {
        return orders.get(orderId);
    }

    public List<Order> getAllOrders() {
        return new ArrayList<>(orders.values());
    }

    public List<Order> getOrdersByCustomer(int customerId) {
        List<Order> result = new ArrayList<>();
        for (Order order : orders.values()) {
            if (order.getCustomerId() == customerId) {
                result.add(order);
            }
        }
        return result;
    }

    public void saveOrder(Order order) {
        if (order.getId() == 0) {
            order.setId(nextOrderId++);
        }
        orders.put(order.getId(), order);
    }

    // Inventory operations
    public Inventory getInventory(String isbn) {
        return inventory.getOrDefault(isbn, new Inventory(isbn, 0));
    }

    public void updateInventory(String isbn, Inventory inv) {
        inventory.put(isbn, inv);
    }

    public boolean hasStock(String isbn, int quantity) {
        Inventory inv = getInventory(isbn);
        return inv.hasStock(quantity);
    }

    // Discount operations
    public Discount getDiscount(String code) {
        return discounts.get(code);
    }

    public List<Discount> getAllDiscounts() {
        return new ArrayList<>(discounts.values());
    }

    public void saveDiscount(Discount discount) {
        discounts.put(discount.getCode(), discount);
    }

    public List<Discount> getActiveDiscounts() {
        List<Discount> active = new ArrayList<>();
        for (Discount d : discounts.values()) {
            if (d.isValid()) {
                active.add(d);
            }
        }
        return active;
    }
}
