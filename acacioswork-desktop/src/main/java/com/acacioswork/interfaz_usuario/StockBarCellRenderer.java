package com.acacioswork.interfaz_usuario;

import java.awt.Component;
import javax.swing.JTable;
import javax.swing.table.TableCellRenderer;

/**
 * Renderizador de celda que integra el componente StockBarPanel en tablas de JTable.
 * @author RADJ
 */
public class StockBarCellRenderer implements TableCellRenderer {
    private final StockBarPanel rendererPanel = new StockBarPanel();

    @Override
    public Component getTableCellRendererComponent(JTable table, Object value,
            boolean isSelected, boolean hasFocus, int row, int column) {
        if (value instanceof StockData) {
            rendererPanel.setStockData((StockData) value, isSelected);
        }
        if (isSelected) {
            rendererPanel.setBackground(table.getSelectionBackground());
            rendererPanel.setOpaque(true);
        } else {
            rendererPanel.setOpaque(false);
        }
        return rendererPanel;
    }
}
