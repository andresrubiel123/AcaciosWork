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
import javax.swing.JCheckBox;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JPasswordField;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.table.DefaultTableModel;

import com.acacioswork.model.Rol;
import com.acacioswork.model.Usuario;
import com.acacioswork.util.ApiClient;

/** Interfaz para gestión de usuarios vía API REST. @author RADJ */
public class GestionUsuarios extends JPanel {

    private DefaultTableModel modeloTabla;
    private JTable tablaUsuarios;

    public GestionUsuarios() {
        setLayout(new BorderLayout());
        setBackground(new Color(15, 23, 42));

        // Panel superior: Botón atrás
        JPanel panelSuperior = new JPanel(new FlowLayout(FlowLayout.LEFT));
        panelSuperior.setOpaque(false);
        GradientButton btnAtras = new GradientButton("‹ Volver", new Color(99, 102, 241), new Color(79, 70, 229));
        btnAtras.addActionListener(e -> {
            MainFrame.navigateTo(new Administrador());
        });
        panelSuperior.add(btnAtras);

        JLabel lblTitle = new JLabel("Gestión de Usuarios");
        lblTitle.setFont(new Font("Inter", Font.BOLD, 20));
        lblTitle.setForeground(Color.WHITE);
        panelSuperior.add(lblTitle);

        add(panelSuperior, BorderLayout.NORTH);

        // Tabla de usuarios
        modeloTabla = new DefaultTableModel() {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
        modeloTabla.addColumn("ID");
        modeloTabla.addColumn("Doc/Id");
        modeloTabla.addColumn("Nombre");
        modeloTabla.addColumn("Apellido");
        modeloTabla.addColumn("Usuario");
        modeloTabla.addColumn("Email");
        modeloTabla.addColumn("Rol");
        modeloTabla.addColumn("Activo");

        tablaUsuarios = new JTable(modeloTabla);
        tablaUsuarios.setBackground(new Color(30, 41, 59));
        tablaUsuarios.setForeground(Color.WHITE);
        tablaUsuarios.setGridColor(new Color(51, 65, 85));
        tablaUsuarios.getTableHeader().setBackground(new Color(51, 65, 85));
        tablaUsuarios.getTableHeader().setForeground(Color.WHITE);

        JScrollPane scrollPane = new JScrollPane(tablaUsuarios);
        scrollPane.setBorder(BorderFactory.createEmptyBorder());
        scrollPane.getViewport().setBackground(new Color(15, 23, 42));
        add(scrollPane, BorderLayout.CENTER);

        // Panel inferior: Botones CRUD
        JPanel panelBotones = new JPanel();
        panelBotones.setOpaque(false);
        panelBotones.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        GradientButton btnAgregar = new GradientButton("Agregar", new Color(16, 185, 129), new Color(5, 150, 105));
        GradientButton btnEditar = new GradientButton("Editar", new Color(99, 102, 241), new Color(79, 70, 229));
        GradientButton btnEliminar = new GradientButton("Eliminar", new Color(239, 68, 68), new Color(185, 28, 28));

        btnAgregar.addActionListener(e -> agregarUsuario());
        btnEditar.addActionListener(e -> editarUsuario());
        btnEliminar.addActionListener(e -> eliminarUsuario());

        panelBotones.add(btnAgregar);
        panelBotones.add(btnEditar);
        panelBotones.add(btnEliminar);
        add(panelBotones, BorderLayout.SOUTH);

        cargarUsuarios();
    }

    private void cargarUsuarios() {
        modeloTabla.setRowCount(0);
        try {
            Usuario[] usuarios = ApiClient.get("/usuarios", Usuario[].class);
            for (Usuario u : usuarios) {
                modeloTabla.addRow(new Object[] {
                        u.getId(),
                        u.getIdentificacion(),
                        u.getNombre(),
                        u.getApellido(),
                        u.getUsuario(),
                        u.getEmail(),
                        u.getIdRol(),
                        u.getActivo() != null && u.getActivo() == 1 ? "Sí" : "No"
                });
            }
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, "Error al cargar usuarios: " + e.getMessage());
        }
    }

