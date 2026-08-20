package com.acacioswork.interfaz_usuario;

import java.awt.BorderLayout;
import java.awt.GridLayout;
import java.util.Map;
import javax.swing.JButton;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JTable;
import javax.swing.SwingUtilities;
import javax.swing.SwingWorker;
import javax.swing.border.EmptyBorder;
import com.acacioswork.model.Cliente;
import com.acacioswork.util.ApiClient;

/**
 * Pestaña de gestión de clientes para la interfaz de Administrador.
 * Muestra KPIs de clientes y una tabla con acciones de edición/eliminación.
 * @author RADJ
 */
public class ClientesTab extends JPanel {
    private final JTable table;
    private final JPanel statsClientes;

    public ClientesTab() {
        setLayout(new BorderLayout(0, 16));
        setBackground(Administrador.BG_DARK);
        setBorder(new EmptyBorder(24, 28, 24, 28));

        // Estadísticas de clientes
        statsClientes = new JPanel(new GridLayout(1, 2, 12, 0));
        statsClientes.setOpaque(false);
        statsClientes.setBorder(new EmptyBorder(0, 0, 16, 0));
        statsClientes.add(UIUtils.buildStatCard("Total Clientes", "0", Administrador.TEXT_MAIN));
        statsClientes.add(UIUtils.buildStatCard("Activos", "0", Administrador.ACCENT));

        // Inicializar tabla
        table = UIUtils.buildStyledTable(new String[] {
                "ID", "Nombre", "Identificación", "Teléfono", "Email", "Frecuente", "Estado", "Acciones"
        });
        UIUtils.hideColumn(table, 0);
        table.getColumnModel().getColumn(6).setCellRenderer(new EstadoCellRenderer());

        UIUtils.setupAccionesColumn(table,
                this::editarCliente,
                () -> UIUtils.eliminarGeneric(table, "/clientes", "Cliente", this, this::refresh));

        // Botón agregar
        JButton bAdd = UIUtils.createActionButton("+ Nuevo Cliente", Administrador.ACCENT);
        bAdd.addActionListener(e -> agregarCliente());

        add(UIUtils.buildSectionHeader("Clientes", "Base de datos de clientes registrados", bAdd), BorderLayout.NORTH);

        JPanel center = new JPanel(new BorderLayout(0, 12));
        center.setOpaque(false);
        center.add(statsClientes, BorderLayout.NORTH);

        JPanel tableContainer = new JPanel(new BorderLayout(0, 8));
        tableContainer.setOpaque(false);
        tableContainer.add(UIUtils.buildSearchPanel(table), BorderLayout.NORTH);
        tableContainer.add(UIUtils.wrapTable(table), BorderLayout.CENTER);

        center.add(tableContainer, BorderLayout.CENTER);
        add(center, BorderLayout.CENTER);
    }

    public void refresh() {
        // Cargar tabla
        UIUtils.loadTable(table, "/clientes", row -> new Object[] {
                UIUtils.id(row),
                UIUtils.str(row, "nombre"),
                UIUtils.str(row, "numeroDocumento"),
                UIUtils.str(row, "telefono"),
                UIUtils.str(row, "email"),
                "true".equals(UIUtils.str(row, "frecuente")) ? "Sí" : "No",
                "1".equals(UIUtils.str(row, "activo")) ? "Activo" : "Inactivo",
                ""
        });

        // Recargar estadísticas en segundo plano
        new SwingWorker<Void, Void>() {
            @SuppressWarnings("unchecked")
            @Override
            protected Void doInBackground() {
                try {
                    Object[] data = ApiClient.get("/clientes", Object[].class);
                    int activosCount = 0;
                    if (data != null) {
                        for (Object raw : data) {
                            Map<String, Object> c = (Map<String, Object>) raw;
                            if ("1".equals(UIUtils.str(c, "activo"))) {
                                activosCount++;
                            }
                        }
                        final int total = data.length;
                        final int activos = activosCount;
                        SwingUtilities.invokeLater(() -> {
                            UIUtils.updateStatCard((JPanel) statsClientes.getComponents()[0], String.valueOf(total));
                            UIUtils.updateStatCard((JPanel) statsClientes.getComponents()[1], String.valueOf(activos));
                        });
                    }
                } catch (Exception e) {
                }
                return null;
            }
        }.execute();
    }

    private void agregarCliente() {
        new com.acacioswork.interfaz_usuario.dialogos.ClienteDialog((java.awt.Frame) SwingUtilities.getWindowAncestor(this), null, this::refresh)
                .setVisible(true);
    }

    private void editarCliente() {
        int row = table.getSelectedRow();
        if (row == -1) {
            JOptionPane.showMessageDialog(this, "Seleccione un cliente.");
            return;
        }
        Long id = (Long) table.getValueAt(row, 0);
        try {
            Cliente c = ApiClient.get("/clientes/" + id, Cliente.class);
            new com.acacioswork.interfaz_usuario.dialogos.ClienteDialog((java.awt.Frame) SwingUtilities.getWindowAncestor(this), c, this::refresh)
                    .setVisible(true);
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, "Error al cargar cliente: " + e.getMessage());
        }
    }
}
