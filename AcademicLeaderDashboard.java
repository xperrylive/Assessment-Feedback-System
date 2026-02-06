import javax.swing.*;
import javax.swing.table.*;
import java.awt.*;
import java.awt.event.*;
import java.util.List;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

/**
 * AcademicLeaderDashboard - Academic Leader interface
 * Features:
 * 1. View assigned lecturers (where SupervisorID matches leader's ID)
 * 2. Create/Assign modules
 * 3. View analyzed reports for modules (requires min 5 results)
 */
public class AcademicLeaderDashboard extends JFrame {
    
    private JTabbedPane tabbedPane;
    private JTable lecturerTable;
    private DefaultTableModel lecturerTableModel;
    private JTable moduleTable;
    private DefaultTableModel moduleTableModel;
    private JTextArea reportArea;
    private JComboBox<String> reportModuleCombo;
    
    private static final String USERS_FILE = "users.txt";
    private static final String MODULES_FILE = "modules.txt";
    private static final String ASSESSMENTS_FILE = "assessments.txt";
    private static final String RESULTS_FILE = "results.txt";
    private static final String GRADING_FILE = "grading.txt";
    
    private String currentLeaderId;
    
    public AcademicLeaderDashboard() {
        currentLeaderId = Session.getInstance().getCurrentUserId();
        initializeUI();
        loadLecturerData();
        loadModuleData();
        populateReportModules();
    }
    
    private void initializeUI() {
        setTitle("Academic Leader Dashboard - Assessment Feedback System");
        setSize(1000, 700);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        
        JPanel mainPanel = new JPanel(new BorderLayout());
        
        // Header
        JPanel headerPanel = createHeaderPanel();
        mainPanel.add(headerPanel, BorderLayout.NORTH);
        
        // Tabbed pane
        tabbedPane = new JTabbedPane();
        tabbedPane.setFont(new Font("Arial", Font.PLAIN, 14));
        
        tabbedPane.addTab("My Lecturers", createLecturerPanel());
        tabbedPane.addTab("My Modules", createModulePanel());
        tabbedPane.addTab("Module Reports", createReportPanel());
        
        mainPanel.add(tabbedPane, BorderLayout.CENTER);
        
        add(mainPanel);
    }
    
    /**
     * Creates header panel
     */
    private JPanel createHeaderPanel() {
        JPanel headerPanel = new JPanel(new BorderLayout());
        headerPanel.setBackground(new Color(142, 68, 173));
        headerPanel.setPreferredSize(new Dimension(1000, 60));
        headerPanel.setBorder(BorderFactory.createEmptyBorder(10, 20, 10, 20));
        
        User currentUser = Session.getInstance().getCurrentUser();
        JLabel welcomeLabel = new JLabel("Welcome, " + currentUser.getFullName() + " (Academic Leader)");
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
     * Creates lecturer panel - shows lecturers supervised by this leader
     */
    private JPanel createLecturerPanel() {
        JPanel panel = new JPanel(new BorderLayout(10, 10));
        panel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        
        // Info label
        JLabel infoLabel = new JLabel("Lecturers under your supervision:");
        infoLabel.setFont(new Font("Arial", Font.BOLD, 14));
        infoLabel.setBorder(BorderFactory.createEmptyBorder(5, 5, 5, 5));
        
        // Table
        String[] columns = {"Lecturer ID", "Username", "Full Name", "Gender", "Email", "Phone", "Age", "Assigned Module"};
        lecturerTableModel = new DefaultTableModel(columns, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
        lecturerTable = new JTable(lecturerTableModel);
        lecturerTable.setFont(new Font("Arial", Font.PLAIN, 12));
        lecturerTable.setRowHeight(25);
        lecturerTable.getTableHeader().setFont(new Font("Arial", Font.BOLD, 12));
        lecturerTable.getTableHeader().setBackground(new Color(142, 68, 173));
        lecturerTable.getTableHeader().setForeground(Color.WHITE);
        
        JScrollPane scrollPane = new JScrollPane(lecturerTable);
        
        // Button panel
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 10));
        JButton refreshButton = createStyledButton("Refresh", new Color(52, 152, 219));
        refreshButton.addActionListener(e -> loadLecturerData());
        buttonPanel.add(refreshButton);
        
        panel.add(infoLabel, BorderLayout.NORTH);
        panel.add(scrollPane, BorderLayout.CENTER);
        panel.add(buttonPanel, BorderLayout.SOUTH);
        
        return panel;
    }
    
