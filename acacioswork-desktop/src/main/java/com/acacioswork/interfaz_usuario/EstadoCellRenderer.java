package com.acacioswork.interfaz_usuario;

import java.awt.Color;
import java.awt.Component;
import java.awt.Font;
import javax.swing.BorderFactory;
import javax.swing.JTable;
import javax.swing.table.DefaultTableCellRenderer;

/**
 * Renderizador de celda para columnas que representan un estado (Activo/Inactivo) con iconos de colores.
 * @author RADJ
 */
public class EstadoCellRenderer extends DefaultTableCellRenderer {
    private final DotIcon iconActivo = new DotIcon(new Color(16, 185, 129), 8);
    private final DotIcon iconInactivo = new DotIcon(new Color(239, 68, 68), 8);

    @Override
    public Component getTableCellRendererComponent(JTable table, Object value,
            boolean isSelected, boolean hasFocus, int row, int column) {
        super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);
        setFont(new Font("Inter", Font.BOLD, 12));
        setIconTextGap(8);
        setBorder(BorderFactory.createEmptyBorder(0, 12, 0, 12));

        String valStr = value != null ? value.toString() : "";
        if ("Activo".equalsIgnoreCase(valStr)) {
            setIcon(iconActivo);
            setForeground(new Color(16, 185, 129));
        } else if ("Inactivo".equalsIgnoreCase(valStr)) {
            setIcon(iconInactivo);
            setForeground(new Color(239, 68, 68));
        } else {
            setIcon(null);
        }
        if (isSelected) {
            setBackground(table.getSelectionBackground());
        } else {
            setBackground(table.getBackground());
        }
        return this;
    }
}
