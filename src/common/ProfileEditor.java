package common;

import utils.*;
import javax.swing.*;
import java.awt.*;

public class ProfileEditor extends JDialog {
    private JTextField idField;
    private JTextField nameField;
    private JPasswordField passwordField;
    private JTextField emailField;
    private JTextField phoneField;
    private User user;

    // UI Constants
    private final Color PRIMARY_COLOR = new Color(52, 152, 219); // Blue
    private final Color CANCEL_COLOR = new Color(149, 165, 166); // Grey
    private final Color BG_COLOR = Color.WHITE;
    private final Color READONLY_BG = new Color(245, 245, 245); // Light Grey

    public ProfileEditor(JFrame parent, User user) {
        super(parent, "Edit Profile", true);
        this.user = user;
        setSize(450, 400);
        setLocationRelativeTo(parent);
        setResizable(false);
        initComponents();
    }

    private void initComponents() {
        JPanel mainPanel = new JPanel();
        mainPanel.setLayout(new BorderLayout());
        mainPanel.setBackground(BG_COLOR);

        // --- Header ---
        JPanel headerPanel = new JPanel();
        headerPanel.setBackground(BG_COLOR);
        headerPanel.setBorder(BorderFactory.createEmptyBorder(15, 0, 0, 0));
        JLabel titleLabel = new JLabel("Update Your Details");
        titleLabel.setFont(new Font("Arial", Font.BOLD, 18));
        titleLabel.setForeground(PRIMARY_COLOR);
        headerPanel.add(titleLabel);
        mainPanel.add(headerPanel, BorderLayout.NORTH);

        // --- Form Panel ---
        JPanel formPanel = new JPanel();
        formPanel.setLayout(new GridBagLayout());
        formPanel.setBackground(BG_COLOR);
        formPanel.setBorder(BorderFactory.createEmptyBorder(10, 30, 20, 30));

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(8, 8, 8, 8);
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.anchor = GridBagConstraints.WEST;

        // 1. User ID (Read Only)
        addFormRow(formPanel, gbc, 0, "User ID:", createReadOnlyField(user.getId()));

        // 2. Full Name (Read Only)
        addFormRow(formPanel, gbc, 1, "Full Name:", createReadOnlyField(user.getFullName()));

        // 3. Password
        gbc.gridx = 0; gbc.gridy = 2; gbc.weightx = 0;
        formPanel.add(new JLabel("Password:"), gbc);
        gbc.gridx = 1; gbc.weightx = 1.0;
        passwordField = new JPasswordField(user.getPassword(), 20);
        formPanel.add(passwordField, gbc);

        // 4. Email
        gbc.gridx = 0; gbc.gridy = 3; gbc.weightx = 0;
        formPanel.add(new JLabel("Email:"), gbc);
        gbc.gridx = 1; gbc.weightx = 1.0;
        emailField = new JTextField(user.getEmail(), 20);
        formPanel.add(emailField, gbc);

        // 5. Phone
        gbc.gridx = 0; gbc.gridy = 4; gbc.weightx = 0;
        formPanel.add(new JLabel("Phone:"), gbc);
        gbc.gridx = 1; gbc.weightx = 1.0;
        phoneField = new JTextField(user.getPhone(), 20);
        formPanel.add(phoneField, gbc);

        mainPanel.add(formPanel, BorderLayout.CENTER);

        // --- Button Panel ---
        JPanel buttonPanel = new JPanel();
        buttonPanel.setBackground(BG_COLOR);
        buttonPanel.setBorder(BorderFactory.createEmptyBorder(0, 0, 20, 0));

        JButton saveButton = createStyledButton("Save Changes", PRIMARY_COLOR);
        saveButton.addActionListener(e -> saveProfile());

        JButton cancelButton = createStyledButton("Cancel", CANCEL_COLOR);
        cancelButton.addActionListener(e -> dispose());

        buttonPanel.add(saveButton);
        buttonPanel.add(cancelButton);

        mainPanel.add(buttonPanel, BorderLayout.SOUTH);
        add(mainPanel);
    }

    private JButton createStyledButton(String text, Color bg) {
        JButton btn = new JButton(text);
        btn.setBackground(bg);
        btn.setForeground(Color.WHITE);
        btn.setFocusPainted(false);
        btn.setFont(new Font("Arial", Font.BOLD, 12));
        // FIX FOR INVISIBLE BUTTONS
        btn.setOpaque(true);
        btn.setBorderPainted(false);
        btn.setBorder(BorderFactory.createEmptyBorder(8, 15, 8, 15));
        return btn;
    }

    private JTextField createReadOnlyField(String text) {
        JTextField field = new JTextField(text, 20);
        field.setEditable(false);
        field.setBackground(READONLY_BG);
        field.setForeground(Color.DARK_GRAY);
        field.setBorder(BorderFactory.createLineBorder(Color.LIGHT_GRAY));
        return field;
    }

    private void addFormRow(JPanel panel, GridBagConstraints gbc, int y, String labelText, JComponent field) {
        gbc.gridx = 0; gbc.gridy = y; gbc.weightx = 0;
        JLabel label = new JLabel(labelText);
        label.setFont(new Font("Arial", Font.BOLD, 12));
        panel.add(label, gbc);
        gbc.gridx = 1; gbc.weightx = 1.0;
        panel.add(field, gbc);
    }

    private void saveProfile() {
        String newPassword = new String(passwordField.getPassword());
        String newEmail = emailField.getText().trim();
        String newPhone = phoneField.getText().trim();

        if (newPassword.isEmpty() || newEmail.isEmpty() || newPhone.isEmpty()) {
            JOptionPane.showMessageDialog(this, "All fields must be filled", "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }

        // HASH NEW PASSWORD
        String hashedPassword = Security.hashPassword(newPassword);
        
        // Update user object with HASH
        user.setPassword(hashedPassword);
        user.setEmail(newEmail);
        user.setPhone(newPhone);

        boolean success = FileHandler.updateLine("users.txt", 0, user.getId(), user.toFileFormat());

        if (success) {
            Session.getInstance().setCurrentUser(user);
            // LOG THE ACTION
            Logger.log(user.getId(), "Updated profile details");
            
            JOptionPane.showMessageDialog(this, "Profile updated successfully!", "Success", JOptionPane.INFORMATION_MESSAGE);
            dispose();
        } else {
            JOptionPane.showMessageDialog(this, "Failed to update profile", "Error", JOptionPane.ERROR_MESSAGE);
        }
    }
}