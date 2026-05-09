package com.bookstore;

import com.bookstore.models.Discount;
import com.bookstore.models.Order;
import com.bookstore.models.OrderItem;
import com.bookstore.utils.Database;
import java.math.BigDecimal;

public class DiscountService {
    private Database database;

    public DiscountService(Database database) {
        this.database = database;
    }

    // BUG #1: Wrong logic for PERCENTAGE vs FIXED discounts
    // If type is PERCENTAGE and value is 10, it treats 10 as 10% (correct)
    // But if type is FIXED, value 10 should be $10 off, not 10% off
    // This code applies the same logic to both!
    public BigDecimal calculateDiscount(Order order, String discountCode) {
        Discount discount = database.getDiscount(discountCode);

        if (discount == null) {
            return BigDecimal.ZERO;
        }

        // BUG #2: Doesn't check if discount is valid (expired, max usage reached)
        if (!discount.isValid()) {
            return BigDecimal.ZERO;
        }

        // BUG #3: Doesn't validate min purchase requirement
        if (order.getSubtotal().compareTo(discount.getMinPurchase()) < 0) {
            return BigDecimal.ZERO;
        }

        BigDecimal discountAmount = BigDecimal.ZERO;

        // BUG #4: Same calculation for PERCENTAGE and FIXED types!
        if (discount.getType().equals("PERCENTAGE")) {
            // If discount.getValue() is 10, this should mean 10%, so: subtotal * 0.10
            // But the code below treats it as if value is already in decimal form (like 0.10)
            // So 10% discount gets calculated as 10x discount!
            discountAmount = order.getSubtotal().multiply(discount.getValue());
        } else if (discount.getType().equals("FIXED")) {
            // For FIXED, should just be the value directly
            // But this also multiplies by the value, making a $10 fixed discount become $100!
            discountAmount = order.getSubtotal().multiply(discount.getValue());
        }

        // BUG #5: No cap on discount - could result in negative prices
        if (discountAmount.compareTo(order.getSubtotal()) > 0) {
            discountAmount = order.getSubtotal(); // This caps it, but buried way down here
        }

        return discountAmount;
    }

    // BUG #6: Inefficient - loops through ALL discounts every time
    // Instead of using a HashMap lookup
    public Discount validateDiscountCode(String code) {
        for (Discount discount : database.getAllDiscounts()) {
            if (discount.getCode().equals(code)) {
                // BUG #7: Checks validity but doesn't account for category/book restrictions
                return discount;
            }
        }
        return null;
    }

    // BUG #8: Doesn't update usage count after applying discount
    public void applyDiscount(Order order, String discountCode) {
        Discount discount = database.getDiscount(discountCode);
        if (discount != null && discount.isValid()) {
            BigDecimal discountAmount = calculateDiscount(order, discountCode);
            order.setDiscountApplied(discountAmount);
            order.setDiscountCode(discountCode);
            // BUG: Never increments usage count!
            // discount.incrementUsage();
        }
    }

    // BUG #9: No rounding for fractional cents
    // If discount results in $10.335, this stays as-is instead of rounding to $10.34
    public BigDecimal getAppliedDiscountAmount(Order order) {
        return order.getDiscountApplied();
    }

    // BUG #10: Category and book-specific discounts are ignored
    public boolean isApplicableToOrder(Discount discount, Order order) {
        // Check if discount applies to any books in the order
        // Currently doesn't check applicable categories or books
        return true; // Always returns true!
    }
}
