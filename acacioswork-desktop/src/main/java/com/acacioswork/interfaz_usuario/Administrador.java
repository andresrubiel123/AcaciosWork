package com.acacioswork.interfaz_usuario;

import java.awt.BorderLayout;
import java.awt.CardLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Cursor;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.GradientPaint;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.GridLayout;
import java.awt.RenderingHints;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JPasswordField;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.SwingUtilities;
import javax.swing.SwingWorker;
import javax.swing.border.EmptyBorder;
import javax.swing.table.DefaultTableModel;

import com.acacioswork.model.AlertaStockMinimo;
import com.acacioswork.model.Producto;
import com.acacioswork.model.Rol;
import com.acacioswork.model.Usuario;
import com.acacioswork.util.ApiClient;

public class Administrador extends JPanel {

    private static final Color BG_DARK    = new Color(15, 23, 42);
    private static final Color BG_CARD    = new Color(30, 41, 59);
    private static final Color BG_SIDEBAR = new Color(2, 6, 23);
    private static final Color TEXT_MAIN  = new Color(248, 250, 252);
    private static final Color TEXT_MUTED = new Color(148, 163, 184);
    private static final Color PRIMARY    = new Color(99, 102, 241);
    private static final Color ACCENT     = new Color(16, 185, 129);
    private static final Color DANGER     = new Color(239, 68, 68);
    
    private JPanel contentPanel;
    private CardLayout cardLayout;
    private JPanel alertasContentPanel;

    public Administrador() {
        try {
            setLayout(new BorderLayout());
            setBackground(BG_DARK);
            add(buildToolbar(), BorderLayout.NORTH);
            
            cardLayout = new CardLayout();
            contentPanel = new JPanel(cardLayout);
            contentPanel.setBackground(BG_DARK);
            
            contentPanel.add(buildInventarioPanel(),  "inventario");
            contentPanel.add(buildProveedoresPanel(), "proveedores");
            contentPanel.add(buildClientesPanel(),    "clientes");
            contentPanel.add(buildUsuariosPanel(),    "usuarios");
            contentPanel.add(buildReportesPanel(),    "reportes");
            contentPanel.add(buildAlertasPanel(),     "alertas");
            
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
        btnSalir.addActionListener(e -> MainFrame.navigateTo(new Login()));
        toolbar.add(btnSalir);
        
        String[][] sections = {
            {"Inventario",  "inventario",  "249,115,22",  "239,68,68"},
            {"Proveedores", "proveedores", "249,115,22",  "239,68,68"},
            {"Clientes",    "clientes",    "249,115,22",  "239,68,68"},
            {"Usuarios",    "usuarios",    "249,115,22",  "239,68,68"},
            {"Reportes",    "reportes",    "249,115,22",  "239,68,68"},
            {"⚠ Alertas",   "alertas",     "236,72,153",  "244,63,94"},
        };
        for (String[] s : sections) {
            Color c1 = parseColor(s[2]); Color c2 = parseColor(s[3]);
            JButton btn = createToolbarBtn(s[0], c1, c2);
            btn.addActionListener(e -> {
                setActiveBtn(btn, c1, c2);
                cardLayout.show(contentPanel, s[1]);
                if (s[1].equals("alertas")) refreshAlertas();
            });
            toolbar.add(btn);
            if (s[1].equals("inventario")) btn.setBackground(PRIMARY);
        }
        return toolbar;
    }

    private void setActiveBtn(JButton btn, Color c1, Color c2) {
        for (Component c : ((JPanel) btn.getParent()).getComponents()) if (c instanceof JButton) c.setBackground(null);
        btn.setBackground(c1.darker());
    }

    private JButton createToolbarBtn(String text, Color c1, Color c2) {
        JButton btn = new JButton(text) {
            @Override protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setPaint(new GradientPaint(0, 0, c1, 0, getHeight(), c2));
                g2.fillRoundRect(0, 0, getWidth(), getHeight(), 20, 20);
                g2.dispose();
                super.paintComponent(g);
            }
        };
        btn.setForeground(Color.WHITE); btn.setFont(new Font("Dialog", Font.BOLD, 12));
        btn.setBorder(new EmptyBorder(7, 18, 7, 18));
        btn.setFocusPainted(false); btn.setContentAreaFilled(false);
        btn.setOpaque(false); btn.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        return btn;
    }

    private JPanel buildInventarioPanel() {
        JPanel panel = createContentPanel();
        JPanel stats = new JPanel(new GridLayout(1, 3, 12, 0));
        stats.setOpaque(false); stats.setBorder(new EmptyBorder(0, 0, 16, 0));
        stats.add(buildStatCard("Total Productos", "0", TEXT_MAIN));
        stats.add(buildStatCard("Stock Bajo", "0", DANGER));
        stats.add(buildStatCard("Valor Inventario", "$0", ACCENT));

        JTable table = buildStyledTable(new String[]{"ID", "Código", "Nombre", "Stock", "P. Venta", "IVA", "Estado"});
        hideColumn(table, 0);

        JButton bAdd = createActionButton("+ Nuevo", ACCENT); bAdd.addActionListener(e -> agregarProductoDash(table, stats));
        JButton bEdit = createActionButton("✎ Editar", PRIMARY); bEdit.addActionListener(e -> editarProductoInv(table, stats));
        JButton bDel = createActionButton("🗑 Eliminar", DANGER); bDel.addActionListener(e -> eliminarGeneric(table, "/productos", "Producto", () -> refreshInventario(table, stats)));

        panel.add(buildSectionHeader("Inventario de Productos", "Existencias y precios", bAdd, bEdit, bDel), BorderLayout.NORTH);
        
        JPanel center = new JPanel(new BorderLayout(0, 12)); center.setOpaque(false);
        center.add(stats, BorderLayout.NORTH); center.add(wrapTable(table), BorderLayout.CENTER);
        panel.add(center, BorderLayout.CENTER);

        refreshInventario(table, stats);
        return panel;
    }

