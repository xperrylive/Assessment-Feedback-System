import javax.swing.*;
import javax.swing.table.*;
import java.awt.*;
import java.awt.event.*;
import java.util.List;
import java.util.ArrayList;

/**
 * StudentDashboard - Student interface
 * Features:
 * 1. Register for Classes (Enrollment)
 * 2. View Results & Feedback
 * 3. Provide Comments/Feedback to Lecturers
 */
public class StudentDashboard extends JFrame {

    private JTabbedPane tabbedPane;
    private JTable classTable;
    private DefaultTableModel classTableModel;
    private JTable resultsTable;
    private DefaultTableModel resultsTableModel;
    private JTextArea feedbackArea;
    private JComboBox<String> lecturerCombo;

    // File constants
    private static final String CLASSES_FILE = "classes.txt";
    private static final String ENROLLMENTS_FILE = "enrollments.txt";
    private static final String ASSESSMENTS_FILE = "assessments.txt";
    private static final String RESULTS_FILE = "results.txt";
    private static final String MODULES_FILE = "modules.txt";
    private static final String USERS_FILE = "users.txt";
    private static final String FEEDBACK_FILE = "student_feedback.txt";

    private User currentStudent;

    public StudentDashboard() {
        this.currentStudent = Session.getInstance().getCurrentUser();
        initializeUI();
        loadAvailableClasses();
        loadStudentResults();
        populateLecturerCombo();
    }

    private void initializeUI() {
        setTitle("Student Dashboard - Assessment Feedback System");
        setSize(900, 600);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);

        JPanel mainPanel = new JPanel(new BorderLayout());

        // Header
        JPanel headerPanel = createHeaderPanel();
        mainPanel.add(headerPanel, BorderLayout.NORTH);

        // Tabbed Pane
        tabbedPane = new JTabbedPane();
        tabbedPane.setFont(new Font("Arial", Font.PLAIN, 14));

        tabbedPane.addTab("Register for Classes", createEnrollmentPanel());
        tabbedPane.addTab("My Results", createResultsPanel());
        tabbedPane.addTab("Feedback to Lecturers", createFeedbackPanel());

