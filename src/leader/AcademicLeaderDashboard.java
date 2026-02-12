package leader;

import common.*;
import utils.*;

import javax.swing.*;
import javax.swing.table.*;
import java.awt.*;
import java.util.*;
import java.util.List;

public class AcademicLeaderDashboard extends JFrame {
    private JTabbedPane tabbedPane;
    private JTable lecturerTable;
    private JTable moduleTable;
    private DefaultTableModel lecturerTableModel;
    private DefaultTableModel moduleTableModel;
    private String currentLeaderId;

    // UI Constants
    private final Color PRIMARY_COLOR = new Color(52, 152, 219);
    private final Color DANGER_COLOR = new Color(231, 76, 60);
    private final Color SUCCESS_COLOR = new Color(46, 204, 113);
    private final Color HEADER_COLOR = new Color(41, 128, 185);
    private final Color BG_COLOR = Color.WHITE;

    public AcademicLeaderDashboard() {
        currentLeaderId = Session.getInstance().getCurrentUser().getId();
        setTitle("Academic Leader Dashboard - " + Session.getInstance().getCurrentUser().getFullName());
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
        
        // --- Tabs ---
        tabbedPane = new JTabbedPane();
        tabbedPane.setFont(new Font("Arial", Font.PLAIN, 14));
        tabbedPane.setBackground(BG_COLOR);

        tabbedPane.addTab("My Lecturers", createLecturersPanel());
        tabbedPane.addTab("My Modules", createModulesPanel());
        tabbedPane.addTab("Reports", createReportsPanel());

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

    private JPanel createLecturersPanel() {
        JPanel panel = new JPanel(new BorderLayout(10, 10));
        panel.setBorder(BorderFactory.createEmptyBorder(15, 15, 15, 15));
        panel.setBackground(BG_COLOR);

        String[] columns = {"Lecturer ID", "Name", "Email", "Phone"};
        lecturerTableModel = new DefaultTableModel(columns, 0) {
            @Override
            public boolean isCellEditable(int row, int column) { return false; }
        };
        lecturerTable = new JTable(lecturerTableModel);
        JScrollPane scrollPane = new JScrollPane(lecturerTable);
        styleTable(lecturerTable, scrollPane);

        panel.add(scrollPane, BorderLayout.CENTER);

        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        buttonPanel.setBackground(BG_COLOR);

        JButton refreshButton = createStyledButton("Refresh", PRIMARY_COLOR);
        refreshButton.addActionListener(e -> loadLecturerData());

        buttonPanel.add(refreshButton);
        panel.add(buttonPanel, BorderLayout.SOUTH);
        return panel;
    }

    private JPanel createModulesPanel() {
        JPanel panel = new JPanel(new BorderLayout(10, 10));
        panel.setBorder(BorderFactory.createEmptyBorder(15, 15, 15, 15));
        panel.setBackground(BG_COLOR);

        String[] columns = {"Module ID", "Module Name", "Lecturer ID"};
        moduleTableModel = new DefaultTableModel(columns, 0) {
            @Override
            public boolean isCellEditable(int row, int column) { return false; }
        };
        moduleTable = new JTable(moduleTableModel);
        JScrollPane scrollPane = new JScrollPane(moduleTable);
        styleTable(moduleTable, scrollPane);

        panel.add(scrollPane, BorderLayout.CENTER);

        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        buttonPanel.setBackground(BG_COLOR);

        // NEW: Module Management Buttons
        JButton addButton = createStyledButton("Add Module", SUCCESS_COLOR);
        addButton.addActionListener(e -> showModuleDialog(null));

        JButton editButton = createStyledButton("Edit Module", PRIMARY_COLOR);
        editButton.addActionListener(e -> {
            int row = moduleTable.getSelectedRow();
            if(row != -1) showModuleDialog((String)moduleTableModel.getValueAt(row, 0));
            else JOptionPane.showMessageDialog(this, "Select a module to edit");
        });

        JButton deleteButton = createStyledButton("Delete Module", DANGER_COLOR);
        deleteButton.addActionListener(e -> deleteModule());

        JButton assignButton = createStyledButton("Assign Lecturer", new Color(155, 89, 182)); // Purple
        assignButton.addActionListener(e -> assignLecturer());

        buttonPanel.add(addButton);
        buttonPanel.add(editButton);
        buttonPanel.add(deleteButton);
        buttonPanel.add(assignButton);
        panel.add(buttonPanel, BorderLayout.SOUTH);
        return panel;
    }

    private JPanel createReportsPanel() {
        JPanel panel = new JPanel(new BorderLayout(10, 10));
        panel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
        panel.setBackground(BG_COLOR);

        JPanel topPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        topPanel.setBackground(BG_COLOR);

        JLabel selectLbl = new JLabel("Select Module: ");
        selectLbl.setFont(new Font("Arial", Font.BOLD, 12));
        topPanel.add(selectLbl);
        
        JComboBox<String> moduleCombo = new JComboBox<>();
        moduleCombo.setBackground(Color.WHITE);
        List<String[]> modules = FileHandler.getAllRecords("modules.txt");
        for (String[] module : modules) {
            if (module.length >= 4 && module[3].equals(currentLeaderId)) {
                moduleCombo.addItem(module[0] + " - " + module[1]);
            }
        }
        topPanel.add(moduleCombo);

        JButton generateButton = createStyledButton("Generate Report", PRIMARY_COLOR);
        generateButton.addActionListener(e -> {
            if (moduleCombo.getSelectedItem() != null) {
                String selected = (String) moduleCombo.getSelectedItem();
                String moduleId = selected.split(" - ")[0];
                generateReport(moduleId, panel);
            }
        });
        topPanel.add(generateButton);

        panel.add(topPanel, BorderLayout.NORTH);
        return panel;
    }

    private void generateReport(String moduleId, JPanel containerPanel) {
        // ... (Logic remains same as previous version, just ensuring UI is clean) ...
        List<String[]> assessments = FileHandler.getAllRecords("assessments.txt");
        List<String> assessmentIds = new ArrayList<>();
        
        for (String[] assessment : assessments) {
            if (assessment.length >= 2 && assessment[1].equals(moduleId)) {
                assessmentIds.add(assessment[0]);
            }
        }

        if (assessmentIds.isEmpty()) {
            JOptionPane.showMessageDialog(this, "No assessments found for this module", "No Data", JOptionPane.WARNING_MESSAGE);
            return;
        }

        List<String[]> results = FileHandler.getAllRecords("results.txt");
        List<Double> marks = new ArrayList<>();
        final int[] counts = new int[2];

        for (String[] result : results) {
            if (result.length >= 3 && assessmentIds.contains(result[0])) {
                try {
                    double mark = Double.parseDouble(result[2]);
                    marks.add(mark);
                    if (mark >= 50) counts[0]++; else counts[1]++;
                } catch (NumberFormatException e) { }
            }
        }
        
        // Remove old report components
        Component[] components = containerPanel.getComponents();
        for (Component comp : components) {
            if (comp instanceof JPanel && comp != containerPanel.getComponent(0)) {
                containerPanel.remove(comp);
            }
        }

        if (marks.isEmpty()) {
             JOptionPane.showMessageDialog(this, "No student results found yet.", "Info", JOptionPane.INFORMATION_MESSAGE);
             containerPanel.repaint();
             return;
        }

        double avg = marks.stream().mapToDouble(Double::doubleValue).average().orElse(0);
        double min = marks.stream().mapToDouble(Double::doubleValue).min().orElse(0);
        double max = marks.stream().mapToDouble(Double::doubleValue).max().orElse(0);

        JPanel reportPanel = new JPanel();
        reportPanel.setLayout(new BoxLayout(reportPanel, BoxLayout.Y_AXIS));
        reportPanel.setBackground(BG_COLOR);
        reportPanel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));

