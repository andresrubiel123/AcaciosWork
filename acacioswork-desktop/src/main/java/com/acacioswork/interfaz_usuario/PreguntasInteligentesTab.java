package com.acacioswork.interfaz_usuario;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Cursor;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.GradientPaint;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.GridLayout;
import java.awt.RenderingHints;
import java.awt.event.ActionListener;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.SwingWorker;
import javax.swing.Timer;
import javax.swing.border.EmptyBorder;
import com.acacioswork.model.Venta;

/**
 * Pestaña de Preguntas Inteligentes analíticas sobre el negocio.
 * Incluye la selección de periodo y las 9 tarjetas de análisis (incluyendo fechas de vencimiento).
 * @author RADJ
 */
public class PreguntasInteligentesTab extends JPanel {
    private final JComboBox<String> comboMes;
    private final JComboBox<String> comboAnio;
    private final JLabel lblIqStatus;
    private final List<PulsingAnswerLabel> pulsingLabels = new ArrayList<>();
    private final IntelligenceEngine engine = new IntelligenceEngine();

    public PreguntasInteligentesTab() {
        setLayout(new BorderLayout(0, 16));
        setBackground(Administrador.BG_DARK);
        setBorder(new EmptyBorder(24, 28, 24, 28));

        // Encabezado
        add(UIUtils.buildSectionHeader("🤖 Preguntas Inteligentes", "Análisis automático del estado de tu negocio", (JButton) null), BorderLayout.NORTH);

        JPanel body = new JPanel(new BorderLayout(0, 16));
        body.setOpaque(false);

        // Barra de filtros
        JPanel filterBar = new JPanel(new FlowLayout(FlowLayout.LEFT, 12, 8)) {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setColor(Administrador.BG_CARD);
                g2.fillRoundRect(0, 0, getWidth(), getHeight(), 12, 12);
                g2.setColor(new Color(255, 255, 255, 13));
                g2.drawRoundRect(0, 0, getWidth() - 1, getHeight() - 1, 12, 12);
                g2.dispose();
            }
        };
        filterBar.setOpaque(false);
        filterBar.setBorder(new EmptyBorder(12, 16, 12, 16));

        JLabel lblFilter = new JLabel("📅 Periodo de Análisis: ");
        lblFilter.setForeground(Administrador.TEXT_MAIN);
        lblFilter.setFont(new Font("Inter", Font.BOLD, 14));
        filterBar.add(lblFilter);

        String[] meses = {
                "Enero", "Febrero", "Marzo", "Abril", "Mayo", "Junio",
                "Julio", "Agosto", "Septiembre", "Octubre", "Noviembre", "Diciembre"
        };
        comboMes = new JComboBox<>(meses);
        comboMes.setBackground(Administrador.BG_DARK);
        comboMes.setForeground(Administrador.TEXT_MAIN);
        comboMes.setFont(new Font("Inter", Font.PLAIN, 13));

        String[] anios = { "2024", "2025", "2026", "2027", "2028", "2029", "2030" };
        comboAnio = new JComboBox<>(anios);
        comboAnio.setBackground(Administrador.BG_DARK);
        comboAnio.setForeground(Administrador.TEXT_MAIN);
        comboAnio.setFont(new Font("Inter", Font.PLAIN, 13));

        LocalDate today = LocalDate.now();
        comboMes.setSelectedIndex(today.getMonthValue() - 1);
        comboAnio.setSelectedItem(String.valueOf(today.getYear()));

        lblIqStatus = new JLabel("Analizando: " + meses[today.getMonthValue() - 1] + " " + today.getYear());
        lblIqStatus.setForeground(Administrador.ACCENT);
        lblIqStatus.setFont(new Font("Inter", Font.BOLD, 13));

        ActionListener filterListener = e -> {
            int m = comboMes.getSelectedIndex();
            String y = (String) comboAnio.getSelectedItem();
            lblIqStatus.setText("Analizando: " + meses[m] + " " + y);
            clearIqCache();
        };

        comboMes.addActionListener(filterListener);
        comboAnio.addActionListener(filterListener);

        filterBar.add(comboMes);
        filterBar.add(new JLabel("  "));
        filterBar.add(comboAnio);
        filterBar.add(new JLabel("    "));
        filterBar.add(lblIqStatus);

        body.add(filterBar, BorderLayout.NORTH);

        // Cuadrícula de tarjetas
        JPanel grid = new JPanel(new GridLayout(0, 2, 16, 16));
        grid.setOpaque(false);

        grid.add(buildQuestionCard("📊 Rentabilidad", "¿Cuáles fueron los productos más rentables?", "rentables"));
        grid.add(buildQuestionCard("🔄 Rotación", "¿Qué productos tienen baja rotación?", "baja-rotacion"));
        grid.add(buildQuestionCard("📦 Stock", "¿Qué productos debo reabastecer?", "reabastecer"));
        grid.add(buildQuestionCard("📅 Vencimiento", "¿Cuáles son los productos más próximos a vencerse?", "proximos-vencer"));
        grid.add(buildQuestionCard("🏭 Costos", "¿Cuál proveedor vende más caro?", "proveedor-caro"));
        grid.add(buildQuestionCard("👥 Clientes", "¿Qué clientes compran más?", "top-clientes"));
        grid.add(buildQuestionCard("📈 Historial", "¿Cuál fue el mes con mayores ganancias?", "mejor-mes"));
        grid.add(buildQuestionCard("⚠️ Margen", "¿Qué productos me están generando pérdidas?", "perdidas"));
        grid.add(buildQuestionCard("💤 Inactivos", "¿Qué productos llevan más tiempo sin venderse?", "sin-vender"));

