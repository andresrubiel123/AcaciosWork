package com.acacioswork.interfaz_usuario;

import java.awt.BorderLayout;
import java.util.Arrays;
import javax.swing.JButton;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JTable;
import javax.swing.SwingUtilities;
import javax.swing.border.EmptyBorder;
import com.acacioswork.model.Usuario;
import com.acacioswork.util.ApiClient;

/**
 * Pestaña de gestión de usuarios del sistema para la interfaz de Administrador.
 * Permite listar, agregar, editar y eliminar usuarios.
 * @author RADJ
 */
public class UsuariosTab extends JPanel {
    private final JTable table;

    public UsuariosTab() {
        setLayout(new BorderLayout(0, 16));
        setBackground(Administrador.BG_DARK);
        setBorder(new EmptyBorder(24, 28, 24, 28));

        // Inicializar tabla
        table = UIUtils.buildStyledTable(new String[] { "ID", "Nombre", "Usuario", "Doc/Id", "Estado", "Acciones" });
        UIUtils.hideColumn(table, 0);
        table.getColumnModel().getColumn(4).setCellRenderer(new EstadoCellRenderer());

        UIUtils.setupAccionesColumn(table,
                this::editarUsuario,
                this::eliminarUsuario);

        // Botón agregar
        JButton bAdd = UIUtils.createActionButton("+ Nuevo Usuario", Administrador.ACCENT);
        bAdd.addActionListener(e -> agregarUsuario());

        add(UIUtils.buildSectionHeader("Usuarios del Sistema", "Administración de accesos y roles", bAdd),
                BorderLayout.NORTH);

        // Contenedor de tabla
        JPanel tableContainer = new JPanel(new BorderLayout(0, 8));
        tableContainer.setOpaque(false);
        tableContainer.add(UIUtils.buildSearchPanel(table), BorderLayout.NORTH);
        tableContainer.add(UIUtils.wrapTable(table), BorderLayout.CENTER);

        add(tableContainer, BorderLayout.CENTER);
    }

    public void refresh() {
        UIUtils.loadTable(table, "/usuarios", row -> new Object[] {
                UIUtils.id(row),
                UIUtils.str(row, "nombre"),
                UIUtils.str(row, "usuario"),
                UIUtils.str(row, "numeroDocumento"),
                "1".equals(UIUtils.str(row, "activo")) ? "Activo" : "Inactivo",
                ""
        });
    }

    private void agregarUsuario() {
        new com.acacioswork.interfaz_usuario.dialogos.UsuarioDialog((java.awt.Frame) SwingUtilities.getWindowAncestor(this), null, this::refresh)
                .setVisible(true);
    }

    private void editarUsuario() {
        int row = table.getSelectedRow();
        if (row == -1) {
            JOptionPane.showMessageDialog(this, "Seleccione un usuario de la tabla.");
            return;
        }
        String iden = table.getValueAt(row, 3).toString();
        try {
            Usuario[] todos = ApiClient.get("/usuarios", Usuario[].class);
            Usuario u = Arrays.stream(todos)
                    .filter(user -> iden.equals(user.getIdentificacion()))
                    .findFirst()
                    .orElse(null);

            if (u == null) {
                JOptionPane.showMessageDialog(this, "No se encontró la información del usuario.");
                return;
            }
            new com.acacioswork.interfaz_usuario.dialogos.UsuarioDialog((java.awt.Frame) SwingUtilities.getWindowAncestor(this), u, this::refresh)
                    .setVisible(true);
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, "Error al cargar usuario: " + e.getMessage());
        }
    }

    private void eliminarUsuario() {
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
                refresh();
            } catch (Exception e) {
                JOptionPane.showMessageDialog(this, "Error al eliminar: " + e.getMessage());
            }
        }
    }
}
