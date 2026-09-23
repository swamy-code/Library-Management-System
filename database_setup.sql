CREATE DATABASE IF NOT EXISTS library_db;
USE library_db;

-- Users table (Admins and Students)
CREATE TABLE IF NOT EXISTS users (
    id INT AUTO_INCREMENT PRIMARY KEY,
    username VARCHAR(50) UNIQUE NOT NULL,
    password VARCHAR(100) NOT NULL,
    role ENUM('ADMIN', 'USER') DEFAULT 'USER',
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

-- Books table
CREATE TABLE IF NOT EXISTS books (
    id INT AUTO_INCREMENT PRIMARY KEY,
    title VARCHAR(255) NOT NULL,
    author VARCHAR(255) NOT NULL,
    isbn VARCHAR(20) UNIQUE,
    status ENUM('AVAILABLE', 'BORROWED') DEFAULT 'AVAILABLE',
    added_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

-- Borrowed books tracking table
CREATE TABLE IF NOT EXISTS borrowed_books (
    id INT AUTO_INCREMENT PRIMARY KEY,
    book_id INT,
    user_id INT,
    borrow_date DATE,
    return_date DATE,
    status ENUM('ISSUED', 'RETURNED') DEFAULT 'ISSUED',
    FOREIGN KEY (book_id) REFERENCES books(id) ON DELETE CASCADE,
    FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE CASCADE
);

-- Insert a default admin user (password is 'admin123')
INSERT INTO users (username, password, role) VALUES ('admin', 'admin123', 'ADMIN') ON DUPLICATE KEY UPDATE id=id;
-- Insert a default student user (password is 'user123')
INSERT INTO users (username, password, role) VALUES ('user', 'user123', 'USER') ON DUPLICATE KEY UPDATE id=id;

-- Insert some sample books
INSERT INTO books (title, author, isbn) VALUES ('Clean Code', 'Robert C. Martin', '9780132350884') ON DUPLICATE KEY UPDATE id=id;
INSERT INTO books (title, author, isbn) VALUES ('Design Patterns', 'Erich Gamma', '9780201633610') ON DUPLICATE KEY UPDATE id=id;
INSERT INTO books (title, author, isbn) VALUES ('Effective Java', 'Joshua Bloch', '9780134685991') ON DUPLICATE KEY UPDATE id=id;
