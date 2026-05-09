package com.bookstore.models;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

public class Order {
    private int id;
    private int customerId;
    private List<OrderItem> items;
    private BigDecimal subtotal;
    private BigDecimal discountApplied;
    private String discountCode;
    private BigDecimal tax;
    private BigDecimal total;
    private String status; // PENDING, CONFIRMED, SHIPPED, DELIVERED
    private LocalDateTime orderDate;
    private LocalDateTime shippingDate;
    private LocalDateTime deliveryDate;

    public Order(int id, int customerId) {
        this.id = id;
        this.customerId = customerId;
        this.items = new ArrayList<>();
        this.subtotal = BigDecimal.ZERO;
        this.discountApplied = BigDecimal.ZERO;
        this.tax = BigDecimal.ZERO;
        this.total = BigDecimal.ZERO;
        this.status = "PENDING";
        this.orderDate = LocalDateTime.now();
    }

    public int getId() { return id; }
    public void setId(int id) { this.id = id; }

    public int getCustomerId() { return customerId; }
    public void setCustomerId(int customerId) { this.customerId = customerId; }

    public List<OrderItem> getItems() { return items; }
    public void setItems(List<OrderItem> items) { this.items = items; }

    public BigDecimal getSubtotal() { return subtotal; }
    public void setSubtotal(BigDecimal subtotal) { this.subtotal = subtotal; }

    public BigDecimal getDiscountApplied() { return discountApplied; }
    public void setDiscountApplied(BigDecimal discountApplied) { this.discountApplied = discountApplied; }

    public String getDiscountCode() { return discountCode; }
    public void setDiscountCode(String discountCode) { this.discountCode = discountCode; }

    public BigDecimal getTax() { return tax; }
    public void setTax(BigDecimal tax) { this.tax = tax; }

    public BigDecimal getTotal() { return total; }
    public void setTotal(BigDecimal total) { this.total = total; }

    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }

    public LocalDateTime getOrderDate() { return orderDate; }
    public void setOrderDate(LocalDateTime orderDate) { this.orderDate = orderDate; }

    public LocalDateTime getShippingDate() { return shippingDate; }
    public void setShippingDate(LocalDateTime shippingDate) { this.shippingDate = shippingDate; }

    public LocalDateTime getDeliveryDate() { return deliveryDate; }
    public void setDeliveryDate(LocalDateTime deliveryDate) { this.deliveryDate = deliveryDate; }

    public void addItem(OrderItem item) {
        items.add(item);
        recalculateSubtotal();
    }

    public void removeItem(String bookIsbn) {
        items.removeIf(item -> item.getBookIsbn().equals(bookIsbn));
        recalculateSubtotal();
    }

    public void recalculateSubtotal() {
        subtotal = items.stream()
                .map(OrderItem::getLineTotal)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
    }

    @Override
    public String toString() {
        return "Order{" +
                "id=" + id +
                ", customerId=" + customerId +
                ", status='" + status + '\'' +
                ", subtotal=" + subtotal +
                ", discount=" + discountApplied +
                ", total=" + total +
                ", itemCount=" + items.size() +
                '}';
    }
}
