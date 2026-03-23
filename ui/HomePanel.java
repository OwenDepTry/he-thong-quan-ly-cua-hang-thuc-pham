package ui;

import java.awt.BorderLayout;
import java.awt.Graphics;
import java.awt.Image;
import java.net.URL;
import javax.swing.ImageIcon;
import javax.swing.JPanel;

public class HomePanel extends JPanel {

    private Image background;

    public HomePanel(String role) {
        setLayout(new BorderLayout());
        setBackground(AppTheme.BG_MAIN);

        URL imageUrl = getClass().getResource("/images/home-food.png");
        if (imageUrl != null) {
            background = new ImageIcon(imageUrl).getImage();
        }

        JPanel canvas = new JPanel() {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                if (background != null) {
                    g.drawImage(background, 0, 0, getWidth(), getHeight(), this);
                }
            }
        };
        canvas.setOpaque(false);
        add(canvas, BorderLayout.CENTER);
    }
}