    /**
     * Creates module panel - manage modules
     */
    private JPanel createModulePanel() {
        JPanel panel = new JPanel(new BorderLayout(10, 10));
        panel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        
        // Info label
        JLabel infoLabel = new JLabel("Modules under your management:");
        infoLabel.setFont(new Font("Arial", Font.BOLD, 14));
        infoLabel.setBorder(BorderFactory.createEmptyBorder(5, 5, 5, 5));
        
        // Table
        String[] columns = {"Module Code", "Module Name", "Assigned Lecturer", "Lecturer Name"};
        moduleTableModel = new DefaultTableModel(columns, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
        moduleTable = new JTable(moduleTableModel);
        moduleTable.setFont(new Font("Arial", Font.PLAIN, 12));
        moduleTable.setRowHeight(25);
        moduleTable.getTableHeader().setFont(new Font("Arial", Font.BOLD, 12));
        moduleTable.getTableHeader().setBackground(new Color(142, 68, 173));
        moduleTable.getTableHeader().setForeground(Color.WHITE);
        
        JScrollPane scrollPane = new JScrollPane(moduleTable);
        
        // Button panel
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 10));
        
        JButton addButton = createStyledButton("Create Module", new Color(46, 204, 113));
        JButton assignButton = createStyledButton("Assign Lecturer", new Color(241, 196, 15));
        JButton deleteButton = createStyledButton("Delete Module", new Color(231, 76, 60));
        JButton refreshButton = createStyledButton("Refresh", new Color(52, 152, 219));
        
        addButton.addActionListener(e -> showCreateModuleDialog());
        assignButton.addActionListener(e -> showAssignLecturerDialog());
        deleteButton.addActionListener(e -> deleteModule());
        refreshButton.addActionListener(e -> loadModuleData());
        
        buttonPanel.add(addButton);
        buttonPanel.add(assignButton);
        buttonPanel.add(deleteButton);
        buttonPanel.add(refreshButton);
        
        panel.add(infoLabel, BorderLayout.NORTH);
        panel.add(scrollPane, BorderLayout.CENTER);
        panel.add(buttonPanel, BorderLayout.SOUTH);
        
