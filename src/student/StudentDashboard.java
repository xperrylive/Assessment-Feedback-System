package student;

import common.*;
import utils.*;

import javax.swing.*;
import javax.swing.table.*;
import java.awt.*;
import java.util.List;
import java.util.ArrayList;

public class StudentDashboard extends JFrame {
    private JTabbedPane tabbedPane;
    private JTable classTable;
    private JTable resultsTable;
    private DefaultTableModel classTableModel;
    private DefaultTableModel resultsTableModel;
    private String currentStudentId;

    // UI Constants
    private final Color PRIMARY_COLOR = new Color(52, 152, 219);
    private final Color DANGER_COLOR = new Color(231, 76, 60);
    private final Color SUCCESS_COLOR = new Color(46, 204, 113);
    private final Color HEADER_COLOR = new Color(41, 128, 185);
    private final Color BG_COLOR = Color.WHITE;

    public StudentDashboard() {
        currentStudentId = Session.getInstance().getCurrentUser().getId();
        setTitle("Student Dashboard - " + Session.getInstance().getCurrentUser().getFullName());
        setSize(1000, 650);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);

        initComponents();
        loadData();
    }

    private void initComponents() {
        setLayout(new BorderLayout());
        
        JPanel topPanel = new JPanel(new BorderLayout());
        topPanel.setBackground(BG_COLOR);
        topPanel.setBorder(BorderFactory.createEmptyBorder(15, 15, 15, 15));
        
        JLabel welcomeLabel = new JLabel("Welcome, " + Session.getInstance().getCurrentUser().getFullName());
        welcomeLabel.setFont(new Font("Arial", Font.BOLD, 16));
        welcomeLabel.setForeground(HEADER_COLOR);
        topPanel.add(welcomeLabel, BorderLayout.WEST);
        
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
        
        tabbedPane = new JTabbedPane();
        tabbedPane.setFont(new Font("Arial", Font.PLAIN, 14));
        tabbedPane.setBackground(BG_COLOR);

        tabbedPane.addTab("Enroll in Classes", createEnrollmentPanel());
        tabbedPane.addTab("My Results", createResultsPanel());

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

    private JPanel createEnrollmentPanel() {
        JPanel panel = new JPanel(new BorderLayout(10, 10));
        panel.setBorder(BorderFactory.createEmptyBorder(15, 15, 15, 15));
        panel.setBackground(BG_COLOR);

        String[] columns = {"Class ID", "Class Name", "Module ID", "Status"};
        classTableModel = new DefaultTableModel(columns, 0) {
            @Override
            public boolean isCellEditable(int row, int column) { return false; }
        };
        classTable = new JTable(classTableModel);
        JScrollPane scrollPane = new JScrollPane(classTable);
        styleTable(classTable, scrollPane);

        panel.add(scrollPane, BorderLayout.CENTER);

        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        buttonPanel.setBackground(BG_COLOR);

        JButton enrollButton = createStyledButton("Enroll", SUCCESS_COLOR);
        enrollButton.addActionListener(e -> enrollInClass());

        JButton refreshButton = createStyledButton("Refresh", PRIMARY_COLOR);
        refreshButton.addActionListener(e -> loadClassData());

        buttonPanel.add(enrollButton);
        buttonPanel.add(refreshButton);
        panel.add(buttonPanel, BorderLayout.SOUTH);
        return panel;
    }

    private JPanel createResultsPanel() {
        JPanel panel = new JPanel(new BorderLayout(10, 10));
        panel.setBorder(BorderFactory.createEmptyBorder(15, 15, 15, 15));
        panel.setBackground(BG_COLOR);

        String[] columns = {"Assessment ID", "Assessment Title", "Marks", "Max Marks", "Grade", "Feedback"};
        resultsTableModel = new DefaultTableModel(columns, 0) {
            @Override
            public boolean isCellEditable(int row, int column) { return false; }
        };
        resultsTable = new JTable(resultsTableModel);
        JScrollPane scrollPane = new JScrollPane(resultsTable);
        styleTable(resultsTable, scrollPane);

        TableColumnModel columnModel = resultsTable.getColumnModel();
        columnModel.getColumn(5).setPreferredWidth(200);

        panel.add(scrollPane, BorderLayout.CENTER);

        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        buttonPanel.setBackground(BG_COLOR);

        JButton refreshButton = createStyledButton("Refresh", PRIMARY_COLOR);
        refreshButton.addActionListener(e -> loadResultsData());

        JButton commentButton = createStyledButton("Send Feedback to Lecturer", new Color(155, 89, 182));
        commentButton.addActionListener(e -> showFeedbackDialog());

        buttonPanel.add(refreshButton);
        buttonPanel.add(commentButton);
        panel.add(buttonPanel, BorderLayout.SOUTH);
        return panel;
    }

    private void showFeedbackDialog() {
        JDialog dialog = new JDialog(this, "Send Feedback to Lecturer", true);
        dialog.setSize(450, 400);
        dialog.setLocationRelativeTo(this);
        
        JPanel panel = new JPanel(new GridBagLayout());
        panel.setBackground(Color.WHITE);
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(5,5,5,5);
        gbc.fill = GridBagConstraints.HORIZONTAL;

        // 1. Select Lecturer
        JComboBox<String> lecturerCombo = new JComboBox<>();
        List<String[]> enrollments = FileHandler.getAllRecords("enrollments.txt");
        List<String[]> classes = FileHandler.getAllRecords("classes.txt");
        List<String[]> modules = FileHandler.getAllRecords("modules.txt");
        List<String[]> users = FileHandler.getAllRecords("users.txt");
        
        // Find Lecturers for enrolled classes
        for(String[] enroll : enrollments) {
            if(enroll[0].equals(currentStudentId)) {
                for(String[] cls : classes) {
                    if(cls[0].equals(enroll[1])) {
                        for(String[] mod : modules) {
                            if(mod[0].equals(cls[2])) {
                                String lecturerId = mod[2];
                                for(String[] u : users) {
                                    if(u[0].equals(lecturerId)) {
                                        String item = u[0] + " - " + u[3] + " (" + mod[1] + ")"; // ID - Name (Module)
                                        boolean exists = false;
                                        for(int i=0; i<lecturerCombo.getItemCount(); i++) 
                                            if(lecturerCombo.getItemAt(i).equals(item)) exists = true;
                                        if(!exists) lecturerCombo.addItem(item);
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }

        // 2. Select Context (General or Specific Assessment)
        JComboBox<String> contextCombo = new JComboBox<>();
        contextCombo.addItem("General Comment"); // Default
        
        // Logic to update assessment combo when lecturer changes
        lecturerCombo.addActionListener(e -> {
            contextCombo.removeAllItems();
            contextCombo.addItem("General Comment");
            
            if(lecturerCombo.getSelectedItem() != null) {
                // Parse selected lecturer to get module name (it's in brackets)
                String selection = (String)lecturerCombo.getSelectedItem();
                // Extract module name from "ID - Name (ModuleName)"
                String moduleName = selection.substring(selection.indexOf("(") + 1, selection.indexOf(")"));
                
                // Find Module ID
                String moduleId = "";
                for(String[] m : modules) {
                    if(m[1].equals(moduleName)) { moduleId = m[0]; break; }
                }
                
                // Load assessments for this module
                List<String[]> assessments = FileHandler.getAllRecords("assessments.txt");
                for(String[] ass : assessments) {
                    if(ass[1].equals(moduleId)) {
                        contextCombo.addItem(ass[0] + " - " + ass[2]);
                    }
                }
            }
        });
        
        // Trigger manually once to load initial data
        if(lecturerCombo.getItemCount() > 0) lecturerCombo.setSelectedIndex(0);

        JTextArea msgArea = new JTextArea(5, 20);
        msgArea.setBorder(BorderFactory.createLineBorder(Color.LIGHT_GRAY));

        gbc.gridx=0; gbc.gridy=0; panel.add(new JLabel("Select Lecturer:"), gbc);
        gbc.gridx=1; panel.add(lecturerCombo, gbc);
        
        gbc.gridx=0; gbc.gridy=1; panel.add(new JLabel("Topic:"), gbc);
        gbc.gridx=1; panel.add(contextCombo, gbc);
        
        gbc.gridx=0; gbc.gridy=2; panel.add(new JLabel("Message:"), gbc);
        gbc.gridx=1; panel.add(new JScrollPane(msgArea), gbc);

        JButton sendBtn = createStyledButton("Send", SUCCESS_COLOR);
        sendBtn.addActionListener(e -> {
            if(lecturerCombo.getSelectedItem() == null) return;
            String lecId = ((String)lecturerCombo.getSelectedItem()).split(" - ")[0];
            String msg = msgArea.getText().trim();
            
            String context = (String)contextCombo.getSelectedItem();
            String contextId = "General";
            if(context != null && !context.equals("General Comment")) {
                contextId = context.split(" - ")[0]; // Get Assessment ID
            }

            if(msg.isEmpty()) { JOptionPane.showMessageDialog(dialog, "Enter a message"); return; }
            
            // Format: StudentID | LecturerID | AssessmentID | Message
            FileHandler.appendLine("comments.txt", currentStudentId, lecId, contextId, msg);
            JOptionPane.showMessageDialog(dialog, "Feedback sent successfully!");
            dialog.dispose();
        });

        gbc.gridx=0; gbc.gridy=3; gbc.gridwidth=2; panel.add(sendBtn, gbc);
        dialog.add(panel);
        dialog.setVisible(true);
    }

    private void loadData() {
        loadClassData();
        loadResultsData();
    }

    private void loadClassData() {
        classTableModel.setRowCount(0);
        List<String[]> classes = FileHandler.getAllRecords("classes.txt");
        List<String[]> enrollments = FileHandler.getAllRecords("enrollments.txt");

        for (String[] cls : classes) {
            if (cls.length >= 3) {
                boolean enrolled = false;
                for (String[] enrollment : enrollments) {
                    if (enrollment.length >= 2 && 
                        enrollment[0].equals(currentStudentId) && 
                        enrollment[1].equals(cls[0])) {
                        enrolled = true;
                        break;
                    }
                }
                
                String status = enrolled ? "Enrolled" : "Not Enrolled";
                classTableModel.addRow(new Object[]{cls[0], cls[1], cls[2], status});
            }
        }
    }

    private void loadResultsData() {
        resultsTableModel.setRowCount(0);
        List<String[]> results = FileHandler.getAllRecords("results.txt");
        List<String[]> assessments = FileHandler.getAllRecords("assessments.txt");
        List<String[]> grading = FileHandler.getAllRecords("grading.txt");

        for (String[] result : results) {
            if (result.length >= 4 && result[1].equals(currentStudentId)) {
                String assessmentId = result[0];
                String marks = result[2];
                String feedback = result[3];
                
                String assessmentTitle = "";
                String maxMarks = "";
                
                for (String[] assessment : assessments) {
                    if (assessment.length >= 4 && assessment[0].equals(assessmentId)) {
                        assessmentTitle = assessment[2];
                        maxMarks = assessment[3];
                        break;
                    }
                }
                
                String grade = calculateGrade(marks, grading);
                
                resultsTableModel.addRow(new Object[]{
                    assessmentId, assessmentTitle, marks, maxMarks, grade, feedback
                });
            }
        }
    }

    private String calculateGrade(String marksStr, List<String[]> grading) {
        try {
            double marks = Double.parseDouble(marksStr);
            for (String[] grade : grading) {
                if (grade.length >= 3) {
                    double min = Double.parseDouble(grade[1]);
                    double max = Double.parseDouble(grade[2]);
                    if (marks >= min && marks <= max) return grade[0];
                }
            }
        } catch (NumberFormatException e) { }
        return "N/A";
    }

    private void enrollInClass() {
        int selectedRow = classTable.getSelectedRow();
        if (selectedRow == -1) {
            JOptionPane.showMessageDialog(this, "Please select a class to enroll", 
                "No Selection", JOptionPane.WARNING_MESSAGE);
            return;
        }

        String status = (String) classTableModel.getValueAt(selectedRow, 3);
        if (status.equals("Enrolled")) {
            JOptionPane.showMessageDialog(this, "You are already enrolled in this class", 
                "Already Enrolled", JOptionPane.INFORMATION_MESSAGE);
            return;
        }

        String classId = (String) classTableModel.getValueAt(selectedRow, 0);
        String className = (String) classTableModel.getValueAt(selectedRow, 1);

        int confirm = JOptionPane.showConfirmDialog(this, 
            "Do you want to enroll in " + className + "?", 
            "Confirm Enrollment", JOptionPane.YES_NO_OPTION);
        
        if (confirm == JOptionPane.YES_OPTION) {
            FileHandler.appendLine("enrollments.txt", currentStudentId, classId);
            loadClassData();
            JOptionPane.showMessageDialog(this, "Enrolled successfully!", 
                "Success", JOptionPane.INFORMATION_MESSAGE);
        }
    }
}