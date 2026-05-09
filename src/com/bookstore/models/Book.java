package com.bookstore.models;

import java.math.BigDecimal;
import java.time.LocalDate;

public class Book {
    private String isbn;
    private String title;
    private String author;
    private String description;
    private String category;
    private BigDecimal listPrice;
    private BigDecimal currentPrice;
    private String publisher;
    private LocalDate publishedDate;

    public Book(String isbn, String title, String author, BigDecimal listPrice, String category) {
        this.isbn = isbn;
        this.title = title;
        this.author = author;
        this.listPrice = listPrice;
        this.currentPrice = listPrice;
        this.category = category;
    }

    public Book(String isbn, String title, String author, String description,
                String category, BigDecimal listPrice, BigDecimal currentPrice,
                String publisher, LocalDate publishedDate) {
        this.isbn = isbn;
        this.title = title;
        this.author = author;
        this.description = description;
        this.category = category;
        this.listPrice = listPrice;
        this.currentPrice = currentPrice;
        this.publisher = publisher;
        this.publishedDate = publishedDate;
    }

    public String getIsbn() { return isbn; }
    public void setIsbn(String isbn) { this.isbn = isbn; }

    public String getTitle() { return title; }
    public void setTitle(String title) { this.title = title; }

    public String getAuthor() { return author; }
    public void setAuthor(String author) { this.author = author; }

    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }

    public String getCategory() { return category; }
    public void setCategory(String category) { this.category = category; }

    public BigDecimal getListPrice() { return listPrice; }
    public void setListPrice(BigDecimal listPrice) { this.listPrice = listPrice; }

    public BigDecimal getCurrentPrice() { return currentPrice; }
    public void setCurrentPrice(BigDecimal currentPrice) { this.currentPrice = currentPrice; }

    public String getPublisher() { return publisher; }
    public void setPublisher(String publisher) { this.publisher = publisher; }

    public LocalDate getPublishedDate() { return publishedDate; }
    public void setPublishedDate(LocalDate publishedDate) { this.publishedDate = publishedDate; }

    @Override
    public String toString() {
        return "Book{" +
                "isbn='" + isbn + '\'' +
                ", title='" + title + '\'' +
                ", author='" + author + '\'' +
                ", currentPrice=" + currentPrice +
                ", category='" + category + '\'' +
                '}';
    }
}
