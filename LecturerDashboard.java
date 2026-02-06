import javax.swing.*;
import javax.swing.table.*;
import java.awt.*;
import java.awt.event.*;
import java.util.List;
import java.util.ArrayList;

/**
 * LecturerDashboard - Lecturer interface
 * Features:
 * 1. View ONLY their assigned module (where LecturerID matches in modules.txt)
 * 2. Add assessments for their module
 * 3. Enter marks and feedback for students
 */
public class LecturerDashboard extends JFrame {
    
    private JTabbedPane tabbedPane;
    private JLabel moduleInfoLabel;
    private JTable assessmentTable;
    private DefaultTableModel assessmentTableModel;
    private JTable studentTable;
    private DefaultTableModel studentTableModel;
    private JComboBox<String> assessmentCombo;
    
    private static final String MODULES_FILE = "modules.txt";
    private static final String ASSESSMENTS_FILE = "assessments.txt";
    private static final String RESULTS_FILE = "results.txt";
    private static final String CLASSES_FILE = "classes.txt";
    private static final String USERS_FILE = "users.txt";
    
    private String currentLecturerId;
    private String assignedModuleCode;
    private String assignedModuleName;
    
    public LecturerDashboard() {
        currentLecturerId = Session.getInstance().getCurrentUserId();
        loadAssignedModule();
        initializeUI();
        
        if (assignedModuleCode != null) {
            loadAssessmentData();
            populateAssessmentCombo();
        }
    }
    
    /**
     * Loads the module assigned to this lecturer
     */
    private void loadAssignedModule() {
        List<String[]> modules = FileHandler.getAllRecords(MODULES_FILE);
        
        for (String[] module : modules) {
            if (module.length >= 4 && module[3].equals(currentLecturerId)) {
                assignedModuleCode = module[0];
                assignedModuleName = module[1];
                return;
            }
        }
        
        // No module assigned
        assignedModuleCode = null;
        assignedModuleName = null;
    }
    
    private void initializeUI() {
        setTitle("Lecturer Dashboard - Assessment Feedback System");
        setSize(1000, 700);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        
        JPanel mainPanel = new JPanel(new BorderLayout());
        
        // Header
        JPanel headerPanel = createHeaderPanel();
        mainPanel.add(headerPanel, BorderLayout.NORTH);
        
        // Check if module is assigned
        if (assignedModuleCode == null) {
            JPanel noModulePanel = new JPanel(new BorderLayout());
            noModulePanel.setBorder(BorderFactory.createEmptyBorder(50, 50, 50, 50));
            
            JLabel messageLabel = new JLabel("<html><center>You have not been assigned to any module yet.<br>" +
                "Please contact your Academic Leader for module assignment.</center></html>", SwingConstants.CENTER);
            messageLabel.setFont(new Font("Arial", Font.PLAIN, 16));
            
            noModulePanel.add(messageLabel, BorderLayout.CENTER);
            mainPanel.add(noModulePanel, BorderLayout.CENTER);
        } else {
            // Tabbed pane
            tabbedPane = new JTabbedPane();
            tabbedPane.setFont(new Font("Arial", Font.PLAIN, 14));
            
            tabbedPane.addTab("My Module", createModuleInfoPanel());
            tabbedPane.addTab("Assessments", createAssessmentPanel());
            tabbedPane.addTab("Enter Marks", createMarksEntryPanel());
            
            mainPanel.add(tabbedPane, BorderLayout.CENTER);
        }
        
        add(mainPanel);
    }
    
    /**
     * Creates header panel
     */
    private JPanel createHeaderPanel() {
        JPanel headerPanel = new JPanel(new BorderLayout());
        headerPanel.setBackground(new Color(22, 160, 133));
        headerPanel.setPreferredSize(new Dimension(1000, 60));
        headerPanel.setBorder(BorderFactory.createEmptyBorder(10, 20, 10, 20));
        
        User currentUser = Session.getInstance().getCurrentUser();
        JLabel welcomeLabel = new JLabel("Welcome, " + currentUser.getFullName() + " (Lecturer)");
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
     * Creates module info panel
     */
    private JPanel createModuleInfoPanel() {
        JPanel panel = new JPanel(new BorderLayout(10, 10));
        panel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
        
        JPanel infoPanel = new JPanel();
        infoPanel.setLayout(new BoxLayout(infoPanel, BoxLayout.Y_AXIS));
        infoPanel.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createTitledBorder(BorderFactory.createLineBorder(new Color(22, 160, 133), 2), 
                "Module Information", 
                javax.swing.border.TitledBorder.LEFT, 
                javax.swing.border.TitledBorder.TOP,
                new Font("Arial", Font.BOLD, 14)),
            BorderFactory.createEmptyBorder(20, 20, 20, 20)
        ));
        
