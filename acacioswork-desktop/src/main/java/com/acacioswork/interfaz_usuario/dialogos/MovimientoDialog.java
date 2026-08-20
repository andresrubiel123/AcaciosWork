package com.acacioswork.interfaz_usuario.dialogos;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.time.LocalDate;
import java.util.HashMap;
import java.util.Map;
import com.acacioswork.interfaz_usuario.Administrador;
import com.acacioswork.model.Producto;
import com.acacioswork.util.ApiClient;
import com.acacioswork.util.SessionManager;

/**
 * Diálogo modal para registrar movimientos de inventario (Entrada/Salida) en el cliente de escritorio.
 * @author RADJ
 */
public class MovimientoDialog extends JDialog {
    private final Frame owner;
    private final Producto producto;
    private final String tipo;
    private final Runnable onSuccess;

    private JSpinner spinCantidad;
    private JTextField txtFechaVencimiento;
    private JTextField txtCodigoLote;
    private JTextField txtReferencia;
    private JTextField txtObservacion;

    public MovimientoDialog(Frame owner, Producto producto, String tipo, Runnable onSuccess) {
        super(owner, tipo.equals("ENTRADA") ? "Registrar Entrada de Stock" : "Registrar Salida de Stock", true);
        this.owner = owner;
        this.producto = producto;
        this.tipo = tipo;
        this.onSuccess = onSuccess;
        initUI();
    }

