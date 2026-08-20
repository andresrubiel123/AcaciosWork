package com.acacioswork.interfaz_usuario;

import java.awt.BorderLayout;
import javax.swing.JButton;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JTable;
import javax.swing.SwingUtilities;
import javax.swing.border.EmptyBorder;
import com.acacioswork.model.Producto;
import com.acacioswork.util.ApiClient;

/**
 * Pestaña de gestión de inventario para la interfaz de Administrador.
 * Permite listar, agregar, editar, eliminar y realizar movimientos de stock de productos.
 * @author RADJ
 */
public class InventarioTab extends JPanel {
    private final Administrador parent;
    private final JTable table;

    public InventarioTab(Administrador parent) {
        this.parent = parent;
        setLayout(new BorderLayout(0, 16));
        setBackground(Administrador.BG_DARK);
        setBorder(new EmptyBorder(24, 28, 24, 28));

        // Inicializar tabla con columnas alineadas a la web
        table = UIUtils.buildStyledTable(new String[] {
                "ID", "Código", "Nombre", "Unidad", "Stock", "P. Compra", "P. Venta", "Vencimiento", "IVA", "Estado", "Movimientos", "Acciones"
        });
        UIUtils.hideColumn(table, 0);
        table.getColumnModel().getColumn(1).setPreferredWidth(100);
        table.getColumnModel().getColumn(2).setPreferredWidth(250);
        table.getColumnModel().getColumn(3).setPreferredWidth(80);
        table.getColumnModel().getColumn(4).setPreferredWidth(100);
        table.getColumnModel().getColumn(5).setPreferredWidth(100);
        table.getColumnModel().getColumn(6).setPreferredWidth(100);
        table.getColumnModel().getColumn(7).setPreferredWidth(110);
        table.getColumnModel().getColumn(8).setPreferredWidth(60);
        table.getColumnModel().getColumn(9).setPreferredWidth(80);

        table.getColumnModel().getColumn(9).setCellRenderer(new EstadoCellRenderer());
        table.getColumnModel().getColumn(4).setCellRenderer(new StockNumberCellRenderer());

        // Configurar columna de Movimientos (Entrada/Salida)
        MovimientosPanel.setupColumn(table, 10,
                () -> registrarMovimiento("ENTRADA"),
                () -> registrarMovimiento("SALIDA")
        );

        // Configurar columna de Acciones (Editar/Borrar)
        UIUtils.setupAccionesColumn(table,
                this::editarProducto,
                () -> UIUtils.eliminarGeneric(table, "/productos", "Producto", this, () -> {
                    refresh();
                    parent.refreshWelcomeStats();
                }));

        // Botón de agregar
        JButton bAdd = UIUtils.createActionButton("+ Nuevo Producto", Administrador.ACCENT);
        bAdd.addActionListener(e -> agregarProducto());

        add(UIUtils.buildSectionHeader("Inventario de Productos", "Control total de existencias y precios", bAdd),
                BorderLayout.NORTH);

        // Contenedor de la tabla
        JPanel tableContainer = new JPanel(new BorderLayout(0, 8));
        tableContainer.setOpaque(false);
        tableContainer.add(UIUtils.buildSearchPanel(table), BorderLayout.NORTH);
        tableContainer.add(UIUtils.wrapTable(table), BorderLayout.CENTER);

        add(tableContainer, BorderLayout.CENTER);
    }

    public void refresh() {
        UIUtils.loadTable(table, "/productos", row -> {
            Long id = UIUtils.id(row);
            int qty = UIUtils.num(row, "stockActual");
            int min = row.get("stockMinimo") != null ? UIUtils.num(row, "stockMinimo") : 5;
            int opt = row.get("stockOptimo") != null ? UIUtils.num(row, "stockOptimo") : 200;
            double precioCompra = UIUtils.dbl(row, "precioCompra");
            double precioVenta = UIUtils.dbl(row, "precioVenta");

            String estadoLabel = "1".equals(UIUtils.str(row, "estado")) ? "Activo" : "Inactivo";
            String ivaLabel = row.get("iva") != null ? UIUtils.str(row, "iva") + "%" : "0%";
            String unidadMedida = UIUtils.str(row, "unidadMedida") != null && !UIUtils.str(row, "unidadMedida").equals("—")
                    ? UIUtils.str(row, "unidadMedida")
                    : "Unidad";
            String fv = UIUtils.str(row, "fechaVencimiento");

            return new Object[] {
                    id,
                    UIUtils.str(row, "codigoBarras"),
                    UIUtils.str(row, "nombre"),
                    unidadMedida,
                    new StockData(qty, min, opt),
                    com.acacioswork.util.ConfiguracionManager.formatCurrency(precioCompra),
                    com.acacioswork.util.ConfiguracionManager.formatCurrency(precioVenta),
                    fv,
                    ivaLabel,
                    estadoLabel,
                    "",
                    ""
            };
        });
    }

    private void agregarProducto() {
        new com.acacioswork.interfaz_usuario.dialogos.ProductoDialog((java.awt.Frame) SwingUtilities.getWindowAncestor(this), null, () -> {
            refresh();
            parent.refreshWelcomeStats();
        }).setVisible(true);
    }

    private void editarProducto() {
        int r = table.getSelectedRow();
        if (r == -1) {
            JOptionPane.showMessageDialog(this, "Seleccione un producto de la tabla.", "Advertencia",
                    JOptionPane.WARNING_MESSAGE);
            return;
        }
        Long id = (Long) table.getValueAt(r, 0);
        try {
            Producto p = ApiClient.get("/productos/" + id, Producto.class);
            new com.acacioswork.interfaz_usuario.dialogos.ProductoDialog((java.awt.Frame) SwingUtilities.getWindowAncestor(this), p, () -> {
                refresh();
                parent.refreshWelcomeStats();
            }).setVisible(true);
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, "Error al cargar producto: " + e.getMessage(), "Error",
                    JOptionPane.ERROR_MESSAGE);
        }
    }

    private void registrarMovimiento(String tipo) {
        int r = table.getSelectedRow();
        if (r == -1) {
            JOptionPane.showMessageDialog(this, "Seleccione un producto de la tabla.", "Advertencia",
                    JOptionPane.WARNING_MESSAGE);
            return;
        }
        Long id = (Long) table.getValueAt(r, 0);
        try {
            Producto p = ApiClient.get("/productos/" + id, Producto.class);
            new com.acacioswork.interfaz_usuario.dialogos.MovimientoDialog((java.awt.Frame) SwingUtilities.getWindowAncestor(this), p, tipo, () -> {
                refresh();
                parent.refreshWelcomeStats();
            }).setVisible(true);
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, "Error al cargar producto: " + e.getMessage(), "Error",
                    JOptionPane.ERROR_MESSAGE);
        }
    }
}