        JLabel codeLabel = new JLabel("Module Code: " + assignedModuleCode);
        codeLabel.setFont(new Font("Arial", Font.PLAIN, 16));
        
        JLabel nameLabel = new JLabel("Module Name: " + assignedModuleName);
        nameLabel.setFont(new Font("Arial", Font.PLAIN, 16));
        
        // Count assessments
        List<String[]> assessments = FileHandler.getAllRecords(ASSESSMENTS_FILE);
        int assessmentCount = 0;
        for (String[] assessment : assessments) {
            if (assessment.length >= 2 && assessment[1].equals(assignedModuleCode)) {
                assessmentCount++;
            }
        }
        
        JLabel assessmentCountLabel = new JLabel("Total Assessments: " + assessmentCount);
        assessmentCountLabel.setFont(new Font("Arial", Font.PLAIN, 16));
        
        // Count enrolled students
        List<String[]> classes = FileHandler.getAllRecords(CLASSES_FILE);
        int classCount = 0;
        for (String[] classData : classes) {
            if (classData.length >= 2 && classData[1].equals(assignedModuleCode)) {
                classCount++;
            }
        }
        
        JLabel classCountLabel = new JLabel("Active Classes: " + classCount);
        classCountLabel.setFont(new Font("Arial", Font.PLAIN, 16));
        
        infoPanel.add(codeLabel);
        infoPanel.add(Box.createRigidArea(new Dimension(0, 15)));
        infoPanel.add(nameLabel);
        infoPanel.add(Box.createRigidArea(new Dimension(0, 15)));
        infoPanel.add(assessmentCountLabel);
        infoPanel.add(Box.createRigidArea(new Dimension(0, 15)));
        infoPanel.add(classCountLabel);
        
        panel.add(infoPanel, BorderLayout.NORTH);
        
