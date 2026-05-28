package com.acacioswork.interfaz_usuario;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.GridLayout;
import java.awt.RenderingHints;
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
 * Panel de gráfico personalizado que muestra las Ventas por Categoría de Producto.
 * Permite filtrar por mes y año con inputs estilizados de fondo oscuro y letras blancas.
 * 
 * Requisitos:
 * 1. Las barras son horizontales.
 * 2. Eje Y muestra las categorías ordenadas de mayor a menor ventas generadas.
 * 3. Eje X representa las unidades vendidas.
 * 4. Etiquetas de datos al final de cada barra en formato: "N und.  $ X Ganancia"
 * 
 * @author RADJ
 */
public class CategoriasChartPanel extends JPanel {

    private static final Color BG_CARD = Administrador.BG_CARD;
    private static final Color TEXT_MAIN = Administrador.TEXT_MAIN;
    private static final Color TEXT_MUTED = Administrador.TEXT_MUTED;
    private static final Color ACCENT = Administrador.ACCENT; // Verde para ganancias

    private static final String[] NOMBRES_MESES = {
        "", "Enero", "Febrero", "Marzo", "Abril", "Mayo", "Junio",
        "Julio", "Agosto", "Septiembre", "Octubre", "Noviembre", "Diciembre"
    };

    private JComboBox<String> comboMes;
    private JTextField txtAno;
    private JLabel lblResumen;
    private ChartCanvas chartCanvas;

    // Cache de datos de la API
    private List<Venta> cachedVentas = new ArrayList<>();
    private Map<Long, Producto> cachedProductos = new HashMap<>();
    private List<Categoria> cachedCategorias = new ArrayList<>();

    // Datos filtrados para dibujar
    private List<CategoryStat> statsList = new ArrayList<>();
    private int totalUnidadesPeriodo = 0;
    private double totalGananciaPeriodo = 0.0;

    /**
     * Clase auxiliar para guardar las estadísticas por categoría.
     * @author RADJ
     */
    private static class CategoryStat implements Comparable<CategoryStat> {
        String nombre;
        int unidades;
        double ganancia;

        public CategoryStat(String nombre, int unidades, double ganancia) {
            this.nombre = nombre;
            this.unidades = unidades;
            this.ganancia = ganancia;
        }

        @Override
        public int compareTo(CategoryStat o) {
            // Ordenar de mayor a menor unidades vendidas
            return Integer.compare(o.unidades, this.unidades);
        }
    }

