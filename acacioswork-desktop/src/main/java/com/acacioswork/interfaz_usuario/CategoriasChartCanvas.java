package com.acacioswork.interfaz_usuario;

import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.text.NumberFormat;
import java.util.Locale;
import javax.swing.JPanel;

/**
 * Lienzo independiente para el dibujo vectorial del gráfico de barras de categorías.
 * @author RADJ
 */
public class CategoriasChartCanvas extends JPanel {
    private final CategoriasChartPanel parent;

    public CategoriasChartCanvas(CategoriasChartPanel parent) {
        this.parent = parent;
        setOpaque(false);
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        Graphics2D g2 = (Graphics2D) g.create();
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

        int width = getWidth();
        int height = getHeight();

        // Título del gráfico
        g2.setFont(new Font("Inter", Font.BOLD, 14));
        g2.setColor(Administrador.TEXT_MAIN);
        g2.drawString("📊 Ventas por Categoría de Producto", 10, 24);

        java.util.List<CategoriasChartPanel.CategoryStat> statsList = parent.getStatsList();

        if (statsList == null || statsList.isEmpty()) {
            g2.setFont(new Font("Inter", Font.PLAIN, 12));
            g2.setColor(Administrador.TEXT_MUTED);
            g2.drawString("Sin datos de ventas para el período seleccionado.", 20, height / 2);
            g2.dispose();
            return;
        }

        int paddingLeft = 140;
        int paddingRight = 240;
        int paddingTop = 50;
        int paddingBottom = 20;

        int graphWidth = width - paddingLeft - paddingRight;
        int graphHeight = height - paddingTop - paddingBottom;

        if (graphWidth <= 50 || graphHeight <= 50) {
            g2.dispose();
            return;
        }

        // Máximo de unidades
        int maxUnidades = 0;
        for (CategoriasChartPanel.CategoryStat stat : statsList) {
            if (stat.unidades > maxUnidades) {
                maxUnidades = stat.unidades;
            }
        }
        if (maxUnidades == 0) {
            maxUnidades = 1;
        }

        // Paleta de colores
        Color[] palette = {
                new Color(99, 102, 241),  // indigo
                new Color(139, 92, 246),  // violet
                new Color(59, 130, 246),   // blue
                new Color(16, 185, 129),  // emerald
                new Color(245, 158, 11),  // amber
                new Color(239, 68, 68)    // red
        };

        int numItems = statsList.size();
        int barHeight = Math.min(28, graphHeight / (numItems * 2));
        int gap = (graphHeight - (barHeight * numItems)) / (numItems + 1);
        if (gap < 4) {
            gap = 4;
        }

        NumberFormat nfUnd = NumberFormat.getNumberInstance(Locale.GERMANY);
        NumberFormat nfGan = NumberFormat.getNumberInstance(Locale.GERMANY);
        nfGan.setMaximumFractionDigits(0);

        for (int i = 0; i < numItems; i++) {
            CategoriasChartPanel.CategoryStat stat = statsList.get(i);
            int y = paddingTop + gap + i * (barHeight + gap);

            // 1. Nombre de categoría
            g2.setFont(new Font("Inter", Font.BOLD, 11));
            g2.setColor(Administrador.TEXT_MAIN);
            int strWidth = g2.getFontMetrics().stringWidth(stat.nombre);
            g2.drawString(stat.nombre, paddingLeft - 15 - strWidth, y + (barHeight / 2) + 4);

            // 2. Ancho de barra
            int barWidth = (int) (((double) stat.unidades / maxUnidades) * graphWidth);
            if (barWidth < 4) {
                barWidth = 4;
            }

            // 3. Dibujar barra
            g2.setColor(palette[i % palette.length]);
            g2.fillRoundRect(paddingLeft, y, barWidth, barHeight, 8, 8);

            // 4. Dibujar etiquetas de datos
            int textX = paddingLeft + barWidth + 10;
            g2.setFont(new Font("Inter", Font.BOLD, 11));
            g2.setColor(Administrador.TEXT_MUTED);
            String undStr = nfUnd.format(stat.unidades) + " und.  ";
            g2.drawString(undStr, textX, y + (barHeight / 2) + 4);

            int undWidth = g2.getFontMetrics().stringWidth(undStr);
            g2.setColor(Administrador.ACCENT);
            String ganStr = "$ " + nfGan.format(stat.ganancia) + " Ganancia";
            g2.drawString(ganStr, textX + undWidth, y + (barHeight / 2) + 4);
        }

        // Línea del eje Y
        g2.setColor(new Color(255, 255, 255, 20));
        g2.drawLine(paddingLeft, paddingTop, paddingLeft, paddingTop + graphHeight);

        g2.dispose();
    }
}
