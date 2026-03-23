package ui;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import javax.swing.JPanel;

public class SimpleBarChartPanel extends JPanel {

    private final String title;
    private final String[] labels;
    private final int[] values;

    public SimpleBarChartPanel(String title, String[] labels, int[] values) {
        this.title = title;
        this.labels = labels;
        this.values = values;
        setOpaque(true);
        setBackground(Color.WHITE);
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);

        if (labels == null || values == null || labels.length == 0 || values.length == 0) {
            return;
        }

        Graphics2D g2 = (Graphics2D) g.create();
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

        int width = getWidth();
        int height = getHeight();

        int left = 55;
        int right = 20;
        int top = 45;
        int bottom = 55;

        int chartW = width - left - right;
        int chartH = height - top - bottom;

        // title
        g2.setColor(new Color(20, 100, 40));
        g2.setFont(new Font("Arial", Font.BOLD, 18));
        g2.drawString(title, 20, 28);

        // axis
        g2.setColor(new Color(120, 120, 120));
        g2.setStroke(new BasicStroke(1.5f));
        g2.drawLine(left, top, left, top + chartH);
        g2.drawLine(left, top + chartH, left + chartW, top + chartH);

        int max = 1;
        for (int v : values) {
            if (v > max) {
                max = v;
            }
        }

        // grid + y labels
        g2.setFont(new Font("Arial", Font.PLAIN, 11));
        for (int i = 0; i <= 5; i++) {
            int y = top + chartH - (i * chartH / 5);
            int val = max * i / 5;

            g2.setColor(new Color(230, 230, 230));
            g2.drawLine(left, y, left + chartW, y);

            g2.setColor(new Color(80, 80, 80));
            g2.drawString(String.valueOf(val), 8, y + 4);
        }

        int barCount = Math.min(labels.length, values.length);
        int space = 18;
        int barW = Math.max(25, (chartW - (barCount + 1) * space) / barCount);

        for (int i = 0; i < barCount; i++) {
            int x = left + space + i * (barW + space);
            int barH = (int) ((values[i] * 1.0 / max) * (chartH - 10));
            int y = top + chartH - barH;

            g2.setColor(new Color(70, 190, 90));
            g2.fillRoundRect(x, y, barW, barH, 12, 12);

            g2.setColor(new Color(40, 130, 60));
            g2.drawRoundRect(x, y, barW, barH, 12, 12);

            g2.setColor(Color.BLACK);
            g2.drawString(String.valueOf(values[i]), x + 6, y - 6);

            g2.setFont(new Font("Arial", Font.PLAIN, 11));
            String label = labels[i];
            int textW = g2.getFontMetrics().stringWidth(label);
            g2.drawString(label, x + (barW - textW) / 2, top + chartH + 18);
        }

        g2.dispose();
    }
}