    /**
     * Constructor del panel. Configura la UI y la carga de datos.
     * @author RADJ
     */
    public CategoriasChartPanel() {
        setBackground(BG_CARD);
        setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(255, 255, 255, 13), 1),
                new EmptyBorder(16, 20, 16, 20)));
        setLayout(new BorderLayout(0, 12));

        buildHeader();
        buildChartArea();
        
        // Iniciar la carga de datos en segundo plano
        loadData();
    }

    /**
     * Construye la parte superior con los filtros y la etiqueta del resumen.
     * @author RADJ
     */
    private void buildHeader() {
        JPanel headerPanel = new JPanel(new BorderLayout(0, 8));
        headerPanel.setOpaque(false);

        // Fila superior: Filtros (Mes y Año) alineados a la izquierda
        JPanel filtersRow = new JPanel(new FlowLayout(FlowLayout.LEFT, 12, 0));
        filtersRow.setOpaque(false);

        // Etiqueta y combo de Mes
        JLabel lblMes = new JLabel("Mes:");
        lblMes.setForeground(TEXT_MUTED);
        lblMes.setFont(new Font("Inter", Font.BOLD, 12));
        filtersRow.add(lblMes);

        String[] comboItems = new String[12];
        for (int i = 1; i <= 12; i++) {
            comboItems[i - 1] = NOMBRES_MESES[i];
        }
        comboMes = new JComboBox<>(comboItems);
        comboMes.setBackground(new Color(15, 23, 42)); // Fondo negro/oscuro
        comboMes.setForeground(Color.WHITE); // Letras blancas
        comboMes.setFont(new Font("Inter", Font.PLAIN, 12));
        comboMes.setUI(new BasicComboBoxUI() {
            @Override
            protected javax.swing.JButton createArrowButton() {
                javax.swing.JButton btn = super.createArrowButton();
                btn.setBackground(new Color(15, 23, 42));
                btn.setBorder(BorderFactory.createEmptyBorder());
                return btn;
            }
        });
        comboMes.setBorder(BorderFactory.createLineBorder(new Color(255, 255, 255, 20), 1));
        comboMes.addActionListener(e -> processAndFilterData());
        filtersRow.add(comboMes);

        // Etiqueta e input de Año
        JLabel lblAno = new JLabel("Año:");
        lblAno.setForeground(TEXT_MUTED);
        lblAno.setFont(new Font("Inter", Font.BOLD, 12));
        filtersRow.add(lblAno);

        txtAno = new JTextField(6);
        txtAno.setBackground(new Color(15, 23, 42)); // Fondo negro/oscuro
        txtAno.setForeground(Color.WHITE); // Letras blancas
        txtAno.setCaretColor(Color.WHITE);
        txtAno.setFont(new Font("Inter", Font.PLAIN, 12));
        txtAno.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(255, 255, 255, 20), 1),
                BorderFactory.createEmptyBorder(2, 6, 2, 6)));
        
        // Poner año actual
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

        // Fila inferior: Título "RESUMEN DEL PERIODO SELECCIONADO" y el texto del resumen
        JPanel resumenPanel = new JPanel(new GridLayout(2, 1, 2, 2));
        resumenPanel.setOpaque(false);
        resumenPanel.setBorder(new EmptyBorder(8, 4, 0, 0));

        JLabel lblTituloResumen = new JLabel("RESUMEN DEL PERÍODO SELECCIONADO");
        lblTituloResumen.setForeground(TEXT_MAIN);
        lblTituloResumen.setFont(new Font("Inter", Font.BOLD, 11));
        resumenPanel.add(lblTituloResumen);

        lblResumen = new JLabel("Cargando datos...");
        lblResumen.setForeground(TEXT_MUTED);
        lblResumen.setFont(new Font("Inter", Font.PLAIN, 12));
        resumenPanel.add(lblResumen);

        headerPanel.add(resumenPanel, BorderLayout.SOUTH);

        add(headerPanel, BorderLayout.NORTH);
    }

    /**
     * Construye el lienzo del gráfico.
     * @author RADJ
     */
    private void buildChartArea() {
        chartCanvas = new ChartCanvas();
        add(chartCanvas, BorderLayout.CENTER);
    }

    /**
     * Mapea un Map a un modelo de Venta de forma segura sin depender del deserializador LocalDateTime de Jackson.
     * @author RADJ
     */
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

    /**
     * Mapea un Map a un modelo de Producto de forma segura.
     * @author RADJ
     */
    private Producto mapToProducto(Map<String, Object> m) {
        Producto p = new Producto();
        p.setId(m.get("id") != null ? ((Number) m.get("id")).longValue() : null);
        p.setNombre((String) m.get("nombre"));
        p.setPrecioCompra(m.get("precioCompra") != null ? ((Number) m.get("precioCompra")).doubleValue() : 0.0);
        p.setPrecioVenta(m.get("precioVenta") != null ? ((Number) m.get("precioVenta")).doubleValue() : 0.0);
        p.setIva(m.get("iva") != null ? ((Number) m.get("iva")).doubleValue() : 0.0);
        p.setIdCategoria(m.get("idCategoria") != null ? ((Number) m.get("idCategoria")).longValue() : null);
        p.setIdProveedor(m.get("idProveedor") != null ? ((Number) m.get("idProveedor")).longValue() : null);
        p.setStockActual(m.get("stockActual") != null ? ((Number) m.get("stockActual")).intValue() : 0);
        p.setStockMinimo(m.get("stockMinimo") != null ? ((Number) m.get("stockMinimo")).intValue() : 0);
        p.setStockOptimo(m.get("stockOptimo") != null ? ((Number) m.get("stockOptimo")).intValue() : 200);
        p.setUnidadMedida(m.get("unidadMedida") != null ? m.get("unidadMedida").toString() : "Unidad");
        return p;
    }

    /**
     * Mapea un Map a un modelo de Categoría de forma segura.
     * @author RADJ
     */
    private Categoria mapToCategoria(Map<String, Object> m) {
        Categoria c = new Categoria();
        c.setId(m.get("id") != null ? ((Number) m.get("id")).longValue() : null);
        c.setNombre((String) m.get("nombre"));
        return c;
    }

    /**
     * Carga de forma asíncrona todos los datos necesarios desde la API.
     * @author RADJ
     */
    public void loadData() {
        new SwingWorker<Void, Void>() {
            @Override
            @SuppressWarnings("unchecked")
            protected Void doInBackground() throws Exception {
                try {
                    // Obtener datos crudos como arreglos de objetos genéricos (JSON Maps)
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

    /**
     * Filtra y agrupa las ventas según los filtros seleccionados, luego repinta el canvas.
     * @author RADJ
     */
    private void processAndFilterData() {
        if (comboMes == null || txtAno == null || lblResumen == null) return;

        int mesSeleccionado = comboMes.getSelectedIndex() + 1;
        int anoSeleccionado;
        try {
            anoSeleccionado = Integer.parseInt(txtAno.getText().trim());
        } catch (NumberFormatException e) {
            return; // No filtrar si el año no es un número válido
        }

        // Acumuladores
        Map<Long, CategoryStat> statsMap = new HashMap<>();
        for (Categoria c : cachedCategorias) {
            statsMap.put(c.getId(), new CategoryStat(c.getNombre(), 0, 0.0));
        }

        totalUnidadesPeriodo = 0;
        totalGananciaPeriodo = 0.0;

        for (Venta v : cachedVentas) {
            if (v.getFechaHora() == null) continue;

            // Evitar problemas de zona horaria parseando el LocalDateTime directamente
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

        // Convertir a lista y ordenar
        statsList.clear();
        for (CategoryStat stat : statsMap.values()) {
            if (stat.unidades > 0) {
                statsList.add(stat);
            }
        }
        Collections.sort(statsList);

        // Actualizar resumen
        NumberFormat nfUnd = NumberFormat.getNumberInstance(Locale.GERMANY);
        NumberFormat nfGan = NumberFormat.getNumberInstance(Locale.GERMANY);
        nfGan.setMaximumFractionDigits(0);

        String mesStr = NOMBRES_MESES[mesSeleccionado];
        lblResumen.setText(String.format("Total Unidades Vendidas en %s %d = %s Unidades  |  Ganancia Total: $ %s",
                mesStr, anoSeleccionado, nfUnd.format(totalUnidadesPeriodo), nfGan.format(totalGananciaPeriodo)));

        // Repintar gráfico
        chartCanvas.repaint();
    }

    /**
     * Lienzo personalizado que dibuja el gráfico de barras horizontales.
     * @author RADJ
     */
    private class ChartCanvas extends JPanel {

        public ChartCanvas() {
            setOpaque(false);
        }

        @Override
        protected void paintComponent(Graphics g) {
            super.paintComponent(g);
            Graphics2D g2 = (Graphics2D) g.create();
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

            int width = getWidth();
            int height = getHeight();

            // Dibujar título del gráfico
            g2.setFont(new Font("Inter", Font.BOLD, 14));
            g2.setColor(TEXT_MAIN);
            g2.drawString("📊 Ventas por Categoría de Producto", 10, 24);

            if (statsList.isEmpty()) {
                g2.setFont(new Font("Inter", Font.PLAIN, 12));
                g2.setColor(TEXT_MUTED);
                g2.drawString("Sin datos de ventas para el período seleccionado.", 20, height / 2);
                g2.dispose();
                return;
            }

            int paddingLeft = 140;
            int paddingRight = 240; // Espacio para las etiquetas del final de barra
            int paddingTop = 50;
            int paddingBottom = 20;

            int graphWidth = width - paddingLeft - paddingRight;
            int graphHeight = height - paddingTop - paddingBottom;

            if (graphWidth <= 50 || graphHeight <= 50) {
                g2.dispose();
                return;
            }

            // Encontrar el valor máximo de unidades para escalar
            int maxUnidades = 0;
            for (CategoryStat stat : statsList) {
                if (stat.unidades > maxUnidades) {
                    maxUnidades = stat.unidades;
                }
            }
            if (maxUnidades == 0) maxUnidades = 1;

            // Paleta de colores para las barras
            Color[] palette = {
                new Color(99, 102, 241),   // Indigo
                new Color(139, 92, 246),  // Violet
                new Color(59, 130, 246),  // Blue
                new Color(16, 185, 129),  // Emerald
                new Color(245, 158, 11),  // Amber
                new Color(239, 68, 68)    // Red
            };

            int numItems = statsList.size();
            int barHeight = Math.min(28, graphHeight / (numItems * 2));
            int gap = (graphHeight - (barHeight * numItems)) / (numItems + 1);
            if (gap < 4) gap = 4;

            NumberFormat nfUnd = NumberFormat.getNumberInstance(Locale.GERMANY);
            NumberFormat nfGan = NumberFormat.getNumberInstance(Locale.GERMANY);
            nfGan.setMaximumFractionDigits(0);

            for (int i = 0; i < numItems; i++) {
                CategoryStat stat = statsList.get(i);
                int y = paddingTop + gap + i * (barHeight + gap);

                // 1. Dibujar nombre de la categoría (alineado a la derecha en el paddingLeft)
                g2.setFont(new Font("Inter", Font.BOLD, 11));
                g2.setColor(TEXT_MAIN);
                int strWidth = g2.getFontMetrics().stringWidth(stat.nombre);
                g2.drawString(stat.nombre, paddingLeft - 15 - strWidth, y + (barHeight / 2) + 4);

                // 2. Calcular ancho de la barra
                int barWidth = (int) (((double) stat.unidades / maxUnidades) * graphWidth);
                if (barWidth < 4) barWidth = 4; // Ancho mínimo visible

                // 3. Dibujar la barra redondeada
                g2.setColor(palette[i % palette.length]);
                g2.fillRoundRect(paddingLeft, y, barWidth, barHeight, 8, 8);

                // 4. Dibujar etiquetas de datos al final de la barra: "N und.  $ X Ganancia"
                int textX = paddingLeft + barWidth + 10;
                
                // Unidades en gris claro
                g2.setFont(new Font("Inter", Font.BOLD, 11));
                g2.setColor(TEXT_MUTED);
                String undStr = nfUnd.format(stat.unidades) + " und.  ";
                g2.drawString(undStr, textX, y + (barHeight / 2) + 4);

                // Ganancia en verde
                int undWidth = g2.getFontMetrics().stringWidth(undStr);
                g2.setColor(ACCENT);
                String ganStr = "$ " + nfGan.format(stat.ganancia) + " Ganancia";
                g2.drawString(ganStr, textX + undWidth, y + (barHeight / 2) + 4);
            }

            // Dibujar línea del eje Y
            g2.setColor(new Color(255, 255, 255, 20));
            g2.drawLine(paddingLeft, paddingTop, paddingLeft, paddingTop + graphHeight);

            g2.dispose();
        }
    }
}
