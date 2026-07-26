package com.acacioswork;

import javax.swing.SwingUtilities;

import com.acacioswork.interfaz_usuario.Login;
import com.acacioswork.interfaz_usuario.MainFrame;
import com.formdev.flatlaf.FlatDarkLaf;

/** punto de entrada de la aplicacion escritorio. @author RADJ */
public class App {
    public static void main(String[] args) {
        FlatDarkLaf.setup();

        SwingUtilities.invokeLater(() -> {
            MainFrame.navigateTo(new Login());
        });
    }
}

