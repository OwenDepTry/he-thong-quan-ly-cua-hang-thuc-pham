package ui;

import java.awt.Color;
import java.awt.Font;
import javax.swing.BorderFactory;
import javax.swing.border.Border;

public class AppTheme {

    private AppTheme() {
    }

    public static final Color BG_MAIN = new Color(235, 235, 235);
    public static final Color PANEL_WHITE = Color.WHITE;

    public static final Color GREEN_TOP = new Color(0, 180, 55);
    public static final Color GREEN_TOP_DARK = new Color(0, 130, 45);
    public static final Color GREEN_SIDEBAR = new Color(141, 236, 136);
    public static final Color GREEN_SIDEBAR_ACTIVE = new Color(170, 246, 165);
    public static final Color GREEN_SIDEBAR_TOP = new Color(0, 110, 40);
    public static final Color GREEN_BORDER = new Color(86, 200, 86);
    public static final Color GREEN_FORM = new Color(211, 224, 212);
    public static final Color GREEN_SOFT = new Color(224, 255, 224);

    public static final Color RED_LOGOUT = new Color(255, 98, 98);
    public static final Color PINK_LOGOUT = new Color(233, 140, 233);
    public static final Color RED_ACTION = new Color(255, 88, 88);
    public static final Color BLUE_ACTION = new Color(58, 162, 255);
    public static final Color CYAN_ACTION = new Color(93, 211, 255);
    public static final Color ORANGE_ACTION = new Color(255, 178, 66);
    public static final Color WHITE_ACTION = new Color(245, 250, 255);

    public static final Color TABLE_HEADER = new Color(247, 247, 247);
    public static final Color TABLE_GRID = new Color(224, 224, 224);
    public static final Color TEXT_DARK = new Color(35, 35, 35);
    public static final Color TEXT_MUTED = new Color(88, 88, 88);
    public static final Color YELLOW_TEXT = new Color(255, 235, 59);
    public static final Color WHITE = Color.WHITE;

    public static final Font FONT_TITLE = new Font("Arial", Font.BOLD, 30);
    public static final Font FONT_HEADER = new Font("Arial", Font.BOLD, 18);
    public static final Font FONT_SUB = new Font("Arial", Font.BOLD, 14);
    public static final Font FONT_NORMAL = new Font("Arial", Font.PLAIN, 12);
    public static final Font FONT_SMALL = new Font("Arial", Font.PLAIN, 11);
    public static final Font FONT_BOLD_SMALL = new Font("Arial", Font.BOLD, 11);
    public static final Font FONT_TINY = new Font("Arial", Font.PLAIN, 10);
    public static final Font FONT_BOLD = new Font("Arial", Font.BOLD, 14);

    public static Border line(Color c) {
        return BorderFactory.createLineBorder(c, 1);
    }

    public static Border empty(int t, int l, int b, int r) {
        return BorderFactory.createEmptyBorder(t, l, b, r);
    }
}   