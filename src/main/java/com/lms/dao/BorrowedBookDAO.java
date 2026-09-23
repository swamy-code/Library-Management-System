package com.lms.dao;

import com.lms.model.BorrowedBook;
import com.lms.util.DatabaseConnection;

import java.sql.Connection;
import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class BorrowedBookDAO {

    public boolean borrowBook(int bookId, int userId) {
        String insertQuery = "INSERT INTO borrowed_books (book_id, user_id, borrow_date) VALUES (?, ?, CURRENT_DATE)";
        String updateBookQuery = "UPDATE books SET status = 'BORROWED' WHERE id = ?";

        Connection conn = DatabaseConnection.getConnection();
        if (conn == null)
            return false;
        try {
            conn.setAutoCommit(false); // Start transaction

            try (PreparedStatement insertStmt = conn.prepareStatement(insertQuery);
                    PreparedStatement updateStmt = conn.prepareStatement(updateBookQuery)) {

                insertStmt.setInt(1, bookId);
                insertStmt.setInt(2, userId);
                insertStmt.executeUpdate();

                updateStmt.setInt(1, bookId);
                updateStmt.executeUpdate();

                conn.commit(); // Commit transaction
                return true;
            } catch (SQLException e) {
                conn.rollback(); // Rollback on error
                e.printStackTrace();
            }
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            if (conn != null) {
                try {
                    conn.setAutoCommit(true);
                    conn.close();
                } catch (SQLException e) {
                    e.printStackTrace();
                }
            }
        }
        return false;
    }

    public boolean returnBook(int borrowId, int bookId) {
        String updateBorrowQuery = "UPDATE borrowed_books SET status = 'RETURNED', return_date = CURRENT_DATE WHERE id = ?";
        String updateBookQuery = "UPDATE books SET status = 'AVAILABLE' WHERE id = ?";

        Connection conn = DatabaseConnection.getConnection();
        if (conn == null)
            return false;
        try {
            conn.setAutoCommit(false); // Start transaction

            try (PreparedStatement updateBorrowStmt = conn.prepareStatement(updateBorrowQuery);
                    PreparedStatement updateBookStmt = conn.prepareStatement(updateBookQuery)) {

                updateBorrowStmt.setInt(1, borrowId);
                updateBorrowStmt.executeUpdate();

                updateBookStmt.setInt(1, bookId);
                updateBookStmt.executeUpdate();

                conn.commit(); // Commit transaction
                return true;
            } catch (SQLException e) {
                conn.rollback(); // Rollback on error
                e.printStackTrace();
            }
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            if (conn != null) {
                try {
                    conn.setAutoCommit(true);
                    conn.close();
                } catch (SQLException e) {
                    e.printStackTrace();
                }
            }
        }
        return false;
    }

    public List<BorrowedBook> getBorrowedBooksByUser(int userId) {
        List<BorrowedBook> list = new ArrayList<>();
        String query = "SELECT * FROM borrowed_books WHERE user_id = ?";
        Connection conn = DatabaseConnection.getConnection();
        if (conn == null)
            return list;
        try (PreparedStatement pstmt = conn.prepareStatement(query)) {

            pstmt.setInt(1, userId);
            ResultSet rs = pstmt.executeQuery();
            while (rs.next()) {
                list.add(new BorrowedBook(
                        rs.getInt("id"),
                        rs.getInt("book_id"),
                        rs.getInt("user_id"),
                        rs.getDate("borrow_date"),
                        rs.getDate("return_date"),
                        rs.getString("status")));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            try {
                conn.close();
            } catch (Exception ignored) {
            }
        }
        return list;
    }
}
