package com.acacioswork.interfaz_usuario;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.GridLayout;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.text.NumberFormat;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.SwingWorker;
import javax.swing.border.EmptyBorder;
import javax.swing.plaf.basic.BasicComboBoxUI;
import com.acacioswork.model.Categoria;
import com.acacioswork.model.DetalleVenta;
import com.acacioswork.model.Producto;
import com.acacioswork.model.Venta;
import com.acacioswork.util.ApiClient;

/**
 * Panel de gráfico personalizado que gestiona los filtros y los datos de ventas por categoría.
 * @author RADJ
 */
public class CategoriasChartPanel extends JPanel {
    private static final String[] NOMBRES_MESES = {
        "", "Enero", "Febrero", "Marzo", "Abril", "Mayo", "Junio",
        "Julio", "Agosto", "Septiembre", "Octubre", "Noviembre", "Diciembre"
    };

    private JComboBox<String> comboMes;
    private JTextField txtAno;
    private JLabel lblResumen;
    private CategoriasChartCanvas chartCanvas;

    private final List<Venta> cachedVentas = new ArrayList<>();
    private final Map<Long, Producto> cachedProductos = new HashMap<>();
    private final List<Categoria> cachedCategorias = new ArrayList<>();

    private final List<CategoryStat> statsList = new ArrayList<>();
    private int totalUnidadesPeriodo = 0;
    private double totalGananciaPeriodo = 0.0;

    public static class CategoryStat implements Comparable<CategoryStat> {
        public final String nombre;
        public int unidades;
        public double ganancia;

        public CategoryStat(String nombre, int unidades, double ganancia) {
            this.nombre = nombre;
            this.unidades = unidades;
            this.ganancia = ganancia;
        }

        @Override
        public int compareTo(CategoryStat o) {
            return Integer.compare(o.unidades, this.unidades);
        }
    }

