package ui;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Font;
import javax.swing.BorderFactory;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.SwingConstants;

public class PlaceholderPanel extends JPanel {

    public PlaceholderPanel(String title) {
        setLayout(new BorderLayout());
        setBackground(Color.WHITE);
        setBorder(BorderFactory.createEmptyBorder(30, 30, 30, 30));

        JLabel lblTitle = new JLabel(title, SwingConstants.CENTER);
        lblTitle.setFont(new Font("Arial", Font.BOLD, 30));
        lblTitle.setForeground(new Color(25, 100, 45));

        JLabel lblDesc = new JLabel("Khu vực này sẽ làm chức năng sau", SwingConstants.CENTER);
        lblDesc.setFont(new Font("Arial", Font.PLAIN, 18));
        lblDesc.setForeground(new Color(100, 100, 100));

        JPanel center = new JPanel(new BorderLayout());
        center.setBackground(Color.WHITE);
        center.add(lblTitle, BorderLayout.CENTER);
        center.add(lblDesc, BorderLayout.SOUTH);

        add(center, BorderLayout.CENTER);
    }
}