        return panel;
    }
    
    /**
     * Creates assessment panel
     */
    private JPanel createAssessmentPanel() {
        JPanel panel = new JPanel(new BorderLayout(10, 10));
        panel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        
        // Info label
        JLabel infoLabel = new JLabel("Assessments for " + assignedModuleCode + " - " + assignedModuleName);
        infoLabel.setFont(new Font("Arial", Font.BOLD, 14));
        infoLabel.setBorder(BorderFactory.createEmptyBorder(5, 5, 5, 5));
        
        // Table
        String[] columns = {"Assessment ID", "Type", "Title", "Max Marks", "Submissions"};
        assessmentTableModel = new DefaultTableModel(columns, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
        assessmentTable = new JTable(assessmentTableModel);
        assessmentTable.setFont(new Font("Arial", Font.PLAIN, 12));
        assessmentTable.setRowHeight(25);
        assessmentTable.getTableHeader().setFont(new Font("Arial", Font.BOLD, 12));
        assessmentTable.getTableHeader().setBackground(new Color(22, 160, 133));
        assessmentTable.getTableHeader().setForeground(Color.WHITE);
        
        JScrollPane scrollPane = new JScrollPane(assessmentTable);
        
        // Button panel
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 10));
        
        JButton addButton = createStyledButton("Add Assessment", new Color(46, 204, 113));
        JButton deleteButton = createStyledButton("Delete Assessment", new Color(231, 76, 60));
        JButton refreshButton = createStyledButton("Refresh", new Color(52, 152, 219));
        
        addButton.addActionListener(e -> showAddAssessmentDialog());
        deleteButton.addActionListener(e -> deleteAssessment());
        refreshButton.addActionListener(e -> loadAssessmentData());
        
        buttonPanel.add(addButton);
        buttonPanel.add(deleteButton);
        buttonPanel.add(refreshButton);
        
        panel.add(infoLabel, BorderLayout.NORTH);
        panel.add(scrollPane, BorderLayout.CENTER);
        panel.add(buttonPanel, BorderLayout.SOUTH);
        
        return panel;
    }
    
    /**
     * Creates marks entry panel
     */
    private JPanel createMarksEntryPanel() {
        JPanel panel = new JPanel(new BorderLayout(10, 10));
        panel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        
        // Top panel with assessment selection
        JPanel topPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 10));
        topPanel.add(new JLabel("Select Assessment:"));
        
        assessmentCombo = new JComboBox<>();
        assessmentCombo.setPreferredSize(new Dimension(400, 30));
        assessmentCombo.setFont(new Font("Arial", Font.PLAIN, 13));
        assessmentCombo.addActionListener(e -> loadStudentsForAssessment());
        topPanel.add(assessmentCombo);
        
        JButton refreshButton = createStyledButton("Refresh", new Color(52, 152, 219));
        refreshButton.addActionListener(e -> {
            populateAssessmentCombo();
            loadStudentsForAssessment();
        });
        topPanel.add(refreshButton);
        
        // Table
        String[] columns = {"Student ID", "Student Name", "Marks", "Feedback", "Status"};
        studentTableModel = new DefaultTableModel(columns, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
        studentTable = new JTable(studentTableModel);
        studentTable.setFont(new Font("Arial", Font.PLAIN, 12));
        studentTable.setRowHeight(25);
        studentTable.getTableHeader().setFont(new Font("Arial", Font.BOLD, 12));
        studentTable.getTableHeader().setBackground(new Color(22, 160, 133));
        studentTable.getTableHeader().setForeground(Color.WHITE);
        
        JScrollPane scrollPane = new JScrollPane(studentTable);
        
        // Button panel
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 10));
        
        JButton enterMarksButton = createStyledButton("Enter Marks", new Color(46, 204, 113));
        JButton editMarksButton = createStyledButton("Edit Marks", new Color(241, 196, 15));
        
        enterMarksButton.addActionListener(e -> showEnterMarksDialog());
        editMarksButton.addActionListener(e -> showEditMarksDialog());
        
        buttonPanel.add(enterMarksButton);
        buttonPanel.add(editMarksButton);
        
        panel.add(topPanel, BorderLayout.NORTH);
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
        button.setPreferredSize(new Dimension(140, 35));
        return button;
    }
    
    /**
     * Loads assessment data
     */
    private void loadAssessmentData() {
        assessmentTableModel.setRowCount(0);
        List<String[]> assessments = FileHandler.getAllRecords(ASSESSMENTS_FILE);
        List<String[]> results = FileHandler.getAllRecords(RESULTS_FILE);
        
        for (String[] assessment : assessments) {
            if (assessment.length >= 5 && assessment[1].equals(assignedModuleCode)) {
                // Count submissions
                int submissions = 0;
                for (String[] result : results) {
                    if (result.length >= 2 && result[1].equals(assessment[0])) {
                        submissions++;
                    }
                }
                
                Object[] row = {
                    assessment[0],  // Assessment ID
                    assessment[2],  // Type
                    assessment[3],  // Title
                    assessment[4],  // Max Marks
                    submissions
                };
                assessmentTableModel.addRow(row);
            }
        }
    }
    
    /**
     * Populates assessment combo box
     */
    private void populateAssessmentCombo() {
        assessmentCombo.removeAllItems();
        List<String[]> assessments = FileHandler.getAllRecords(ASSESSMENTS_FILE);
        
        for (String[] assessment : assessments) {
            if (assessment.length >= 5 && assessment[1].equals(assignedModuleCode)) {
                assessmentCombo.addItem(assessment[0] + " - " + assessment[3] + " (" + assessment[2] + ")");
            }
        }
    }
    
    /**
     * Loads students for selected assessment
     */
    private void loadStudentsForAssessment() {
        studentTableModel.setRowCount(0);
        
        if (assessmentCombo.getSelectedItem() == null) {
            return;
        }
        
        String selection = assessmentCombo.getSelectedItem().toString();
        String assessmentId = selection.split(" - ")[0];
        
        // Get all students
        List<String[]> users = FileHandler.getAllRecords(USERS_FILE);
        List<String[]> results = FileHandler.getAllRecords(RESULTS_FILE);
        
        for (String[] user : users) {
            if (user.length >= 5 && user[3].equals("Student")) {
                String studentId = user[0];
                String studentName = user[4];
                
                // Check if student has result for this assessment
                String marks = "Not Graded";
                String feedback = "-";
                String status = "Pending";
                
                for (String[] result : results) {
                    if (result.length >= 5 && result[1].equals(assessmentId) && result[2].equals(studentId)) {
                        marks = result[3];
                        feedback = result[4];
                        status = "Graded";
                        break;
                    }
                }
                
                Object[] row = {studentId, studentName, marks, feedback, status};
                studentTableModel.addRow(row);
            }
        }
    }
    
    /**
     * Shows dialog to add assessment
     */
    private void showAddAssessmentDialog() {
        JDialog dialog = new JDialog(this, "Add New Assessment", true);
        dialog.setSize(500, 350);
        dialog.setLocationRelativeTo(this);
        
        JPanel panel = new JPanel(new GridBagLayout());
        panel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
        
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.insets = new Insets(5, 5, 5, 5);
        
        JTextField idField = new JTextField(20);
        JComboBox<String> typeCombo = new JComboBox<>(new String[]{"Quiz", "Assignment", "Exam", "Project", "Test"});
        JTextField titleField = new JTextField(20);
        JSpinner maxMarksSpinner = new JSpinner(new SpinnerNumberModel(100, 1, 1000, 1));
        
        addFormField(panel, gbc, 0, "Assessment ID:", idField);
        addFormField(panel, gbc, 1, "Type:", typeCombo);
        addFormField(panel, gbc, 2, "Title:", titleField);
        addFormField(panel, gbc, 3, "Max Marks:", maxMarksSpinner);
        
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 10, 10));
        JButton saveButton = new JButton("Save");
        JButton cancelButton = new JButton("Cancel");
        
        saveButton.addActionListener(e -> {
            String id = idField.getText().trim();
            String type = (String) typeCombo.getSelectedItem();
            String title = titleField.getText().trim();
            int maxMarks = (Integer) maxMarksSpinner.getValue();
            
            if (id.isEmpty() || title.isEmpty()) {
                JOptionPane.showMessageDialog(dialog, "Please fill all fields", "Error", JOptionPane.ERROR_MESSAGE);
                return;
            }
            
            // Check if ID exists
            String[] existing = FileHandler.searchLine(ASSESSMENTS_FILE, 0, id);
            if (existing != null) {
                JOptionPane.showMessageDialog(dialog, "Assessment ID already exists", "Error", JOptionPane.ERROR_MESSAGE);
                return;
            }
            
            // Schema: AssessmentID|ModuleCode|Type|Title|MaxMarks
            FileHandler.appendLine(ASSESSMENTS_FILE, id, assignedModuleCode, type, title, String.valueOf(maxMarks));
            JOptionPane.showMessageDialog(dialog, "Assessment added successfully!", "Success", JOptionPane.INFORMATION_MESSAGE);
            loadAssessmentData();
            populateAssessmentCombo();
            dialog.dispose();
        });
        
        cancelButton.addActionListener(e -> dialog.dispose());
        
        buttonPanel.add(saveButton);
        buttonPanel.add(cancelButton);
        
        gbc.gridx = 0;
        gbc.gridy = 4;
        gbc.gridwidth = 2;
        panel.add(buttonPanel, gbc);
        
        dialog.add(panel);
        dialog.setVisible(true);
    }
    
    /**
     * Deletes selected assessment
     */
    private void deleteAssessment() {
        int selectedRow = assessmentTable.getSelectedRow();
        if (selectedRow == -1) {
            JOptionPane.showMessageDialog(this, "Please select an assessment to delete", "Warning", JOptionPane.WARNING_MESSAGE);
            return;
        }
        
        String assessmentId = assessmentTable.getValueAt(selectedRow, 0).toString();
        
        int confirm = JOptionPane.showConfirmDialog(this,
            "Are you sure you want to delete this assessment?\nAll associated results will also be deleted.",
            "Confirm Delete",
            JOptionPane.YES_NO_OPTION);
        
        if (confirm == JOptionPane.YES_OPTION) {
            FileHandler.deleteLine(ASSESSMENTS_FILE, 0, assessmentId);
            JOptionPane.showMessageDialog(this, "Assessment deleted successfully!", "Success", JOptionPane.INFORMATION_MESSAGE);
            loadAssessmentData();
            populateAssessmentCombo();
        }
    }
    
    /**
     * Shows dialog to enter marks
     */
    private void showEnterMarksDialog() {
        int selectedRow = studentTable.getSelectedRow();
        if (selectedRow == -1) {
            JOptionPane.showMessageDialog(this, "Please select a student to enter marks", "Warning", JOptionPane.WARNING_MESSAGE);
            return;
        }
        
        String status = studentTable.getValueAt(selectedRow, 4).toString();
        if (status.equals("Graded")) {
            JOptionPane.showMessageDialog(this, "Student already has marks. Use 'Edit Marks' to modify.", "Warning", JOptionPane.WARNING_MESSAGE);
            return;
        }
        
        String studentId = studentTable.getValueAt(selectedRow, 0).toString();
        String studentName = studentTable.getValueAt(selectedRow, 1).toString();
        
        String selection = assessmentCombo.getSelectedItem().toString();
        String assessmentId = selection.split(" - ")[0];
        
        // Get max marks
        String[] assessmentData = FileHandler.searchLine(ASSESSMENTS_FILE, 0, assessmentId);
        if (assessmentData == null) {
            JOptionPane.showMessageDialog(this, "Assessment not found", "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }
        int maxMarks = Integer.parseInt(assessmentData[4]);
        
        JDialog dialog = new JDialog(this, "Enter Marks", true);
        dialog.setSize(450, 300);
        dialog.setLocationRelativeTo(this);
        
        JPanel panel = new JPanel(new GridBagLayout());
        panel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
        
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.insets = new Insets(5, 5, 5, 5);
        
        JLabel studentLabel = new JLabel(studentName + " (" + studentId + ")");
        studentLabel.setFont(new Font("Arial", Font.BOLD, 13));
        
        JLabel assessmentLabel = new JLabel(selection);
        assessmentLabel.setFont(new Font("Arial", Font.PLAIN, 12));
        
        JSpinner marksSpinner = new JSpinner(new SpinnerNumberModel(0, 0, maxMarks, 1));
        JTextArea feedbackArea = new JTextArea(5, 20);
        feedbackArea.setLineWrap(true);
        feedbackArea.setWrapStyleWord(true);
        JScrollPane feedbackScroll = new JScrollPane(feedbackArea);
        
        addFormField(panel, gbc, 0, "Student:", studentLabel);
        addFormField(panel, gbc, 1, "Assessment:", assessmentLabel);
        addFormField(panel, gbc, 2, "Marks (Max " + maxMarks + "):", marksSpinner);
        
        gbc.gridx = 0;
        gbc.gridy = 3;
        gbc.weightx = 0.3;
        JLabel feedbackLabel = new JLabel("Feedback:");
        feedbackLabel.setFont(new Font("Arial", Font.PLAIN, 13));
        panel.add(feedbackLabel, gbc);
        
        gbc.gridx = 1;
        gbc.weightx = 0.7;
        panel.add(feedbackScroll, gbc);
        
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 10, 10));
        JButton saveButton = new JButton("Save");
        JButton cancelButton = new JButton("Cancel");
        
        saveButton.addActionListener(e -> {
            int marks = (Integer) marksSpinner.getValue();
            String feedback = feedbackArea.getText().trim();
            
            if (feedback.isEmpty()) {
                feedback = "No feedback provided";
            }
            
            // Generate result ID
            String resultId = generateResultId();
            
            // Schema: ResultID|AssessmentID|StudentID|Marks|Feedback
            FileHandler.appendLine(RESULTS_FILE, resultId, assessmentId, studentId, String.valueOf(marks), feedback);
            JOptionPane.showMessageDialog(dialog, "Marks saved successfully!", "Success", JOptionPane.INFORMATION_MESSAGE);
            loadStudentsForAssessment();
            loadAssessmentData();
            dialog.dispose();
        });
        
        cancelButton.addActionListener(e -> dialog.dispose());
        
        buttonPanel.add(saveButton);
        buttonPanel.add(cancelButton);
        
        gbc.gridx = 0;
        gbc.gridy = 4;
        gbc.gridwidth = 2;
        panel.add(buttonPanel, gbc);
        
        dialog.add(panel);
        dialog.setVisible(true);
    }
    
    /**
     * Shows dialog to edit marks
     */
    private void showEditMarksDialog() {
        int selectedRow = studentTable.getSelectedRow();
        if (selectedRow == -1) {
            JOptionPane.showMessageDialog(this, "Please select a student to edit marks", "Warning", JOptionPane.WARNING_MESSAGE);
            return;
        }
        
        String status = studentTable.getValueAt(selectedRow, 4).toString();
        if (!status.equals("Graded")) {
            JOptionPane.showMessageDialog(this, "No marks entered yet. Use 'Enter Marks' first.", "Warning", JOptionPane.WARNING_MESSAGE);
            return;
        }
        
        String studentId = studentTable.getValueAt(selectedRow, 0).toString();
        String studentName = studentTable.getValueAt(selectedRow, 1).toString();
        String currentMarks = studentTable.getValueAt(selectedRow, 2).toString();
        String currentFeedback = studentTable.getValueAt(selectedRow, 3).toString();
        
        String selection = assessmentCombo.getSelectedItem().toString();
        String assessmentId = selection.split(" - ")[0];
        
        // Get max marks and result ID
        String[] assessmentData = FileHandler.searchLine(ASSESSMENTS_FILE, 0, assessmentId);
        if (assessmentData == null) {
            JOptionPane.showMessageDialog(this, "Assessment not found", "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }
        int maxMarks = Integer.parseInt(assessmentData[4]);
        
        // Find result ID
        String resultId = null;
        List<String[]> results = FileHandler.getAllRecords(RESULTS_FILE);
        for (String[] result : results) {
            if (result.length >= 5 && result[1].equals(assessmentId) && result[2].equals(studentId)) {
                resultId = result[0];
                break;
            }
        }
        
        if (resultId == null) {
            JOptionPane.showMessageDialog(this, "Result not found", "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }
        
        JDialog dialog = new JDialog(this, "Edit Marks", true);
        dialog.setSize(450, 300);
        dialog.setLocationRelativeTo(this);
        
        JPanel panel = new JPanel(new GridBagLayout());
        panel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
        
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.insets = new Insets(5, 5, 5, 5);
        
        JLabel studentLabel = new JLabel(studentName + " (" + studentId + ")");
        studentLabel.setFont(new Font("Arial", Font.BOLD, 13));
        
        JLabel assessmentLabel = new JLabel(selection);
        assessmentLabel.setFont(new Font("Arial", Font.PLAIN, 12));
        
        JSpinner marksSpinner = new JSpinner(new SpinnerNumberModel(Integer.parseInt(currentMarks), 0, maxMarks, 1));
        JTextArea feedbackArea = new JTextArea(5, 20);
        feedbackArea.setText(currentFeedback);
        feedbackArea.setLineWrap(true);
        feedbackArea.setWrapStyleWord(true);
        JScrollPane feedbackScroll = new JScrollPane(feedbackArea);
        
        addFormField(panel, gbc, 0, "Student:", studentLabel);
        addFormField(panel, gbc, 1, "Assessment:", assessmentLabel);
        addFormField(panel, gbc, 2, "Marks (Max " + maxMarks + "):", marksSpinner);
        
        gbc.gridx = 0;
        gbc.gridy = 3;
        gbc.weightx = 0.3;
        JLabel feedbackLabel = new JLabel("Feedback:");
        feedbackLabel.setFont(new Font("Arial", Font.PLAIN, 13));
        panel.add(feedbackLabel, gbc);
        
        gbc.gridx = 1;
        gbc.weightx = 0.7;
        panel.add(feedbackScroll, gbc);
        
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 10, 10));
        JButton saveButton = new JButton("Update");
        JButton cancelButton = new JButton("Cancel");
        
        final String finalResultId = resultId;
        saveButton.addActionListener(e -> {
            int marks = (Integer) marksSpinner.getValue();
            String feedback = feedbackArea.getText().trim();
            
            if (feedback.isEmpty()) {
                feedback = "No feedback provided";
            }
            
            // Update result
            FileHandler.updateLine(RESULTS_FILE, 0, finalResultId, finalResultId, assessmentId, studentId, String.valueOf(marks), feedback);
            JOptionPane.showMessageDialog(dialog, "Marks updated successfully!", "Success", JOptionPane.INFORMATION_MESSAGE);
            loadStudentsForAssessment();
            dialog.dispose();
        });
        
        cancelButton.addActionListener(e -> dialog.dispose());
        
        buttonPanel.add(saveButton);
        buttonPanel.add(cancelButton);
        
        gbc.gridx = 0;
        gbc.gridy = 4;
        gbc.gridwidth = 2;
        panel.add(buttonPanel, gbc);
        
        dialog.add(panel);
        dialog.setVisible(true);
    }
    
    /**
     * Generates unique result ID
     */
    private String generateResultId() {
        List<String[]> results = FileHandler.getAllRecords(RESULTS_FILE);
        int maxId = 0;
        
        for (String[] result : results) {
            if (result.length >= 1) {
                try {
                    String idNum = result[0].replace("RES", "");
                    int num = Integer.parseInt(idNum);
                    if (num > maxId) maxId = num;
                } catch (NumberFormatException e) {
                    // Skip invalid IDs
                }
            }
        }
        
        return String.format("RES%05d", maxId + 1);
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
