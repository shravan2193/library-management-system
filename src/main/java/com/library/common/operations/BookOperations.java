package com.library.common.operations;

import com.library.common.model.Book;
import com.library.common.model.BorrowRecord;

import java.util.List;

public interface BookOperations {
    List<Book> getAllBooks();

    Book getBookById(int bookId);

    List<Book> getBooksByCategory(String category);

    List<Book> searchBooks(String title, String author, String category, String publisher);

    boolean borrowBook(int bookId);

    boolean checkBookBorrowedByUser(int bookId, int userId);

    void returnBook(int bookId);

    List<BorrowRecord> getBorrowHistory(int userId);
}
