package com.acacioswork.interfaz_usuario;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Cursor;
import java.awt.Font;
import java.awt.GradientPaint;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import javax.swing.JButton;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JTable;
import javax.swing.SwingConstants;
import javax.swing.SwingWorker;
import javax.swing.border.EmptyBorder;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;
import com.acacioswork.util.ApiClient;

/**
 * Pestaña de alertas de stock crítico para la interfaz de Administrador.
 * Muestra los productos con stock bajo y permite consultar información de proveedores y generar PDFs.
 * @author RADJ
 */
public class AlertasTab extends JPanel {
    private final JTable tableAlertas;

    public AlertasTab(Administrador parent) {
        setLayout(new BorderLayout(0, 16));
        setBackground(Administrador.BG_DARK);
        setBorder(new EmptyBorder(24, 28, 24, 28));

        JButton bPdf = new JButton("📄 Descargar lista PDF") {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setPaint(new GradientPaint(0, 0, new Color(245, 158, 11), 0, getHeight(), new Color(217, 119, 6)));
                g2.fillRoundRect(0, 0, getWidth(), getHeight(), 10, 10);
                g2.dispose();
                super.paintComponent(g);
            }
        };
        bPdf.setForeground(Color.WHITE);
        bPdf.setFont(new Font("Inter", Font.BOLD, 12));
        bPdf.setBorder(new EmptyBorder(8, 18, 8, 18));
        bPdf.setFocusPainted(false);
        bPdf.setContentAreaFilled(false);
        bPdf.setOpaque(false);
        bPdf.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));

        add(UIUtils.buildSectionHeader("Alertas de Stock Crítico",
                "Productos con existencias en nivel mínimo de reabastecimiento", bPdf),
                BorderLayout.NORTH);

        // Inicializar tabla
        tableAlertas = UIUtils.buildStyledTable(
                new String[] { "ID", "Producto", "Stock Actual", "Mínimo", "Proveedor", "Acción" });
        UIUtils.hideColumn(tableAlertas, 0);

        // Renderizadores de celdas
        tableAlertas.getColumnModel().getColumn(2).setCellRenderer(new DefaultTableCellRenderer() {
            {
                setHorizontalAlignment(SwingConstants.CENTER);
            }
            @Override
            public Component getTableCellRendererComponent(JTable t, Object v, boolean s, boolean f, int r, int c) {
                Component comp = super.getTableCellRendererComponent(t, v, s, f, r, c);
                comp.setFont(t.getFont());
                if (s) {
                    comp.setBackground(t.getSelectionBackground());
                    comp.setForeground(t.getSelectionForeground());
                } else {
                    comp.setBackground(t.getBackground());
                    comp.setForeground(Administrador.TEXT_MAIN);
                    try {
                        Object val = t.getValueAt(r, 2);
                        if (val != null) {
                            int stock = java.lang.Integer.parseInt(val.toString().replaceAll("[^0-9]", ""));
                            if (stock == 0) {
                                comp.setBackground(new Color(239, 68, 68, 40));
                                comp.setForeground(Administrador.DANGER);
                                comp.setFont(t.getFont().deriveFont(Font.BOLD));
                            } else {
                                comp.setBackground(new Color(245, 158, 11, 40));
                                comp.setForeground(new Color(245, 158, 11));
                                comp.setFont(t.getFont().deriveFont(Font.BOLD));
                            }
                        }
                    } catch (Exception ex) {
                    }
                }
                return comp;
            }
        });

        tableAlertas.getColumnModel().getColumn(3).setCellRenderer(new DefaultTableCellRenderer() {
            {
                setHorizontalAlignment(SwingConstants.CENTER);
            }
            @Override
            public Component getTableCellRendererComponent(JTable t, Object v, boolean s, boolean f, int r, int c) {
                Component comp = super.getTableCellRendererComponent(t, v, s, f, r, c);
                comp.setFont(t.getFont());
                if (s) {
                    comp.setBackground(t.getSelectionBackground());
                    comp.setForeground(t.getSelectionForeground());
                } else {
                    comp.setBackground(t.getBackground());
                    comp.setForeground(Administrador.TEXT_MUTED);
                }
                return comp;
            }
        });

        tableAlertas.getColumnModel().getColumn(5).setCellRenderer(new DefaultTableCellRenderer() {
            {
                setHorizontalAlignment(SwingConstants.CENTER);
            }
            @Override
            public Component getTableCellRendererComponent(JTable t, Object v, boolean s, boolean f, int r, int c) {
                Component comp = super.getTableCellRendererComponent(t, v, s, f, r, c);
                comp.setFont(t.getFont());
                if (s) {
                    comp.setBackground(t.getSelectionBackground());
                    comp.setForeground(t.getSelectionForeground());
                } else {
                    comp.setBackground(new Color(99, 102, 241, 30));
                    comp.setForeground(Administrador.PRIMARY);
                    comp.setFont(t.getFont().deriveFont(Font.BOLD));
                }
                return comp;
            }
        });

        // Contenedor de la tabla
        JPanel tableContainer = new JPanel(new BorderLayout(0, 8));
        tableContainer.setOpaque(false);
        tableContainer.add(UIUtils.buildSearchPanel(tableAlertas), BorderLayout.NORTH);
        tableContainer.add(UIUtils.wrapTable(tableAlertas), BorderLayout.CENTER);

        add(tableContainer, BorderLayout.CENTER);

        // Listeners
        bPdf.addActionListener(e -> parent.generarReporte("stock-bajo"));

        tableAlertas.addMouseListener(new java.awt.event.MouseAdapter() {
            @Override
            public void mousePressed(java.awt.event.MouseEvent e) {
                int row = tableAlertas.rowAtPoint(e.getPoint());
                int col = tableAlertas.columnAtPoint(e.getPoint());
                if (col == 5 && row != -1) {
                    mostrarInfoProveedor(tableAlertas.getValueAt(row, 0));
                }
            }
        });
    }

    public void refresh() {
        new SwingWorker<Object[][], Void>() {
            @SuppressWarnings("unchecked")
            @Override
            protected Object[][] doInBackground() throws Exception {
                try {
                    Object[] products = ApiClient.get("/productos", Object[].class);
                    Object[] providers = ApiClient.get("/proveedores", Object[].class);

                    Map<String, String> provMap = new HashMap<>();
                    if (providers != null) {
                        for (Object pr : providers) {
                            Map<String, Object> pm = (Map<String, Object>) pr;
                            provMap.put(UIUtils.id(pm).toString(), UIUtils.str(pm, "nombre"));
                        }
                    }

                    List<Object[]> rows = new ArrayList<>();
                    if (products != null) {
                        for (Object raw : products) {
                            Map<String, Object> p = (Map<String, Object>) raw;
                            int stock = UIUtils.num(p, "stockActual");
                            int min = p.get("stockMinimo") != null ? UIUtils.num(p, "stockMinimo") : 5;
                            if (stock <= min) {
                                Object idProv = p.get("idProveedor");
                                String provName = (idProv != null)
                                        ? provMap.getOrDefault(idProv.toString(), "Sin asignar")
                                        : "Sin asignar";
                                rows.add(new Object[] {
                                        UIUtils.id(p),
                                        UIUtils.str(p, "nombre"),
                                        stock + " uds",
                                        min + " uds",
                                        provName,
                                        "🔍 Ver Proveedor"
                                });
                            }
                        }
                    }
                    return rows.toArray(new Object[0][]);
                } catch (Exception e) {
                    return null;
                }
            }

            @Override
            protected void done() {
                try {
                    Object[][] rows = get();
                    DefaultTableModel dtm = (DefaultTableModel) tableAlertas.getModel();
                    dtm.setRowCount(0);
                    if (rows != null) {
                        for (Object[] r : rows) {
                            dtm.addRow(r);
                        }
                    }
                } catch (Exception e) {
                }
            }
        }.execute();
    }

    private void mostrarInfoProveedor(Object idProd) {
        try {
            com.acacioswork.model.Producto p = ApiClient.get("/productos/" + idProd,
                    com.acacioswork.model.Producto.class);
            if (p.getIdProveedor() == null) {
                JOptionPane.showMessageDialog(this, "Este producto no tiene un proveedor asignado.");
                return;
            }
            com.acacioswork.model.Proveedor prov = ApiClient.get("/proveedores/" + p.getIdProveedor(),
                    com.acacioswork.model.Proveedor.class);
            Object[] msg = {
                    "<html><b>Proveedor:</b> " + prov.getNombre() + "</html>",
                    "<html><b>Teléfono:</b> " + prov.getTelefono() + "</html>",
                    "<html><b>Email:</b> " + prov.getEmail() + "</html>",
                    "<html><b>Cuenta:</b> " + prov.getCuentaBancaria() + "</html>"
            };
            JOptionPane.showMessageDialog(this, msg, "Información de Contacto", JOptionPane.INFORMATION_MESSAGE);
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, "No se pudo obtener la información del proveedor.");
        }
    }
}
