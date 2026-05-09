package com.bookstore;

import com.bookstore.models.*;
import com.bookstore.utils.Database;
import java.math.BigDecimal;
import java.util.*;

public class Main {
    private static Database database;
    private static BookService bookService;
    private static OrderService orderService;
    private static InventoryService inventoryService;
    private static DiscountService discountService;
    private static AnalyticsService analyticsService;

    public static void main(String[] args) {
        initializeServices();
        runCLI();
    }

    private static void initializeServices() {
        database = new Database();
        bookService = new BookService(database);
        inventoryService = new InventoryService(database);
        discountService = new DiscountService(database);
        orderService = new OrderService(database, inventoryService, discountService);
        analyticsService = new AnalyticsService(database);

        System.out.println("=== Bookstore Practice Codebase ===");
        System.out.println("Loaded " + bookService.getTotalBooksCount() + " books");
        System.out.println();
    }

    private static void runCLI() {
        Scanner scanner = new Scanner(System.in);
        boolean running = true;

        while (running) {
            printMenu();
            System.out.print("Enter command: ");
            String input = scanner.nextLine().trim();

            if (input.isEmpty()) continue;

            String[] parts = input.split(" ", 2);
            String command = parts[0].toLowerCase();

            try {
                switch (command) {
                    case "books":
                        handleBooksCommand(parts.length > 1 ? parts[1] : "");
                        break;
                    case "order":
                        handleOrderCommand(parts.length > 1 ? parts[1] : "");
                        break;
                    case "inventory":
                        handleInventoryCommand(parts.length > 1 ? parts[1] : "");
                        break;
                    case "discount":
                        handleDiscountCommand(parts.length > 1 ? parts[1] : "");
                        break;
                    case "analytics":
                        handleAnalyticsCommand(parts.length > 1 ? parts[1] : "");
                        break;
                    case "exit":
                        running = false;
                        System.out.println("Goodbye!");
                        break;
                    default:
                        System.out.println("Unknown command: " + command);
                }
            } catch (Exception e) {
                System.out.println("Error: " + e.getMessage());
            }
            System.out.println();
        }
        scanner.close();
    }

    private static void printMenu() {
        System.out.println("Available commands:");
        System.out.println("  books list              - List all books");
        System.out.println("  books search <keyword> - Search for a book");
        System.out.println("  books category <cat>   - List books by category");
        System.out.println("  order create <isbn> <qty> - Create order");
        System.out.println("  order list             - List all orders");
        System.out.println("  order customer <id>    - Get customer's orders");
        System.out.println("  inventory check <isbn> - Check book stock");
        System.out.println("  discount create <code> - Create discount");
        System.out.println("  discount list          - List discounts");
        System.out.println("  analytics sales        - Get sales metrics");
        System.out.println("  analytics top-books    - Get top selling books");
        System.out.println("  exit                   - Exit program");
    }

    private static void handleBooksCommand(String args) {
        if (args.isEmpty()) {
            System.out.println("Available: list, search, category");
            return;
        }

        String[] parts = args.split(" ", 2);
        String subcommand = parts[0].toLowerCase();

        switch (subcommand) {
            case "list":
                listAllBooks();
                break;
            case "search":
                if (parts.length < 2) {
                    System.out.println("Usage: books search <keyword>");
                } else {
                    searchBooks(parts[1]);
                }
                break;
            case "category":
                if (parts.length < 2) {
                    System.out.println("Usage: books category <category>");
                } else {
                    listBooksByCategory(parts[1]);
                }
                break;
            default:
                System.out.println("Unknown subcommand: " + subcommand);
        }
    }

    private static void listAllBooks() {
        List<Book> books = bookService.getAllBooks();
        System.out.println("Total books: " + books.size());
        for (int i = 0; i < Math.min(10, books.size()); i++) {
            Book book = books.get(i);
            System.out.println(book.getIsbn() + " | " + book.getTitle() + " | $" + book.getCurrentPrice());
        }
        if (books.size() > 10) {
            System.out.println("... and " + (books.size() - 10) + " more");
        }
    }

    private static void searchBooks(String keyword) {
        List<Book> results = bookService.searchBooks(keyword);
        System.out.println("Found " + results.size() + " books:");
        for (Book book : results) {
            System.out.println("  " + book.getTitle() + " by " + book.getAuthor());
        }
    }

    private static void listBooksByCategory(String category) {
        List<Book> books = bookService.getBooksByCategory(category);
        System.out.println("Books in category '" + category + "': " + books.size());
        for (Book book : books) {
            System.out.println("  " + book.getTitle() + " - $" + book.getCurrentPrice());
        }
    }

