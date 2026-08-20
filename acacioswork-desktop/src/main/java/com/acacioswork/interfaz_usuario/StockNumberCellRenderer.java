package com.acacioswork.interfaz_usuario;

import java.awt.Color;
import java.awt.Component;
import java.awt.Font;
import javax.swing.JTable;
import javax.swing.SwingConstants;
import javax.swing.table.DefaultTableCellRenderer;

/**
 * Renderizador de celda numérico que formatea y colorea la cantidad del stock.
 * @author RADJ
 */
public class StockNumberCellRenderer extends DefaultTableCellRenderer {
    public StockNumberCellRenderer() {
        setHorizontalAlignment(SwingConstants.CENTER);
    }

    @Override
    public Component getTableCellRendererComponent(JTable table, Object value,
            boolean isSelected, boolean hasFocus, int row, int column) {
        Component c = super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);
        c.setFont(new Font("Inter", Font.BOLD, 12));
        if (value instanceof StockData) {
            StockData data = (StockData) value;
            int qty = data.actual;
            int optimo = data.optimo > 0 ? data.optimo : 200;
            int pct = (int) Math.round(((double) qty / optimo) * 100);

            setText(String.valueOf(qty));
            if (isSelected) {
                // Mantiene los colores por defecto al estar seleccionado
            } else {
                if (pct <= 30) {
                    setForeground(new Color(248, 113, 113));
                } else if (pct <= 69) {
                    setForeground(new Color(251, 146, 60));
                } else {
                    setForeground(new Color(52, 211, 153));
                }
            }
        }
        return c;
    }
}
