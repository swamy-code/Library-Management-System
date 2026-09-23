package com.lms.ui;

import com.lms.dao.BookDAO;
import com.lms.dao.BorrowedBookDAO;
import com.lms.model.Book;
import com.lms.model.BorrowedBook;
import com.lms.model.User;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.util.List;

public class UserDashboard extends JFrame {
    private User currentUser;
    private JTable bookTable;
    private DefaultTableModel tableModel;
    private BookDAO bookDAO;
    private BorrowedBookDAO borrowedBookDAO;

    public UserDashboard(User user) {
        this.currentUser = user;
        this.bookDAO = new BookDAO();
        this.borrowedBookDAO = new BorrowedBookDAO();
        
        setTitle("User Dashboard - " + user.getUsername());
        setSize(800, 500);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        
        initComponents();
        loadAvailableBooks();
    }

    private void initComponents() {
        setLayout(new BorderLayout());

        // Header Panel
        JPanel headerPanel = new JPanel();
        headerPanel.setBackground(new Color(39, 174, 96));
        JLabel titleLabel = new JLabel("Library User Dashboard");
        titleLabel.setForeground(Color.WHITE);
        titleLabel.setFont(new Font("Arial", Font.BOLD, 24));
        headerPanel.add(titleLabel);
        add(headerPanel, BorderLayout.NORTH);

        // Table Panel
        String[] columnNames = {"ID", "Title", "Author", "ISBN", "Status"};
        tableModel = new DefaultTableModel(columnNames, 0);
        bookTable = new JTable(tableModel);
        JScrollPane scrollPane = new JScrollPane(bookTable);
        add(scrollPane, BorderLayout.CENTER);

        // Button Panel
        JPanel buttonPanel = new JPanel();
        JButton borrowButton = new JButton("Borrow Selected Book");
        JButton viewBorrowedButton = new JButton("My Borrowed Books");
        JButton refreshButton = new JButton("Refresh Available Books");
        JButton logoutButton = new JButton("Logout");

        buttonPanel.add(borrowButton);
        buttonPanel.add(viewBorrowedButton);
        buttonPanel.add(refreshButton);
        buttonPanel.add(logoutButton);
        add(buttonPanel, BorderLayout.SOUTH);

        // Actions
        refreshButton.addActionListener(e -> loadAvailableBooks());
        
        logoutButton.addActionListener(e -> {
            dispose();
            new LoginFrame().setVisible(true);
        });

        borrowButton.addActionListener(e -> {
            int selectedRow = bookTable.getSelectedRow();
            if (selectedRow == -1) {
                JOptionPane.showMessageDialog(this, "Please select a book to borrow.");
                return;
            }
            int bookId = (int) tableModel.getValueAt(selectedRow, 0);
            String status = (String) tableModel.getValueAt(selectedRow, 4);
            
            if (!"AVAILABLE".equals(status)) {
                JOptionPane.showMessageDialog(this, "This book is not available.");
                return;
            }
            
            if (borrowedBookDAO.borrowBook(bookId, currentUser.getId())) {
                JOptionPane.showMessageDialog(this, "Book borrowed successfully!");
                loadAvailableBooks();
            } else {
                JOptionPane.showMessageDialog(this, "Failed to borrow book.");
            }
        });
        
        viewBorrowedButton.addActionListener(e -> showMyBorrowedBooks());
    }

    private void loadAvailableBooks() {
        tableModel.setRowCount(0);
        List<Book> books = bookDAO.getAvailableBooks();
        for (Book b : books) {
            tableModel.addRow(new Object[]{b.getId(), b.getTitle(), b.getAuthor(), b.getIsbn(), b.getStatus()});
        }
    }
    
    private void showMyBorrowedBooks() {
        JDialog dialog = new JDialog(this, "My Borrowed Books", true);
        dialog.setSize(600, 400);
        dialog.setLocationRelativeTo(this);
        
        String[] cols = {"Borrow ID", "Book ID", "Borrow Date", "Return Date", "Status"};
        DefaultTableModel model = new DefaultTableModel(cols, 0);
        JTable table = new JTable(model);
        
        List<BorrowedBook> list = borrowedBookDAO.getBorrowedBooksByUser(currentUser.getId());
        for (BorrowedBook b : list) {
            model.addRow(new Object[]{b.getId(), b.getBookId(), b.getBorrowDate(), b.getReturnDate(), b.getStatus()});
        }
        
        dialog.add(new JScrollPane(table), BorderLayout.CENTER);
        
        JButton returnButton = new JButton("Return Selected Book");
        returnButton.addActionListener(e -> {
            int row = table.getSelectedRow();
            if (row != -1) {
                int borrowId = (int) model.getValueAt(row, 0);
                int bookId = (int) model.getValueAt(row, 1);
                String status = (String) model.getValueAt(row, 4);
                
                if ("RETURNED".equals(status)) {
                    JOptionPane.showMessageDialog(dialog, "Book already returned.");
                    return;
                }
                
                if (borrowedBookDAO.returnBook(borrowId, bookId)) {
                    JOptionPane.showMessageDialog(dialog, "Book returned successfully!");
                    dialog.dispose();
                    loadAvailableBooks();
                } else {
                    JOptionPane.showMessageDialog(dialog, "Failed to return book.");
                }
            } else {
                JOptionPane.showMessageDialog(dialog, "Please select a book to return.");
            }
        });
        
        JPanel btnPanel = new JPanel();
        btnPanel.add(returnButton);
        dialog.add(btnPanel, BorderLayout.SOUTH);
        
        dialog.setVisible(true);
    }
}
