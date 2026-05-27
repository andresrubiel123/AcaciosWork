package com.acacioswork.interfaz_usuario;

import com.acacioswork.model.Configuracion;
import com.acacioswork.util.ConfiguracionManager;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;

public class GestionConfiguracion extends JPanel {

    private JTextField txtNombreEmpresa;
    private JComboBox<String> cbIdioma;
    private JTextField txtMoneda;
    private JTextField txtLector;
    private JTextField txtImpresora;
    private JTextField txtTicketLogo;
    private JTextArea txtTicketEncabezado;
    private JTextArea txtTicketPie;
    private JSpinner spAnchoMm;
    private JSpinner spAltoMm;
    private JSpinner spMargenIzq;
    private JSpinner spMargenDer;

    public GestionConfiguracion() {
        setLayout(new BorderLayout());
        setBorder(new EmptyBorder(20, 20, 20, 20));

        JLabel titleLabel = new JLabel("Configuración del Sistema");
        titleLabel.setFont(new Font("Inter", Font.BOLD, 24));
        titleLabel.setBorder(new EmptyBorder(0, 0, 20, 0));
        add(titleLabel, BorderLayout.NORTH);

        JTabbedPane tabbedPane = new JTabbedPane();
        tabbedPane.setFont(new Font("Inter", Font.PLAIN, 14));

        tabbedPane.addTab("General", buildGeneralPanel());
        tabbedPane.addTab("Hardware", buildHardwarePanel());
        tabbedPane.addTab("Control del Ticket", buildTicketPanel());

        add(tabbedPane, BorderLayout.CENTER);

        JPanel bottomPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        JButton btnGuardar = new JButton("Guardar Configuración");
        btnGuardar.setFont(new Font("Inter", Font.BOLD, 14));
        btnGuardar.setBackground(new Color(16, 185, 129));
        btnGuardar.setForeground(Color.WHITE);
        btnGuardar.setFocusPainted(false);
        btnGuardar.addActionListener(e -> guardarConfiguracion());
        bottomPanel.add(btnGuardar);

        add(bottomPanel, BorderLayout.SOUTH);

        cargarDatos();
    }

    private JPanel buildGeneralPanel() {
        JPanel panel = new JPanel(new GridLayout(0, 2, 10, 10));
        panel.setBorder(new EmptyBorder(20, 20, 20, 20));

        panel.add(new JLabel("Nombre de la Empresa:"));
        txtNombreEmpresa = new JTextField();
        panel.add(txtNombreEmpresa);

        panel.add(new JLabel("Idioma:"));
        cbIdioma = new JComboBox<>(new String[]{"es", "en"});
        panel.add(cbIdioma);

        panel.add(new JLabel("Moneda (Ej. COP, USD):"));
        txtMoneda = new JTextField();
        panel.add(txtMoneda);

        // Fill empty spaces to maintain layout
        for (int i = 0; i < 10; i++) panel.add(new JLabel());
        
        return panel;
    }

    private JPanel buildHardwarePanel() {
        JPanel panel = new JPanel(new GridLayout(0, 2, 10, 10));
        panel.setBorder(new EmptyBorder(20, 20, 20, 20));

        panel.add(new JLabel("Lector de Código de Barras:"));
        txtLector = new JTextField();
        panel.add(txtLector);

        panel.add(new JLabel("Impresora Activa:"));
        txtImpresora = new JTextField();
        panel.add(txtImpresora);

        for (int i = 0; i < 12; i++) panel.add(new JLabel());

        return panel;
    }

    private JPanel buildTicketPanel() {
        JPanel panel = new JPanel(new GridLayout(0, 2, 10, 10));
        panel.setBorder(new EmptyBorder(20, 20, 20, 20));

        panel.add(new JLabel("URL/Base64 Logotipo:"));
        txtTicketLogo = new JTextField();
        panel.add(txtTicketLogo);

        panel.add(new JLabel("Encabezado del Ticket:"));
        txtTicketEncabezado = new JTextArea(3, 20);
        panel.add(new JScrollPane(txtTicketEncabezado));

        panel.add(new JLabel("Pie de Página del Ticket:"));
        txtTicketPie = new JTextArea(3, 20);
        panel.add(new JScrollPane(txtTicketPie));

        panel.add(new JLabel("Ancho (mm):"));
        spAnchoMm = new JSpinner(new SpinnerNumberModel(80, 40, 200, 1));
        panel.add(spAnchoMm);

        panel.add(new JLabel("Alto (mm):"));
        spAltoMm = new JSpinner(new SpinnerNumberModel(297, 50, 500, 1));
        panel.add(spAltoMm);

        panel.add(new JLabel("Margen Izquierdo (mm):"));
        spMargenIzq = new JSpinner(new SpinnerNumberModel(5, 0, 50, 1));
        panel.add(spMargenIzq);

        panel.add(new JLabel("Margen Derecho (mm):"));
        spMargenDer = new JSpinner(new SpinnerNumberModel(5, 0, 50, 1));
        panel.add(spMargenDer);

        return panel;
    }

    private void cargarDatos() {
        Configuracion config = ConfiguracionManager.getConfiguracion();
        if (config != null) {
            txtNombreEmpresa.setText(config.getNombreEmpresa());
            cbIdioma.setSelectedItem(config.getIdioma());
            txtMoneda.setText(config.getMoneda());
            txtLector.setText(config.getLectorCodigoBarras());
            txtImpresora.setText(config.getImpresoraActiva());
            txtTicketLogo.setText(config.getTicketLogotipo());
            txtTicketEncabezado.setText(config.getTicketEncabezado());
            txtTicketPie.setText(config.getTicketPiePagina());
            spAnchoMm.setValue(config.getTicketAnchoMm() != null ? config.getTicketAnchoMm() : 80);
            spAltoMm.setValue(config.getTicketAltoMm() != null ? config.getTicketAltoMm() : 297);
            spMargenIzq.setValue(config.getTicketMargenIzq() != null ? config.getTicketMargenIzq() : 5);
            spMargenDer.setValue(config.getTicketMargenDer() != null ? config.getTicketMargenDer() : 5);
        }
    }

    private void guardarConfiguracion() {
        try {
            Configuracion config = new Configuracion();
            config.setNombreEmpresa(txtNombreEmpresa.getText());
            config.setIdioma((String) cbIdioma.getSelectedItem());
            config.setMoneda(txtMoneda.getText());
            config.setLectorCodigoBarras(txtLector.getText());
            config.setImpresoraActiva(txtImpresora.getText());
            config.setTicketLogotipo(txtTicketLogo.getText());
            config.setTicketEncabezado(txtTicketEncabezado.getText());
            config.setTicketPiePagina(txtTicketPie.getText());
            config.setTicketAnchoMm((Integer) spAnchoMm.getValue());
            config.setTicketAltoMm((Integer) spAltoMm.getValue());
            config.setTicketMargenIzq((Integer) spMargenIzq.getValue());
            config.setTicketMargenDer((Integer) spMargenDer.getValue());

            ConfiguracionManager.saveConfiguracion(config);
            JOptionPane.showMessageDialog(this, "Configuración guardada exitosamente.", "Éxito", JOptionPane.INFORMATION_MESSAGE);
            // Notify other components if needed, or prompt restart for major changes.
        } catch (Exception ex) {
            ex.printStackTrace();
            JOptionPane.showMessageDialog(this, "Error al guardar la configuración: " + ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        }
    }
}
