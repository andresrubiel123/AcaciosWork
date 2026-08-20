package com.acacioswork.interfaz_usuario;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Cursor;
import java.awt.Font;
import java.awt.GradientPaint;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.GridLayout;
import java.awt.RenderingHints;
import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.border.EmptyBorder;

/**
 * Pestaña de reportes para la interfaz de Administrador.
 * Muestra el panel con las 10 tarjetas de exportación a PDF para la empresa.
 * @author RADJ
 */
public class ReportesTab extends JPanel {
    private final Administrador parent;

    public ReportesTab(Administrador parent) {
        this.parent = parent;
        setLayout(new BorderLayout(0, 16));
        setBackground(Administrador.BG_DARK);
        setBorder(new EmptyBorder(24, 28, 24, 28));

        // Encabezado
        add(UIUtils.buildSectionHeader("Reportes", "Generación y exportación de informes", (JButton) null),
                BorderLayout.NORTH);

        // Cuadrícula de tarjetas de reporte (10 tarjetas)
        JPanel grid = new JPanel(new GridLayout(0, 3, 16, 16));
        grid.setOpaque(false);

        String[][] rpts = {
                { "📦 Inventario General", "Lista completa de productos con stock y precios actuales.", "inventario" },
                { "⚠️ Productos con Stock Bajo", "Listado de artículos por debajo del stock mínimo definido.", "stock-bajo" },
                { "📅 Control de Vencimientos", "Productos vencidos o próximos a vencer dentro de los siguientes 5 días.", "vencimientos" },
                { "👥 Reporte de Clientes", "Base de clientes registrados con su información de contacto.", "clientes" },
                { "🏭 Reporte de Proveedores", "Directorio de proveedores con datos de contacto y productos.", "proveedores" },
                { "👤 Usuarios del Sistema", "Listado de usuarios activos, roles y permisos asignados.", "usuarios" },
                { "🛒 Reporte de Ventas", "Listado histórico de todas las ventas con fecha, clientes y totales.", "ventas" },
                { "📈 Reporte de Ganancias", "Análisis de rentabilidad detallando costos, ingresos y margen de ganancia por venta.", "ganancias" },
                { "📊 Reporte Ejecutivo", "Métricas principales de inventario y estado general de la empresa.", "resumen" },
                { "⚠️ Vencimientos a 15 Días", "Productos vencidos o próximos a vencer en los siguientes 15 días.", "vencimientos-15" }
        };

        for (String[] r : rpts) {
            grid.add(buildReportCard(r[0], r[1], r[2]));
        }

        JScrollPane scroll = new JScrollPane(grid);
        scroll.setBorder(BorderFactory.createEmptyBorder());
        scroll.setOpaque(false);
        scroll.getViewport().setOpaque(false);
        add(scroll, BorderLayout.CENTER);
    }

    private JPanel buildReportCard(String titulo, String descripcion, String tipo) {
        JPanel c = new JPanel(new BorderLayout(0, 8));
        c.setBackground(Administrador.BG_CARD);
        c.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(255, 255, 255, 13), 1),
                new EmptyBorder(12, 12, 12, 12)));

        JLabel lt = new JLabel(titulo);
        lt.setForeground(Administrador.TEXT_MAIN);
        lt.setFont(new Font("Inter", Font.BOLD, 16));

        JLabel ld = new JLabel("<html><body style='width: 220px;'>" + descripcion + "</body></html>");
        ld.setForeground(Administrador.TEXT_MUTED);
        ld.setFont(new Font("Inter", Font.PLAIN, 12));

        JButton b = new JButton("Generar PDF") {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setPaint(new GradientPaint(0, 0, new Color(245, 158, 11), 0, getHeight(), new Color(217, 119, 6)));
                g2.fillRoundRect(0, 0, getWidth(), getHeight(), 10, 10);
                g2.dispose();
                super.paintComponent(g);
            }
        };
        b.setForeground(Color.WHITE);
        b.setFont(new Font("Inter", Font.BOLD, 11));
        b.setBorder(new EmptyBorder(6, 12, 6, 12));
        b.setFocusPainted(false);
        b.setContentAreaFilled(false);
        b.setOpaque(false);
        b.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        b.addActionListener(e -> parent.generarReporte(tipo));

        JPanel textPanel = new JPanel(new BorderLayout(0, 4));
        textPanel.setOpaque(false);
        textPanel.add(lt, BorderLayout.NORTH);
        textPanel.add(ld, BorderLayout.CENTER);

        c.add(textPanel, BorderLayout.CENTER);
        c.add(b, BorderLayout.SOUTH);
        return c;
    }

    public void refresh() {
        // No hay gráficos ni datos locales que refrescar aquí ahora.
    }
}
