package com.acacioswork.interfaz_usuario.dialogos;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import com.acacioswork.interfaz_usuario.Administrador;
import com.acacioswork.model.Cliente;
import com.acacioswork.model.TipoDocumento;
import com.acacioswork.util.ApiClient;

public class ClienteDialog extends JDialog {
    private final Frame owner;
    private final Cliente cliente; // null means create, non-null means edit
    private final Runnable onSuccess;

    private JTextField txtNom;
    private JComboBox<TipoDocumento> cbTipo;
    private JTextField txtDoc;
    private JTextField txtTel;
    private JTextField txtMail;
    private JTextField txtDir;
    private JComboBox<String> cbFreq;
    private JComboBox<String> cbEstado;

    public ClienteDialog(Frame owner, Cliente cliente, Runnable onSuccess) {
        super(owner, cliente == null ? "Registrar Nuevo Cliente" : "Editar Cliente", true);
        this.owner = owner;
        this.cliente = cliente;
        this.onSuccess = onSuccess;
        initUI();
    }

    private void initUI() {
        setResizable(false);

        txtNom = new JTextField();
        txtNom.putClientProperty("JTextField.placeholderText", "Nombre del cliente");
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
        
        // Seleccionar por defecto Cédula (id = 1)
        for (int i = 0; i < cbTipo.getItemCount(); i++) {
            TipoDocumento td = cbTipo.getItemAt(i);
            if (td.getId().equals(1L) || td.getNombre().toLowerCase().contains("cedula")) {
                cbTipo.setSelectedIndex(i);
                break;
            }
        }

        txtDoc = new JTextField();
        txtDoc.putClientProperty("JTextField.placeholderText", "Cédula / NIT");
        txtDoc.putClientProperty("JComponent.roundRect", true);

        txtTel = new JTextField();
        txtTel.putClientProperty("JTextField.placeholderText", "Teléfono");
        txtTel.putClientProperty("JComponent.roundRect", true);

        txtMail = new JTextField();
        txtMail.putClientProperty("JTextField.placeholderText", "correo@ejemplo.com");
        txtMail.putClientProperty("JComponent.roundRect", true);

        txtDir = new JTextField();
        txtDir.putClientProperty("JTextField.placeholderText", "Dirección física");
        txtDir.putClientProperty("JComponent.roundRect", true);

        cbFreq = new JComboBox<>(new String[] { "No", "Sí" });
        cbFreq.putClientProperty("JComponent.roundRect", true);

        cbEstado = new JComboBox<>(new String[] { "Activo", "Inactivo" });
        cbEstado.putClientProperty("JComponent.roundRect", true);

        if (cliente != null) {
            txtNom.setText(cliente.getNombre());
            txtDoc.setText(cliente.getNumeroDocumento());
            txtTel.setText(cliente.getTelefono());
            txtMail.setText(cliente.getEmail());
            txtDir.setText(cliente.getDireccion());
            cbFreq.setSelectedItem(cliente.isFrecuente() ? "Sí" : "No");
            cbEstado.setSelectedItem(cliente.getActivo() != null && cliente.getActivo() == 1 ? "Activo" : "Inactivo");

            if (cliente.getIdTipoDocumento() != null) {
                for (int i = 0; i < cbTipo.getItemCount(); i++) {
                    TipoDocumento td = cbTipo.getItemAt(i);
                    if (td.getId().equals(cliente.getIdTipoDocumento())) {
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

        JLabel lblTitle = new JLabel(cliente == null ? "Nuevo Cliente" : "Editar Cliente");
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

        addField.accept("Nombre Completo", txtNom);

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

        addField.accept("Dirección", txtDir);

        JPanel statusRow = new JPanel(new GridLayout(1, 2, 16, 0));
        statusRow.setBackground(Administrador.BG_DARK);
        statusRow.setMaximumSize(new Dimension(Integer.MAX_VALUE, 58));
        statusRow.setAlignmentX(Component.LEFT_ALIGNMENT);
        statusRow.add(createFieldPanel("Cliente Frecuente", cbFreq));
        statusRow.add(createFieldPanel("Estado", cbEstado));
        mainPanel.add(statusRow);
        mainPanel.add(Box.createVerticalStrut(14));

        mainPanel.add(Box.createVerticalStrut(10));

        JButton btnCancel = new JButton("Cancelar");
        btnCancel.setBackground(new Color(46, 53, 79));
        btnCancel.setForeground(Color.WHITE);
        btnCancel.setFont(new Font("Segoe UI", Font.BOLD, 14));
        btnCancel.setFocusPainted(false);
        btnCancel.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        btnCancel.putClientProperty("JButton.buttonType", "roundRect");

        JButton btnSave = new JButton("Guardar");
        btnSave.setBackground(Administrador.PRIMARY);
        btnSave.setForeground(Color.WHITE);
        btnSave.setFont(new Font("Segoe UI", Font.BOLD, 14));
        btnSave.setFocusPainted(false);
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
                    JOptionPane.showMessageDialog(this, "El nombre completo es obligatorio.", "Error", JOptionPane.ERROR_MESSAGE);
                    return;
                }

                Cliente c = cliente != null ? cliente : new Cliente();
                TipoDocumento tdSel = (TipoDocumento) cbTipo.getSelectedItem();
                c.setIdTipoDocumento(tdSel != null ? tdSel.getId() : 1L);
                c.setNumeroDocumento(txtDoc.getText());
                c.setNombre(txtNom.getText());
                c.setTelefono(txtTel.getText());
                c.setEmail(txtMail.getText());
                c.setDireccion(txtDir.getText());
                c.setFrecuente("Sí".equals(cbFreq.getSelectedItem()));
                c.setActivo("Activo".equals(cbEstado.getSelectedItem()) ? 1 : 0);

                if (cliente == null) {
                    ApiClient.post("/clientes", c, Cliente.class);
                    JOptionPane.showMessageDialog(owner, "Cliente guardado con éxito.", "Éxito", JOptionPane.INFORMATION_MESSAGE);
                } else {
                    ApiClient.put("/clientes/" + c.getId(), c, Cliente.class);
                    JOptionPane.showMessageDialog(owner, "Cliente actualizado con éxito.", "Éxito", JOptionPane.INFORMATION_MESSAGE);
                }
                dispose();
                if (onSuccess != null) {
                    onSuccess.run();
                }
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(this, "Error al guardar el cliente: " + ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
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
