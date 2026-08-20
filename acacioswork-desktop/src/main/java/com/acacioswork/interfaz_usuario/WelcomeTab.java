package com.acacioswork.interfaz_usuario;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.GridLayout;
import java.text.NumberFormat;
import java.util.Locale;
import java.util.Map;
import javax.swing.JButton;
import javax.swing.JPanel;
import javax.swing.JTable;
import javax.swing.SwingUtilities;
import javax.swing.SwingWorker;
import javax.swing.border.EmptyBorder;
import javax.swing.table.DefaultTableModel;
import com.acacioswork.util.ApiClient;

/**
 * Pestaña de Inicio (Resumen de Inventario) de la interfaz de Administrador.
 * Muestra KPIs de negocio y una tabla simplificada de productos.
 * @author RADJ
 */
public class WelcomeTab extends JPanel {
    private final Administrador parent;
    private final JPanel statsInventario;
    private final JTable tableHome;

    public WelcomeTab(Administrador parent) {
        this.parent = parent;
        setLayout(new BorderLayout(0, 16));
        setBackground(Administrador.BG_DARK);
        setBorder(new EmptyBorder(24, 28, 24, 28));

        // Encabezado
        add(UIUtils.buildSectionHeader("Resumen de Inventario", "Vista rápida del estado de existencias", (JButton) null), BorderLayout.NORTH);

        // Tarjetas de estadísticas
        statsInventario = new JPanel(new GridLayout(1, 5, 12, 0));
        statsInventario.setOpaque(false);
        statsInventario.setBorder(new EmptyBorder(0, 0, 16, 0));
        statsInventario.add(UIUtils.buildStatCard("Total Productos", "0", Administrador.TEXT_MAIN));
        statsInventario.add(UIUtils.buildStatCard("Stock Bajo", "0", Administrador.DANGER));
        statsInventario.add(UIUtils.buildStatCard("Valor Inventario", "$0", Administrador.ACCENT));
        statsInventario.add(UIUtils.buildStatCard("Valor Costo", "$0", new Color(245, 158, 11)));
        statsInventario.add(UIUtils.buildStatCard("Utilidad Neta", "$0", Administrador.ACCENT));

        // Tabla simplificada
        tableHome = UIUtils.buildStyledTable(new String[] { "ID", "Código", "Nombre", "Unidad", "Stock", "Estado" });
        UIUtils.hideColumn(tableHome, 0);
        tableHome.getColumnModel().getColumn(1).setPreferredWidth(100);
        tableHome.getColumnModel().getColumn(2).setPreferredWidth(160);
        tableHome.getColumnModel().getColumn(3).setPreferredWidth(100);
        tableHome.getColumnModel().getColumn(4).setPreferredWidth(300);
        tableHome.getColumnModel().getColumn(5).setPreferredWidth(100);

        tableHome.getColumnModel().getColumn(4).setCellRenderer(new StockBarCellRenderer());
        tableHome.getColumnModel().getColumn(5).setCellRenderer(new EstadoCellRenderer());

        // Buscador y Tabla
        JPanel tableContainer = new JPanel(new BorderLayout(0, 8));
        tableContainer.setOpaque(false);
        tableContainer.add(UIUtils.buildSearchPanel(tableHome), BorderLayout.NORTH);
        tableContainer.add(UIUtils.wrapTable(tableHome), BorderLayout.CENTER);

        // Contenedor Central
        JPanel centerContainer = new JPanel(new BorderLayout(0, 16));
        centerContainer.setOpaque(false);
        centerContainer.add(statsInventario, BorderLayout.NORTH);
        centerContainer.add(tableContainer, BorderLayout.CENTER);

        add(centerContainer, BorderLayout.CENTER);
    }

    public void refresh() {
        new SwingWorker<Void, Void>() {
            @SuppressWarnings("unchecked")
            @Override
            protected Void doInBackground() throws Exception {
                try {
                    Object[] data = ApiClient.get("/productos", Object[].class);
                    SwingUtilities.invokeLater(() -> {
                        if (data == null || statsInventario == null) return;
                        int bajo = 0;
                        double valor = 0;
                        double valorCosto = 0;

                        DefaultTableModel model = (DefaultTableModel) tableHome.getModel();
                        model.setRowCount(0);

                        for (Object raw : data) {
                            Map<String, Object> p = (Map<String, Object>) raw;
                            Long id = UIUtils.id(p);
                            int qty = UIUtils.num(p, "stockActual");
                            int min = p.get("stockMinimo") != null ? UIUtils.num(p, "stockMinimo") : 5;
                            int opt = p.get("stockOptimo") != null ? UIUtils.num(p, "stockOptimo") : 200;
                            double precioCompra = UIUtils.dbl(p, "precioCompra");
                            double precioVenta = UIUtils.dbl(p, "precioVenta");
                            
                            valor += qty * precioVenta;
                            valorCosto += qty * precioCompra;
                            if (qty <= min) {
                                bajo++;
                            }

                            String estadoLabel = "1".equals(UIUtils.str(p, "estado")) ? "Activo" : "Inactivo";
                            String unidadMedida = UIUtils.str(p, "unidadMedida") != null && !UIUtils.str(p, "unidadMedida").equals("—")
                                    ? UIUtils.str(p, "unidadMedida")
                                    : "Unidad";

                            model.addRow(new Object[] {
                                    id,
                                    UIUtils.str(p, "codigoBarras"),
                                    UIUtils.str(p, "nombre"),
                                    unidadMedida,
                                    new StockData(qty, min, opt),
                                    estadoLabel
                            });
                        }
                        double finalValor = valor;
                        double finalCosto = valorCosto;
                        double finalUtilidad = valor - valorCosto;
                        int finalBajo = bajo;

                        NumberFormat nf = NumberFormat.getNumberInstance(Locale.GERMANY);
                        nf.setMaximumFractionDigits(0);

                        UIUtils.updateStatCard((JPanel) statsInventario.getComponents()[0], String.valueOf(data.length));
                        UIUtils.updateStatCard((JPanel) statsInventario.getComponents()[1], String.valueOf(finalBajo));
                        UIUtils.updateStatCard((JPanel) statsInventario.getComponents()[2], "$" + nf.format(finalValor));
                        UIUtils.updateStatCard((JPanel) statsInventario.getComponents()[3], "$" + nf.format(finalCosto));
                        UIUtils.updateStatCard((JPanel) statsInventario.getComponents()[4], "$" + nf.format(finalUtilidad));

                        parent.updateAlertasPulsing(bajo);
                    });
                } catch (Exception e) {
                }
                return null;
            }
        }.execute();
    }
}
