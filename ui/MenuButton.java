package ui;

import java.awt.Color;
import java.awt.Cursor;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Insets;
import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.SwingConstants;

public class MenuButton extends JButton {

    private final Color normalBg = new Color(145, 233, 140);
    private final Color hoverBg = new Color(134, 226, 129);
    private final Color selectedBg = new Color(126, 220, 121);
    private final Color borderColor = new Color(111, 210, 104);

    private final Color logoutBg = new Color(255, 98, 98);
    private final Color logoutHoverBg = new Color(246, 86, 86);

    private boolean selected = false;
    private boolean logoutStyle = false;

    public MenuButton(String text) {
        super(text);
        setFocusPainted(false);
        setOpaque(true);
        setContentAreaFilled(true);
        setBorderPainted(true);
        setHorizontalAlignment(SwingConstants.LEFT);
        setFont(new Font("Arial", Font.BOLD, 12));
        setForeground(new Color(32, 73, 36));
        setBackground(normalBg);
        setBorder(BorderFactory.createLineBorder(borderColor, 1));
        setPreferredSize(new Dimension(190, 52));
        setMargin(new Insets(0, 12, 0, 8));
        setCursor(new Cursor(Cursor.HAND_CURSOR));

        initHoverEffect();
    }

    private void initHoverEffect() {
        addMouseListener(new java.awt.event.MouseAdapter() {
            @Override
            public void mouseEntered(java.awt.event.MouseEvent evt) {
                if (!selected) {
                    setBackground(logoutStyle ? logoutHoverBg : hoverBg);
                }
            }

            @Override
            public void mouseExited(java.awt.event.MouseEvent evt) {
                if (!selected) {
                    setBackground(logoutStyle ? logoutBg : normalBg);
                }
            }
        });
    }

    public void setSelectedStyle(boolean selected) {
        this.selected = selected;

        if (logoutStyle) return;

        if (selected) {
            setBackground(selectedBg);
            setBorder(BorderFactory.createLineBorder(new Color(83, 184, 77), 2));
        } else {
            setBackground(normalBg);
            setBorder(BorderFactory.createLineBorder(borderColor, 1));
        }
    }

    public void setLogoutStyle() {
        logoutStyle = true;
        setHorizontalAlignment(SwingConstants.CENTER);
        setForeground(Color.BLACK);
        setFont(new Font("Arial", Font.BOLD, 13));
        setBackground(logoutBg);
        setBorder(BorderFactory.createLineBorder(new Color(220, 77, 77), 1));
        setPreferredSize(new Dimension(190, 42));
    }
}