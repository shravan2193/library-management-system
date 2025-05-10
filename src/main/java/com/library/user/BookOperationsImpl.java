package com.library.user;

import com.library.common.DBUtil;
import com.library.common.model.Book;
import com.library.common.model.BorrowRecord;
import com.library.common.operations.BookOperations;

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

public class BookOperationsImpl implements BookOperations {
    private static final Logger LOG = Logger.getLogger(BookOperationsImpl.class.getName());

    @Override
    public List<Book> getAllBooks() {
        List<Book> books = new ArrayList<>();

        // SQL query to fetch all books
        String query = "SELECT * FROM books";

        // Establish the connection
        try (Connection connection = DBUtil.getConnection();
             Statement statement = connection.createStatement();
             ResultSet resultSet = statement.executeQuery(query)) {

            // Process the result set
            while (resultSet.next()) {
                // Map result set to Book object
                Book book = new Book();
                book.setBookId(resultSet.getInt("book_id"));
                book.setTitle(resultSet.getString("title"));
                book.setAuthor(resultSet.getString("author"));
                book.setPublisher(resultSet.getString("publisher"));
                book.setTotalCopies(resultSet.getInt("total_copies"));
                book.setAvailableCopies(resultSet.getInt("available_copies"));
                book.setAddedOn(resultSet.getTimestamp("added_on"));

                // Add the book to the list
                books.add(book);
            }
        } catch (SQLException e) {
            LOG.log(Level.INFO, "Exception while fetching books: ", e);
        }

        return books;
    }

    @Override
    public Book getBookById(int bookId) {
        Book book = null;

        // SQL query to fetch a book by ID
        String query = "SELECT * FROM books WHERE book_id = ?";

        // Establish the connection
        try (Connection connection = DBUtil.getConnection();
             PreparedStatement preparedStatement = connection.prepareStatement(query)) {

            // Set the book ID in the query
            preparedStatement.setInt(1, bookId);

            // Execute the query
            try (ResultSet resultSet = preparedStatement.executeQuery()) {
                // Process the result set
                if (resultSet.next()) {
                    book = new Book();
                    book.setBookId(resultSet.getInt("book_id"));
                    book.setTitle(resultSet.getString("title"));
                    book.setAuthor(resultSet.getString("author"));
                    book.setPublisher(resultSet.getString("publisher"));
                    book.setTotalCopies(resultSet.getInt("total_copies"));
                    book.setAvailableCopies(resultSet.getInt("available_copies"));
                    book.setAddedOn(resultSet.getTimestamp("added_on"));
                }
            }
        } catch (SQLException e) {
            LOG.log(Level.INFO, "Exception while fetching book by ID: ", e);
        }

        return book;
    }

    @Override
    public List<Book> getBooksByCategory(String category) {
        List<Book> books = new ArrayList<>();

        // SQL query to fetch books by category
        String query = "SELECT b.book_id, b.title, b.author, b.publisher, b.total_copies, b.available_copies, b.added_on " +
                "FROM books b " +
                "JOIN book_categories bc ON b.book_id = bc.book_id " +
                "JOIN categories c ON bc.category_id = c.category_id " +
                "WHERE c.category_name = ?";

        // Establish the connection
        try (Connection connection = DBUtil.getConnection();
             PreparedStatement preparedStatement = connection.prepareStatement(query)) {

            // Set the category parameter
            preparedStatement.setString(1, category);

            // Execute the query
            try (ResultSet resultSet = preparedStatement.executeQuery()) {

                // Process the result set
                while (resultSet.next()) {
                    // Map result set to Book object
                    Book book = new Book();
                    book.setBookId(resultSet.getInt("book_id"));
                    book.setTitle(resultSet.getString("title"));
                    book.setAuthor(resultSet.getString("author"));
                    book.setPublisher(resultSet.getString("publisher"));
                    book.setTotalCopies(resultSet.getInt("total_copies"));
                    book.setAvailableCopies(resultSet.getInt("available_copies"));
                    book.setAddedOn(resultSet.getTimestamp("added_on"));

                    // Add the book to the list
                    books.add(book);
                }
            }
        } catch (SQLException e) {
            LOG.log(Level.SEVERE, "Exception while fetching books by category: ", e);
        }

        return books;
    }

