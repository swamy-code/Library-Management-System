package com.lms.ui;

import com.lms.dao.UserDAO;
import com.lms.model.User;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class LoginFrame extends JFrame {
    private JTextField usernameField;
    private JPasswordField passwordField;
    private JButton loginButton;
    private JButton signUpLinkButton;

    public LoginFrame() {
        setTitle("Library Management System - Sign In");
        setSize(450, 420);
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

        JLabel headerLabel = new JLabel("Library System Sign In");
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
        gbc.insets = new Insets(10, 5, 10, 5);

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

        // Login Button
        loginButton = new JButton("Sign In");
        loginButton.setFont(new Font("Segoe UI", Font.BOLD, 15));
        loginButton.setBackground(new Color(41, 128, 185));
        loginButton.setForeground(Color.WHITE);
        loginButton.setFocusPainted(false);
        loginButton.setCursor(new Cursor(Cursor.HAND_CURSOR));
        loginButton.setPreferredSize(new Dimension(200, 40));

        gbc.gridx = 0;
        gbc.gridy = 2;
        gbc.gridwidth = 2;
        gbc.weightx = 1.0;
        gbc.insets = new Insets(20, 5, 10, 5);
        formPanel.add(loginButton, gbc);

        // Switch to Sign Up Link Button
        signUpLinkButton = new JButton("Don't have an account? Sign Up");
        signUpLinkButton.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        signUpLinkButton.setForeground(new Color(39, 174, 96));
        signUpLinkButton.setContentAreaFilled(false);
        signUpLinkButton.setBorderPainted(false);
        signUpLinkButton.setCursor(new Cursor(Cursor.HAND_CURSOR));

        gbc.gridy = 3;
        gbc.insets = new Insets(0, 5, 5, 5);
        formPanel.add(signUpLinkButton, gbc);

        mainPanel.add(formPanel, BorderLayout.CENTER);
        add(mainPanel);

        // Action Listeners
        loginButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                performLogin();
            }
        });

        signUpLinkButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                dispose();
                new SignUpFrame().setVisible(true);
            }
        });

        // Allow pressing Enter in password field to login
        passwordField.addActionListener(e -> performLogin());
    }

    private void performLogin() {
        String username = usernameField.getText().trim();
        String password = new String(passwordField.getPassword());

        if (username.isEmpty() || password.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Please enter both username and password.", "Validation Error", JOptionPane.ERROR_MESSAGE);
            return;
        }

        UserDAO userDAO = new UserDAO();
        User user = userDAO.authenticateUser(username, password);

        if (user != null) {
            JOptionPane.showMessageDialog(this, "Login Successful! Welcome, " + user.getUsername() + ".", "Success", JOptionPane.INFORMATION_MESSAGE);
            dispose(); // Close login window
            
            if ("ADMIN".equalsIgnoreCase(user.getRole())) {
                new AdminDashboard(user).setVisible(true);
            } else {
                new UserDashboard(user).setVisible(true);
            }
        } else {
            JOptionPane.showMessageDialog(this, "Invalid username or password. Please try again.", "Login Failed", JOptionPane.ERROR_MESSAGE);
        }
    }
}
