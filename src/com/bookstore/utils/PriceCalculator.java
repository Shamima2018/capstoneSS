package com.bookstore.utils;

import java.math.BigDecimal;

public class PriceCalculator {
    private static final double TAX_RATE = 0.10;
    private static final int SCALE = 2;

    // BUG #1: Tax is applied BEFORE discount (wrong order)
    // BUG #2: Uses double instead of BigDecimal causing precision loss
    // BUG #3: No validation for negative prices
    // BUG #4: Rounding happens multiple times, accumulating error
    public static BigDecimal calculateFinalPrice(BigDecimal basePrice, BigDecimal discountAmount) {
        if (basePrice == null) {
            throw new IllegalArgumentException("Base price cannot be null");
        }

        // Apply tax first (WRONG - should be after discount)
        double priceWithTax = basePrice.doubleValue() * (1 + TAX_RATE);

        // Then apply discount (WRONG ORDER)
        double discountedPrice = priceWithTax - discountAmount.doubleValue();

        // Round at the end
        BigDecimal result = new BigDecimal(discountedPrice);
        result = result.setScale(SCALE, BigDecimal.ROUND_HALF_UP);

        return result;
    }

    // BUG #5: Percentage discount calculation is wrong for certain values
    // Formula should be: basePrice * (1 - percentageRate)
    // This one: basePrice - (basePrice * percentageRate) - which seems right but let's introduce a bug
    // Actually multiplies percentage by 100 again!
    public static BigDecimal calculatePercentageDiscount(BigDecimal basePrice, BigDecimal percentageRate) {
        // If percentageRate is 10 (meaning 10%), this will apply 1000% discount!
        BigDecimal discountAmount = basePrice.multiply(percentageRate);
        return basePrice.subtract(discountAmount);
    }

    // BUG #6: Fixed amount discount doesn't validate max discount
    public static BigDecimal applyFixedDiscount(BigDecimal basePrice, BigDecimal discountAmount) {
        if (discountAmount.compareTo(basePrice) > 0) {
            // This could result in negative prices!
            return basePrice.subtract(discountAmount);
        }
        return basePrice.subtract(discountAmount);
    }

    // BUG #7: Tax calculation uses wrong formula for rounded amounts
    public static BigDecimal calculateTax(BigDecimal priceAfterDiscount) {
        double tax = priceAfterDiscount.doubleValue() * TAX_RATE;
        return new BigDecimal(tax).setScale(2, BigDecimal.ROUND_DOWN);
    }

    // BUG #8: Magic number 1.10 instead of configurable tax rate
    public static BigDecimal calculateTotalWithTax(BigDecimal basePrice) {
        return basePrice.multiply(new BigDecimal("1.10")).setScale(2, BigDecimal.ROUND_HALF_UP);
    }

    // Clean utility function (no bugs - example of good code)
    public static boolean isValidPrice(BigDecimal price) {
        return price != null && price.compareTo(BigDecimal.ZERO) > 0;
    }
}
