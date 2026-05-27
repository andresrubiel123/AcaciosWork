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
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.Arrays;
import java.util.Vector;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.table.DefaultTableModel;

import com.acacioswork.model.Categoria;
import com.acacioswork.model.Producto;
import com.acacioswork.model.Proveedor;
import com.acacioswork.util.ApiClient;

/** Interfaz de gestión de inventario con CRUD completo. @author RADJ */
public class GestionInventario extends JPanel {

    private DefaultTableModel modeloTabla;
    private JTable tablaProductos;
    private JLabel statTotal;
    private JLabel statBajo;
    private JLabel statValor;

    public GestionInventario() {
        setLayout(new BorderLayout());
        setBackground(new Color(15, 23, 42));

        // Panel superior: Título y Volver
        JPanel panelSuperior = new JPanel(new FlowLayout(FlowLayout.LEFT));
        panelSuperior.setOpaque(false);
        panelSuperior.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        
        GradientButton btnAtras = new GradientButton("‹ Volver", new Color(99, 102, 241), new Color(79, 70, 229));
        btnAtras.addActionListener(e -> MainFrame.navigateTo(new Administrador()));
        panelSuperior.add(btnAtras);

        JLabel lblTitle = new JLabel("Inventario de Productos");
        lblTitle.setFont(new Font("Inter", Font.BOLD, 22));
        lblTitle.setForeground(Color.WHITE);
        panelSuperior.add(lblTitle);

        // Contenedor superior: título + estadísticas
        JPanel topContainer = new JPanel(new BorderLayout());
        topContainer.setOpaque(false);
        topContainer.add(panelSuperior, BorderLayout.NORTH);

        // Stats panel
        JPanel statsPanel = new JPanel(new GridLayout(1, 3, 12, 0));
        statsPanel.setOpaque(false);
        statsPanel.setBorder(BorderFactory.createEmptyBorder(8, 12, 8, 12));

        JPanel card1 = new JPanel(new BorderLayout());
        card1.setBackground(new Color(30, 41, 59));
        card1.setBorder(BorderFactory.createEmptyBorder(10, 12, 10, 12));
        JLabel lbl1 = new JLabel("Total Productos"); lbl1.setForeground(new Color(148,163,184));
        statTotal = new JLabel("—"); statTotal.setForeground(new Color(248,250,252)); statTotal.setFont(new Font("Dialog", Font.BOLD, 18));
        card1.add(lbl1, BorderLayout.NORTH); card1.add(statTotal, BorderLayout.CENTER);

        JPanel card2 = new JPanel(new BorderLayout());
        card2.setBackground(new Color(30, 41, 59));
        card2.setBorder(BorderFactory.createEmptyBorder(10, 12, 10, 12));
        JLabel lbl2 = new JLabel("Stock Bajo"); lbl2.setForeground(new Color(148,163,184));
        statBajo = new JLabel("—"); statBajo.setForeground(new Color(239,68,68)); statBajo.setFont(new Font("Dialog", Font.BOLD, 18));
        card2.add(lbl2, BorderLayout.NORTH); card2.add(statBajo, BorderLayout.CENTER);

        JPanel card3 = new JPanel(new BorderLayout());
        card3.setBackground(new Color(30, 41, 59));
        card3.setBorder(BorderFactory.createEmptyBorder(10, 12, 10, 12));
        JLabel lbl3 = new JLabel("Valor Inventario"); lbl3.setForeground(new Color(148,163,184));
        statValor = new JLabel("—"); statValor.setForeground(new Color(16,185,129)); statValor.setFont(new Font("Dialog", Font.BOLD, 18));
        card3.add(lbl3, BorderLayout.NORTH); card3.add(statValor, BorderLayout.CENTER);

        statsPanel.add(card1); statsPanel.add(card2); statsPanel.add(card3);
        topContainer.add(statsPanel, BorderLayout.SOUTH);

        add(topContainer, BorderLayout.NORTH);

        // Tabla de productos
        modeloTabla = new DefaultTableModel() {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
        modeloTabla.addColumn("ID");
        modeloTabla.addColumn("Código");
        modeloTabla.addColumn("Nombre");
        modeloTabla.addColumn("Unidad");
        modeloTabla.addColumn("Stock");
        modeloTabla.addColumn("Precio Compra");
        modeloTabla.addColumn("Precio Venta");
        modeloTabla.addColumn("IVA");
        modeloTabla.addColumn("Estado");
        modeloTabla.addColumn("Acciones");

        tablaProductos = new JTable(modeloTabla);
        tablaProductos.setBackground(new Color(30, 41, 59));
        tablaProductos.setForeground(Color.WHITE);
        tablaProductos.setGridColor(new Color(51, 65, 85));
        tablaProductos.setRowHeight(25);
        tablaProductos.getTableHeader().setBackground(new Color(51, 65, 85));
        tablaProductos.getTableHeader().setForeground(Color.WHITE);
        tablaProductos.getTableHeader().setFont(new Font("Inter", Font.BOLD, 12));

        JScrollPane scrollPane = new JScrollPane(tablaProductos);
        scrollPane.setBorder(BorderFactory.createEmptyBorder());
        scrollPane.getViewport().setBackground(new Color(15, 23, 42));
        add(scrollPane, BorderLayout.CENTER);
        // Ajustar anchos de columnas (incluye nueva columna IVA)
        tablaProductos.getColumnModel().getColumn(0).setPreferredWidth(50); // ID
        tablaProductos.getColumnModel().getColumn(1).setPreferredWidth(100); // Código
        tablaProductos.getColumnModel().getColumn(2).setPreferredWidth(300); // Nombre
        tablaProductos.getColumnModel().getColumn(3).setPreferredWidth(80); // Unidad
        tablaProductos.getColumnModel().getColumn(4).setPreferredWidth(100); // Stock
        tablaProductos.getColumnModel().getColumn(5).setPreferredWidth(110); // Precio Compra
        tablaProductos.getColumnModel().getColumn(6).setPreferredWidth(110); // Precio Venta
        tablaProductos.getColumnModel().getColumn(7).setPreferredWidth(80); // IVA
        tablaProductos.getColumnModel().getColumn(8).setPreferredWidth(80); // Estado
        tablaProductos.getColumnModel().getColumn(9).setPreferredWidth(140); // Acciones

        // Renderer para la columna Stock: número coloreado según cantidad. @author RADJ
        tablaProductos.getColumnModel().getColumn(4).setCellRenderer(new javax.swing.table.DefaultTableCellRenderer() {
            @Override
            public java.awt.Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected,
                    boolean hasFocus, int row, int column) {
                JLabel label = (JLabel) super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);
                label.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
                label.setFont(new Font("Inter", Font.BOLD, 12));
                try {
                    int qty = 0;
                    if (value instanceof Number) {
                        qty = ((Number) value).intValue();
                    } else if (value != null) {
                        qty = Integer.parseInt(value.toString().replaceAll("[^0-9]", ""));
                    }
                    int stockOptimo = 200;
                    int pct = Math.min(100, (int) Math.round((qty * 100.0) / stockOptimo));

                    if (isSelected) {
                        // Mantiene colores de selección de la JTable
                    } else {
                        if (pct <= 30) {
                            label.setForeground(new Color(248, 113, 113));
                        } else if (pct <= 69) {
                            label.setForeground(new Color(251, 146, 60));
                        } else {
                            label.setForeground(new Color(52, 211, 153));
                        }
                    }
                } catch (Exception e) {
                    label.setForeground(isSelected ? table.getSelectionForeground() : table.getForeground());
                }
                return label;
            }
        });

