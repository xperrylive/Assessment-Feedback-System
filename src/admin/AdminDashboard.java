package admin;

import common.*;
import utils.*;

import javax.swing.*;
import javax.swing.table.*;
import java.awt.*;
import java.util.List;
import java.util.ArrayList;

public class AdminDashboard extends JFrame {
    private JTabbedPane tabbedPane;
    private JTable userTable;
    private JTable classTable;
    private JTable gradingTable;
    private JTable logsTable; // NEW: Logs Table
    private DefaultTableModel userTableModel;
    private DefaultTableModel classTableModel;
    private DefaultTableModel gradingTableModel;
    private DefaultTableModel logsTableModel; // NEW: Logs Model
    private TableRowSorter<TableModel> userSorter; // NEW: Sorter for Search

    // UI Constants
    private final Color PRIMARY_COLOR = new Color(52, 152, 219);
    private final Color DANGER_COLOR = new Color(231, 76, 60);
    private final Color SUCCESS_COLOR = new Color(46, 204, 113);
    private final Color HEADER_COLOR = new Color(41, 128, 185);
    private final Color BG_COLOR = Color.WHITE;

    public AdminDashboard() {
        String userName = (Session.getInstance().getCurrentUser() != null) 
                          ? Session.getInstance().getCurrentUser().getFullName() 
                          : "Admin";
        
        setTitle("Admin Dashboard - " + userName);
        setSize(1000, 650);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);

