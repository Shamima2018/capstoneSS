package com.bookstore.models;

import java.time.LocalDateTime;

public class Inventory {
    private String bookIsbn;
    private int quantityAvailable;
    private int quantityReserved;
    private int reorderLevel;
    private LocalDateTime lastRestocked;

    public Inventory(String bookIsbn, int quantityAvailable) {
        this.bookIsbn = bookIsbn;
        this.quantityAvailable = quantityAvailable;
        this.quantityReserved = 0;
        this.reorderLevel = 10;
        this.lastRestocked = LocalDateTime.now();
    }

    public String getBookIsbn() { return bookIsbn; }
    public void setBookIsbn(String bookIsbn) { this.bookIsbn = bookIsbn; }

    public int getQuantityAvailable() { return quantityAvailable; }
    public void setQuantityAvailable(int quantityAvailable) { this.quantityAvailable = quantityAvailable; }

    public int getQuantityReserved() { return quantityReserved; }
    public void setQuantityReserved(int quantityReserved) { this.quantityReserved = quantityReserved; }

    public int getReorderLevel() { return reorderLevel; }
    public void setReorderLevel(int reorderLevel) { this.reorderLevel = reorderLevel; }

    public LocalDateTime getLastRestocked() { return lastRestocked; }
    public void setLastRestocked(LocalDateTime lastRestocked) { this.lastRestocked = lastRestocked; }

    public int getTotalQuantity() {
        return quantityAvailable + quantityReserved;
    }

    public boolean hasStock(int quantity) {
        return quantityAvailable >= quantity;
    }

    public boolean isLowStock() {
        return quantityAvailable <= reorderLevel;
    }

    @Override
    public String toString() {
        return "Inventory{" +
                "bookIsbn='" + bookIsbn + '\'' +
                ", available=" + quantityAvailable +
                ", reserved=" + quantityReserved +
                '}';
    }
}