    private static void handleOrderCommand(String args) {
        if (args.isEmpty()) {
            System.out.println("Available: create, list, customer");
            return;
        }

        String[] parts = args.split(" ");
        String subcommand = parts[0].toLowerCase();

        switch (subcommand) {
            case "create":
                if (parts.length < 3) {
                    System.out.println("Usage: order create <isbn> <qty>");
                } else {
                    createOrder(parts[1], Integer.parseInt(parts[2]));
                }
                break;
            case "list":
                listOrders();
                break;
            case "customer":
                if (parts.length < 2) {
                    System.out.println("Usage: order customer <customer_id>");
                } else {
                    getCustomerOrders(Integer.parseInt(parts[1]));
                }
                break;
            default:
                System.out.println("Unknown subcommand: " + subcommand);
        }
    }

    private static void createOrder(String isbn, int quantity) {
        Order order = orderService.createOrder(1, isbn, quantity);
        if (order != null) {
            System.out.println("Order created! ID: " + order.getId());
            System.out.println("Total: $" + order.getTotal());
        }
    }

    private static void listOrders() {
        List<Order> orders = database.getAllOrders();
        System.out.println("Total orders: " + orders.size());
        for (Order order : orders) {
            System.out.println("  Order " + order.getId() + " | Customer " + order.getCustomerId() + " | $" + order.getTotal());
        }
    }

    private static void getCustomerOrders(int customerId) {
        List<Order> orders = orderService.getOrdersByCustomer(customerId);
        System.out.println("Orders for customer " + customerId + ": " + orders.size());
        for (Order order : orders) {
            System.out.println("  Order " + order.getId() + " | $" + order.getTotal());
        }
    }

    private static void handleInventoryCommand(String args) {
        if (args.isEmpty() || !args.contains(" ")) {
            System.out.println("Usage: inventory check <isbn>");
            return;
        }

        String[] parts = args.split(" ");
        String isbn = parts[1];

        Inventory inv = inventoryService.getInventory(isbn);
        System.out.println("ISBN: " + isbn);
        System.out.println("Available: " + inv.getQuantityAvailable());
        System.out.println("Reserved: " + inv.getQuantityReserved());
    }

    private static void handleDiscountCommand(String args) {
        if (args.isEmpty()) {
            System.out.println("Available: create, list");
            return;
        }

        String subcommand = args.split(" ")[0].toLowerCase();

        switch (subcommand) {
            case "create":
                createDiscount();
                break;
            case "list":
                listDiscounts();
                break;
            default:
                System.out.println("Unknown subcommand: " + subcommand);
        }
    }

    private static void createDiscount() {
        Scanner scanner = new Scanner(System.in);
        System.out.print("Discount code: ");
        String code = scanner.nextLine();
        System.out.print("Type (PERCENTAGE/FIXED): ");
        String type = scanner.nextLine();
        System.out.print("Value: ");
        BigDecimal value = new BigDecimal(scanner.nextLine());

        Discount discount = new Discount(code, type, value);
        database.saveDiscount(discount);
        System.out.println("Discount created: " + discount.getCode());
    }

    private static void listDiscounts() {
        List<Discount> discounts = database.getAllDiscounts();
        System.out.println("Total discounts: " + discounts.size());
        for (Discount d : discounts) {
            System.out.println("  " + d.getCode() + " | " + d.getType() + " | " + d.getValue());
        }
    }

    private static void handleAnalyticsCommand(String args) {
        if (args.isEmpty()) {
            System.out.println("Available: sales, top-books");
            return;
        }

        String subcommand = args.split(" ")[0].toLowerCase();

        switch (subcommand) {
            case "sales":
                showSalesMetrics();
                break;
            case "top-books":
                showTopBooks();
                break;
            default:
                System.out.println("Unknown subcommand: " + subcommand);
        }
    }

    private static void showSalesMetrics() {
        Map<String, Object> metrics = analyticsService.getSalesMetrics();
        System.out.println("=== Sales Metrics ===");
        System.out.println("Total Orders: " + metrics.get("orderCount"));
        System.out.println("Total Revenue: $" + metrics.get("totalRevenue"));
        System.out.println("Average Order: $" + metrics.get("averageOrderValue"));
    }

    private static void showTopBooks() {
        List<String> topBooks = analyticsService.getTopSellingBooks(5);
        System.out.println("Top 5 Selling Books:");
        for (String isbn : topBooks) {
            Book book = bookService.getBook(isbn);
            if (book != null) {
                System.out.println("  " + book.getTitle());
            }
        }
    }
}
