package com.acacioswork.interfaz_usuario;

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

    public Administrador() {
        try {
            setLayout(new BorderLayout());
            setBackground(BG_DARK);
            add(buildToolbar(), BorderLayout.NORTH);

            cardLayout = new CardLayout();
            contentPanel = new JPanel(cardLayout);
            contentPanel.setBackground(BG_DARK);

            contentPanel.add(buildInventarioPanel(), "inventario");
            contentPanel.add(buildProveedoresPanel(), "proveedores");
            contentPanel.add(buildClientesPanel(), "clientes");
            contentPanel.add(buildUsuariosPanel(), "usuarios");
            contentPanel.add(buildReportesPanel(), "reportes");
            contentPanel.add(buildAlertasPanel(), "alertas");

            add(contentPanel, BorderLayout.CENTER);
            cardLayout.show(contentPanel, "inventario");
        } catch (Exception e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(null, "Error al cargar el Dashboard: " + e.getMessage());
        }
    }

    private JPanel buildToolbar() {
        JPanel toolbar = new JPanel(new FlowLayout(FlowLayout.LEFT, 6, 8));
        toolbar.setBackground(BG_SIDEBAR);
        toolbar.setBorder(BorderFactory.createMatteBorder(0, 0, 1, 0, new Color(255, 255, 255, 15)));

        JLabel brand = new JLabel("AcaciosWork");
        brand.setForeground(PRIMARY);
        brand.setFont(new Font("Dialog", Font.BOLD, 14));
        brand.setBorder(new EmptyBorder(0, 8, 0, 16));
        toolbar.add(brand);

        JButton btnSalir = createToolbarBtn("‹ Salir", new Color(71, 85, 105), new Color(51, 65, 85));
        btnSalir.setBackground(new Color(71, 85, 105));
        btnSalir.addActionListener(e -> MainFrame.navigateTo(new Login()));
        toolbar.add(btnSalir);

        String[][] sections = {
                { "Inventario", "inventario", "249,115,22", "239,68,68" },
                { "Proveedores", "proveedores", "249,115,22", "239,68,68" },
                { "Clientes", "clientes", "249,115,22", "239,68,68" },
                { "Usuarios", "usuarios", "249,115,22", "239,68,68" },
                { "Reportes", "reportes", "249,115,22", "239,68,68" },
                { "⚠ Alertas", "alertas", "236,72,153", "244,63,94" },
        };
        for (String[] s : sections) {
            Color c1 = parseColor(s[2]);
            Color c2 = parseColor(s[3]);
            JButton btn = createToolbarBtn(s[0], c1, c2);
            btn.putClientProperty("isTab", true);
            btn.addActionListener(e -> {
                setActiveBtn(btn, c1, c2);
                cardLayout.show(contentPanel, s[1]);
                if (s[1].equals("alertas"))
                    refreshAlertas();
            });
            toolbar.add(btn);
            if (s[1].equals("inventario"))
                btn.setBackground(PRIMARY);
        }
        return toolbar;
    }

    private void setActiveBtn(JButton btn, Color c1, Color c2) {
        for (Component c : ((JPanel) btn.getParent()).getComponents()) {
            if (c instanceof JButton && Boolean.TRUE.equals(((JButton) c).getClientProperty("isTab"))) {
                c.setBackground(null);
            }
        }
        btn.setBackground(c1.darker());
    }

    private JButton createToolbarBtn(String text, Color c1, Color c2) {
        JButton btn = new JButton(text) {
            @Override
            protected void paintComponent(Graphics g) {
                boolean active = getBackground() != null;
                setForeground(active ? Color.WHITE : TEXT_MUTED);
                if (active) {
                    Graphics2D g2 = (Graphics2D) g.create();
                    g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                    g2.setPaint(new GradientPaint(0, 0, c1, 0, getHeight(), c2));
                    g2.fillRoundRect(0, 0, getWidth(), getHeight(), 20, 20);
                    g2.dispose();
                }
                super.paintComponent(g);
            }
        };
        btn.setForeground(TEXT_MUTED);
        btn.setFont(new Font("Dialog", Font.BOLD, 12));
        btn.setBorder(new EmptyBorder(7, 18, 7, 18));
        btn.setFocusPainted(false);
        btn.setContentAreaFilled(false);
        btn.setOpaque(false);
        btn.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        return btn;
    }

    private JPanel buildInventarioPanel() {
        JPanel panel = createContentPanel();
        JPanel stats = new JPanel(new GridLayout(1, 3, 12, 0));
        stats.setOpaque(false);
        stats.setBorder(new EmptyBorder(0, 0, 16, 0));
        stats.add(buildStatCard("Total Productos", "0", TEXT_MAIN));
        stats.add(buildStatCard("Stock Bajo", "0", DANGER));
        stats.add(buildStatCard("Valor Inventario", "$0", ACCENT));

        JTable table = buildStyledTable(
                new String[] { "ID", "Código", "Nombre", "Stock", "P. Compra", "P. Venta", "IVA", "Estado" });
        hideColumn(table, 0);

        JButton bAdd = createActionButton("+ Nuevo", ACCENT);
        bAdd.addActionListener(e -> agregarProductoDash(table, stats));
        JButton bEdit = createActionButton("✎ Editar", PRIMARY);
        bEdit.addActionListener(e -> editarProductoInv(table, stats));
        JButton bDel = createActionButton("🗑 Eliminar", DANGER);
        bDel.addActionListener(
                e -> eliminarGeneric(table, "/productos", "Producto", () -> refreshInventario(table, stats)));

        panel.add(buildSectionHeader("Inventario de Productos", "Existencias y precios", bAdd, bEdit, bDel),
                BorderLayout.NORTH);

        JPanel center = new JPanel(new BorderLayout(0, 12));
        center.setOpaque(false);
        center.add(stats, BorderLayout.NORTH);
        center.add(wrapTable(table), BorderLayout.CENTER);
        panel.add(center, BorderLayout.CENTER);

        refreshInventario(table, stats);
        return panel;
    }

    private JPanel buildProveedoresPanel() {
        JPanel panel = createContentPanel();
        JTable table = buildStyledTable(
                new String[] { "ID", "Nombre", "Teléfono", "Email", "Doc/NIT", "Cuenta Bancaria", "Estado" });
        hideColumn(table, 0);

        JButton bAdd = createActionButton("+ Nuevo", ACCENT);
        bAdd.addActionListener(e -> agregarProveedorDash(table));
        JButton bEdit = createActionButton("✎ Editar", PRIMARY);
        bEdit.addActionListener(e -> editarProveedorDash(table));
        JButton bDel = createActionButton("🗑 Eliminar", DANGER);
        bDel.addActionListener(
                e -> eliminarGeneric(table, "/proveedores", "Proveedor", () -> refreshProveedores(table)));

        panel.add(buildSectionHeader("Proveedores", "Gestión de suministradores", bAdd, bEdit, bDel),
                BorderLayout.NORTH);
        panel.add(wrapTable(table), BorderLayout.CENTER);
        refreshProveedores(table);
        return panel;
    }

    private JPanel buildClientesPanel() {
        JPanel panel = createContentPanel();
        JTable table = buildStyledTable(
                new String[] { "ID", "Nombre", "Identificación", "Teléfono", "Email", "Frecuente", "Estado" });
        hideColumn(table, 0);

        JButton bAdd = createActionButton("+ Nuevo", ACCENT);
        bAdd.addActionListener(e -> agregarClienteDash(table));
        JButton bEdit = createActionButton("✎ Editar", PRIMARY);
        bEdit.addActionListener(e -> editarClienteDash(table));
        JButton bDel = createActionButton("🗑 Eliminar", DANGER);
        bDel.addActionListener(e -> eliminarGeneric(table, "/clientes", "Cliente", () -> refreshClientes(table)));

        panel.add(buildSectionHeader("Clientes", "Base de datos registrados", bAdd, bEdit, bDel), BorderLayout.NORTH);
        panel.add(wrapTable(table), BorderLayout.CENTER);
        refreshClientes(table);
        return panel;
    }

    private JPanel buildUsuariosPanel() {
        JPanel panel = createContentPanel();
        JTable table = buildStyledTable(new String[] { "ID", "Nombre", "Usuario", "Doc/Id", "Estado" });
        hideColumn(table, 0);

        JButton bAdd = createActionButton("+ Nuevo", ACCENT);
        bAdd.addActionListener(e -> agregarUsuarioDash(table));

        JButton bEdit = createActionButton("✎ Editar", PRIMARY);
        bEdit.addActionListener(e -> editarUsuarioDash(table));

        JButton bDel = createActionButton("🗑 Eliminar", DANGER);
        bDel.addActionListener(e -> eliminarUsuarioDash(table));

        panel.add(buildSectionHeader("Usuarios del Sistema", "Administración de accesos", bAdd, bEdit, bDel),
                BorderLayout.NORTH);
        panel.add(wrapTable(table), BorderLayout.CENTER);
        loadTable(table, "/usuarios", row -> new Object[] { id(row), str(row, "nombre"), str(row, "usuario"),
                str(row, "numeroDocumento"), str(row, "activo").equals("1") ? "Activo" : "Inactivo" });
        return panel;
    }

    private JPanel buildReportesPanel() {
        JPanel panel = createContentPanel();
        panel.add(buildSectionHeader("Reportes", "Exportación de informes", (JButton) null), BorderLayout.NORTH);
        JPanel grid = new JPanel(new GridLayout(12, 8, 12, 12));
        grid.setOpaque(false);
        String[] rpts = { "📦 INVENTARIO", "👥 CLIENTES", "🏭 PROVEEDORES", "👤 USUARIOS", "📊 RESUMEN" };
        for (String r : rpts)
            grid.add(buildReportCard(r));
        JScrollPane scroll = new JScrollPane(grid);
        scroll.setBorder(BorderFactory.createEmptyBorder());
        scroll.setOpaque(false);
        scroll.getViewport().setOpaque(false);
        panel.add(scroll, BorderLayout.CENTER);
        return panel;
    }

    private JPanel buildAlertasPanel() {
        JPanel panel = createContentPanel();

        JButton bPdf = createActionButton("Descargar lista PDF", new Color(40, 167, 69));
        JButton bPrint = createActionButton("Imprimir lista", new Color(34, 139, 34));

        panel.add(
                buildSectionHeader("Alertas de Stock Crítico",
                        "Productos con existencias en nivel mínimo de reabastecimiento", bPdf, bPrint),
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

        panel.add(wrapTable(tableAlertas), BorderLayout.CENTER);

        bPdf.addActionListener(e -> generarReporte("stock-bajo"));
        bPrint.addActionListener(e -> generarReporte("stock-bajo"));

        // Botón Ver Proveedor
        tableAlertas.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent e) {
                int row = tableAlertas.getSelectedRow();
                int col = tableAlertas.getSelectedColumn();
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
        Object idVal = table.getModel().getValueAt(row, 0);
        String nameVal = table.getModel().getValueAt(row, 1).toString();

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
                str(row, "activo").equals("1") ? "Activo" : "Inactivo"
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
                str(row, "activo").equals("1") ? "Activo" : "Inactivo"
        });
    }

    private void populateInventarioTable(JTable table, Object[] rows, JPanel stats) {
        DefaultTableModel model = (DefaultTableModel) table.getModel();
        model.setRowCount(0);
        if (rows == null)
            return;
        int bajo = 0;
        double valor = 0;
        for (Object raw : rows) {
            java.util.Map<String, Object> p = (java.util.Map<String, Object>) raw;
            Long id = id(p);
            int qty = num(p, "cantidad");
            int min = num(p, "stockMinimo");
            double precioCompra = dbl(p, "precioCompra");
            double precioVenta = dbl(p, "precioVenta");
            valor += qty * precioVenta;
            if (qty <= min)
                bajo++;

            String estadoLabel = "1".equals(str(p, "estado")) ? "Activo" : "Inactivo";
            String ivaLabel = str(p, "iva") != null ? str(p, "iva") + "%" : "0%";

            model.addRow(new Object[] {
                    id,
                    str(p, "codigoBarras"),
                    str(p, "nombre"),
                    qty + " uds",
                    "$" + (long) precioCompra,
                    "$" + (long) precioVenta,
                    ivaLabel,
                    estadoLabel
            });
        }
        updateStatCard((JPanel) stats.getComponents()[0], String.valueOf(rows.length));
        updateStatCard((JPanel) stats.getComponents()[1], String.valueOf(bajo));
        updateStatCard((JPanel) stats.getComponents()[2], "$" + String.format("%,.0f", valor));
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
                str(row, "numeroDocumento"), str(row, "activo").equals("1") ? "Activo" : "Inactivo" });
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
        lt.setFont(new Font("Dialog", Font.BOLD, 20));
        JLabel ls = new JLabel(s);
        ls.setForeground(TEXT_MUTED);
        ls.setFont(new Font("Dialog", Font.PLAIN, 12));
        JPanel l = new JPanel(new GridLayout(2, 1, 0, 2));
        l.setOpaque(false);
        l.add(lt);
        l.add(ls);
        h.add(l, BorderLayout.WEST);
        if (b != null && b.length > 0 && b[0] != null) {
            JPanel r = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 0));
            r.setOpaque(false);
            for (JButton btn : b)
                if (btn != null)
                    r.add(btn);
            h.add(r, BorderLayout.EAST);
        }
        return h;
    }

    private JButton createActionButton(String t, Color bg) {
        JButton b = new JButton(t);
        b.setBackground(bg);
        b.setForeground(Color.WHITE);
        b.setFont(new Font("Dialog", Font.BOLD, 12));
        b.setBorder(new EmptyBorder(8, 18, 8, 18));
        b.setFocusPainted(false);
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
        JLabel lv = new JLabel(v);
        lv.setForeground(vc);
        lv.setFont(new Font("Dialog", Font.BOLD, 22));
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

    private JPanel buildReportCard(String t) {
        JPanel c = new JPanel(new BorderLayout(0, 8));
        c.setBackground(BG_CARD);
        c.setBorder(BorderFactory.createCompoundBorder(BorderFactory.createLineBorder(new Color(255, 255, 255, 13), 1),
                new EmptyBorder(16, 16, 16, 16)));
        JLabel lt = new JLabel(t);
        lt.setForeground(TEXT_MAIN);
        JButton b = new JButton("Generar PDF");
        b.setBackground(PRIMARY);
        b.setForeground(Color.WHITE);
        b.setFocusPainted(false);
        b.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));

        String tipo = "";
        if (t.contains("Inventario"))
            tipo = "inventario";
        else if (t.contains("Stock Bajo"))
            tipo = "stock-bajo";
        else if (t.contains("Clientes"))
            tipo = "clientes";
        else if (t.contains("Proveedores"))
            tipo = "proveedores";
        else if (t.contains("Usuarios"))
            tipo = "usuarios";
        else if (t.contains("Resumen"))
            tipo = "resumen";

        final String finalTipo = tipo;
        b.addActionListener(e -> generarReporte(finalTipo));

        c.add(lt, BorderLayout.NORTH);

        if (t.contains("Inventario")) {
            try {
                java.net.URL imgUrl = getClass().getResource("/images/inventario.png");
                if (imgUrl != null) {
                    javax.swing.ImageIcon origIcon = new javax.swing.ImageIcon(imgUrl);
                    java.awt.Image img = origIcon.getImage();
                    java.awt.Image newImg = img.getScaledInstance(160, 160, java.awt.Image.SCALE_SMOOTH);
                    JLabel lblImg = new JLabel(new javax.swing.ImageIcon(newImg));
                    lblImg.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
                    c.add(lblImg, BorderLayout.CENTER);
                }
            } catch (Exception ex) {
                // Silencioso
            }
        }

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
                                int qty = num(p, "cantidad");
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
                                int qty = num(p, "cantidad");
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
                                int qty = num(p, "cantidad");
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

    private Color parseColor(String rgb) {
        String[] p = rgb.split(",");
        return new Color(Integer.parseInt(p[0].trim()), Integer.parseInt(p[1].trim()), Integer.parseInt(p[2].trim()));
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
                            int stock = num(p, "cantidad");
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
}
