package com.acacioswork;

import javax.swing.SwingUtilities;
import com.formdev.flatlaf.FlatDarkLaf;
import com.acacioswork.interfaz_usuario.Login;
import com.acacioswork.interfaz_usuario.MainFrame;

/** Punto de entrada de la aplicacion AcaciosWork (Escritorio). @author RADJ */
public class App {
    public static void main(String[] args) {
        FlatDarkLaf.setup();

        SwingUtilities.invokeLater(() -> {
            MainFrame.navigateTo(new Login());
        });
    }
}
