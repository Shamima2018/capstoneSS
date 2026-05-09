package com.bookstore;

import com.bookstore.models.Book;
import com.bookstore.utils.Database;
import java.math.BigDecimal;
import java.util.*;

public class Cart {
    private Database database;
    // BUG #1: Stores references to Book objects instead of snapshots
    // If book price changes in database, cart prices change too!
    private Map<String, CartItem> items; // key = isbn
    private BigDecimal total;

    public Cart(Database database) {
        this.database = database;
        this.items = new HashMap<>();
        this.total = BigDecimal.ZERO;
    }

    // BUG #2: Doesn't check if book already in cart
    // Just overwrites the entry instead of incrementing quantity
    public void addItem(String isbn, int quantity) {
        Book book = database.getBook(isbn);
        if (book == null) {
            throw new IllegalArgumentException("Book not found: " + isbn);
        }

        // If isbn already in cart, this overwrites instead of incrementing!
        CartItem item = new CartItem(isbn, book.getTitle(), quantity, book.getCurrentPrice());
        items.put(isbn, item);

        // BUG: Total not updated properly
        recalculateTotal();
    }

    // BUG #3: Total doesn't stay in sync with items
    // You can modify item quantity but total won't update
    public void updateQuantity(String isbn, int newQuantity) {
        CartItem item = items.get(isbn);
        if (item != null) {
            item.setQuantity(newQuantity);
            // Total is not recalculated!
        }
    }

    // BUG #4: Doesn't validate cart state - total might not match sum of items
    private void recalculateTotal() {
        total = BigDecimal.ZERO;
        for (CartItem item : items.values()) {
            BigDecimal itemTotal = item.getPrice().multiply(new BigDecimal(item.getQuantity()));
            total = total.add(itemTotal);
        }
        // BUG: No rounding or precision handling
    }

    // BUG #5: No undo functionality - can't rollback bad operations
    public void removeItem(String isbn) {
        items.remove(isbn);
        // Missing: recalculateTotal();
        // Total stays the same, becomes out of sync!
    }

    // BUG #6: Doesn't validate removing all items
    public void clear() {
        items.clear();
        total = BigDecimal.ZERO;
        // What if clearing fails midway? No transaction semantics
    }

    // BUG #7: equals() and hashCode() not implemented
    // Two carts with same items are not equal!
    // This breaks deduplication and comparison

    // BUG #8: Thread safety issues - no synchronization
    // Multiple threads can modify items concurrently
    public synchronized BigDecimal getTotal() {
        return total;
    }

    // BUG #9: Doesn't handle price changes gracefully
    // If book price changes after adding to cart, should the cart update?
    // Currently it does (because it stores references)
    // Should store price snapshot
    public BigDecimal getItemPrice(String isbn) {
        CartItem item = items.get(isbn);
        if (item != null) {
            // BUG: Returns current price from book, not price when added
            return database.getBook(isbn).getCurrentPrice();
        }
        return BigDecimal.ZERO;
    }

    public CartItem getItem(String isbn) {
        return items.get(isbn);
    }

    public List<CartItem> getAllItems() {
        return new ArrayList<>(items.values());
    }

    public int getItemCount() {
        return items.size();
    }

    public int getTotalQuantity() {
        int total = 0;
        for (CartItem item : items.values()) {
            total += item.getQuantity();
        }
        return total;
    }

    public boolean isEmpty() {
        return items.isEmpty();
    }

    // CLEAN CLASS - CartItem as example
    public static class CartItem {
        private String isbn;
        private String title;
        private int quantity;
        private BigDecimal price;

        public CartItem(String isbn, String title, int quantity, BigDecimal price) {
            this.isbn = isbn;
            this.title = title;
            this.quantity = quantity;
            this.price = price;
        }

        public String getIsbn() { return isbn; }
        public String getTitle() { return title; }
        public int getQuantity() { return quantity; }
        public void setQuantity(int quantity) { this.quantity = quantity; }
        public BigDecimal getPrice() { return price; }

        public BigDecimal getLineTotal() {
            return price.multiply(new BigDecimal(quantity));
        }
    }
}
