import javax.swing.*;
import javax.swing.table.*;
import java.awt.*;
import java.awt.event.*;
import java.util.List;
import java.util.ArrayList;

/**
 * AdminDashboard - Admin interface
 * Features:
 * 1. Add/Edit/Delete Users (Staff and Students)
 * 2. Create Classes for Modules
 * 3. Edit Grading System
 */
public class AdminDashboard extends JFrame {
    
    private JTabbedPane tabbedPane;
    private JTable userTable;
    private DefaultTableModel userTableModel;
    private JTable gradingTable;
    private DefaultTableModel gradingTableModel;
    private JTable classTable;
    private DefaultTableModel classTableModel;
    
    private static final String USERS_FILE = "users.txt";
    private static final String GRADING_FILE = "grading.txt";
    private static final String CLASSES_FILE = "classes.txt";
    private static final String MODULES_FILE = "modules.txt";
    
    public AdminDashboard() {
        initializeUI();
        loadUserData();
        loadGradingData();
        loadClassData();
    }
    
    private void initializeUI() {
        setTitle("Admin Dashboard - Assessment Feedback System");
        setSize(1000, 700);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        
        // Main panel
        JPanel mainPanel = new JPanel(new BorderLayout());
        
        // Header
        JPanel headerPanel = createHeaderPanel();
        mainPanel.add(headerPanel, BorderLayout.NORTH);
        
        // Tabbed pane
        tabbedPane = new JTabbedPane();
        tabbedPane.setFont(new Font("Arial", Font.PLAIN, 14));
        
        // Tabs
        tabbedPane.addTab("Manage Users", createUserManagementPanel());
        tabbedPane.addTab("Manage Classes", createClassManagementPanel());
        tabbedPane.addTab("Grading System", createGradingSystemPanel());
        
        mainPanel.add(tabbedPane, BorderLayout.CENTER);
        
        add(mainPanel);
    }
    
    /**
     * Creates header panel with welcome message and logout
     */
    private JPanel createHeaderPanel() {
        JPanel headerPanel = new JPanel(new BorderLayout());
        headerPanel.setBackground(new Color(41, 128, 185));
        headerPanel.setPreferredSize(new Dimension(1000, 60));
        headerPanel.setBorder(BorderFactory.createEmptyBorder(10, 20, 10, 20));
        
        User currentUser = Session.getInstance().getCurrentUser();
        JLabel welcomeLabel = new JLabel("Welcome, " + currentUser.getFullName() + " (Administrator)");
        welcomeLabel.setFont(new Font("Arial", Font.BOLD, 18));
        welcomeLabel.setForeground(Color.WHITE);
        
        JButton logoutButton = new JButton("Logout");
        logoutButton.setFont(new Font("Arial", Font.PLAIN, 14));
        logoutButton.setBackground(new Color(231, 76, 60));
        logoutButton.setForeground(Color.WHITE);
        logoutButton.setFocusPainted(false);
        logoutButton.addActionListener(e -> logout());
        
        headerPanel.add(welcomeLabel, BorderLayout.WEST);
        headerPanel.add(logoutButton, BorderLayout.EAST);
        
        return headerPanel;
    }
    
    /**
     * Creates user management panel
     */
    private JPanel createUserManagementPanel() {
        JPanel panel = new JPanel(new BorderLayout(10, 10));
        panel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        
        // Table
        String[] columns = {"ID", "Username", "Role", "Full Name", "Gender", "Email", "Phone", "Age", "Supervisor ID"};
        userTableModel = new DefaultTableModel(columns, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
        userTable = new JTable(userTableModel);
        userTable.setFont(new Font("Arial", Font.PLAIN, 12));
        userTable.setRowHeight(25);
        userTable.getTableHeader().setFont(new Font("Arial", Font.BOLD, 12));
        userTable.getTableHeader().setBackground(new Color(52, 152, 219));
        userTable.getTableHeader().setForeground(Color.WHITE);
        
        JScrollPane scrollPane = new JScrollPane(userTable);
        
        // Button panel
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 10));
        
