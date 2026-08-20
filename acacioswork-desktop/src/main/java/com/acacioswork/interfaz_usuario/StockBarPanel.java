package com.acacioswork.interfaz_usuario;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Font;
import java.awt.GradientPaint;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import javax.swing.BorderFactory;
import javax.swing.JLabel;
import javax.swing.JPanel;

/**
 * Componente gráfico que dibuja una barra de stock con un degradado dinámico.
 * @author RADJ
 */
public class StockBarPanel extends JPanel {
    private int actual;
    private int optimo;
    private int pct;
    private Color textColor;
    private Color barColorStart;
    private Color barColorEnd;

    public StockBarPanel() {
        setLayout(new BorderLayout(0, 2));
        setOpaque(false);
        setBorder(BorderFactory.createEmptyBorder(4, 8, 4, 8));
    }

    public void setStockData(StockData data, boolean isSelected) {
        this.actual = data.actual;
        this.optimo = data.optimo > 0 ? data.optimo : 200;
        this.pct = (int) Math.round(((double) actual / optimo) * 100);

        if (pct <= 30) {
            textColor = new Color(248, 113, 113);
            barColorStart = new Color(248, 113, 113);
            barColorEnd = new Color(239, 68, 68);
        } else if (pct <= 69) {
            textColor = new Color(251, 146, 60);
            barColorStart = new Color(251, 146, 60);
            barColorEnd = new Color(249, 115, 22);
        } else {
            textColor = new Color(52, 211, 153);
            barColorStart = new Color(52, 211, 153);
            barColorEnd = new Color(16, 185, 129);
        }

        removeAll();

        JPanel infoPanel = new JPanel(new BorderLayout());
        infoPanel.setOpaque(false);

        JLabel lblQty = new JLabel(actual + " / " + optimo + " uds");
        lblQty.setFont(new Font("Inter", Font.PLAIN, 11));
        lblQty.setForeground(new Color(226, 232, 240));

        JLabel lblPct = new JLabel(pct + "%");
        lblPct.setFont(new Font("Inter", Font.BOLD, 11));
        lblPct.setForeground(textColor);

        infoPanel.add(lblQty, BorderLayout.WEST);
        infoPanel.add(lblPct, BorderLayout.EAST);
        add(infoPanel, BorderLayout.NORTH);

        JPanel barPanel = new JPanel() {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setColor(new Color(255, 255, 255, 13));
                g2.fillRoundRect(0, 0, getWidth(), getHeight(), 8, 8);
                g2.setColor(new Color(255, 255, 255, 20));
                g2.drawRoundRect(0, 0, getWidth() - 1, getHeight() - 1, 8, 8);

                int fillWidth = (int) (getWidth() * (Math.min(pct, 100) / 100.0));
                if (fillWidth > 0) {
                    GradientPaint paint = new GradientPaint(0, 0, barColorStart, fillWidth, 0, barColorEnd);
                    g2.setPaint(paint);
                    g2.fillRoundRect(0, 0, fillWidth, getHeight(), 8, 8);
                }
                g2.dispose();
            }
        };
        barPanel.setOpaque(false);
        barPanel.setPreferredSize(new java.awt.Dimension(100, 8));
        add(barPanel, BorderLayout.CENTER);
    }
}
