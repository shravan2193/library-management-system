package com.library.common.operations;

import com.library.common.model.Book;
import com.library.common.model.BorrowRecord;
import com.library.common.model.User;

import java.util.List;

public interface AdminOperations {
    boolean addBook(Book book);

    boolean updateBook(int bookId, Book book);

    boolean deleteBook(int bookId);

    boolean addCategory(String name);

    boolean assignCategoriesToBook(int bookId, List<Integer> categoryIds);

    List<User> getAllUsers();

    boolean promoteUserToAdmin(int userId);

    boolean deleteUser(int userId);

    List<BorrowRecord> getAllBorrowRecords();
}