        JLabel statsLabel = new JLabel(String.format(
            "<html><h2>Statistics</h2>" +
            "Total Results: %d<br>Average: %.2f<br>Minimum: %.2f<br>Maximum: %.2f<br>" +
            "Pass: %d | Fail: %d</html>", 
            marks.size(), avg, min, max, counts[0], counts[1]
        ));
        statsLabel.setFont(new Font("Arial", Font.PLAIN, 14));
        reportPanel.add(statsLabel);
        reportPanel.add(Box.createVerticalStrut(20));

        JPanel chartPanel = new JPanel() {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                if (marks.isEmpty()) return;
                
                Graphics2D g2d = (Graphics2D) g;
                g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                int total = counts[0] + counts[1];
                if(total == 0) return;

                int h = getHeight() - 50;
                int barW = 60;
                
                // Pass Bar
                int h1 = (counts[0] * h) / total;
                g2d.setColor(SUCCESS_COLOR);
                g2d.fillRect(50, getHeight() - h1 - 30, barW, h1);
                g2d.setColor(Color.BLACK);
                g2d.drawString("Pass: " + counts[0], 50, getHeight() - 10);

                // Fail Bar
                int h2 = (counts[1] * h) / total;
                g2d.setColor(DANGER_COLOR);
                g2d.fillRect(150, getHeight() - h2 - 30, barW, h2);
                g2d.setColor(Color.BLACK);
                g2d.drawString("Fail: " + counts[1], 150, getHeight() - 10);
            }
        };
        chartPanel.setPreferredSize(new Dimension(400, 200));
        chartPanel.setBackground(BG_COLOR);
        chartPanel.setBorder(BorderFactory.createLineBorder(Color.LIGHT_GRAY));
        reportPanel.add(chartPanel);

        containerPanel.add(reportPanel, BorderLayout.CENTER);
        containerPanel.revalidate();
        containerPanel.repaint();
    }

    private void loadData() {
        loadLecturerData();
        loadModuleData();
    }

    private void loadLecturerData() {
        lecturerTableModel.setRowCount(0);
        List<String[]> users = FileHandler.getAllRecords("users.txt");
        for (String[] user : users) {
            if (user.length >= 10 && user[2].equals("Lecturer") && user[9].equals(currentLeaderId)) {
                lecturerTableModel.addRow(new Object[]{user[0], user[3], user[5], user[6]});
            }
        }
    }

    private void loadModuleData() {
        moduleTableModel.setRowCount(0);
        List<String[]> modules = FileHandler.getAllRecords("modules.txt");
        for (String[] module : modules) {
            if (module.length >= 4 && module[3].equals(currentLeaderId)) {
                moduleTableModel.addRow(new Object[]{module[0], module[1], module[2]});
            }
        }
    }

    // --- Module CRUD Dialogs ---
    private void showModuleDialog(String moduleIdToEdit) {
        boolean isEdit = (moduleIdToEdit != null);
        JDialog dialog = new JDialog(this, isEdit ? "Edit Module" : "Add Module", true);
        dialog.setSize(350, 250);
        dialog.setLocationRelativeTo(this);
        
        JPanel panel = new JPanel(new GridBagLayout());
        panel.setBackground(Color.WHITE);
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(5,5,5,5);
        gbc.fill = GridBagConstraints.HORIZONTAL;

        JTextField nameField = new JTextField(15);
        JTextField idField = new JTextField(15);
        if(isEdit) {
            idField.setText(moduleIdToEdit);
            idField.setEditable(false);
            // Load existing name
            List<String[]> modules = FileHandler.getAllRecords("modules.txt");
            for(String[] m : modules) {
                if(m[0].equals(moduleIdToEdit)) { nameField.setText(m[1]); break; }
            }
        }

        gbc.gridx=0; gbc.gridy=0; panel.add(new JLabel("Module ID:"), gbc);
        gbc.gridx=1; panel.add(idField, gbc);
        gbc.gridx=0; gbc.gridy=1; panel.add(new JLabel("Module Name:"), gbc);
        gbc.gridx=1; panel.add(nameField, gbc);

        JButton saveBtn = createStyledButton("Save", SUCCESS_COLOR);
        saveBtn.addActionListener(e -> {
            String mid = idField.getText().trim();
            String mname = nameField.getText().trim();
            if(mid.isEmpty() || mname.isEmpty()) {
                JOptionPane.showMessageDialog(dialog, "Fields required");
                return;
            }
            
            if(isEdit) {
                // Keep existing lecturer (index 2)
                List<String[]> modules = FileHandler.getAllRecords("modules.txt");
                String lecturer = "None";
                for(String[] m : modules) if(m[0].equals(mid)) lecturer = m[2];
                
                FileHandler.updateLine("modules.txt", 0, mid, mid, mname, lecturer, currentLeaderId);
            } else {
                FileHandler.appendLine("modules.txt", mid, mname, "None", currentLeaderId);
            }
            loadModuleData();
            dialog.dispose();
        });
        
        gbc.gridx=0; gbc.gridy=2; gbc.gridwidth=2; panel.add(saveBtn, gbc);
        dialog.add(panel);
        dialog.setVisible(true);
    }

    private void deleteModule() {
        int row = moduleTable.getSelectedRow();
        if(row == -1) return;
        String mid = (String)moduleTableModel.getValueAt(row, 0);
        if(JOptionPane.showConfirmDialog(this, "Delete " + mid + "?", "Confirm", JOptionPane.YES_NO_OPTION) == JOptionPane.YES_OPTION) {
            FileHandler.deleteLine("modules.txt", 0, mid);
            loadModuleData();
        }
    }

    private void assignLecturer() {
        int selectedRow = moduleTable.getSelectedRow();
        if (selectedRow == -1) {
            JOptionPane.showMessageDialog(this, "Please select a module", "No Selection", JOptionPane.WARNING_MESSAGE);
            return;
        }

        String moduleId = (String) moduleTableModel.getValueAt(selectedRow, 0);
        String moduleName = (String) moduleTableModel.getValueAt(selectedRow, 1);

        List<String[]> lecturers = FileHandler.getAllRecords("users.txt");
        Vector<String> lecturerList = new Vector<>();
        
        for (String[] user : lecturers) {
            if (user.length >= 10 && user[2].equals("Lecturer") && user[9].equals(currentLeaderId)) {
                lecturerList.add(user[0] + " - " + user[3]);
            }
        }

        if (lecturerList.isEmpty()) {
            JOptionPane.showMessageDialog(this, "No lecturers available under your supervision", 
                "No Lecturers", JOptionPane.WARNING_MESSAGE);
            return;
        }

        String selected = (String) JOptionPane.showInputDialog(
            this,
            "Select lecturer for " + moduleName + ":",
            "Assign Lecturer",
            JOptionPane.PLAIN_MESSAGE,
            null,
            lecturerList.toArray(),
            lecturerList.get(0)
        );

        if (selected != null) {
            String lecturerId = selected.split(" - ")[0];
            List<String[]> modules = FileHandler.getAllRecords("modules.txt");
            for (String[] module : modules) {
                if (module.length >= 4 && module[0].equals(moduleId)) {
                    FileHandler.updateLine("modules.txt", 0, moduleId, 
                        module[0], module[1], lecturerId, module[3]);
                    break;
                }
            }
            loadModuleData();
            JOptionPane.showMessageDialog(this, "Lecturer assigned successfully", "Success", JOptionPane.INFORMATION_MESSAGE);
        }
    }
}