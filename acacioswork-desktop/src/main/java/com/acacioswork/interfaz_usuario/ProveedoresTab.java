package com.acacioswork.interfaz_usuario;

import java.awt.BorderLayout;
import javax.swing.JButton;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JTable;
import javax.swing.SwingUtilities;
import javax.swing.border.EmptyBorder;
import com.acacioswork.model.Proveedor;
import com.acacioswork.util.ApiClient;

/**
 * Pestaña de gestión de proveedores para la interfaz de Administrador.
 * Permite listar, agregar, editar y eliminar proveedores.
 * @author RADJ
 */
public class ProveedoresTab extends JPanel {
    private final JTable table;

    public ProveedoresTab() {
        setLayout(new BorderLayout(0, 16));
        setBackground(Administrador.BG_DARK);
        setBorder(new EmptyBorder(24, 28, 24, 28));

        // Inicializar tabla
        table = UIUtils.buildStyledTable(new String[] {
                "ID", "Nombre", "Teléfono", "Email", "Doc/NIT", "Cuenta Bancaria", "Estado", "Acciones"
        });
        UIUtils.hideColumn(table, 0);
        table.getColumnModel().getColumn(6).setCellRenderer(new EstadoCellRenderer());

        UIUtils.setupAccionesColumn(table,
                this::editarProveedor,
                () -> UIUtils.eliminarGeneric(table, "/proveedores", "Proveedor", this, this::refresh));

        // Botón de agregar
        JButton bAdd = UIUtils.createActionButton("+ Nuevo Proveedor", Administrador.ACCENT);
        bAdd.addActionListener(e -> agregarProveedor());

        add(UIUtils.buildSectionHeader("Proveedores", "Gestión de contactos y suministradores", bAdd),
                BorderLayout.NORTH);

        // Contenedor de la tabla
        JPanel tableContainer = new JPanel(new BorderLayout(0, 8));
        tableContainer.setOpaque(false);
        tableContainer.add(UIUtils.buildSearchPanel(table), BorderLayout.NORTH);
        tableContainer.add(UIUtils.wrapTable(table), BorderLayout.CENTER);

        add(tableContainer, BorderLayout.CENTER);
    }

    public void refresh() {
        UIUtils.loadTable(table, "/proveedores", row -> new Object[] {
                UIUtils.id(row),
                UIUtils.str(row, "nombre"),
                UIUtils.str(row, "telefono"),
                UIUtils.str(row, "email"),
                UIUtils.str(row, "numeroDocumento"),
                UIUtils.str(row, "cuentaBancaria"),
                "1".equals(UIUtils.str(row, "activo")) ? "Activo" : "Inactivo",
                ""
        });
    }

    private void agregarProveedor() {
        new com.acacioswork.interfaz_usuario.dialogos.ProveedorDialog((java.awt.Frame) SwingUtilities.getWindowAncestor(this), null,
                this::refresh).setVisible(true);
    }

    private void editarProveedor() {
        int row = table.getSelectedRow();
        if (row == -1) {
            JOptionPane.showMessageDialog(this, "Seleccione un proveedor.");
            return;
        }
        Long id = (Long) table.getValueAt(row, 0);
        try {
            Proveedor p = ApiClient.get("/proveedores/" + id, Proveedor.class);
            new com.acacioswork.interfaz_usuario.dialogos.ProveedorDialog((java.awt.Frame) SwingUtilities.getWindowAncestor(this), p,
                    this::refresh).setVisible(true);
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, "Error al cargar proveedor: " + e.getMessage());
        }
    }
}
