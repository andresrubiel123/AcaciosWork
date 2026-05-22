package com.acacioswork.interfaz_usuario;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Cursor;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.GradientPaint;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
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

        add(panelSuperior, BorderLayout.NORTH);

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
        modeloTabla.addColumn("Stock");
        modeloTabla.addColumn("Precio Compra");
        modeloTabla.addColumn("Precio Venta");
        modeloTabla.addColumn("Estado");

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
                        p.getCantidad(), 
                        "$" + p.getPrecioCompra(),
                        "$" + p.getPrecioVenta(),
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

        Object[] message = {
                "Código de Barras:", txtCodigo,
                "Nombre:", txtNombre,
                "Stock Inicial:", txtCant,
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
                p.setCantidad(Integer.parseInt(txtCant.getText()));
                p.setPrecioCompra(Double.parseDouble(txtPCompra.getText()));
                p.setPrecioVenta(Double.parseDouble(txtPVenta.getText()));
                p.setIva(19.0);
                p.setEstado(1);
                p.setStockMinimo(5);

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
            JTextField txtCant = new JTextField(String.valueOf(p.getCantidad()));
            JTextField txtPVenta = new JTextField(String.valueOf(p.getPrecioVenta()));
            JComboBox<String> cbEstado = new JComboBox<>(new String[]{"Activo", "Inactivo"});
            cbEstado.setSelectedItem(p.getEstado() != null && p.getEstado() == 1 ? "Activo" : "Inactivo");

            Object[] message = {
                    "Nombre:", txtNombre,
                    "Stock:", txtCant,
                    "Precio Venta:", txtPVenta,
                    "Estado:", cbEstado
            };

            int option = JOptionPane.showConfirmDialog(this, message, "Editar Producto", JOptionPane.OK_CANCEL_OPTION);
            if (option == JOptionPane.OK_OPTION) {
                p.setNombre(txtNombre.getText());
                p.setCantidad(Integer.parseInt(txtCant.getText()));
                p.setPrecioVenta(Double.parseDouble(txtPVenta.getText()));
                p.setEstado(cbEstado.getSelectedItem().equals("Activo") ? 1 : 0);

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
