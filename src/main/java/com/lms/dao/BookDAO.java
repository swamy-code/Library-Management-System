package com.lms.dao;

import com.lms.model.Book;
import com.lms.util.DatabaseConnection;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class BookDAO {

    public List<Book> getAllBooks() {
        List<Book> books = new ArrayList<>();
        String query = "SELECT * FROM books";
        Connection conn = DatabaseConnection.getConnection();
        if (conn == null) return books;
        try (Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(query)) {
             
            while (rs.next()) {
                books.add(new Book(
                    rs.getInt("id"),
                    rs.getString("title"),
                    rs.getString("author"),
                    rs.getString("isbn"),
                    rs.getString("status"),
                    rs.getTimestamp("added_at")
                ));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            try { conn.close(); } catch (Exception ignored) {}
        }
        return books;
    }

    public List<Book> getAvailableBooks() {
        List<Book> books = new ArrayList<>();
        String query = "SELECT * FROM books WHERE status = 'AVAILABLE'";
        Connection conn = DatabaseConnection.getConnection();
        if (conn == null) return books;
        try (Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(query)) {
             
            while (rs.next()) {
                books.add(new Book(
                    rs.getInt("id"),
                    rs.getString("title"),
                    rs.getString("author"),
                    rs.getString("isbn"),
                    rs.getString("status"),
                    rs.getTimestamp("added_at")
                ));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            try { conn.close(); } catch (Exception ignored) {}
        }
        return books;
    }

    public boolean addBook(Book book) {
        String query = "INSERT INTO books (title, author, isbn) VALUES (?, ?, ?)";
        Connection conn = DatabaseConnection.getConnection();
        if (conn == null) return false;
        try (PreparedStatement pstmt = conn.prepareStatement(query)) {
            
            pstmt.setString(1, book.getTitle());
            pstmt.setString(2, book.getAuthor());
            pstmt.setString(3, book.getIsbn());
            
            int rowsAffected = pstmt.executeUpdate();
            return rowsAffected > 0;
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            try { conn.close(); } catch (Exception ignored) {}
        }
        return false;
    }
    
    public boolean updateBookStatus(int bookId, String status) {
        String query = "UPDATE books SET status = ? WHERE id = ?";
        Connection conn = DatabaseConnection.getConnection();
        if (conn == null) return false;
        try (PreparedStatement pstmt = conn.prepareStatement(query)) {
            
            pstmt.setString(1, status);
            pstmt.setInt(2, bookId);
            
            int rowsAffected = pstmt.executeUpdate();
            return rowsAffected > 0;
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            try { conn.close(); } catch (Exception ignored) {}
        }
        return false;
    }
    
    public boolean deleteBook(int bookId) {
        String query = "DELETE FROM books WHERE id = ?";
        Connection conn = DatabaseConnection.getConnection();
        if (conn == null) return false;
        try (PreparedStatement pstmt = conn.prepareStatement(query)) {
            
            pstmt.setInt(1, bookId);
            
            int rowsAffected = pstmt.executeUpdate();
            return rowsAffected > 0;
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            try { conn.close(); } catch (Exception ignored) {}
        }
        return false;
    }
}
