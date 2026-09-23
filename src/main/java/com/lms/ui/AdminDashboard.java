package com.lms.ui;

import com.lms.dao.BookDAO;
import com.lms.model.Book;
import com.lms.model.User;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.util.List;

public class AdminDashboard extends JFrame {
    private User admin;
    private JTable bookTable;
    private DefaultTableModel tableModel;
    private BookDAO bookDAO;

    public AdminDashboard(User admin) {
        this.admin = admin;
        this.bookDAO = new BookDAO();

        setTitle("Admin Dashboard - " + admin.getUsername());
        setSize(800, 500);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);

        initComponents();
        loadBooks();
    }

    private void initComponents() {
        setLayout(new BorderLayout());

        // Header Panel
        JPanel headerPanel = new JPanel();
        headerPanel.setBackground(new Color(52, 73, 94));
        JLabel titleLabel = new JLabel("Library Admin Dashboard");
        titleLabel.setForeground(Color.WHITE);
        titleLabel.setFont(new Font("Arial", Font.BOLD, 24));
        headerPanel.add(titleLabel);
        add(headerPanel, BorderLayout.NORTH);

        // Table Panel
        String[] columnNames = { "ID", "Title", "Author", "ISBN", "Status" };
        tableModel = new DefaultTableModel(columnNames, 0);
        bookTable = new JTable(tableModel);
        JScrollPane scrollPane = new JScrollPane(bookTable);
        add(scrollPane, BorderLayout.CENTER);

        // Button Panel
        JPanel buttonPanel = new JPanel();
        JButton addButton = new JButton("Add Book");
        JButton deleteButton = new JButton("Delete Book");
        JButton refreshButton = new JButton("Refresh");
        JButton logoutButton = new JButton("Logout");

        buttonPanel.add(addButton);
        buttonPanel.add(deleteButton);
        buttonPanel.add(refreshButton);
        buttonPanel.add(logoutButton);
        add(buttonPanel, BorderLayout.SOUTH);

        // Button Actions
        refreshButton.addActionListener(e -> loadBooks());

        logoutButton.addActionListener(e -> {
            dispose();
            new LoginFrame().setVisible(true);
        });

        addButton.addActionListener(e -> showAddBookDialog());

        deleteButton.addActionListener(e -> {
            int selectedRow = bookTable.getSelectedRow();
            if (selectedRow == -1) {
                JOptionPane.showMessageDialog(this, "Please select a book to delete.");
                return;
            }
            int bookId = (int) tableModel.getValueAt(selectedRow, 0);
            int confirm = JOptionPane.showConfirmDialog(this, "Are you sure you want to delete this book?");
            if (confirm == JOptionPane.YES_OPTION) {
                if (bookDAO.deleteBook(bookId)) {
                    JOptionPane.showMessageDialog(this, "Book deleted successfully!");
                    loadBooks();
                } else {
                    JOptionPane.showMessageDialog(this, "Failed to delete book.");
                }
            }
        });
    }

    private void loadBooks() {
        tableModel.setRowCount(0); // Clear existing
        List<Book> books = bookDAO.getAllBooks();
        for (Book b : books) {
            tableModel.addRow(new Object[] { b.getId(), b.getTitle(), b.getAuthor(), b.getIsbn(), b.getStatus() });
        }
    }

    private void showAddBookDialog() {
        JTextField titleField = new JTextField(15);
        JTextField authorField = new JTextField(15);
        JTextField isbnField = new JTextField(15);

        JPanel panel = new JPanel(new GridLayout(3, 2, 5, 5));
        panel.add(new JLabel("Title:"));
        panel.add(titleField);
        panel.add(new JLabel("Author:"));
        panel.add(authorField);
        panel.add(new JLabel("ISBN:"));
        panel.add(isbnField);

        int result = JOptionPane.showConfirmDialog(this, panel, "Add New Book", JOptionPane.OK_CANCEL_OPTION);
        if (result == JOptionPane.OK_OPTION) {
            Book book = new Book();
            book.setTitle(titleField.getText());
            book.setAuthor(authorField.getText());
            book.setIsbn(isbnField.getText());

            if (bookDAO.addBook(book)) {
                JOptionPane.showMessageDialog(this, "Book added successfully!");
                loadBooks();
            } else {
                JOptionPane.showMessageDialog(this, "Failed to add book.");
            }
        }
    }
}
