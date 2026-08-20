package com.acacioswork.interfaz_usuario;

import java.awt.*;
import java.util.Map;
import java.util.function.Function;
import java.util.regex.Pattern;
import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableModel;
import javax.swing.table.TableRowSorter;
import com.acacioswork.util.ApiClient;

/**
 * Utilidades de interfaz de usuario compartidas por los paneles modulares de AcaciosWork.
 * @author RADJ
 */
public class UIUtils {

    public static JPanel buildSectionHeader(String title, String subtitle, JButton... buttons) {
        JPanel header = new JPanel(new BorderLayout());
        header.setOpaque(false);
        header.setBorder(new EmptyBorder(0, 0, 16, 0));

        JLabel lblTitle = new JLabel(title);
        lblTitle.setForeground(Administrador.TEXT_MAIN);
        lblTitle.setFont(new Font("Inter", Font.BOLD, 22));

        JLabel lblSubtitle = new JLabel(subtitle);
        lblSubtitle.setForeground(Administrador.TEXT_MUTED);
        lblSubtitle.setFont(new Font("Inter", Font.PLAIN, 12));

        JPanel labelPanel = new JPanel(new GridLayout(2, 1, 0, 2));
        labelPanel.setOpaque(false);
        labelPanel.add(lblTitle);
        labelPanel.add(lblSubtitle);

        JPanel leftContainer = new JPanel(new FlowLayout(FlowLayout.LEFT, 20, 0));
        leftContainer.setOpaque(false);
        leftContainer.add(labelPanel);

        if (buttons != null && buttons.length > 0 && buttons[0] != null) {
            for (JButton btn : buttons) {
                if (btn != null) {
                    leftContainer.add(btn);
                }
            }
        }

        header.add(leftContainer, BorderLayout.WEST);
        return header;
    }

