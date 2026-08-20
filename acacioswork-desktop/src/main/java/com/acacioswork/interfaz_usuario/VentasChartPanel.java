package com.acacioswork.interfaz_usuario;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Font;
import java.awt.GradientPaint;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.geom.Path2D;
import java.text.NumberFormat;
import java.util.Locale;
import javax.swing.BorderFactory;
import javax.swing.JPanel;
import javax.swing.border.EmptyBorder;

/**
 * Panel de gráfico personalizado que dibuja la tendencia de ganancias mensuales de forma vectorial.
 * @author RADJ
 */
public class VentasChartPanel extends JPanel {
    private final double[] data = new double[12];
    private final String[] months = {"Ene", "Feb", "Mar", "Abr", "May", "Jun", "Jul", "Ago", "Sep", "Oct", "Nov", "Dic"};

    public VentasChartPanel() {
        setBackground(Administrador.BG_CARD);
        setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(255, 255, 255, 13), 1),
                new EmptyBorder(16, 20, 16, 20)));
    }

    public void setSalesData(double[] newData) {
        if (newData != null && newData.length == 12) {
            System.arraycopy(newData, 0, this.data, 0, 12);
            repaint();
        }
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        Graphics2D g2 = (Graphics2D) g.create();
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

        int width = getWidth();
        int height = getHeight();

        // Título
        g2.setFont(new Font("Inter", Font.BOLD, 14));
        g2.setColor(Administrador.TEXT_MAIN);
        g2.drawString("📈 Tendencia de Ganancias Mensuales", 20, 24);

        int paddingLeft = 90;
        int paddingRight = 30;
        int paddingTop = 60;
        int paddingBottom = 40;

        int graphWidth = width - paddingLeft - paddingRight;
        int graphHeight = height - paddingTop - paddingBottom;

        if (graphWidth <= 0 || graphHeight <= 0) {
            g2.dispose();
            return;
        }

        // Determinar máximo para escala
        double maxVal = 50000;
        for (double val : data) {
            if (val > maxVal) {
                maxVal = val;
            }
        }

        int numDivisions = 5;
        double divisionStepVal = maxVal / numDivisions;

        // COP label
        g2.setFont(new Font("Inter", Font.BOLD, 10));
        g2.setColor(Administrador.TEXT_MUTED);
        g2.drawString("COP", paddingLeft - 10 - g2.getFontMetrics().stringWidth("COP"), paddingTop - 12);

        // Rejilla y etiquetas Y
        NumberFormat nfY = NumberFormat.getNumberInstance(Locale.of("es", "CO"));
        nfY.setMaximumFractionDigits(0);
        g2.setFont(new Font("Inter", Font.PLAIN, 10));

        for (int i = 0; i <= numDivisions; i++) {
            double currentVal = i * divisionStepVal;
            int y = paddingTop + graphHeight - (int) ((currentVal / maxVal) * graphHeight);

            if (i > 0) {
                g2.setColor(new Color(255, 255, 255, 10));
                g2.drawLine(paddingLeft, y, paddingLeft + graphWidth, y);
            }

            String labelStr = nfY.format(currentVal);
            g2.setColor(Administrador.TEXT_MUTED);
            g2.drawString(labelStr, paddingLeft - 10 - g2.getFontMetrics().stringWidth(labelStr), y + 4);
        }

        // Rejilla y etiquetas X
        int stepX = graphWidth / 11;
        int[] pointXs = new int[12];
        int[] pointYs = new int[12];

        for (int i = 0; i < 12; i++) {
            int x = paddingLeft + i * stepX;
            pointXs[i] = x;
            pointYs[i] = paddingTop + graphHeight - (int) ((data[i] / maxVal) * graphHeight);

            g2.setColor(new Color(255, 255, 255, 8));
            g2.drawLine(x, paddingTop, x, paddingTop + graphHeight);

            g2.setColor(Administrador.TEXT_MUTED);
            String monthName = months[i];
            int strW = g2.getFontMetrics().stringWidth(monthName);
            g2.drawString(monthName, x - strW / 2, paddingTop + graphHeight + 18);
        }

        // Eje X principal
        g2.setColor(new Color(255, 255, 255, 20));
        g2.drawLine(paddingLeft, paddingTop + graphHeight, paddingLeft + graphWidth, paddingTop + graphHeight);

        // Curva
        Path2D.Double path = new Path2D.Double();
        path.moveTo(pointXs[0], pointYs[0]);
        for (int i = 1; i < 12; i++) {
            int prevX = pointXs[i - 1];
            int prevY = pointYs[i - 1];
            int currX = pointXs[i];
            int currY = pointYs[i];
            int ctrlX1 = prevX + (currX - prevX) / 2;
            int ctrlY1 = prevY;
            int ctrlX2 = prevX + (currX - prevX) / 2;
            int ctrlY2 = currY;
            path.curveTo(ctrlX1, ctrlY1, ctrlX2, ctrlY2, currX, currY);
        }

        // Relleno degradado
        Path2D.Double fillPath = (Path2D.Double) path.clone();
        fillPath.lineTo(pointXs[11], paddingTop + graphHeight);
        fillPath.lineTo(pointXs[0], paddingTop + graphHeight);
        fillPath.closePath();

        g2.setPaint(new GradientPaint(0, paddingTop, new Color(99, 102, 241, 45), 0, paddingTop + graphHeight, new Color(99, 102, 241, 0)));
        g2.fill(fillPath);

        // Dibujar línea principal
        g2.setColor(Administrador.PRIMARY);
        g2.setStroke(new BasicStroke(3f, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND));
        g2.draw(path);

        // Dibujar puntos
        for (int i = 0; i < 12; i++) {
            int x = pointXs[i];
            int y = pointYs[i];

            g2.setColor(Color.WHITE);
            g2.fillOval(x - 5, y - 5, 10, 10);

            g2.setColor(new Color(249, 115, 22));
            g2.fillOval(x - 3, y - 3, 6, 6);

            if (data[i] > 0) {
                g2.setFont(new Font("Inter", Font.BOLD, 9));
                g2.setColor(new Color(251, 146, 60));

                NumberFormat nfPoint = NumberFormat.getNumberInstance(Locale.of("es", "CO"));
                nfPoint.setMaximumFractionDigits(0);
                String valStr = nfPoint.format(data[i]);

                int strW = g2.getFontMetrics().stringWidth(valStr);
                g2.drawString(valStr, x - strW / 2, y - 10);
            }
        }

        g2.dispose();
    }
}
