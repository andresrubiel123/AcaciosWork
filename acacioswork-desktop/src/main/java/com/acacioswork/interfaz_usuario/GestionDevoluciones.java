package com.acacioswork.interfaz_usuario;

import javax.swing.*;
import java.awt.*;

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
