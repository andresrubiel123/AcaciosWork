package com.acacioswork.interfaz_usuario;

import java.awt.BasicStroke;
import java.awt.BorderLayout;
import java.awt.CardLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Cursor;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.GradientPaint;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.GridLayout;
import java.awt.RenderingHints;
import java.awt.geom.Path2D;
import java.io.File;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.SwingUtilities;
import javax.swing.SwingWorker;
import javax.swing.border.EmptyBorder;
import javax.swing.table.DefaultTableModel;

import com.acacioswork.interfaz_usuario.dialogos.ClienteDialog;
import com.acacioswork.interfaz_usuario.dialogos.ProductoDialog;
import com.acacioswork.interfaz_usuario.dialogos.ProveedorDialog;
import com.acacioswork.interfaz_usuario.dialogos.UsuarioDialog;
import com.acacioswork.model.Producto;
import com.acacioswork.model.Usuario;
import com.acacioswork.util.ApiClient;
import com.acacioswork.util.SessionManager;

@SuppressWarnings("unchecked")
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
    private JTable tableAlertas;
    private JTable tableHome;
    private VentasChartPanel chartPanel;
    private JButton btnAlertas;
    private JPanel statsClientes;
    private JPanel statsInventario;

    public Administrador() {
        try {
            com.acacioswork.util.ConfiguracionManager.loadConfiguracion();
            setLayout(new BorderLayout());
            setBackground(BG_DARK);
            add(buildToolbar(), BorderLayout.WEST);

            cardLayout = new CardLayout();
            contentPanel = new JPanel(cardLayout);
            contentPanel.setBackground(BG_DARK);

            contentPanel.add(buildWelcomePanel(), "welcome");
            contentPanel.add(buildInventarioPanel(), "inventario");
            contentPanel.add(new PuntoDeVenta(false), "vender");
            contentPanel.add(buildProveedoresPanel(), "proveedores");
            contentPanel.add(buildClientesPanel(), "clientes");
            contentPanel.add(buildUsuariosPanel(), "usuarios");
            contentPanel.add(buildReportesPanel(), "reportes");
            contentPanel.add(buildAlertasPanel(), "alertas");
            contentPanel.add(new GestionConfiguracion(), "configuracion");

            add(contentPanel, BorderLayout.CENTER);
            cardLayout.show(contentPanel, "welcome");
            refreshWelcomeStats();
        } catch (Exception e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(null, "Error al cargar el Dashboard: " + e.getMessage());
        }
    }

    private JPanel buildToolbar() {
        JPanel toolbar = new JPanel(new BorderLayout());
        toolbar.setBackground(BG_SIDEBAR);
        toolbar.setPreferredSize(new java.awt.Dimension(260, 0));
        toolbar.setBorder(BorderFactory.createMatteBorder(0, 0, 0, 1, new Color(255, 255, 255, 15)));

        // Top Section (Logo) - Vertically Centered using GridBagLayout
        JPanel logoPanel = new JPanel(new java.awt.GridBagLayout());
        logoPanel.setOpaque(false);
        logoPanel.setBorder(BorderFactory.createEmptyBorder(24, 16, 24, 16));
        java.awt.GridBagConstraints gbcLogo = new java.awt.GridBagConstraints();
        gbcLogo.gridx = 0;
        gbcLogo.gridy = 0;

        JLabel brand = new JLabel("AcaciosWork");
        brand.setForeground(PRIMARY);
        brand.setFont(new Font("Inter", Font.BOLD, 28));
        logoPanel.add(brand, gbcLogo);

        // Nombre de la persona logueada debajo del título del programa
        String userName = "Usuario";
        if (SessionManager.getUsuario() != null) {
            Usuario u = SessionManager.getUsuario();
            userName = u.getNombre()
                    + (u.getApellido() != null && !u.getApellido().equals("—") ? " " + u.getApellido() : "");
        }

        JLabel lblUser = new JLabel("👤 " + userName);
        lblUser.setForeground(new Color(203, 213, 225));
        lblUser.setFont(new Font("Inter", Font.BOLD, 15));

        gbcLogo.gridy = 1;
        gbcLogo.insets = new java.awt.Insets(8, 0, 0, 0);
        logoPanel.add(lblUser, gbcLogo);

        toolbar.add(logoPanel, BorderLayout.NORTH);

        // Center container holding menu buttons (top) and user/exit section (bottom)
        JPanel menuContainer = new JPanel(new BorderLayout());
        menuContainer.setOpaque(false);

        // Center section (Navigation Buttons) - Vertically Centered using GridBagLayout
        JPanel centerPanel = new JPanel(new java.awt.GridBagLayout());
        centerPanel.setOpaque(false);
        java.awt.GridBagConstraints gbcCenter = new java.awt.GridBagConstraints();
        gbcCenter.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gbcCenter.insets = new java.awt.Insets(5, 16, 5, 16);
        gbcCenter.gridx = 0;
        gbcCenter.weightx = 1.0;
        gbcCenter.gridy = 0;

        String[][] sections = {
                { "🏠 Inicio", "welcome" },
                { "Inventario", "inventario" },
                { "🛒 Vender", "vender" },
                { "Proveedores", "proveedores" },
                { "Clientes", "clientes" },
                { "Usuarios", "usuarios" },
                { "Reportes", "reportes" },
                { "⚠ Alertas Stock", "alertas" },
                { "⚙ Configuración", "configuracion" }
        };

        for (String[] s : sections) {
            JButton btn;
            if (s[1].equals("alertas")) {
                btnAlertas = createToolbarBtn(s[0], s[1]);
                btn = btnAlertas;
            } else {
                btn = createToolbarBtn(s[0], s[1]);
            }
            btn.addActionListener(e -> {
                setActiveBtn(btn);
                cardLayout.show(contentPanel, s[1]);
                if (s[1].equals("welcome")) {
                    refreshWelcomeStats();
                }
                if (s[1].equals("alertas")) {
                    refreshAlertas();
                }
                if (s[1].equals("reportes")) {
                    refreshReportesChart();
                }
            });
            centerPanel.add(btn, gbcCenter);
            gbcCenter.gridy++;
            if (s[1].equals("welcome")) {
                btn.putClientProperty("active", true);
            }
        }
        menuContainer.add(centerPanel, BorderLayout.NORTH);

        // Bottom section (User Profile + Logout Button) - Vertically Centered using GridBagLayout
        JPanel rightPanel = new JPanel(new java.awt.GridBagLayout());
        rightPanel.setOpaque(false);
        rightPanel.setBorder(BorderFactory.createEmptyBorder(16, 16, 24, 16));
        java.awt.GridBagConstraints gbcRight = new java.awt.GridBagConstraints();
        gbcRight.gridx = 0;
        gbcRight.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gbcRight.weightx = 1.0;

        JButton btnSalir = new JButton("✕ Salir") {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                // Red gradient matching var(--btn-exit)
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
        gbcRight.insets = new java.awt.Insets(0, 0, 0, 0);
        rightPanel.add(btnSalir, gbcRight);

        menuContainer.add(rightPanel, BorderLayout.SOUTH);
        toolbar.add(menuContainer, BorderLayout.CENTER);

        /** Animación de pulsación de verde neón para la marca y el usuario. @author RADJ */
        final Color colorBright = new Color(57, 255, 20);
        final Color colorDim = new Color(20, 90, 7);
        final long startTime = System.currentTimeMillis();

        javax.swing.Timer pulseTimer = new javax.swing.Timer(50, e -> {
            long elapsed = System.currentTimeMillis() - startTime;
            double progress = (elapsed % 2000) / 2000.0; // 2 seconds cycle
            double sinVal = Math.sin(progress * 2.0 * Math.PI);
            double factor = (sinVal + 1.0) / 2.0; // range 0.0 to 1.0

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
        JButton btn = new JButton(text) {
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
                    // Active style: Same orange gradient
                    c1 = new Color(249, 115, 22);
                    c2 = new Color(239, 68, 68);
                    setForeground(Color.WHITE);
                } else if ("alertas".equals(secName)) {
                    // Alertas Stock button
                    if (Boolean.TRUE.equals(getClientProperty("pulsing"))) {
                        // Pulsing / Red style
                        c1 = new Color(255, 59, 48);
                        c2 = new Color(255, 45, 85);
                        setForeground(Color.WHITE);
                    } else {
                        // Dark translucent style
                        c1 = new Color(30, 41, 59, 200);
                        c2 = new Color(30, 41, 59, 200);
                        setForeground(new Color(239, 68, 68));
                    }
                } else {
                    // Inactive nav style: Dark slate translucent
                    c1 = new Color(30, 41, 59, 120);
                    c2 = new Color(30, 41, 59, 120);
                    setForeground(TEXT_MUTED);
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
        };
        btn.setHorizontalAlignment(javax.swing.SwingConstants.LEFT);
        btn.setForeground(TEXT_MUTED);
        btn.setFont(new Font("Inter", Font.BOLD, 13));
        btn.setBorder(new EmptyBorder(6, 16, 6, 16));
        btn.setFocusPainted(false);
        btn.setContentAreaFilled(false);
        btn.setOpaque(false);
        btn.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        btn.putClientProperty("isTab", true);
        return btn;
    }

    private JPanel buildInventarioPanel() {
        JPanel panel = createContentPanel();

        JTable table = buildStyledTable(
                new String[] { "ID", "Código", "Nombre", "Unidad", "Stock", "P. Compra", "P. Venta", "IVA", "Estado",
                        "Acciones" });
        hideColumn(table, 0);
        table.getColumnModel().getColumn(1).setPreferredWidth(100); // Código
        table.getColumnModel().getColumn(2).setPreferredWidth(350); // Nombre
        table.getColumnModel().getColumn(3).setPreferredWidth(100); // Unidad
        table.getColumnModel().getColumn(4).setPreferredWidth(110); // Stock
        table.getColumnModel().getColumn(5).setPreferredWidth(110); // P. Compra
        table.getColumnModel().getColumn(6).setPreferredWidth(110); // P. Venta
        table.getColumnModel().getColumn(7).setPreferredWidth(80); // IVA
        table.getColumnModel().getColumn(8).setPreferredWidth(100); // Estado
        table.getColumnModel().getColumn(8).setCellRenderer(new EstadoCellRenderer());
        table.getColumnModel().getColumn(4).setCellRenderer(new StockNumberCellRenderer());

        setupAccionesColumn(table,
                () -> editarProductoInv(table, statsInventario),
                () -> eliminarGeneric(table, "/productos", "Producto", () -> refreshInventario(table, statsInventario)));

        JButton bAdd = createActionButton("+ Nuevo Producto", ACCENT);
        bAdd.addActionListener(e -> agregarProductoDash(table, statsInventario));

        panel.add(buildSectionHeader("Inventario de Productos", "Control total de existencias y precios", bAdd),
                BorderLayout.NORTH);

        JPanel center = new JPanel(new BorderLayout(0, 12));
        center.setOpaque(false);

        JPanel tableContainer = new JPanel(new BorderLayout(0, 8));
        tableContainer.setOpaque(false);
        tableContainer.add(buildSearchPanel(table), BorderLayout.NORTH);
        tableContainer.add(wrapTable(table), BorderLayout.CENTER);

        center.add(tableContainer, BorderLayout.CENTER);
        panel.add(center, BorderLayout.CENTER);

        refreshInventario(table, statsInventario);
        return panel;
    }

    private JPanel buildProveedoresPanel() {
        JPanel panel = createContentPanel();
        JTable table = buildStyledTable(
                new String[] { "ID", "Nombre", "Teléfono", "Email", "Doc/NIT", "Cuenta Bancaria", "Estado",
                        "Acciones" });
        hideColumn(table, 0);
        table.getColumnModel().getColumn(6).setCellRenderer(new EstadoCellRenderer());

        setupAccionesColumn(table,
                () -> editarProveedorDash(table),
                () -> eliminarGeneric(table, "/proveedores", "Proveedor", () -> refreshProveedores(table)));

        JButton bAdd = createActionButton("+ Nuevo Proveedor", ACCENT);
        bAdd.addActionListener(e -> agregarProveedorDash(table));

        panel.add(buildSectionHeader("Proveedores", "Gestión de contactos y suministradores", bAdd),
                BorderLayout.NORTH);
        JPanel tableContainer = new JPanel(new BorderLayout(0, 8));
        tableContainer.setOpaque(false);
        tableContainer.add(buildSearchPanel(table), BorderLayout.NORTH);
        tableContainer.add(wrapTable(table), BorderLayout.CENTER);

        panel.add(tableContainer, BorderLayout.CENTER);
        refreshProveedores(table);
        return panel;
    }

    private JPanel buildClientesPanel() {
        JPanel panel = createContentPanel();

        statsClientes = new JPanel(new GridLayout(1, 2, 12, 0));
        statsClientes.setOpaque(false);
        statsClientes.setBorder(new EmptyBorder(0, 0, 16, 0));
        statsClientes.add(buildStatCard("Total Clientes", "0", TEXT_MAIN));
        statsClientes.add(buildStatCard("Activos", "0", ACCENT));

        JTable table = buildStyledTable(
                new String[] { "ID", "Nombre", "Identificación", "Teléfono", "Email", "Frecuente", "Estado",
                        "Acciones" });
        hideColumn(table, 0);
        table.getColumnModel().getColumn(6).setCellRenderer(new EstadoCellRenderer());

        setupAccionesColumn(table,
                () -> editarClienteDash(table),
                () -> eliminarGeneric(table, "/clientes", "Cliente", () -> refreshClientes(table)));

        JButton bAdd = createActionButton("+ Nuevo Cliente", ACCENT);
        bAdd.addActionListener(e -> agregarClienteDash(table));

        panel.add(buildSectionHeader("Clientes", "Base de datos de clientes registrados", bAdd), BorderLayout.NORTH);

        JPanel center = new JPanel(new BorderLayout(0, 12));
        center.setOpaque(false);
        center.add(statsClientes, BorderLayout.NORTH);

        JPanel tableContainer = new JPanel(new BorderLayout(0, 8));
        tableContainer.setOpaque(false);
        tableContainer.add(buildSearchPanel(table), BorderLayout.NORTH);
        tableContainer.add(wrapTable(table), BorderLayout.CENTER);

        center.add(tableContainer, BorderLayout.CENTER);
        panel.add(center, BorderLayout.CENTER);

        refreshClientes(table);
        return panel;
    }

    private JPanel buildUsuariosPanel() {
        JPanel panel = createContentPanel();
        JTable table = buildStyledTable(new String[] { "ID", "Nombre", "Usuario", "Doc/Id", "Estado", "Acciones" });
        hideColumn(table, 0);
        table.getColumnModel().getColumn(4).setCellRenderer(new EstadoCellRenderer());

        setupAccionesColumn(table,
                () -> editarUsuarioDash(table),
                () -> eliminarUsuarioDash(table));

        JButton bAdd = createActionButton("+ Nuevo Usuario", ACCENT);
        bAdd.addActionListener(e -> agregarUsuarioDash(table));

        panel.add(buildSectionHeader("Usuarios del Sistema", "Administración de accesos y roles", bAdd),
                BorderLayout.NORTH);
        JPanel tableContainer = new JPanel(new BorderLayout(0, 8));
        tableContainer.setOpaque(false);
        tableContainer.add(buildSearchPanel(table), BorderLayout.NORTH);
        tableContainer.add(wrapTable(table), BorderLayout.CENTER);

        panel.add(tableContainer, BorderLayout.CENTER);
        refreshUsuarios(table);
        return panel;
    }

    private JPanel buildReportesPanel() {
        JPanel panel = createContentPanel();
        panel.add(buildSectionHeader("Reportes", "Generación y exportación de informes", (JButton) null),
                BorderLayout.NORTH);

        JPanel grid = new JPanel(new GridLayout(0, 3, 16, 16));
        grid.setOpaque(false);

        String[][] rpts = {
                { "📦 Inventario General", "Lista completa de productos con stock y precios actuales.", "inventario" },
                { "⚠️ Productos con Stock Bajo", "Listado de artículos por debajo del stock mínimo definido.",
                        "stock-bajo" },
                { "👥 Reporte de Clientes", "Base de datos completa de clientes con información de contacto.",
                        "clientes" },
                { "🏭 Directorio de Proveedores", "Directorio de proveedores registrados con NIT y contacto.",
                        "proveedores" },
                { "👤 Usuarios del Sistema", "Informe detallado de usuarios, roles y accesos.", "usuarios" },
                { "🛒 Reporte de Ventas", "Listado histórico de todas las ventas con fecha, clientes y totales.", "ventas" },
                { "📈 Reporte de Ganancias", "Análisis de rentabilidad detallando costos, ingresos y margen de ganancia.", "ganancias" },
                { "📊 Resumen Ejecutivo", "Métricas principales de inventario y estado general de la empresa.",
                        "resumen" }
        };

        for (String[] r : rpts) {
            grid.add(buildReportCard(r[0], r[1], r[2]));
        }

        JScrollPane scroll = new JScrollPane(grid);
        scroll.setBorder(BorderFactory.createEmptyBorder());
        scroll.setOpaque(false);
        scroll.getViewport().setOpaque(false);
        panel.add(scroll, BorderLayout.CENTER);

        /** Inicializar y agregar el panel del gráfico de ventas. @author RADJ */
        chartPanel = new VentasChartPanel();
        chartPanel.setPreferredSize(new java.awt.Dimension(0, 320));
        panel.add(chartPanel, BorderLayout.SOUTH);

        return panel;
    }

    private JPanel buildAlertasPanel() {
        JPanel panel = createContentPanel();

        JButton bPdf = new JButton("📄 Descargar lista PDF") {
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
        bPdf.setForeground(Color.WHITE);
        bPdf.setFont(new Font("Inter", Font.BOLD, 12));
        bPdf.setBorder(new EmptyBorder(8, 18, 8, 18));
        bPdf.setFocusPainted(false);
        bPdf.setContentAreaFilled(false);
        bPdf.setOpaque(false);
        bPdf.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));

        panel.add(
                buildSectionHeader("Alertas de Stock Crítico",
                        "Productos con existencias en nivel mínimo de reabastecimiento", bPdf),
                BorderLayout.NORTH);

        tableAlertas = buildStyledTable(
                new String[] { "ID", "Producto", "Stock Actual", "Mínimo", "Proveedor", "Acción" });
        hideColumn(tableAlertas, 0);

        // Centrar y colorear columna Stock Actual
        tableAlertas.getColumnModel().getColumn(2).setCellRenderer(new javax.swing.table.DefaultTableCellRenderer() {
            {
                setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
            }

            @Override
            public Component getTableCellRendererComponent(JTable t, Object v, boolean s, boolean f, int r, int c) {
                Component comp = super.getTableCellRendererComponent(t, v, s, f, r, c);
                comp.setFont(t.getFont());
                if (s) {
                    comp.setBackground(t.getSelectionBackground());
                    comp.setForeground(t.getSelectionForeground());
                } else {
                    comp.setBackground(t.getBackground());
                    comp.setForeground(TEXT_MAIN);
                    try {
                        Object val = t.getValueAt(r, 2);
                        if (val != null) {
                            int stock = Integer.parseInt(val.toString().replaceAll("[^0-9]", ""));
                            if (stock == 0) {
                                comp.setBackground(new Color(239, 68, 68, 40)); // Rojo suave
                                comp.setForeground(DANGER);
                                comp.setFont(t.getFont().deriveFont(Font.BOLD));
                            } else {
                                comp.setBackground(new Color(245, 158, 11, 40)); // Naranja suave
                                comp.setForeground(new Color(245, 158, 11));
                                comp.setFont(t.getFont().deriveFont(Font.BOLD));
                            }
                        }
                    } catch (Exception ex) {
                    }
                }
                return comp;
            }
        });

        // Centrar columna Mínimo
        tableAlertas.getColumnModel().getColumn(3).setCellRenderer(new javax.swing.table.DefaultTableCellRenderer() {
            {
                setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
            }

            @Override
            public Component getTableCellRendererComponent(JTable t, Object v, boolean s, boolean f, int r, int c) {
                Component comp = super.getTableCellRendererComponent(t, v, s, f, r, c);
                comp.setFont(t.getFont());
                if (s) {
                    comp.setBackground(t.getSelectionBackground());
                    comp.setForeground(t.getSelectionForeground());
                } else {
                    comp.setBackground(t.getBackground());
                    comp.setForeground(TEXT_MUTED);
                }
                return comp;
            }
        });

        // Diseñar botón Ver Proveedor en columna Acción
        tableAlertas.getColumnModel().getColumn(5).setCellRenderer(new javax.swing.table.DefaultTableCellRenderer() {
            {
                setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
            }

            @Override
            public Component getTableCellRendererComponent(JTable t, Object v, boolean s, boolean f, int r, int c) {
                Component comp = super.getTableCellRendererComponent(t, v, s, f, r, c);
                comp.setFont(t.getFont());
                if (s) {
                    comp.setBackground(t.getSelectionBackground());
                    comp.setForeground(t.getSelectionForeground());
                } else {
                    comp.setBackground(new Color(99, 102, 241, 30)); // Indigo translúcido
                    comp.setForeground(PRIMARY);
                    comp.setFont(t.getFont().deriveFont(Font.BOLD));
                }
                return comp;
            }
        });

        JPanel tableContainer = new JPanel(new BorderLayout(0, 8));
        tableContainer.setOpaque(false);
        tableContainer.add(buildSearchPanel(tableAlertas), BorderLayout.NORTH);
        tableContainer.add(wrapTable(tableAlertas), BorderLayout.CENTER);

        panel.add(tableContainer, BorderLayout.CENTER);

        bPdf.addActionListener(e -> generarReporte("stock-bajo"));

        // Botón Ver Proveedor
        tableAlertas.addMouseListener(new java.awt.event.MouseAdapter() {
            @Override
            public void mousePressed(java.awt.event.MouseEvent e) {
                int row = tableAlertas.rowAtPoint(e.getPoint());
                int col = tableAlertas.columnAtPoint(e.getPoint());
                if (col == 5 && row != -1) { // Columna Acción
                    Administrador.this.mostrarInfoProveedor(tableAlertas.getValueAt(row, 0));
                }
            }
        });

        refreshAlertas(tableAlertas);
        return panel;
    }

    private void hideColumn(JTable table, int index) {
        table.getColumnModel().getColumn(index).setMinWidth(0);
        table.getColumnModel().getColumn(index).setMaxWidth(0);
        table.getColumnModel().getColumn(index).setPreferredWidth(0);
    }

    private void eliminarGeneric(JTable table, String endpoint, String entityName, Runnable onFinish) {
        int row = table.getSelectedRow();
        if (row == -1) {
            JOptionPane.showMessageDialog(this, "Seleccione un " + entityName);
            return;
        }

        // Obtenemos el ID (columna 0, oculta)
        Object idVal = table.getValueAt(row, 0);
        String nameVal = table.getValueAt(row, 1).toString();

        if (JOptionPane.showConfirmDialog(this, "¿Eliminar " + entityName + ": " + nameVal + "?", "Confirmar Acción",
                JOptionPane.OK_CANCEL_OPTION, JOptionPane.WARNING_MESSAGE) == JOptionPane.OK_OPTION) {
            try {
                ApiClient.delete(endpoint + "/" + idVal);
                JOptionPane.showMessageDialog(this, entityName + " eliminado correctamente.");
                if (onFinish != null)
                    onFinish.run();
            } catch (Exception e) {
                JOptionPane.showMessageDialog(this, "Error al eliminar: " + e.getMessage());
            }
        }
    }

    private void refreshInventario(JTable table, JPanel stats) {
        new SwingWorker<Void, Void>() {
            @Override
            protected Void doInBackground() throws Exception {
                try {
                    Object[] data = ApiClient.get("/productos", Object[].class);
                    SwingUtilities.invokeLater(() -> populateInventarioTable(table, data, stats));
                } catch (Exception e) {
                }
                return null;
            }
        }.execute();
    }

    private void refreshProveedores(JTable table) {
        loadTable(table, "/proveedores", row -> new Object[] {
                id(row),
                str(row, "nombre"),
                str(row, "telefono"),
                str(row, "email"),
                str(row, "numeroDocumento"),
                str(row, "cuentaBancaria"),
                str(row, "activo").equals("1") ? "Activo" : "Inactivo",
                ""
        });
    }

    private void refreshClientes(JTable table) {
        loadTable(table, "/clientes", row -> new Object[] {
                id(row),
                str(row, "nombre"),
                str(row, "numeroDocumento"),
                str(row, "telefono"),
                str(row, "email"),
                str(row, "frecuente").equals("true") ? "Sí" : "No",
                str(row, "activo").equals("1") ? "Activo" : "Inactivo",
                ""
        });

        if (statsClientes != null) {
            new SwingWorker<Void, Void>() {
                @Override
                protected Void doInBackground() {
                    try {
                        Object[] data = ApiClient.get("/clientes", Object[].class);
                        int activosCount = 0;
                        if (data != null) {
                            for (Object raw : data) {
                                java.util.Map<String, Object> c = (java.util.Map<String, Object>) raw;
                                if ("1".equals(str(c, "activo"))) {
                                    activosCount++;
                                }
                            }
                            final int total = data.length;
                            final int activos = activosCount;
                            SwingUtilities.invokeLater(() -> {
                                updateStatCard((JPanel) statsClientes.getComponents()[0], String.valueOf(total));
                                updateStatCard((JPanel) statsClientes.getComponents()[1], String.valueOf(activos));
                            });
                        }
                    } catch (Exception e) {
                    }
                    return null;
                }
            }.execute();
        }
    }

    private void populateInventarioTable(JTable table, Object[] rows, JPanel stats) {
        DefaultTableModel model = (DefaultTableModel) table.getModel();
        model.setRowCount(0);
        if (rows == null)
            return;
        int bajo = 0;
        double valor = 0;
        double valorCosto = 0;
        for (Object raw : rows) {
            java.util.Map<String, Object> p = (java.util.Map<String, Object>) raw;
            Long id = id(p);
            int qty = num(p, "stockActual");
            int min = p.get("stockMinimo") != null ? num(p, "stockMinimo") : 5;
            int opt = p.get("stockOptimo") != null ? num(p, "stockOptimo") : 200;
            double precioCompra = dbl(p, "precioCompra");
            double precioVenta = dbl(p, "precioVenta");
            valor += qty * precioVenta;
            valorCosto += qty * precioCompra;
            if (qty <= min)
                bajo++;

            String estadoLabel = "1".equals(str(p, "estado")) ? "Activo" : "Inactivo";
            String ivaLabel = str(p, "iva") != null ? str(p, "iva") + "%" : "0%";
            String unidadMedida = str(p, "unidadMedida") != null && !str(p, "unidadMedida").equals("—")
                    ? str(p, "unidadMedida")
                    : "Unidad";

            model.addRow(new Object[] {
                    id,
                    str(p, "codigoBarras"),
                    str(p, "nombre"),
                    unidadMedida,
                    new StockData(qty, min, opt),
                    com.acacioswork.util.ConfiguracionManager.formatCurrency(precioCompra),
                    com.acacioswork.util.ConfiguracionManager.formatCurrency(precioVenta),
                    ivaLabel,
                    estadoLabel,
                    ""
            });
        }

        final double finalValor = valor;
        final double finalCosto = valorCosto;
        final double finalUtilidad = valor - valorCosto;
        final int finalBajo = bajo;

        java.text.NumberFormat nf = java.text.NumberFormat.getNumberInstance(java.util.Locale.GERMANY);
        nf.setMaximumFractionDigits(0);

        updateStatCard((JPanel) stats.getComponents()[0], String.valueOf(rows.length));
        updateStatCard((JPanel) stats.getComponents()[1], String.valueOf(finalBajo));
        updateStatCard((JPanel) stats.getComponents()[2], com.acacioswork.util.ConfiguracionManager.formatCurrency(finalValor));
        updateStatCard((JPanel) stats.getComponents()[3], com.acacioswork.util.ConfiguracionManager.formatCurrency(finalCosto));
        updateStatCard((JPanel) stats.getComponents()[4], com.acacioswork.util.ConfiguracionManager.formatCurrency(finalUtilidad));

        updateAlertasPulsing(bajo);
    }

    private void updateAlertasPulsing(int bajoCount) {
        if (btnAlertas != null) {
            btnAlertas.putClientProperty("pulsing", bajoCount > 0);
            btnAlertas.repaint();
        }
    }

    private void agregarProductoDash(JTable table, JPanel stats) {
        new ProductoDialog((java.awt.Frame) SwingUtilities.getWindowAncestor(this), null,
                () -> refreshInventario(table, stats)).setVisible(true);
    }

    private void editarProductoInv(JTable t, JPanel s) {
        int r = t.getSelectedRow();
        if (r == -1) {
            JOptionPane.showMessageDialog(this, "Seleccione un producto de la tabla.", "Advertencia",
                    JOptionPane.WARNING_MESSAGE);
            return;
        }
        Long id = (Long) t.getValueAt(r, 0);
        try {
            Producto p = ApiClient.get("/productos/" + id, Producto.class);
            new ProductoDialog((java.awt.Frame) SwingUtilities.getWindowAncestor(this), p,
                    () -> refreshInventario(t, s)).setVisible(true);
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, "Error al cargar producto: " + e.getMessage(), "Error",
                    JOptionPane.ERROR_MESSAGE);
        }
    }

    private void refreshUsuarios(JTable table) {
        loadTable(table, "/usuarios", row -> new Object[] { id(row), str(row, "nombre"), str(row, "usuario"),
                str(row, "numeroDocumento"), str(row, "activo").equals("1") ? "Activo" : "Inactivo", "" });
    }

    private void agregarUsuarioDash(JTable table) {
        new UsuarioDialog((java.awt.Frame) SwingUtilities.getWindowAncestor(this), null, () -> refreshUsuarios(table))
                .setVisible(true);
    }

    private void editarUsuarioDash(JTable table) {
        int row = table.getSelectedRow();
        if (row == -1) {
            JOptionPane.showMessageDialog(this, "Seleccione un usuario de la tabla.");
            return;
        }
        String iden = table.getValueAt(row, 3).toString();
        try {
            Usuario[] todos = ApiClient.get("/usuarios", Usuario[].class);
            Usuario u = java.util.Arrays.stream(todos)
                    .filter(user -> iden.equals(user.getIdentificacion()))
                    .findFirst()
                    .orElse(null);

            if (u == null) {
                JOptionPane.showMessageDialog(this, "No se encontró la información del usuario.");
                return;
            }
            new UsuarioDialog((java.awt.Frame) SwingUtilities.getWindowAncestor(this), u, () -> refreshUsuarios(table))
                    .setVisible(true);
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, "Error al cargar usuario: " + e.getMessage());
        }
    }

    private void eliminarUsuarioDash(JTable table) {
        int row = table.getSelectedRow();
        if (row == -1) {
            JOptionPane.showMessageDialog(this, "Seleccione un usuario para eliminar.");
            return;
        }
        String iden = table.getValueAt(row, 3).toString();
        if (JOptionPane.showConfirmDialog(this, "¿Está seguro de eliminar el usuario: " + iden + "?",
                "Confirmar Eliminación", JOptionPane.OK_CANCEL_OPTION,
                JOptionPane.WARNING_MESSAGE) == JOptionPane.OK_OPTION) {
            try {
                ApiClient.delete("/usuarios/" + iden);
                JOptionPane.showMessageDialog(this, "Usuario eliminado con éxito.");
                refreshUsuarios(table);
            } catch (Exception e) {
                JOptionPane.showMessageDialog(this, "Error al eliminar: " + e.getMessage());
            }
        }
    }

    private JPanel buildSectionHeader(String t, String s, JButton... b) {
        JPanel h = new JPanel(new BorderLayout());
        h.setOpaque(false);
        h.setBorder(new EmptyBorder(0, 0, 16, 0));
        JLabel lt = new JLabel(t);
        lt.setForeground(TEXT_MAIN);
        lt.setFont(new Font("Inter", Font.BOLD, 22));
        JLabel ls = new JLabel(s);
        ls.setForeground(TEXT_MUTED);
        ls.setFont(new Font("Inter", Font.PLAIN, 12));
        JPanel l = new JPanel(new GridLayout(2, 1, 0, 2));
        l.setOpaque(false);
        l.add(lt);
        l.add(ls);

        JPanel leftContainer = new JPanel(new FlowLayout(FlowLayout.LEFT, 20, 0));
        leftContainer.setOpaque(false);
        leftContainer.add(l);

        if (b != null && b.length > 0 && b[0] != null) {
            for (JButton btn : b) {
                if (btn != null) {
                    leftContainer.add(btn);
                }
            }
        }

        h.add(leftContainer, BorderLayout.WEST);
        return h;
    }

    private JButton createActionButton(String t, Color bg) {
        JButton b = new JButton(t) {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setColor(getBackground());
                g2.fillRoundRect(0, 0, getWidth(), getHeight(), 10, 10);
                g2.dispose();
                super.paintComponent(g);
            }
        };
        b.setBackground(bg);
        b.setForeground(Color.WHITE);
        b.setFont(new Font("Inter", Font.BOLD, 12));
        b.setBorder(new EmptyBorder(8, 18, 8, 18));
        b.setFocusPainted(false);
        b.setContentAreaFilled(false);
        b.setOpaque(false);
        b.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        return b;
    }

    private JPanel buildStatCard(String l, String v, Color vc) {
        JPanel c = new JPanel(new GridLayout(2, 1, 0, 4));
        c.setBackground(BG_CARD);
        c.setBorder(BorderFactory.createCompoundBorder(BorderFactory.createLineBorder(new Color(255, 255, 255, 13), 1),
                new EmptyBorder(14, 18, 14, 18)));
        JLabel ll = new JLabel(l);
        ll.setForeground(TEXT_MUTED);
        ll.setFont(new Font("Inter", Font.BOLD, 18));
        JLabel lv = new JLabel(v);
        lv.setForeground(vc);
        lv.setFont(new Font("Inter", Font.BOLD, 36));
        lv.setName("value");
        c.add(ll);
        c.add(lv);
        return c;
    }

    private void updateStatCard(JPanel c, String v) {
        for (Component cmp : c.getComponents())
            if (cmp instanceof JLabel && "value".equals(cmp.getName()))
                ((JLabel) cmp).setText(v);
    }

    private JTable buildStyledTable(String[] cols) {
        DefaultTableModel m = new DefaultTableModel(cols, 0) {
            @Override
            public boolean isCellEditable(int r, int c) {
                return false;
            }
        };
        JTable t = new JTable(m);
        t.setBackground(BG_CARD);
        t.setForeground(TEXT_MAIN);
        t.setRowHeight(36);
        t.getTableHeader().setBackground(BG_CARD);
        t.getTableHeader().setForeground(TEXT_MUTED);
        t.setSelectionBackground(new Color(99, 102, 241, 60));
        t.setSelectionForeground(TEXT_MAIN);
        return t;
    }

    private JScrollPane wrapTable(JTable t) {
        JScrollPane s = new JScrollPane(t);
        s.setBackground(BG_CARD);
        s.getViewport().setBackground(BG_CARD);
        s.setBorder(BorderFactory.createLineBorder(new Color(255, 255, 255, 13), 1));
        return s;
    }

    private JPanel buildSearchPanel(JTable table) {
        JPanel p = new JPanel(new BorderLayout());
        p.setOpaque(false);
        p.setBorder(BorderFactory.createEmptyBorder(0, 0, 4, 0));

        javax.swing.JTextField txtSearch = new javax.swing.JTextField() {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setColor(BG_CARD);
                g2.fillRoundRect(0, 0, getWidth(), getHeight(), 10, 10);
                g2.setColor(new Color(255, 255, 255, 20));
                g2.drawRoundRect(0, 0, getWidth() - 1, getHeight() - 1, 10, 10);
                g2.dispose();
                super.paintComponent(g);
            }
        };
        txtSearch.setBackground(BG_CARD);
        txtSearch.setForeground(TEXT_MAIN);
        txtSearch.setCaretColor(TEXT_MAIN);
        txtSearch.setFont(new Font("Inter", Font.PLAIN, 14));
        txtSearch.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createEmptyBorder(),
                BorderFactory.createEmptyBorder(8, 14, 8, 14)));

        txtSearch.putClientProperty("JTextField.placeholderText", "🔍 Buscar en la tabla...");

        javax.swing.table.TableRowSorter<javax.swing.table.TableModel> sorter = new javax.swing.table.TableRowSorter<>(
                table.getModel());

        // Ensure actions column (the last one) is not sorted/filtered, or just let all
        // columns be searched
        // Since we are matching regex, it's fine to search all columns.
        table.setRowSorter(sorter);

        txtSearch.getDocument().addDocumentListener(new javax.swing.event.DocumentListener() {
            private void search() {
                String text = txtSearch.getText();
                if (text.trim().length() == 0) {
                    sorter.setRowFilter(null);
                } else {
                    sorter.setRowFilter(
                            javax.swing.RowFilter.regexFilter("(?i)" + java.util.regex.Pattern.quote(text)));
                }
            }

            @Override
            public void insertUpdate(javax.swing.event.DocumentEvent e) {
                search();
            }

            @Override
            public void removeUpdate(javax.swing.event.DocumentEvent e) {
                search();
            }

            @Override
            public void changedUpdate(javax.swing.event.DocumentEvent e) {
                search();
            }
        });

        p.add(txtSearch, BorderLayout.CENTER);
        return p;
    }

    private void loadTable(JTable t, String ep,
            java.util.function.Function<java.util.Map<String, Object>, Object[]> m) {
        new SwingWorker<Void, Void>() {
            @Override
            protected Void doInBackground() {
                try {
                    Object[] data = ApiClient.get(ep, Object[].class);
                    SwingUtilities.invokeLater(() -> {
                        DefaultTableModel dtm = (DefaultTableModel) t.getModel();
                        dtm.setRowCount(0);
                        if (data != null)
                            for (Object raw : data)
                                dtm.addRow(m.apply((java.util.Map<String, Object>) raw));
                    });
                } catch (Exception e) {
                }
                return null;
            }
        }.execute();
    }

    private void refreshAlertas() {
        if (tableAlertas != null) {
            refreshAlertas(tableAlertas);
        }
    }

    private JPanel buildReportCard(String titulo, String descripcion, String tipo) {
        JPanel c = new JPanel(new BorderLayout(0, 12));
        c.setBackground(BG_CARD);
        c.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(255, 255, 255, 13), 1),
                new EmptyBorder(18, 18, 18, 18)));

        JLabel lt = new JLabel(titulo);
        lt.setForeground(TEXT_MAIN);
        lt.setFont(new Font("Inter", Font.BOLD, 22));

        JLabel ld = new JLabel("<html><body style='width: 260px;'>" + descripcion + "</body></html>");
        ld.setForeground(TEXT_MUTED);
        ld.setFont(new Font("Inter", Font.PLAIN, 16));

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
        b.setFont(new Font("Inter", Font.BOLD, 12));
        b.setBorder(new EmptyBorder(8, 16, 8, 16));
        b.setFocusPainted(false);
        b.setContentAreaFilled(false);
        b.setOpaque(false);
        b.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        b.addActionListener(e -> generarReporte(tipo));

        JPanel textPanel = new JPanel(new BorderLayout(0, 6));
        textPanel.setOpaque(false);
        textPanel.add(lt, BorderLayout.NORTH);
        textPanel.add(ld, BorderLayout.CENTER);

        c.add(textPanel, BorderLayout.CENTER);
        c.add(b, BorderLayout.SOUTH);
        return c;
    }

    private void generarReporte(String tipo) {
        setCursor(Cursor.getPredefinedCursor(Cursor.WAIT_CURSOR));
        new SwingWorker<File, Void>() {
            private String errorMsg = null;

            @Override
            protected File doInBackground() {
                try {
                    String titulo = "";
                    StringBuilder headersHtml = new StringBuilder();
                    StringBuilder rowsHtml = new StringBuilder();
                    StringBuilder resumenHtml = new StringBuilder();
                    String nowStr = java.time.LocalDateTime.now()
                            .format(java.time.format.DateTimeFormatter.ofPattern("dd/MM/yyyy hh:mm a"));
                    String userName = SessionManager.getUsuario() != null
                            ? SessionManager.getUsuario().getNombre() + " "
                                    + (SessionManager.getUsuario().getApellido() != null
                                            ? SessionManager.getUsuario().getApellido()
                                            : "")
                            : "Administrador";

                    if ("inventario".equals(tipo)) {
                        titulo = "Inventario General de Productos";
                        String[] headers = { "Código de Barras", "Nombre del Producto", "Stock", "P. Compra",
                                "P. Venta", "Estado" };
                        for (String h : headers) {
                            headersHtml.append("<th>").append(h).append("</th>");
                        }

                        Object[] data = ApiClient.get("/productos", Object[].class);
                        int totalStock = 0;
                        double totalValor = 0;

                        if (data != null) {
                            for (Object raw : data) {
                                java.util.Map<String, Object> p = (java.util.Map<String, Object>) raw;
                                int qty = num(p, "stockActual");
                                double precioCompra = dbl(p, "precioCompra");
                                double precioVenta = dbl(p, "precioVenta");
                                String estado = num(p, "estado") == 1 ? "Activo" : "Inactivo";

                                totalStock += qty;
                                totalValor += qty * precioVenta;

                                rowsHtml.append("<tr>")
                                        .append("<td>").append(str(p, "codigoBarras")).append("</td>")
                                        .append("<td>").append(str(p, "nombre")).append("</td>")
                                        .append("<td>").append(qty).append(" uds</td>")
                                        .append("<td>$").append(String.format("%,.0f", precioCompra)).append("</td>")
                                        .append("<td>$").append(String.format("%,.0f", precioVenta)).append("</td>")
                                        .append("<td>").append(estado).append("</td>")
                                        .append("</tr>");
                            }
                        }
                        resumenHtml.append("<div class='summary-box'>")
                                .append("<p><strong>Total Productos:</strong> ").append(data != null ? data.length : 0)
                                .append("</p>")
                                .append("<p><strong>Stock Total en Almacén:</strong> ").append(totalStock)
                                .append(" unidades</p>")
                                .append("<p><strong>Valoración Comercial (a P. Venta):</strong> $")
                                .append(String.format("%,.0f", totalValor)).append("</p>")
                                .append("</div>");

                    } else if ("stock-bajo".equals(tipo)) {
                        titulo = "Reporte de Productos con Stock Bajo";
                        String[] headers = { "Código de Barras", "Nombre", "Stock Actual", "Stock Mínimo", "P. Venta",
                                "Proveedor" };
                        for (String h : headers) {
                            headersHtml.append("<th>").append(h).append("</th>");
                        }

                        Object[] data = ApiClient.get("/productos", Object[].class);
                        Object[] provs = ApiClient.get("/proveedores", Object[].class);
                        int stockCriticoCount = 0;

                        if (data != null) {
                            for (Object raw : data) {
                                java.util.Map<String, Object> p = (java.util.Map<String, Object>) raw;
                                int qty = num(p, "stockActual");
                                int min = p.get("stockMinimo") != null ? num(p, "stockMinimo") : 5;
                                if (qty <= min) {
                                    stockCriticoCount++;
                                    double precioVenta = dbl(p, "precioVenta");

                                    String provName = "Sin asignar";
                                    Object idProv = p.get("idProveedor");
                                    if (idProv != null && provs != null) {
                                        for (Object prRaw : provs) {
                                            java.util.Map<String, Object> pr = (java.util.Map<String, Object>) prRaw;
                                            if (idProv.toString().equals(id(pr).toString())) {
                                                provName = str(pr, "nombre");
                                                break;
                                            }
                                        }
                                    }

                                    rowsHtml.append("<tr>")
                                            .append("<td>").append(str(p, "codigoBarras")).append("</td>")
                                            .append("<td>").append(str(p, "nombre")).append("</td>")
                                            .append("<td><span style='color:#ef4444; font-weight:bold'>").append(qty)
                                            .append(" uds</span></td>")
                                            .append("<td>").append(min).append(" uds</td>")
                                            .append("<td>$").append(String.format("%,.0f", precioVenta)).append("</td>")
                                            .append("<td>").append(provName).append("</td>")
                                            .append("</tr>");
                                }
                            }
                        }
                        resumenHtml.append("<div class='summary-box'>")
                                .append("<p><strong>Total en Stock Crítico:</strong> ").append(stockCriticoCount)
                                .append(" productos</p>")
                                .append("</div>");

                    } else if ("clientes".equals(tipo)) {
                        titulo = "Reporte General de Clientes";
                        String[] headers = { "Nombre Completo", "Identificación", "Teléfono", "Email", "Dirección",
                                "Frecuente", "Estado" };
                        for (String h : headers) {
                            headersHtml.append("<th>").append(h).append("</th>");
                        }

                        Object[] data = ApiClient.get("/clientes", Object[].class);
                        if (data != null) {
                            for (Object raw : data) {
                                java.util.Map<String, Object> c = (java.util.Map<String, Object>) raw;
                                String frecuente = "true".equals(str(c, "frecuente")) ? "Sí" : "No";
                                String estado = "1".equals(str(c, "activo")) ? "Activo" : "Inactivo";

                                rowsHtml.append("<tr>")
                                        .append("<td>").append(str(c, "nombre")).append("</td>")
                                        .append("<td>").append(str(c, "numeroDocumento")).append("</td>")
                                        .append("<td>").append(str(c, "telefono")).append("</td>")
                                        .append("<td>").append(str(c, "email")).append("</td>")
                                        .append("<td>").append(str(c, "direccion")).append("</td>")
                                        .append("<td>").append(frecuente).append("</td>")
                                        .append("<td>").append(estado).append("</td>")
                                        .append("</tr>");
                            }
                        }
                        resumenHtml.append("<div class='summary-box'>")
                                .append("<p><strong>Total Clientes Registrados:</strong> ")
                                .append(data != null ? data.length : 0).append("</p>")
                                .append("</div>");

                    } else if ("proveedores".equals(tipo)) {
                        titulo = "Directorio General de Proveedores";
                        String[] headers = { "Nombre / Empresa", "NIT / Identificación", "Teléfono", "Email",
                                "Dirección", "Cuenta Bancaria", "Estado" };
                        for (String h : headers) {
                            headersHtml.append("<th>").append(h).append("</th>");
                        }

                        Object[] data = ApiClient.get("/proveedores", Object[].class);
                        if (data != null) {
                            for (Object raw : data) {
                                java.util.Map<String, Object> p = (java.util.Map<String, Object>) raw;
                                String estado = "1".equals(str(p, "activo")) ? "Activo" : "Inactivo";

                                rowsHtml.append("<tr>")
                                        .append("<td>").append(str(p, "nombre")).append("</td>")
                                        .append("<td>").append(str(p, "numeroDocumento")).append("</td>")
                                        .append("<td>").append(str(p, "telefono")).append("</td>")
                                        .append("<td>").append(str(p, "email")).append("</td>")
                                        .append("<td>").append(str(p, "direccion")).append("</td>")
                                        .append("<td>").append(str(p, "cuentaBancaria")).append("</td>")
                                        .append("<td>").append(estado).append("</td>")
                                        .append("</tr>");
                            }
                        }
                        resumenHtml.append("<div class='summary-box'>")
                                .append("<p><strong>Total Proveedores Registrados:</strong> ")
                                .append(data != null ? data.length : 0).append("</p>")
                                .append("</div>");

                    } else if ("usuarios".equals(tipo)) {
                        titulo = "Reporte de Usuarios del Sistema";
                        String[] headers = { "Nombre Completo", "Identificación", "Usuario", "Email", "Rol", "Estado" };
                        for (String h : headers) {
                            headersHtml.append("<th>").append(h).append("</th>");
                        }

                        Object[] data = ApiClient.get("/usuarios", Object[].class);
                        if (data != null) {
                            for (Object raw : data) {
                                java.util.Map<String, Object> u = (java.util.Map<String, Object>) raw;
                                String rol = "1".equals(str(u, "idRol")) ? "Administrador" : "Auxiliar";
                                String estado = "1".equals(str(u, "activo")) ? "Activo" : "Inactivo";

                                rowsHtml.append("<tr>")
                                        .append("<td>").append(str(u, "nombre")).append(" ")
                                        .append(str(u, "apellido").equals("—") ? "" : str(u, "apellido"))
                                        .append("</td>")
                                        .append("<td>").append(str(u, "numeroDocumento")).append("</td>")
                                        .append("<td>").append(str(u, "usuario")).append("</td>")
                                        .append("<td>").append(str(u, "email")).append("</td>")
                                        .append("<td>").append(rol).append("</td>")
                                        .append("<td>").append(estado).append("</td>")
                                        .append("</tr>");
                            }
                        }
                        resumenHtml.append("<div class='summary-box'>")
                                .append("<p><strong>Total Usuarios Registrados:</strong> ")
                                .append(data != null ? data.length : 0).append("</p>")
                                .append("</div>");

                    } else if ("resumen".equals(tipo)) {
                        titulo = "Resumen Ejecutivo de la Empresa";
                        String[] headers = { "Indicador", "Valor / Métrica", "Estado / Detalle" };
                        for (String h : headers) {
                            headersHtml.append("<th>").append(h).append("</th>");
                        }

                        Object[] productos = ApiClient.get("/productos", Object[].class);
                        Object[] clientes = ApiClient.get("/clientes", Object[].class);
                        Object[] proveedores = ApiClient.get("/proveedores", Object[].class);
                        Object[] usuarios = ApiClient.get("/usuarios", Object[].class);

                        int totalProd = productos != null ? productos.length : 0;
                        int totalStock = 0;
                        int stockBajo = 0;
                        double valorInventario = 0;
                        if (productos != null) {
                            for (Object raw : productos) {
                                java.util.Map<String, Object> p = (java.util.Map<String, Object>) raw;
                                int qty = num(p, "stockActual");
                                int min = p.get("stockMinimo") != null ? num(p, "stockMinimo") : 5;
                                valorInventario += qty * dbl(p, "precioVenta");
                                totalStock += qty;
                                if (qty <= min)
                                    stockBajo++;
                            }
                        }

                        rowsHtml.append("<tr><td>Total de Productos en Catálogo</td><td>").append(totalProd)
                                .append("</td><td>Productos registrados</td></tr>")
                                .append("<tr><td>Unidades de Stock Físico</td><td>").append(totalStock)
                                .append(" uds</td><td>Total unidades en inventario</td></tr>")
                                .append("<tr><td>Productos con Stock Bajo</td><td><span style='color:#ef4444; font-weight:bold'>")
                                .append(stockBajo)
                                .append("</span></td><td>Requieren reabastecimiento urgente</td></tr>")
                                .append("<tr><td>Valoración de Inventario</td><td>$")
                                .append(String.format("%,.2f", valorInventario))
                                .append("</td><td>En base a precios de venta comerciales</td></tr>")
                                .append("<tr><td>Clientes Registrados</td><td>")
                                .append(clientes != null ? clientes.length : 0)
                                .append("</td><td>Base de datos de clientes</td></tr>")
                                .append("<tr><td>Proveedores Registrados</td><td>")
                                .append(proveedores != null ? proveedores.length : 0)
                                .append("</td><td>Suministradores comerciales</td></tr>")
                                .append("<tr><td>Usuarios en el Sistema</td><td>")
                                .append(usuarios != null ? usuarios.length : 0)
                                .append("</td><td>Cuentas con acceso administrativo</td></tr>");

                        resumenHtml.append("<div class='summary-box'>")
                                .append("<p><strong>Fecha del Resumen:</strong> ").append(nowStr).append("</p>")
                                .append("<p><strong>Estado de Operación:</strong> Operando normalmente</p>")
                                .append("</div>");
                    } else if ("ventas".equals(tipo)) {
                        titulo = "Reporte Histórico de Ventas";
                        String[] headers = { "ID Venta", "Fecha / Hora", "Cliente", "Procesado por", "Productos", "Total" };
                        for (String h : headers) {
                            headersHtml.append("<th>").append(h).append("</th>");
                        }

                        Object[] dataVentas = ApiClient.get("/ventas", Object[].class);
                        Object[] dataClientes = ApiClient.get("/clientes", Object[].class);
                        Object[] dataUsuarios = ApiClient.get("/usuarios", Object[].class);

                        java.util.Map<String, String> clientesMap = new java.util.HashMap<>();
                        if (dataClientes != null) {
                            for (Object raw : dataClientes) {
                                java.util.Map<String, Object> c = (java.util.Map<String, Object>) raw;
                                clientesMap.put(id(c).toString(), str(c, "nombre"));
                            }
                        }

                        java.util.Map<String, String> usuariosMap = new java.util.HashMap<>();
                        if (dataUsuarios != null) {
                            for (Object raw : dataUsuarios) {
                                java.util.Map<String, Object> u = (java.util.Map<String, Object>) raw;
                                usuariosMap.put(id(u).toString(), str(u, "nombre") + " " + (str(u, "apellido").equals("—") ? "" : str(u, "apellido")));
                            }
                        }

                        double totalVentasMonto = 0;

                        if (dataVentas != null) {
                            java.util.List<java.util.Map<String, Object>> ventasList = new java.util.ArrayList<>();
                            for (Object raw : dataVentas) {
                                ventasList.add((java.util.Map<String, Object>) raw);
                            }
                            ventasList.sort((a, b) -> {
                                String fA = str(a, "fechaHora");
                                String fB = str(b, "fechaHora");
                                return fB.compareTo(fA); // Descending order
                            });

                            for (java.util.Map<String, Object> v : ventasList) {
                                double total = dbl(v, "valorTotal");
                                totalVentasMonto += total;
                                String fechaRaw = str(v, "fechaHora");
                                String fecha = "—";
                                try {
                                    java.time.LocalDateTime ldt = java.time.LocalDateTime.parse(fechaRaw);
                                    fecha = ldt.format(java.time.format.DateTimeFormatter.ofPattern("dd/MM/yyyy hh:mm a"));
                                } catch (Exception e) {
                                    fecha = fechaRaw;
                                }

                                String cId = v.get("idCliente") != null ? v.get("idCliente").toString() : "";
                                String cliente = cId.isEmpty() ? "Sin cliente" : clientesMap.getOrDefault(cId, "Cliente #" + cId);

                                String uId = v.get("idUsuario") != null ? v.get("idUsuario").toString() : "";
                                String usuario = uId.isEmpty() ? "Sistema" : usuariosMap.getOrDefault(uId, "Usuario #" + uId);

                                java.util.List<?> detalles = (java.util.List<?>) v.get("detalles");
                                int nProductos = detalles != null ? detalles.size() : 0;

                                rowsHtml.append("<tr>")
                                        .append("<td>#").append(id(v)).append("</td>")
                                        .append("<td>").append(fecha).append("</td>")
                                        .append("<td>").append(cliente).append("</td>")
                                        .append("<td>").append(usuario).append("</td>")
                                        .append("<td>").append(nProductos).append(" producto(s)</td>")
                                        .append("<td>$").append(String.format("%,.0f", total)).append("</td>")
                                        .append("</tr>");
                            }
                        }

                        resumenHtml.append("<div class='summary-box'>")
                                .append("<p><strong>Total de Ventas Realizadas:</strong> ")
                                .append(dataVentas != null ? dataVentas.length : 0).append("</p>")
                                .append("<p><strong>Monto Total Recaudado:</strong> $")
                                .append(String.format("%,.0f", totalVentasMonto)).append("</p>")
                                .append("</div>");

                    } else if ("ganancias".equals(tipo)) {
                        titulo = "Reporte de Ganancias y Rentabilidad";
                        String[] headers = { "ID Venta", "Fecha / Hora", "Ingreso (Venta)", "Costo total", "Ganancia Neta", "Margen %" };
                        for (String h : headers) {
                            headersHtml.append("<th>").append(h).append("</th>");
                        }

                        Object[] dataVentas = ApiClient.get("/ventas", Object[].class);
                        Object[] dataProductos = ApiClient.get("/productos", Object[].class);

                        java.util.Map<String, java.util.Map<String, Object>> prodMap = new java.util.HashMap<>();
                        if (dataProductos != null) {
                            for (Object raw : dataProductos) {
                                java.util.Map<String, Object> p = (java.util.Map<String, Object>) raw;
                                prodMap.put(id(p).toString(), p);
                            }
                        }

                        double globalIngresos = 0;
                        double globalCostos = 0;

                        if (dataVentas != null) {
                            java.util.List<java.util.Map<String, Object>> ventasList = new java.util.ArrayList<>();
                            for (Object raw : dataVentas) {
                                ventasList.add((java.util.Map<String, Object>) raw);
                            }
                            ventasList.sort((a, b) -> {
                                String fA = str(a, "fechaHora");
                                String fB = str(b, "fechaHora");
                                return fB.compareTo(fA);
                            });

                            for (java.util.Map<String, Object> v : ventasList) {
                                double ingreso = dbl(v, "valorTotal");
                                globalIngresos += ingreso;

                                String fechaRaw = str(v, "fechaHora");
                                String fecha = "—";
                                try {
                                    java.time.LocalDateTime ldt = java.time.LocalDateTime.parse(fechaRaw);
                                    fecha = ldt.format(java.time.format.DateTimeFormatter.ofPattern("dd/MM/yyyy hh:mm a"));
                                } catch (Exception e) {
                                    fecha = fechaRaw;
                                }

                                double costoVenta = 0;
                                java.util.List<?> detalles = (java.util.List<?>) v.get("detalles");
                                if (detalles != null) {
                                    for (Object detRaw : detalles) {
                                        java.util.Map<String, Object> d = (java.util.Map<String, Object>) detRaw;
                                        String pId = d.get("idProducto") != null ? d.get("idProducto").toString() : "";
                                        java.util.Map<String, Object> prod = prodMap.get(pId);
                                        double precioCompra = prod != null ? dbl(prod, "precioCompra") : 0;
                                        int qty = d.get("cantidad") != null ? ((Number) d.get("cantidad")).intValue() : 0;
                                        costoVenta += qty * precioCompra;
                                    }
                                }
                                globalCostos += costoVenta;

                                double ganancia = ingreso - costoVenta;
                                double margenVal = ingreso > 0 ? (ganancia / ingreso) * 100 : 0;
                                String margen = String.format("%.1f%%", margenVal);

                                String color = ganancia >= 0 ? "#10b981" : "#ef4444";

                                rowsHtml.append("<tr>")
                                        .append("<td>#").append(id(v)).append("</td>")
                                        .append("<td>").append(fecha).append("</td>")
                                        .append("<td>$").append(String.format("%,.0f", ingreso)).append("</td>")
                                        .append("<td>$").append(String.format("%,.0f", costoVenta)).append("</td>")
                                        .append("<td><span style='color:").append(color).append("; font-weight:bold'>$")
                                        .append(String.format("%,.0f", ganancia)).append("</span></td>")
                                        .append("<td>").append(margen).append("</td>")
                                        .append("</tr>");
                            }
                        }

                        double globalGanancia = globalIngresos - globalCostos;
                        double globalMargenVal = globalIngresos > 0 ? (globalGanancia / globalIngresos) * 100 : 0;
                        String globalMargen = String.format("%.1f%%", globalMargenVal);
                        String globalColor = globalGanancia >= 0 ? "#10b981" : "#ef4444";

                        resumenHtml.append("<div class='summary-box'>")
                                .append("<p><strong>Total Ingresos (Ventas):</strong> $").append(String.format("%,.0f", globalIngresos)).append("</p>")
                                .append("<p><strong>Total Costo de Ventas:</strong> $").append(String.format("%,.0f", globalCostos)).append("</p>")
                                .append("<p><strong>Ganancia Neta Total:</strong> <span style='color:").append(globalColor).append("; font-weight:bold'>$")
                                .append(String.format("%,.0f", globalGanancia)).append("</span></p>")
                                .append("<p><strong>Margen de Ganancia Promedio:</strong> ").append(globalMargen).append("</p>")
                                .append("</div>");
                    }

                    String html = "<!DOCTYPE html>\n" +
                            "<html lang=\"es\">\n" +
                            "<head>\n" +
                            "    <meta charset=\"UTF-8\">\n" +
                            "    <title>Reporte - " + titulo + "</title>\n" +
                            "    <style>\n" +
                            "        body {\n" +
                            "            font-family: 'Segoe UI', system-ui, sans-serif;\n" +
                            "            color: #1e293b;\n" +
                            "            background: #ffffff;\n" +
                            "            margin: 0;\n" +
                            "            padding: 2rem;\n" +
                            "        }\n" +
                            "        .header {\n" +
                            "            border-bottom: 2px solid #0f172a;\n" +
                            "            padding-bottom: 1rem;\n" +
                            "            margin-bottom: 2rem;\n" +
                            "            display: flex;\n" +
                            "            justify-content: space-between;\n" +
                            "            align-items: flex-end;\n" +
                            "        }\n" +
                            "        .header h1 {\n" +
                            "            margin: 0 0 0.5rem 0;\n" +
                            "            font-size: 1.8rem;\n" +
                            "            color: #0f172a;\n" +
                            "        }\n" +
                            "        .header p {\n" +
                            "            margin: 0;\n" +
                            "            color: #64748b;\n" +
                            "            font-size: 0.9rem;\n" +
                            "        }\n" +
                            "        .meta-info {\n" +
                            "            text-align: right;\n" +
                            "            font-size: 0.85rem;\n" +
                            "            color: #64748b;\n" +
                            "        }\n" +
                            "        table {\n" +
                            "            width: 100%;\n" +
                            "            border-collapse: collapse;\n" +
                            "            margin-bottom: 2rem;\n" +
                            "        }\n" +
                            "        th {\n" +
                            "            background: #0f172a;\n" +
                            "            color: #ffffff;\n" +
                            "            text-align: left;\n" +
                            "            padding: 0.75rem 1rem;\n" +
                            "            font-size: 0.85rem;\n" +
                            "            text-transform: uppercase;\n" +
                            "            letter-spacing: 0.05em;\n" +
                            "        }\n" +
                            "        td {\n" +
                            "            padding: 0.75rem 1rem;\n" +
                            "            border-bottom: 1px solid #e2e8f0;\n" +
                            "            font-size: 0.9rem;\n" +
                            "        }\n" +
                            "        tr:nth-child(even) td {\n" +
                            "            background: #f8fafc;\n" +
                            "        }\n" +
                            "        .summary-box {\n" +
                            "            background: #f1f5f9;\n" +
                            "            border: 1px solid #e2e8f0;\n" +
                            "            border-radius: 0.5rem;\n" +
                            "            padding: 1.25rem;\n" +
                            "            margin-top: 2rem;\n" +
                            "            display: inline-block;\n" +
                            "            min-width: 320px;\n" +
                            "        }\n" +
                            "        .summary-box p {\n" +
                            "            margin: 0.35rem 0;\n" +
                            "            font-size: 0.95rem;\n" +
                            "        }\n" +
                            "        .no-print {\n" +
                            "            margin-bottom: 1.5rem;\n" +
                            "            display: flex;\n" +
                            "            gap: 0.75rem;\n" +
                            "        }\n" +
                            "        .btn-print {\n" +
                            "            padding: 0.6rem 1.2rem;\n" +
                            "            background: #0f172a;\n" +
                            "            color: white;\n" +
                            "            border: none;\n" +
                            "            border-radius: 0.375rem;\n" +
                            "            cursor: pointer;\n" +
                            "            font-weight: 600;\n" +
                            "            font-size: 0.9rem;\n" +
                            "        }\n" +
                            "        .btn-close {\n" +
                            "            padding: 0.6rem 1.2rem;\n" +
                            "            background: #e2e8f0;\n" +
                            "            color: #1e293b;\n" +
                            "            border: none;\n" +
                            "            border-radius: 0.375rem;\n" +
                            "            cursor: pointer;\n" +
                            "            font-weight: 600;\n" +
                            "            font-size: 0.9rem;\n" +
                            "        }\n" +
                            "        @media print {\n" +
                            "            body { padding: 0; }\n" +
                            "            .no-print { display: none; }\n" +
                            "        }\n" +
                            "    </style>\n" +
                            "</head>\n" +
                            "<body>\n" +
                            "    <div class=\"no-print\">\n" +
                            "        <button class=\"btn-print\" onclick=\"window.print()\">Imprimir / Guardar PDF</button>\n"
                            +
                            "        <button class=\"btn-close\" onclick=\"window.close()\">Cerrar</button>\n" +
                            "    </div>\n" +
                            "    <div class=\"header\">\n" +
                            "        <div>\n" +
                            "            <h1>" + titulo + "</h1>\n" +
                            "            <p>AcaciosWork — Sistema de Control Administrativo</p>\n" +
                            "        </div>\n" +
                            "        <div class=\"meta-info\">\n" +
                            "            <p><strong>Fecha de Generación:</strong> " + nowStr + "</p>\n" +
                            "            <p><strong>Generado por:</strong> " + userName + "</p>\n" +
                            "        </div>\n" +
                            "    </div>\n" +
                            "    \n" +
                            "    <table>\n" +
                            "        <thead>\n" +
                            "            <tr>\n" +
                            "                " + headersHtml.toString() + "\n" +
                            "            </tr>\n" +
                            "        </thead>\n" +
                            "        <tbody>\n" +
                            "            " + rowsHtml.toString() + "\n" +
                            "        </tbody>\n" +
                            "    </table>\n" +
                            "    \n" +
                            "    " + resumenHtml.toString() + "\n" +
                            "    \n" +
                            "    <script>\n" +
                            "        window.onload = function() {\n" +
                            "            setTimeout(function() {\n" +
                            "                window.print();\n" +
                            "            }, 500);\n" +
                            "        };\n" +
                            "    </script>\n" +
                            "</body>\n" +
                            "</html>";

                    File tempFile = File.createTempFile("reporte-" + tipo + "-", ".html");
                    tempFile.deleteOnExit();
                    try (java.io.BufferedWriter writer = new java.io.BufferedWriter(
                            new java.io.FileWriter(tempFile, java.nio.charset.StandardCharsets.UTF_8))) {
                        writer.write(html);
                    }
                    return tempFile;
                } catch (Exception e) {
                    e.printStackTrace();
                    errorMsg = e.getMessage();
                    return null;
                }
            }

            @Override
            protected void done() {
                setCursor(Cursor.getDefaultCursor());
                try {
                    File file = get();
                    if (file != null) {
                        java.awt.Desktop.getDesktop().browse(file.toURI());
                    } else {
                        JOptionPane.showMessageDialog(Administrador.this,
                                "Error al generar reporte: " + (errorMsg != null ? errorMsg : "Error desconocido"),
                                "Error", JOptionPane.ERROR_MESSAGE);
                    }
                } catch (Exception e) {
                    JOptionPane.showMessageDialog(Administrador.this,
                            "Error al abrir reporte: " + e.getMessage(),
                            "Error", JOptionPane.ERROR_MESSAGE);
                }
            }
        }.execute();
    }

    private JPanel createContentPanel() {
        JPanel p = new JPanel(new BorderLayout(0, 16));
        p.setBackground(BG_DARK);
        p.setBorder(new EmptyBorder(24, 28, 24, 28));
        return p;
    }

    private String str(java.util.Map<String, Object> m, String k) {
        Object v = m.get(k);
        return v != null ? v.toString() : "—";
    }

    private int num(java.util.Map<String, Object> m, String k) {
        Object v = m.get(k);
        return (v instanceof Number) ? ((Number) v).intValue() : 0;
    }

    private double dbl(java.util.Map<String, Object> m, String k) {
        Object v = m.get(k);
        return (v instanceof Number) ? ((Number) v).doubleValue() : 0.0;
    }

    private Long id(java.util.Map<String, Object> m) {
        Object v = m.get("id");
        return v != null ? Long.valueOf(v.toString()) : 0L;
    }

    private void agregarProveedorDash(JTable table) {
        new ProveedorDialog((java.awt.Frame) SwingUtilities.getWindowAncestor(this), null,
                () -> refreshProveedores(table)).setVisible(true);
    }

    private void editarProveedorDash(JTable table) {
        int row = table.getSelectedRow();
        if (row == -1) {
            JOptionPane.showMessageDialog(this, "Seleccione un proveedor.");
            return;
        }
        Long id = (Long) table.getValueAt(row, 0);
        try {
            com.acacioswork.model.Proveedor p = ApiClient.get("/proveedores/" + id,
                    com.acacioswork.model.Proveedor.class);
            new ProveedorDialog((java.awt.Frame) SwingUtilities.getWindowAncestor(this), p,
                    () -> refreshProveedores(table)).setVisible(true);
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, "Error al cargar proveedor: " + e.getMessage());
        }
    }

    private void agregarClienteDash(JTable table) {
        new ClienteDialog((java.awt.Frame) SwingUtilities.getWindowAncestor(this), null, () -> refreshClientes(table))
                .setVisible(true);
    }

    private void editarClienteDash(JTable table) {
        int row = table.getSelectedRow();
        if (row == -1) {
            JOptionPane.showMessageDialog(this, "Seleccione un cliente.");
            return;
        }
        Long id = (Long) table.getValueAt(row, 0);
        try {
            com.acacioswork.model.Cliente c = ApiClient.get("/clientes/" + id, com.acacioswork.model.Cliente.class);
            new ClienteDialog((java.awt.Frame) SwingUtilities.getWindowAncestor(this), c, () -> refreshClientes(table))
                    .setVisible(true);
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, "Error al cargar cliente: " + e.getMessage());
        }
    }

    private void refreshAlertas(JTable table) {
        new SwingWorker<Object[][], Void>() {
            @Override
            protected Object[][] doInBackground() throws Exception {
                try {
                    Object[] products = ApiClient.get("/productos", Object[].class);
                    Object[] providers = ApiClient.get("/proveedores", Object[].class);

                    java.util.Map<String, String> provMap = new java.util.HashMap<>();
                    if (providers != null) {
                        for (Object pr : providers) {
                            java.util.Map<String, Object> pm = (java.util.Map<String, Object>) pr;
                            provMap.put(id(pm).toString(), str(pm, "nombre"));
                        }
                    }

                    java.util.List<Object[]> rows = new java.util.ArrayList<>();
                    if (products != null) {
                        for (Object raw : products) {
                            java.util.Map<String, Object> p = (java.util.Map<String, Object>) raw;
                            int stock = num(p, "stockActual");
                            int min = p.get("stockMinimo") != null ? num(p, "stockMinimo") : 5;
                            if (stock <= min) {
                                Object idProv = p.get("idProveedor");
                                String provName = (idProv != null)
                                        ? provMap.getOrDefault(idProv.toString(), "Sin asignar")
                                        : "Sin asignar";
                                rows.add(new Object[] {
                                        id(p),
                                        str(p, "nombre"),
                                        stock + " uds",
                                        min + " uds",
                                        provName,
                                        "🔍 Ver Proveedor"
                                });
                            }
                        }
                    }
                    return rows.toArray(new Object[0][]);
                } catch (Exception e) {
                    return null;
                }
            }

            @Override
            protected void done() {
                try {
                    Object[][] rows = get();
                    DefaultTableModel dtm = (DefaultTableModel) table.getModel();
                    dtm.setRowCount(0);
                    if (rows != null) {
                        for (Object[] r : rows) {
                            dtm.addRow(r);
                        }
                    }
                } catch (Exception e) {
                }
            }
        }.execute();
    }

    private void mostrarInfoProveedor(Object idProd) {
        try {
            com.acacioswork.model.Producto p = ApiClient.get("/productos/" + idProd,
                    com.acacioswork.model.Producto.class);
            if (p.getIdProveedor() == null) {
                JOptionPane.showMessageDialog(this, "Este producto no tiene un proveedor asignado.");
                return;
            }
            com.acacioswork.model.Proveedor prov = ApiClient.get("/proveedores/" + p.getIdProveedor(),
                    com.acacioswork.model.Proveedor.class);
            Object[] msg = {
                    "<html><b>Proveedor:</b> " + prov.getNombre() + "</html>",
                    "<html><b>Teléfono:</b> " + prov.getTelefono() + "</html>",
                    "<html><b>Email:</b> " + prov.getEmail() + "</html>",
                    "<html><b>Cuenta:</b> " + prov.getCuentaBancaria() + "</html>"
            };
            JOptionPane.showMessageDialog(this, msg, "Información de Contacto", JOptionPane.INFORMATION_MESSAGE);
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, "No se pudo obtener la información del proveedor.");
        }
    }

    public static class StockData {
        public final int actual;
        public final int minimo;
        public final int optimo;

        public StockData(int actual, int minimo, int optimo) {
            this.actual = actual;
            this.minimo = minimo;
            this.optimo = optimo;
        }
    }

    public static class StockBarPanel extends JPanel {
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

    public static class StockBarCellRenderer implements javax.swing.table.TableCellRenderer {
        private final StockBarPanel rendererPanel = new StockBarPanel();

        @Override
        public Component getTableCellRendererComponent(JTable table, Object value,
                boolean isSelected, boolean hasFocus, int row, int column) {
            if (value instanceof StockData) {
                rendererPanel.setStockData((StockData) value, isSelected);
            }
            if (isSelected) {
                rendererPanel.setBackground(table.getSelectionBackground());
                rendererPanel.setOpaque(true);
            } else {
                rendererPanel.setOpaque(false);
            }
            return rendererPanel;
        }
    }

    public static class StockNumberCellRenderer extends javax.swing.table.DefaultTableCellRenderer {
        public StockNumberCellRenderer() {
            setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        }

        @Override
        public Component getTableCellRendererComponent(JTable table, Object value,
                boolean isSelected, boolean hasFocus, int row, int column) {
            Component c = super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);
            c.setFont(new Font("Inter", Font.BOLD, 12));
            if (value instanceof StockData) {
                StockData data = (StockData) value;
                int qty = data.actual;
                int optimo = data.optimo > 0 ? data.optimo : 200;
                int pct = (int) Math.round(((double) qty / optimo) * 100);

                setText(String.valueOf(qty));
                if (isSelected) {
                    // Mantiene colores por defecto de la selección de Swing
                } else {
                    if (pct <= 30) {
                        setForeground(new Color(248, 113, 113));
                    } else if (pct <= 69) {
                        setForeground(new Color(251, 146, 60));
                    } else {
                        setForeground(new Color(52, 211, 153));
                    }
                }
            }
            return c;
        }
    }

    public static class DotIcon implements javax.swing.Icon {
        private final Color color;
        private final int size;

        public DotIcon(Color color, int size) {
            this.color = color;
            this.size = size;
        }

        @Override
        public void paintIcon(Component c, Graphics g, int x, int y) {
            Graphics2D g2 = (Graphics2D) g.create();
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            g2.setColor(color);
            g2.fillOval(x, y, size, size);
            g2.dispose();
        }

        @Override
        public int getIconWidth() {
            return size;
        }

        @Override
        public int getIconHeight() {
            return size;
        }
    }

    public static class EstadoCellRenderer extends javax.swing.table.DefaultTableCellRenderer {
        private final DotIcon iconActivo = new DotIcon(new Color(16, 185, 129), 8);
        private final DotIcon iconInactivo = new DotIcon(new Color(239, 68, 68), 8);

        @Override
        public Component getTableCellRendererComponent(JTable table, Object value,
                boolean isSelected, boolean hasFocus, int row, int column) {
            super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);
            setFont(new Font("Inter", Font.BOLD, 12));
            setIconTextGap(8);
            setBorder(BorderFactory.createEmptyBorder(0, 12, 0, 12));

            String valStr = value != null ? value.toString() : "";
            if ("Activo".equalsIgnoreCase(valStr)) {
                setIcon(iconActivo);
                setForeground(new Color(16, 185, 129));
            } else if ("Inactivo".equalsIgnoreCase(valStr)) {
                setIcon(iconInactivo);
                setForeground(new Color(239, 68, 68));
            } else {
                setIcon(null);
            }
            if (isSelected) {
                setBackground(table.getSelectionBackground());
            } else {
                setBackground(table.getBackground());
            }
            return this;
        }
    }

    public static class AccionesPanel extends JPanel {
        public final JButton btnEditar;
        public final JButton btnBorrar;

        public AccionesPanel() {
            setLayout(new FlowLayout(FlowLayout.CENTER, 6, 2));
            setOpaque(false);

            btnEditar = new JButton("Editar");
            btnEditar.setFont(new Font("Inter", Font.BOLD, 11));
            btnEditar.setForeground(Color.WHITE);
            btnEditar.setBackground(new Color(51, 65, 85));
            btnEditar.setBorder(BorderFactory.createEmptyBorder(4, 10, 4, 10));
            btnEditar.setFocusPainted(false);
            btnEditar.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));

            btnBorrar = new JButton("Borrar");
            btnBorrar.setFont(new Font("Inter", Font.BOLD, 11));
            btnBorrar.setForeground(new Color(239, 68, 68));
            btnBorrar.setBackground(new Color(239, 68, 68, 38));
            btnBorrar.setBorder(BorderFactory.createEmptyBorder(4, 10, 4, 10));
            btnBorrar.setFocusPainted(false);
            btnBorrar.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));

            add(btnEditar);
            add(btnBorrar);
        }
    }

    public static class AccionesCellRenderer implements javax.swing.table.TableCellRenderer {
        private final AccionesPanel panel = new AccionesPanel();

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

    private void setupAccionesColumn(JTable table, Runnable onEditar, Runnable onBorrar) {
        int colIndex = table.getColumnCount() - 1;
        if (table.getColumnName(colIndex).equals("Acciones")) {
            table.getColumnModel().getColumn(colIndex).setCellRenderer(new AccionesCellRenderer());
            table.getColumnModel().getColumn(colIndex).setPreferredWidth(140);

            table.addMouseListener(new java.awt.event.MouseAdapter() {
                @Override
                public void mousePressed(java.awt.event.MouseEvent e) {
                    int row = table.rowAtPoint(e.getPoint());
                    int col = table.columnAtPoint(e.getPoint());
                    if (col == colIndex && row != -1) {
                        java.awt.Rectangle rect = table.getCellRect(row, col, true);
                        int cellX = e.getX() - rect.x;
                        int width = rect.width;
                        table.setRowSelectionInterval(row, row);
                        if (cellX < width / 2) {
                            onEditar.run();
                        } else {
                            onBorrar.run();
                        }
                    }
                }
            });
        }
    }

    /** Construye el panel de inicio (welcome panel) con estadísticas y tabla. @author RADJ */
    private JPanel buildWelcomePanel() {
        JPanel panel = createContentPanel();

        /** Añadir encabezado de sección alineado a la izquierda. @author RADJ */
        panel.add(buildSectionHeader("Resumen de Inventario", "Vista rápida del estado de existencias", (JButton) null), BorderLayout.NORTH);
        
        statsInventario = new JPanel(new GridLayout(1, 5, 12, 0));
        statsInventario.setOpaque(false);
        statsInventario.setBorder(new EmptyBorder(0, 0, 16, 0));
        statsInventario.add(buildStatCard("Total Productos", "0", TEXT_MAIN));
        statsInventario.add(buildStatCard("Stock Bajo", "0", DANGER));
        statsInventario.add(buildStatCard("Valor Inventario", "$0", ACCENT));
        statsInventario.add(buildStatCard("Valor Costo", "$0", new Color(245, 158, 11)));
        statsInventario.add(buildStatCard("Utilidad Neta", "$0", ACCENT));
        
        /** Inicializar tabla simplificada de productos para inicio. @author RADJ */
        tableHome = buildStyledTable(new String[] { "ID", "Código", "Nombre", "Unidad", "Stock", "Estado" });
        hideColumn(tableHome, 0);
        tableHome.getColumnModel().getColumn(1).setPreferredWidth(100); // Código
        tableHome.getColumnModel().getColumn(2).setPreferredWidth(160); // Nombre
        tableHome.getColumnModel().getColumn(3).setPreferredWidth(100); // Unidad
        tableHome.getColumnModel().getColumn(4).setPreferredWidth(300); // Stock
        tableHome.getColumnModel().getColumn(5).setPreferredWidth(100); // Estado
        tableHome.getColumnModel().getColumn(4).setCellRenderer(new StockBarCellRenderer());
        tableHome.getColumnModel().getColumn(5).setCellRenderer(new EstadoCellRenderer());

        /** Construir el contenedor para la tabla y su buscador. @author RADJ */
        JPanel tableContainer = new JPanel(new BorderLayout(0, 8));
        tableContainer.setOpaque(false);
        tableContainer.add(buildSearchPanel(tableHome), BorderLayout.NORTH);
        tableContainer.add(wrapTable(tableHome), BorderLayout.CENTER);

        /** Organizar estadísticas y tabla en el contenedor central. @author RADJ */
        JPanel centerContainer = new JPanel(new BorderLayout(0, 16));
        centerContainer.setOpaque(false);
        centerContainer.add(statsInventario, BorderLayout.NORTH);
        centerContainer.add(tableContainer, BorderLayout.CENTER);
        
        panel.add(centerContainer, BorderLayout.CENTER);
        return panel;
    }

    private void refreshWelcomeStats() {
        new SwingWorker<Void, Void>() {
            @Override
            protected Void doInBackground() throws Exception {
                try {
                    Object[] data = ApiClient.get("/productos", Object[].class);
                    SwingUtilities.invokeLater(() -> {
                        if (data == null || statsInventario == null) return;
                        int bajo = 0;
                        double valor = 0;
                        double valorCosto = 0;

                        /** Limpiar y preparar la tabla de productos de inicio. @author RADJ */
                        DefaultTableModel model = (DefaultTableModel) tableHome.getModel();
                        model.setRowCount(0);

                        for (Object raw : data) {
                            java.util.Map<String, Object> p = (java.util.Map<String, Object>) raw;
                            Long id = id(p);
                            int qty = num(p, "stockActual");
                            int min = p.get("stockMinimo") != null ? num(p, "stockMinimo") : 5;
                            int opt = p.get("stockOptimo") != null ? num(p, "stockOptimo") : 200;
                            double precioCompra = dbl(p, "precioCompra");
                            double precioVenta = dbl(p, "precioVenta");
                            valor += qty * precioVenta;
                            valorCosto += qty * precioCompra;
                            if (qty <= min)
                                bajo++;

                            String estadoLabel = "1".equals(str(p, "estado")) ? "Activo" : "Inactivo";
                            String unidadMedida = str(p, "unidadMedida") != null && !str(p, "unidadMedida").equals("—")
                                    ? str(p, "unidadMedida")
                                    : "Unidad";

                            /** Agregar fila a la tabla simplificada de inicio. @author RADJ */
                            model.addRow(new Object[] {
                                    id,
                                    str(p, "codigoBarras"),
                                    str(p, "nombre"),
                                    unidadMedida,
                                    new StockData(qty, min, opt),
                                    estadoLabel
                            });
                        }
                        double finalValor = valor;
                        double finalCosto = valorCosto;
                        double finalUtilidad = valor - valorCosto;
                        int finalBajo = bajo;

                        java.text.NumberFormat nf = java.text.NumberFormat.getNumberInstance(java.util.Locale.GERMANY);
                        nf.setMaximumFractionDigits(0);

                        updateStatCard((JPanel) statsInventario.getComponents()[0], String.valueOf(data.length));
                        updateStatCard((JPanel) statsInventario.getComponents()[1], String.valueOf(finalBajo));
                        updateStatCard((JPanel) statsInventario.getComponents()[2], "$" + nf.format(finalValor));
                        updateStatCard((JPanel) statsInventario.getComponents()[3], "$" + nf.format(finalCosto));
                        updateStatCard((JPanel) statsInventario.getComponents()[4], "$" + nf.format(finalUtilidad));

                        updateAlertasPulsing(bajo);
                    });
                } catch (Exception e) {
                }
                return null;
            }
        }.execute();
    }

    /**
     * Consulta las ventas de la API en segundo plano, calcula la tendencia mensual del año actual,
     * y actualiza el panel del gráfico. @author RADJ
     */
    private void refreshReportesChart() {
        if (chartPanel == null) return;
        new SwingWorker<double[], Void>() {
            @Override
            protected double[] doInBackground() throws Exception {
                try {
                    Object[] ventasRaw = ApiClient.get("/ventas", Object[].class);
                    double[] monthlyData = new double[12];
                    int currentYear = java.time.LocalDate.now().getYear();

                    if (ventasRaw != null) {
                        for (Object raw : ventasRaw) {
                            java.util.Map<String, Object> v = (java.util.Map<String, Object>) raw;
                            String fechaRaw = (String) v.get("fechaHora");
                            if (fechaRaw != null) {
                                java.time.LocalDateTime ldt = java.time.LocalDateTime.parse(fechaRaw);
                                if (ldt.getYear() == currentYear) {
                                    int mes = ldt.getMonthValue() - 1; // 1-12 -> 0-11
                                    double total = dbl(v, "valorTotal");
                                    if (total == 0.0 && v.get("detalles") != null) {
                                        // Fallback sum of details
                                        java.util.List<?> detalles = (java.util.List<?>) v.get("detalles");
                                        for (Object detRaw : detalles) {
                                            java.util.Map<String, Object> d = (java.util.Map<String, Object>) detRaw;
                                            total += dbl(d, "subtotal");
                                        }
                                    }
                                    monthlyData[mes] += total;
                                }
                            }
                        }
                    }
                    return monthlyData;
                } catch (Exception e) {
                    e.printStackTrace();
                    return new double[12];
                }
            }

            @Override
            protected void done() {
                try {
                    double[] res = get();
                    if (chartPanel != null) {
                        chartPanel.setSalesData(res);
                    }
                } catch (Exception e) {
                }
            }
        }.execute();
    }

    /** Panel de gráfico personalizado que dibuja la tendencia de ventas mensuales. @author RADJ */
    public static class VentasChartPanel extends JPanel {
        private double[] data = new double[12];
        private final String[] months = {"Ene", "Feb", "Mar", "Abr", "May", "Jun", "Jul", "Ago", "Sep", "Oct", "Nov", "Dic"};

        public VentasChartPanel() {
            setBackground(BG_CARD);
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

            // Dibujar título del gráfico
            g2.setFont(new Font("Inter", Font.BOLD, 14));
            g2.setColor(TEXT_MAIN);
            g2.drawString("📈 Tendencia de Ventas Mensuales", 20, 24);

            g2.setFont(new Font("Inter", Font.PLAIN, 10));
            g2.setColor(TEXT_MUTED);
            g2.drawString("Ingresos mensuales en pesos colombianos (año actual)", 20, 40);

            // Márgenes del área de gráfico
            int paddingLeft = 75;
            int paddingRight = 30;
            int paddingTop = 60;
            int paddingBottom = 40;

            int graphWidth = width - paddingLeft - paddingRight;
            int graphHeight = height - paddingTop - paddingBottom;

            // Determinar valor máximo para escalar el eje Y
            double maxVal = 50000; // Valor mínimo base
            for (double val : data) {
                if (val > maxVal) {
                    maxVal = val;
                }
            }

            // Redondear el máximo a un múltiplo limpio para las divisiones
            int numDivisions = 5;
            double divisionStepVal = maxVal / numDivisions;

            // Dibujar "COP" una sola vez en la parte superior del eje Y. @author RADJ
            g2.setFont(new Font("Inter", Font.BOLD, 10));
            g2.setColor(TEXT_MUTED);
            g2.drawString("COP", paddingLeft - 10 - g2.getFontMetrics().stringWidth("COP"), paddingTop - 12);

            // Dibujar rejilla horizontal y etiquetas del eje Y
            java.text.NumberFormat nfY = java.text.NumberFormat.getNumberInstance(new java.util.Locale("es", "CO"));
            nfY.setMaximumFractionDigits(0);
            g2.setFont(new Font("Inter", Font.PLAIN, 10));

            for (int i = 0; i <= numDivisions; i++) {
                double currentVal = i * divisionStepVal;
                int y = paddingTop + graphHeight - (int) ((currentVal / maxVal) * graphHeight);

                // Dibujar línea guía de rejilla
                if (i > 0) {
                    g2.setColor(new Color(255, 255, 255, 10));
                    g2.drawLine(paddingLeft, y, paddingLeft + graphWidth, y);
                }

                // Etiqueta formateada con el valor real
                String labelStr = nfY.format(currentVal);

                g2.setColor(TEXT_MUTED);
                g2.drawString(labelStr, paddingLeft - 10 - g2.getFontMetrics().stringWidth(labelStr), y + 4);
            }

            // Dibujar rejilla vertical y etiquetas del eje X
            int stepX = graphWidth / 11;
            int[] pointXs = new int[12];
            int[] pointYs = new int[12];

            for (int i = 0; i < 12; i++) {
                int x = paddingLeft + i * stepX;
                pointXs[i] = x;
                pointYs[i] = paddingTop + graphHeight - (int) ((data[i] / maxVal) * graphHeight);

                // Rejilla vertical suave
                g2.setColor(new Color(255, 255, 255, 8));
                g2.drawLine(x, paddingTop, x, paddingTop + graphHeight);

                // Nombre del mes centrado
                g2.setColor(TEXT_MUTED);
                String monthName = months[i];
                int strW = g2.getFontMetrics().stringWidth(monthName);
                g2.drawString(monthName, x - strW / 2, paddingTop + graphHeight + 18);
            }

            // Dibujar eje X principal
            g2.setColor(new Color(255, 255, 255, 20));
            g2.drawLine(paddingLeft, paddingTop + graphHeight, paddingLeft + graphWidth, paddingTop + graphHeight);

            // Crear el trazado de la línea con curvas
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

            // Rellenar área inferior del degradado
            Path2D.Double fillPath = (Path2D.Double) path.clone();
            fillPath.lineTo(pointXs[11], paddingTop + graphHeight);
            fillPath.lineTo(pointXs[0], paddingTop + graphHeight);
            fillPath.closePath();

            g2.setPaint(new GradientPaint(0, paddingTop, new Color(99, 102, 241, 45), 0, paddingTop + graphHeight, new Color(99, 102, 241, 0)));
            g2.fill(fillPath);

            // Dibujar la línea principal
            g2.setColor(PRIMARY);
            g2.setStroke(new BasicStroke(3f, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND));
            g2.draw(path);

            // Dibujar los puntos resaltados
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

                    // Formatear con el valor real sin el símbolo de dólar ni K/M. @author RADJ
                    java.text.NumberFormat nfPoint = java.text.NumberFormat.getNumberInstance(new java.util.Locale("es", "CO"));
                    nfPoint.setMaximumFractionDigits(0);
                    String valStr = nfPoint.format(data[i]);

                    int strW = g2.getFontMetrics().stringWidth(valStr);
                    g2.drawString(valStr, x - strW / 2, y - 10);
                }
            }

            g2.dispose();
        }
    }
}
