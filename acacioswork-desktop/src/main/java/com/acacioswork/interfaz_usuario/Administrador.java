package com.acacioswork.interfaz_usuario;

import java.awt.BorderLayout;
import java.awt.CardLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Cursor;
import java.awt.Font;
import java.awt.GradientPaint;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.RenderingHints;
import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.Timer;
import javax.swing.border.EmptyBorder;
import com.acacioswork.model.Usuario;
import com.acacioswork.util.SessionManager;

/**
 * Contenedor principal (Shell) del Dashboard de Administración de AcaciosWork.
 * Gestiona el menú de navegación lateral y el intercambio de paneles principales asíncronos.
 * @author RADJ
 */
public class Administrador extends JPanel {

    public static final Color BG_DARK = new Color(15, 23, 42);
    public static final Color BG_CARD = new Color(30, 41, 59);
    public static final Color BG_SIDEBAR = new Color(2, 6, 23);
    public static final Color TEXT_MAIN = new Color(248, 250, 252);
    public static final Color TEXT_MUTED = new Color(148, 163, 184);
    public static final Color PRIMARY = new Color(99, 102, 241);
    public static final Color ACCENT = new Color(16, 185, 129);
    public static final Color DANGER = new Color(239, 68, 68);

    private JPanel contentPanel;
    private CardLayout cardLayout;
    private JButton btnAlertas;

    private WelcomeTab welcomeTab;
    private InventarioTab inventarioTab;
    private ProveedoresTab proveedoresTab;
    private ClientesTab clientesTab;
    private UsuariosTab usuariosTab;
    private ReportesTab reportesTab;
    private PreguntasInteligentesTab preguntasInteligentesTab;
    private AlertasTab alertasTab;
    private GraficosTab graficosTab;
    private HistorialTab historialTab;

    public Administrador() {
        try {
            com.acacioswork.util.ConfiguracionManager.loadConfiguracion();
            setLayout(new BorderLayout());
            setBackground(BG_DARK);
            add(buildToolbar(), BorderLayout.WEST);

            cardLayout = new CardLayout();
            contentPanel = new JPanel(cardLayout);
            contentPanel.setBackground(BG_DARK);

            // Instanciar pestañas modulares
            welcomeTab = new WelcomeTab(this);
            inventarioTab = new InventarioTab(this);
            proveedoresTab = new ProveedoresTab();
            clientesTab = new ClientesTab();
            usuariosTab = new UsuariosTab();
            reportesTab = new ReportesTab(this);
            preguntasInteligentesTab = new PreguntasInteligentesTab();
            alertasTab = new AlertasTab(this);
            graficosTab = new GraficosTab();
            historialTab = new HistorialTab();

            contentPanel.add(welcomeTab, "welcome");
            contentPanel.add(inventarioTab, "inventario");
            contentPanel.add(new PuntoDeVenta(false), "vender");
            contentPanel.add(proveedoresTab, "proveedores");
            contentPanel.add(clientesTab, "clientes");
            contentPanel.add(usuariosTab, "usuarios");
            contentPanel.add(reportesTab, "reportes");
            contentPanel.add(preguntasInteligentesTab, "preguntas-inteligentes");
            contentPanel.add(alertasTab, "alertas");
            contentPanel.add(graficosTab, "graficos");
            contentPanel.add(historialTab, "historial");
            contentPanel.add(new GestionConfiguracion(), "configuracion");

            add(contentPanel, BorderLayout.CENTER);
            cardLayout.show(contentPanel, "welcome");

            refreshWelcomeStats();
        } catch (Exception e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(null, "Error al cargar el Dashboard: " + e.getMessage());
        }
    }

    public void refreshWelcomeStats() {
        if (welcomeTab != null) {
            welcomeTab.refresh();
        }
    }

    public void updateAlertasPulsing(int bajoCount) {
        if (btnAlertas != null) {
            btnAlertas.putClientProperty("pulsing", bajoCount > 0);
            btnAlertas.repaint();
        }
    }

    public void generarReporte(String tipo) {
        ReportExporter.generarReporte(tipo, this);
    }

