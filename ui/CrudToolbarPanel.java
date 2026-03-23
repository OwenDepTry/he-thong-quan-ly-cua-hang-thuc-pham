package ui;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Font;
import java.util.LinkedHashMap;
import java.util.Map;
import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JPanel;

public class CrudToolbarPanel extends JPanel {

    private final Map<String, JButton> buttonMap = new LinkedHashMap<>();

    public CrudToolbarPanel(String... buttonNames) {
        setLayout(new FlowLayout(FlowLayout.LEFT, 6, 6));
        setOpaque(false);
        setBorder(BorderFactory.createEmptyBorder(0, 8, 0, 0));

        for (String name : buttonNames) {
            JButton btn = createButton(name);
            buttonMap.put(name, btn);
            add(btn);
        }
    }

    private JButton createButton(String text) {
        JButton btn = new JButton(buildButtonHtml(text));
        btn.setFocusPainted(false);
        btn.setPreferredSize(new Dimension(92, 74));
        btn.setBackground(resolveButtonColor(text));
        btn.setForeground(resolveTextColor(text));
        btn.setFont(new Font("Arial", Font.BOLD, 12));
        btn.setBorder(BorderFactory.createLineBorder(new Color(230, 245, 230), 1));
        return btn;
    }

    private String buildButtonHtml(String text) {
        String symbol = resolveSymbol(text);
        String displayText = resolveDisplayText(text);

        return "<html><div style='text-align:center; line-height:1.15;'>"
                + "<div style='font-size:20px; font-weight:bold; margin-bottom:4px;'>" + symbol + "</div>"
                + "<div style='font-size:11px; font-weight:bold;'>" + displayText + "</div>"
                + "</div></html>";
    }

    private String resolveDisplayText(String text) {
        switch (text) {
            case "Xuất excel": return "Xuất Excel";
            case "Nhập excel": return "Nhập Excel";
            case "In PDF": return "In PDF";
            default: return text;
        }
    }

    private String resolveSymbol(String text) {
        switch (text) {
            case "Thêm": return "✚";
            case "Xóa": return "✖";
            case "Sửa": return "✎";
            case "Chi tiết": return "i";
            case "In PDF": return "📄";
            case "Xuất excel": return "▦";
            case "Nhập excel": return "🗎";
            default: return "•";
        }
    }

    private Color resolveButtonColor(String text) {
        switch (text) {
            case "Thêm": return new Color(78, 210, 115);
            case "Xóa": return new Color(255, 99, 99);
            case "Sửa": return new Color(255, 195, 94);
            case "Chi tiết": return new Color(72, 161, 232);
            case "In PDF": return new Color(243, 86, 72);
            case "Xuất excel": return new Color(55, 196, 108);
            case "Nhập excel": return new Color(96, 193, 235);
            default: return Color.LIGHT_GRAY;
        }
    }

    private Color resolveTextColor(String text) {
        if ("Sửa".equals(text)) {
            return new Color(110, 60, 0);
        }
        return Color.WHITE;
    }

    public JButton getButton(String name) {
        return buttonMap.get(name);
    }
}