    @Override
    public List<Book> searchBooks(String title, String author, String category, String publisher) {
        List<Book> books = new ArrayList<>();

        String query = "SELECT b.book_id, b.title, b.author, b.publisher, b.total_copies, b.available_copies, b.added_on " +
                "FROM books b " +
                "LEFT JOIN book_categories bc ON b.book_id = bc.book_id " +
                "LEFT JOIN categories c ON bc.category_id = c.category_id " +
                "WHERE (b.title LIKE CONCAT('%', ?, '%') OR ? IS NULL) " +
                "AND (b.author LIKE CONCAT('%', ?, '%') OR ? IS NULL) " +
                "AND (c.name LIKE CONCAT('%', ?, '%') OR ? IS NULL) " +
                "AND (b.publisher LIKE CONCAT('%', ?, '%') OR ? IS NULL)";

        try (Connection connection = DBUtil.getConnection();
             PreparedStatement preparedStatement = connection.prepareStatement(query)) {

            // Set parameters for the query
            preparedStatement.setString(1, title);
            preparedStatement.setString(2, title);
            preparedStatement.setString(3, author);
            preparedStatement.setString(4, author);
            preparedStatement.setString(5, category);
            preparedStatement.setString(6, category);
            preparedStatement.setString(7, publisher);
            preparedStatement.setString(8, publisher);

            try (ResultSet resultSet = preparedStatement.executeQuery()) {
                while (resultSet.next()) {
                    // Map result set to Book object
                    Book book = new Book();
                    book.setBookId(resultSet.getInt("book_id"));
                    book.setTitle(resultSet.getString("title"));
                    book.setAuthor(resultSet.getString("author"));
                    book.setPublisher(resultSet.getString("publisher"));
                    book.setTotalCopies(resultSet.getInt("total_copies"));
                    book.setAvailableCopies(resultSet.getInt("available_copies"));
                    book.setAddedOn(resultSet.getTimestamp("added_on"));
                    books.add(book);
                }
            }
        } catch (SQLException e) {
            LOG.log(Level.SEVERE, "Exception while searching books: ", e);
        }

        return books;
    }

    @Override
    public boolean borrowBook(int bookId) {
        String checkAvailabilityQuery = "SELECT available_copies FROM books WHERE book_id = ?";
        String updateBookQuery = "UPDATE books SET available_copies = available_copies - 1 WHERE book_id = ?";
        String insertBorrowRecordQuery = "INSERT INTO borrow_records (book_id, user_id, borrow_date) VALUES (?, ?, NOW())";

        try (Connection connection = DBUtil.getConnection()) {

            // Check availability
            try (PreparedStatement checkStmt = connection.prepareStatement(checkAvailabilityQuery)) {
                checkStmt.setInt(1, bookId);
                ResultSet resultSet = checkStmt.executeQuery();

                if (resultSet.next()) {
                    int availableCopies = resultSet.getInt("available_copies");

                    if (availableCopies > 0) {
                        // Book is available, proceed to borrow it
                        try (PreparedStatement updateStmt = connection.prepareStatement(updateBookQuery)) {
                            updateStmt.setInt(1, bookId);
                            int rowsUpdated = updateStmt.executeUpdate();

                            if (rowsUpdated > 0) {
                                // Insert into borrow history
                                try (PreparedStatement insertStmt = connection.prepareStatement(insertBorrowRecordQuery)) {
                                    insertStmt.setInt(1, bookId);
                                    insertStmt.setInt(2, 1);  // Assuming the user_id is 1, adjust accordingly
                                    insertStmt.executeUpdate();
                                    return true;  // Book borrowed successfully
                                }
                            }
                        }
                    }
                }
            }
        } catch (SQLException e) {
            LOG.log(Level.SEVERE, "Exception while borrowing books: ", e);
        }
        return false;
    }

    @Override
    public boolean checkBookBorrowedByUser(int bookId, int userId) {
        String query = "SELECT * FROM borrow_records WHERE book_id = ? AND user_id = ? AND return_date IS NULL";

        try (Connection connection = DBUtil.getConnection();
             PreparedStatement statement = connection.prepareStatement(query)) {
            statement.setInt(1, bookId);
            statement.setInt(2, userId);
            ResultSet resultSet = statement.executeQuery();

            return resultSet.next(); // If a result exists, the book is borrowed by the user
        } catch (SQLException e) {
            LOG.log(Level.SEVERE, "Exception while checking borrowed books: ", e);
        }

        return false; // Book not borrowed by the user
    }

    @Override
    public void returnBook(int bookId) {
        String updateQuery = "UPDATE books SET available_copies = available_copies + 1 WHERE book_id = ?";

        try (Connection connection = DBUtil.getConnection();
             PreparedStatement statement = connection.prepareStatement(updateQuery)) {
            statement.setInt(1, bookId);
            statement.executeUpdate();
        } catch (SQLException e) {
            LOG.log(Level.SEVERE, "Exception while returning books: ", e);
        }

        // Mark the book as returned in the borrow_records table
        String returnQuery = "UPDATE borrow_records SET return_date = NOW() WHERE book_id = ? AND return_date IS NULL";
        try (Connection connection = DBUtil.getConnection();
             PreparedStatement statement = connection.prepareStatement(returnQuery)) {
            statement.setInt(1, bookId);
            statement.executeUpdate();
        } catch (SQLException e) {
            LOG.log(Level.SEVERE, "Exception while updating book record: ", e);
        }
    }

    @Override
    public List<BorrowRecord> getBorrowHistory(int userId) {
        List<BorrowRecord> history = new ArrayList<>();
        String query = "SELECT book_id, borrow_date, return_date FROM borrow_records WHERE user_id = ?";

        try (Connection connection = DBUtil.getConnection();
             PreparedStatement stmt = connection.prepareStatement(query)) {

            stmt.setInt(1, userId);
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    BorrowRecord record = new BorrowRecord();
                    record.setBookId(rs.getInt("book_id"));
                    record.setBorrowDate(rs.getTimestamp("borrow_date"));
                    record.setReturnDate(rs.getTimestamp("return_date"));
                    history.add(record);
                }
            }
        } catch (SQLException e) {
            LOG.log(Level.SEVERE, "Error fetching borrow history", e);
        }

        return history;
    }
}
