package com.acacioswork.interfaz_usuario;

import java.awt.Color;
import java.awt.Component;
import java.awt.Cursor;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.Rectangle;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JPanel;
import javax.swing.JTable;
import javax.swing.table.TableCellRenderer;

/**
 * Panel contenedor con botones de Entrada y Salida para incrustarse en la tabla de Inventario.
 * @author RADJ
 */
public class MovimientosPanel extends JPanel {
    public final JButton btnEntrada;
    public final JButton btnSalida;

    public MovimientosPanel() {
        setLayout(new FlowLayout(FlowLayout.CENTER, 6, 2));
        setOpaque(false);

        btnEntrada = new JButton("Entrada") {
            @Override
            protected void paintComponent(java.awt.Graphics g) {
                java.awt.Graphics2D g2 = (java.awt.Graphics2D) g.create();
                g2.setRenderingHint(java.awt.RenderingHints.KEY_ANTIALIASING, java.awt.RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setColor(getBackground());
                g2.fillRoundRect(0, 0, getWidth(), getHeight(), 6, 6);
                g2.dispose();
                super.paintComponent(g);
            }
        };
        btnEntrada.setFont(new Font("Inter", Font.BOLD, 11));
        btnEntrada.setForeground(Color.WHITE);
        btnEntrada.setBackground(new Color(16, 185, 129));
        btnEntrada.setBorder(BorderFactory.createEmptyBorder(4, 10, 4, 10));
        btnEntrada.setFocusPainted(false);
        btnEntrada.setContentAreaFilled(false);
        btnEntrada.setOpaque(false);
        btnEntrada.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));

        btnSalida = new JButton("Salida") {
            @Override
            protected void paintComponent(java.awt.Graphics g) {
                java.awt.Graphics2D g2 = (java.awt.Graphics2D) g.create();
                g2.setRenderingHint(java.awt.RenderingHints.KEY_ANTIALIASING, java.awt.RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setColor(getBackground());
                g2.fillRoundRect(0, 0, getWidth(), getHeight(), 6, 6);
                g2.dispose();
                super.paintComponent(g);
            }
        };
        btnSalida.setFont(new Font("Inter", Font.BOLD, 11));
        btnSalida.setForeground(Color.WHITE);
        btnSalida.setBackground(new Color(239, 68, 68));
        btnSalida.setBorder(BorderFactory.createEmptyBorder(4, 10, 4, 10));
        btnSalida.setFocusPainted(false);
        btnSalida.setContentAreaFilled(false);
        btnSalida.setOpaque(false);
        btnSalida.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));

        add(btnEntrada);
        add(btnSalida);
    }

    public static void setupColumn(JTable table, int colIndex, Runnable onEntrada, Runnable onSalida) {
        table.getColumnModel().getColumn(colIndex).setCellRenderer(new MovimientosCellRenderer());
        table.getColumnModel().getColumn(colIndex).setPreferredWidth(160);

        table.addMouseListener(new MouseAdapter() {
            @Override
            public void mousePressed(MouseEvent e) {
                int row = table.rowAtPoint(e.getPoint());
                int col = table.columnAtPoint(e.getPoint());
                if (col == colIndex && row != -1) {
                    Rectangle rect = table.getCellRect(row, col, true);
                    int cellX = e.getX() - rect.x;
                    int width = rect.width;
                    table.setRowSelectionInterval(row, row);
                    if (cellX < width / 2) {
                        onEntrada.run();
                    } else {
                        onSalida.run();
                    }
                }
            }
        });
    }
}

class MovimientosCellRenderer implements TableCellRenderer {
    private final MovimientosPanel panel = new MovimientosPanel();

    @Override
    public Component getTableCellRendererComponent(JTable table, Object value,
            boolean isSelected, boolean hasFocus, int row, int column) {
        if (isSelected) {
            panel.setBackground(table.getSelectionBackground());
            panel.setOpaque(true);
        } else {
            panel.setOpaque(false);
        }
        return panel;
    }
}
