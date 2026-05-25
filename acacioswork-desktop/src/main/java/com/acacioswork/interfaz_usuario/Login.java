package com.acacioswork.interfaz_usuario;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Cursor;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.GridBagLayout;

import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JPasswordField;
import javax.swing.JTextField;
import javax.swing.SwingConstants;

import com.acacioswork.model.LoginResponse;
import com.acacioswork.util.ApiClient;
import com.acacioswork.util.SessionManager;

/** Panel de inicio de sesión premium para la app de escritorio. @author RADJ */
public class Login extends JPanel {

    private JTextField txtUsuario;
    private JPasswordField txtPassword;
    private final Color PRIMARY_COLOR = new Color(99, 102, 241); // Indigo
    private final Color ACCENT_COLOR = new Color(16, 185, 129); // Emerald

    public Login() {
        setLayout(new BorderLayout());
        initComponents();
    }

    private void initComponents() {
        JPanel mainPanel = new JPanel(new BorderLayout());
        mainPanel.setBackground(new Color(15, 23, 42)); // Slate 900

        // Header
        JPanel header = new JPanel(new GridBagLayout());
        header.setOpaque(false);
        header.setBorder(BorderFactory.createEmptyBorder(40, 0, 0, 0));
        JLabel lblTitle = new JLabel("AcaciosWork");
        lblTitle.setFont(new Font("Inter", Font.BOLD, 32));
        lblTitle.setForeground(new Color(57, 255, 20));
        header.add(lblTitle);
        mainPanel.add(header, BorderLayout.NORTH);

        /** Animación de pulsación de verde neón para el título de inicio de sesión. @author RADJ */
        final Color colorBright = new Color(57, 255, 20);
        final Color colorDim = new Color(20, 90, 7);
        final long startTime = System.currentTimeMillis();
        
        javax.swing.Timer pulseTimer = new javax.swing.Timer(50, e -> {
            long elapsed = System.currentTimeMillis() - startTime;
            double progress = (elapsed % 2000) / 2000.0; // 2 seconds cycle
            double sinVal = Math.sin(progress * 2.0 * Math.PI);
            double factor = (sinVal + 1.0) / 2.0; // range 0.0 to 1.0
            
            int r = (int) (colorDim.getRed() + factor * (colorBright.getRed() - colorDim.getRed()));
            int g = (int) (colorDim.getGreen() + factor * (colorBright.getGreen() - colorDim.getGreen()));
            int b = (int) (colorDim.getBlue() + factor * (colorBright.getBlue() - colorDim.getBlue()));
            
            lblTitle.setForeground(new Color(r, g, b));
        });
        pulseTimer.start();

        // Content
        JPanel content = new JPanel();
        content.setLayout(new BoxLayout(content, BoxLayout.Y_AXIS));
        content.setOpaque(false);
        content.setBorder(BorderFactory.createEmptyBorder(20, 60, 20, 60));

        // Logo / Icon
        JLabel lblLogo = new JLabel("\uD83D\uDED2", SwingConstants.CENTER);
        lblLogo.setFont(new Font("Segoe UI Emoji", Font.PLAIN, 80));
        lblLogo.setForeground(ACCENT_COLOR);
        lblLogo.setAlignmentX(Component.CENTER_ALIGNMENT);
        content.add(lblLogo);
        content.add(Box.createRigidArea(new Dimension(0, 30)));

        // Inputs
        content.add(createLabel("Nombre de Usuario"));
        txtUsuario = createStyledTextField("Usuario");
        content.add(txtUsuario);
        content.add(Box.createRigidArea(new Dimension(0, 15)));

        content.add(createLabel("Contraseña"));
        txtPassword = createStyledPasswordField();
        content.add(txtPassword);
        content.add(Box.createRigidArea(new Dimension(0, 30)));

        // Login Button
        JButton btnLogin = new JButton("Iniciar Sesión");
        btnLogin.setBackground(PRIMARY_COLOR);
        btnLogin.setForeground(Color.WHITE);
        btnLogin.setFont(new Font("Inter", Font.BOLD, 16));
        btnLogin.setAlignmentX(Component.CENTER_ALIGNMENT);
        btnLogin.setMaximumSize(new Dimension(Integer.MAX_VALUE, 45));
        btnLogin.setCursor(new Cursor(Cursor.HAND_CURSOR));
        btnLogin.addActionListener(e -> handleLogin());
        content.add(btnLogin);

        mainPanel.add(content, BorderLayout.CENTER);
        add(mainPanel);
    }

    private JTextField createStyledTextField(String placeholder) {
        JTextField tf = new JTextField();
        tf.setMaximumSize(new Dimension(Integer.MAX_VALUE, 40));
        tf.setBackground(new Color(30, 41, 59));
        tf.setForeground(Color.WHITE);
        tf.setCaretColor(Color.WHITE);
        tf.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(51, 65, 85)),
                BorderFactory.createEmptyBorder(5, 10, 5, 10)));
        return tf;
    }

    private JPasswordField createStyledPasswordField() {
        JPasswordField pf = new JPasswordField();
        pf.setMaximumSize(new Dimension(Integer.MAX_VALUE, 40));
        pf.setBackground(new Color(30, 41, 59));
        pf.setForeground(Color.WHITE);
        pf.setCaretColor(Color.WHITE);
        pf.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(51, 65, 85)),
                BorderFactory.createEmptyBorder(5, 10, 5, 10)));
        return pf;
    }

    private JLabel createLabel(String text) {
        JLabel lbl = new JLabel(text);
        lbl.setForeground(new Color(148, 163, 184));
        lbl.setFont(new Font("Inter", Font.PLAIN, 12));
        lbl.setAlignmentX(Component.LEFT_ALIGNMENT);
        lbl.setBorder(BorderFactory.createEmptyBorder(0, 0, 5, 0));
        return lbl;
    }

    private void handleLogin() {
        String username = txtUsuario.getText();
        String password = new String(txtPassword.getPassword());

        if (username.isEmpty() || password.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Ingresa todos los datos", "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }

        try {
            com.acacioswork.model.LoginRequest loginReq = new com.acacioswork.model.LoginRequest(username, password);

            // El backend devuelve ApiResponse<LoginResponse> con token y usuario
            LoginResponse response = ApiClient.post("/usuarios/login", loginReq, LoginResponse.class);

            if (response != null) {
                SessionManager.setToken(response.getToken());
                SessionManager.setUsuario(response.getUsuario());

                JOptionPane.showMessageDialog(this, "Bienvenido " + response.getUsuario().getNombre(), "Éxito",
                        JOptionPane.INFORMATION_MESSAGE);

                if (response.getUsuario().getIdRol() != null && response.getUsuario().getIdRol() == 1L) {
                    MainFrame.navigateTo(new Administrador());
                } else {
                    MainFrame.navigateTo(new PuntoDeVenta());
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
            String errorMsg = e.getMessage() != null ? e.getMessage() : e.getClass().getSimpleName();
            JOptionPane.showMessageDialog(this, "Error de acceso: " + errorMsg, "Error", JOptionPane.ERROR_MESSAGE);
        }
    }
}
