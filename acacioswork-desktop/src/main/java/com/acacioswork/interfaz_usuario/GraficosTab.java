package com.acacioswork.interfaz_usuario;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.GridLayout;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.SwingWorker;
import javax.swing.border.EmptyBorder;
import com.acacioswork.util.ApiClient;

/**
 * Pestaña modular de visualización de gráficos estadísticos del sistema.
 * Hospeda el gráfico lineal de ganancias mensuales y el gráfico de barras por categoría.
 * @author RADJ
 */
public class GraficosTab extends JPanel {
    private final VentasChartPanel chartPanel;
    private final CategoriasChartPanel categoriasChartPanel;

    public GraficosTab() {
        setLayout(new BorderLayout(0, 16));
        setBackground(Administrador.BG_DARK);
        setBorder(new EmptyBorder(24, 28, 24, 28));

        // Encabezado
        add(UIUtils.buildSectionHeader("Gráficos Estadísticos", "Visualización del rendimiento y ventas por categorías", (JButton) null),
                BorderLayout.NORTH);

        // Contenedor de gráficos
        JPanel chartsContainer = new JPanel(new GridLayout(2, 1, 0, 16));
        chartsContainer.setOpaque(false);

        chartPanel = new VentasChartPanel();
        chartPanel.setPreferredSize(new Dimension(0, 320));
        chartsContainer.add(chartPanel);

        categoriasChartPanel = new CategoriasChartPanel();
        categoriasChartPanel.setPreferredSize(new Dimension(0, 360));
        chartsContainer.add(categoriasChartPanel);

        // Envolver en ScrollPane para pantallas pequeñas
        JScrollPane scroll = new JScrollPane(chartsContainer);
        scroll.setBorder(BorderFactory.createEmptyBorder());
        scroll.setOpaque(false);
        scroll.getViewport().setOpaque(false);

        add(scroll, BorderLayout.CENTER);
    }

    public void refresh() {
        refreshReportesChart();
        categoriasChartPanel.loadData();
    }

    private void refreshReportesChart() {
        if (chartPanel == null) return;
        new SwingWorker<double[], Void>() {
            @SuppressWarnings("unchecked")
            @Override
            protected double[] doInBackground() throws Exception {
                try {
                    Object[] ventasRaw = ApiClient.get("/ventas", Object[].class);
                    Object[] prodRaw = ApiClient.get("/productos", Object[].class);
                    double[] monthlyData = new double[12];
                    int currentYear = LocalDate.now().getYear();

                    Map<Long, Map<String, Object>> prodMap = new HashMap<>();
                    if (prodRaw != null) {
                        for (Object pr : prodRaw) {
                            Map<String, Object> p = (Map<String, Object>) pr;
                            long pid = ((Number) p.get("id")).longValue();
                            prodMap.put(pid, p);
                        }
                    }

                    if (ventasRaw != null) {
                        for (Object raw : ventasRaw) {
                            Map<String, Object> v = (Map<String, Object>) raw;
                            String fechaRaw = (String) v.get("fechaHora");
                            if (fechaRaw != null) {
                                LocalDateTime ldt = LocalDateTime.parse(fechaRaw);
                                if (ldt.getYear() == currentYear) {
                                    int mes = ldt.getMonthValue() - 1;
                                    double total = UIUtils.dbl(v, "valorTotal");
                                    if (total == 0.0 && v.get("detalles") != null) {
                                        List<?> detalles = (List<?>) v.get("detalles");
                                        for (Object detRaw : detalles) {
                                            Map<String, Object> d = (Map<String, Object>) detRaw;
                                            total += UIUtils.dbl(d, "subtotal");
                                        }
                                    }

                                    double cost = 0.0;
                                    if (v.get("detalles") != null) {
                                        List<?> detalles = (List<?>) v.get("detalles");
                                        for (Object detRaw : detalles) {
                                            Map<String, Object> d = (Map<String, Object>) detRaw;
                                            long pid = ((Number) d.get("idProducto")).longValue();
                                            double precioCompra = 0.0;
                                            if (prodMap.containsKey(pid)) {
                                                precioCompra = UIUtils.dbl(prodMap.get(pid), "precioCompra");
                                            }
                                            int cantidad = ((Number) d.get("cantidad")).intValue();
                                            cost += cantidad * precioCompra;
                                        }
                                    }
                                    monthlyData[mes] += (total - cost);
                                }
                            }
                        }
                    }
                    return monthlyData;
                } catch (Exception e) {
                    e.printStackTrace();
                    return new double[12];
                }
            }

            @Override
            protected void done() {
                try {
                    double[] res = get();
                    if (chartPanel != null) {
                        chartPanel.setSalesData(res);
                    }
                } catch (Exception e) {
                }
            }
        }.execute();
    }
}