        JScrollPane scroll = new JScrollPane(grid);
        scroll.setBorder(BorderFactory.createEmptyBorder());
        scroll.setOpaque(false);
        scroll.getViewport().setOpaque(false);

        body.add(scroll, BorderLayout.CENTER);
        add(body, BorderLayout.CENTER);

        // Timer para parpadeo
        final long startTime = System.currentTimeMillis();
        Timer iqPulseTimer = new Timer(50, e -> {
            long elapsed = System.currentTimeMillis() - startTime;
            double progress = (elapsed % 2000) / 2000.0;
            double sinVal = Math.sin(progress * 2.0 * Math.PI);
            double factor = (sinVal + 1.0) / 2.0;
            for (PulsingAnswerLabel l : pulsingLabels) {
                l.setFactor(factor);
            }
        });
        iqPulseTimer.start();
    }

    public void clearIqCache() {
        engine.clearCache();
        for (PulsingAnswerLabel l : pulsingLabels) {
            l.setText("");
        }
    }

    private void ejecutarPreguntaInteligente(String tipo, PulsingAnswerLabel label) {
        label.setText("<html><font color='#94a3b8'>Analizando datos...</font></html>");
        int mes = comboMes.getSelectedIndex() + 1;
        int anio = java.lang.Integer.parseInt((String) comboAnio.getSelectedItem());

        new SwingWorker<String, Void>() {
            @Override
            protected String doInBackground() throws Exception {
                engine.initCache();
                List<Venta> ventasFiltradas = engine.getVentasPeriodo(anio, mes);

                switch (tipo) {
                    case "rentables":
                        return engine.iqAnalizarRentables(ventasFiltradas);
                    case "baja-rotacion":
                        return engine.iqAnalizarBajaRotacion(ventasFiltradas);
                    case "reabastecer":
                        return engine.iqAnalizarReabastecer();
                    case "proveedor-caro":
                        return engine.iqAnalizarProveedorCaro();
                    case "top-clientes":
                        return engine.iqAnalizarTopClientes(ventasFiltradas);
                    case "mejor-mes":
                        return engine.iqAnalizarMejorMes();
                    case "perdidas":
                        return engine.iqAnalizarPerdidas();
                    case "sin-vender":
                        return engine.iqAnalizarSinVender(ventasFiltradas);
                    case "proximos-vencer":
                        return engine.iqAnalizarProximosVencer();
                }
                return "Pregunta no reconocida.";
            }

            @Override
            protected void done() {
                try {
                    String result = get();
                    if (result == null || result.trim().isEmpty()) {
                        label.setText("<html><font color='#94a3b8'>Sin datos para este periodo.</font></html>");
                    } else {
                        label.setText("<html>" + result.replace("\n", "<br>") + "</html>");
                    }
                } catch (Exception e) {
                    label.setText("<html><font color='#ef4444'>Error al analizar: " + e.getMessage() + "</font></html>");
                }
            }
        }.execute();
    }

    private JPanel buildQuestionCard(String badgeText, String questionText, String iqType) {
        JPanel c = new JPanel(new BorderLayout(0, 10)) {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setColor(Administrador.BG_CARD);
                g2.fillRoundRect(0, 0, getWidth(), getHeight(), 12, 12);
                g2.setPaint(new GradientPaint(0, 0, new Color(249, 115, 22), getWidth(), 0, new Color(239, 68, 68)));
                g2.fillRect(0, 0, getWidth(), 4);
                g2.setColor(new Color(255, 255, 255, 10));
                g2.drawRoundRect(0, 0, getWidth() - 1, getHeight() - 1, 12, 12);
                g2.dispose();
            }
        };
        c.setOpaque(false);
        c.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(255, 255, 255, 0), 1),
                new EmptyBorder(14, 16, 14, 16)));

        JLabel badge = new JLabel(badgeText);
        badge.setForeground(new Color(129, 140, 248));
        badge.setFont(new Font("Inter", Font.BOLD, 11));

        JLabel qText = new JLabel(questionText);
        qText.setForeground(Administrador.TEXT_MAIN);
        qText.setFont(new Font("Inter", Font.BOLD, 14));

        JPanel topPanel = new JPanel(new GridLayout(2, 1, 0, 4));
        topPanel.setOpaque(false);
        topPanel.add(badge);
        topPanel.add(qText);

        PulsingAnswerLabel ansLabel = new PulsingAnswerLabel();
        pulsingLabels.add(ansLabel);

        JButton btnAnalizar = new JButton("Analizar 🤖") {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setPaint(new GradientPaint(0, 0, new Color(249, 115, 22), 0, getHeight(), new Color(239, 68, 68)));
                g2.fillRoundRect(0, 0, getWidth(), getHeight(), 8, 8);
                g2.dispose();
                super.paintComponent(g);
            }
        };
        btnAnalizar.setForeground(Color.WHITE);
        btnAnalizar.setFont(new Font("Inter", Font.BOLD, 11));
        btnAnalizar.setBorder(new EmptyBorder(6, 14, 6, 14));
        btnAnalizar.setFocusPainted(false);
        btnAnalizar.setContentAreaFilled(false);
        btnAnalizar.setOpaque(false);
        btnAnalizar.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        btnAnalizar.addActionListener(e -> ejecutarPreguntaInteligente(iqType, ansLabel));

        c.add(topPanel, BorderLayout.NORTH);
        c.add(ansLabel, BorderLayout.CENTER);

        JPanel bottom = new JPanel(new FlowLayout(FlowLayout.RIGHT, 0, 0));
        bottom.setOpaque(false);
        bottom.add(btnAnalizar);
        c.add(bottom, BorderLayout.SOUTH);

        return c;
    }
}
