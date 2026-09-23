package com.lms.util;

import java.net.InetSocketAddress;
import java.net.Socket;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;

public class DatabaseConnection {
    private static final String MYSQL_URL = "jdbc:mysql://localhost:3306/library_db?useSSL=false&allowPublicKeyRetrieval=true&serverTimezone=UTC";
    private static final String MYSQL_USER = "root";
    private static final String MYSQL_PASSWORD = "";

    private static final String H2_URL = "jdbc:h2:./library_db;MODE=MySQL;DATABASE_TO_LOWER=TRUE";
    private static final String H2_USER = "sa";
    private static final String H2_PASSWORD = "";

    private static boolean isInitialized = false;
    private static String activeMode = null;

    public static synchronized Connection getConnection() {
        Connection connection = null;
        
        // 1. Try MySQL first if not locked to H2
        if (!"H2".equals(activeMode)) {
            if (isPortOpen("localhost", 3306, 150)) {
                try {
                    Class.forName("com.mysql.cj.jdbc.Driver");
                    connection = DriverManager.getConnection(MYSQL_URL, MYSQL_USER, MYSQL_PASSWORD);
                    activeMode = "MYSQL";
                } catch (Exception e) {
                    activeMode = "H2";
                }
            } else {
                if (activeMode == null) {
                    System.out.println("[INFO] MySQL port 3306 not open. Instantly using embedded database mode (H2).");
                }
                activeMode = "H2";
            }
        }

        // 2. Fallback to H2 embedded database if MySQL failed or H2 is active
        if (connection == null) {
            try {
                Class.forName("org.h2.Driver");
                connection = DriverManager.getConnection(H2_URL, H2_USER, H2_PASSWORD);
                activeMode = "H2";
            } catch (Exception e) {
                System.err.println("[ERROR] Failed to establish database connection!");
                e.printStackTrace();
                return null;
            }
        }

        // 3. Initialize schema on first successful connection
        if (!isInitialized && connection != null) {
            initializeDatabase(connection);
            isInitialized = true;
        }

        return connection;
    }

    private static boolean isPortOpen(String host, int port, int timeoutMs) {
        try (Socket socket = new Socket()) {
            socket.connect(new InetSocketAddress(host, port), timeoutMs);
            return true;
        } catch (Exception e) {
            return false;
        }
    }

    private static void initializeDatabase(Connection conn) {
        try (Statement stmt = conn.createStatement()) {
            // Users table
            stmt.executeUpdate("CREATE TABLE IF NOT EXISTS users (" +
                    "id INT AUTO_INCREMENT PRIMARY KEY, " +
                    "username VARCHAR(50) UNIQUE NOT NULL, " +
                    "password VARCHAR(100) NOT NULL, " +
                    "role VARCHAR(20) DEFAULT 'USER', " +
                    "created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP)");

            // Books table
            stmt.executeUpdate("CREATE TABLE IF NOT EXISTS books (" +
                    "id INT AUTO_INCREMENT PRIMARY KEY, " +
                    "title VARCHAR(255) NOT NULL, " +
                    "author VARCHAR(255) NOT NULL, " +
                    "isbn VARCHAR(20) UNIQUE, " +
                    "status VARCHAR(20) DEFAULT 'AVAILABLE', " +
                    "added_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP)");

            // Borrowed books table
            stmt.executeUpdate("CREATE TABLE IF NOT EXISTS borrowed_books (" +
                    "id INT AUTO_INCREMENT PRIMARY KEY, " +
                    "book_id INT, " +
                    "user_id INT, " +
                    "borrow_date DATE, " +
                    "return_date DATE, " +
                    "status VARCHAR(20) DEFAULT 'ISSUED', " +
                    "FOREIGN KEY (book_id) REFERENCES books(id) ON DELETE CASCADE, " +
                    "FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE CASCADE)");

            // Seed default admin and user if users table is empty
            var rs = stmt.executeQuery("SELECT COUNT(*) FROM users");
            if (rs.next() && rs.getInt(1) == 0) {
                stmt.executeUpdate("INSERT INTO users (username, password, role) VALUES ('admin', 'admin123', 'ADMIN')");
                stmt.executeUpdate("INSERT INTO users (username, password, role) VALUES ('user', 'user123', 'USER')");
            }

            // Seed sample books if books table is empty
            var rsBooks = stmt.executeQuery("SELECT COUNT(*) FROM books");
            if (rsBooks.next() && rsBooks.getInt(1) == 0) {
                stmt.executeUpdate("INSERT INTO books (title, author, isbn) VALUES ('Clean Code', 'Robert C. Martin', '9780132350884')");
                stmt.executeUpdate("INSERT INTO books (title, author, isbn) VALUES ('Design Patterns', 'Erich Gamma', '9780201633610')");
                stmt.executeUpdate("INSERT INTO books (title, author, isbn) VALUES ('Effective Java', 'Joshua Bloch', '9780134685991')");
            }

            System.out.println("[INFO] Database schema and initial data verified successfully in " + activeMode + " mode.");
        } catch (SQLException e) {
            System.err.println("[WARN] Database schema auto-initialization exception: " + e.getMessage());
        }
    }
}
