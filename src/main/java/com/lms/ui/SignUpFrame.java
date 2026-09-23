package com.lms.ui;

import com.lms.dao.UserDAO;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class SignUpFrame extends JFrame {
    private JTextField usernameField;
    private JPasswordField passwordField;
    private JPasswordField confirmPasswordField;
    private JComboBox<String> roleComboBox;
    private JButton registerButton;
    private JButton loginLinkButton;

    public SignUpFrame() {
        setTitle("Library Management System - Create Account");
        setSize(450, 480);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        setResizable(false);

        initComponents();
    }

    private void initComponents() {
        JPanel mainPanel = new JPanel(new BorderLayout());
        mainPanel.setBackground(new Color(245, 247, 250));

        // Header Panel
        JPanel headerPanel = new JPanel();
        headerPanel.setBackground(new Color(41, 128, 185));
        headerPanel.setPreferredSize(new Dimension(450, 70));
        headerPanel.setLayout(new GridBagLayout());

        JLabel headerLabel = new JLabel("Create New Account");
        headerLabel.setFont(new Font("Segoe UI", Font.BOLD, 22));
        headerLabel.setForeground(Color.WHITE);
        headerPanel.add(headerLabel);

        mainPanel.add(headerPanel, BorderLayout.NORTH);

        // Form Panel
        JPanel formPanel = new JPanel(new GridBagLayout());
        formPanel.setBackground(new Color(245, 247, 250));
        formPanel.setBorder(BorderFactory.createEmptyBorder(20, 30, 20, 30));
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.insets = new Insets(8, 5, 8, 5);

        Font labelFont = new Font("Segoe UI", Font.PLAIN, 14);
        Font inputFont = new Font("Segoe UI", Font.PLAIN, 14);

        // Username
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.gridwidth = 1;
        gbc.weightx = 0.3;
        JLabel userLabel = new JLabel("Username:");
        userLabel.setFont(labelFont);
        formPanel.add(userLabel, gbc);

        usernameField = new JTextField(15);
        usernameField.setFont(inputFont);
        gbc.gridx = 1;
        gbc.weightx = 0.7;
        formPanel.add(usernameField, gbc);

        // Password
        gbc.gridx = 0;
        gbc.gridy = 1;
        gbc.weightx = 0.3;
        JLabel passLabel = new JLabel("Password:");
        passLabel.setFont(labelFont);
        formPanel.add(passLabel, gbc);

        passwordField = new JPasswordField(15);
        passwordField.setFont(inputFont);
        gbc.gridx = 1;
        gbc.weightx = 0.7;
        formPanel.add(passwordField, gbc);

        // Confirm Password
        gbc.gridx = 0;
        gbc.gridy = 2;
        gbc.weightx = 0.3;
        JLabel confirmPassLabel = new JLabel("Confirm Pass:");
        confirmPassLabel.setFont(labelFont);
        formPanel.add(confirmPassLabel, gbc);

        confirmPasswordField = new JPasswordField(15);
        confirmPasswordField.setFont(inputFont);
        gbc.gridx = 1;
        gbc.weightx = 0.7;
        formPanel.add(confirmPasswordField, gbc);

        // Role Selection
        gbc.gridx = 0;
        gbc.gridy = 3;
        gbc.weightx = 0.3;
        JLabel roleLabel = new JLabel("Account Role:");
        roleLabel.setFont(labelFont);
        formPanel.add(roleLabel, gbc);

        String[] roles = {"USER (Student/Member)", "ADMIN (Librarian/Staff)"};
        roleComboBox = new JComboBox<>(roles);
        roleComboBox.setFont(inputFont);
        gbc.gridx = 1;
        gbc.weightx = 0.7;
        formPanel.add(roleComboBox, gbc);

        // Register Button
        registerButton = new JButton("Sign Up");
        registerButton.setFont(new Font("Segoe UI", Font.BOLD, 15));
        registerButton.setBackground(new Color(39, 174, 96));
        registerButton.setForeground(Color.WHITE);
        registerButton.setFocusPainted(false);
        registerButton.setCursor(new Cursor(Cursor.HAND_CURSOR));
        registerButton.setPreferredSize(new Dimension(200, 40));

        gbc.gridx = 0;
        gbc.gridy = 4;
        gbc.gridwidth = 2;
        gbc.weightx = 1.0;
        gbc.insets = new Insets(18, 5, 10, 5);
        formPanel.add(registerButton, gbc);

        // Switch to Login Link Button
        loginLinkButton = new JButton("Already have an account? Sign In");
        loginLinkButton.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        loginLinkButton.setForeground(new Color(41, 128, 185));
        loginLinkButton.setContentAreaFilled(false);
        loginLinkButton.setBorderPainted(false);
        loginLinkButton.setCursor(new Cursor(Cursor.HAND_CURSOR));

        gbc.gridy = 5;
        gbc.insets = new Insets(0, 5, 5, 5);
        formPanel.add(loginLinkButton, gbc);

        mainPanel.add(formPanel, BorderLayout.CENTER);
        add(mainPanel);

        // Action Listeners
        registerButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                performRegistration();
            }
        });

        loginLinkButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                dispose();
                new LoginFrame().setVisible(true);
            }
        });
    }

    private void performRegistration() {
        String username = usernameField.getText().trim();
        String password = new String(passwordField.getPassword());
        String confirmPassword = new String(confirmPasswordField.getPassword());
        String selectedRole = roleComboBox.getSelectedIndex() == 1 ? "ADMIN" : "USER";

        if (username.isEmpty() || password.isEmpty() || confirmPassword.isEmpty()) {
            JOptionPane.showMessageDialog(this, "All fields are required!", "Validation Error", JOptionPane.ERROR_MESSAGE);
            return;
        }

        if (username.length() < 3) {
            JOptionPane.showMessageDialog(this, "Username must be at least 3 characters long.", "Validation Error", JOptionPane.WARNING_MESSAGE);
            return;
        }

        if (!password.equals(confirmPassword)) {
            JOptionPane.showMessageDialog(this, "Passwords do not match!", "Validation Error", JOptionPane.ERROR_MESSAGE);
            return;
        }

        if (password.length() < 4) {
            JOptionPane.showMessageDialog(this, "Password must be at least 4 characters long.", "Validation Error", JOptionPane.WARNING_MESSAGE);
            return;
        }

        UserDAO userDAO = new UserDAO();
        if (userDAO.isUsernameTaken(username)) {
            JOptionPane.showMessageDialog(this, "Username '" + username + "' is already registered! Please choose another.", "Registration Failed", JOptionPane.WARNING_MESSAGE);
            return;
        }

        boolean success = userDAO.registerUser(username, password, selectedRole);

        if (success) {
            JOptionPane.showMessageDialog(this, "Account registered successfully! You can now log in.", "Success", JOptionPane.INFORMATION_MESSAGE);
            dispose();
            new LoginFrame().setVisible(true);
        } else {
            JOptionPane.showMessageDialog(this, "Registration failed due to a database error. Please try again.", "Error", JOptionPane.ERROR_MESSAGE);
        }
    }
}
