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
import javax.swing.JPasswordField;
import javax.swing.JTextField;
import javax.swing.border.EmptyBorder;

import com.acacioswork.interfaz_usuario.Administrador;
import com.acacioswork.model.Rol;
import com.acacioswork.model.TipoDocumento;
import com.acacioswork.model.Usuario;
import com.acacioswork.util.ApiClient;

public class UsuarioDialog extends JDialog {
    private final Frame owner;
    private final Usuario usuario; // null means create, non-null means edit
    private final Runnable onSuccess;

    private JTextField txtNom;
    private JTextField txtApe;
    private JComboBox<TipoDocumento> cbTipo;
    private JTextField txtDoc;
    private JTextField txtTel;
    private JTextField txtMail;
    private JTextField txtUser;
    private JPasswordField txtPass;
    private JComboBox<Rol> cbRol;
    private JComboBox<String> cbEstado;

    public UsuarioDialog(Frame owner, Usuario usuario, Runnable onSuccess) {
        super(owner, usuario == null ? "Registrar Nuevo Usuario" : "Editar Usuario", true);
        this.owner = owner;
        this.usuario = usuario;
        this.onSuccess = onSuccess;
        initUI();
    }

    private void initUI() {
        setResizable(false);

        txtNom = new JTextField();
        txtNom.putClientProperty("JTextField.placeholderText", "Nombre");
        txtNom.putClientProperty("JComponent.roundRect", true);

        txtApe = new JTextField();
        txtApe.putClientProperty("JTextField.placeholderText", "Apellido");
        txtApe.putClientProperty("JComponent.roundRect", true);

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
        txtDoc.putClientProperty("JTextField.placeholderText", "Número de Documento");
        txtDoc.putClientProperty("JComponent.roundRect", true);

        txtTel = new JTextField();
        txtTel.putClientProperty("JTextField.placeholderText", "Teléfono");
        txtTel.putClientProperty("JComponent.roundRect", true);

        txtMail = new JTextField();
        txtMail.putClientProperty("JTextField.placeholderText", "correo@ejemplo.com");
        txtMail.putClientProperty("JComponent.roundRect", true);

        txtUser = new JTextField();
        txtUser.putClientProperty("JTextField.placeholderText", "Nombre de usuario");
        txtUser.putClientProperty("JComponent.roundRect", true);

        txtPass = new JPasswordField();
        txtPass.putClientProperty("JTextField.placeholderText", "Contraseña");
        txtPass.putClientProperty("JComponent.roundRect", true);

        java.util.Vector<Rol> roles = new java.util.Vector<>();
        try {
            Rol[] rolesArr = ApiClient.get("/roles", Rol[].class);
            if (rolesArr != null) {
                roles.addAll(java.util.Arrays.asList(rolesArr));
            }
        } catch (Exception e) {
            roles.add(new Rol(1L, "Administrador"));
            roles.add(new Rol(2L, "Vendedor"));
        }
        cbRol = new JComboBox<>(roles);
        cbRol.putClientProperty("JComponent.roundRect", true);

        cbEstado = new JComboBox<>(new String[] { "Activo", "Inactivo" });
        cbEstado.putClientProperty("JComponent.roundRect", true);

        if (usuario != null) {
            txtNom.setText(usuario.getNombre());
            txtApe.setText(usuario.getApellido());
            txtDoc.setText(usuario.getIdentificacion());
            txtTel.setText(usuario.getTelefono());
            txtMail.setText(usuario.getEmail());
            txtUser.setText(usuario.getUsuario());
            txtPass.setText(""); // clear by default for safety in edit
            
            if (usuario.getIdTipoDocumento() != null) {
                for (int i = 0; i < cbTipo.getItemCount(); i++) {
                    TipoDocumento td = cbTipo.getItemAt(i);
                    if (td.getId().equals(usuario.getIdTipoDocumento())) {
                        cbTipo.setSelectedIndex(i);
                        break;
                    }
                }
            }

            if (usuario.getIdRol() != null) {
                for (Rol r : roles) {
                    if (r.getId().equals(usuario.getIdRol())) {
                        cbRol.setSelectedItem(r);
                        break;
                    }
                }
            }

            cbEstado.setSelectedItem(usuario.getActivo() != null && usuario.getActivo() == 1 ? "Activo" : "Inactivo");
            
            // Identificación field is primary key in many backend operations, so keep it editable or disabled depending on need
            // Let's keep it editable but we can make it disabled during edit if necessary. The original was editable.
        }

        JPanel mainPanel = new JPanel();
        mainPanel.setLayout(new BoxLayout(mainPanel, BoxLayout.Y_AXIS));
        mainPanel.setBackground(Administrador.BG_DARK);
        mainPanel.setBorder(new EmptyBorder(24, 28, 24, 28));

        JLabel lblTitle = new JLabel(usuario == null ? "Nuevo Usuario" : "Editar Usuario");
        lblTitle.setForeground(Administrador.TEXT_MAIN);
        lblTitle.setFont(new Font("Segoe UI", Font.BOLD, 26));
        lblTitle.setAlignmentX(Component.LEFT_ALIGNMENT);
        mainPanel.add(lblTitle);
        mainPanel.add(Box.createVerticalStrut(20));


        JPanel nameRow = new JPanel(new GridLayout(1, 2, 16, 0));
        nameRow.setBackground(Administrador.BG_DARK);
        nameRow.setMaximumSize(new Dimension(Integer.MAX_VALUE, 58));
        nameRow.setAlignmentX(Component.LEFT_ALIGNMENT);
        nameRow.add(createFieldPanel("Nombre", txtNom));
        nameRow.add(createFieldPanel("Apellido", txtApe));
        mainPanel.add(nameRow);
        mainPanel.add(Box.createVerticalStrut(14));

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

        JPanel loginRow = new JPanel(new GridLayout(1, 2, 16, 0));
        loginRow.setBackground(Administrador.BG_DARK);
        loginRow.setMaximumSize(new Dimension(Integer.MAX_VALUE, 58));
        loginRow.setAlignmentX(Component.LEFT_ALIGNMENT);
        loginRow.add(createFieldPanel("Nombre de Usuario", txtUser));
        loginRow.add(createFieldPanel(usuario == null ? "Contraseña" : "Nueva Contraseña", txtPass));
        mainPanel.add(loginRow);
        mainPanel.add(Box.createVerticalStrut(14));

        JPanel roleRow = new JPanel(new GridLayout(1, 2, 16, 0));
        roleRow.setBackground(Administrador.BG_DARK);
        roleRow.setMaximumSize(new Dimension(Integer.MAX_VALUE, 58));
        roleRow.setAlignmentX(Component.LEFT_ALIGNMENT);
        roleRow.add(createFieldPanel("Rol", cbRol));
        roleRow.add(createFieldPanel("Estado", cbEstado));
        mainPanel.add(roleRow);
        mainPanel.add(Box.createVerticalStrut(20));

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
                if (txtNom.getText().trim().isEmpty() || txtUser.getText().trim().isEmpty()) {
                    JOptionPane.showMessageDialog(this, "El nombre y el usuario son obligatorios.", "Error", JOptionPane.ERROR_MESSAGE);
                    return;
                }

                Usuario u = usuario != null ? usuario : new Usuario();
                TipoDocumento tdSel = (TipoDocumento) cbTipo.getSelectedItem();
                u.setIdTipoDocumento(tdSel != null ? tdSel.getId() : 1L);
                u.setIdentificacion(txtDoc.getText());
                u.setNombre(txtNom.getText());
                u.setApellido(txtApe.getText());
                u.setTelefono(txtTel.getText());
                u.setEmail(txtMail.getText());
                u.setUsuario(txtUser.getText());
                
                String pass = new String(txtPass.getPassword());
                if (usuario == null || !pass.isEmpty()) {
                    u.setClave(pass);
                }

                Rol r = (Rol) cbRol.getSelectedItem();
                u.setIdRol(r != null ? r.getId() : 2L);
                u.setActivo("Activo".equals(cbEstado.getSelectedItem()) ? 1 : 0);

                if (usuario == null) {
                    ApiClient.post("/usuarios", u, Usuario.class);
                    JOptionPane.showMessageDialog(owner, "Usuario creado exitosamente.", "Éxito", JOptionPane.INFORMATION_MESSAGE);
                } else {
                    ApiClient.put("/usuarios/" + u.getIdentificacion(), u, Usuario.class);
                    JOptionPane.showMessageDialog(owner, "Usuario actualizado correctamente.", "Éxito", JOptionPane.INFORMATION_MESSAGE);
                }
                dispose();
                if (onSuccess != null) {
                    onSuccess.run();
                }
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(this, "Error al guardar el usuario: " + ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
            }
        });

        setContentPane(mainPanel);
        pack();
        setSize(460, 480);
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
