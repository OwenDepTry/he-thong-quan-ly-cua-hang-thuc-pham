package ui;

import java.awt.Color;
import java.awt.Cursor;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Insets;
import java.awt.RenderingHints;
import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.plaf.basic.BasicButtonUI;

/**
 * Nút bo góc tự vẽ, có hover/pressed, giống nhau dù chạy với Look & Feel nào.
 */
public class AppButton extends JButton {

    public enum Variant { PRIMARY, DANGER, SECONDARY, GHOST }

    private Variant variant;
    private final int arc = 8;

    public AppButton(String text) {
        this(text, null, Variant.SECONDARY);
    }

    public AppButton(String text, String iconName, Variant variant) {
        super(text);
        setUI(new BasicButtonUI());
        setContentAreaFilled(false);
        setBorderPainted(false);
        setFocusPainted(false);
        setOpaque(false);
        setRolloverEnabled(true);
        setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        setFont(AppTheme.font(Font.BOLD, 13));
        setIconTextGap(6);
        setBorder(BorderFactory.createEmptyBorder(0, 14, 0, 14));
        setMargin(new Insets(0, 14, 0, 14));
        setVariant(variant);
        if (iconName != null) {
            setIcon(AppIcons.get(iconName, 16, getForeground()));
        }
    }

    public final void setVariant(Variant variant) {
        this.variant = variant;
        switch (variant) {
            case PRIMARY:
                setForeground(AppTheme.WHITE);
                break;
            case DANGER:
                setForeground(AppTheme.DANGER);
                break;
            default:
                setForeground(AppTheme.TEXT_DARK);
                break;
        }
        repaint();
    }

    @Override
    public Dimension getPreferredSize() {
        Dimension d = super.getPreferredSize();
        return new Dimension(Math.max(d.width, 72), Math.max(d.height, 34));
    }

    @Override
    protected void paintComponent(Graphics g) {
        Graphics2D g2 = (Graphics2D) g.create();
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

        boolean hover = getModel().isRollover() && isEnabled();
        boolean pressed = getModel().isPressed() && isEnabled();

        Color fill;
        Color border;
        switch (variant) {
            case PRIMARY:
                fill = pressed ? AppTheme.PRIMARY_TEXT : hover ? AppTheme.PRIMARY_HOVER : AppTheme.PRIMARY;
                border = fill;
                break;
            case DANGER:
                fill = pressed ? AppTheme.DANGER_SOFT.darker() : hover ? AppTheme.DANGER_SOFT : AppTheme.SURFACE;
                border = hover ? AppTheme.DANGER : AppTheme.BORDER_STRONG;
                break;
            case GHOST:
                fill = pressed ? AppTheme.BORDER : hover ? AppTheme.SURFACE_ALT : new Color(0, 0, 0, 0);
                border = fill;
                break;
            default:
                fill = pressed ? AppTheme.BORDER : hover ? AppTheme.SURFACE_ALT : AppTheme.SURFACE;
                border = AppTheme.BORDER_STRONG;
                break;
        }
        if (!isEnabled()) {
            fill = AppTheme.SURFACE_ALT;
            border = AppTheme.BORDER;
        }

        g2.setColor(fill);
        g2.fillRoundRect(0, 0, getWidth() - 1, getHeight() - 1, arc, arc);
        g2.setColor(border);
        g2.drawRoundRect(0, 0, getWidth() - 1, getHeight() - 1, arc, arc);
        g2.dispose();

        super.paintComponent(g);
    }
}
