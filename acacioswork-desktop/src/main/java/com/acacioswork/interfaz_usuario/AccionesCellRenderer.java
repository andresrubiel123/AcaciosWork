package com.acacioswork.interfaz_usuario;

import java.awt.Component;
import javax.swing.JTable;
import javax.swing.table.TableCellRenderer;

/**
 * Renderizador de celda que integra el panel AccionesPanel en columnas de tablas.
 * @author RADJ
 */
public class AccionesCellRenderer implements TableCellRenderer {
    private final AccionesPanel panel = new AccionesPanel();

    @Override
    public Component getTableCellRendererComponent(JTable table, Object value,
            boolean isSelected, boolean hasFocus, int row, int column) {
        if (isSelected) {
            panel.setBackground(table.getSelectionBackground());
            panel.setOpaque(true);
        } else {
            panel.setOpaque(false);
        }
        return panel;
    }
}
