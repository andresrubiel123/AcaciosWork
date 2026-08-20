package com.acacioswork.interfaz_usuario;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import com.acacioswork.util.ApiClient;

/**
 * Generador de fragmentos HTML para reportes del área financiera, clientes y resumen ejecutivo.
 * @author RADJ
 */
public class FinanceReportBuilder {

    @SuppressWarnings("unchecked")
    public static void build(String tipo, StringBuilder title, StringBuilder headers, StringBuilder rows, StringBuilder resumen, String nowStr) throws Exception {
        if ("clientes".equals(tipo)) {
            title.append("Reporte General de Clientes");
            String[] hdrs = { "Nombre Completo", "Identificación", "Teléfono", "Email", "Dirección", "Frecuente", "Estado" };
            for (String h : hdrs) headers.append("<th>").append(h).append("</th>");

            Object[] data = ApiClient.get("/clientes", Object[].class);
            if (data != null) {
                for (Object raw : data) {
                    Map<String, Object> c = (Map<String, Object>) raw;
                    String frecuente = "true".equals(UIUtils.str(c, "frecuente")) ? "Sí" : "No";
                    String estado = "1".equals(UIUtils.str(c, "activo")) ? "Activo" : "Inactivo";

                    rows.append("<tr>")
                        .append("<td>").append(UIUtils.str(c, "nombre")).append("</td>")
                        .append("<td>").append(UIUtils.str(c, "numeroDocumento")).append("</td>")
                        .append("<td>").append(UIUtils.str(c, "telefono")).append("</td>")
                        .append("<td>").append(UIUtils.str(c, "email")).append("</td>")
                        .append("<td>").append(UIUtils.str(c, "direccion")).append("</td>")
                        .append("<td>").append(frecuente).append("</td>")
                        .append("<td>").append(estado).append("</td>")
                        .append("</tr>");
                }
            }
            resumen.append("<div class='summary-box'>")
                   .append("<p><strong>Total Clientes Registrados:</strong> ").append(data != null ? data.length : 0).append("</p>")
                   .append("</div>");

        } else if ("resumen".equals(tipo)) {
            title.append("Resumen Ejecutivo de la Empresa");
            String[] hdrs = { "Indicador", "Valor / Métrica", "Estado / Detalle" };
            for (String h : hdrs) headers.append("<th>").append(h).append("</th>");

            Object[] productos = ApiClient.get("/productos", Object[].class);
            Object[] clientes = ApiClient.get("/clientes", Object[].class);
            Object[] proveedores = ApiClient.get("/proveedores", Object[].class);
            Object[] usuarios = ApiClient.get("/usuarios", Object[].class);

            int totalProd = productos != null ? productos.length : 0;
            int totalStock = 0;
            int stockBajo = 0;
            double valorInventario = 0;
            if (productos != null) {
                for (Object raw : productos) {
                    Map<String, Object> p = (Map<String, Object>) raw;
                    int qty = UIUtils.num(p, "stockActual");
                    int min = p.get("stockMinimo") != null ? UIUtils.num(p, "stockMinimo") : 5;
                    valorInventario += qty * UIUtils.dbl(p, "precioVenta");
                    totalStock += qty;
                    if (qty <= min) stockBajo++;
                }
            }

            rows.append("<tr><td>Total de Productos en Catálogo</td><td>").append(totalProd).append("</td><td>Productos registrados</td></tr>")
                .append("<tr><td>Unidades de Stock Físico</td><td>").append(totalStock).append(" uds</td><td>Total unidades en inventario</td></tr>")
                .append("<tr><td>Productos con Stock Bajo</td><td><span style='color:#ef4444; font-weight:bold'>").append(stockBajo).append("</span></td><td>Requieren reabastecimiento urgente</td></tr>")
                .append("<tr><td>Valoración de Inventario</td><td>$").append(String.format("%,.2f", valorInventario)).append("</td><td>En base a precios de venta comerciales</td></tr>")
                .append("<tr><td>Clientes Registrados</td><td>").append(clientes != null ? clientes.length : 0).append("</td><td>Base de datos de clientes</td></tr>")
                .append("<tr><td>Proveedores Registrados</td><td>").append(proveedores != null ? proveedores.length : 0).append("</td><td>Suministradores comerciales</td></tr>")
                .append("<tr><td>Usuarios en el Sistema</td><td>").append(usuarios != null ? usuarios.length : 0).append("</td><td>Cuentas con acceso administrativo</td></tr>");

            resumen.append("<div class='summary-box'>")
                   .append("<p><strong>Fecha del Resumen:</strong> ").append(nowStr).append("</p>")
                   .append("<p><strong>Estado de Operación:</strong> Operando normalmente</p>")
                   .append("</div>");

        } else if ("ventas".equals(tipo)) {
            title.append("Reporte Histórico de Ventas");
            String[] hdrs = { "ID Venta", "Fecha / Hora", "Cliente", "Procesado por", "Productos", "Total" };
            for (String h : hdrs) headers.append("<th>").append(h).append("</th>");

            Object[] dataVentas = ApiClient.get("/ventas", Object[].class);
            Object[] dataClientes = ApiClient.get("/clientes", Object[].class);
            Object[] dataUsuarios = ApiClient.get("/usuarios", Object[].class);

            Map<String, String> clientesMap = new HashMap<>();
            if (dataClientes != null) {
                for (Object raw : dataClientes) {
                    Map<String, Object> c = (Map<String, Object>) raw;
                    clientesMap.put(UIUtils.id(c).toString(), UIUtils.str(c, "nombre"));
                }
            }

            Map<String, String> usuariosMap = new HashMap<>();
            if (dataUsuarios != null) {
                for (Object raw : dataUsuarios) {
                    Map<String, Object> u = (Map<String, Object>) raw;
                    usuariosMap.put(UIUtils.id(u).toString(), UIUtils.str(u, "nombre") + " " + (UIUtils.str(u, "apellido").equals("—") ? "" : UIUtils.str(u, "apellido")));
                }
            }

            double totalVentasMonto = 0;
            if (dataVentas != null) {
                List<Map<String, Object>> ventasList = new ArrayList<>();
                for (Object raw : dataVentas) ventasList.add((Map<String, Object>) raw);
                ventasList.sort((a, b) -> UIUtils.str(b, "fechaHora").compareTo(UIUtils.str(a, "fechaHora")));

                for (Map<String, Object> v : ventasList) {
                    double total = UIUtils.dbl(v, "valorTotal");
                    totalVentasMonto += total;
                    String fechaRaw = UIUtils.str(v, "fechaHora");
                    String fecha = "—";
                    try {
                        fecha = java.time.LocalDateTime.parse(fechaRaw)
                                    .format(java.time.format.DateTimeFormatter.ofPattern("dd/MM/yyyy hh:mm a"));
                    } catch (Exception e) { fecha = fechaRaw; }

                    String cId = v.get("idCliente") != null ? v.get("idCliente").toString() : "";
                    String cliente = cId.isEmpty() ? "Sin cliente" : clientesMap.getOrDefault(cId, "Cliente #" + cId);
                    String uId = v.get("idUsuario") != null ? v.get("idUsuario").toString() : "";
                    String usuario = uId.isEmpty() ? "Sistema" : usuariosMap.getOrDefault(uId, "Usuario #" + uId);

                    List<?> detalles = (List<?>) v.get("detalles");
                    int nProductos = detalles != null ? detalles.size() : 0;

                    rows.append("<tr>")
                        .append("<td>#").append(UIUtils.id(v)).append("</td>")
                        .append("<td>").append(fecha).append("</td>")
                        .append("<td>").append(cliente).append("</td>")
                        .append("<td>").append(usuario).append("</td>")
                        .append("<td>").append(nProductos).append(" producto(s)</td>")
                        .append("<td>$").append(String.format("%,.0f", total)).append("</td>")
                        .append("</tr>");
                }
            }
            resumen.append("<div class='summary-box'>")
                   .append("<p><strong>Total de Ventas Realizadas:</strong> ").append(dataVentas != null ? dataVentas.length : 0).append("</p>")
                   .append("<p><strong>Monto Total Recaudado:</strong> $").append(String.format("%,.0f", totalVentasMonto)).append("</p>")
                   .append("</div>");

        } else if ("ganancias".equals(tipo)) {
            title.append("Reporte de Ganancias y Rentabilidad");
            String[] hdrs = { "ID Venta", "Fecha / Hora", "Ingreso (Venta)", "Costo total", "Ganancia Neta", "Margen %" };
            for (String h : hdrs) headers.append("<th>").append(h).append("</th>");

            Object[] dataVentas = ApiClient.get("/ventas", Object[].class);
            Object[] dataProductos = ApiClient.get("/productos", Object[].class);

            Map<String, Map<String, Object>> prodMap = new HashMap<>();
            if (dataProductos != null) {
                for (Object raw : dataProductos) {
                    Map<String, Object> p = (Map<String, Object>) raw;
                    prodMap.put(UIUtils.id(p).toString(), p);
                }
            }

            double globalIngresos = 0;
            double globalCostos = 0;

            if (dataVentas != null) {
                List<Map<String, Object>> ventasList = new ArrayList<>();
                for (Object raw : dataVentas) ventasList.add((Map<String, Object>) raw);
                ventasList.sort((a, b) -> UIUtils.str(b, "fechaHora").compareTo(UIUtils.str(a, "fechaHora")));

                for (Map<String, Object> v : ventasList) {
                    double ingreso = UIUtils.dbl(v, "valorTotal");
                    globalIngresos += ingreso;

                    String fechaRaw = UIUtils.str(v, "fechaHora");
                    String fecha = "—";
                    try {
                        fecha = java.time.LocalDateTime.parse(fechaRaw)
                                    .format(java.time.format.DateTimeFormatter.ofPattern("dd/MM/yyyy hh:mm a"));
                    } catch (Exception e) { fecha = fechaRaw; }

                    double costoVenta = 0;
                    List<?> detalles = (List<?>) v.get("detalles");
                    if (detalles != null) {
                        for (Object detRaw : detalles) {
                            Map<String, Object> d = (Map<String, Object>) detRaw;
                            String pId = d.get("idProducto") != null ? d.get("idProducto").toString() : "";
                            Map<String, Object> prod = prodMap.get(pId);
                            double precioCompra = prod != null ? UIUtils.dbl(prod, "precioCompra") : 0;
                            int qty = d.get("cantidad") != null ? ((Number) d.get("cantidad")).intValue() : 0;
                            costoVenta += qty * precioCompra;
                        }
                    }
                    globalCostos += costoVenta;

                    double ganancia = ingreso - costoVenta;
                    double margenVal = ingreso > 0 ? (ganancia / ingreso) * 100 : 0;
                    String margen = String.format("%.1f%%", margenVal);
                    String color = ganancia >= 0 ? "#10b981" : "#ef4444";

                    rows.append("<tr>")
                        .append("<td>#").append(UIUtils.id(v)).append("</td>")
                        .append("<td>").append(fecha).append("</td>")
                        .append("<td>$").append(String.format("%,.0f", ingreso)).append("</td>")
                        .append("<td>$").append(String.format("%,.0f", costoVenta)).append("</td>")
                        .append("<td><span style='color:").append(color).append("; font-weight:bold'>$")
                        .append(String.format("%,.0f", ganancia)).append("</span></td>")
                        .append("<td>").append(margen).append("</td>")
                        .append("</tr>");
                }
            }

            double globalGanancia = globalIngresos - globalCostos;
            double globalMargenVal = globalIngresos > 0 ? (globalGanancia / globalIngresos) * 100 : 0;
            String globalMargen = String.format("%.1f%%", globalMargenVal);
            String globalColor = globalGanancia >= 0 ? "#10b981" : "#ef4444";

            resumen.append("<div class='summary-box'>")
                   .append("<p><strong>Total Ingresos (Ventas):</strong> $").append(String.format("%,.0f", globalIngresos)).append("</p>")
                   .append("<p><strong>Total Costo de Ventas:</strong> $").append(String.format("%,.0f", globalCostos)).append("</p>")
                   .append("<p><strong>Ganancia Neta Total:</strong> <span style='color:").append(globalColor).append("; font-weight:bold'>$")
                   .append(String.format("%,.0f", globalGanancia)).append("</span></p>")
                   .append("<p><strong>Margen de Ganancia Promedio:</strong> ").append(globalMargen).append("</p>")
                   .append("</div>");
        }
    }
}
