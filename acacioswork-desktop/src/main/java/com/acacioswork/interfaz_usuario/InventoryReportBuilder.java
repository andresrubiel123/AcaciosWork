package com.acacioswork.interfaz_usuario;

import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import com.acacioswork.util.ApiClient;

/**
 * Generador de fragmentos HTML para reportes del área de inventario, proveedores, usuarios y vencimientos.
 * @author RADJ
 */
public class InventoryReportBuilder {

    @SuppressWarnings("unchecked")
    public static void build(String tipo, StringBuilder title, StringBuilder headers, StringBuilder rows, StringBuilder resumen) throws Exception {
        if ("inventario".equals(tipo)) {
            title.append("Inventario General de Productos");
            String[] hdrs = { "Código de Barras", "Nombre del Producto", "Stock", "P. Compra", "P. Venta", "Estado" };
            for (String h : hdrs) headers.append("<th>").append(h).append("</th>");

            Object[] data = ApiClient.get("/productos", Object[].class);
            int totalStock = 0;
            double totalValor = 0;

            if (data != null) {
                for (Object raw : data) {
                    Map<String, Object> p = (Map<String, Object>) raw;
                    int qty = UIUtils.num(p, "stockActual");
                    double precioCompra = UIUtils.dbl(p, "precioCompra");
                    double precioVenta = UIUtils.dbl(p, "precioVenta");
                    String estado = UIUtils.num(p, "estado") == 1 ? "Activo" : "Inactivo";

                    totalStock += qty;
                    totalValor += qty * precioVenta;

                    rows.append("<tr>")
                        .append("<td>").append(UIUtils.str(p, "codigoBarras")).append("</td>")
                        .append("<td>").append(UIUtils.str(p, "nombre")).append("</td>")
                        .append("<td>").append(qty).append(" uds</td>")
                        .append("<td>$").append(String.format("%,.0f", precioCompra)).append("</td>")
                        .append("<td>$").append(String.format("%,.0f", precioVenta)).append("</td>")
                        .append("<td>").append(estado).append("</td>")
                        .append("</tr>");
                }
            }
            resumen.append("<div class='summary-box'>")
                   .append("<p><strong>Total Productos:</strong> ").append(data != null ? data.length : 0).append("</p>")
                   .append("<p><strong>Stock Total en Almacén:</strong> ").append(totalStock).append(" unidades</p>")
                   .append("<p><strong>Valoración Comercial (a P. Venta):</strong> $").append(String.format("%,.0f", totalValor)).append("</p>")
                   .append("</div>");

        } else if ("stock-bajo".equals(tipo)) {
            title.append("Reporte de Productos con Stock Bajo");
            String[] hdrs = { "Código de Barras", "Nombre", "Stock Actual", "Stock Mínimo", "P. Venta", "Proveedor" };
            for (String h : hdrs) headers.append("<th>").append(h).append("</th>");

            Object[] data = ApiClient.get("/productos", Object[].class);
            Object[] provs = ApiClient.get("/proveedores", Object[].class);
            int stockCriticoCount = 0;

            if (data != null) {
                for (Object raw : data) {
                    Map<String, Object> p = (Map<String, Object>) raw;
                    int qty = UIUtils.num(p, "stockActual");
                    int min = p.get("stockMinimo") != null ? UIUtils.num(p, "stockMinimo") : 5;
                    if (qty <= min) {
                        stockCriticoCount++;
                        double precioVenta = UIUtils.dbl(p, "precioVenta");
                        String provName = "Sin asignar";
                        Object idProv = p.get("idProveedor");
                        if (idProv != null && provs != null) {
                            for (Object prRaw : provs) {
                                Map<String, Object> pr = (Map<String, Object>) prRaw;
                                if (idProv.toString().equals(UIUtils.id(pr).toString())) {
                                    provName = UIUtils.str(pr, "nombre");
                                    break;
                                }
                            }
                        }

                        rows.append("<tr>")
                            .append("<td>").append(UIUtils.str(p, "codigoBarras")).append("</td>")
                            .append("<td>").append(UIUtils.str(p, "nombre")).append("</td>")
                            .append("<td><span style='color:#ef4444; font-weight:bold'>").append(qty).append(" uds</span></td>")
                            .append("<td>").append(min).append(" uds</td>")
                            .append("<td>$").append(String.format("%,.0f", precioVenta)).append("</td>")
                            .append("<td>").append(provName).append("</td>")
                            .append("</tr>");
                    }
                }
            }
            resumen.append("<div class='summary-box'>")
                   .append("<p><strong>Total en Stock Crítico:</strong> ").append(stockCriticoCount).append(" productos</p>")
                   .append("</div>");

        } else if ("vencimientos".equals(tipo) || "vencimientos-15".equals(tipo)) {
            int maxDays = "vencimientos".equals(tipo) ? 5 : 15;
            title.append("vencimientos".equals(tipo)
                    ? "Reporte de Productos Próximos a Vencer o Vencidos"
                    : "Reporte de Productos Próximos a Vencer o Vencidos (15 días)");

            String[] hdrs = { "Código de Barras", "Nombre del Producto", "Fecha Vencimiento", "Estado / Días", "Proveedor" };
            for (String h : hdrs) headers.append("<th>").append(h).append("</th>");

            Object[] data = ApiClient.get("/productos", Object[].class);
            Object[] provs = ApiClient.get("/proveedores", Object[].class);

            Map<String, String> provMap = new HashMap<>();
            if (provs != null) {
                for (Object prRaw : provs) {
                    Map<String, Object> pr = (Map<String, Object>) prRaw;
                    provMap.put(UIUtils.id(pr).toString(), UIUtils.str(pr, "nombre"));
                }
            }

            int count = 0;
            LocalDate today = LocalDate.now();

            if (data != null) {
                List<Map<String, Object>> sortedProds = new ArrayList<>();
                for (Object raw : data) {
                    Map<String, Object> p = (Map<String, Object>) raw;
                    String fv = UIUtils.str(p, "fechaVencimiento");
                    if (fv != null && !fv.trim().isEmpty() && !fv.equals("—")) {
                        try {
                            LocalDate.parse(fv);
                            sortedProds.add(p);
                        } catch (Exception e) {
                        }
                    }
                }

                sortedProds.sort((a, b) -> UIUtils.str(a, "fechaVencimiento").compareTo(UIUtils.str(b, "fechaVencimiento")));

                for (Map<String, Object> p : sortedProds) {
                    String fv = UIUtils.str(p, "fechaVencimiento");
                    LocalDate expDate = LocalDate.parse(fv);
                    long diffDays = ChronoUnit.DAYS.between(today, expDate);

                    if (diffDays <= maxDays) {
                        count++;
                        String statusText;
                        if (diffDays < 0) {
                            statusText = "<span style=\"color:#ef4444; font-weight:bold\">Vencido (" + Math.abs(diffDays) + "d)</span>";
                        } else if (diffDays == 0) {
                            statusText = "<span style=\"color:#ef4444; font-weight:bold\">Vence HOY</span>";
                        } else if (diffDays == 1) {
                            statusText = "<span style=\"color:#f97316; font-weight:bold\">Vence Mañana</span>";
                        } else if (diffDays <= 5) {
                            statusText = "<span style=\"color:#d97706; font-weight:bold\">Vence en " + diffDays + " días</span>";
                        } else {
                            statusText = "<span style=\"color:#a16207; font-weight:bold\">Vence en " + diffDays + " días</span>";
                        }

                        String idProv = p.get("idProveedor") != null ? p.get("idProveedor").toString() : "";
                        String provName = idProv.isEmpty() ? "Sin asignar" : provMap.getOrDefault(idProv, "Sin asignar");

                        rows.append("<tr>")
                            .append("<td>").append(UIUtils.str(p, "codigoBarras")).append("</td>")
                            .append("<td>").append(UIUtils.str(p, "nombre")).append("</td>")
                            .append("<td>").append(fv).append("</td>")
                            .append("<td>").append(statusText).append("</td>")
                            .append("<td>").append(provName).append("</td>")
                            .append("</tr>");
                    }
                }
            }

            resumen.append("<div class='summary-box'>")
                   .append("<p><strong>Total por Vencer o Vencidos (Límite ").append(maxDays).append(" días):</strong> ").append(count).append(" productos</p>")
                   .append("</div>");

        } else if ("proveedores".equals(tipo)) {
            title.append("Directorio General de Proveedores");
            String[] hdrs = { "Nombre / Empresa", "NIT / Identificación", "Teléfono", "Email", "Dirección", "Cuenta Bancaria", "Estado" };
            for (String h : hdrs) headers.append("<th>").append(h).append("</th>");

            Object[] data = ApiClient.get("/proveedores", Object[].class);
            if (data != null) {
                for (Object raw : data) {
                    Map<String, Object> p = (Map<String, Object>) raw;
                    String estado = "1".equals(UIUtils.str(p, "activo")) ? "Activo" : "Inactivo";

                    rows.append("<tr>")
                        .append("<td>").append(UIUtils.str(p, "nombre")).append("</td>")
                        .append("<td>").append(UIUtils.str(p, "numeroDocumento")).append("</td>")
                        .append("<td>").append(UIUtils.str(p, "telefono")).append("</td>")
                        .append("<td>").append(UIUtils.str(p, "email")).append("</td>")
                        .append("<td>").append(UIUtils.str(p, "direccion")).append("</td>")
                        .append("<td>").append(UIUtils.str(p, "cuentaBancaria")).append("</td>")
                        .append("<td>").append(estado).append("</td>")
                        .append("</tr>");
                }
            }
            resumen.append("<div class='summary-box'>")
                   .append("<p><strong>Total Proveedores Registrados:</strong> ").append(data != null ? data.length : 0).append("</p>")
                   .append("</div>");

        } else if ("usuarios".equals(tipo)) {
            title.append("Reporte de Usuarios del Sistema");
            String[] hdrs = { "Nombre Completo", "Identificación", "Usuario", "Email", "Rol", "Estado" };
            for (String h : hdrs) headers.append("<th>").append(h).append("</th>");

            Object[] data = ApiClient.get("/usuarios", Object[].class);
            if (data != null) {
                for (Object raw : data) {
                    Map<String, Object> u = (Map<String, Object>) raw;
                    String rol = "1".equals(UIUtils.str(u, "idRol")) ? "Administrador" : "Auxiliar";
                    String estado = "1".equals(UIUtils.str(u, "activo")) ? "Activo" : "Inactivo";

                    rows.append("<tr>")
                        .append("<td>").append(UIUtils.str(u, "nombre")).append(" ")
                        .append(UIUtils.str(u, "apellido").equals("—") ? "" : UIUtils.str(u, "apellido"))
                        .append("</td>")
                        .append("<td>").append(UIUtils.str(u, "numeroDocumento")).append("</td>")
                        .append("<td>").append(UIUtils.str(u, "usuario")).append("</td>")
                        .append("<td>").append(UIUtils.str(u, "email")).append("</td>")
                        .append("<td>").append(rol).append("</td>")
                        .append("<td>").append(estado).append("</td>")
                        .append("</tr>");
                }
            }
            resumen.append("<div class='summary-box'>")
                   .append("<p><strong>Total Usuarios Registrados:</strong> ").append(data != null ? data.length : 0).append("</p>")
                   .append("</div>");
        }
    }
}
