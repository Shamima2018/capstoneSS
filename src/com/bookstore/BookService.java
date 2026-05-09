package com.bookstore;

import com.bookstore.models.Book;
import com.bookstore.utils.Database;
import java.util.List;

public class BookService {
    private Database database;

    public BookService(Database database) {
        this.database = database;
    }

    public Book getBook(String isbn) {
        if (isbn == null || isbn.isEmpty()) {
            throw new IllegalArgumentException("ISBN cannot be empty");
        }
        return database.getBook(isbn);
    }

    public List<Book> getAllBooks() {
        return database.getAllBooks();
    }

    public List<Book> searchBooks(String keyword) {
        if (keyword == null || keyword.isEmpty()) {
            return getAllBooks();
        }
        return database.searchBooks(keyword);
    }

    public List<Book> getBooksByCategory(String category) {
        if (category == null || category.isEmpty()) {
            throw new IllegalArgumentException("Category cannot be empty");
        }
        return database.getBooksByCategory(category);
    }

    public void saveBook(Book book) {
        if (book == null) {
            throw new IllegalArgumentException("Book cannot be null");
        }
        if (book.getIsbn() == null || book.getIsbn().isEmpty()) {
            throw new IllegalArgumentException("ISBN cannot be empty");
        }
        if (book.getTitle() == null || book.getTitle().isEmpty()) {
            throw new IllegalArgumentException("Title cannot be empty");
        }
        database.saveBook(book);
    }

    public List<String> getAllCategories() {
        java.util.Set<String> categories = new java.util.HashSet<>();
        for (Book book : getAllBooks()) {
            if (book.getCategory() != null) {
                categories.add(book.getCategory());
            }
        }
        return new java.util.ArrayList<>(categories);
    }

    public boolean bookExists(String isbn) {
        return database.getBook(isbn) != null;
    }

    public int getTotalBooksCount() {
        return getAllBooks().size();
    }
}
