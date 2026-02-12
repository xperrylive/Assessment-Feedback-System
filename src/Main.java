import common.LoginFrame;
import utils.DataSeeder;

import javax.swing.*;

public class Main {
    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            try {
                UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
            } catch (Exception e) {
                e.printStackTrace();
            }
            
            DataSeeder.seedIfEmpty();
            new LoginFrame().setVisible(true);
        });
    }
}