    public static JButton createActionButton(String text, Color bg) {
        JButton button = new JButton(text) {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setColor(getBackground());
                g2.fillRoundRect(0, 0, getWidth(), getHeight(), 10, 10);
                g2.dispose();
                super.paintComponent(g);
            }
        };
        button.setBackground(bg);
        button.setForeground(Color.WHITE);
        button.setFont(new Font("Inter", Font.BOLD, 12));
        button.setBorder(new EmptyBorder(8, 18, 8, 18));
        button.setFocusPainted(false);
        button.setContentAreaFilled(false);
        button.setOpaque(false);
        button.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        return button;
    }

    public static JPanel buildStatCard(String label, String value, Color valueColor) {
        JPanel card = new JPanel(new GridLayout(2, 1, 0, 4));
        card.setBackground(Administrador.BG_CARD);
        card.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(255, 255, 255, 13), 1),
                new EmptyBorder(14, 18, 14, 18)));

        JLabel lblLabel = new JLabel(label);
        lblLabel.setForeground(Administrador.TEXT_MUTED);
        lblLabel.setFont(new Font("Inter", Font.BOLD, 18));

        JLabel lblValue = new JLabel(value);
        lblValue.setForeground(valueColor);
        lblValue.setFont(new Font("Inter", Font.BOLD, 36));
        lblValue.setName("value");

        card.add(lblLabel);
        card.add(lblValue);
        return card;
    }

    public static void updateStatCard(JPanel card, String newValue) {
        for (Component cmp : card.getComponents()) {
            if (cmp instanceof JLabel && "value".equals(cmp.getName())) {
                ((JLabel) cmp).setText(newValue);
            }
        }
    }

    public static JTable buildStyledTable(String[] cols) {
        DefaultTableModel model = new DefaultTableModel(cols, 0) {
            @Override
            public boolean isCellEditable(int r, int c) {
                return false;
            }
        };
        JTable table = new JTable(model);
        table.setBackground(Administrador.BG_CARD);
        table.setForeground(Administrador.TEXT_MAIN);
        table.setRowHeight(36);
        table.getTableHeader().setBackground(Administrador.BG_CARD);
        table.getTableHeader().setForeground(Administrador.TEXT_MUTED);
        table.setSelectionBackground(new Color(99, 102, 241, 60));
        table.setSelectionForeground(Administrador.TEXT_MAIN);
        return table;
    }

    public static JScrollPane wrapTable(JTable table) {
        JScrollPane scroll = new JScrollPane(table);
        scroll.setBackground(Administrador.BG_CARD);
        scroll.getViewport().setBackground(Administrador.BG_CARD);
        scroll.setBorder(BorderFactory.createLineBorder(new Color(255, 255, 255, 13), 1));
        return scroll;
    }

    public static JPanel buildSearchPanel(JTable table) {
        JPanel p = new JPanel(new BorderLayout());
        p.setOpaque(false);
        p.setBorder(BorderFactory.createEmptyBorder(0, 0, 4, 0));

        JTextField txtSearch = new JTextField() {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setColor(Administrador.BG_CARD);
                g2.fillRoundRect(0, 0, getWidth(), getHeight(), 10, 10);
                g2.setColor(new Color(255, 255, 255, 20));
                g2.drawRoundRect(0, 0, getWidth() - 1, getHeight() - 1, 10, 10);
                g2.dispose();
                super.paintComponent(g);
            }
        };
        txtSearch.setBackground(Administrador.BG_CARD);
        txtSearch.setForeground(Administrador.TEXT_MAIN);
        txtSearch.setCaretColor(Administrador.TEXT_MAIN);
        txtSearch.setFont(new Font("Inter", Font.PLAIN, 14));
        txtSearch.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createEmptyBorder(),
                BorderFactory.createEmptyBorder(8, 14, 8, 14)));

        txtSearch.putClientProperty("JTextField.placeholderText", "🔍 Buscar en la tabla...");

        TableRowSorter<TableModel> sorter = new TableRowSorter<>(table.getModel());
        table.setRowSorter(sorter);

        txtSearch.getDocument().addDocumentListener(new javax.swing.event.DocumentListener() {
            private void search() {
                String text = txtSearch.getText();
                if (text.trim().length() == 0) {
                    sorter.setRowFilter(null);
                } else {
                    sorter.setRowFilter(RowFilter.regexFilter("(?i)" + Pattern.quote(text)));
                }
            }
            @Override public void insertUpdate(javax.swing.event.DocumentEvent e) { search(); }
            @Override public void removeUpdate(javax.swing.event.DocumentEvent e) { search(); }
            @Override public void changedUpdate(javax.swing.event.DocumentEvent e) { search(); }
        });

        p.add(txtSearch, BorderLayout.CENTER);
        return p;
    }

    public static void loadTable(JTable table, String endpoint, Function<Map<String, Object>, Object[]> rowMapper) {
        new SwingWorker<Void, Void>() {
            @SuppressWarnings("unchecked")
            @Override
            protected Void doInBackground() {
                try {
                    Object[] data = ApiClient.get(endpoint, Object[].class);
                    SwingUtilities.invokeLater(() -> {
                        DefaultTableModel dtm = (DefaultTableModel) table.getModel();
                        dtm.setRowCount(0);
                        if (data != null) {
                            for (Object raw : data) {
                                dtm.addRow(rowMapper.apply((Map<String, Object>) raw));
                            }
                        }
                    });
                } catch (Exception e) {
                }
                return null;
            }
        }.execute();
    }

    public static void eliminarGeneric(JTable table, String endpoint, String entityName, Component parent, Runnable onFinish) {
        int row = table.getSelectedRow();
        if (row == -1) {
            JOptionPane.showMessageDialog(parent, "Seleccione un " + entityName);
            return;
        }

        Object idVal = table.getValueAt(row, 0);
        String nameVal = table.getValueAt(row, 1).toString();

        if (JOptionPane.showConfirmDialog(parent, "¿Eliminar " + entityName + ": " + nameVal + "?", "Confirmar Acción",
                JOptionPane.OK_CANCEL_OPTION, JOptionPane.WARNING_MESSAGE) == JOptionPane.OK_OPTION) {
            try {
                ApiClient.delete(endpoint + "/" + idVal);
                JOptionPane.showMessageDialog(parent, entityName + " eliminado correctamente.");
                if (onFinish != null) {
                    onFinish.run();
                }
            } catch (Exception e) {
                JOptionPane.showMessageDialog(parent, "Error al eliminar: " + e.getMessage());
            }
        }
    }

    public static void setupAccionesColumn(JTable table, Runnable onEditar, Runnable onBorrar) {
        int colIndex = table.getColumnCount() - 1;
        if (table.getColumnName(colIndex).equals("Acciones")) {
            table.getColumnModel().getColumn(colIndex).setCellRenderer(new AccionesCellRenderer());
            table.getColumnModel().getColumn(colIndex).setPreferredWidth(140);

            table.addMouseListener(new java.awt.event.MouseAdapter() {
                @Override
                public void mousePressed(java.awt.event.MouseEvent e) {
                    int row = table.rowAtPoint(e.getPoint());
                    int col = table.columnAtPoint(e.getPoint());
                    if (col == colIndex && row != -1) {
                        Rectangle rect = table.getCellRect(row, col, true);
                        int cellX = e.getX() - rect.x;
                        int width = rect.width;
                        table.setRowSelectionInterval(row, row);
                        if (cellX < width / 2) {
                            onEditar.run();
                        } else {
                            onBorrar.run();
                        }
                    }
                }
            });
        }
    }

    public static void hideColumn(JTable table, int index) {
        table.getColumnModel().getColumn(index).setMinWidth(0);
        table.getColumnModel().getColumn(index).setMaxWidth(0);
        table.getColumnModel().getColumn(index).setPreferredWidth(0);
    }

    public static String str(Map<String, Object> m, String k) {
        Object v = m.get(k);
        return v != null ? v.toString() : "—";
    }

    public static int num(Map<String, Object> m, String k) {
        Object v = m.get(k);
        return (v instanceof Number) ? ((Number) v).intValue() : 0;
    }

    public static double dbl(Map<String, Object> m, String k) {
        Object v = m.get(k);
        return (v instanceof Number) ? ((Number) v).doubleValue() : 0.0;
    }

    public static Long id(Map<String, Object> m) {
        Object v = m.get("id");
        return v != null ? Long.valueOf(v.toString()) : 0L;
    }
}
