package common;

import utils.*;
import admin.*;
import leader.*;
import lecturer.*;
import student.*;

import javax.swing.*;
import java.awt.*;
import java.util.List;

public class LoginFrame extends JFrame {
    private JTextField userIdField;
    private JPasswordField passwordField;
    private JLabel errorLabel;

    // UI Constants for consistency
    private final Color PRIMARY_COLOR = new Color(52, 152, 219); // Blue
    private final Color TEXT_COLOR = new Color(44, 62, 80);      // Dark Grey
    private final Color BG_COLOR = Color.WHITE;

    public LoginFrame() {
        setTitle("Assessment Feedback System - Login");
        setSize(450, 400); // Slightly taller for better spacing
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        setResizable(false); // Fixed size window is better for login screens

        try {
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
        } catch (Exception e) {
            e.printStackTrace();
        }

        initComponents();
    }

    private void initComponents() {
        JPanel mainPanel = new JPanel();
        mainPanel.setLayout(new GridBagLayout()); // Use GridBag for the whole panel to center it perfectly
        mainPanel.setBackground(BG_COLOR);

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(10, 10, 10, 10); // Comfortable spacing
        gbc.fill = GridBagConstraints.HORIZONTAL;

        // --- 1. Title ---
        JLabel titleLabel = new JLabel("System Login");
        titleLabel.setFont(new Font("Arial", Font.BOLD, 24));
        titleLabel.setForeground(PRIMARY_COLOR);
        titleLabel.setHorizontalAlignment(SwingConstants.CENTER);
        
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.gridwidth = 2;
        mainPanel.add(titleLabel, gbc);

        // --- 2. User ID ---
        gbc.gridwidth = 1;
        gbc.gridy = 1;
        gbc.gridx = 0;
        JLabel userIdLabel = new JLabel("User ID:");
        userIdLabel.setFont(new Font("Arial", Font.BOLD, 14));
        userIdLabel.setForeground(TEXT_COLOR);
        mainPanel.add(userIdLabel, gbc);

        gbc.gridx = 1;
        userIdField = new JTextField(15);
        userIdField.setFont(new Font("Arial", Font.PLAIN, 14));
        userIdField.setPreferredSize(new Dimension(200, 30));
        mainPanel.add(userIdField, gbc);

        // --- 3. Password ---
        gbc.gridy = 2;
        gbc.gridx = 0;
        JLabel passwordLabel = new JLabel("Password:");
        passwordLabel.setFont(new Font("Arial", Font.BOLD, 14));
        passwordLabel.setForeground(TEXT_COLOR);
        mainPanel.add(passwordLabel, gbc);

        gbc.gridx = 1;
        passwordField = new JPasswordField(15);
        passwordField.setFont(new Font("Arial", Font.PLAIN, 14));
        passwordField.setPreferredSize(new Dimension(200, 30));
        mainPanel.add(passwordField, gbc);

        // --- 4. Login Button ---
        gbc.gridy = 3;
        gbc.gridx = 0;
        gbc.gridwidth = 2;
        gbc.insets = new Insets(20, 10, 10, 10); // Extra space before button

        JButton loginButton = new JButton("Login");
        loginButton.setFont(new Font("Arial", Font.BOLD, 14));
        loginButton.setBackground(PRIMARY_COLOR);
        loginButton.setForeground(Color.WHITE); // Fixed: Changed from RED to WHITE for readability
        loginButton.setFocusPainted(false);
        loginButton.setCursor(new Cursor(Cursor.HAND_CURSOR));
        
        // UI BUG FIX: Forces background color to render on Mac/Windows
        loginButton.setOpaque(true);
        loginButton.setBorderPainted(false);
        
        // Add hover effect
        loginButton.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseEntered(java.awt.event.MouseEvent evt) {
                loginButton.setBackground(PRIMARY_COLOR.darker());
            }
            public void mouseExited(java.awt.event.MouseEvent evt) {
                loginButton.setBackground(PRIMARY_COLOR);
            }
        });

        loginButton.addActionListener(e -> handleLogin());
        mainPanel.add(loginButton, gbc);

        // --- 5. Error Label ---
        gbc.gridy = 4;
        gbc.insets = new Insets(5, 10, 10, 10);
        errorLabel = new JLabel(" "); // Empty space holder to prevent jumping
        errorLabel.setForeground(new Color(231, 76, 60)); // Red for errors
        errorLabel.setFont(new Font("Arial", Font.PLAIN, 12));
        errorLabel.setHorizontalAlignment(SwingConstants.CENTER);
        mainPanel.add(errorLabel, gbc);

        // Allow pressing "Enter" on password field to login
        passwordField.addActionListener(e -> handleLogin());
        userIdField.addActionListener(e -> passwordField.requestFocus());

        add(mainPanel);
    }

    private void handleLogin() {
        String userId = userIdField.getText().trim();
        String rawPassword = new String(passwordField.getPassword());

        if (userId.isEmpty() || rawPassword.isEmpty()) {
            errorLabel.setText("Please enter both User ID and Password");
            return;
        }

        // HASH INPUT PASSWORD
        String hashedInput = Security.hashPassword(rawPassword);

        List<String[]> users = FileHandler.getAllRecords("users.txt");
        if (users == null) {
            errorLabel.setText("System Error: Cannot load users.");
            return;
        }

        User authenticatedUser = null;

        for (String[] userData : users) {
            // COMPARE HASHED PASSWORD INSTEAD OF RAW
            if (userData.length >= 9 && userData[0].equals(userId) && userData[1].equals(hashedInput)) {
                String role = userData[2];
                try {
                    switch (role) {
                        case "Admin":
                            authenticatedUser = new Admin(userData[0], userData[1], userData[2], 
                                userData[3], userData[4], userData[5], userData[6], userData[7], userData[8]);
                            break;
                        case "Leader":
                            authenticatedUser = new AcademicLeader(userData[0], userData[1], userData[2], 
                                userData[3], userData[4], userData[5], userData[6], userData[7], userData[8]);
                            break;
                        case "Lecturer":
                            String supervisorId = userData.length > 9 ? userData[9] : "";
                            Lecturer lecturer = new Lecturer(userData[0], userData[1], userData[2], 
                                userData[3], userData[4], userData[5], userData[6], userData[7], userData[8]);
                            lecturer.setSupervisorId(supervisorId);
                            authenticatedUser = lecturer;
                            break;
                        case "Student":
                            authenticatedUser = new Student(userData[0], userData[1], userData[2], 
                                userData[3], userData[4], userData[5], userData[6], userData[7], userData[8]);
                            break;
                    }
                } catch (Exception e) {
                    errorLabel.setText("Error loading user profile.");
                    e.printStackTrace();
                    return;
                }
                break;
            }
        }

        if (authenticatedUser != null) {
            Session.getInstance().setCurrentUser(authenticatedUser);
            // LOG THE LOGIN ACTION
            Logger.log(userId, "User logged in");
            
            openDashboard(authenticatedUser.getRole());
            dispose();
        } else {
            errorLabel.setText("Invalid User ID or Password");
        }
    }

    private void openDashboard(String role) {
        SwingUtilities.invokeLater(() -> {
            switch (role) {
                case "Admin":
                    new AdminDashboard().setVisible(true);
                    break;
                case "Leader":
                    new AcademicLeaderDashboard().setVisible(true);
                    break;
                case "Lecturer":
                    new LecturerDashboard().setVisible(true);
                    break;
                case "Student":
                    new StudentDashboard().setVisible(true);
                    break;
                default:
                    JOptionPane.showMessageDialog(this, "Unknown role: " + role, "Login Error", JOptionPane.ERROR_MESSAGE);
            }
        });
    }
}