        initComponents();
        loadData();
    }

    private void initComponents() {
        setLayout(new BorderLayout());
        
        // --- Top Toolbar ---
        JPanel topPanel = new JPanel(new BorderLayout());
        topPanel.setBackground(BG_COLOR);
        topPanel.setBorder(BorderFactory.createEmptyBorder(15, 15, 15, 15));
        
        String userName = (Session.getInstance().getCurrentUser() != null) 
                          ? Session.getInstance().getCurrentUser().getFullName() 
                          : "User";
        JLabel welcomeLabel = new JLabel("Welcome, " + userName);
        welcomeLabel.setFont(new Font("Arial", Font.BOLD, 16));
        welcomeLabel.setForeground(HEADER_COLOR);
        topPanel.add(welcomeLabel, BorderLayout.WEST);
        
        JPanel topRightPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 0));
        topRightPanel.setBackground(BG_COLOR);

        JButton profileButton = createStyledButton("Edit Profile", PRIMARY_COLOR);
        profileButton.addActionListener(e -> {
            if(Session.getInstance().getCurrentUser() != null)
                new ProfileEditor(this, Session.getInstance().getCurrentUser()).setVisible(true);
        });

        JButton logoutButton = createStyledButton("Logout", DANGER_COLOR);
        logoutButton.addActionListener(e -> {
            Logger.log(Session.getInstance().getCurrentUser().getId(), "Logged out");
            Session.getInstance().logout();
            dispose();
            new LoginFrame().setVisible(true);
        });

        topRightPanel.add(profileButton);
        topRightPanel.add(logoutButton);
        topPanel.add(topRightPanel, BorderLayout.EAST);
        add(topPanel, BorderLayout.NORTH);
        
        // --- Tabs ---
        tabbedPane = new JTabbedPane();
        tabbedPane.setFont(new Font("Arial", Font.PLAIN, 14));
        tabbedPane.setBackground(BG_COLOR);

        tabbedPane.addTab("Manage Users", createUserManagementPanel());
        tabbedPane.addTab("Classes", createClassManagementPanel());
        tabbedPane.addTab("Grading", createGradingPanel());
        tabbedPane.addTab("Activity Logs", createLogsPanel()); // NEW TAB

        add(tabbedPane, BorderLayout.CENTER);
    }

    // ... (styleTable & createStyledButton methods remain same as before) ...
    private void styleTable(JTable table, JScrollPane scrollPane) {
        table.setFont(new Font("Arial", Font.PLAIN, 13));
        table.setRowHeight(30);
        table.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        table.setGridColor(new Color(230, 230, 230));
        JTableHeader header = table.getTableHeader();
        header.setDefaultRenderer(new DefaultTableCellRenderer() {
            public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column) {
                Component c = super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);
                c.setBackground(HEADER_COLOR);
                c.setForeground(Color.WHITE);
                c.setFont(new Font("Arial", Font.BOLD, 13));
                if (c instanceof JLabel) {
                    ((JLabel) c).setBorder(BorderFactory.createEmptyBorder(0, 5, 0, 5));
                    ((JLabel) c).setHorizontalAlignment(SwingConstants.LEFT);
                }
                return c;
            }
        });
        table.setFillsViewportHeight(true);
        table.setBackground(Color.WHITE);
        scrollPane.getViewport().setBackground(Color.WHITE);
        scrollPane.setBorder(BorderFactory.createLineBorder(new Color(230, 230, 230)));
    }

    private JButton createStyledButton(String text, Color bg) {
        JButton btn = new JButton(text);
        btn.setBackground(bg);
        btn.setForeground(Color.WHITE);
        btn.setFont(new Font("Arial", Font.BOLD, 12));
        btn.setFocusPainted(false);
        btn.setOpaque(true);
        btn.setBorderPainted(false);
        btn.setBorder(BorderFactory.createEmptyBorder(8, 15, 8, 15));
        btn.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseEntered(java.awt.event.MouseEvent evt) { btn.setBackground(bg.darker()); }
            public void mouseExited(java.awt.event.MouseEvent evt) { btn.setBackground(bg); }
        });
        return btn;
    }

    private JPanel createUserManagementPanel() {
        JPanel panel = new JPanel(new BorderLayout(10, 10));
        panel.setBorder(BorderFactory.createEmptyBorder(15, 15, 15, 15));
        panel.setBackground(BG_COLOR);

        // --- NEW: Search Bar Panel ---
        JPanel searchPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        searchPanel.setBackground(BG_COLOR);
        searchPanel.add(new JLabel("Search Users: "));
        JTextField searchField = new JTextField(20);
        searchPanel.add(searchField);
        panel.add(searchPanel, BorderLayout.NORTH);

        String[] columns = {"User ID", "Name", "Role", "Email", "Phone", "Gender", "Age", "DOB", "Supervisor"};
        userTableModel = new DefaultTableModel(columns, 0) {
            public boolean isCellEditable(int row, int column) { return false; }
        };
        
        userTable = new JTable(userTableModel);
        
        // --- NEW: Attach Sorter ---
        userSorter = new TableRowSorter<>(userTableModel);
        userTable.setRowSorter(userSorter);
        
        // Search Logic
        searchField.getDocument().addDocumentListener(new javax.swing.event.DocumentListener() {
            public void insertUpdate(javax.swing.event.DocumentEvent e) { filter(); }
            public void removeUpdate(javax.swing.event.DocumentEvent e) { filter(); }
            public void changedUpdate(javax.swing.event.DocumentEvent e) { filter(); }
            private void filter() {
                String text = searchField.getText();
                if (text.trim().length() == 0) {
                    userSorter.setRowFilter(null);
                } else {
                    userSorter.setRowFilter(RowFilter.regexFilter("(?i)" + text));
                }
            }
        });

        JScrollPane scrollPane = new JScrollPane(userTable);
        styleTable(userTable, scrollPane);
        panel.add(scrollPane, BorderLayout.CENTER);

        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        buttonPanel.setBackground(BG_COLOR);

        JButton addButton = createStyledButton("Add User", SUCCESS_COLOR);
        addButton.addActionListener(e -> showUserDialog(null));

        JButton editButton = createStyledButton("Edit User", PRIMARY_COLOR);
        editButton.addActionListener(e -> {
            int selectedRow = userTable.getSelectedRow();
            if (selectedRow == -1) {
                JOptionPane.showMessageDialog(this, "Select a user"); return;
            }
            // Convert view index to model index (important when sorting/filtering!)
            int modelRow = userTable.convertRowIndexToModel(selectedRow);
            String userId = (String) userTableModel.getValueAt(modelRow, 0);
            showUserDialog(userId);
        });

        JButton deleteButton = createStyledButton("Delete User", DANGER_COLOR);
        deleteButton.addActionListener(e -> deleteUser());

        JButton refreshButton = createStyledButton("Refresh", new Color(149, 165, 166));
        refreshButton.addActionListener(e -> loadUserData());

        buttonPanel.add(addButton);
        buttonPanel.add(editButton);
        buttonPanel.add(deleteButton);
        buttonPanel.add(refreshButton);

        panel.add(buttonPanel, BorderLayout.SOUTH);
        return panel;
    }

    // --- NEW: Logs Panel ---
    private JPanel createLogsPanel() {
        JPanel panel = new JPanel(new BorderLayout(10, 10));
        panel.setBorder(BorderFactory.createEmptyBorder(15, 15, 15, 15));
        panel.setBackground(BG_COLOR);

        String[] columns = {"Timestamp", "User ID", "Action"};
        logsTableModel = new DefaultTableModel(columns, 0) {
            public boolean isCellEditable(int row, int column) { return false; }
        };
        logsTable = new JTable(logsTableModel);
        JScrollPane scrollPane = new JScrollPane(logsTable);
        styleTable(logsTable, scrollPane);
        
        // Auto-width for Timestamp and User ID
        logsTable.getColumnModel().getColumn(0).setPreferredWidth(150);
        logsTable.getColumnModel().getColumn(1).setPreferredWidth(100);
        logsTable.getColumnModel().getColumn(2).setPreferredWidth(500);

        panel.add(scrollPane, BorderLayout.CENTER);

        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        buttonPanel.setBackground(BG_COLOR);
        JButton refreshButton = createStyledButton("Refresh Logs", PRIMARY_COLOR);
        refreshButton.addActionListener(e -> loadLogsData());
        buttonPanel.add(refreshButton);
        
        panel.add(buttonPanel, BorderLayout.SOUTH);
        return panel;
    }

    // ... (createClassManagementPanel & createGradingPanel remain mostly same) ...
    // Just omitting their unchanged code for brevity, BUT ensure createGradingPanel calls Logger.log on save

    private JPanel createClassManagementPanel() {
        JPanel panel = new JPanel(new BorderLayout(10, 10));
        panel.setBorder(BorderFactory.createEmptyBorder(15, 15, 15, 15));
        panel.setBackground(BG_COLOR);
        String[] columns = {"Class ID", "Class Name", "Module ID"};
        classTableModel = new DefaultTableModel(columns, 0) { public boolean isCellEditable(int r, int c) { return false; } };
        classTable = new JTable(classTableModel);
        JScrollPane scrollPane = new JScrollPane(classTable);
        styleTable(classTable, scrollPane);
        panel.add(scrollPane, BorderLayout.CENTER);
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        buttonPanel.setBackground(BG_COLOR);
        JButton addButton = createStyledButton("Add Class", SUCCESS_COLOR);
        addButton.addActionListener(e -> showAddClassDialog());
        buttonPanel.add(addButton);
        panel.add(buttonPanel, BorderLayout.SOUTH);
        return panel;
    }

    private JPanel createGradingPanel() {
        JPanel panel = new JPanel(new BorderLayout(10, 10));
        panel.setBorder(BorderFactory.createEmptyBorder(15, 15, 15, 15));
        panel.setBackground(BG_COLOR);
        String[] columns = {"Grade", "Min Marks", "Max Marks"};
        gradingTableModel = new DefaultTableModel(columns, 0) { public boolean isCellEditable(int r, int c) { return false; } };
        gradingTable = new JTable(gradingTableModel);
        JScrollPane scrollPane = new JScrollPane(gradingTable);
        styleTable(gradingTable, scrollPane);
        panel.add(scrollPane, BorderLayout.CENTER);
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        buttonPanel.setBackground(BG_COLOR);
        JButton editButton = createStyledButton("Edit Range", PRIMARY_COLOR);
        editButton.addActionListener(e -> editGradeRange());
        buttonPanel.add(editButton);
        panel.add(buttonPanel, BorderLayout.SOUTH);
        return panel;
    }

    private void loadData() {
        loadUserData();
        loadClassData();
        loadGradingData();
        loadLogsData(); // Load logs
    }

    private void loadUserData() {
        userTableModel.setRowCount(0);
        List<String[]> users = FileHandler.getAllRecords("users.txt");
        for (String[] user : users) {
            if (user.length >= 9) {
                String supervisor = (user.length > 9) ? user[9] : "-";
                userTableModel.addRow(new Object[]{
                    user[0], user[3], user[2], user[5], user[6], user[4], user[7], user[8], supervisor
                });
            }
        }
    }

    private void loadClassData() {
        classTableModel.setRowCount(0);
        List<String[]> classes = FileHandler.getAllRecords("classes.txt");
        for (String[] cls : classes) {
            if (cls.length >= 3) classTableModel.addRow(cls);
        }
    }

    private void loadGradingData() {
        gradingTableModel.setRowCount(0);
        List<String[]> grades = FileHandler.getAllRecords("grading.txt");
        for (String[] grade : grades) {
            if (grade.length >= 3) gradingTableModel.addRow(grade);
        }
    }

    // --- NEW: Load Logs ---
    private void loadLogsData() {
        logsTableModel.setRowCount(0);
        List<String[]> logs = FileHandler.getAllRecords("activity_log.txt");
        // Add logs in reverse order (newest first)
        for (int i = logs.size() - 1; i >= 0; i--) {
            String[] log = logs.get(i);
            if (log.length >= 3) logsTableModel.addRow(log);
        }
    }

    // --- Dialogs (Updated with Hashing and Logging) ---

    private void showUserDialog(String userIdToEdit) {
        // ... (UI Code remains same as previous step) ...
        boolean isEdit = (userIdToEdit != null);
        String title = isEdit ? "Edit User" : "Add New User";
        JDialog dialog = new JDialog(this, title, true);
        dialog.setSize(450, 500);
        dialog.setLocationRelativeTo(this);
        JPanel panel = new JPanel(new GridBagLayout());
        panel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
        panel.setBackground(BG_COLOR);
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(8, 8, 8, 8);
        gbc.fill = GridBagConstraints.HORIZONTAL;

        JTextField nameField = new JTextField(20);
        JTextField dobField = new JTextField(20);
        JComboBox<String> roleCombo = new JComboBox<>(new String[]{"Student", "Lecturer", "Leader"});
        roleCombo.setBackground(Color.WHITE);
        JComboBox<String> genderCombo = new JComboBox<>(new String[]{"Male", "Female"});
        genderCombo.setBackground(Color.WHITE);
        JTextField emailField = new JTextField(20);
        JTextField phoneField = new JTextField(20);
        JTextField ageField = new JTextField(20);
        JComboBox<String> supervisorCombo = new JComboBox<>();
        supervisorCombo.setBackground(Color.WHITE);
        supervisorCombo.setEnabled(false);
        
        List<String[]> users = FileHandler.getAllRecords("users.txt");
        supervisorCombo.addItem("None");
        for(String[] u : users) if(u.length >= 3 && u[2].equals("Leader")) supervisorCombo.addItem(u[0] + " - " + u[3]);

        final String[] currentPassword = {null};
        if (isEdit) {
            for (String[] u : users) {
                if (u[0].equals(userIdToEdit)) {
                    nameField.setText(u[3]); dobField.setText(u[8]); roleCombo.setSelectedItem(u[2]);
                    genderCombo.setSelectedItem(u[4]); emailField.setText(u[5]); phoneField.setText(u[6]);
                    ageField.setText(u[7]); currentPassword[0] = u[1];
                    roleCombo.setEnabled(false); dobField.setEnabled(false);
                    if(u[2].equals("Lecturer") && u.length > 9) {
                        supervisorCombo.setEnabled(true);
                        for(int i=0; i<supervisorCombo.getItemCount(); i++) 
                            if(supervisorCombo.getItemAt(i).startsWith(u[9])) { supervisorCombo.setSelectedIndex(i); break; }
                    }
                    break;
                }
            }
        }

        roleCombo.addActionListener(e -> supervisorCombo.setEnabled("Lecturer".equals(roleCombo.getSelectedItem())));

        addFormRow(panel, gbc, 0, "Full Name:", nameField);
        addFormRow(panel, gbc, 1, "Date of Birth (DD/MM/YYYY):", dobField);
        addFormRow(panel, gbc, 2, "Role:", roleCombo);
        addFormRow(panel, gbc, 3, "Gender:", genderCombo);
        addFormRow(panel, gbc, 4, "Email:", emailField);
        addFormRow(panel, gbc, 5, "Phone:", phoneField);
        addFormRow(panel, gbc, 6, "Age:", ageField);
        addFormRow(panel, gbc, 7, "Supervisor:", supervisorCombo);

        JPanel buttonPanel = new JPanel(); buttonPanel.setBackground(BG_COLOR);
        JButton saveButton = createStyledButton("Save", SUCCESS_COLOR);
        
        saveButton.addActionListener(e -> {
            String name = nameField.getText().trim();
            String dob = dobField.getText().trim();
            String role = (String) roleCombo.getSelectedItem();
            String gender = (String) genderCombo.getSelectedItem();
            String email = emailField.getText().trim();
            String phone = phoneField.getText().trim();
            String age = ageField.getText().trim();
            String supervisorSelection = (String) supervisorCombo.getSelectedItem();
            String supervisorId = (supervisorSelection != null && !supervisorSelection.equals("None")) ? supervisorSelection.split(" - ")[0] : "-";

            if (name.isEmpty() || dob.isEmpty() || email.isEmpty() || phone.isEmpty() || age.isEmpty()) {
                JOptionPane.showMessageDialog(dialog, "All fields required!"); return;
            }

            String currentAdminId = Session.getInstance().getCurrentUser().getId();

            if (isEdit) {
                String[] updatedData = { userIdToEdit, currentPassword[0], role, name, gender, email, phone, age, dob, supervisorId };
                FileHandler.updateLine("users.txt", 0, userIdToEdit, updatedData);
                // LOG
                Logger.log(currentAdminId, "Edited user " + userIdToEdit);
                JOptionPane.showMessageDialog(this, "User updated!");
            } else {
                String id = generateUserId(role);
                // HASH NEW USER PASSWORD
                String password = Security.hashPassword(id + dob.replace("/", ""));
                FileHandler.appendLine("users.txt", id, password, role, name, gender, email, phone, age, dob, supervisorId);
                // LOG
                Logger.log(currentAdminId, "Created user " + id);
                JOptionPane.showMessageDialog(this, "User created: " + id);
            }
            loadUserData(); dialog.dispose();
        });

        JButton cancelButton = createStyledButton("Cancel", Color.GRAY);
        cancelButton.addActionListener(e -> dialog.dispose());
        buttonPanel.add(saveButton); buttonPanel.add(cancelButton);
        gbc.gridx = 0; gbc.gridy = 8; gbc.gridwidth = 2; panel.add(buttonPanel, gbc);
        dialog.add(panel); dialog.setVisible(true);
    }
    
    private void addFormRow(JPanel panel, GridBagConstraints gbc, int y, String label, JComponent comp) {
        gbc.gridx = 0; gbc.gridy = y; gbc.gridwidth = 1; panel.add(new JLabel(label), gbc);
        gbc.gridx = 1; panel.add(comp, gbc);
    }

    private String generateUserId(String role) {
        String prefix = ""; int startNum = 0;
        switch (role) {
            case "Student": prefix = "TP"; startNum = 30001; break;
            case "Lecturer": prefix = "LC"; startNum = 20001; break;
            case "Leader": prefix = "AL"; startNum = 10001; break;
        }
        List<String[]> users = FileHandler.getAllRecords("users.txt");
        int maxNum = startNum - 1;
        for (String[] user : users) {
            if (user[0].startsWith(prefix)) {
                try { int num = Integer.parseInt(user[0].substring(2)); if (num > maxNum) maxNum = num; } catch (NumberFormatException e) { }
            }
        }
        return prefix + String.format("%05d", maxNum + 1);
    }

    private void deleteUser() {
        int selectedRow = userTable.getSelectedRow();
        if (selectedRow == -1) { JOptionPane.showMessageDialog(this, "Select a user"); return; }
        
        int modelRow = userTable.convertRowIndexToModel(selectedRow);
        String userId = (String) userTableModel.getValueAt(modelRow, 0);
        
        if (JOptionPane.showConfirmDialog(this, "Delete " + userId + "?", "Confirm", JOptionPane.YES_NO_OPTION) == JOptionPane.YES_OPTION) {
            if (FileHandler.deleteLine("users.txt", 0, userId)) {
                // LOG
                Logger.log(Session.getInstance().getCurrentUser().getId(), "Deleted user " + userId);
                loadUserData();
                JOptionPane.showMessageDialog(this, "Deleted success");
            }
        }
    }

    private void showAddClassDialog() {
        // ... (Keep existing implementation, add Logger.log call on save) ...
        JDialog dialog = new JDialog(this, "Add New Class", true); dialog.setSize(400, 300); dialog.setLocationRelativeTo(this);
        JPanel panel = new JPanel(new GridBagLayout()); panel.setBackground(BG_COLOR);
        GridBagConstraints gbc = new GridBagConstraints(); gbc.insets = new Insets(8, 8, 8, 8); gbc.fill = GridBagConstraints.HORIZONTAL;
        JTextField classIdField = new JTextField(15); JTextField classNameField = new JTextField(15); JTextField moduleIdField = new JTextField(15);
        addFormRow(panel, gbc, 0, "Class ID:", classIdField); addFormRow(panel, gbc, 1, "Class Name:", classNameField); addFormRow(panel, gbc, 2, "Module ID:", moduleIdField);
        JPanel buttonPanel = new JPanel(); buttonPanel.setBackground(BG_COLOR);
        JButton saveButton = createStyledButton("Save", SUCCESS_COLOR);
        saveButton.addActionListener(e -> {
            String cid = classIdField.getText(); String cname = classNameField.getText(); String mid = moduleIdField.getText();
            if(!cid.isEmpty() && !cname.isEmpty() && !mid.isEmpty()) {
                FileHandler.appendLine("classes.txt", cid, cname, mid);
                Logger.log(Session.getInstance().getCurrentUser().getId(), "Created class " + cid);
                loadClassData(); dialog.dispose();
            }
        });
        JButton cancelButton = createStyledButton("Cancel", Color.GRAY); cancelButton.addActionListener(e -> dialog.dispose());
        buttonPanel.add(saveButton); buttonPanel.add(cancelButton);
        gbc.gridx=0; gbc.gridy=3; gbc.gridwidth=2; panel.add(buttonPanel, gbc);
        dialog.add(panel); dialog.setVisible(true);
    }

    private void editGradeRange() {
        // ... (Keep existing implementation, add Logger.log call on save) ...
        int selectedRow = gradingTable.getSelectedRow();
        if (selectedRow == -1) return;
        String grade = (String) gradingTableModel.getValueAt(selectedRow, 0);
        String min = (String) gradingTableModel.getValueAt(selectedRow, 1);
        String max = (String) gradingTableModel.getValueAt(selectedRow, 2);
        
        JDialog dialog = new JDialog(this, "Edit", true); dialog.setSize(300, 250); dialog.setLocationRelativeTo(this);
        JPanel panel = new JPanel(new GridBagLayout()); panel.setBackground(BG_COLOR);
        GridBagConstraints gbc = new GridBagConstraints(); gbc.insets = new Insets(5,5,5,5); gbc.fill = GridBagConstraints.HORIZONTAL;
        JTextField minF = new JTextField(min, 10); JTextField maxF = new JTextField(max, 10);
        gbc.gridx=0; gbc.gridy=0; panel.add(new JLabel("Grade "+grade), gbc);
        addFormRow(panel, gbc, 1, "Min:", minF); addFormRow(panel, gbc, 2, "Max:", maxF);
        
        JButton saveBtn = createStyledButton("Save", SUCCESS_COLOR);
        saveBtn.addActionListener(e -> {
            FileHandler.updateLine("grading.txt", 0, grade, grade, minF.getText(), maxF.getText());
            Logger.log(Session.getInstance().getCurrentUser().getId(), "Edited grading for " + grade);
            loadGradingData(); dialog.dispose();
        });
        
        gbc.gridx=0; gbc.gridy=3; panel.add(saveBtn, gbc);
        dialog.add(panel); dialog.setVisible(true);
    }
}