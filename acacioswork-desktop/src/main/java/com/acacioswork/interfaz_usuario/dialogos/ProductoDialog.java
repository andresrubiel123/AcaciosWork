package com.acacioswork.interfaz_usuario.dialogos;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Cursor;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Frame;
import java.awt.GridLayout;

import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JComponent;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.border.EmptyBorder;

import com.acacioswork.interfaz_usuario.Administrador;
import com.acacioswork.model.Categoria;
import com.acacioswork.model.Producto;
import com.acacioswork.model.Proveedor;
import com.acacioswork.util.ApiClient;

public class ProductoDialog extends JDialog {
    private final Frame owner;
    private final Producto producto; // null means create, non-null means edit
    private final Runnable onSuccess;

    private JTextField txtCodigo;
    private JTextField txtNombre;
    private JTextField txtCant;
    private JTextField txtMin;
    private JTextField txtOptimo;
    private JTextField txtPCompra;
    private JTextField txtPVenta;
    private JTextField txtIva;
    private JComboBox<Categoria> cbCat;
    private JComboBox<Proveedor> cbProv;
    private JComboBox<String> cbEstado;
    private JTextField txtUnidadMedida;

    public ProductoDialog(Frame owner, Producto producto, Runnable onSuccess) {
        super(owner, producto == null ? "Registrar Nuevo Producto" : "Editar Producto", true);
        this.owner = owner;
        this.producto = producto;
        this.onSuccess = onSuccess;
        initUI();
    }