    private void agregarUsuario() {
        JTextField txtIdentificacion = new JTextField();
        JTextField txtNombre = new JTextField();
        JTextField txtApellido = new JTextField();
        JTextField txtEmail = new JTextField();
        JTextField txtUsuario = new JTextField();
        JPasswordField txtClave = new JPasswordField();

        // Cargar roles desde API
        Vector<Rol> roles = new Vector<>();
        try {
            Rol[] rolesArr = ApiClient.get("/roles", Rol[].class);
            roles.addAll(Arrays.asList(rolesArr));
        } catch (Exception e) {
            roles.add(new Rol(2L, "Vendedor"));
        }
        JComboBox<Rol> cbRol = new JComboBox<>(roles);
        JCheckBox chkActivo = new JCheckBox("Usuario activo", true);

        Object[] message = {
                "Identificación:", txtIdentificacion,
                "Nombre:", txtNombre,
                "Apellido:", txtApellido,
                "Email:", txtEmail,
                "Usuario (Login):", txtUsuario,
                "Contraseña:", txtClave,
                "Rol:", cbRol,
                chkActivo
        };

        int option = JOptionPane.showConfirmDialog(this, message, "Agregar Usuario", JOptionPane.OK_CANCEL_OPTION);
        if (option == JOptionPane.OK_OPTION) {
            Usuario u = new Usuario();
            u.setIdentificacion(txtIdentificacion.getText());
            u.setNombre(txtNombre.getText());
            u.setApellido(txtApellido.getText());
            u.setEmail(txtEmail.getText());
            u.setUsuario(txtUsuario.getText());
            u.setClave(new String(txtClave.getPassword()));
            Rol selRol = (Rol) cbRol.getSelectedItem();
            u.setIdRol(selRol != null ? selRol.getId() : 2L);
            u.setActivo(chkActivo.isSelected() ? 1 : 0);

            try {
                ApiClient.post("/usuarios", u, Usuario.class);
                cargarUsuarios();
                JOptionPane.showMessageDialog(this, "Usuario creado exitosamente.");
            } catch (Exception e) {
                JOptionPane.showMessageDialog(this, "Error al crear: " + e.getMessage());
            }
        }
    }

    private void editarUsuario() {
        int fila = tablaUsuarios.getSelectedRow();
        if (fila == -1) {
            JOptionPane.showMessageDialog(this, "Seleccione un usuario.");
            return;
        }

        String identificacion = (String) modeloTabla.getValueAt(fila, 1);
        try {
            Usuario actual = ApiClient.get("/usuarios/" + identificacion, Usuario.class);

            JTextField txtNombre = new JTextField(actual.getNombre());
            JTextField txtApellido = new JTextField(actual.getApellido());
            JTextField txtEmail = new JTextField(actual.getEmail());
            JTextField txtUsuario = new JTextField(actual.getUsuario());
            JPasswordField txtClave = new JPasswordField(); // Opcional al editar
            JCheckBox chkActivo = new JCheckBox("Usuario activo", actual.getActivo() != null && actual.getActivo() == 1);

            Object[] message = {
                    "Nombre:", txtNombre,
                    "Apellido:", txtApellido,
                    "Email:", txtEmail,
                    "Usuario:", txtUsuario,
                    "Nueva Contraseña (dejar vacío para no cambiar):", txtClave,
                    chkActivo
            };

            int option = JOptionPane.showConfirmDialog(this, message, "Editar Usuario", JOptionPane.OK_CANCEL_OPTION);
            if (option == JOptionPane.OK_OPTION) {
                actual.setNombre(txtNombre.getText());
                actual.setApellido(txtApellido.getText());
                actual.setEmail(txtEmail.getText());
                actual.setUsuario(txtUsuario.getText());
                actual.setActivo(chkActivo.isSelected() ? 1 : 0);
                
                String pass = new String(txtClave.getPassword());
                if (!pass.isEmpty()) {
                    actual.setClave(pass);
                }

                ApiClient.put("/usuarios/" + identificacion, actual, Usuario.class);
                cargarUsuarios();
                JOptionPane.showMessageDialog(this, "Usuario actualizado correctamente.");
            }
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, "Error: " + e.getMessage());
        }
    }

    private void eliminarUsuario() {
        int fila = tablaUsuarios.getSelectedRow();
        if (fila == -1) {
            JOptionPane.showMessageDialog(this, "Seleccione un usuario.");
            return;
        }
        String identificacion = (String) modeloTabla.getValueAt(fila, 1);

        int confirm = JOptionPane.showConfirmDialog(this, "¿Eliminar usuario Doc " + identificacion + "?", "Confirmar",
                JOptionPane.YES_NO_OPTION);
        if (confirm == JOptionPane.YES_OPTION) {
            try {
                ApiClient.delete("/usuarios/" + identificacion);
                cargarUsuarios();
                JOptionPane.showMessageDialog(this, "Usuario eliminado.");
            } catch (Exception e) {
                JOptionPane.showMessageDialog(this, "Error: " + e.getMessage());
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
            setBorder(BorderFactory.createEmptyBorder(8, 15, 8, 15));
            setFocusPainted(false);
            setOpaque(false);
            setFont(new Font("Inter", Font.BOLD, 13));
            setCursor(new Cursor(Cursor.HAND_CURSOR));
        }

        @Override
        protected void paintComponent(Graphics g) {
            Graphics2D g2 = (Graphics2D) g.create();
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            GradientPaint paint = new GradientPaint(0, 0, startColor, 0, getHeight(), endColor);
            g2.setPaint(paint);
            g2.fillRoundRect(0, 0, getWidth(), getHeight(), 10, 10);
            g2.dispose();
            super.paintComponent(g);
        }
    }
}