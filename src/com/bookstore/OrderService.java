package com.bookstore;

import com.bookstore.models.*;
import com.bookstore.utils.Database;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

public class OrderService {
    private Database database;
    private InventoryService inventoryService;
    private DiscountService discountService;

    public OrderService(Database database, InventoryService inventoryService, DiscountService discountService) {
        this.database = database;
        this.inventoryService = inventoryService;
        this.discountService = discountService;
    }

    // BUG #1: No validation - doesn't check if customer exists
    // BUG #2: No validation - doesn't check if book exists
    // BUG #3: No validation - doesn't check inventory before creating order
    // BUG #4: Inventory is never reserved/decremented
    public Order createOrder(int customerId, String bookIsbn, int quantity) {
        Customer customer = database.getCustomer(customerId);
        if (customer == null) {
            System.out.println("Customer not found!");
            return null;
        }

        Book book = database.getBook(bookIsbn);
        if (book == null) {
            System.out.println("Book not found!");
            return null;
        }

        // BUG #3: Doesn't check if there's enough inventory
        // Inventory service has the stock, but we never call it

        Order order = new Order(0, customerId);

        OrderItem item = new OrderItem(bookIsbn, quantity, book.getCurrentPrice());
        order.addItem(item);

        // BUG #4: Total calculation is wrong - doesn't apply discount or tax
        order.setSubtotal(book.getCurrentPrice().multiply(new BigDecimal(quantity)));
        order.setTotal(order.getSubtotal());

        database.saveOrder(order);

        // BUG: Never updates inventory!
        // inventoryService.decrementStock(bookIsbn, quantity);

        return order;
    }

    // BUG #5: N+1 QUERY PROBLEM
    // Gets all orders, then for each order, loads customer details separately
    // In a real DB, this would be 1 + N queries (1 for orders, N for customers)
    public List<Order> getAllOrdersWithDetails() {
        List<Order> orders = database.getAllOrders();

        // Loop through each order to fetch customer (N+1 pattern)
        for (Order order : orders) {
            Customer customer = database.getCustomer(order.getCustomerId());
            // In a real scenario, we'd store this in the order object
            // But here, we're just simulating the extra lookups
            if (customer != null) {
                // This simulates fetching customer data
                String name = customer.getFullName();
            }
        }

        return orders;
    }

    // BUG #6: INEFFICIENT SEARCH - Linear search through all orders
    // If there are 10,000 orders, this checks every single one
    // Should use a HashMap index by customer ID
    public List<Order> getOrdersByCustomer(int customerId) {
        List<Order> customerOrders = new ArrayList<>();

        // Linear search - O(n) when should be O(1)
        for (Order order : database.getAllOrders()) {
            if (order.getCustomerId() == customerId) {
                customerOrders.add(order);
            }
        }

        return customerOrders;
    }

    // BUG #7: Total is not recalculated after applying discount and tax
    public Order applyDiscountToOrder(int orderId, String discountCode) {
        Order order = database.getOrder(orderId);
        if (order == null) {
            return null;
        }

        discountService.applyDiscount(order, discountCode);

        // BUG: Total should be updated but it's not
        // Should calculate: subtotal - discount + tax
        // order.setTotal(...);

        return order;
    }

    // BUG #8: No state validation - allows status changes that don't make sense
    // E.g., can go from SHIPPED back to PENDING
    public void updateOrderStatus(int orderId, String newStatus) {
        Order order = database.getOrder(orderId);
        if (order != null) {
            order.setStatus(newStatus);
            database.saveOrder(order);
        }
    }

    // CLEAN FUNCTION - example of good code
    public Order getOrder(int orderId) {
        if (orderId <= 0) {
            throw new IllegalArgumentException("Order ID must be positive");
        }
        Order order = database.getOrder(orderId);
        if (order == null) {
            throw new IllegalArgumentException("Order not found: " + orderId);
        }
        return order;
    }

    public int getTotalOrderCount() {
        return database.getAllOrders().size();
    }

    public BigDecimal getTotalRevenue() {
        BigDecimal total = BigDecimal.ZERO;
        for (Order order : database.getAllOrders()) {
            total = total.add(order.getTotal());
        }
        return total;
    }
}
