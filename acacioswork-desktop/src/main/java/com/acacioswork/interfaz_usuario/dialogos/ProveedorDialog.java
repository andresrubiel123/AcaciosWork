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
import com.acacioswork.model.Proveedor;
import com.acacioswork.model.TipoDocumento;
import com.acacioswork.util.ApiClient;

public class ProveedorDialog extends JDialog {
    private final Frame owner;
    private final Proveedor proveedor; // null means create, non-null means edit
    private final Runnable onSuccess;

    private JTextField txtNom;
    private JComboBox<TipoDocumento> cbTipo;
    private JTextField txtDoc;
    private JTextField txtTel;
    private JTextField txtMail;
    private JTextField txtDir;
    private JTextField txtCuenta;
    private JComboBox<String> cbEstado;

    public ProveedorDialog(Frame owner, Proveedor proveedor, Runnable onSuccess) {
        super(owner, proveedor == null ? "Registrar Nuevo Proveedor" : "Editar Proveedor", true);
        this.owner = owner;
        this.proveedor = proveedor;
        this.onSuccess = onSuccess;
        initUI();
    }

    private void initUI() {
        setResizable(false);

        txtNom = new JTextField();
        txtNom.putClientProperty("JTextField.placeholderText", "Nombre de la empresa");
        txtNom.putClientProperty("JComponent.roundRect", true);

        java.util.Vector<TipoDocumento> tipos = new java.util.Vector<>();
        try {
            TipoDocumento[] arr = ApiClient.get("/tipos-documentos", TipoDocumento[].class);
            if (arr != null) {
                tipos.addAll(java.util.Arrays.asList(arr));
            }
        } catch (Exception e) {
            e.printStackTrace();
            tipos.add(new TipoDocumento(1L, "Cedula de Cuidadania", 1));
            tipos.add(new TipoDocumento(2L, "Cedula de Extangeria", 1));
            tipos.add(new TipoDocumento(3L, "Nit", 1));
        }

        cbTipo = new JComboBox<>(tipos);
        cbTipo.putClientProperty("JComponent.roundRect", true);
        
        // Seleccionar por defecto NIT (id = 3)
        for (int i = 0; i < cbTipo.getItemCount(); i++) {
            TipoDocumento td = cbTipo.getItemAt(i);
            if (td.getId().equals(3L) || td.getNombre().toLowerCase().contains("nit")) {
                cbTipo.setSelectedIndex(i);
                break;
            }
        }

        txtDoc = new JTextField();
        txtDoc.putClientProperty("JTextField.placeholderText", "NIT / Cédula");
        txtDoc.putClientProperty("JComponent.roundRect", true);

        txtTel = new JTextField();
        txtTel.putClientProperty("JTextField.placeholderText", "Teléfono de contacto");
        txtTel.putClientProperty("JComponent.roundRect", true);

        txtMail = new JTextField();
        txtMail.putClientProperty("JTextField.placeholderText", "correo@empresa.com");
        txtMail.putClientProperty("JComponent.roundRect", true);

        txtDir = new JTextField();
        txtDir.putClientProperty("JTextField.placeholderText", "Dirección física");
        txtDir.putClientProperty("JComponent.roundRect", true);

        txtCuenta = new JTextField();
        txtCuenta.putClientProperty("JTextField.placeholderText", "Ej: Ahorros Bancolombia No. 123...");
        txtCuenta.putClientProperty("JComponent.roundRect", true);

        cbEstado = new JComboBox<>(new String[] { "Activo", "Inactivo" });
        cbEstado.putClientProperty("JComponent.roundRect", true);

        if (proveedor != null) {
            txtNom.setText(proveedor.getNombre());
            txtDoc.setText(proveedor.getNumeroDocumento());
            txtTel.setText(proveedor.getTelefono());
            txtMail.setText(proveedor.getEmail());
            txtDir.setText(proveedor.getDireccion());
            txtCuenta.setText(proveedor.getCuentaBancaria());
            cbEstado.setSelectedItem(proveedor.getActivo() == 1 ? "Activo" : "Inactivo");

            if (proveedor.getIdTipoDocumento() != null) {
                for (int i = 0; i < cbTipo.getItemCount(); i++) {
                    TipoDocumento td = cbTipo.getItemAt(i);
                    if (td.getId().equals(proveedor.getIdTipoDocumento())) {
                        cbTipo.setSelectedIndex(i);
                        break;
                    }
                }
            }
        }

        JPanel mainPanel = new JPanel();
        mainPanel.setLayout(new BoxLayout(mainPanel, BoxLayout.Y_AXIS));
        mainPanel.setBackground(Administrador.BG_DARK);
        mainPanel.setBorder(new EmptyBorder(24, 28, 24, 28));

        JLabel lblTitle = new JLabel(proveedor == null ? "Nuevo Proveedor" : "Editar Proveedor");
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

        addField.accept("Nombre / Razón Social", txtNom);

        JPanel docRow = new JPanel(new GridLayout(1, 2, 16, 0));
        docRow.setBackground(Administrador.BG_DARK);
        docRow.setMaximumSize(new Dimension(Integer.MAX_VALUE, 58));
        docRow.setAlignmentX(Component.LEFT_ALIGNMENT);
        docRow.add(createFieldPanel("Tipo Doc.", cbTipo));
        docRow.add(createFieldPanel("Número de Documento", txtDoc));
        mainPanel.add(docRow);
        mainPanel.add(Box.createVerticalStrut(14));

        JPanel contactRow = new JPanel(new GridLayout(1, 2, 16, 0));
        contactRow.setBackground(Administrador.BG_DARK);
        contactRow.setMaximumSize(new Dimension(Integer.MAX_VALUE, 58));
        contactRow.setAlignmentX(Component.LEFT_ALIGNMENT);
        contactRow.add(createFieldPanel("Teléfono", txtTel));
        contactRow.add(createFieldPanel("Email", txtMail));
        mainPanel.add(contactRow);
        mainPanel.add(Box.createVerticalStrut(14));

        addField.accept("Dirección (Ciudad/Dirección)", txtDir);

        JPanel bankRow = new JPanel(new GridLayout(1, 2, 16, 0));
        bankRow.setBackground(Administrador.BG_DARK);
        bankRow.setMaximumSize(new Dimension(Integer.MAX_VALUE, 58));
        bankRow.setAlignmentX(Component.LEFT_ALIGNMENT);
        bankRow.add(createFieldPanel("Cuenta Bancaria", txtCuenta));
        bankRow.add(createFieldPanel("Estado", cbEstado));
        mainPanel.add(bankRow);
        mainPanel.add(Box.createVerticalStrut(14));

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
                if (txtNom.getText().trim().isEmpty()) {
                    JOptionPane.showMessageDialog(this, "El nombre/razón social es obligatorio.", "Error", JOptionPane.ERROR_MESSAGE);
                    return;
                }

                Proveedor p = proveedor != null ? proveedor : new Proveedor();
                TipoDocumento tdSel = (TipoDocumento) cbTipo.getSelectedItem();
                p.setIdTipoDocumento(tdSel != null ? tdSel.getId() : 1L);
                p.setNumeroDocumento(txtDoc.getText());
                p.setNombre(txtNom.getText());
                p.setTelefono(txtTel.getText());
                p.setDireccion(txtDir.getText());
                p.setEmail(txtMail.getText());
                p.setCuentaBancaria(txtCuenta.getText());
                p.setActivo("Activo".equals(cbEstado.getSelectedItem()) ? 1 : 0);

                if (proveedor == null) {
                    ApiClient.post("/proveedores", p, Proveedor.class);
                    JOptionPane.showMessageDialog(owner, "Proveedor guardado con éxito.", "Éxito", JOptionPane.INFORMATION_MESSAGE);
                } else {
                    ApiClient.put("/proveedores/" + p.getId(), p, Proveedor.class);
                    JOptionPane.showMessageDialog(owner, "Proveedor actualizado con éxito.", "Éxito", JOptionPane.INFORMATION_MESSAGE);
                }
                dispose();
                if (onSuccess != null) {
                    onSuccess.run();
                }
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(this, "Error al guardar el proveedor: " + ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
            }
        });

        setContentPane(mainPanel);
        pack();
        setSize(460, 490);
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
