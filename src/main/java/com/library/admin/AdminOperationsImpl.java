package com.library.admin;

import com.library.common.DBUtil;
import com.library.common.model.Book;
import com.library.common.model.BorrowRecord;
import com.library.common.model.User;
import com.library.common.operations.AdminOperations;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

public class AdminOperationsImpl implements AdminOperations {
    private static final Logger LOG = Logger.getLogger(AdminOperationsImpl.class.getName());

    @Override
    public boolean addBook(Book book) {
        String query = "INSERT INTO books (title, author, publisher, total_copies, available_copies) VALUES (?, ?, ?, ?, ?)";

        try (Connection connection = DBUtil.getConnection();
             PreparedStatement ps = connection.prepareStatement(query)) {

            ps.setString(1, book.getTitle());
            ps.setString(2, book.getAuthor());
            ps.setString(3, book.getPublisher());
            ps.setInt(4, book.getTotalCopies());
            ps.setInt(5, book.getTotalCopies());  // Initially available copies = total copies

            int rowsInserted = ps.executeUpdate();
            return rowsInserted > 0;

        } catch (SQLException e) {
            LOG.log(Level.SEVERE, "Error adding book: ", e);
            return false;
        }
    }

    @Override
    public boolean updateBook(int bookId, Book book) {
        String query = "UPDATE books SET title = ?, author = ?, publisher = ?, total_copies = ?, available_copies = ? WHERE book_id = ?";
        try (Connection connection = DBUtil.getConnection();
             PreparedStatement statement = connection.prepareStatement(query)) {

            statement.setString(1, book.getTitle());
            statement.setString(2, book.getAuthor());
            statement.setString(3, book.getPublisher());
            statement.setInt(4, book.getTotalCopies());
            statement.setInt(5, book.getAvailableCopies());
            statement.setInt(6, bookId);

            int rowsUpdated = statement.executeUpdate();
            return rowsUpdated > 0;

        } catch (SQLException e) {
            LOG.log(Level.SEVERE, "Error updating book details", e);
            return false;
        }
    }

    @Override
    public boolean deleteBook(int bookId) {
        String query = "DELETE FROM books WHERE book_id = ?";

        try (Connection connection = DBUtil.getConnection();
             PreparedStatement statement = connection.prepareStatement(query)) {

            statement.setInt(1, bookId);
            int rowsAffected = statement.executeUpdate();
            return rowsAffected > 0;

        } catch (SQLException e) {
            LOG.log(Level.SEVERE, "Error while deleting book from DB", e);
        }

        return false;
    }

    @Override
    public boolean addCategory(String name) {
        String checkQuery = "SELECT * FROM categories WHERE name = ?";
        String insertQuery = "INSERT INTO categories (name) VALUES (?)";

        try (Connection connection = DBUtil.getConnection();
             PreparedStatement checkStmt = connection.prepareStatement(checkQuery);
             PreparedStatement insertStmt = connection.prepareStatement(insertQuery)) {

            checkStmt.setString(1, name);
            ResultSet rs = checkStmt.executeQuery();
            if (rs.next()) {
                return false; // category already exists
            }

            insertStmt.setString(1, name);
            int rowsInserted = insertStmt.executeUpdate();

            return rowsInserted > 0;

        } catch (SQLException e) {
            LOG.log(Level.SEVERE, "Error while adding category", e);
        }

        return false;
    }

    @Override
    public boolean assignCategoriesToBook(int bookId, List<Integer> categoryIds) {
        String insertQuery = "INSERT INTO book_categories (book_id, category_id) VALUES (?, ?)";

        try (Connection connection = DBUtil.getConnection()) {
            try (PreparedStatement statement = connection.prepareStatement(insertQuery)) {
                for (int categoryId : categoryIds) {
                    statement.setInt(1, bookId);
                    statement.setInt(2, categoryId);
                    statement.addBatch();
                }
                statement.executeBatch();
            }
        } catch (SQLException e) {
            LOG.log(Level.SEVERE, "Error assigning categories to book", e);
            return false;
        }

        return true;
    }

    @Override
    public List<User> getAllUsers() {
        List<User> users = new ArrayList<>();

        String query = "SELECT user_id, name, email, role FROM users";

        try (Connection connection = DBUtil.getConnection();
             Statement statement = connection.createStatement();
             ResultSet resultSet = statement.executeQuery(query)) {

            while (resultSet.next()) {
                User user = new User();
                user.setUserid(resultSet.getInt("user_id"));
                user.setUsername(resultSet.getString("name"));
                user.setEmail(resultSet.getString("email"));
                user.setRole(resultSet.getString("role"));

                users.add(user);
            }

        } catch (SQLException e) {
            LOG.log(Level.SEVERE, "Error while fetching users: ", e);
        }

        return users;
    }

    @Override
    public boolean promoteUserToAdmin(int userId) {
        String checkQuery = "SELECT role FROM users WHERE user_id = ?";
        String updateQuery = "UPDATE users SET role = 'ADMIN' WHERE user_id = ?";

        try (Connection connection = DBUtil.getConnection();
             PreparedStatement checkStmt = connection.prepareStatement(checkQuery)) {

            checkStmt.setInt(1, userId);
            ResultSet rs = checkStmt.executeQuery();

            if (rs.next()) {
                String currentRole = rs.getString("role");
                if ("ADMIN".equalsIgnoreCase(currentRole)) {
                    return false; // Already an admin
                }

                try (PreparedStatement updateStmt = connection.prepareStatement(updateQuery)) {
                    updateStmt.setInt(1, userId);
                    int rowsAffected = updateStmt.executeUpdate();
                    return rowsAffected > 0;
                }
            }

        } catch (SQLException e) {
            LOG.log(Level.SEVERE, "Error promoting user to admin", e);
        }

        return false;
    }

    @Override
    public boolean deleteUser(int userId) {
        String deleteQuery = "DELETE FROM users WHERE user_id = ?";

        try (Connection connection = DBUtil.getConnection();
             PreparedStatement statement = connection.prepareStatement(deleteQuery)) {

            statement.setInt(1, userId);
            int rowsAffected = statement.executeUpdate();
            return rowsAffected > 0;

        } catch (SQLException e) {
            LOG.log(Level.SEVERE, "Error deleting user with ID: " + userId, e);
        }

        return false;
    }

    @Override
    public List<BorrowRecord> getAllBorrowRecords() {
        List<BorrowRecord> records = new ArrayList<>();
        String query = "SELECT user_id, book_id, borrow_date, return_date FROM borrow_records";

        try (Connection connection = DBUtil.getConnection();
             Statement statement = connection.createStatement();
             ResultSet resultSet = statement.executeQuery(query)) {

            while (resultSet.next()) {
                BorrowRecord record = new BorrowRecord();
                record.setUserId(resultSet.getInt("user_id"));
                record.setBookId(resultSet.getInt("book_id"));
                record.setBorrowDate(resultSet.getTimestamp("borrow_date"));
                record.setReturnDate(resultSet.getTimestamp("return_date"));
                records.add(record);
            }

        } catch (SQLException e) {
            LOG.log(Level.SEVERE, "Error fetching borrow records", e);
        }

        return records;
    }
}
