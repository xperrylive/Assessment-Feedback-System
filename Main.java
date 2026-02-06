import javax.swing.*;

/**
 * Main - Application entry point
 * Initializes data seeding and launches LoginFrame
 */
public class Main {
    
    public static void main(String[] args) {
        // Set look and feel to system default
        try {
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
        } catch (Exception e) {
            e.printStackTrace();
        }
        
        // Seed data if files are empty
        DataSeeder.seedIfEmpty();
        
        // Launch login screen on Event Dispatch Thread
        SwingUtilities.invokeLater(() -> {
            LoginFrame loginFrame = new LoginFrame();
            loginFrame.setVisible(true);
        });
    }
}
