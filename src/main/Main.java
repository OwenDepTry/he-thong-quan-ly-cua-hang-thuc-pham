package main;

import javax.swing.SwingUtilities;
import ui.AppTheme;
import ui.LoginFrame;

public class Main {
    public static void main(String[] args) {
        AppTheme.installLookAndFeel();
        SwingUtilities.invokeLater(() -> {
            new LoginFrame().setVisible(true);
        });
    }
}