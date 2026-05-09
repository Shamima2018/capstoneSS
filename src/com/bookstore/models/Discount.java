package com.bookstore.models;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.Set;

public class Discount {
    private String code;
    private String description;
    private String type; // PERCENTAGE or FIXED
    private BigDecimal value;
    private BigDecimal minPurchase;
    private int maxUsage;
    private int usageCount;
    private Set<String> applicableCategories;
    private Set<String> applicableBooks;
    private LocalDateTime startDate;
    private LocalDateTime endDate;
    private boolean active;

    public Discount(String code, String type, BigDecimal value) {
        this.code = code;
        this.type = type;
        this.value = value;
        this.minPurchase = BigDecimal.ZERO;
        this.maxUsage = Integer.MAX_VALUE;
        this.usageCount = 0;
        this.applicableCategories = new HashSet<>();
        this.applicableBooks = new HashSet<>();
        this.active = true;
        this.startDate = LocalDateTime.now();
    }

    public String getCode() { return code; }
    public void setCode(String code) { this.code = code; }

    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }

    public String getType() { return type; }
    public void setType(String type) { this.type = type; }

    public BigDecimal getValue() { return value; }
    public void setValue(BigDecimal value) { this.value = value; }

    public BigDecimal getMinPurchase() { return minPurchase; }
    public void setMinPurchase(BigDecimal minPurchase) { this.minPurchase = minPurchase; }

    public int getMaxUsage() { return maxUsage; }
    public void setMaxUsage(int maxUsage) { this.maxUsage = maxUsage; }

    public int getUsageCount() { return usageCount; }
    public void setUsageCount(int usageCount) { this.usageCount = usageCount; }

    public Set<String> getApplicableCategories() { return applicableCategories; }
    public void setApplicableCategories(Set<String> applicableCategories) { this.applicableCategories = applicableCategories; }

    public Set<String> getApplicableBooks() { return applicableBooks; }
    public void setApplicableBooks(Set<String> applicableBooks) { this.applicableBooks = applicableBooks; }

    public LocalDateTime getStartDate() { return startDate; }
    public void setStartDate(LocalDateTime startDate) { this.startDate = startDate; }

    public LocalDateTime getEndDate() { return endDate; }
    public void setEndDate(LocalDateTime endDate) { this.endDate = endDate; }

    public boolean isActive() { return active; }
    public void setActive(boolean active) { this.active = active; }

    public boolean isExpired() {
        if (endDate == null) return false;
        return LocalDateTime.now().isAfter(endDate);
    }

    public boolean isValid() {
        return active && !isExpired() && usageCount < maxUsage;
    }

    public void incrementUsage() {
        this.usageCount++;
    }

    @Override
    public String toString() {
        return "Discount{" +
                "code='" + code + '\'' +
                ", type='" + type + '\'' +
                ", value=" + value +
                ", usageCount=" + usageCount +
                ", active=" + active +
                '}';
    }
}