    private void initUI() {
        setResizable(false);

        // Fields
        txtCodigo = new JTextField();
        txtCodigo.putClientProperty("JTextField.placeholderText", "Ej: 7701234");
        txtCodigo.putClientProperty("JComponent.roundRect", true);

        txtNombre = new JTextField();
        txtNombre.putClientProperty("JTextField.placeholderText", "Nombre del producto");
        txtNombre.putClientProperty("JComponent.roundRect", true);

        txtCant = new JTextField("0");
        txtCant.putClientProperty("JTextField.placeholderText", "0");
        txtCant.putClientProperty("JComponent.roundRect", true);

        txtMin = new JTextField("5");
        txtMin.putClientProperty("JTextField.placeholderText", "5");
        txtMin.putClientProperty("JComponent.roundRect", true);

        txtOptimo = new JTextField("200");
        txtOptimo.putClientProperty("JTextField.placeholderText", "200");
        txtOptimo.putClientProperty("JComponent.roundRect", true);

        txtPCompra = new JTextField("0");
        txtPCompra.putClientProperty("JTextField.placeholderText", "0");
        txtPCompra.putClientProperty("JComponent.roundRect", true);

        txtPVenta = new JTextField("0");
        txtPVenta.putClientProperty("JTextField.placeholderText", "0");
        txtPVenta.putClientProperty("JComponent.roundRect", true);

        txtIva = new JTextField("19");
        txtIva.putClientProperty("JTextField.placeholderText", "19");
        txtIva.putClientProperty("JComponent.roundRect", true);

        java.util.Vector<Categoria> categorias = new java.util.Vector<>();
        categorias.add(new Categoria(-1L, "Seleccione una categoría"));

        java.util.Vector<Proveedor> proveedores = new java.util.Vector<>();
        Proveedor placeholderProv = new Proveedor();
        placeholderProv.setId(-1L);
        placeholderProv.setNombre("Seleccione un proveedor");
        proveedores.add(placeholderProv);

        try {
            categorias.addAll(java.util.Arrays.asList(ApiClient.get("/categorias", Categoria[].class)));
            proveedores.addAll(java.util.Arrays.asList(ApiClient.get("/proveedores", Proveedor[].class)));
        } catch (Exception e) {
            e.printStackTrace();
        }

        cbCat = new JComboBox<>(categorias);
        cbCat.putClientProperty("JComponent.roundRect", true);

        cbProv = new JComboBox<>(proveedores);
        cbProv.putClientProperty("JComponent.roundRect", true);

        cbEstado = new JComboBox<>(new String[] { "Activo", "Inactivo" });
        cbEstado.putClientProperty("JComponent.roundRect", true);

        txtUnidadMedida = new JTextField("");
        txtUnidadMedida.putClientProperty("JTextField.placeholderText", "Ej: Kilo, Litro, Unidad");
        txtUnidadMedida.putClientProperty("JComponent.roundRect", true);

        // Populate fields if editing
        if (producto != null) {
            txtCodigo.setText(producto.getCodigoBarras());
            txtNombre.setText(producto.getNombre());
            txtCant.setText(String.valueOf(producto.getStockActual()));
            txtMin.setText(producto.getStockMinimo() != null ? String.valueOf(producto.getStockMinimo()) : "5");
            txtOptimo.setText(producto.getStockOptimo() != null ? String.valueOf(producto.getStockOptimo()) : "200");
            txtUnidadMedida.setText(producto.getUnidadMedida() != null ? producto.getUnidadMedida() : "");
            txtPCompra.setText(String.valueOf(producto.getPrecioCompra()));
            txtPVenta.setText(String.valueOf(producto.getPrecioVenta()));
            txtIva.setText(String.valueOf(producto.getIva()));

            if (producto.getIdCategoria() != null) {
                for (Categoria cat : categorias) {
                    if (cat.getId().equals(producto.getIdCategoria())) {
                        cbCat.setSelectedItem(cat);
                        break;
                    }
                }
            }

            if (producto.getIdProveedor() != null) {
                for (Proveedor prov : proveedores) {
                    if (prov.getId().equals(producto.getIdProveedor())) {
                        cbProv.setSelectedItem(prov);
                        break;
                    }
                }
            }

            cbEstado.setSelectedItem(producto.getEstado() != null && producto.getEstado() == 1 ? "Activo" : "Inactivo");
        }

        JPanel mainPanel = new JPanel();
        mainPanel.setLayout(new BoxLayout(mainPanel, BoxLayout.Y_AXIS));
        mainPanel.setBackground(Administrador.BG_DARK);
        mainPanel.setBorder(new EmptyBorder(24, 28, 24, 28));

        JLabel lblTitle = new JLabel(producto == null ? "Nuevo Producto" : "Editar Producto");
        lblTitle.setForeground(Administrador.TEXT_MAIN);
        lblTitle.setFont(new Font("Segoe UI", Font.BOLD, 26));
        lblTitle.setAlignmentX(Component.LEFT_ALIGNMENT);
        mainPanel.add(lblTitle);
        mainPanel.add(Box.createVerticalStrut(20));

        java.util.function.BiConsumer<String, JComponent> addField = (label, comp) -> {
            JPanel fPanel = createFieldPanel(label, comp);
            fPanel.setAlignmentX(Component.LEFT_ALIGNMENT);
            mainPanel.add(fPanel);
            mainPanel.add(Box.createVerticalStrut(14));
        };

        addField.accept("Nombre del Producto", txtNombre);
        addField.accept("Unidad de Medida", txtUnidadMedida);

        JPanel codeIvaRow = new JPanel(new GridLayout(1, 2, 16, 0));
        codeIvaRow.setBackground(Administrador.BG_DARK);
        codeIvaRow.setMaximumSize(new Dimension(Integer.MAX_VALUE, 58));
        codeIvaRow.setAlignmentX(Component.LEFT_ALIGNMENT);
        codeIvaRow.add(createFieldPanel("Código de Barras", txtCodigo));
        codeIvaRow.add(createFieldPanel("IVA (%)", txtIva));
        mainPanel.add(codeIvaRow);
        mainPanel.add(Box.createVerticalStrut(14));

        JPanel stockRow = new JPanel(new GridLayout(1, 3, 16, 0));
        stockRow.setBackground(Administrador.BG_DARK);
        stockRow.setMaximumSize(new Dimension(Integer.MAX_VALUE, 58));
        stockRow.setAlignmentX(Component.LEFT_ALIGNMENT);
        stockRow.add(createFieldPanel("Stock / Actual", txtCant));
        stockRow.add(createFieldPanel("Stock Mínimo", txtMin));
        stockRow.add(createFieldPanel("Stock Óptimo", txtOptimo));
        mainPanel.add(stockRow);
        mainPanel.add(Box.createVerticalStrut(14));

        JPanel priceRow = new JPanel(new GridLayout(1, 2, 16, 0));
        priceRow.setBackground(Administrador.BG_DARK);
        priceRow.setMaximumSize(new Dimension(Integer.MAX_VALUE, 58));
        priceRow.setAlignmentX(Component.LEFT_ALIGNMENT);
        priceRow.add(createFieldPanel("Precio Compra", txtPCompra));
        priceRow.add(createFieldPanel("Precio Venta", txtPVenta));
        mainPanel.add(priceRow);
        mainPanel.add(Box.createVerticalStrut(14));

        JPanel catProvRow = new JPanel(new GridLayout(1, 2, 16, 0));
        catProvRow.setBackground(Administrador.BG_DARK);
        catProvRow.setMaximumSize(new Dimension(Integer.MAX_VALUE, 58));
        catProvRow.setAlignmentX(Component.LEFT_ALIGNMENT);
        catProvRow.add(createFieldPanel("Categoría", cbCat));
        catProvRow.add(createFieldPanel("Proveedor", cbProv));
        mainPanel.add(catProvRow);
        mainPanel.add(Box.createVerticalStrut(14));

        addField.accept("Estado", cbEstado);

        mainPanel.add(Box.createVerticalStrut(10));

        JButton btnCancel = new JButton("Cancelar");
        btnCancel.setBackground(new Color(46, 53, 79));
        btnCancel.setForeground(Color.WHITE);
        btnCancel.setFont(new Font("Segoe UI", Font.BOLD, 14));
        btnCancel.setFocusPainted(false);
        btnCancel.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        btnCancel.putClientProperty("JButton.buttonType", "roundRect");

        JButton btnSave = new JButton("Guardar") {
            @Override
            protected void paintComponent(java.awt.Graphics g) {
                java.awt.Graphics2D g2 = (java.awt.Graphics2D) g.create();
                g2.setRenderingHint(java.awt.RenderingHints.KEY_ANTIALIASING, java.awt.RenderingHints.VALUE_ANTIALIAS_ON);
                // Degradado naranja a rojo del dashboard activo
                g2.setPaint(new java.awt.GradientPaint(0, 0, new Color(249, 115, 22), 0, getHeight(), new Color(239, 68, 68)));
                g2.fillRoundRect(0, 0, getWidth(), getHeight(), 10, 10);
                g2.dispose();
                super.paintComponent(g);
            }
        };
        btnSave.setForeground(Color.WHITE);
        btnSave.setFont(new Font("Segoe UI", Font.BOLD, 14));
        btnSave.setFocusPainted(false);
        btnSave.setContentAreaFilled(false);
        btnSave.setOpaque(false);
        btnSave.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        btnSave.putClientProperty("JButton.buttonType", "roundRect");

        JPanel btnPanel = new JPanel(new GridLayout(1, 2, 16, 0));
        btnPanel.setBackground(Administrador.BG_DARK);
        btnPanel.setMaximumSize(new Dimension(Integer.MAX_VALUE, 44));
        btnPanel.setAlignmentX(Component.LEFT_ALIGNMENT);
        btnPanel.add(btnCancel);
        btnPanel.add(btnSave);
        mainPanel.add(btnPanel);

        btnCancel.addActionListener(e -> dispose());
        btnSave.addActionListener(e -> {
            try {
                if (txtNombre.getText().trim().isEmpty()) {
                    JOptionPane.showMessageDialog(this, "El nombre del producto es obligatorio.", "Error",
                            JOptionPane.ERROR_MESSAGE);
                    return;
                }

                Producto p = producto != null ? producto : new Producto();
                p.setCodigoBarras(txtCodigo.getText());
                p.setNombre(txtNombre.getText());
                p.setStockActual(Integer.parseInt(txtCant.getText().trim()));
                p.setPrecioCompra(Double.parseDouble(txtPCompra.getText().trim()));
                p.setPrecioVenta(Double.parseDouble(txtPVenta.getText().trim()));

                double ivaVal = 19.0;
                try {
                    ivaVal = Double.parseDouble(txtIva.getText().trim());
                } catch (Exception ex) {
                }
                p.setIva(ivaVal);

                p.setEstado("Activo".equals(cbEstado.getSelectedItem()) ? 1 : 0);

                int minVal = 5;
                try {
                    minVal = Integer.parseInt(txtMin.getText().trim());
                } catch (Exception ex) {
                }
                p.setStockMinimo(minVal);

                int optimoVal = 200;
                try {
                    optimoVal = Integer.parseInt(txtOptimo.getText().trim());
                } catch (Exception ex) {
                }
                p.setStockOptimo(optimoVal);
                p.setUnidadMedida(txtUnidadMedida.getText().trim().isEmpty() ? "Unidad" : txtUnidadMedida.getText().trim());

                Categoria c = (Categoria) cbCat.getSelectedItem();
                if (c != null && c.getId() != -1L) {
                    p.setIdCategoria(c.getId());
                } else {
                    p.setIdCategoria(null);
                }

                Proveedor pr = (Proveedor) cbProv.getSelectedItem();
                if (pr != null && pr.getId() != -1L) {
                    p.setIdProveedor(pr.getId());
                } else {
                    p.setIdProveedor(null);
                }

                if (producto == null) {
                    ApiClient.post("/productos", p, Producto.class);
                    JOptionPane.showMessageDialog(owner, "Producto guardado con éxito.", "Éxito",
                            JOptionPane.INFORMATION_MESSAGE);
                } else {
                    ApiClient.put("/productos/" + p.getId(), p, Producto.class);
                    JOptionPane.showMessageDialog(owner, "Producto actualizado con éxito.", "Éxito",
                            JOptionPane.INFORMATION_MESSAGE);
                }
                dispose();
                if (onSuccess != null) {
                    onSuccess.run();
                }
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(this, "Error al guardar el producto: " + ex.getMessage(), "Error",
                        JOptionPane.ERROR_MESSAGE);
            }
        });

        setContentPane(mainPanel);
        pack();
        setSize(460, 680);
        setLocationRelativeTo(owner);
    }

    private JPanel createFieldPanel(String labelText, JComponent component) {
        JPanel panel = new JPanel(new BorderLayout(0, 4));
        panel.setBackground(Administrador.BG_DARK);
        JLabel label = new JLabel(labelText);
        label.setForeground(Administrador.TEXT_MUTED);
        label.setFont(new Font("Segoe UI", Font.BOLD, 12));
        panel.add(label, BorderLayout.NORTH);
        component.setPreferredSize(new Dimension(component.getPreferredSize().width, 36));
        panel.add(component, BorderLayout.CENTER);
        return panel;
    }
}
