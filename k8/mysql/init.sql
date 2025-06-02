--CREATE DATABASE IF NOT EXISTS library_db;

--USE library_db;

--CREATE USER 'admin'@'%' IDENTIFIED BY 'password';
--GRANT ALL PRIVILEGES ON library_db.* TO 'admin'@'%';
--FLUSH PRIVILEGES;

-- Drop tables if they already exist (safe for dev resets)
DROP TABLE IF EXISTS library_db.book_categories;
DROP TABLE IF EXISTS library_db.borrow_records;
DROP TABLE IF EXISTS library_db.categories;
DROP TABLE IF EXISTS library_db.books;
DROP TABLE IF EXISTS library_db.users;

-- Users table
CREATE TABLE library_db.users (
    user_id INT AUTO_INCREMENT PRIMARY KEY,
    name VARCHAR(100) NOT NULL,
    email VARCHAR(100) UNIQUE NOT NULL,
    password VARCHAR(255) NOT NULL,
    role VARCHAR(255) DEFAULT 'MEMBER',
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

-- Books table
CREATE TABLE library_db.books (
    book_id INT AUTO_INCREMENT PRIMARY KEY,
    title VARCHAR(200) NOT NULL,
    author VARCHAR(100),
    publisher VARCHAR(100),
    total_copies INT NOT NULL,
    available_copies INT NOT NULL,
    added_on TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

-- Categories table
CREATE TABLE library_db.categories (
    category_id INT AUTO_INCREMENT PRIMARY KEY,
    name VARCHAR(100) UNIQUE NOT NULL
);

-- Book-Categories (Many-to-Many)
CREATE TABLE library_db.book_categories (
    book_id INT,
    category_id INT,
    PRIMARY KEY (book_id, category_id),
    FOREIGN KEY (book_id) REFERENCES library_db.books(book_id) ON DELETE CASCADE,
    FOREIGN KEY (category_id) REFERENCES library_db.categories(category_id) ON DELETE CASCADE
);

-- Borrow Records table
CREATE TABLE library_db.borrow_records (
    record_id INT AUTO_INCREMENT PRIMARY KEY,
    user_id INT,
    book_id INT,
    borrow_date DATE NOT NULL,
    due_date DATE NOT NULL,
    return_date DATE,
    status VARCHAR(100) DEFAULT 'BORROWED',
    FOREIGN KEY (user_id) REFERENCES library_db.users(user_id) ON DELETE CASCADE,
    FOREIGN KEY (book_id) REFERENCES library_db.books(book_id) ON DELETE CASCADE
);

-- Optional: Seed data (sample)
INSERT INTO library_db.users (name, email, password, role) VALUES
('Admin User', 'admin@library.com', 'admin123', 'ADMIN'),
('John Doe', 'john@example.com', 'password', 'MEMBER');

INSERT INTO library_db.categories (name) VALUES
('Fiction'), ('Non-fiction'), ('Science Fiction'), ('Biography');

INSERT INTO library_db.books (title, author, publisher, total_copies, available_copies) VALUES
('1984', 'George Orwell', 'Secker & Warburg', 5, 5),
('Sapiens', 'Yuval Noah Harari', 'Harvill Secker', 3, 3);

INSERT INTO library_db.book_categories (book_id, category_id) VALUES
(1, 1), (1, 3),  -- 1984: Fiction, Sci-Fi
(2, 2);          -- Sapiens: Non-fiction

