package com.acacioswork.interfaz_usuario;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Font;

import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.SwingConstants;

/** Interfaz de gestión de devoluciones (Placeholder). @author RADJ */
public class GestionDevoluciones extends JPanel {
    public GestionDevoluciones() {
        setLayout(new BorderLayout());
        setBackground(new Color(15, 23, 42));
        
        JButton btnVolver = new JButton("‹ Volver");
        btnVolver.addActionListener(e -> MainFrame.navigateTo(new PuntoDeVenta()));
        add(btnVolver, BorderLayout.NORTH);

        JLabel lbl = new JLabel("Gestión de Devoluciones - Próximamente", SwingConstants.CENTER);
        lbl.setForeground(Color.WHITE);
        lbl.setFont(new Font("Inter", Font.BOLD, 18));
        add(lbl, BorderLayout.CENTER);
    }
}
