package com.acacioswork.interfaz_usuario;

import javax.swing.*;

/** Ventana principal del sistema para el cliente de escritorio. @author RADJ */
public class MainFrame extends JFrame {

    private static MainFrame instance;

    public MainFrame() {
        setTitle("AcaciosWork - Dashboard Administrador");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(1000, 700);
        setLocationRelativeTo(null);
        instance = this;
    }

    public static void navigateTo(JPanel panel) {
        if (instance == null) {
            new MainFrame().setVisible(true);
        }
        instance.getContentPane().removeAll();
        instance.getContentPane().add(panel);
        instance.revalidate();
        instance.repaint();
    }
}
