package com.acacioswork.interfaz_usuario;

import java.awt.Color;
import java.awt.Font;
import javax.swing.JLabel;

/**
 * Etiqueta personalizada que realiza una animación de pulsación de color naranja.
 * @author RADJ
 */
public class PulsingAnswerLabel extends JLabel {
    private static final Color ORANGE_MAIN = new Color(249, 115, 22);
    private static final Color ORANGE_DIM = new Color(154, 52, 18);
    private double factor = 0.0;

    public PulsingAnswerLabel() {
        setForeground(ORANGE_MAIN);
        setFont(new Font("Inter", Font.BOLD, 13));
    }

    public void setFactor(double f) {
        this.factor = f;
        int r = (int) (ORANGE_DIM.getRed() + factor * (ORANGE_MAIN.getRed() - ORANGE_DIM.getRed()));
        int g = (int) (ORANGE_DIM.getGreen() + factor * (ORANGE_MAIN.getGreen() - ORANGE_DIM.getGreen()));
        int b = (int) (ORANGE_DIM.getBlue() + factor * (ORANGE_MAIN.getBlue() - ORANGE_DIM.getBlue()));
        setForeground(new Color(r, g, b));
    }
}
