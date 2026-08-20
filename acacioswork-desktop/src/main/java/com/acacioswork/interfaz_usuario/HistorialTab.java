package com.acacioswork.interfaz_usuario;

import java.awt.BorderLayout;
import java.awt.GridLayout;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import javax.swing.JButton;
import javax.swing.JPanel;
import javax.swing.JTable;
import javax.swing.SwingWorker;
import javax.swing.border.EmptyBorder;
import javax.swing.table.DefaultTableModel;
import com.acacioswork.util.ApiClient;
import com.acacioswork.util.ConfiguracionManager;

/**
 * Pestaña modular para la consulta del Historial de Ventas.
 * Permite buscar transacciones históricas y ver consolidados de recaudación.
 * @author RADJ
 */
public class HistorialTab extends JPanel {
    private final JTable table;
    private final JPanel statsPanel;

    public HistorialTab() {
        setLayout(new BorderLayout(0, 16));
        setBackground(Administrador.BG_DARK);
        setBorder(new EmptyBorder(24, 28, 24, 28));

        // Encabezado
        add(UIUtils.buildSectionHeader("📋 Historial de Ventas", "Ventas registradas en el sistema", (JButton) null),
                BorderLayout.NORTH);

        // Tarjetas de estadísticas rápidas
        statsPanel = new JPanel(new GridLayout(1, 2, 16, 0));
        statsPanel.setOpaque(false);
        statsPanel.setBorder(new EmptyBorder(0, 0, 16, 0));
        statsPanel.add(UIUtils.buildStatCard("Total Ventas", "0", Administrador.TEXT_MAIN));
        statsPanel.add(UIUtils.buildStatCard("Total Recaudado", "$0", Administrador.ACCENT));

        // Inicializar tabla de historial
        table = UIUtils.buildStyledTable(new String[] { "# ID", "Fecha", "Cliente", "Productos", "Total" });
        table.getColumnModel().getColumn(0).setPreferredWidth(60);
        table.getColumnModel().getColumn(1).setPreferredWidth(180);
        table.getColumnModel().getColumn(2).setPreferredWidth(250);
        table.getColumnModel().getColumn(3).setPreferredWidth(120);
        table.getColumnModel().getColumn(4).setPreferredWidth(120);

        // Contenedor de la tabla
        JPanel center = new JPanel(new BorderLayout(0, 12));
        center.setOpaque(false);
        center.add(statsPanel, BorderLayout.NORTH);

        JPanel tableContainer = new JPanel(new BorderLayout(0, 8));
        tableContainer.setOpaque(false);
        tableContainer.add(UIUtils.buildSearchPanel(table), BorderLayout.NORTH);
        tableContainer.add(UIUtils.wrapTable(table), BorderLayout.CENTER);

        center.add(tableContainer, BorderLayout.CENTER);
        add(center, BorderLayout.CENTER);
    }

    public void refresh() {
        new SwingWorker<Map<String, Object>, Void>() {
            @SuppressWarnings("unchecked")
            @Override
            protected Map<String, Object> doInBackground() throws Exception {
                Map<String, Object> result = new HashMap<>();
                try {
                    Object[] ventasRaw = ApiClient.get("/ventas", Object[].class);
                    Object[] clientesRaw = ApiClient.get("/clientes", Object[].class);

                    Map<String, String> clientesMap = new HashMap<>();
                    if (clientesRaw != null) {
                        for (Object raw : clientesRaw) {
                            Map<String, Object> c = (Map<String, Object>) raw;
                            clientesMap.put(UIUtils.id(c).toString(), UIUtils.str(c, "nombre"));
                        }
                    }

                    List<Object[]> rows = new ArrayList<>();
                    double totalRecaudado = 0;

                    if (ventasRaw != null) {
                        List<Map<String, Object>> ventasList = new ArrayList<>();
                        for (Object raw : ventasRaw) {
                            ventasList.add((Map<String, Object>) raw);
                        }
                        // Ordenar descendente por fecha
                        ventasList.sort((a, b) -> UIUtils.str(b, "fechaHora").compareTo(UIUtils.str(a, "fechaHora")));

                        for (Map<String, Object> v : ventasList) {
                            double total = UIUtils.dbl(v, "valorTotal");
                            totalRecaudado += total;

                            String fechaRaw = UIUtils.str(v, "fechaHora");
                            String fecha = "—";
                            try {
                                fecha = LocalDateTime.parse(fechaRaw)
                                        .format(DateTimeFormatter.ofPattern("dd/MM/yyyy hh:mm a"));
                            } catch (Exception e) {
                                fecha = fechaRaw;
                            }

                            String cId = v.get("idCliente") != null ? v.get("idCliente").toString() : "";
                            String cliente = cId.isEmpty() ? "Sin cliente" : clientesMap.getOrDefault(cId, "Cliente #" + cId);

                            List<?> detalles = (List<?>) v.get("detalles");
                            int nProd = detalles != null ? detalles.size() : 0;
                            String prodStr = "📦 " + nProd + " producto" + (nProd != 1 ? "s" : "");

                            rows.add(new Object[] {
                                    "#" + UIUtils.id(v),
                                    fecha,
                                    cliente,
                                    prodStr,
                                    ConfiguracionManager.formatCurrency(total)
                            });
                        }
                    }

                    result.put("rows", rows);
                    result.put("totalVentas", ventasRaw != null ? ventasRaw.length : 0);
                    result.put("totalRecaudado", totalRecaudado);
                } catch (Exception e) {
                    e.printStackTrace();
                }
                return result;
            }

            @Override
            @SuppressWarnings("unchecked")
            protected void done() {
                try {
                    Map<String, Object> res = get();
                    DefaultTableModel dtm = (DefaultTableModel) table.getModel();
                    dtm.setRowCount(0);

                    List<Object[]> rows = (List<Object[]>) res.get("rows");
                    if (rows != null) {
                        for (Object[] r : rows) {
                            dtm.addRow(r);
                        }
                    }

                    int totalVentas = (int) res.getOrDefault("totalVentas", 0);
                    double totalRecaudado = (double) res.getOrDefault("totalRecaudado", 0.0);

                    UIUtils.updateStatCard((JPanel) statsPanel.getComponents()[0], String.valueOf(totalVentas));
                    UIUtils.updateStatCard((JPanel) statsPanel.getComponents()[1], ConfiguracionManager.formatCurrency(totalRecaudado));
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }.execute();
    }
}
