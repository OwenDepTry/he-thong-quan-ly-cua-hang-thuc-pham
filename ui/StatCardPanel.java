package ui;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Font;
import javax.swing.BorderFactory;
import javax.swing.JLabel;
import javax.swing.JPanel;

public class StatCardPanel extends JPanel {

    private final JLabel lblValue;

    public StatCardPanel(String title, String value) {
        setLayout(new BorderLayout());
        setBackground(Color.WHITE);
        setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(210, 210, 210)),
                BorderFactory.createEmptyBorder(12, 14, 12, 14)
        ));

        JLabel lblTitle = new JLabel(title);
        lblTitle.setFont(new Font("Arial", Font.BOLD, 16));
        lblTitle.setForeground(new Color(45, 45, 45));

        lblValue = new JLabel(value);
        lblValue.setFont(new Font("Arial", Font.BOLD, 26));
        lblValue.setForeground(new Color(20, 120, 55));

        add(lblTitle, BorderLayout.NORTH);
        add(lblValue, BorderLayout.CENTER);
    }

    public void setValue(String value) {
        lblValue.setText(value);
    }
}