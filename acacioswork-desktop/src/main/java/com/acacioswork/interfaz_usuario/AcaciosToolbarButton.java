package com.acacioswork.interfaz_usuario;

import javax.swing.JButton;
import javax.swing.border.EmptyBorder;
import java.awt.Color;
import java.awt.Cursor;
import java.awt.Font;
import java.awt.GradientPaint;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.RenderingHints;

/**
 * Botón personalizado para la barra de navegación lateral en Desktop.
 * @author RADJ / Antigravity
 */
public class AcaciosToolbarButton extends JButton {
    private final String secName;

    public AcaciosToolbarButton(String text, String secName) {
        super(text);
        this.secName = secName;
        setHorizontalAlignment(javax.swing.SwingConstants.LEFT);
        setForeground(new Color(148, 163, 184));
        setFont(new Font("Inter", Font.BOLD, 13));
        setBorder(new EmptyBorder(6, 16, 6, 16));
        setFocusPainted(false);
        setContentAreaFilled(false);
        setOpaque(false);
        setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        putClientProperty("isTab", true);
    }

    @Override
    public java.awt.Dimension getPreferredSize() {
        return new java.awt.Dimension(228, 42);
    }

    @Override
    protected void paintComponent(Graphics g) {
        boolean active = Boolean.TRUE.equals(getClientProperty("active"));
        Graphics2D g2 = (Graphics2D) g.create();
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

        Color c1, c2;
        if (active) {
            c1 = new Color(249, 115, 22);
            c2 = new Color(239, 68, 68);
            setForeground(Color.WHITE);
        } else if ("alertas".equals(secName)) {
            if (Boolean.TRUE.equals(getClientProperty("pulsing"))) {
                c1 = new Color(255, 59, 48);
                c2 = new Color(255, 45, 85);
                setForeground(Color.WHITE);
            } else {
                c1 = new Color(30, 41, 59, 200);
                c2 = new Color(30, 41, 59, 200);
                setForeground(new Color(239, 68, 68));
            }
        } else {
            c1 = new Color(30, 41, 59, 120);
            c2 = new Color(30, 41, 59, 120);
            setForeground(new Color(148, 163, 184));
        }

        g2.setPaint(new GradientPaint(0, 0, c1, 0, getHeight(), c2));
        g2.fillRoundRect(0, 0, getWidth(), getHeight(), 10, 10);

        if (active) {
            g2.setColor(new Color(255, 255, 255, 38));
            g2.setStroke(new java.awt.BasicStroke(1));
            g2.drawRoundRect(0, 0, getWidth() - 1, getHeight() - 1, 10, 10);
        } else if (!"alertas".equals(secName)) {
            g2.setColor(new Color(255, 255, 255, 10));
            g2.drawRoundRect(0, 0, getWidth() - 1, getHeight() - 1, 10, 10);
        }
        g2.dispose();
        super.paintComponent(g);
    }
}