    private JPanel buildToolbar() {
        JPanel toolbar = new JPanel(new BorderLayout());
        toolbar.setBackground(BG_SIDEBAR);
        toolbar.setPreferredSize(new java.awt.Dimension(260, 0));
        toolbar.setBorder(BorderFactory.createMatteBorder(0, 0, 0, 1, new Color(255, 255, 255, 15)));

        JPanel logoPanel = new JPanel(new GridBagLayout());
        logoPanel.setOpaque(false);
        logoPanel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));

        GridBagConstraints gbcLogo = new GridBagConstraints();
        gbcLogo.gridx = 0;
        gbcLogo.gridy = 0;

        java.net.URL logoUrl = getClass().getResource("/images/logo.png");
        if (logoUrl != null) {
            javax.swing.ImageIcon origIcon = new javax.swing.ImageIcon(logoUrl);
            java.awt.Image scaledImage = origIcon.getImage().getScaledInstance(36, 36, java.awt.Image.SCALE_SMOOTH);
            JLabel logoIcon = new JLabel(new javax.swing.ImageIcon(scaledImage));
            logoIcon.setBorder(BorderFactory.createEmptyBorder(0, 0, 0, 10));
            logoPanel.add(logoIcon, gbcLogo);
            gbcLogo.gridx = 1;
        }

        JLabel brand = new JLabel("AcaciosWork");
        brand.setForeground(PRIMARY);
        brand.setFont(new Font("Inter", Font.BOLD, 28));
        logoPanel.add(brand, gbcLogo);

        String userName = "Usuario";
        if (SessionManager.getUsuario() != null) {
            Usuario u = SessionManager.getUsuario();
            userName = u.getNombre() + (u.getApellido() != null && !u.getApellido().equals("—") ? " " + u.getApellido() : "");
        }

        JLabel lblUser = new JLabel("👤 " + userName);
        lblUser.setForeground(new Color(203, 213, 225));
        lblUser.setFont(new Font("Inter", Font.BOLD, 15));

        gbcLogo.gridy = 1;
        gbcLogo.insets = new Insets(8, 0, 0, 0);
        logoPanel.add(lblUser, gbcLogo);
        toolbar.add(logoPanel, BorderLayout.NORTH);

        JPanel menuContainer = new JPanel(new BorderLayout());
        menuContainer.setOpaque(false);

        JPanel centerPanel = new JPanel(new GridBagLayout());
        centerPanel.setOpaque(false);
        GridBagConstraints gbcCenter = new GridBagConstraints();
        gbcCenter.fill = GridBagConstraints.HORIZONTAL;
        gbcCenter.insets = new Insets(5, 16, 5, 16);
        gbcCenter.gridx = 0;
        gbcCenter.weightx = 1.0;
        gbcCenter.gridy = 0;

        // Orden de botones alineado con la versión web
        String[][] sections = {
                { "🏠 Inicio", "welcome" },
                { "Inventario", "inventario" },
                { "🛒 Vender", "vender" },
                { "Proveedores", "proveedores" },
                { "Clientes", "clientes" },
                { "Reportes", "reportes" },
                { "⚠ Alertas Stock", "alertas" },
                { "Preguntas Inteligentes", "preguntas-inteligentes" },
                { "📊 Gráficos", "graficos" },
                { "📋 Historial", "historial" },
                { "Usuarios", "usuarios" },
                { "⚙ Configuración", "configuracion" }
        };

        for (String[] s : sections) {
            JButton btn = s[1].equals("alertas") ? (btnAlertas = createToolbarBtn(s[0], s[1])) : createToolbarBtn(s[0], s[1]);
            btn.addActionListener(e -> {
                setActiveBtn(btn);
                cardLayout.show(contentPanel, s[1]);
                if (s[1].equals("welcome")) refreshWelcomeStats();
                if (s[1].equals("alertas")) alertasTab.refresh();
                if (s[1].equals("preguntas-inteligentes")) preguntasInteligentesTab.clearIqCache();
                if (s[1].equals("reportes")) reportesTab.refresh();
                if (s[1].equals("inventario")) inventarioTab.refresh();
                if (s[1].equals("proveedores")) proveedoresTab.refresh();
                if (s[1].equals("clientes")) clientesTab.refresh();
                if (s[1].equals("usuarios")) usuariosTab.refresh();
                if (s[1].equals("graficos")) graficosTab.refresh();
                if (s[1].equals("historial")) historialTab.refresh();
            });
            centerPanel.add(btn, gbcCenter);
            gbcCenter.gridy++;
            if (s[1].equals("welcome")) btn.putClientProperty("active", true);
        }
        menuContainer.add(centerPanel, BorderLayout.NORTH);

        JPanel rightPanel = new JPanel(new GridBagLayout());
        rightPanel.setOpaque(false);
        rightPanel.setBorder(BorderFactory.createEmptyBorder(16, 16, 24, 16));
        GridBagConstraints gbcRight = new GridBagConstraints();
        gbcRight.gridx = 0;
        gbcRight.fill = GridBagConstraints.HORIZONTAL;
        gbcRight.weightx = 1.0;

        JButton btnSalir = new JButton("✕ Cerrar Sesión") {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setPaint(new GradientPaint(0, 0, new Color(255, 59, 48), 0, getHeight(), new Color(255, 45, 85)));
                g2.fillRoundRect(0, 0, getWidth(), getHeight(), 10, 10);
                g2.dispose();
                super.paintComponent(g);
            }
        };
        btnSalir.setForeground(Color.WHITE);
        btnSalir.setFont(new Font("Inter", Font.BOLD, 13));
        btnSalir.setBorder(new EmptyBorder(10, 16, 10, 16));
        btnSalir.setFocusPainted(false);
        btnSalir.setContentAreaFilled(false);
        btnSalir.setOpaque(false);
        btnSalir.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        btnSalir.addActionListener(e -> MainFrame.navigateTo(new Login()));

        gbcRight.gridy = 0;
        rightPanel.add(btnSalir, gbcRight);

        JLabel lblCopyright = new JLabel("<html><center>Copyright © 2026 Rubiel Andrés Díaz<br>Contacto: andresrubiel@gmail.com</center></html>");
        lblCopyright.setForeground(TEXT_MUTED);
        lblCopyright.setFont(new Font("Inter", Font.PLAIN, 10));
        lblCopyright.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);

        gbcRight.gridy = 1;
        gbcRight.insets = new Insets(12, 0, 0, 0);
        rightPanel.add(lblCopyright, gbcRight);

        menuContainer.add(rightPanel, BorderLayout.SOUTH);
        toolbar.add(menuContainer, BorderLayout.CENTER);

        // Animación marca
        final Color colorBright = new Color(57, 255, 20);
        final Color colorDim = new Color(20, 90, 7);
        final long startTime = System.currentTimeMillis();

        Timer pulseTimer = new Timer(50, e -> {
            long elapsed = System.currentTimeMillis() - startTime;
            double progress = (elapsed % 2000) / 2000.0;
            double sinVal = Math.sin(progress * 2.0 * Math.PI);
            double factor = (sinVal + 1.0) / 2.0;

            int r = (int) (colorDim.getRed() + factor * (colorBright.getRed() - colorDim.getRed()));
            int g = (int) (colorDim.getGreen() + factor * (colorBright.getGreen() - colorDim.getGreen()));
            int b = (int) (colorDim.getBlue() + factor * (colorBright.getBlue() - colorDim.getBlue()));

            Color currentColor = new Color(r, g, b);
            brand.setForeground(currentColor);
            lblUser.setForeground(currentColor);
        });
        pulseTimer.start();

        return toolbar;
    }

    private void setActiveBtn(JButton btn) {
        for (Component c : btn.getParent().getComponents()) {
            if (c instanceof JButton && Boolean.TRUE.equals(((JButton) c).getClientProperty("isTab"))) {
                ((JButton) c).putClientProperty("active", false);
            }
        }
        btn.putClientProperty("active", true);
        btn.getParent().repaint();
    }

    private JButton createToolbarBtn(String text, String secName) {
        return new AcaciosToolbarButton(text, secName);
    }
}