    private JPanel buildProveedoresPanel() {
        JPanel panel = createContentPanel();
        JTable table = buildStyledTable(new String[]{"ID", "Nombre", "Teléfono", "Email", "Doc/NIT", "Cuenta Bancaria", "Estado"});
        hideColumn(table, 0);

        JButton bAdd = createActionButton("+ Nuevo", ACCENT); bAdd.addActionListener(e -> agregarProveedorDash(table));
        JButton bEdit = createActionButton("✎ Editar", PRIMARY); bEdit.addActionListener(e -> editarProveedorDash(table));
        JButton bDel = createActionButton("🗑 Eliminar", DANGER); bDel.addActionListener(e -> eliminarGeneric(table, "/proveedores", "Proveedor", () -> refreshProveedores(table)));

        panel.add(buildSectionHeader("Proveedores", "Gestión de suministradores", bAdd, bEdit, bDel), BorderLayout.NORTH);
        panel.add(wrapTable(table), BorderLayout.CENTER);
        refreshProveedores(table);
        return panel;
    }

    private JPanel buildClientesPanel() {
        JPanel panel = createContentPanel();
        JTable table = buildStyledTable(new String[]{"ID", "Nombre", "Identificación", "Teléfono", "Email", "Frecuente", "Estado"});
        hideColumn(table, 0);

        JButton bAdd = createActionButton("+ Nuevo", ACCENT); bAdd.addActionListener(e -> agregarClienteDash(table));
        JButton bEdit = createActionButton("✎ Editar", PRIMARY); bEdit.addActionListener(e -> editarClienteDash(table));
        JButton bDel = createActionButton("🗑 Eliminar", DANGER); bDel.addActionListener(e -> eliminarGeneric(table, "/clientes", "Cliente", () -> refreshClientes(table)));

        panel.add(buildSectionHeader("Clientes", "Base de datos registrados", bAdd, bEdit, bDel), BorderLayout.NORTH);
        panel.add(wrapTable(table), BorderLayout.CENTER);
        refreshClientes(table);
        return panel;
    }

    private JPanel buildUsuariosPanel() {
        JPanel panel = createContentPanel();
        JTable table = buildStyledTable(new String[]{"ID", "Nombre", "Usuario", "Doc/Id", "Estado"});
        hideColumn(table, 0);

        JButton bAdd = createActionButton("+ Nuevo", ACCENT);
        bAdd.addActionListener(e -> agregarUsuarioDash(table));
        
        JButton bEdit = createActionButton("✎ Editar", PRIMARY);
        bEdit.addActionListener(e -> editarUsuarioDash(table));
        
        JButton bDel = createActionButton("🗑 Eliminar", DANGER); 
        bDel.addActionListener(e -> eliminarUsuarioDash(table));

        panel.add(buildSectionHeader("Usuarios del Sistema", "Administración de accesos", bAdd, bEdit, bDel), BorderLayout.NORTH);
        panel.add(wrapTable(table), BorderLayout.CENTER);
        loadTable(table, "/usuarios", row -> new Object[]{id(row), str(row, "nombre"), str(row, "usuario"), str(row, "numeroDocumento"), str(row, "activo").equals("1") ? "Activo" : "Inactivo"});
        return panel;
    }

    private JPanel buildReportesPanel() {
        JPanel panel = createContentPanel();
        panel.add(buildSectionHeader("Reportes", "Exportación de informes", (JButton)null), BorderLayout.NORTH);
        JPanel grid = new JPanel(new GridLayout(2, 3, 12, 12)); grid.setOpaque(false);
        String[] rpts = {"📦 Inventario", "⚠️ Stock Bajo", "👥 Clientes", "🏭 Proveedores", "👤 Usuarios", "📊 Resumen"};
        for (String r : rpts) grid.add(buildReportCard(r));
        JScrollPane scroll = new JScrollPane(grid); scroll.setBorder(BorderFactory.createEmptyBorder()); scroll.setOpaque(false); scroll.getViewport().setOpaque(false);
        panel.add(scroll, BorderLayout.CENTER);
        return panel;
    }