        JButton addButton = createStyledButton("Add User", new Color(46, 204, 113));
        JButton editButton = createStyledButton("Edit User", new Color(241, 196, 15));
        JButton deleteButton = createStyledButton("Delete User", new Color(231, 76, 60));
        JButton refreshButton = createStyledButton("Refresh", new Color(52, 152, 219));
        
        addButton.addActionListener(e -> showAddUserDialog());
        editButton.addActionListener(e -> showEditUserDialog());
        deleteButton.addActionListener(e -> deleteUser());
        refreshButton.addActionListener(e -> loadUserData());
        
        buttonPanel.add(addButton);
        buttonPanel.add(editButton);
        buttonPanel.add(deleteButton);
        buttonPanel.add(refreshButton);
        
        panel.add(scrollPane, BorderLayout.CENTER);
        panel.add(buttonPanel, BorderLayout.SOUTH);
        
        return panel;
    }
    
    /**
     * Creates class management panel
     */
    private JPanel createClassManagementPanel() {
        JPanel panel = new JPanel(new BorderLayout(10, 10));
        panel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        
        // Table
        String[] columns = {"Class ID", "Module Code", "Module Name", "Intake Code"};
        classTableModel = new DefaultTableModel(columns, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
        classTable = new JTable(classTableModel);
        classTable.setFont(new Font("Arial", Font.PLAIN, 12));
        classTable.setRowHeight(25);
        classTable.getTableHeader().setFont(new Font("Arial", Font.BOLD, 12));
        classTable.getTableHeader().setBackground(new Color(52, 152, 219));
        classTable.getTableHeader().setForeground(Color.WHITE);
        
        JScrollPane scrollPane = new JScrollPane(classTable);
        
        // Button panel
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 10));
        
        JButton addButton = createStyledButton("Add Class", new Color(46, 204, 113));
        JButton deleteButton = createStyledButton("Delete Class", new Color(231, 76, 60));
        JButton refreshButton = createStyledButton("Refresh", new Color(52, 152, 219));
        
        addButton.addActionListener(e -> showAddClassDialog());
        deleteButton.addActionListener(e -> deleteClass());
        refreshButton.addActionListener(e -> loadClassData());
        
        buttonPanel.add(addButton);
        buttonPanel.add(deleteButton);
        buttonPanel.add(refreshButton);
        
        panel.add(scrollPane, BorderLayout.CENTER);
        panel.add(buttonPanel, BorderLayout.SOUTH);
        
        return panel;
    }
    
    /**
     * Creates grading system panel
     */
    private JPanel createGradingSystemPanel() {
        JPanel panel = new JPanel(new BorderLayout(10, 10));
        panel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        
        // Table
        String[] columns = {"Grade", "Min Marks", "Max Marks"};
        gradingTableModel = new DefaultTableModel(columns, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
        gradingTable = new JTable(gradingTableModel);
        gradingTable.setFont(new Font("Arial", Font.PLAIN, 12));
        gradingTable.setRowHeight(25);
        gradingTable.getTableHeader().setFont(new Font("Arial", Font.BOLD, 12));
        gradingTable.getTableHeader().setBackground(new Color(52, 152, 219));
        gradingTable.getTableHeader().setForeground(Color.WHITE);
        
        JScrollPane scrollPane = new JScrollPane(gradingTable);
        
        // Button panel
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 10));
        
        JButton addButton = createStyledButton("Add Grade", new Color(46, 204, 113));
        JButton editButton = createStyledButton("Edit Grade", new Color(241, 196, 15));
        JButton deleteButton = createStyledButton("Delete Grade", new Color(231, 76, 60));
        JButton refreshButton = createStyledButton("Refresh", new Color(52, 152, 219));
        
        addButton.addActionListener(e -> showAddGradeDialog());
        editButton.addActionListener(e -> showEditGradeDialog());
        deleteButton.addActionListener(e -> deleteGrade());
        refreshButton.addActionListener(e -> loadGradingData());
        
        buttonPanel.add(addButton);
        buttonPanel.add(editButton);
        buttonPanel.add(deleteButton);
        buttonPanel.add(refreshButton);
        
        panel.add(scrollPane, BorderLayout.CENTER);
        panel.add(buttonPanel, BorderLayout.SOUTH);
        
        return panel;
    }
    
    /**
     * Creates styled button
     */
    private JButton createStyledButton(String text, Color color) {
        JButton button = new JButton(text);
        button.setFont(new Font("Arial", Font.PLAIN, 13));
        button.setBackground(color);
        button.setForeground(Color.WHITE);
        button.setFocusPainted(false);
        button.setPreferredSize(new Dimension(120, 35));
        return button;
    }
    
    /**
     * Loads user data into table
     */
    private void loadUserData() {
        userTableModel.setRowCount(0);
        List<String[]> users = FileHandler.getAllRecords(USERS_FILE);
        
        for (String[] user : users) {
            if (user.length >= 10) {
                // ID|Username|Password|Role|Name|Gender|Email|Phone|Age|SupervisorID
                Object[] row = {
                    user[0],  // ID
                    user[1],  // Username
                    user[3],  // Role
                    user[4],  // Full Name
                    user[5],  // Gender
                    user[6],  // Email
                    user[7],  // Phone
                    user[8],  // Age
                    user[9]   // Supervisor ID
                };
                userTableModel.addRow(row);
            }
        }
    }
    
    /**
     * Loads grading data into table
     */
    private void loadGradingData() {
        gradingTableModel.setRowCount(0);
        List<String[]> grades = FileHandler.getAllRecords(GRADING_FILE);
        
        for (String[] grade : grades) {
            if (grade.length >= 3) {
                gradingTableModel.addRow(grade);
            }
        }
    }
    
    /**
     * Loads class data into table
     */
    private void loadClassData() {
        classTableModel.setRowCount(0);
        List<String[]> classes = FileHandler.getAllRecords(CLASSES_FILE);
        List<String[]> modules = FileHandler.getAllRecords(MODULES_FILE);
        
        for (String[] classData : classes) {
            if (classData.length >= 3) {
                // Find module name
                String moduleName = "";
                for (String[] module : modules) {
                    if (module.length >= 2 && module[0].equals(classData[1])) {
                        moduleName = module[1];
                        break;
                    }
                }
                
                Object[] row = {
                    classData[0],  // Class ID
                    classData[1],  // Module Code
                    moduleName,    // Module Name
                    classData[2]   // Intake Code
                };
                classTableModel.addRow(row);
            }
        }
    }
    
    /**
     * Shows dialog to add new user
     */
    private void showAddUserDialog() {
        JDialog dialog = new JDialog(this, "Add New User", true);
        dialog.setSize(500, 550);
        dialog.setLocationRelativeTo(this);
        
        JPanel panel = new JPanel(new GridBagLayout());
        panel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
        
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.insets = new Insets(5, 5, 5, 5);
        
        // Fields
        JTextField idField = new JTextField(20);
        JTextField usernameField = new JTextField(20);
        JPasswordField passwordField = new JPasswordField(20);
        JTextField nameField = new JTextField(20);
        JComboBox<String> roleCombo = new JComboBox<>(new String[]{"Admin", "AcademicLeader", "Lecturer", "Student"});
        JComboBox<String> genderCombo = new JComboBox<>(new String[]{"Male", "Female", "Other"});
        JTextField emailField = new JTextField(20);
        JTextField phoneField = new JTextField(20);
        JSpinner ageSpinner = new JSpinner(new SpinnerNumberModel(20, 18, 100, 1));
        JComboBox<String> supervisorCombo = new JComboBox<>();
        
        // Populate supervisor combo with Academic Leaders
        supervisorCombo.addItem("N/A");
        List<String[]> users = FileHandler.getAllRecords(USERS_FILE);
        for (String[] user : users) {
            if (user.length >= 5 && user[3].equals("AcademicLeader")) {
                supervisorCombo.addItem(user[0] + " - " + user[4]);
            }
        }
        
        // Role change listener to show/hide supervisor field
        JLabel supervisorLabel = new JLabel("Supervisor:");
        roleCombo.addActionListener(e -> {
            String role = (String) roleCombo.getSelectedItem();
            boolean showSupervisor = "Lecturer".equals(role);
            supervisorLabel.setVisible(showSupervisor);
            supervisorCombo.setVisible(showSupervisor);
        });
        
        // Add components
        int row = 0;
        addFormField(panel, gbc, row++, "User ID:", idField);
        addFormField(panel, gbc, row++, "Username:", usernameField);
        addFormField(panel, gbc, row++, "Password:", passwordField);
        addFormField(panel, gbc, row++, "Full Name:", nameField);
        addFormField(panel, gbc, row++, "Role:", roleCombo);
        addFormField(panel, gbc, row++, "Gender:", genderCombo);
        addFormField(panel, gbc, row++, "Email:", emailField);
        addFormField(panel, gbc, row++, "Phone:", phoneField);
        addFormField(panel, gbc, row++, "Age:", ageSpinner);
        addFormField(panel, gbc, row++, "Supervisor:", supervisorCombo);
        
        supervisorLabel.setVisible(false);
        supervisorCombo.setVisible(false);
        
        // Buttons
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 10, 10));
        JButton saveButton = new JButton("Save");
        JButton cancelButton = new JButton("Cancel");
        
        saveButton.addActionListener(e -> {
            String id = idField.getText().trim();
            String username = usernameField.getText().trim();
            String password = new String(passwordField.getPassword());
            String name = nameField.getText().trim();
            String role = (String) roleCombo.getSelectedItem();
            String gender = (String) genderCombo.getSelectedItem();
            String email = emailField.getText().trim();
            String phone = phoneField.getText().trim();
            int age = (Integer) ageSpinner.getValue();
            
            String supervisorId = "N/A";
            if ("Lecturer".equals(role) && !supervisorCombo.getSelectedItem().equals("N/A")) {
                supervisorId = supervisorCombo.getSelectedItem().toString().split(" - ")[0];
            }
            
            if (id.isEmpty() || username.isEmpty() || password.isEmpty() || name.isEmpty()) {
                JOptionPane.showMessageDialog(dialog, "Please fill all required fields", "Error", JOptionPane.ERROR_MESSAGE);
                return;
            }
            
            // Check if ID or username already exists
            String[] existing = FileHandler.searchLine(USERS_FILE, 0, id);
            if (existing != null) {
                JOptionPane.showMessageDialog(dialog, "User ID already exists", "Error", JOptionPane.ERROR_MESSAGE);
                return;
            }
            
            FileHandler.appendLine(USERS_FILE, id, username, password, role, name, gender, email, phone, String.valueOf(age), supervisorId);
            JOptionPane.showMessageDialog(dialog, "User added successfully!", "Success", JOptionPane.INFORMATION_MESSAGE);
            loadUserData();
            dialog.dispose();
        });
        
        cancelButton.addActionListener(e -> dialog.dispose());
        
        buttonPanel.add(saveButton);
        buttonPanel.add(cancelButton);
        
        gbc.gridx = 0;
        gbc.gridy = row;
        gbc.gridwidth = 2;
        panel.add(buttonPanel, gbc);
        
        dialog.add(panel);
        dialog.setVisible(true);
    }
    
    /**
     * Shows dialog to edit user
     */
    private void showEditUserDialog() {
        int selectedRow = userTable.getSelectedRow();
        if (selectedRow == -1) {
            JOptionPane.showMessageDialog(this, "Please select a user to edit", "Warning", JOptionPane.WARNING_MESSAGE);
            return;
        }
        
        String userId = userTable.getValueAt(selectedRow, 0).toString();
        String[] userData = FileHandler.searchLine(USERS_FILE, 0, userId);
        
        if (userData == null) {
            JOptionPane.showMessageDialog(this, "User not found", "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }
        
        JDialog dialog = new JDialog(this, "Edit User", true);
        dialog.setSize(500, 550);
        dialog.setLocationRelativeTo(this);
        
        JPanel panel = new JPanel(new GridBagLayout());
        panel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
        
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.insets = new Insets(5, 5, 5, 5);
        
        // Pre-populate fields
        JTextField idField = new JTextField(userData[0], 20);
        idField.setEditable(false);
        JTextField usernameField = new JTextField(userData[1], 20);
        JPasswordField passwordField = new JPasswordField(userData[2], 20);
        JTextField nameField = new JTextField(userData[4], 20);
        JComboBox<String> roleCombo = new JComboBox<>(new String[]{"Admin", "AcademicLeader", "Lecturer", "Student"});
        roleCombo.setSelectedItem(userData[3]);
        JComboBox<String> genderCombo = new JComboBox<>(new String[]{"Male", "Female", "Other"});
        genderCombo.setSelectedItem(userData[5]);
        JTextField emailField = new JTextField(userData[6], 20);
        JTextField phoneField = new JTextField(userData[7], 20);
        JSpinner ageSpinner = new JSpinner(new SpinnerNumberModel(Integer.parseInt(userData[8]), 18, 100, 1));
        JComboBox<String> supervisorCombo = new JComboBox<>();
        
        supervisorCombo.addItem("N/A");
        List<String[]> users = FileHandler.getAllRecords(USERS_FILE);
        for (String[] user : users) {
            if (user.length >= 5 && user[3].equals("AcademicLeader")) {
                supervisorCombo.addItem(user[0] + " - " + user[4]);
            }
        }
        supervisorCombo.setSelectedItem(userData[9]);
        
        JLabel supervisorLabel = new JLabel("Supervisor:");
        roleCombo.addActionListener(e -> {
            String role = (String) roleCombo.getSelectedItem();
            boolean showSupervisor = "Lecturer".equals(role);
            supervisorLabel.setVisible(showSupervisor);
            supervisorCombo.setVisible(showSupervisor);
        });
        
        int row = 0;
        addFormField(panel, gbc, row++, "User ID:", idField);
        addFormField(panel, gbc, row++, "Username:", usernameField);
        addFormField(panel, gbc, row++, "Password:", passwordField);
        addFormField(panel, gbc, row++, "Full Name:", nameField);
        addFormField(panel, gbc, row++, "Role:", roleCombo);
        addFormField(panel, gbc, row++, "Gender:", genderCombo);
        addFormField(panel, gbc, row++, "Email:", emailField);
        addFormField(panel, gbc, row++, "Phone:", phoneField);
        addFormField(panel, gbc, row++, "Age:", ageSpinner);
        addFormField(panel, gbc, row++, "Supervisor:", supervisorCombo);
        
        supervisorLabel.setVisible("Lecturer".equals(userData[3]));
        supervisorCombo.setVisible("Lecturer".equals(userData[3]));
        
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 10, 10));
        JButton saveButton = new JButton("Save");
        JButton cancelButton = new JButton("Cancel");
        
        saveButton.addActionListener(e -> {
            String username = usernameField.getText().trim();
            String password = new String(passwordField.getPassword());
            String name = nameField.getText().trim();
            String role = (String) roleCombo.getSelectedItem();
            String gender = (String) genderCombo.getSelectedItem();
            String email = emailField.getText().trim();
            String phone = phoneField.getText().trim();
            int age = (Integer) ageSpinner.getValue();
            
            String supervisorId = "N/A";
            if ("Lecturer".equals(role) && !supervisorCombo.getSelectedItem().equals("N/A")) {
                supervisorId = supervisorCombo.getSelectedItem().toString().split(" - ")[0];
            }
            
            FileHandler.updateLine(USERS_FILE, 0, userId, userId, username, password, role, name, gender, email, phone, String.valueOf(age), supervisorId);
            JOptionPane.showMessageDialog(dialog, "User updated successfully!", "Success", JOptionPane.INFORMATION_MESSAGE);
            loadUserData();
            dialog.dispose();
        });
        
        cancelButton.addActionListener(e -> dialog.dispose());
        
        buttonPanel.add(saveButton);
        buttonPanel.add(cancelButton);
        
        gbc.gridx = 0;
        gbc.gridy = row;
        gbc.gridwidth = 2;
        panel.add(buttonPanel, gbc);
        
        dialog.add(panel);
        dialog.setVisible(true);
    }
    
    /**
     * Deletes selected user
     */
    private void deleteUser() {
        int selectedRow = userTable.getSelectedRow();
        if (selectedRow == -1) {
            JOptionPane.showMessageDialog(this, "Please select a user to delete", "Warning", JOptionPane.WARNING_MESSAGE);
            return;
        }
        
        String userId = userTable.getValueAt(selectedRow, 0).toString();
        
        int confirm = JOptionPane.showConfirmDialog(this, 
            "Are you sure you want to delete this user?", 
            "Confirm Delete", 
            JOptionPane.YES_NO_OPTION);
        
        if (confirm == JOptionPane.YES_OPTION) {
            FileHandler.deleteLine(USERS_FILE, 0, userId);
            JOptionPane.showMessageDialog(this, "User deleted successfully!", "Success", JOptionPane.INFORMATION_MESSAGE);
            loadUserData();
        }
    }
    
    /**
     * Shows dialog to add new grade
     */
    private void showAddGradeDialog() {
        JDialog dialog = new JDialog(this, "Add Grade", true);
        dialog.setSize(400, 250);
        dialog.setLocationRelativeTo(this);
        
        JPanel panel = new JPanel(new GridBagLayout());
        panel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
        
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.insets = new Insets(5, 5, 5, 5);
        
        JTextField gradeField = new JTextField(20);
        JSpinner minSpinner = new JSpinner(new SpinnerNumberModel(0, 0, 100, 1));
        JSpinner maxSpinner = new JSpinner(new SpinnerNumberModel(100, 0, 100, 1));
        
        addFormField(panel, gbc, 0, "Grade:", gradeField);
        addFormField(panel, gbc, 1, "Min Marks:", minSpinner);
        addFormField(panel, gbc, 2, "Max Marks:", maxSpinner);
        
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 10, 10));
        JButton saveButton = new JButton("Save");
        JButton cancelButton = new JButton("Cancel");
        
        saveButton.addActionListener(e -> {
            String grade = gradeField.getText().trim();
            int min = (Integer) minSpinner.getValue();
            int max = (Integer) maxSpinner.getValue();
            
            if (grade.isEmpty()) {
                JOptionPane.showMessageDialog(dialog, "Please enter grade", "Error", JOptionPane.ERROR_MESSAGE);
                return;
            }
            
            if (min > max) {
                JOptionPane.showMessageDialog(dialog, "Min marks cannot be greater than max marks", "Error", JOptionPane.ERROR_MESSAGE);
                return;
            }
            
            FileHandler.appendLine(GRADING_FILE, grade, String.valueOf(min), String.valueOf(max));
            JOptionPane.showMessageDialog(dialog, "Grade added successfully!", "Success", JOptionPane.INFORMATION_MESSAGE);
            loadGradingData();
            dialog.dispose();
        });
        
        cancelButton.addActionListener(e -> dialog.dispose());
        
        buttonPanel.add(saveButton);
        buttonPanel.add(cancelButton);
        
        gbc.gridx = 0;
        gbc.gridy = 3;
        gbc.gridwidth = 2;
        panel.add(buttonPanel, gbc);
        
        dialog.add(panel);
        dialog.setVisible(true);
    }
    
    /**
     * Shows dialog to edit grade
     */
    private void showEditGradeDialog() {
        int selectedRow = gradingTable.getSelectedRow();
        if (selectedRow == -1) {
            JOptionPane.showMessageDialog(this, "Please select a grade to edit", "Warning", JOptionPane.WARNING_MESSAGE);
            return;
        }
        
        String grade = gradingTable.getValueAt(selectedRow, 0).toString();
        String[] gradeData = FileHandler.searchLine(GRADING_FILE, 0, grade);
        
        if (gradeData == null) {
            JOptionPane.showMessageDialog(this, "Grade not found", "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }
        
        JDialog dialog = new JDialog(this, "Edit Grade", true);
        dialog.setSize(400, 250);
        dialog.setLocationRelativeTo(this);
        
        JPanel panel = new JPanel(new GridBagLayout());
        panel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
        
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.insets = new Insets(5, 5, 5, 5);
        
        JTextField gradeField = new JTextField(gradeData[0], 20);
        gradeField.setEditable(false);
        JSpinner minSpinner = new JSpinner(new SpinnerNumberModel(Integer.parseInt(gradeData[1]), 0, 100, 1));
        JSpinner maxSpinner = new JSpinner(new SpinnerNumberModel(Integer.parseInt(gradeData[2]), 0, 100, 1));
        
        addFormField(panel, gbc, 0, "Grade:", gradeField);
        addFormField(panel, gbc, 1, "Min Marks:", minSpinner);
        addFormField(panel, gbc, 2, "Max Marks:", maxSpinner);
        
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 10, 10));
        JButton saveButton = new JButton("Save");
        JButton cancelButton = new JButton("Cancel");
        
        saveButton.addActionListener(e -> {
            int min = (Integer) minSpinner.getValue();
            int max = (Integer) maxSpinner.getValue();
            
            if (min > max) {
                JOptionPane.showMessageDialog(dialog, "Min marks cannot be greater than max marks", "Error", JOptionPane.ERROR_MESSAGE);
                return;
            }
            
            FileHandler.updateLine(GRADING_FILE, 0, grade, grade, String.valueOf(min), String.valueOf(max));
            JOptionPane.showMessageDialog(dialog, "Grade updated successfully!", "Success", JOptionPane.INFORMATION_MESSAGE);
            loadGradingData();
            dialog.dispose();
        });
        
        cancelButton.addActionListener(e -> dialog.dispose());
        
        buttonPanel.add(saveButton);
        buttonPanel.add(cancelButton);
        
        gbc.gridx = 0;
        gbc.gridy = 3;
        gbc.gridwidth = 2;
        panel.add(buttonPanel, gbc);
        
        dialog.add(panel);
        dialog.setVisible(true);
    }
    
    /**
     * Deletes selected grade
     */
    private void deleteGrade() {
        int selectedRow = gradingTable.getSelectedRow();
        if (selectedRow == -1) {
            JOptionPane.showMessageDialog(this, "Please select a grade to delete", "Warning", JOptionPane.WARNING_MESSAGE);
            return;
        }
        
        String grade = gradingTable.getValueAt(selectedRow, 0).toString();
        
        int confirm = JOptionPane.showConfirmDialog(this, 
            "Are you sure you want to delete this grade?", 
            "Confirm Delete", 
            JOptionPane.YES_NO_OPTION);
        
        if (confirm == JOptionPane.YES_OPTION) {
            FileHandler.deleteLine(GRADING_FILE, 0, grade);
            JOptionPane.showMessageDialog(this, "Grade deleted successfully!", "Success", JOptionPane.INFORMATION_MESSAGE);
            loadGradingData();
        }
    }
    
    /**
     * Shows dialog to add new class
     */
    private void showAddClassDialog() {
        JDialog dialog = new JDialog(this, "Add Class", true);
        dialog.setSize(450, 250);
        dialog.setLocationRelativeTo(this);
        
        JPanel panel = new JPanel(new GridBagLayout());
        panel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
        
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.insets = new Insets(5, 5, 5, 5);
        
        JTextField classIdField = new JTextField(20);
        JComboBox<String> moduleCombo = new JComboBox<>();
        JTextField intakeField = new JTextField(20);
        
        // Populate modules
        List<String[]> modules = FileHandler.getAllRecords(MODULES_FILE);
        for (String[] module : modules) {
            if (module.length >= 2) {
                moduleCombo.addItem(module[0] + " - " + module[1]);
            }
        }
        
        addFormField(panel, gbc, 0, "Class ID:", classIdField);
        addFormField(panel, gbc, 1, "Module:", moduleCombo);
        addFormField(panel, gbc, 2, "Intake Code:", intakeField);
        
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 10, 10));
        JButton saveButton = new JButton("Save");
        JButton cancelButton = new JButton("Cancel");
        
        saveButton.addActionListener(e -> {
            String classId = classIdField.getText().trim();
            String moduleCode = moduleCombo.getSelectedItem().toString().split(" - ")[0];
            String intake = intakeField.getText().trim();
            
            if (classId.isEmpty() || intake.isEmpty()) {
                JOptionPane.showMessageDialog(dialog, "Please fill all fields", "Error", JOptionPane.ERROR_MESSAGE);
                return;
            }
            
            // Check if class ID exists
            String[] existing = FileHandler.searchLine(CLASSES_FILE, 0, classId);
            if (existing != null) {
                JOptionPane.showMessageDialog(dialog, "Class ID already exists", "Error", JOptionPane.ERROR_MESSAGE);
                return;
            }
            
            FileHandler.appendLine(CLASSES_FILE, classId, moduleCode, intake);
            JOptionPane.showMessageDialog(dialog, "Class added successfully!", "Success", JOptionPane.INFORMATION_MESSAGE);
            loadClassData();
            dialog.dispose();
        });
        
        cancelButton.addActionListener(e -> dialog.dispose());
        
        buttonPanel.add(saveButton);
        buttonPanel.add(cancelButton);
        
        gbc.gridx = 0;
        gbc.gridy = 3;
        gbc.gridwidth = 2;
        panel.add(buttonPanel, gbc);
        
        dialog.add(panel);
        dialog.setVisible(true);
    }
    
    /**
     * Deletes selected class
     */
    private void deleteClass() {
        int selectedRow = classTable.getSelectedRow();
        if (selectedRow == -1) {
            JOptionPane.showMessageDialog(this, "Please select a class to delete", "Warning", JOptionPane.WARNING_MESSAGE);
            return;
        }
        
        String classId = classTable.getValueAt(selectedRow, 0).toString();
        
        int confirm = JOptionPane.showConfirmDialog(this, 
            "Are you sure you want to delete this class?", 
            "Confirm Delete", 
            JOptionPane.YES_NO_OPTION);
        
        if (confirm == JOptionPane.YES_OPTION) {
            FileHandler.deleteLine(CLASSES_FILE, 0, classId);
            JOptionPane.showMessageDialog(this, "Class deleted successfully!", "Success", JOptionPane.INFORMATION_MESSAGE);
            loadClassData();
        }
    }
    
    /**
     * Helper method to add form field
     */
    private void addFormField(JPanel panel, GridBagConstraints gbc, int row, String label, JComponent field) {
        gbc.gridx = 0;
        gbc.gridy = row;
        gbc.weightx = 0.3;
        JLabel jLabel = new JLabel(label);
        jLabel.setFont(new Font("Arial", Font.PLAIN, 13));
        panel.add(jLabel, gbc);
        
        gbc.gridx = 1;
        gbc.weightx = 0.7;
        panel.add(field, gbc);
    }
    
    /**
     * Logout and return to login screen
     */
    private void logout() {
        Session.getInstance().logout();
        new LoginFrame().setVisible(true);
        dispose();
    }
}
