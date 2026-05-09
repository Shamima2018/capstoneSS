package com.bookstore;

import com.bookstore.models.Order;
import com.bookstore.utils.Database;
import java.math.BigDecimal;
import java.util.*;

public class AnalyticsService {
    private Database database;

    public AnalyticsService(Database database) {
        this.database = database;
    }

    // BUG #1: INEFFICIENT - Loops through all orders multiple times
    // Gets all orders, calculates total in first loop
    // Then loops again to calculate average
    // Should do both in one pass
    public Map<String, Object> getSalesMetrics() {
        List<Order> allOrders = database.getAllOrders();

        // First loop - calculate total
        BigDecimal totalRevenue = BigDecimal.ZERO;
        for (Order order : allOrders) {
            totalRevenue = totalRevenue.add(order.getTotal());
        }

        // BUG #2: Creates unnecessary intermediate list
        List<BigDecimal> orderTotals = new ArrayList<>();
        for (Order order : allOrders) {
            orderTotals.add(order.getTotal());
        }

        // Second loop - calculate average
        BigDecimal averageOrderValue = BigDecimal.ZERO;
        if (!orderTotals.isEmpty()) {
            BigDecimal sum = BigDecimal.ZERO;
            for (BigDecimal total : orderTotals) {
                sum = sum.add(total);
            }
            averageOrderValue = sum.divide(new BigDecimal(orderTotals.size()));
        }

        // BUG #3: No caching - recalculates every time
        Map<String, Object> metrics = new HashMap<>();
        metrics.put("totalRevenue", totalRevenue);
        metrics.put("averageOrderValue", averageOrderValue);
        metrics.put("orderCount", allOrders.size());

        return metrics;
    }

    // BUG #4: WRONG LOGIC - Double counts books in categories
    // Joins orders with inventory, resulting in duplicates
    public Map<String, BigDecimal> getCategoryRevenue() {
        Map<String, BigDecimal> categoryRevenue = new HashMap<>();

        // Gets all orders
        List<Order> allOrders = database.getAllOrders();

        // For each order, tries to find books by category
        for (Order order : allOrders) {
            // This nested loop causes O(n²) complexity
            // And doesn't properly match books to categories
            for (int i = 0; i < order.getItems().size(); i++) {
                // Just adds amount multiple times if book appears multiple times
                String isbn = order.getItems().get(i).getBookIsbn();
                BigDecimal amount = order.getItems().get(i).getLineTotal();

                // Doesn't even know the category!
                categoryRevenue.put("Unknown", amount);
            }
        }

        return categoryRevenue;
    }

    // BUG #5: INEFFICIENT - Loads all orders just to find top N
    // Should use a priority queue or TreeMap for O(n log k)
    public List<String> getTopSellingBooks(int limit) {
        Map<String, Integer> bookSales = new HashMap<>();

        // Load ALL orders and count books
        List<Order> allOrders = database.getAllOrders();
        for (Order order : allOrders) {
            for (int i = 0; i < order.getItems().size(); i++) {
                String isbn = order.getItems().get(i).getBookIsbn();
                int qty = order.getItems().get(i).getQuantity();
                bookSales.put(isbn, bookSales.getOrDefault(isbn, 0) + qty);
            }
        }

        // BUG #6: Sorts ENTIRE map when only need top N
        // For 10k books and limit=10, this sorts 10k instead of maintaining top 10
        List<String> topBooks = new ArrayList<>(bookSales.keySet());
        topBooks.sort((a, b) -> bookSales.get(b).compareTo(bookSales.get(a)));

        return topBooks.subList(0, Math.min(limit, topBooks.size()));
    }

    // BUG #7: No filtering, no limits, recalculates entire dataset
    public BigDecimal calculateMonthlyRevenue(int year, int month) {
        BigDecimal total = BigDecimal.ZERO;

        // Loads ALL orders, then manually filters
        // Should have indexed data by date
        List<Order> allOrders = database.getAllOrders();
        for (Order order : allOrders) {
            int orderMonth = order.getOrderDate().getMonthValue();
            int orderYear = order.getOrderDate().getYear();

            if (orderMonth == month && orderYear == year) {
                total = total.add(order.getTotal());
            }
        }

        return total;
    }

    // BUG #8: MEMORY WASTE - Creates large intermediate collections
    public Map<Integer, Integer> getCustomerOrderCounts() {
        Map<Integer, Integer> customerOrderCounts = new HashMap<>();

        // Creates a list of all customer IDs first
        Set<Integer> allCustomerIds = new HashSet<>();
        for (Order order : database.getAllOrders()) {
            allCustomerIds.add(order.getCustomerId());
        }

        // Then loops again to count
        // Should combine into one loop
        for (Integer customerId : allCustomerIds) {
            int count = 0;
            for (Order order : database.getAllOrders()) { // Another full scan!
                if (order.getCustomerId() == customerId) {
                    count++;
                }
            }
            customerOrderCounts.put(customerId, count);
        }

        return customerOrderCounts;
    }

    // CLEAN FUNCTION - example of good code
    public int getTotalOrderCount() {
        return database.getAllOrders().size();
    }
}