        // Columna Acciones: renderer simple y handler de clics
        tablaProductos.getColumnModel().getColumn(9).setCellRenderer(new javax.swing.table.DefaultTableCellRenderer() {
            @Override
            public java.awt.Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected,
                    boolean hasFocus, int row, int column) {
                JPanel p = new JPanel(new FlowLayout(FlowLayout.CENTER, 6, 4));
                p.setOpaque(!isSelected);
                p.setBackground(isSelected ? table.getSelectionBackground() : table.getBackground());
                JLabel edit = new JLabel("Editar");
                edit.setForeground(new Color(99, 102, 241));
                edit.setFont(new Font("Dialog", Font.BOLD, 12));
                JLabel del = new JLabel("Borrar");
                del.setForeground(new Color(239, 68, 68));
                del.setFont(new Font("Dialog", Font.BOLD, 12));
                p.add(edit);
                p.add(del);
                return p;
            }
        });

        // Mouse listener para detectar clicks en 'Acciones'
        tablaProductos.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                int row = tablaProductos.rowAtPoint(e.getPoint());
                int col = tablaProductos.columnAtPoint(e.getPoint());
                if (row == -1 || col == -1) return;
                if (col == 9) { // Acciones
                    int cellX = e.getX() - tablaProductos.getCellRect(row, col, true).x;
                    int width = tablaProductos.getColumnModel().getColumn(col).getWidth();
                    if (cellX < width / 2) {
                        // Edit
                        editarProducto();
                    } else {
                        // Delete
                        eliminarProducto();
                    }
                }
            }
        });

        // Panel inferior: Botones de Acción
        JPanel panelBotones = new JPanel(new FlowLayout(FlowLayout.CENTER, 20, 10));
        panelBotones.setOpaque(false);
        panelBotones.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        GradientButton btnAgregar = new GradientButton("Agregar", new Color(16, 185, 129), new Color(5, 150, 105));
        GradientButton btnEditar = new GradientButton("Editar", new Color(99, 102, 241), new Color(79, 70, 229));
        GradientButton btnEliminar = new GradientButton("Eliminar", new Color(239, 68, 68), new Color(185, 28, 28));

        btnAgregar.addActionListener(e -> agregarProducto());
        btnEditar.addActionListener(e -> editarProducto());
        btnEliminar.addActionListener(e -> eliminarProducto());

        panelBotones.add(btnAgregar);
        panelBotones.add(btnEditar);
        panelBotones.add(btnEliminar);
        add(panelBotones, BorderLayout.SOUTH);

        cargarProductos();
    }

    private void cargarProductos() {
        modeloTabla.setRowCount(0);
        try {
            Producto[] productos = ApiClient.get("/productos", Producto[].class);
            for (Producto p : productos) {
                modeloTabla.addRow(new Object[] {
                        p.getId(), 
                        p.getCodigoBarras(), 
                        p.getNombre(), 
                        p.getUnidadMedida() != null ? p.getUnidadMedida() : "Unidad",
                        p.getStockActual(), 
                        "$" + p.getPrecioCompra(),
                        "$" + p.getPrecioVenta(),
                        p.getIva() + "%",
                        p.getEstado() != null && p.getEstado() == 1 ? "Activo" : "Inactivo"
                });
            }
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, "Error al cargar inventario: " + e.getMessage());
        }
    }

    public void agregarProducto() {
        JTextField txtCodigo = new JTextField();
        JTextField txtNombre = new JTextField();
        JTextField txtCant = new JTextField();
        JTextField txtPCompra = new JTextField();
        JTextField txtPVenta = new JTextField();
        
        Vector<Categoria> categorias = new Vector<>();
        Vector<Proveedor> proveedores = new Vector<>();
        
        try {
            categorias.addAll(Arrays.asList(ApiClient.get("/categorias", Categoria[].class)));
            proveedores.addAll(Arrays.asList(ApiClient.get("/proveedores", Proveedor[].class)));
        } catch (Exception e) {
            System.err.println("Error cargando combos: " + e.getMessage());
        }

        JComboBox<Categoria> cbCat = new JComboBox<>(categorias);
        JComboBox<Proveedor> cbProv = new JComboBox<>(proveedores);
        JTextField txtUnidadMedida = new JTextField("");
        JTextField txtIva = new JTextField("19"); // IVA default 19%

        Object[] message = {
                "Código de Barras:", txtCodigo,
                "Nombre:", txtNombre,
                "Unidad de Medida:", txtUnidadMedida,
                "IVA (%):", txtIva,
                "Stock Actual:", txtCant,
                "Precio Compra:", txtPCompra,
                "Precio Venta:", txtPVenta,
                "Categoría:", cbCat,
                "Proveedor:", cbProv
        };

        int option = JOptionPane.showConfirmDialog(this, message, "Registrar Nuevo Producto", JOptionPane.OK_CANCEL_OPTION);
        if (option == JOptionPane.OK_OPTION) {
            try {
                Producto p = new Producto();
                p.setCodigoBarras(txtCodigo.getText());
                p.setNombre(txtNombre.getText());
                p.setStockActual(Integer.parseInt(txtCant.getText()));
                p.setPrecioCompra(Double.parseDouble(txtPCompra.getText()));
                p.setPrecioVenta(Double.parseDouble(txtPVenta.getText()));
                p.setIva(Double.parseDouble(txtIva.getText()));
                p.setEstado(1);
                p.setStockMinimo(5);
                p.setUnidadMedida(txtUnidadMedida.getText().trim().isEmpty() ? "Unidad" : txtUnidadMedida.getText().trim());

                Categoria c = (Categoria) cbCat.getSelectedItem();
                if (c != null) p.setIdCategoria(c.getId());
                
                Proveedor pr = (Proveedor) cbProv.getSelectedItem();
                if (pr != null) p.setIdProveedor(pr.getId());

                ApiClient.post("/productos", p, Producto.class);
                cargarProductos();
                JOptionPane.showMessageDialog(this, "Producto agregado al inventario.");
            } catch (Exception e) {
                JOptionPane.showMessageDialog(this, "Error al guardar: " + e.getMessage());
            }
        }
    }

    private void editarProducto() {
        int fila = tablaProductos.getSelectedRow();
        if (fila == -1) {
            JOptionPane.showMessageDialog(this, "Seleccione un producto de la tabla.");
            return;
        }
        Long id = (Long) modeloTabla.getValueAt(fila, 0);

        try {
            Producto p = ApiClient.get("/productos/" + id, Producto.class);
            
            JTextField txtNombre = new JTextField(p.getNombre());
            JTextField txtCant = new JTextField(String.valueOf(p.getStockActual()));
            JTextField txtPVenta = new JTextField(String.valueOf(p.getPrecioVenta()));
            JComboBox<String> cbEstado = new JComboBox<>(new String[]{"Activo", "Inactivo"});
            cbEstado.setSelectedItem(p.getEstado() != null && p.getEstado() == 1 ? "Activo" : "Inactivo");
            JTextField txtUnidadMedida = new JTextField(p.getUnidadMedida() != null ? p.getUnidadMedida() : "");
            JTextField txtIva = new JTextField(String.valueOf(p.getIva()));

            Object[] message = {
                    "Nombre:", txtNombre,
                    "Unidad de Medida:", txtUnidadMedida,
                    "IVA (%):", txtIva,
                    "Stock:", txtCant,
                    "Precio Venta:", txtPVenta,
                    "Estado:", cbEstado
            };

            int option = JOptionPane.showConfirmDialog(this, message, "Editar Producto", JOptionPane.OK_CANCEL_OPTION);
            if (option == JOptionPane.OK_OPTION) {
                p.setNombre(txtNombre.getText());
                p.setStockActual(Integer.parseInt(txtCant.getText()));
                p.setPrecioVenta(Double.parseDouble(txtPVenta.getText()));
                p.setEstado(cbEstado.getSelectedItem().equals("Activo") ? 1 : 0);
                p.setIva(Double.parseDouble(txtIva.getText()));
                p.setUnidadMedida(txtUnidadMedida.getText().trim().isEmpty() ? "Unidad" : txtUnidadMedida.getText().trim());

                ApiClient.put("/productos/" + id, p, Producto.class);
                cargarProductos();
                JOptionPane.showMessageDialog(this, "Producto actualizado.");
            }
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, "Error al editar: " + e.getMessage());
        }
    }

    private void eliminarProducto() {
        int fila = tablaProductos.getSelectedRow();
        if (fila == -1) {
            JOptionPane.showMessageDialog(this, "Seleccione un producto para eliminar.");
            return;
        }
        Long id = (Long) modeloTabla.getValueAt(fila, 0);
        String nombre = (String) modeloTabla.getValueAt(fila, 2);

        int confirm = JOptionPane.showConfirmDialog(this, "¿Está seguro de eliminar el producto: " + nombre + "?", 
                "Confirmar Eliminación", JOptionPane.YES_NO_OPTION);
        
        if (confirm == JOptionPane.YES_OPTION) {
            try {
                ApiClient.delete("/productos/" + id);
                cargarProductos();
                JOptionPane.showMessageDialog(this, "Producto eliminado con éxito.");
            } catch (Exception e) {
                JOptionPane.showMessageDialog(this, "Error al eliminar: " + e.getMessage());
            }
        }
    }

    private static class GradientButton extends JButton {
        private final Color startColor;
        private final Color endColor;

        public GradientButton(String text, Color startColor, Color endColor) {
            super(text);
            this.startColor = startColor;
            this.endColor = endColor;
            setContentAreaFilled(false);
            setForeground(Color.WHITE);
            setBorder(BorderFactory.createEmptyBorder(10, 20, 10, 20));
            setFocusPainted(false);
            setOpaque(false);
            setFont(new Font("Inter", Font.BOLD, 14));
            setCursor(new Cursor(Cursor.HAND_CURSOR));
        }

        @Override
        protected void paintComponent(Graphics g) {
            Graphics2D g2 = (Graphics2D) g.create();
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            GradientPaint paint = new GradientPaint(0, 0, startColor, 0, getHeight(), endColor);
            g2.setPaint(paint);
            g2.fillRoundRect(0, 0, getWidth(), getHeight(), 12, 12);
            g2.dispose();
            super.paintComponent(g);
        }
    }
}
