package com.bookstore;

import com.bookstore.models.Inventory;
import com.bookstore.utils.Database;

public class InventoryService {
    private Database database;

    public InventoryService(Database database) {
        this.database = database;
    }

    // BUG #1: RACE CONDITION - Not thread-safe!
    // If two threads call this simultaneously, both might see available=100
    // and both decrement, resulting in 98 instead of proper synchronization
    public void decrementStock(String isbn, int quantity) {
        Inventory inv = database.getInventory(isbn);

        // Thread could be interrupted here between read and write
        if (inv.getQuantityAvailable() < quantity) {
            throw new IllegalArgumentException("Insufficient inventory for " + isbn);
        }

        int newQuantity = inv.getQuantityAvailable() - quantity;
        inv.setQuantityAvailable(newQuantity);
        database.updateInventory(isbn, inv);
    }

    // BUG #2: No reservation system
    // When order is created, inventory isn't "reserved"
    // So two customers can buy the last book before inventory updates
    public void reserveStock(String isbn, int quantity) {
        Inventory inv = database.getInventory(isbn);

        if (inv.getQuantityAvailable() < quantity) {
            return; // Silently fails
        }

        // BUG: Updates available but should update RESERVED instead
        inv.setQuantityAvailable(inv.getQuantityAvailable() - quantity);
        // Should be: inv.setQuantityReserved(inv.getQuantityReserved() + quantity);
        database.updateInventory(isbn, inv);
    }

    // BUG #3: INCONSISTENT STATE POSSIBLE
    // This checks availability but doesn't atomically reserve it
    // Between check and reservation, another thread could buy the stock
    public boolean canFulfillOrder(String isbn, int quantity) {
        Inventory inv = database.getInventory(isbn);
        return inv.getQuantityAvailable() >= quantity;
        // Then caller would try to reserveStock() but it might fail
    }

    // BUG #4: No rollback on failure
    // If order fails to complete after inventory is decremented,
    // the inventory stays decremented (no compensation)
    public void processOrderInventory(String bookIsbn, int quantity) {
        try {
            decrementStock(bookIsbn, quantity);
            // Order processing continues...
            // If it fails here, inventory is already gone!
            // No way to put it back
        } catch (Exception e) {
            // Inventory was already decremented, can't rollback
            throw e;
        }
    }

    // BUG #5: Inefficient check - loops through all inventory
    public void restockLowItems() {
        // Would need to loop through all inventory items
        // Should use event-based or listener pattern instead
    }

    // CLEAN FUNCTION - example of good code
    public Inventory getInventory(String isbn) {
        if (isbn == null || isbn.isEmpty()) {
            throw new IllegalArgumentException("ISBN cannot be empty");
        }
        return database.getInventory(isbn);
    }

    public boolean hasStock(String isbn, int quantity) {
        Inventory inv = getInventory(isbn);
        return inv.hasStock(quantity);
    }

    public int getAvailableQuantity(String isbn) {
        return getInventory(isbn).getQuantityAvailable();
    }
}
