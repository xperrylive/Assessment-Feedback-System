package lecturer;

import common.*;
import utils.*;

import javax.swing.*;
import javax.swing.table.*;
import java.awt.*;
import java.util.*;
import java.util.List;

public class LecturerDashboard extends JFrame {
    private JTabbedPane tabbedPane;
    private JTable assessmentTable;
    private DefaultTableModel assessmentTableModel;
    private JTable feedbackTable; // NEW: Table for feedback
    private DefaultTableModel feedbackTableModel; // NEW: Model for feedback
    private String currentLecturerId;
    private String currentModuleId;

    // UI Constants
    private final Color PRIMARY_COLOR = new Color(52, 152, 219);
    private final Color DANGER_COLOR = new Color(231, 76, 60);
    private final Color SUCCESS_COLOR = new Color(46, 204, 113);
    private final Color HEADER_COLOR = new Color(41, 128, 185);
    private final Color BG_COLOR = Color.WHITE;

    public LecturerDashboard() {
        currentLecturerId = Session.getInstance().getCurrentUser().getId();
        findLecturerModule();
        
        setTitle("Lecturer Dashboard - " + Session.getInstance().getCurrentUser().getFullName());
        setSize(900, 600);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);

        initComponents();
        loadData();
    }

    private void findLecturerModule() {
        List<String[]> modules = FileHandler.getAllRecords("modules.txt");
        for (String[] module : modules) {
            if (module.length >= 3 && module[2].equals(currentLecturerId)) {
                currentModuleId = module[0];
                break;
            }
        }
    }

    private void initComponents() {
        setLayout(new BorderLayout());
        
        // --- Top Toolbar ---
        JPanel topPanel = new JPanel(new BorderLayout());
        topPanel.setBackground(BG_COLOR);
        topPanel.setBorder(BorderFactory.createEmptyBorder(15, 15, 15, 15));
        
        JLabel welcomeLabel = new JLabel("Welcome, " + Session.getInstance().getCurrentUser().getFullName());
        welcomeLabel.setFont(new Font("Arial", Font.BOLD, 16));
        welcomeLabel.setForeground(HEADER_COLOR);
        topPanel.add(welcomeLabel, BorderLayout.WEST);
        
        // --- Top Right Buttons ---
        JPanel topRightPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 0));
        topRightPanel.setBackground(BG_COLOR);

        JButton profileButton = createStyledButton("Edit Profile", PRIMARY_COLOR);
        profileButton.addActionListener(e -> {
            new ProfileEditor(this, Session.getInstance().getCurrentUser()).setVisible(true);
        });

        JButton logoutButton = createStyledButton("Logout", DANGER_COLOR);
        logoutButton.addActionListener(e -> {
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

        tabbedPane.addTab("Assessments", createAssessmentsPanel());
        tabbedPane.addTab("Grade Students", createGradingPanel());
        tabbedPane.addTab("Student Feedback", createFeedbackPanel()); // NEW TAB

        add(tabbedPane, BorderLayout.CENTER);
    }

    private void styleTable(JTable table, JScrollPane scrollPane) {
        table.setFont(new Font("Arial", Font.PLAIN, 13));
        table.setRowHeight(30);
        table.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        table.setGridColor(new Color(230, 230, 230));
        
        JTableHeader header = table.getTableHeader();
        header.setDefaultRenderer(new DefaultTableCellRenderer() {
            @Override
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

    private JPanel createAssessmentsPanel() {
        JPanel panel = new JPanel(new BorderLayout(10, 10));
        panel.setBorder(BorderFactory.createEmptyBorder(15, 15, 15, 15));
        panel.setBackground(BG_COLOR);

        if (currentModuleId == null) {
            JLabel noModuleLabel = new JLabel("No module assigned to you", SwingConstants.CENTER);
            noModuleLabel.setFont(new Font("Arial", Font.BOLD, 16));
            panel.add(noModuleLabel, BorderLayout.CENTER);
            return panel;
        }

        String[] columns = {"Assessment ID", "Title", "Type", "Max Marks"};
        assessmentTableModel = new DefaultTableModel(columns, 0) {
            @Override
            public boolean isCellEditable(int row, int column) { return false; }
        };
        assessmentTable = new JTable(assessmentTableModel);
        JScrollPane scrollPane = new JScrollPane(assessmentTable);
        styleTable(assessmentTable, scrollPane);

        panel.add(scrollPane, BorderLayout.CENTER);

        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        buttonPanel.setBackground(BG_COLOR);

        JButton addButton = createStyledButton("Add Assessment", SUCCESS_COLOR);
        addButton.addActionListener(e -> showAddAssessmentDialog());

        JButton refreshButton = createStyledButton("Refresh", PRIMARY_COLOR);
        refreshButton.addActionListener(e -> loadAssessmentData());

        buttonPanel.add(addButton);
        buttonPanel.add(refreshButton);
        panel.add(buttonPanel, BorderLayout.SOUTH);
        return panel;
    }

    private JPanel createGradingPanel() {
        JPanel panel = new JPanel(new BorderLayout(10, 10));
        panel.setBorder(BorderFactory.createEmptyBorder(15, 15, 15, 15));
        panel.setBackground(BG_COLOR);

        if (currentModuleId == null) {
            JLabel noModuleLabel = new JLabel("No module assigned to you", SwingConstants.CENTER);
            noModuleLabel.setFont(new Font("Arial", Font.BOLD, 16));
            panel.add(noModuleLabel, BorderLayout.CENTER);
            return panel;
        }

        JPanel topPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        topPanel.setBackground(BG_COLOR);

        JLabel selectLabel = new JLabel("Select Assessment: ");
        selectLabel.setFont(new Font("Arial", Font.BOLD, 12));
        topPanel.add(selectLabel);
        
        JComboBox<String> assessmentCombo = new JComboBox<>();
        assessmentCombo.setBackground(Color.WHITE);
        List<String[]> assessments = FileHandler.getAllRecords("assessments.txt");
        for (String[] assessment : assessments) {
            if (assessment.length >= 4 && assessment[1].equals(currentModuleId)) {
                assessmentCombo.addItem(assessment[0] + " - " + assessment[2]);
            }
        }
        topPanel.add(assessmentCombo);

        String[] columns = {"Student ID", "Student Name", "Marks", "Feedback"};
        DefaultTableModel studentTableModel = new DefaultTableModel(columns, 0) {
            @Override
            public boolean isCellEditable(int row, int column) { return false; }
        };
        JTable studentTable = new JTable(studentTableModel);
        JScrollPane scrollPane = new JScrollPane(studentTable);
        styleTable(studentTable, scrollPane);

        JButton loadStudentsButton = createStyledButton("Load Students", PRIMARY_COLOR);
        loadStudentsButton.addActionListener(e -> {
            if (assessmentCombo.getSelectedItem() != null) {
                String assessmentId = ((String) assessmentCombo.getSelectedItem()).split(" - ")[0];
                loadStudentsForGrading(studentTableModel, assessmentId);
            }
        });
        topPanel.add(loadStudentsButton);
        
        panel.add(topPanel, BorderLayout.NORTH);
        panel.add(scrollPane, BorderLayout.CENTER);

        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        buttonPanel.setBackground(BG_COLOR);

        JButton enterMarksButton = createStyledButton("Enter Marks", SUCCESS_COLOR);
        enterMarksButton.addActionListener(e -> {
            int selectedRow = studentTable.getSelectedRow();
            if (selectedRow == -1) {
                JOptionPane.showMessageDialog(this, "Please select a student", "No Selection", JOptionPane.WARNING_MESSAGE);
                return;
            }

            if (assessmentCombo.getSelectedItem() == null) {
                JOptionPane.showMessageDialog(this, "Please select an assessment", "No Selection", JOptionPane.WARNING_MESSAGE);
                return;
            }

            String assessmentId = ((String) assessmentCombo.getSelectedItem()).split(" - ")[0];
            String studentId = (String) studentTableModel.getValueAt(selectedRow, 0);
            String studentName = (String) studentTableModel.getValueAt(selectedRow, 1);
            
            showEnterMarksDialog(assessmentId, studentId, studentName, studentTableModel, selectedRow);
        });

        buttonPanel.add(enterMarksButton);
        panel.add(buttonPanel, BorderLayout.SOUTH);

        return panel;
    }

    // NEW: Feedback Panel
    private JPanel createFeedbackPanel() {
        JPanel panel = new JPanel(new BorderLayout(10, 10));
        panel.setBorder(BorderFactory.createEmptyBorder(15, 15, 15, 15));
        panel.setBackground(BG_COLOR);

        String[] columns = {"Student Name", "Context", "Message"};
        feedbackTableModel = new DefaultTableModel(columns, 0) {
            @Override
            public boolean isCellEditable(int row, int column) { return false; }
        };
        
        feedbackTable = new JTable(feedbackTableModel);
        JScrollPane scrollPane = new JScrollPane(feedbackTable);
        styleTable(feedbackTable, scrollPane);
        
        // Increase row height for messages
        feedbackTable.setRowHeight(40);
        feedbackTable.getColumnModel().getColumn(2).setPreferredWidth(400);

        panel.add(scrollPane, BorderLayout.CENTER);

        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        buttonPanel.setBackground(BG_COLOR);

        JButton refreshButton = createStyledButton("Refresh", PRIMARY_COLOR);
        refreshButton.addActionListener(e -> loadFeedbackData());

        buttonPanel.add(refreshButton);
        panel.add(buttonPanel, BorderLayout.SOUTH);
        return panel;
    }

    private void loadFeedbackData() {
        feedbackTableModel.setRowCount(0);
        List<String[]> comments = FileHandler.getAllRecords("comments.txt");
        List<String[]> users = FileHandler.getAllRecords("users.txt");
        List<String[]> assessments = FileHandler.getAllRecords("assessments.txt");

        for (String[] comment : comments) {
            // Format: StudentID | LecturerID | AssessmentID | Message
            if (comment.length >= 4 && comment[1].equals(currentLecturerId)) {
                
                String studentName = "Unknown";
                for(String[] u : users) {
                    if(u[0].equals(comment[0])) { studentName = u[3]; break; }
                }

                String context = "General";
                if(!comment[2].equals("General")) {
                    for(String[] ass : assessments) {
                        if(ass[0].equals(comment[2])) { context = ass[2] + " (" + ass[0] + ")"; break; }
                    }
                }

                feedbackTableModel.addRow(new Object[]{studentName, context, comment[3]});
            }
        }
    }

    private void loadStudentsForGrading(DefaultTableModel tableModel, String assessmentId) {
        tableModel.setRowCount(0);
        List<String[]> students = FileHandler.getAllRecords("users.txt");
        List<String[]> results = FileHandler.getAllRecords("results.txt");

        for (String[] student : students) {
            if (student.length >= 9 && student[2].equals("Student")) {
                String marks = "";
                String feedback = "";
                
                for (String[] result : results) {
                    if (result.length >= 4 && result[0].equals(assessmentId) && result[1].equals(student[0])) {
                        marks = result[2];
                        feedback = result[3];
                        break;
                    }
                }
                tableModel.addRow(new Object[]{student[0], student[3], marks, feedback});
            }
        }
    }

    private void showEnterMarksDialog(String assessmentId, String studentId, String studentName, 
                                      DefaultTableModel tableModel, int row) {
        JDialog dialog = new JDialog(this, "Enter Marks for " + studentName, true);
        dialog.setSize(400, 300);
        dialog.setLocationRelativeTo(this);

        JPanel panel = new JPanel(new GridBagLayout());
        panel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
        panel.setBackground(BG_COLOR);

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(10, 10, 10, 10);
        gbc.fill = GridBagConstraints.HORIZONTAL;

        JTextField marksField = new JTextField(15);
        JTextArea feedbackArea = new JTextArea(5, 15);
        feedbackArea.setLineWrap(true);
        feedbackArea.setWrapStyleWord(true);
        feedbackArea.setBorder(BorderFactory.createLineBorder(Color.LIGHT_GRAY));
        JScrollPane feedbackScroll = new JScrollPane(feedbackArea);

        gbc.gridx = 0; gbc.gridy = 0;
        panel.add(new JLabel("Marks:"), gbc);
        gbc.gridx = 1;
        panel.add(marksField, gbc);

        gbc.gridx = 0; gbc.gridy = 1;
        panel.add(new JLabel("Feedback:"), gbc);
        gbc.gridx = 1;
        panel.add(feedbackScroll, gbc);

        JPanel buttonPanel = new JPanel();
        buttonPanel.setBackground(BG_COLOR);
        
        JButton saveButton = createStyledButton("Save", SUCCESS_COLOR);
        saveButton.addActionListener(e -> {
            String marks = marksField.getText().trim();
            String feedback = feedbackArea.getText().trim();

            if (marks.isEmpty()) {
                JOptionPane.showMessageDialog(dialog, "Marks field is required!", "Error", JOptionPane.ERROR_MESSAGE);
                return;
            }

            try { Double.parseDouble(marks); } 
            catch (NumberFormatException ex) {
                JOptionPane.showMessageDialog(dialog, "Marks must be a number!", "Error", JOptionPane.ERROR_MESSAGE);
                return;
            }

            boolean exists = false;
            List<String[]> results = FileHandler.getAllRecords("results.txt");
            for (String[] result : results) {
                if (result.length >= 2 && result[0].equals(assessmentId) && result[1].equals(studentId)) {
                    exists = true;
                    FileHandler.updateLine("results.txt", 0, assessmentId, 
                        assessmentId, studentId, marks, feedback);
                    break;
                }
            }

            if (!exists) {
                FileHandler.appendLine("results.txt", assessmentId, studentId, marks, feedback);
            }

            tableModel.setValueAt(marks, row, 2);
            tableModel.setValueAt(feedback, row, 3);
            
            dialog.dispose();
            JOptionPane.showMessageDialog(this, "Marks saved successfully!", "Success", JOptionPane.INFORMATION_MESSAGE);
        });

        JButton cancelButton = createStyledButton("Cancel", Color.GRAY);
        cancelButton.addActionListener(e -> dialog.dispose());

        buttonPanel.add(saveButton);
        buttonPanel.add(cancelButton);

        gbc.gridx = 0; gbc.gridy = 2; gbc.gridwidth = 2;
        panel.add(buttonPanel, gbc);

        dialog.add(panel);
        dialog.setVisible(true);
    }

    private void loadData() {
        loadAssessmentData();
        loadFeedbackData();
    }

    private void loadAssessmentData() {
        if (currentModuleId == null) return;
        
        assessmentTableModel.setRowCount(0);
        List<String[]> assessments = FileHandler.getAllRecords("assessments.txt");
        for (String[] assessment : assessments) {
            if (assessment.length >= 4 && assessment[1].equals(currentModuleId)) {
                assessmentTableModel.addRow(assessment);
            }
        }
    }

    private void showAddAssessmentDialog() {
        JDialog dialog = new JDialog(this, "Add New Assessment", true);
        dialog.setSize(400, 300);
        dialog.setLocationRelativeTo(this);

        JPanel panel = new JPanel(new GridBagLayout());
        panel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
        panel.setBackground(BG_COLOR);

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(10, 10, 10, 10);
        gbc.fill = GridBagConstraints.HORIZONTAL;

        JTextField titleField = new JTextField(20);
        JComboBox<String> typeCombo = new JComboBox<>(new String[]{"Quiz", "Assignment", "Exam", "Project"});
        typeCombo.setBackground(Color.WHITE);
        JTextField maxMarksField = new JTextField(20);

        gbc.gridx = 0; gbc.gridy = 0;
        panel.add(new JLabel("Title:"), gbc);
        gbc.gridx = 1;
        panel.add(titleField, gbc);

        gbc.gridx = 0; gbc.gridy = 1;
        panel.add(new JLabel("Type:"), gbc);
        gbc.gridx = 1;
        panel.add(typeCombo, gbc);

        gbc.gridx = 0; gbc.gridy = 2;
        panel.add(new JLabel("Max Marks:"), gbc);
        gbc.gridx = 1;
        panel.add(maxMarksField, gbc);

        JPanel buttonPanel = new JPanel();
        buttonPanel.setBackground(BG_COLOR);
        
        JButton saveButton = createStyledButton("Save", SUCCESS_COLOR);
        saveButton.addActionListener(e -> {
            String title = titleField.getText().trim();
            String type = (String) typeCombo.getSelectedItem();
            String maxMarks = maxMarksField.getText().trim();

            if (title.isEmpty() || maxMarks.isEmpty()) {
                JOptionPane.showMessageDialog(dialog, "All fields are required!", "Error", JOptionPane.ERROR_MESSAGE);
                return;
            }

            String assessmentId = generateAssessmentId();
            FileHandler.appendLine("assessments.txt", assessmentId, currentModuleId, title, maxMarks);
            loadAssessmentData();
            dialog.dispose();
            JOptionPane.showMessageDialog(this, "Assessment added successfully!", "Success", JOptionPane.INFORMATION_MESSAGE);
        });

        JButton cancelButton = createStyledButton("Cancel", Color.GRAY);
        cancelButton.addActionListener(e -> dialog.dispose());

        buttonPanel.add(saveButton);
        buttonPanel.add(cancelButton);

        gbc.gridx = 0; gbc.gridy = 3; gbc.gridwidth = 2;
        panel.add(buttonPanel, gbc);

        dialog.add(panel);
        dialog.setVisible(true);
    }

    private String generateAssessmentId() {
        List<String[]> assessments = FileHandler.getAllRecords("assessments.txt");
        int maxNum = 0;
        
        for (String[] assessment : assessments) {
            if (assessment[0].startsWith("ASS")) {
                try {
                    int num = Integer.parseInt(assessment[0].substring(3));
                    if (num > maxNum) maxNum = num;
                } catch (NumberFormatException e) { }
            }
        }
        return "ASS" + String.format("%03d", maxNum + 1);
    }
}