        return panel;
    }
    
    /**
     * Creates report panel - view analyzed reports
     */
    private JPanel createReportPanel() {
        JPanel panel = new JPanel(new BorderLayout(10, 10));
        panel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        
        // Top panel with module selection
        JPanel topPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 10));
        topPanel.add(new JLabel("Select Module:"));
        
        reportModuleCombo = new JComboBox<>();
        reportModuleCombo.setPreferredSize(new Dimension(300, 30));
        reportModuleCombo.setFont(new Font("Arial", Font.PLAIN, 13));
        topPanel.add(reportModuleCombo);
        
        JButton generateButton = createStyledButton("Generate Report", new Color(46, 204, 113));
        generateButton.addActionListener(e -> generateReport());
        topPanel.add(generateButton);
        
        // Report area
        reportArea = new JTextArea();
        reportArea.setFont(new Font("Monospaced", Font.PLAIN, 12));
        reportArea.setEditable(false);
        reportArea.setMargin(new Insets(10, 10, 10, 10));
        
        JScrollPane scrollPane = new JScrollPane(reportArea);
        
        panel.add(topPanel, BorderLayout.NORTH);
        panel.add(scrollPane, BorderLayout.CENTER);
        
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
        button.setPreferredSize(new Dimension(140, 35));
        return button;
    }
    
    /**
     * Loads lecturer data - only lecturers supervised by this leader
     */
    private void loadLecturerData() {
        lecturerTableModel.setRowCount(0);
        List<String[]> users = FileHandler.getAllRecords(USERS_FILE);
        List<String[]> modules = FileHandler.getAllRecords(MODULES_FILE);
        
        for (String[] user : users) {
            if (user.length >= 10 && user[3].equals("Lecturer") && user[9].equals(currentLeaderId)) {
                // Find assigned module
                String assignedModule = "Not Assigned";
                for (String[] module : modules) {
                    if (module.length >= 4 && module[3].equals(user[0])) {
                        assignedModule = module[0] + " - " + module[1];
                        break;
                    }
                }
                
                Object[] row = {
                    user[0],  // Lecturer ID
                    user[1],  // Username
                    user[4],  // Full Name
                    user[5],  // Gender
                    user[6],  // Email
                    user[7],  // Phone
                    user[8],  // Age
                    assignedModule
                };
                lecturerTableModel.addRow(row);
            }
        }
    }
    
    /**
     * Loads module data - only modules managed by this leader
     */
    private void loadModuleData() {
        moduleTableModel.setRowCount(0);
        List<String[]> modules = FileHandler.getAllRecords(MODULES_FILE);
        List<String[]> users = FileHandler.getAllRecords(USERS_FILE);
        
        for (String[] module : modules) {
            if (module.length >= 4 && module[2].equals(currentLeaderId)) {
                // Find lecturer name
                String lecturerName = "Not Assigned";
                if (!module[3].equals("N/A")) {
                    for (String[] user : users) {
                        if (user.length >= 5 && user[0].equals(module[3])) {
                            lecturerName = user[4];
                            break;
                        }
                    }
                }
                
                Object[] row = {
                    module[0],  // Module Code
                    module[1],  // Module Name
                    module[3],  // Lecturer ID
                    lecturerName
                };
                moduleTableModel.addRow(row);
            }
        }
    }
    
    /**
     * Populates report module combo box
     */
    private void populateReportModules() {
        reportModuleCombo.removeAllItems();
        List<String[]> modules = FileHandler.getAllRecords(MODULES_FILE);
        
        for (String[] module : modules) {
            if (module.length >= 3 && module[2].equals(currentLeaderId)) {
                reportModuleCombo.addItem(module[0] + " - " + module[1]);
            }
        }
    }
    
    /**
     * Shows dialog to create new module
     */
    private void showCreateModuleDialog() {
        JDialog dialog = new JDialog(this, "Create New Module", true);
        dialog.setSize(450, 300);
        dialog.setLocationRelativeTo(this);
        
        JPanel panel = new JPanel(new GridBagLayout());
        panel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
        
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.insets = new Insets(5, 5, 5, 5);
        
        JTextField codeField = new JTextField(20);
        JTextField nameField = new JTextField(20);
        JComboBox<String> lecturerCombo = new JComboBox<>();
        
        // Populate lecturer combo with supervised lecturers
        lecturerCombo.addItem("N/A - Assign Later");
        List<String[]> users = FileHandler.getAllRecords(USERS_FILE);
        for (String[] user : users) {
            if (user.length >= 10 && user[3].equals("Lecturer") && user[9].equals(currentLeaderId)) {
                lecturerCombo.addItem(user[0] + " - " + user[4]);
            }
        }
        
        addFormField(panel, gbc, 0, "Module Code:", codeField);
        addFormField(panel, gbc, 1, "Module Name:", nameField);
        addFormField(panel, gbc, 2, "Assign Lecturer:", lecturerCombo);
        
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 10, 10));
        JButton saveButton = new JButton("Create");
        JButton cancelButton = new JButton("Cancel");
        
        saveButton.addActionListener(e -> {
            String code = codeField.getText().trim();
            String name = nameField.getText().trim();
            String lecturerSelection = (String) lecturerCombo.getSelectedItem();
            String lecturerId = lecturerSelection.startsWith("N/A") ? "N/A" : lecturerSelection.split(" - ")[0];
            
            if (code.isEmpty() || name.isEmpty()) {
                JOptionPane.showMessageDialog(dialog, "Please fill all fields", "Error", JOptionPane.ERROR_MESSAGE);
                return;
            }
            
            // Check if module code exists
            String[] existing = FileHandler.searchLine(MODULES_FILE, 0, code);
            if (existing != null) {
                JOptionPane.showMessageDialog(dialog, "Module code already exists", "Error", JOptionPane.ERROR_MESSAGE);
                return;
            }
            
            // Schema: ModuleCode|ModuleName|LeaderID|LecturerID
            FileHandler.appendLine(MODULES_FILE, code, name, currentLeaderId, lecturerId);
            JOptionPane.showMessageDialog(dialog, "Module created successfully!", "Success", JOptionPane.INFORMATION_MESSAGE);
            loadModuleData();
            populateReportModules();
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
     * Shows dialog to assign lecturer to module
     */
    private void showAssignLecturerDialog() {
        int selectedRow = moduleTable.getSelectedRow();
        if (selectedRow == -1) {
            JOptionPane.showMessageDialog(this, "Please select a module to assign lecturer", "Warning", JOptionPane.WARNING_MESSAGE);
            return;
        }
        
        String moduleCode = moduleTable.getValueAt(selectedRow, 0).toString();
        String moduleName = moduleTable.getValueAt(selectedRow, 1).toString();
        
        String[] moduleData = FileHandler.searchLine(MODULES_FILE, 0, moduleCode);
        if (moduleData == null) {
            JOptionPane.showMessageDialog(this, "Module not found", "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }
        
        JDialog dialog = new JDialog(this, "Assign Lecturer to Module", true);
        dialog.setSize(450, 200);
        dialog.setLocationRelativeTo(this);
        
        JPanel panel = new JPanel(new GridBagLayout());
        panel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
        
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.insets = new Insets(5, 5, 5, 5);
        
        JLabel moduleLabel = new JLabel(moduleCode + " - " + moduleName);
        moduleLabel.setFont(new Font("Arial", Font.BOLD, 13));
        
        JComboBox<String> lecturerCombo = new JComboBox<>();
        lecturerCombo.addItem("N/A - Unassign");
        
        List<String[]> users = FileHandler.getAllRecords(USERS_FILE);
        for (String[] user : users) {
            if (user.length >= 10 && user[3].equals("Lecturer") && user[9].equals(currentLeaderId)) {
                lecturerCombo.addItem(user[0] + " - " + user[4]);
            }
        }
        
        // Set current lecturer if assigned
        if (!moduleData[3].equals("N/A")) {
            for (String[] user : users) {
                if (user.length >= 5 && user[0].equals(moduleData[3])) {
                    lecturerCombo.setSelectedItem(user[0] + " - " + user[4]);
                    break;
                }
            }
        }
        
        addFormField(panel, gbc, 0, "Module:", moduleLabel);
        addFormField(panel, gbc, 1, "Assign to:", lecturerCombo);
        
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 10, 10));
        JButton saveButton = new JButton("Assign");
        JButton cancelButton = new JButton("Cancel");
        
        saveButton.addActionListener(e -> {
            String lecturerSelection = (String) lecturerCombo.getSelectedItem();
            String lecturerId = lecturerSelection.startsWith("N/A") ? "N/A" : lecturerSelection.split(" - ")[0];
            
            FileHandler.updateLine(MODULES_FILE, 0, moduleCode, moduleCode, moduleData[1], moduleData[2], lecturerId);
            JOptionPane.showMessageDialog(dialog, "Lecturer assigned successfully!", "Success", JOptionPane.INFORMATION_MESSAGE);
            loadModuleData();
            loadLecturerData();
            dialog.dispose();
        });
        
        cancelButton.addActionListener(e -> dialog.dispose());
        
        buttonPanel.add(saveButton);
        buttonPanel.add(cancelButton);
        
        gbc.gridx = 0;
        gbc.gridy = 2;
        gbc.gridwidth = 2;
        panel.add(buttonPanel, gbc);
        
        dialog.add(panel);
        dialog.setVisible(true);
    }
    
    /**
     * Deletes selected module
     */
    private void deleteModule() {
        int selectedRow = moduleTable.getSelectedRow();
        if (selectedRow == -1) {
            JOptionPane.showMessageDialog(this, "Please select a module to delete", "Warning", JOptionPane.WARNING_MESSAGE);
            return;
        }
        
        String moduleCode = moduleTable.getValueAt(selectedRow, 0).toString();
        
        int confirm = JOptionPane.showConfirmDialog(this,
            "Are you sure you want to delete this module?\nThis will also delete all associated assessments and results.",
            "Confirm Delete",
            JOptionPane.YES_NO_OPTION);
        
        if (confirm == JOptionPane.YES_OPTION) {
            FileHandler.deleteLine(MODULES_FILE, 0, moduleCode);
            JOptionPane.showMessageDialog(this, "Module deleted successfully!", "Success", JOptionPane.INFORMATION_MESSAGE);
            loadModuleData();
            populateReportModules();
        }
    }
    
    /**
     * Generates analyzed report for selected module
     */
    private void generateReport() {
        if (reportModuleCombo.getSelectedItem() == null) {
            JOptionPane.showMessageDialog(this, "No modules available for reporting", "Warning", JOptionPane.WARNING_MESSAGE);
            return;
        }
        
        String selection = reportModuleCombo.getSelectedItem().toString();
        String moduleCode = selection.split(" - ")[0];
        
        reportArea.setText("Generating report for " + selection + "...\n\n");
        
        // Get all assessments for this module
        List<String[]> assessments = FileHandler.getAllRecords(ASSESSMENTS_FILE);
        List<String[]> allResults = FileHandler.getAllRecords(RESULTS_FILE);
        List<String[]> gradingScale = FileHandler.getAllRecords(GRADING_FILE);
        
        List<String[]> moduleAssessments = new ArrayList<>();
        for (String[] assessment : assessments) {
            if (assessment.length >= 5 && assessment[1].equals(moduleCode)) {
                moduleAssessments.add(assessment);
            }
        }
        
        if (moduleAssessments.isEmpty()) {
            reportArea.append("No assessments found for this module.\n");
            return;
        }
        
        StringBuilder report = new StringBuilder();
        report.append("=" .repeat(70)).append("\n");
        report.append("MODULE ANALYSIS REPORT\n");
        report.append("=" .repeat(70)).append("\n");
        report.append("Module: ").append(selection).append("\n");
        report.append("Generated: ").append(java.time.LocalDateTime.now().format(
            java.time.format.DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"))).append("\n");
        report.append("=" .repeat(70)).append("\n\n");
        
        int totalResults = 0;
        double totalMarksSum = 0;
        Map<String, Integer> gradeDistribution = new HashMap<>();
        
        for (String[] assessment : moduleAssessments) {
            String assessmentId = assessment[0];
            String assessmentTitle = assessment[3];
            int maxMarks = Integer.parseInt(assessment[4]);
            
            // Get results for this assessment
            List<Integer> marks = new ArrayList<>();
            for (String[] result : allResults) {
                if (result.length >= 4 && result[1].equals(assessmentId)) {
                    marks.add(Integer.parseInt(result[3]));
                }
            }
            
            if (!marks.isEmpty()) {
                totalResults += marks.size();
                
                // Calculate statistics
                double sum = 0;
                int min = marks.get(0);
                int max = marks.get(0);
                
                for (int mark : marks) {
                    sum += mark;
                    totalMarksSum += mark;
                    if (mark < min) min = mark;
                    if (mark > max) max = mark;
                    
                    // Calculate grade
                    String grade = getGrade(mark, gradingScale);
                    gradeDistribution.put(grade, gradeDistribution.getOrDefault(grade, 0) + 1);
                }
                
                double average = sum / marks.size();
                
                report.append("Assessment: ").append(assessmentTitle).append("\n");
                report.append("  Type: ").append(assessment[2]).append("\n");
                report.append("  Max Marks: ").append(maxMarks).append("\n");
                report.append("  Submissions: ").append(marks.size()).append("\n");
                report.append("  Average: ").append(String.format("%.2f", average)).append("\n");
                report.append("  Highest: ").append(max).append("\n");
                report.append("  Lowest: ").append(min).append("\n");
                report.append("-".repeat(70)).append("\n");
            }
        }
        
        if (totalResults < 5) {
            report.append("\n*** INSUFFICIENT DATA ***\n");
            report.append("Minimum 5 results required for statistical analysis.\n");
            report.append("Current results: ").append(totalResults).append("\n");
        } else {
            report.append("\n");
            report.append("=" .repeat(70)).append("\n");
            report.append("OVERALL MODULE STATISTICS\n");
            report.append("=" .repeat(70)).append("\n");
            report.append("Total Submissions: ").append(totalResults).append("\n");
            report.append("Overall Average: ").append(String.format("%.2f", totalMarksSum / totalResults)).append("\n\n");
            
            report.append("GRADE DISTRIBUTION:\n");
            report.append("-".repeat(70)).append("\n");
            
            // *** FIX: Use a final copy of totalResults for the lambda ***
            final int finalTotalResults = totalResults;
            
            // Sort grades by count
            gradeDistribution.entrySet().stream()
                .sorted((a, b) -> b.getValue().compareTo(a.getValue()))
                .forEach(entry -> {
                    int count = entry.getValue();
                    double percentage = (count * 100.0) / finalTotalResults;
                    report.append(String.format("  %-5s : %3d students (%.1f%%) ", 
                        entry.getKey(), count, percentage));
                    report.append(getBar((int)percentage)).append("\n");
                });
            
            report.append("=" .repeat(70)).append("\n");
        }
        
        reportArea.setText(report.toString());
    }
    
    /**
     * Gets grade for a mark based on grading scale
     */
    private String getGrade(int mark, List<String[]> gradingScale) {
        for (String[] grade : gradingScale) {
            if (grade.length >= 3) {
                int min = Integer.parseInt(grade[1]);
                int max = Integer.parseInt(grade[2]);
                if (mark >= min && mark <= max) {
                    return grade[0];
                }
            }
        }
        return "N/A";
    }
    
    /**
     * Creates a simple bar chart
     */
    private String getBar(int percentage) {
        int bars = percentage / 2;
        return "█".repeat(bars);
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
     * Logout
     */
    private void logout() {
        Session.getInstance().logout();
        new LoginFrame().setVisible(true);
        dispose();
    }
}