    public CategoriasChartPanel() {
        setBackground(Administrador.BG_CARD);
        setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(255, 255, 255, 13), 1),
                new EmptyBorder(16, 20, 16, 20)));
        setLayout(new BorderLayout(0, 12));

        buildHeader();
        buildChartArea();
        loadData();
    }

    public List<CategoryStat> getStatsList() {
        return statsList;
    }

    private void buildHeader() {
        JPanel headerPanel = new JPanel(new BorderLayout(0, 8));
        headerPanel.setOpaque(false);

        JPanel filtersRow = new JPanel(new FlowLayout(FlowLayout.LEFT, 12, 0));
        filtersRow.setOpaque(false);

        JLabel lblMes = new JLabel("Mes:");
        lblMes.setForeground(Administrador.TEXT_MUTED);
        lblMes.setFont(new Font("Inter", Font.BOLD, 12));
        filtersRow.add(lblMes);

        String[] comboItems = new String[12];
        for (int i = 1; i <= 12; i++) {
            comboItems[i - 1] = NOMBRES_MESES[i];
        }
        comboMes = new JComboBox<>(comboItems);
        comboMes.setBackground(new Color(15, 23, 42));
        comboMes.setForeground(Color.WHITE);
        comboMes.setFont(new Font("Inter", Font.PLAIN, 12));
        comboMes.setUI(new BasicComboBoxUI() {
            @Override
            protected JButton createArrowButton() {
                JButton btn = super.createArrowButton();
                btn.setBackground(new Color(15, 23, 42));
                btn.setBorder(BorderFactory.createEmptyBorder());
                return btn;
            }
        });
        comboMes.setBorder(BorderFactory.createLineBorder(new Color(255, 255, 255, 20), 1));
        comboMes.addActionListener(e -> processAndFilterData());
        filtersRow.add(comboMes);

        JLabel lblAno = new JLabel("Año:");
        lblAno.setForeground(Administrador.TEXT_MUTED);
        lblAno.setFont(new Font("Inter", Font.BOLD, 12));
        filtersRow.add(lblAno);

        txtAno = new JTextField(6);
        txtAno.setBackground(new Color(15, 23, 42));
        txtAno.setForeground(Color.WHITE);
        txtAno.setCaretColor(Color.WHITE);
        txtAno.setFont(new Font("Inter", Font.PLAIN, 12));
        txtAno.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(255, 255, 255, 20), 1),
                BorderFactory.createEmptyBorder(2, 6, 2, 6)));

        LocalDate now = LocalDate.now();
        txtAno.setText(String.valueOf(now.getYear()));
        comboMes.setSelectedIndex(now.getMonthValue() - 1);

        txtAno.addKeyListener(new KeyAdapter() {
            @Override
            public void keyReleased(KeyEvent e) {
                processAndFilterData();
            }
        });
        filtersRow.add(txtAno);
        headerPanel.add(filtersRow, BorderLayout.WEST);

        JPanel resumenPanel = new JPanel(new GridLayout(2, 1, 2, 2));
        resumenPanel.setOpaque(false);
        resumenPanel.setBorder(new EmptyBorder(8, 4, 0, 0));

        JLabel lblTituloResumen = new JLabel("RESUMEN DEL PERÍODO SELECCIONADO");
        lblTituloResumen.setForeground(Administrador.TEXT_MAIN);
        lblTituloResumen.setFont(new Font("Inter", Font.BOLD, 11));
        resumenPanel.add(lblTituloResumen);

        lblResumen = new JLabel("Cargando datos...");
        lblResumen.setForeground(Administrador.TEXT_MUTED);
        lblResumen.setFont(new Font("Inter", Font.PLAIN, 12));
        resumenPanel.add(lblResumen);

        headerPanel.add(resumenPanel, BorderLayout.SOUTH);
        add(headerPanel, BorderLayout.NORTH);
    }

    private void buildChartArea() {
        chartCanvas = new CategoriasChartCanvas(this);
        add(chartCanvas, BorderLayout.CENTER);
    }

    public void loadData() {
        new SwingWorker<Void, Void>() {
            @SuppressWarnings("unchecked")
            @Override
            protected Void doInBackground() throws Exception {
                try {
                    Object[] ventasRaw = ApiClient.get("/ventas", Object[].class);
                    Object[] productosRaw = ApiClient.get("/productos", Object[].class);
                    Object[] categoriasRaw = ApiClient.get("/categorias", Object[].class);

                    cachedVentas.clear();
                    if (ventasRaw != null) {
                        for (Object o : ventasRaw) {
                            if (o instanceof Map) {
                                cachedVentas.add(mapToVenta((Map<String, Object>) o));
                            }
                        }
                    }

                    cachedProductos.clear();
                    if (productosRaw != null) {
                        for (Object o : productosRaw) {
                            if (o instanceof Map) {
                                Producto p = mapToProducto((Map<String, Object>) o);
                                cachedProductos.put(p.getId(), p);
                            }
                        }
                    }

                    cachedCategorias.clear();
                    if (categoriasRaw != null) {
                        for (Object o : categoriasRaw) {
                            if (o instanceof Map) {
                                cachedCategorias.add(mapToCategoria((Map<String, Object>) o));
                            }
                        }
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
                return null;
            }

            @Override
            protected void done() {
                processAndFilterData();
            }
        }.execute();
    }

    private void processAndFilterData() {
        if (comboMes == null || txtAno == null || lblResumen == null) return;

        int mesSeleccionado = comboMes.getSelectedIndex() + 1;
        int anoSeleccionado;
        try {
            anoSeleccionado = Integer.parseInt(txtAno.getText().trim());
        } catch (NumberFormatException e) {
            return;
        }

        Map<Long, CategoryStat> statsMap = new HashMap<>();
        for (Categoria c : cachedCategorias) {
            statsMap.put(c.getId(), new CategoryStat(c.getNombre(), 0, 0.0));
        }

        totalUnidadesPeriodo = 0;
        totalGananciaPeriodo = 0.0;

        for (Venta v : cachedVentas) {
            if (v.getFechaHora() == null) continue;

            int ano = v.getFechaHora().getYear();
            int mes = v.getFechaHora().getMonthValue();

            if (ano == anoSeleccionado && mes == mesSeleccionado) {
                List<DetalleVenta> detalles = v.getDetalles();
                if (detalles != null) {
                    for (DetalleVenta d : detalles) {
                        Producto prod = cachedProductos.get(d.getIdProducto());
                        if (prod != null) {
                            Long cid = prod.getIdCategoria();
                            if (cid != null) {
                                CategoryStat stat = statsMap.get(cid);
                                if (stat == null) {
                                    stat = new CategoryStat("Categoría #" + cid, 0, 0.0);
                                    statsMap.put(cid, stat);
                                }

                                int cantidad = d.getCantidad() != null ? d.getCantidad() : 0;
                                double precioVenta = d.getPrecioUnitario() != null ? d.getPrecioUnitario() : 0.0;
                                double precioCompra = prod.getPrecioCompra();

                                stat.unidades += cantidad;
                                stat.ganancia += (precioVenta - precioCompra) * cantidad;

                                totalUnidadesPeriodo += cantidad;
                                totalGananciaPeriodo += (precioVenta - precioCompra) * cantidad;
                            }
                        }
                    }
                }
            }
        }

        statsList.clear();
        for (CategoryStat stat : statsMap.values()) {
            if (stat.unidades > 0) {
                statsList.add(stat);
            }
        }
        Collections.sort(statsList);

        NumberFormat nfUnd = NumberFormat.getNumberInstance(Locale.GERMANY);
        NumberFormat nfGan = NumberFormat.getNumberInstance(Locale.GERMANY);
        nfGan.setMaximumFractionDigits(0);

        String mesStr = NOMBRES_MESES[mesSeleccionado];
        lblResumen.setText(String.format("Total Unidades Vendidas en %s %d = %s Unidades  |  Ganancia Total: $ %s",
                mesStr, anoSeleccionado, nfUnd.format(totalUnidadesPeriodo), nfGan.format(totalGananciaPeriodo)));

        chartCanvas.repaint();
    }

    @SuppressWarnings("unchecked")
    private Venta mapToVenta(Map<String, Object> m) {
        Venta v = new Venta();
        v.setId(m.get("id") != null ? ((Number) m.get("id")).longValue() : null);
        String fechaStr = (String) m.get("fechaHora");
        if (fechaStr != null && !fechaStr.isBlank()) {
            try {
                v.setFechaHora(java.time.LocalDateTime.parse(fechaStr));
            } catch (Exception e) {
            }
        }
        v.setIdCliente(m.get("idCliente") != null ? ((Number) m.get("idCliente")).longValue() : null);
        v.setIdUsuario(m.get("idUsuario") != null ? ((Number) m.get("idUsuario")).longValue() : null);
        v.setValorTotal(m.get("valorTotal") != null ? ((Number) m.get("valorTotal")).doubleValue() : 0.0);
        
        List<DetalleVenta> detalles = new ArrayList<>();
        if (m.get("detalles") instanceof List) {
            List<?> rawDetalles = (List<?>) m.get("detalles");
            for (Object dObj : rawDetalles) {
                if (dObj instanceof Map) {
                    Map<String, Object> dm = (Map<String, Object>) dObj;
                    DetalleVenta d = new DetalleVenta();
                    d.setId(dm.get("id") != null ? ((Number) dm.get("id")).longValue() : null);
                    d.setIdProducto(dm.get("idProducto") != null ? ((Number) dm.get("idProducto")).longValue() : null);
                    d.setCantidad(dm.get("cantidad") != null ? ((Number) dm.get("cantidad")).intValue() : null);
                    d.setPrecioUnitario(dm.get("precioUnitario") != null ? ((Number) dm.get("precioUnitario")).doubleValue() : null);
                    d.setSubtotal(dm.get("subtotal") != null ? ((Number) dm.get("subtotal")).doubleValue() : null);
                    detalles.add(d);
                }
            }
        }
        v.setDetalles(detalles);
        return v;
    }

    private Producto mapToProducto(Map<String, Object> m) {
        Producto p = new Producto();
        p.setId(m.get("id") != null ? ((Number) m.get("id")).longValue() : null);
        p.setNombre((String) m.get("nombre"));
        p.setPrecioCompra(m.get("precioCompra") != null ? ((Number) m.get("precioCompra")).doubleValue() : 0.0);
        p.setPrecioVenta(m.get("precioVenta") != null ? ((Number) m.get("precioVenta")).doubleValue() : 0.0);
        p.setIdCategoria(m.get("idCategoria") != null ? ((Number) m.get("idCategoria")).longValue() : null);
        return p;
    }

    private Categoria mapToCategoria(Map<String, Object> m) {
        Categoria c = new Categoria();
        c.setId(m.get("id") != null ? ((Number) m.get("id")).longValue() : null);
        c.setNombre((String) m.get("nombre"));
        return c;
    }
}
