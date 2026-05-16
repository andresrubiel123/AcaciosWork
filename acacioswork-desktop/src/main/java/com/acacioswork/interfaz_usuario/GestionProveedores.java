package com.acacioswork.interfaz_usuario;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Cursor;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.GradientPaint;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.RenderingHints;

import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.table.DefaultTableModel;

import com.acacioswork.model.Proveedor;
import com.acacioswork.util.ApiClient;

/** Interfaz para gestión de proveedores conectada a la API. @author RADJ */
public class GestionProveedores extends JPanel {

    private DefaultTableModel modeloTabla;
    private JTable tabla;

    public GestionProveedores() {
        setLayout(new BorderLayout());
        setBackground(new Color(15, 23, 42));

        JPanel panelSuperior = new JPanel(new FlowLayout(FlowLayout.LEFT));
        panelSuperior.setOpaque(false);
        GradientButton btnAtras = new GradientButton("‹ Volver", new Color(99, 102, 241), new Color(79, 70, 229));
        btnAtras.addActionListener(e -> MainFrame.navigateTo(new Administrador()));
        panelSuperior.add(btnAtras);

        JLabel lblTitle = new JLabel("Gestión de Proveedores");
        lblTitle.setFont(new Font("Inter", Font.BOLD, 20));
        lblTitle.setForeground(Color.WHITE);
        panelSuperior.add(lblTitle);
        add(panelSuperior, BorderLayout.NORTH);

        modeloTabla = new DefaultTableModel();
        modeloTabla.addColumn("ID");
        modeloTabla.addColumn("Nombre");
        modeloTabla.addColumn("Teléfono");
        modeloTabla.addColumn("Nro Documento");

        tabla = new JTable(modeloTabla);
        add(new JScrollPane(tabla), BorderLayout.CENTER);

        JPanel panelBotones = new JPanel();
        panelBotones.setOpaque(false);
        GradientButton btnAgregar = new GradientButton("Agregar", new Color(16, 185, 129), new Color(5, 150, 105));
        GradientButton btnEliminar = new GradientButton("Eliminar", new Color(239, 68, 68), new Color(185, 28, 28));

        btnAgregar.addActionListener(e -> agregar());
        btnEliminar.addActionListener(e -> eliminar());

        panelBotones.add(btnAgregar);
        panelBotones.add(btnEliminar);
        add(panelBotones, BorderLayout.SOUTH);

        cargar();
    }

    private void cargar() {
        modeloTabla.setRowCount(0);
        try {
            Proveedor[] arr = ApiClient.get("/proveedores", Proveedor[].class);
            for (Proveedor p : arr) {
                modeloTabla.addRow(new Object[]{ p.getId(), p.getNombre(), p.getTelefono(), p.getNumeroDocumento() });
            }
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, "Error: " + e.getMessage());
        }
    }

    private void agregar() {
        JTextField txtNombre = new JTextField();
        JTextField txtTel = new JTextField();
        JTextField txtDoc = new JTextField();

        Object[] message = { "Nombre:", txtNombre, "Teléfono:", txtTel, "Nro Documento:", txtDoc };
        int option = JOptionPane.showConfirmDialog(this, message, "Nuevo Proveedor", JOptionPane.OK_CANCEL_OPTION);
        if (option == JOptionPane.OK_OPTION) {
            try {
                Proveedor p = new Proveedor();
                p.setNombre(txtNombre.getText());
                p.setTelefono(txtTel.getText());
                p.setNumeroDocumento(txtDoc.getText());
                p.setIdTipoDocumento(1L);
                p.setActivo(1);
                ApiClient.post("/proveedores", p, Proveedor.class);
                cargar();
            } catch (Exception e) {
                JOptionPane.showMessageDialog(this, "Error: " + e.getMessage());
            }
        }
    }

    private void eliminar() {
        int fila = tabla.getSelectedRow();
        if (fila == -1) return;
        Long id = (Long) modeloTabla.getValueAt(fila, 0);
        if (JOptionPane.showConfirmDialog(this, "¿Eliminar?") == JOptionPane.YES_OPTION) {
            try {
                ApiClient.delete("/proveedores/" + id);
                cargar();
            } catch (Exception e) {
                JOptionPane.showMessageDialog(this, "Error: " + e.getMessage());
            }
        }
    }

    private static class GradientButton extends JButton {
        private final Color startColor;
        private final Color endColor;
        public GradientButton(String text, Color s, Color e) {
            super(text); this.startColor = s; this.endColor = e;
            setContentAreaFilled(false); setForeground(Color.WHITE);
            setFocusPainted(false); setCursor(new Cursor(Cursor.HAND_CURSOR));
            setFont(new Font("Inter", Font.BOLD, 13));
        }
        @Override
        protected void paintComponent(Graphics g) {
            Graphics2D g2 = (Graphics2D) g.create();
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            g2.setPaint(new GradientPaint(0, 0, startColor, 0, getHeight(), endColor));
            g2.fillRoundRect(0, 0, getWidth(), getHeight(), 10, 10);
            g2.dispose(); super.paintComponent(g);
        }
    }
}
