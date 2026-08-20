package com.acacioswork.interfaz_usuario;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.*;
import com.acacioswork.model.*;
import com.acacioswork.util.ApiClient;

/**
 * Motor analítico que calcula las respuestas para las Preguntas Inteligentes.
 * Centraliza el acceso de red y optimiza los algoritmos de negocio.
 * @author RADJ
 */
public class IntelligenceEngine {
    public List<Venta> ventas = null;
    public Producto[] productos = null;
    public Proveedor[] proveedores = null;
    public Cliente[] clientes = null;

    public void clearCache() {
        ventas = null;
        productos = null;
        proveedores = null;
        clientes = null;
    }

    public void initCache() throws Exception {
        if (ventas == null) {
            Venta[] v = ApiClient.get("/ventas", Venta[].class);
            ventas = v != null ? Arrays.asList(v) : new ArrayList<>();
        }
        if (productos == null) {
            productos = ApiClient.get("/productos", Producto[].class);
        }
        if (proveedores == null) {
            proveedores = ApiClient.get("/proveedores", Proveedor[].class);
        }
        if (clientes == null) {
            clientes = ApiClient.get("/clientes", Cliente[].class);
        }
    }

    public List<Venta> getVentasPeriodo(int anio, int mes) {
        List<Venta> filtradas = new ArrayList<>();
        if (ventas != null) {
            for (Venta v : ventas) {
                if (v.getFechaHora() != null && v.getFechaHora().getYear() == anio && v.getFechaHora().getMonthValue() == mes) {
                    filtradas.add(v);
                }
            }
        }
        return filtradas;
    }

    public String iqAnalizarRentables(List<Venta> ventasList) {
        if (productos == null || ventasList.isEmpty()) return "No se registraron ventas en este periodo.";
        Map<Long, Producto> prodMap = new HashMap<>();
        for (Producto p : productos) prodMap.put(p.getId(), p);

        Map<Long, Double> gananciaMap = new HashMap<>();
        for (Venta v : ventasList) {
            if (v.getDetalles() == null) continue;
            for (DetalleVenta d : v.getDetalles()) {
                Producto p = prodMap.get(d.getIdProducto());
                if (p != null) {
                    double margen = (d.getPrecioUnitario() - p.getPrecioCompra()) * d.getCantidad();
                    gananciaMap.put(d.getIdProducto(), gananciaMap.getOrDefault(d.getIdProducto(), 0.0) + margen);
                }
            }
        }

        List<Map.Entry<Long, Double>> list = new ArrayList<>(gananciaMap.entrySet());
        list.sort((a, b) -> Double.compare(b.getValue(), a.getValue()));

        if (list.isEmpty()) return "No se registraron ventas en este periodo.";

        StringBuilder sb = new StringBuilder();
        int count = 1;
        for (Map.Entry<Long, Double> entry : list) {
            if (count > 3) break;
            Producto p = prodMap.get(entry.getKey());
            String name = p != null ? p.getNombre() : "Producto #" + entry.getKey();
            sb.append(count).append(". ").append(name)
              .append(" → Ganancia: $").append(String.format("%,.0f", entry.getValue())).append("\n");
            count++;
        }
        return sb.toString().trim();
    }

    public String iqAnalizarBajaRotacion(List<Venta> ventasList) {
        if (productos == null) return "Sin catálogo de productos.";
        Map<Long, Integer> cantidadMap = new HashMap<>();
        for (Producto p : productos) {
            if (p.getEstado() == 1) cantidadMap.put(p.getId(), 0);
        }

        for (Venta v : ventasList) {
            if (v.getDetalles() == null) continue;
            for (DetalleVenta d : v.getDetalles()) {
                if (cantidadMap.containsKey(d.getIdProducto())) {
                    cantidadMap.put(d.getIdProducto(), cantidadMap.get(d.getIdProducto()) + d.getCantidad());
                }
            }
        }

        List<Map.Entry<Long, Integer>> list = new ArrayList<>();
        for (Map.Entry<Long, Integer> entry : cantidadMap.entrySet()) {
            if (entry.getValue() > 0) list.add(entry);
        }
        list.sort(Map.Entry.comparingByValue());

        if (list.isEmpty()) return "No hay productos con ventas en este periodo.";

        Map<Long, Producto> prodMap = new HashMap<>();
        for (Producto p : productos) prodMap.put(p.getId(), p);

        StringBuilder sb = new StringBuilder();
        int count = 1;
        for (Map.Entry<Long, Integer> entry : list) {
            if (count > 3) break;
            Producto p = prodMap.get(entry.getKey());
            String name = p != null ? p.getNombre() : "Producto #" + entry.getKey();
            sb.append(count).append(". ").append(name)
              .append(" → Solo ").append(entry.getValue()).append(" uds vendidas\n");
            count++;
        }
        return sb.toString().trim();
    }

