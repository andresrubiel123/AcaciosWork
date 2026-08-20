package com.acacioswork.interfaz_usuario;

import java.awt.Color;
import java.awt.Cursor;
import java.awt.FlowLayout;
import java.awt.Font;
import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JPanel;

/**
 * Panel contenedor con botones de Editar y Borrar para incrustarse en las tablas de JTable.
 * @author RADJ
 */
public class AccionesPanel extends JPanel {
    public final JButton btnEditar;
    public final JButton btnBorrar;

    public AccionesPanel() {
        setLayout(new FlowLayout(FlowLayout.CENTER, 6, 2));
        setOpaque(false);

        btnEditar = new JButton("Editar");
        btnEditar.setFont(new Font("Inter", Font.BOLD, 11));
        btnEditar.setForeground(Color.WHITE);
        btnEditar.setBackground(new Color(51, 65, 85));
        btnEditar.setBorder(BorderFactory.createEmptyBorder(4, 10, 4, 10));
        btnEditar.setFocusPainted(false);
        btnEditar.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));

        btnBorrar = new JButton("Borrar");
        btnBorrar.setFont(new Font("Inter", Font.BOLD, 11));
        btnBorrar.setForeground(new Color(239, 68, 68));
        btnBorrar.setBackground(new Color(239, 68, 68, 38));
        btnBorrar.setBorder(BorderFactory.createEmptyBorder(4, 10, 4, 10));
        btnBorrar.setFocusPainted(false);
        btnBorrar.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));

        add(btnEditar);
        add(btnBorrar);
    }
}