    private JPanel buildAlertasPanel() {
        JPanel panel = createContentPanel();
        
        JPanel headerActions = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        headerActions.setOpaque(false);
        
        JButton bPdf = createActionButton("Descargar lista PDF", new Color(40, 167, 69));
        JButton bPrint = createActionButton("Imprimir lista", new Color(34, 139, 34));
        
        headerActions.add(bPdf);
        headerActions.add(bPrint);
        
        panel.add(headerActions, BorderLayout.NORTH);

        JTable table = buildStyledTable(new String[]{"ID", "Producto", "Stock Actual", "Mínimo", "Proveedor", "Acción"});
        hideColumn(table, 0);

        // Renderizador para colores
        table.setDefaultRenderer(Object.class, new javax.swing.table.DefaultTableCellRenderer() {
            @Override
            public Component getTableCellRendererComponent(JTable t, Object v, boolean s, boolean f, int r, int c) {
                Component comp = super.getTableCellRendererComponent(t, v, s, f, r, c);
                int stock = Integer.parseInt(t.getValueAt(r, 2).toString());
                if (!s) {
                    if (stock == 0) comp.setBackground(new Color(150, 40, 40)); // Rojo oscuro
                    else if (stock <= 5) comp.setBackground(new Color(150, 100, 40)); // Naranja/Marrón
                    else comp.setBackground(t.getBackground());
                }
                return comp;
            }
        });

        panel.add(new JScrollPane(table), BorderLayout.CENTER);
        
        bPdf.addActionListener(e -> JOptionPane.showMessageDialog(this, "Generando reporte PDF de stock bajo..."));
        bPrint.addActionListener(e -> JOptionPane.showMessageDialog(this, "Enviando lista a la impresora..."));

        // Botón Ver Proveedor
        table.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent e) {
                int row = table.getSelectedRow();
                int col = table.getSelectedColumn();
                if (col == 5 && row != -1) { // Columna Acción
                    Administrador.this.mostrarInfoProveedor(table.getValueAt(row, 0));
                }
            }
        });

        refreshAlertas(table);
        return panel;
    }

    private void hideColumn(JTable table, int index) {
        table.getColumnModel().getColumn(index).setMinWidth(0);
        table.getColumnModel().getColumn(index).setMaxWidth(0);
        table.getColumnModel().getColumn(index).setPreferredWidth(0);
    }

    private void eliminarGeneric(JTable table, String endpoint, String entityName, Runnable onFinish) {
        int row = table.getSelectedRow();
        if (row == -1) { JOptionPane.showMessageDialog(this, "Seleccione un " + entityName); return; }
        
        // Obtenemos el ID (columna 0, oculta)
        Object idVal = table.getModel().getValueAt(row, 0);
        String nameVal = table.getModel().getValueAt(row, 1).toString();

        if (JOptionPane.showConfirmDialog(this, "¿Eliminar " + entityName + ": " + nameVal + "?", "Confirmar Acción", JOptionPane.OK_CANCEL_OPTION, JOptionPane.WARNING_MESSAGE) == JOptionPane.OK_OPTION) {
            try { 
                ApiClient.delete(endpoint + "/" + idVal); 
                JOptionPane.showMessageDialog(this, entityName + " eliminado correctamente."); 
                if (onFinish != null) onFinish.run();
            }
            catch (Exception e) { JOptionPane.showMessageDialog(this, "Error al eliminar: " + e.getMessage()); }
        }
    }

    private void refreshInventario(JTable table, JPanel stats) {
        new SwingWorker<Void, Void>() {
            @Override protected Void doInBackground() throws Exception {
                try {
                    Object[] data = ApiClient.get("/productos", Object[].class);
                    SwingUtilities.invokeLater(() -> populateInventarioTable(table, data, stats));
                } catch(Exception e) {}
                return null;
            }
        }.execute();
    }

    private void refreshProveedores(JTable table) {
        loadTable(table, "/proveedores", row -> new Object[]{
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
        loadTable(table, "/clientes", row -> new Object[]{
            id(row), 
            str(row, "nombre"), 
            str(row, "numeroDocumento"), 
            str(row, "telefono"), 
            str(row, "email"),
            str(row, "frecuente").equals("true") ? "Sí" : "No",
            str(row, "activo").equals("1") ? "Activo" : "Inactivo"
        });
    }

    @SuppressWarnings("unchecked")
    private void populateInventarioTable(JTable table, Object[] rows, JPanel stats) {
        DefaultTableModel model = (DefaultTableModel) table.getModel(); model.setRowCount(0);
        if (rows == null) return;
        int bajo = 0; double valor = 0;
        for (Object raw : rows) {
            java.util.Map<String, Object> p = (java.util.Map<String, Object>) raw;
            Long id = id(p); int qty = num(p, "cantidad"); int min = num(p, "stockMinimo");
            double precio = dbl(p, "precioVenta"); valor += qty * precio; if (qty <= min) bajo++;
            
            String estadoLabel = "1".equals(str(p, "estado")) ? "Activo" : "Inactivo";
            String ivaLabel = str(p, "iva") != null ? str(p, "iva") + "%" : "0%";
            
            model.addRow(new Object[]{
                id, 
                str(p, "codigoBarras"), 
                str(p, "nombre"), 
                qty + " uds", 
                "$" + (long) precio, 
                ivaLabel, 
                estadoLabel
            });
        }
        updateStatCard((JPanel) stats.getComponents()[0], String.valueOf(rows.length));
        updateStatCard((JPanel) stats.getComponents()[1], String.valueOf(bajo));
        updateStatCard((JPanel) stats.getComponents()[2], "$" + String.format("%,.0f", valor));
    }

    private void agregarProductoDash(JTable table, JPanel stats) {
        JTextField txtCodigo = new JTextField();
        JTextField txtNombre = new JTextField();
        JTextField txtCant = new JTextField();
        JTextField txtPCompra = new JTextField();
        JTextField txtPVenta = new JTextField();
        
        java.util.Vector<com.acacioswork.model.Categoria> categorias = new java.util.Vector<>();
        java.util.Vector<com.acacioswork.model.Proveedor> proveedores = new java.util.Vector<>();
        
        try {
            categorias.addAll(java.util.Arrays.asList(ApiClient.get("/categorias", com.acacioswork.model.Categoria[].class)));
            proveedores.addAll(java.util.Arrays.asList(ApiClient.get("/proveedores", com.acacioswork.model.Proveedor[].class)));
        } catch (Exception e) {}

        JComboBox<com.acacioswork.model.Categoria> cbCat = new JComboBox<>(categorias);
        JComboBox<com.acacioswork.model.Proveedor> cbProv = new JComboBox<>(proveedores);

        Object[] message = {
                "Código de Barras:", txtCodigo,
                "Nombre:", txtNombre,
                "Stock Inicial:", txtCant,
                "Precio Compra:", txtPCompra,
                "Precio Venta:", txtPVenta,
                "Categoría:", cbCat,
                "Proveedor:", cbProv
        };

        if (JOptionPane.showConfirmDialog(this, message, "Registrar Nuevo Producto", JOptionPane.OK_CANCEL_OPTION) == JOptionPane.OK_OPTION) {
            try {
                Producto p = new Producto();
                p.setCodigoBarras(txtCodigo.getText());
                p.setNombre(txtNombre.getText());
                p.setCantidad(Integer.parseInt(txtCant.getText()));
                p.setPrecioCompra(Double.parseDouble(txtPCompra.getText()));
                p.setPrecioVenta(Double.parseDouble(txtPVenta.getText()));
                p.setIva(19.0); p.setEstado("1"); p.setStockMinimo(5);

                com.acacioswork.model.Categoria c = (com.acacioswork.model.Categoria) cbCat.getSelectedItem();
                if (c != null) p.setIdCategoria(c.getId());
                
                com.acacioswork.model.Proveedor pr = (com.acacioswork.model.Proveedor) cbProv.getSelectedItem();
                if (pr != null) p.setIdProveedor(pr.getId());

                ApiClient.post("/productos", p, Producto.class);
                JOptionPane.showMessageDialog(this, "Producto agregado.");
                refreshInventario(table, stats);
            } catch (Exception e) {
                JOptionPane.showMessageDialog(this, "Error: " + e.getMessage());
            }
        }
    }

    private void editarProductoInv(JTable t, JPanel s) {
        int r = t.getSelectedRow(); if (r == -1) return; 
        Long id = (Long) t.getValueAt(r, 0);
        try {
            Producto p = ApiClient.get("/productos/" + id, Producto.class);
            JTextField txtN = new JTextField(p.getNombre()); 
            JTextField txtC = new JTextField(String.valueOf(p.getCantidad()));
            JTextField txtPC = new JTextField(String.valueOf(p.getPrecioCompra()));
            JTextField txtPV = new JTextField(String.valueOf(p.getPrecioVenta()));

            Object[] msg = {
                "Nombre del Producto:", txtN, 
                "Stock (Existencias):", txtC,
                "Precio de Compra:", txtPC,
                "Precio de Venta:", txtPV
            };

            if (JOptionPane.showConfirmDialog(this, msg, "Editar Producto", JOptionPane.OK_CANCEL_OPTION, JOptionPane.QUESTION_MESSAGE) == JOptionPane.OK_OPTION) {
                p.setNombre(txtN.getText()); 
                p.setCantidad(Integer.parseInt(txtC.getText()));
                p.setPrecioCompra(Double.parseDouble(txtPC.getText()));
                p.setPrecioVenta(Double.parseDouble(txtPV.getText()));

                ApiClient.put("/productos/" + id, p, Producto.class); 
                JOptionPane.showMessageDialog(this, "Producto actualizado con éxito.");
                refreshInventario(t, s);
            }
        } catch(Exception e) {
            JOptionPane.showMessageDialog(this, "Error al actualizar: " + e.getMessage());
        }
    }

    private void refreshUsuarios(JTable table) {
        loadTable(table, "/usuarios", row -> new Object[]{id(row), str(row, "nombre"), str(row, "usuario"), str(row, "numeroDocumento"), str(row, "activo").equals("1") ? "Activo" : "Inactivo"});
    }

    private void agregarUsuarioDash(JTable table) {
        JTextField txtIden = new JTextField();
        JTextField txtNom = new JTextField();
        JTextField txtApe = new JTextField();
        JTextField txtTel = new JTextField();
        JTextField txtMail = new JTextField();
        JTextField txtUser = new JTextField();
        JPasswordField txtPass = new JPasswordField();
        
        String[] tipos = {"Cédula", "NIT", "Pasaporte"};
        JComboBox<String> cbTipo = new JComboBox<>(tipos);

        java.util.Vector<Rol> roles = new java.util.Vector<>();
        try {
            Rol[] rolesArr = ApiClient.get("/roles", Rol[].class);
            if (rolesArr != null) roles.addAll(java.util.Arrays.asList(rolesArr));
        } catch (Exception e) {
            roles.add(new Rol(1L, "Administrador"));
            roles.add(new Rol(2L, "Vendedor"));
        }
        JComboBox<Rol> cbRol = new JComboBox<>(roles);
        JCheckBox chkAct = new JCheckBox("Usuario activo", true);

        Object[] msg = {
            "Tipo Documento:", cbTipo,
            "Doc/Identificación:", txtIden,
            "Nombre:", txtNom,
            "Apellido:", txtApe,
            "Teléfono:", txtTel,
            "Email:", txtMail,
            "Usuario (Login):", txtUser,
            "Contraseña:", txtPass,
            "Rol:", cbRol,
            chkAct
        };

        if (JOptionPane.showConfirmDialog(this, msg, "Registrar Nuevo Usuario", JOptionPane.OK_CANCEL_OPTION) == JOptionPane.OK_OPTION) {
            try {
                Usuario u = new Usuario();
                u.setIdTipoDocumento((long) (cbTipo.getSelectedIndex() + 1));
                u.setIdentificacion(txtIden.getText());
                u.setNombre(txtNom.getText());
                u.setApellido(txtApe.getText());
                // Nota: Si el modelo Usuario de escritorio no tiene 'telefono', lo ignorará.
                // Pero lo incluimos para que la lógica sea robusta.
                u.setEmail(txtMail.getText());
                u.setUsuario(txtUser.getText());
                u.setClave(new String(txtPass.getPassword()));
                Rol r = (Rol) cbRol.getSelectedItem();
                u.setIdRol(r != null ? r.getId() : 2L);
                u.setActivo(chkAct.isSelected() ? 1 : 0);

                ApiClient.post("/usuarios", u, Usuario.class);
                JOptionPane.showMessageDialog(this, "Usuario creado exitosamente.");
                refreshUsuarios(table);
            } catch (Exception e) {
                JOptionPane.showMessageDialog(this, "Error al crear: " + e.getMessage());
            }
        }
    }

    private void editarUsuarioDash(JTable table) {
        int row = table.getSelectedRow();
        if (row == -1) { JOptionPane.showMessageDialog(this, "Seleccione un usuario de la tabla."); return; }
        String iden = table.getValueAt(row, 3).toString();
        try {
            // Buscamos el usuario en la lista completa ya que el backend no tiene GET individual
            Usuario[] todos = ApiClient.get("/usuarios", Usuario[].class);
            Usuario u = java.util.Arrays.stream(todos)
                    .filter(user -> iden.equals(user.getIdentificacion()))
                    .findFirst()
                    .orElse(null);

            if (u == null) { JOptionPane.showMessageDialog(this, "No se encontró la información del usuario."); return; }

            JTextField txtNom = new JTextField(u.getNombre());
            JTextField txtApe = new JTextField(u.getApellido());
            JTextField txtMail = new JTextField(u.getEmail());
            JTextField txtUser = new JTextField(u.getUsuario());
            JPasswordField txtPass = new JPasswordField();
            JCheckBox chkAct = new JCheckBox("Usuario activo", u.getActivo() != null && u.getActivo() == 1);
            
            Object[] msg = {
                "Nombre:", txtNom,
                "Apellido:", txtApe,
                "Email:", txtMail,
                "Usuario:", txtUser,
                "Nueva Contraseña (dejar vacío para no cambiar):", txtPass,
                chkAct
            };

            if (JOptionPane.showConfirmDialog(this, msg, "Editar Usuario", JOptionPane.OK_CANCEL_OPTION) == JOptionPane.OK_OPTION) {
                u.setNombre(txtNom.getText());
                u.setApellido(txtApe.getText());
                u.setEmail(txtMail.getText());
                u.setUsuario(txtUser.getText());
                u.setActivo(chkAct.isSelected() ? 1 : 0);
                String pass = new String(txtPass.getPassword());
                if (!pass.isEmpty()) u.setClave(pass);

                ApiClient.put("/usuarios/" + iden, u, Usuario.class);
                JOptionPane.showMessageDialog(this, "Usuario actualizado correctamente.");
                refreshUsuarios(table);
            }
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, "Error al editar: " + e.getMessage());
        }
    }

    private void eliminarUsuarioDash(JTable table) {
        int row = table.getSelectedRow();
        if (row == -1) { JOptionPane.showMessageDialog(this, "Seleccione un usuario para eliminar."); return; }
        String iden = table.getValueAt(row, 3).toString();
        if (JOptionPane.showConfirmDialog(this, "¿Está seguro de eliminar el usuario: " + iden + "?", "Confirmar Eliminación", JOptionPane.OK_CANCEL_OPTION, JOptionPane.WARNING_MESSAGE) == JOptionPane.OK_OPTION) {
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
        JPanel h = new JPanel(new BorderLayout()); h.setOpaque(false); h.setBorder(new EmptyBorder(0, 0, 16, 0));
        JLabel lt = new JLabel(t); lt.setForeground(TEXT_MAIN); lt.setFont(new Font("Dialog", Font.BOLD, 20));
        JLabel ls = new JLabel(s); ls.setForeground(TEXT_MUTED); ls.setFont(new Font("Dialog", Font.PLAIN, 12));
        JPanel l = new JPanel(new GridLayout(2, 1, 0, 2)); l.setOpaque(false); l.add(lt); l.add(ls);
        h.add(l, BorderLayout.WEST);
        if (b != null && b.length > 0 && b[0] != null) {
            JPanel r = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 0)); r.setOpaque(false);
            for (JButton btn : b) if(btn != null) r.add(btn);
            h.add(r, BorderLayout.EAST);
        }
        return h;
    }

    private JButton createActionButton(String t, Color bg) {
        JButton b = new JButton(t); b.setBackground(bg); b.setForeground(Color.WHITE);
        b.setFont(new Font("Dialog", Font.BOLD, 12)); b.setBorder(new EmptyBorder(8, 18, 8, 18));
        b.setFocusPainted(false); b.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        return b;
    }

    private JPanel buildStatCard(String l, String v, Color vc) {
        JPanel c = new JPanel(new GridLayout(2, 1, 0, 4)); c.setBackground(BG_CARD);
        c.setBorder(BorderFactory.createCompoundBorder(BorderFactory.createLineBorder(new Color(255,255,255,13),1), new EmptyBorder(14, 18, 14, 18)));
        JLabel ll = new JLabel(l); ll.setForeground(TEXT_MUTED); JLabel lv = new JLabel(v); lv.setForeground(vc);
        lv.setFont(new Font("Dialog", Font.BOLD, 22)); lv.setName("value"); c.add(ll); c.add(lv);
        return c;
    }

    private void updateStatCard(JPanel c, String v) {
        for (Component cmp : c.getComponents()) if (cmp instanceof JLabel && "value".equals(cmp.getName())) ((JLabel) cmp).setText(v);
    }

    private JTable buildStyledTable(String[] cols) {
        DefaultTableModel m = new DefaultTableModel(cols, 0) { @Override public boolean isCellEditable(int r, int c) { return false; } };
        JTable t = new JTable(m); t.setBackground(BG_CARD); t.setForeground(TEXT_MAIN); t.setRowHeight(36);
        t.getTableHeader().setBackground(BG_CARD); t.getTableHeader().setForeground(TEXT_MUTED);
        t.setSelectionBackground(new Color(99,102,241,60)); t.setSelectionForeground(TEXT_MAIN);
        return t;
    }

    private JScrollPane wrapTable(JTable t) {
        JScrollPane s = new JScrollPane(t); s.setBackground(BG_CARD); s.getViewport().setBackground(BG_CARD);
        s.setBorder(BorderFactory.createLineBorder(new Color(255,255,255,13),1)); return s;
    }

    @SuppressWarnings("unchecked")
    private void loadTable(JTable t, String ep, java.util.function.Function<java.util.Map<String, Object>, Object[]> m) {
        new SwingWorker<Void, Void>() {
            @Override protected Void doInBackground() {
                try {
                    Object[] data = ApiClient.get(ep, Object[].class);
                    SwingUtilities.invokeLater(() -> {
                        DefaultTableModel dtm = (DefaultTableModel) t.getModel(); dtm.setRowCount(0);
                        if (data != null) for (Object raw : data) dtm.addRow(m.apply((java.util.Map<String, Object>) raw));
                    });
                } catch(Exception e) {}
                return null;
            }
        }.execute();
    }

    private void refreshAlertas() {
        if (alertasContentPanel == null) return;
        new SwingWorker<Void, Void>() {
            @Override protected Void doInBackground() throws Exception {
                try {
                    AlertaStockMinimo[] a = ApiClient.get("/inventario/alertas", AlertaStockMinimo[].class);
                    SwingUtilities.invokeLater(() -> {
                        alertasContentPanel.removeAll();
                        if (a != null) for (AlertaStockMinimo alerta : a) alertasContentPanel.add(buildAlertRow(alerta.getMensaje(), DANGER));
                        alertasContentPanel.revalidate(); alertasContentPanel.repaint();
                    });
                } catch (Exception ex) {}
                return null;
            }
        }.execute();
    }

    private JPanel buildAlertRow(String msg, Color c) {
        JPanel r = new JPanel(new FlowLayout(FlowLayout.LEFT, 12, 8)); r.setBackground(new Color(245, 158, 11, 15));
        r.setMaximumSize(new Dimension(Integer.MAX_VALUE, 48)); JLabel d = new JLabel("●"); d.setForeground(c);
        JLabel t = new JLabel(msg); t.setForeground(TEXT_MAIN); r.add(d); r.add(t); return r;
    }

    private JPanel buildReportCard(String t) {
        JPanel c = new JPanel(new BorderLayout(0, 8)); c.setBackground(BG_CARD);
        c.setBorder(BorderFactory.createCompoundBorder(BorderFactory.createLineBorder(new Color(255,255,255,13),1), new EmptyBorder(16,16,16,16)));
        JLabel lt = new JLabel(t); lt.setForeground(TEXT_MAIN); JButton b = new JButton("Generar PDF");
        b.setBackground(PRIMARY); b.setForeground(Color.WHITE); c.add(lt, BorderLayout.NORTH); c.add(b, BorderLayout.SOUTH);
        return c;
    }

    private JPanel createContentPanel() {
        JPanel p = new JPanel(new BorderLayout(0, 16)); p.setBackground(BG_DARK); p.setBorder(new EmptyBorder(24, 28, 24, 28));
        return p;
    }

    private String str(java.util.Map<String, Object> m, String k) { Object v = m.get(k); return v != null ? v.toString() : "—"; }
    private int num(java.util.Map<String, Object> m, String k) { Object v = m.get(k); return (v instanceof Number) ? ((Number)v).intValue() : 0; }
    private double dbl(java.util.Map<String, Object> m, String k) { Object v = m.get(k); return (v instanceof Number) ? ((Number)v).doubleValue() : 0.0; }
    private Long id(java.util.Map<String, Object> m) { Object v = m.get("id"); return v != null ? Long.valueOf(v.toString()) : 0L; }
    private Color parseColor(String rgb) { String[] p = rgb.split(","); return new Color(Integer.parseInt(p[0].trim()), Integer.parseInt(p[1].trim()), Integer.parseInt(p[2].trim())); }

    private void agregarProveedorDash(JTable table) {
        JTextField txtNom = new JTextField();
        JTextField txtDoc = new JTextField();
        JTextField txtTel = new JTextField();
        JTextField txtDir = new JTextField();
        JTextField txtMail = new JTextField();
        JTextField txtCuenta = new JTextField();
        JComboBox<String> cbTipo = new JComboBox<>(new String[]{"Cédula", "NIT", "Pasaporte"});
        cbTipo.setSelectedIndex(1);
        JCheckBox chkAct = new JCheckBox("Proveedor activo", true);

        Object[] msg = { 
            "Tipo Documento:", cbTipo, 
            "NIT/Documento:", txtDoc, 
            "Nombre Empresa/Persona:", txtNom, 
            "Teléfono:", txtTel, 
            "Dirección:", txtDir, 
            "Email:", txtMail,
            "Cuenta Bancaria:", txtCuenta,
            chkAct
        };
        if (JOptionPane.showConfirmDialog(this, msg, "Nuevo Proveedor", JOptionPane.OK_CANCEL_OPTION) == JOptionPane.OK_OPTION) {
            try {
                com.acacioswork.model.Proveedor p = new com.acacioswork.model.Proveedor();
                p.setIdTipoDocumento((long)(cbTipo.getSelectedIndex()+1)); p.setNumeroDocumento(txtDoc.getText());
                p.setNombre(txtNom.getText()); p.setTelefono(txtTel.getText()); p.setDireccion(txtDir.getText()); 
                p.setEmail(txtMail.getText()); p.setCuentaBancaria(txtCuenta.getText());
                p.setActivo(chkAct.isSelected() ? 1 : 0);
                ApiClient.post("/proveedores", p, com.acacioswork.model.Proveedor.class);
                JOptionPane.showMessageDialog(this, "Proveedor guardado."); refreshProveedores(table);
            } catch (Exception e) { JOptionPane.showMessageDialog(this, "Error: " + e.getMessage()); }
        }
    }

    private void editarProveedorDash(JTable table) {
        int row = table.getSelectedRow();
        if (row == -1) { JOptionPane.showMessageDialog(this, "Seleccione un proveedor."); return; }
        Long id = (Long) table.getValueAt(row, 0);
        try {
            com.acacioswork.model.Proveedor p = ApiClient.get("/proveedores/" + id, com.acacioswork.model.Proveedor.class);
            JTextField txtNom = new JTextField(p.getNombre()); JTextField txtDoc = new JTextField(p.getNumeroDocumento());
            JTextField txtTel = new JTextField(p.getTelefono()); JTextField txtDir = new JTextField(p.getDireccion()); 
            JTextField txtMail = new JTextField(p.getEmail()); JTextField txtCuenta = new JTextField(p.getCuentaBancaria());
            JCheckBox chkAct = new JCheckBox("Proveedor activo", p.getActivo() == 1);

            Object[] msg = { 
                "NIT/Documento:", txtDoc, 
                "Nombre:", txtNom, 
                "Teléfono:", txtTel, 
                "Dirección:", txtDir, 
                "Email:", txtMail,
                "Cuenta Bancaria:", txtCuenta,
                chkAct
            };
            
            String[] options = {"Aceptar", "Cancelar"};
            int selection = JOptionPane.showOptionDialog(this, msg, "Actualizar proveedor", 
                    JOptionPane.OK_CANCEL_OPTION, JOptionPane.PLAIN_MESSAGE, null, options, options[0]);

            if (selection == JOptionPane.OK_OPTION) {
                p.setNombre(txtNom.getText()); p.setNumeroDocumento(txtDoc.getText()); 
                p.setTelefono(txtTel.getText()); p.setDireccion(txtDir.getText()); 
                p.setEmail(txtMail.getText()); p.setCuentaBancaria(txtCuenta.getText());
                p.setActivo(chkAct.isSelected() ? 1 : 0);

                ApiClient.put("/proveedores/" + id, p, com.acacioswork.model.Proveedor.class);
                JOptionPane.showMessageDialog(this, "Proveedor actualizado con éxito.", "Éxito", JOptionPane.INFORMATION_MESSAGE); 
                refreshProveedores(table);
            }
        } catch (Exception e) { JOptionPane.showMessageDialog(this, "Error: " + e.getMessage()); }
    }

    private void agregarClienteDash(JTable table) {
        JTextField txtNom = new JTextField(); 
        JTextField txtDoc = new JTextField(); 
        JTextField txtTel = new JTextField(); 
        JTextField txtMail = new JTextField();
        JCheckBox chkFreq = new JCheckBox("Cliente frecuente", false);
        JCheckBox chkAct = new JCheckBox("Cliente activo", true);

        Object[] msg = { 
            "Identificación/NIT:", txtDoc, 
            "Nombre Completo:", txtNom, 
            "Teléfono:", txtTel, 
            "Email:", txtMail,
            chkFreq,
            chkAct
        };

        if (JOptionPane.showConfirmDialog(this, msg, "Nuevo Cliente", JOptionPane.OK_CANCEL_OPTION) == JOptionPane.OK_OPTION) {
            try {
                com.acacioswork.model.Cliente c = new com.acacioswork.model.Cliente();
                c.setNumeroDocumento(txtDoc.getText()); 
                c.setNombre(txtNom.getText()); 
                c.setTelefono(txtTel.getText()); 
                c.setEmail(txtMail.getText());
                c.setFrecuente(chkFreq.isSelected());
                c.setActivo(chkAct.isSelected() ? 1 : 0);
                c.setIdTipoDocumento(1L); // Default Cedula

                ApiClient.post("/clientes", c, com.acacioswork.model.Cliente.class);
                JOptionPane.showMessageDialog(this, "Cliente registrado."); refreshClientes(table);
            } catch (Exception e) { JOptionPane.showMessageDialog(this, "Error: " + e.getMessage()); }
        }
    }

    private void editarClienteDash(JTable table) {
        int row = table.getSelectedRow();
        if (row == -1) { JOptionPane.showMessageDialog(this, "Seleccione un cliente."); return; }
        Long id = (Long) table.getValueAt(row, 0);
        try {
            com.acacioswork.model.Cliente c = ApiClient.get("/clientes/" + id, com.acacioswork.model.Cliente.class);
            JTextField txtNom = new JTextField(c.getNombre()); 
            JTextField txtDoc = new JTextField(c.getNumeroDocumento()); 
            JTextField txtTel = new JTextField(c.getTelefono()); 
            JTextField txtMail = new JTextField(c.getEmail());
            JCheckBox chkFreq = new JCheckBox("Cliente frecuente", c.isFrecuente());
            JCheckBox chkAct = new JCheckBox("Cliente activo", c.getActivo() != null && c.getActivo() == 1);

            Object[] msg = { 
                "Identificación/NIT:", txtDoc, 
                "Nombre:", txtNom, 
                "Teléfono:", txtTel, 
                "Email:", txtMail,
                chkFreq,
                chkAct
            };

            String[] options = {"Aceptar", "Cancelar"};
            int selection = JOptionPane.showOptionDialog(this, msg, "Actualizar cliente", 
                    JOptionPane.OK_CANCEL_OPTION, JOptionPane.PLAIN_MESSAGE, null, options, options[0]);

            if (selection == JOptionPane.OK_OPTION) {
                c.setNombre(txtNom.getText()); 
                c.setNumeroDocumento(txtDoc.getText()); 
                c.setTelefono(txtTel.getText()); 
                c.setEmail(txtMail.getText());
                c.setFrecuente(chkFreq.isSelected());
                c.setActivo(chkAct.isSelected() ? 1 : 0);

                ApiClient.put("/clientes/" + id, c, com.acacioswork.model.Cliente.class);
                JOptionPane.showMessageDialog(this, "Cliente actualizado con éxito.", "Éxito", JOptionPane.INFORMATION_MESSAGE); 
                refreshClientes(table);
            }
        } catch (Exception e) { JOptionPane.showMessageDialog(this, "Error: " + e.getMessage()); }
    }

    private void refreshAlertas(JTable table) {
        loadTable(table, "/productos", row -> {
            int stock = num(row, "cantidad");
            int min = row.get("stockMinimo") != null ? num(row, "stockMinimo") : 5;
            
            if (stock <= min) {
                return new Object[]{ 
                    id(row), 
                    str(row, "nombre"), 
                    stock, 
                    min, 
                    "Ver detalles...", 
                    "🔍 Ver Proveedor" 
                };
            }
            return null;
        });
    }

    private void mostrarInfoProveedor(Object idProd) {
        try {
            com.acacioswork.model.Producto p = ApiClient.get("/productos/" + idProd, com.acacioswork.model.Producto.class);
            if (p.getIdProveedor() == null) {
                JOptionPane.showMessageDialog(this, "Este producto no tiene un proveedor asignado.");
                return;
            }
            com.acacioswork.model.Proveedor prov = ApiClient.get("/proveedores/" + p.getIdProveedor(), com.acacioswork.model.Proveedor.class);
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