    private void initUI() {
        setResizable(false);

        spinCantidad = new JSpinner(new SpinnerNumberModel(1, 1, 1000000, 1));
        spinCantidad.putClientProperty("JComponent.roundRect", true);

        txtReferencia = new JTextField();
        txtReferencia.putClientProperty("JTextField.placeholderText", "Referencia (Ej: Factura, Proveedor)");
        txtReferencia.putClientProperty("JComponent.roundRect", true);

        txtObservacion = new JTextField();
        txtObservacion.putClientProperty("JTextField.placeholderText", "Observaciones adicionales");
        txtObservacion.putClientProperty("JComponent.roundRect", true);

        boolean isEntrada = "ENTRADA".equals(tipo);
        if (isEntrada) {
            txtFechaVencimiento = new JTextField(LocalDate.now().plusYears(1).toString());
            txtFechaVencimiento.putClientProperty("JTextField.placeholderText", "YYYY-MM-DD");
            txtFechaVencimiento.putClientProperty("JComponent.roundRect", true);

            txtCodigoLote = new JTextField();
            txtCodigoLote.putClientProperty("JTextField.placeholderText", "Código de Lote");
            txtCodigoLote.putClientProperty("JComponent.roundRect", true);
        }

        JPanel mainPanel = new JPanel();
        mainPanel.setLayout(new BoxLayout(mainPanel, BoxLayout.Y_AXIS));
        mainPanel.setBackground(Administrador.BG_DARK);
        mainPanel.setBorder(new EmptyBorder(24, 28, 24, 28));

        JLabel lblTitle = new JLabel(isEntrada ? "📥 Registrar Entrada" : "📤 Registrar Salida");
        lblTitle.setForeground(Administrador.TEXT_MAIN);
        lblTitle.setFont(new Font("Segoe UI", Font.BOLD, 22));
        lblTitle.setAlignmentX(Component.LEFT_ALIGNMENT);
        mainPanel.add(lblTitle);

        JLabel lblSubtitle = new JLabel("<html>Producto: <strong>" + producto.getNombre() + "</strong> (Stock actual: " + producto.getStockActual() + " u.)</html>");
        lblSubtitle.setForeground(Administrador.TEXT_MUTED);
        lblSubtitle.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        lblSubtitle.setAlignmentX(Component.LEFT_ALIGNMENT);
        mainPanel.add(lblSubtitle);
        mainPanel.add(Box.createVerticalStrut(20));

        java.util.function.BiConsumer<String, JComponent> addField = (label, comp) -> {
            JPanel fPanel = createFieldPanel(label, comp);
            fPanel.setAlignmentX(Component.LEFT_ALIGNMENT);
            mainPanel.add(fPanel);
            mainPanel.add(Box.createVerticalStrut(14));
        };

        addField.accept("Cantidad a transferir", spinCantidad);

        if (isEntrada) {
            JPanel expRow = new JPanel(new GridLayout(1, 2, 16, 0));
            expRow.setBackground(Administrador.BG_DARK);
            expRow.setMaximumSize(new Dimension(Integer.MAX_VALUE, 58));
            expRow.setAlignmentX(Component.LEFT_ALIGNMENT);
            expRow.add(createFieldPanel("Fecha Vencimiento", txtFechaVencimiento));
            expRow.add(createFieldPanel("Código de Lote (Opcional)", txtCodigoLote));
            mainPanel.add(expRow);
            mainPanel.add(Box.createVerticalStrut(14));
        }

        addField.accept("Referencia", txtReferencia);
        addField.accept("Observación", txtObservacion);
        mainPanel.add(Box.createVerticalStrut(10));

        JButton btnCancel = new JButton("Cancelar");
        btnCancel.setBackground(new Color(46, 53, 79));
        btnCancel.setForeground(Color.WHITE);
        btnCancel.setFont(new Font("Segoe UI", Font.BOLD, 14));
        btnCancel.setFocusPainted(false);
        btnCancel.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        btnCancel.putClientProperty("JButton.buttonType", "roundRect");

        JButton btnSave = new JButton(isEntrada ? "Agregar Stock" : "Retirar Stock") {
            @Override
            protected void paintComponent(java.awt.Graphics g) {
                java.awt.Graphics2D g2 = (java.awt.Graphics2D) g.create();
                g2.setRenderingHint(java.awt.RenderingHints.KEY_ANTIALIASING, java.awt.RenderingHints.VALUE_ANTIALIAS_ON);
                Color c1 = isEntrada ? new Color(16, 185, 129) : new Color(239, 68, 68);
                Color c2 = isEntrada ? new Color(5, 150, 105) : new Color(220, 38, 38);
                g2.setPaint(new java.awt.GradientPaint(0, 0, c1, 0, getHeight(), c2));
                g2.fillRoundRect(0, 0, getWidth(), getHeight(), 10, 10);
                g2.dispose();
                super.paintComponent(g);
            }
        };
        btnSave.setForeground(Color.WHITE);
        btnSave.setFont(new Font("Segoe UI", Font.BOLD, 14));
        btnSave.setFocusPainted(false);
        btnSave.setContentAreaFilled(false);
        btnSave.setOpaque(false);
        btnSave.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        btnSave.putClientProperty("JButton.buttonType", "roundRect");

        JPanel btnPanel = new JPanel(new GridLayout(1, 2, 16, 0));
        btnPanel.setBackground(Administrador.BG_DARK);
        btnPanel.setMaximumSize(new Dimension(Integer.MAX_VALUE, 44));
        btnPanel.setAlignmentX(Component.LEFT_ALIGNMENT);
        btnPanel.add(btnCancel);
        btnPanel.add(btnSave);
        mainPanel.add(btnPanel);

        btnCancel.addActionListener(e -> dispose());
        btnSave.addActionListener(e -> {
            try {
                int cantidad = (Integer) spinCantidad.getValue();
                if (cantidad <= 0) {
                    JOptionPane.showMessageDialog(this, "La cantidad debe ser mayor a cero.", "Error", JOptionPane.ERROR_MESSAGE);
                    return;
                }

                String ref = txtReferencia.getText().trim();
                String obs = txtObservacion.getText().trim();

                if (isEntrada) {
                    String fv = txtFechaVencimiento.getText().trim();
                    if (fv.isEmpty()) {
                        JOptionPane.showMessageDialog(this, "La fecha de vencimiento es obligatoria para registrar una entrada.", "Error", JOptionPane.ERROR_MESSAGE);
                        return;
                    }
                    try {
                        LocalDate.parse(fv);
                    } catch (Exception ex) {
                        JOptionPane.showMessageDialog(this, "Fecha de vencimiento inválida. Formato: AAAA-MM-DD.", "Error", JOptionPane.ERROR_MESSAGE);
                        return;
                    }
                    ref = ref.isEmpty() ? "Vencimiento: " + fv : ref + " [" + fv + "]";
                    String lote = txtCodigoLote.getText().trim();
                    if (!lote.isEmpty()) {
                        obs = obs.isEmpty() ? "Lote: " + lote : obs + " [Lote: " + lote + "]";
                    }
                }

                Long userId = SessionManager.getUsuario() != null ? SessionManager.getUsuario().getId() : 1L;

                Map<String, Object> payload = new HashMap<>();
                payload.put("idProducto", producto.getId());
                payload.put("tipoMovimiento", tipo);
                payload.put("cantidad", cantidad);
                payload.put("referencia", ref.isEmpty() ? null : ref);
                payload.put("observacion", obs.isEmpty() ? null : obs);
                payload.put("idUsuario", userId);

                ApiClient.post("/movimientos-inventario", payload, Object.class);
                JOptionPane.showMessageDialog(owner, "Movimiento registrado correctamente.", "Éxito", JOptionPane.INFORMATION_MESSAGE);
                dispose();
                if (onSuccess != null) {
                    onSuccess.run();
                }
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(this, "Error al registrar movimiento: " + ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
            }
        });

        setContentPane(mainPanel);
        pack();
        setSize(460, isEntrada ? 490 : 410);
        setLocationRelativeTo(owner);
    }

    private JPanel createFieldPanel(String labelText, JComponent component) {
        JPanel panel = new JPanel(new BorderLayout(0, 4));
        panel.setBackground(Administrador.BG_DARK);
        JLabel label = new JLabel(labelText);
        label.setForeground(Administrador.TEXT_MUTED);
        label.setFont(new Font("Segoe UI", Font.BOLD, 12));
        panel.add(label, BorderLayout.NORTH);
        component.setPreferredSize(new Dimension(component.getPreferredSize().width, 36));
        panel.add(component, BorderLayout.CENTER);
        return panel;
    }
}