    public String iqAnalizarReabastecer() {
        if (productos == null) return "Sin catálogo de productos.";
        List<Producto> ranking = Arrays.stream(productos)
                .filter(p -> p.getEstado() == 1 && p.getStockActual() <= (p.getStockMinimo() != null ? p.getStockMinimo() : 5))
                .sorted((a, b) -> {
                    int minA = a.getStockMinimo() != null ? a.getStockMinimo() : 5;
                    int minB = b.getStockMinimo() != null ? b.getStockMinimo() : 5;
                    return Integer.compare(a.getStockActual() - minA, b.getStockActual() - minB);
                })
                .limit(3)
                .toList();

        if (ranking.isEmpty()) return "¡Excelente! Todos los productos tienen stock suficiente.";

        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < ranking.size(); i++) {
            Producto p = ranking.get(i);
            int min = p.getStockMinimo() != null ? p.getStockMinimo() : 5;
            sb.append(i + 1).append(". ").append(p.getNombre())
              .append(" → Stock: ").append(p.getStockActual()).append(" uds (mín: ").append(min).append(")\n");
        }
        return sb.toString().trim();
    }

    public String iqAnalizarProveedorCaro() {
        if (productos == null || proveedores == null) return "Sin datos suficientes.";
        class Stats { double total = 0; int count = 0; }
        Map<Long, Stats> provStats = new HashMap<>();
        for (Producto p : productos) {
            if (p.getIdProveedor() != null) {
                Stats s = provStats.computeIfAbsent(p.getIdProveedor(), k -> new Stats());
                s.total += p.getPrecioCompra();
                s.count++;
            }
        }

        Map<Long, Proveedor> provMap = new HashMap<>();
        for (Proveedor pr : proveedores) provMap.put(pr.getId(), pr);

        List<Map.Entry<Long, Stats>> list = new ArrayList<>(provStats.entrySet());
        list.sort((a, b) -> Double.compare(b.getValue().total / b.getValue().count, a.getValue().total / a.getValue().count));

        if (list.isEmpty()) return "No hay proveedores con productos asignados.";

        StringBuilder sb = new StringBuilder();
        int count = 1;
        for (Map.Entry<Long, Stats> entry : list) {
            if (count > 3) break;
            Proveedor pr = provMap.get(entry.getKey());
            String name = pr != null ? pr.getNombre() : "Proveedor #" + entry.getKey();
            double avg = entry.getValue().total / entry.getValue().count;
            sb.append(count).append(". ").append(name)
              .append(" → Promedio: $").append(String.format("%,.0f", avg)).append("\n");
            count++;
        }
        return sb.toString().trim();
    }

    public String iqAnalizarTopClientes(List<Venta> ventasList) {
        if (clientes == null || ventasList.isEmpty()) return "No hay compras con cliente asignado en este periodo.";
        Map<Long, Double> compraMap = new HashMap<>();
        for (Venta v : ventasList) {
            if (v.getIdCliente() != null) {
                compraMap.put(v.getIdCliente(), compraMap.getOrDefault(v.getIdCliente(), 0.0) + v.getValorTotal());
            }
        }

        Map<Long, Cliente> cliMap = new HashMap<>();
        for (Cliente c : clientes) cliMap.put(c.getId(), c);

        List<Map.Entry<Long, Double>> list = new ArrayList<>(compraMap.entrySet());
        list.sort((a, b) -> Double.compare(b.getValue(), a.getValue()));

        if (list.isEmpty()) return "No hay ventas con cliente asignado en este periodo.";

        StringBuilder sb = new StringBuilder();
        int count = 1;
        for (Map.Entry<Long, Double> entry : list) {
            if (count > 3) break;
            Cliente c = cliMap.get(entry.getKey());
            String name = c != null ? c.getNombre() : "Cliente #" + entry.getKey();
            sb.append(count).append(". ").append(name)
              .append(" → Total: $").append(String.format("%,.0f", entry.getValue())).append("\n");
            count++;
        }
        return sb.toString().trim();
    }

    public String iqAnalizarMejorMes() {
        if (productos == null || ventas == null || ventas.isEmpty()) return "No hay historial de ventas para analizar.";
        Map<Long, Producto> prodMap = new HashMap<>();
        for (Producto p : productos) prodMap.put(p.getId(), p);

        class MesGanancia {
            final String label;
            double ganancia = 0;
            MesGanancia(String l) { this.label = l; }
        }

        String[] monthNames = {"Enero", "Febrero", "Marzo", "Abril", "Mayo", "Junio",
                "Julio", "Agosto", "Septiembre", "Octubre", "Noviembre", "Diciembre"};

        Map<String, MesGanancia> gananciasMap = new HashMap<>();

        for (Venta v : ventas) {
            if (v.getFechaHora() == null || v.getDetalles() == null) continue;
            LocalDateTime date = v.getFechaHora();
            String key = date.getYear() + "-" + String.format("%02d", date.getMonthValue());
            String label = monthNames[date.getMonthValue() - 1] + " " + date.getYear();

            MesGanancia mg = gananciasMap.computeIfAbsent(key, k -> new MesGanancia(label));

            for (DetalleVenta d : v.getDetalles()) {
                Producto p = prodMap.get(d.getIdProducto());
                double costo = p != null ? p.getPrecioCompra() : 0.0;
                mg.ganancia += (d.getPrecioUnitario() - costo) * d.getCantidad();
            }
        }

        List<MesGanancia> list = new ArrayList<>(gananciasMap.values());
        list.sort((a, b) -> Double.compare(b.ganancia, a.ganancia));

        if (list.isEmpty()) return "No hay historial de ventas para analizar.";

        StringBuilder sb = new StringBuilder();
        int count = 1;
        for (MesGanancia mg : list) {
            if (count > 3) break;
            sb.append(count).append(". ").append(mg.label)
              .append(" → Ganancia: $").append(String.format("%,.0f", mg.ganancia)).append("\n");
            count++;
        }
        return sb.toString().trim();
    }

    public String iqAnalizarPerdidas() {
        if (productos == null) return "Sin catálogo de productos.";
        List<Producto> ranking = Arrays.stream(productos)
                .filter(p -> p.getEstado() == 1 && p.getPrecioCompra() > 0 && p.getPrecioVenta() < p.getPrecioCompra())
                .sorted((a, b) -> Double.compare(a.getPrecioVenta() - a.getPrecioCompra(), b.getPrecioVenta() - b.getPrecioCompra()))
                .limit(3)
                .toList();

        if (ranking.isEmpty()) return "¡Bien! Ningún producto tiene precio de venta inferior al costo.";

        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < ranking.size(); i++) {
            Producto p = ranking.get(i);
            double loss = p.getPrecioCompra() - p.getPrecioVenta();
            sb.append(i + 1).append(". ").append(p.getNombre())
              .append(" → Pérdida: $").append(String.format("%,.0f", loss)).append(" por unidad\n");
        }
        return sb.toString().trim();
    }

    public String iqAnalizarSinVender(List<Venta> ventasList) {
        if (productos == null) return "Sin catálogo de productos.";
        Set<Long> vendidos = new HashSet<>();
        for (Venta v : ventasList) {
            if (v.getDetalles() != null) {
                for (DetalleVenta d : v.getDetalles()) vendidos.add(d.getIdProducto());
            }
        }

        List<Producto> sinVender = Arrays.stream(productos)
                .filter(p -> p.getEstado() == 1 && !vendidos.contains(p.getId()))
                .limit(3)
                .toList();

        if (sinVender.isEmpty()) return "¡Excelente! Todos los productos activos tuvieron ventas en este periodo.";

        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < sinVender.size(); i++) {
            Producto p = sinVender.get(i);
            sb.append(i + 1).append(". ").append(p.getNombre())
              .append(" → Stock: ").append(p.getStockActual()).append(" uds sin movimiento\n");
        }
        return sb.toString().trim();
    }

    public String iqAnalizarProximosVencer() {
        if (productos == null) return "Sin catálogo de productos.";
        LocalDate today = LocalDate.now();

        class Temp {
            final String n, f;
            final long d;
            Temp(String n, String f, long d) { this.n = n; this.f = f; this.d = d; }
        }

        List<Temp> list = Arrays.stream(productos)
                .filter(p -> p.getFechaVencimiento() != null && !p.getFechaVencimiento().trim().isEmpty() && !p.getFechaVencimiento().equals("—"))
                .map(p -> {
                    try {
                        LocalDate exp = LocalDate.parse(p.getFechaVencimiento());
                        return new Temp(p.getNombre(), p.getFechaVencimiento(), ChronoUnit.DAYS.between(today, exp));
                    } catch (Exception e) { return null; }
                })
                .filter(Objects::nonNull)
                .sorted((a, b) -> Long.compare(a.d, b.d))
                .limit(4)
                .toList();

        if (list.isEmpty()) return "No hay productos con fecha de vencimiento registrada.";

        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < list.size(); i++) {
            Temp t = list.get(i);
            String lbl = t.d < 0 ? "(Vencido hace " + Math.abs(t.d) + "d)" :
                    t.d == 0 ? "(Vence HOY)" :
                    t.d == 1 ? "(Vence Mañana)" : "(Vence en " + t.d + "d)";
            sb.append(i + 1).append(". ").append(t.n).append(" → ").append(t.f).append(" ").append(lbl).append("\n");
        }
        return sb.toString().trim();
    }
}
