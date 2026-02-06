import javax.swing.*;
import java.awt.*;
import java.awt.event.*;

/**
 * LoginFrame - Authentication screen
 * Authenticates users against users.txt and routes to appropriate dashboard
 */
public class LoginFrame extends JFrame {
    
    private JTextField usernameField;
    private JPasswordField passwordField;
    private JButton loginButton;
    private JLabel statusLabel;
    
    private static final String USERS_FILE = "users.txt";
    
    public LoginFrame() {
        initializeUI();
    }
    
    private void initializeUI() {
        setTitle("Assessment Feedback System - Login");
        setSize(450, 350);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        setResizable(false);
        
        // Main panel with padding
        JPanel mainPanel = new JPanel();
        mainPanel.setLayout(new BorderLayout(10, 10));
        mainPanel.setBorder(BorderFactory.createEmptyBorder(20, 40, 20, 40));
        mainPanel.setBackground(new Color(245, 245, 245));
        
        // Header
        JPanel headerPanel = new JPanel();
        headerPanel.setBackground(new Color(41, 128, 185));
        headerPanel.setPreferredSize(new Dimension(450, 80));
        
        JLabel titleLabel = new JLabel("Assessment Feedback System");
        titleLabel.setFont(new Font("Arial", Font.BOLD, 20));
        titleLabel.setForeground(Color.WHITE);
        headerPanel.add(titleLabel);
        
        // Form panel
        JPanel formPanel = new JPanel();
        formPanel.setLayout(new GridBagLayout());
        formPanel.setBackground(Color.WHITE);
        formPanel.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(new Color(200, 200, 200)),
            BorderFactory.createEmptyBorder(30, 30, 30, 30)
        ));
        
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.insets = new Insets(5, 5, 5, 5);
        
        // Username
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.weightx = 0.3;
        JLabel userLabel = new JLabel("Username:");
        userLabel.setFont(new Font("Arial", Font.PLAIN, 14));
        formPanel.add(userLabel, gbc);
        
        gbc.gridx = 1;
        gbc.weightx = 0.7;
        usernameField = new JTextField(20);
        usernameField.setFont(new Font("Arial", Font.PLAIN, 14));
        usernameField.setPreferredSize(new Dimension(200, 30));
        formPanel.add(usernameField, gbc);
        
        // Password
        gbc.gridx = 0;
        gbc.gridy = 1;
        gbc.weightx = 0.3;
        JLabel passLabel = new JLabel("Password:");
        passLabel.setFont(new Font("Arial", Font.PLAIN, 14));
        formPanel.add(passLabel, gbc);
        
        gbc.gridx = 1;
        gbc.weightx = 0.7;
        passwordField = new JPasswordField(20);
        passwordField.setFont(new Font("Arial", Font.PLAIN, 14));
        passwordField.setPreferredSize(new Dimension(200, 30));
        formPanel.add(passwordField, gbc);
        
        // Login button
        gbc.gridx = 0;
        gbc.gridy = 2;
        gbc.gridwidth = 2;
        gbc.insets = new Insets(20, 5, 5, 5);
        loginButton = new JButton("Login");
        loginButton.setFont(new Font("Arial", Font.BOLD, 14));
        loginButton.setBackground(new Color(41, 128, 185));
        loginButton.setForeground(Color.RED);
        loginButton.setFocusPainted(false);
        loginButton.setPreferredSize(new Dimension(150, 35));
        loginButton.addActionListener(e -> performLogin());
        formPanel.add(loginButton, gbc);
        
        // Status label
        gbc.gridy = 3;
        gbc.insets = new Insets(10, 5, 5, 5);
        statusLabel = new JLabel(" ");
        statusLabel.setFont(new Font("Arial", Font.PLAIN, 12));
        statusLabel.setForeground(Color.RED);
        statusLabel.setHorizontalAlignment(SwingConstants.CENTER);
        formPanel.add(statusLabel, gbc);
        
        // Add enter key listener
        passwordField.addActionListener(e -> performLogin());
        
        // Assembly
        mainPanel.add(headerPanel, BorderLayout.NORTH);
        mainPanel.add(formPanel, BorderLayout.CENTER);
        
        add(mainPanel);
    }
    
    /**
     * Performs login authentication
     */
    private void performLogin() {
        String username = usernameField.getText().trim();
        String password = new String(passwordField.getPassword());
        
        if (username.isEmpty() || password.isEmpty()) {
            statusLabel.setText("Please enter both username and password");
            return;
        }
        
        User user = authenticateUser(username, password);
        
        if (user != null) {
            // Set session
            Session.getInstance().setCurrentUser(user);
            
            // Route to appropriate dashboard
            routeToDashboard(user);
            
            // Clear fields
            usernameField.setText("");
            passwordField.setText("");
            statusLabel.setText(" ");
            
        } else {
            statusLabel.setText("Invalid username or password");
            passwordField.setText("");
        }
    }
    
    /**
     * Authenticates user against users.txt
     * @param username Username
     * @param password Password
     * @return User object if authenticated, null otherwise
     */
    private User authenticateUser(String username, String password) {
        java.util.List<String[]> users = FileHandler.getAllRecords(USERS_FILE);
        
        for (String[] userData : users) {
            // Schema: ID|Username|Password|Role|Name|Gender|Email|Phone|Age|SupervisorID
            if (userData.length >= 10) {
                String fileUsername = userData[1];
                String filePassword = userData[2];
                String role = userData[3];
                
                if (fileUsername.equals(username) && filePassword.equals(password)) {
                    // Create appropriate user object based on role
                    return createUserFromData(userData, role);
                }
            }
        }
        
        return null;
    }
    
    /**
     * Creates User object from file data based on role
     * @param data User data array
     * @param role User role
     * @return Appropriate User subclass instance
     */
    private User createUserFromData(String[] data, String role) {
        switch (role) {
            case "Admin":
                return Admin.fromFileFormat(data);
            case "AcademicLeader":
                return AcademicLeader.fromFileFormat(data);
            case "Lecturer":
                return Lecturer.fromFileFormat(data);
            case "Student":
                return Student.fromFileFormat(data);
            default:
                return null;
        }
    }
    
    /**
     * Routes user to appropriate dashboard based on role
     * @param user Authenticated user
     */
    private void routeToDashboard(User user) {
        String role = user.getRole();
        
        switch (role) {
            case "Admin":
                new AdminDashboard().setVisible(true);
                break;
            case "AcademicLeader":
                new AcademicLeaderDashboard().setVisible(true);
                break;
            case "Lecturer":
                new LecturerDashboard().setVisible(true);
                break;
            case "Student":
                new StudentDashboard().setVisible(true);
                break;
            default:
                JOptionPane.showMessageDialog(this, 
                    "Unknown role: " + role, 
                    "Error", 
                    JOptionPane.ERROR_MESSAGE);
        }
    }
    
    /**
     * Makes the login frame visible (used when logging out)
     */
    public void showLogin() {
        usernameField.setText("");
        passwordField.setText("");
        statusLabel.setText(" ");
        setVisible(true);
    }
}
