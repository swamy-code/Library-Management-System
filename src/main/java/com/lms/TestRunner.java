package com.lms;

import com.lms.dao.UserDAO;
import com.lms.model.User;

public class TestRunner {
    public static void main(String[] args) {
        System.out.println("--- Starting UserDAO Verification Test ---");
        UserDAO userDAO = new UserDAO();

        // Check default admin
        boolean adminExists = userDAO.isUsernameTaken("admin");
        System.out.println("Default admin exists: " + adminExists);

        // Register new user
        String testUser = "testuser_" + System.currentTimeMillis();
        boolean registered = userDAO.registerUser(testUser, "secret123", "USER");
        System.out.println("Registered new user '" + testUser + "': " + registered);

        // Verify taken check
        boolean nowTaken = userDAO.isUsernameTaken(testUser);
        System.out.println("Username '" + testUser + "' is taken check: " + nowTaken);

        // Authenticate
        User authUser = userDAO.authenticateUser(testUser, "secret123");
        if (authUser != null) {
            System.out.println("Authenticated successfully! Username: " + authUser.getUsername() + ", Role: "
                    + authUser.getRole());
        } else {
            System.err.println("Authentication failed for newly registered user!");
        }

        System.out.println("--- Test Completed ---");
    }
}