        mainPanel.add(tabbedPane, BorderLayout.CENTER);
        add(mainPanel);
    }

    private JPanel createHeaderPanel() {
        JPanel headerPanel = new JPanel(new BorderLayout());
        headerPanel.setBackground(new Color(39, 174, 96)); // Green theme for students
        headerPanel.setPreferredSize(new Dimension(900, 60));
        headerPanel.setBorder(BorderFactory.createEmptyBorder(10, 20, 10, 20));

        JLabel welcomeLabel = new JLabel("Welcome, " + currentStudent.getFullName() + " (Student)");
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

    // --- TAB 1: ENROLLMENT ---
    private JPanel createEnrollmentPanel() {
        JPanel panel = new JPanel(new BorderLayout(10, 10));
        panel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        JLabel infoLabel = new JLabel("Available Classes for Enrollment:");
        infoLabel.setFont(new Font("Arial", Font.BOLD, 14));

        String[] columns = {"Class ID", "Module Code", "Intake", "Enrollment Status"};
        classTableModel = new DefaultTableModel(columns, 0) {
            @Override
            public boolean isCellEditable(int row, int col) { return false; }
        };
        classTable = new JTable(classTableModel);
        classTable.setRowHeight(25);

        JScrollPane scrollPane = new JScrollPane(classTable);

        JButton enrollButton = new JButton("Enroll in Selected Class");
        enrollButton.setBackground(new Color(46, 204, 113));
        enrollButton.setForeground(Color.WHITE);
        enrollButton.setFont(new Font("Arial", Font.BOLD, 13));

        enrollButton.addActionListener(e -> enrollInClass());

        JButton refreshButton = new JButton("Refresh");
        refreshButton.addActionListener(e -> loadAvailableClasses());

        JPanel btnPanel = new JPanel();
        btnPanel.add(enrollButton);
        btnPanel.add(refreshButton);

        panel.add(infoLabel, BorderLayout.NORTH);
        panel.add(scrollPane, BorderLayout.CENTER);
        panel.add(btnPanel, BorderLayout.SOUTH);

        return panel;
    }

    // --- TAB 2: RESULTS ---
    private JPanel createResultsPanel() {
        JPanel panel = new JPanel(new BorderLayout(10, 10));
        panel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        JLabel infoLabel = new JLabel("My Assessment Results:");
        infoLabel.setFont(new Font("Arial", Font.BOLD, 14));

        String[] columns = {"Assessment ID", "Title", "Module", "Marks", "Lecturer Feedback"};
        resultsTableModel = new DefaultTableModel(columns, 0) {
            @Override
            public boolean isCellEditable(int row, int col) { return false; }
        };
        resultsTable = new JTable(resultsTableModel);
        resultsTable.setRowHeight(30);
        // Make feedback column wider
        resultsTable.getColumnModel().getColumn(4).setPreferredWidth(300);

        JScrollPane scrollPane = new JScrollPane(resultsTable);

        JButton refreshButton = new JButton("Refresh Results");
        refreshButton.addActionListener(e -> loadStudentResults());

        panel.add(infoLabel, BorderLayout.NORTH);
        panel.add(scrollPane, BorderLayout.CENTER);
        panel.add(refreshButton, BorderLayout.SOUTH);

        return panel;
    }

    // --- TAB 3: FEEDBACK TO LECTURER ---
    private JPanel createFeedbackPanel() {
        JPanel panel = new JPanel(new GridBagLayout());
        panel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(10, 10, 10, 10);
        gbc.fill = GridBagConstraints.HORIZONTAL;

        JLabel selectLabel = new JLabel("Select Lecturer (Module):");
        lecturerCombo = new JComboBox<>();

        JLabel commentLabel = new JLabel("Your Comments:");
        feedbackArea = new JTextArea(8, 40);
        feedbackArea.setLineWrap(true);
        JScrollPane areaScroll = new JScrollPane(feedbackArea);

        JButton submitButton = new JButton("Submit Feedback");
        submitButton.setBackground(new Color(52, 152, 219));
        submitButton.setForeground(Color.WHITE);
        submitButton.addActionListener(e -> submitFeedback());

        // Layout
        gbc.gridx = 0; gbc.gridy = 0;
        panel.add(selectLabel, gbc);

        gbc.gridx = 1; gbc.gridy = 0;
        panel.add(lecturerCombo, gbc);

        gbc.gridx = 0; gbc.gridy = 1; gbc.anchor = GridBagConstraints.NORTHWEST;
        panel.add(commentLabel, gbc);

        gbc.gridx = 1; gbc.gridy = 1;
        panel.add(areaScroll, gbc);

        gbc.gridx = 1; gbc.gridy = 2;
        panel.add(submitButton, gbc);

        // Wrap in a BorderLayout to push it to top
        JPanel wrapper = new JPanel(new BorderLayout());
        wrapper.add(panel, BorderLayout.NORTH);
        return wrapper;
    }

    // --- LOGIC METHODS ---

    private void loadAvailableClasses() {
        classTableModel.setRowCount(0);
        List<String[]> classes = FileHandler.getAllRecords(CLASSES_FILE);
        List<String[]> enrollments = FileHandler.getAllRecords(ENROLLMENTS_FILE);

        for (String[] cls : classes) {
            if (cls.length >= 3) { // ClassID|ModuleCode|Intake
                String classId = cls[0];
                String status = "Available";

                // Check if already enrolled
                for (String[] enr : enrollments) {
                    if (enr.length >= 3 && enr[1].equals(currentStudent.getId()) && enr[2].equals(classId)) {
                        status = "Enrolled";
                        break;
                    }
                }

                classTableModel.addRow(new Object[]{classId, cls[1], cls[2], status});
            }
        }
    }

    private void enrollInClass() {
        int selectedRow = classTable.getSelectedRow();
        if (selectedRow == -1) {
            JOptionPane.showMessageDialog(this, "Please select a class first.");
            return;
        }

        String status = (String) classTable.getValueAt(selectedRow, 3);
        if ("Enrolled".equals(status)) {
            JOptionPane.showMessageDialog(this, "You are already enrolled in this class!");
            return;
        }

        String classId = (String) classTable.getValueAt(selectedRow, 0);
        String enrollmentId = "ENR" + System.currentTimeMillis();

        // Schema: EnrollmentID|StudentID|ClassID
        FileHandler.appendLine(ENROLLMENTS_FILE, enrollmentId, currentStudent.getId(), classId);

        JOptionPane.showMessageDialog(this, "Successfully enrolled!");
        loadAvailableClasses();
    }

    private void loadStudentResults() {
        resultsTableModel.setRowCount(0);
        List<String[]> results = FileHandler.getAllRecords(RESULTS_FILE);
        List<String[]> assessments = FileHandler.getAllRecords(ASSESSMENTS_FILE);

        // Filter results for this student
        for (String[] res : results) {
            if (res.length >= 5 && res[2].equals(currentStudent.getId())) {
                String assessID = res[1];
                String marks = res[3];
                String feedback = res[4];

                // Find assessment details (Title & Module)
                String title = "Unknown";
                String module = "Unknown";

                for (String[] asm : assessments) {
                    if (asm.length >= 4 && asm[0].equals(assessID)) {
                        title = asm[3];
                        module = asm[1];
                        break;
                    }
                }

                resultsTableModel.addRow(new Object[]{assessID, title, module, marks, feedback});
            }
        }
    }

    private void populateLecturerCombo() {
        lecturerCombo.removeAllItems();
        List<String[]> modules = FileHandler.getAllRecords(MODULES_FILE);
        List<String[]> users = FileHandler.getAllRecords(USERS_FILE);

        // Show Lecturers for modules found in system
        for (String[] mod : modules) {
            if (mod.length >= 4) {
                String modCode = mod[0];
                String modName = mod[1];
                String lecID = mod[3];

                // Find Lecturer Name
                String lecName = "Unknown";
                for (String[] u : users) {
                    if (u[0].equals(lecID)) {
                        lecName = u[4];
                        break;
                    }
                }

                // Format: ModuleCode - LecturerName (ID)
                if (!lecID.equals("N/A")) {
                    lecturerCombo.addItem(modCode + " : " + modName + " - " + lecName + " (" + lecID + ")");
                }
            }
        }
    }

    private void submitFeedback() {
        String selected = (String) lecturerCombo.getSelectedItem();
        String comments = feedbackArea.getText().trim();

        if (selected == null || comments.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Please select a lecturer and enter comments.");
            return;
        }

        // Extract Lecturer ID from string "Module - Name (ID)"
        String lecturerID = selected.substring(selected.lastIndexOf("(") + 1, selected.lastIndexOf(")"));
        String feedbackID = "FB" + System.currentTimeMillis();

        // Schema: FeedbackID|StudentID|LecturerID|Comment
        // Note: Removing newlines from comments to keep it on one line in text file
        String safeComment = comments.replace("\n", " ");

        FileHandler.appendLine(FEEDBACK_FILE, feedbackID, currentStudent.getId(), lecturerID, safeComment);

        JOptionPane.showMessageDialog(this, "Feedback sent successfully!");
        feedbackArea.setText("");
    }

    private void logout() {
        Session.getInstance().logout();
        new LoginFrame().setVisible(true);
        dispose();
    }
}