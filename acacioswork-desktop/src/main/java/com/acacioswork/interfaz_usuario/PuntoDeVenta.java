package com.acacioswork.interfaz_usuario;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.GridLayout;
import java.util.ArrayList;

import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.table.DefaultTableModel;

import com.acacioswork.model.Cliente;
import com.acacioswork.model.DetalleVenta;
import com.acacioswork.model.Producto;
import com.acacioswork.model.Venta;
import com.acacioswork.util.ApiClient;

/**Interfaz Punto de Venta API REST. @author RADJ */
public class PuntoDeVenta extends JPanel {

    private Venta ventaActual;
    private DefaultTableModel modeloTabla;
    private JTable tablaCarrito;
    private JTextField txtProductoId;
    private JTextField txtCantidad;
    private JComboBox<Cliente> comboClientes;
    private JComboBox<String> comboMetodoPago;
    private JLabel lblTotal;

    public PuntoDeVenta() {
        ventaActual = new Venta();
        ventaActual.setDetalles(new ArrayList<DetalleVenta>());
        initComponents();
    }

    private void initComponents() {
        setLayout(new BorderLayout());
        setBackground(new Color(15, 23, 42));

        // Panel superior: Búsqueda y Selección
        JPanel panelSuperior = new JPanel(new GridLayout(2, 1));
        panelSuperior.setOpaque(false);

        JPanel fila1 = new JPanel(new FlowLayout(FlowLayout.LEFT));
        fila1.setOpaque(false);

        JButton btnAtras = new JButton("‹ Volver");
        btnAtras.addActionListener(e -> MainFrame.navigateTo(new Login()));
        fila1.add(btnAtras);

        fila1.add(createLabel("ID Producto:"));
        txtProductoId = new JTextField(10);
        fila1.add(txtProductoId);

        fila1.add(createLabel("Cantidad:"));
        txtCantidad = new JTextField("1", 5);
        fila1.add(txtCantidad);

        JButton btnAgregar = new JButton("Agregar");
        btnAgregar.addActionListener(e -> agregarProducto());
        fila1.add(btnAgregar);

        JPanel fila2 = new JPanel(new FlowLayout(FlowLayout.LEFT));
        fila2.setOpaque(false);
        fila2.add(createLabel("Cliente:"));
        comboClientes = new JComboBox<>();
        cargarClientes();
        fila2.add(comboClientes);

        fila2.add(createLabel("Método Pago:"));
        comboMetodoPago = new JComboBox<>(new String[] { "Efectivo", "Tarjeta", "Transferencia" });
        fila2.add(comboMetodoPago);

        panelSuperior.add(fila1);
        panelSuperior.add(fila2);
        add(panelSuperior, BorderLayout.NORTH);

        // Tabla del carrito
        modeloTabla = new DefaultTableModel() {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
        modeloTabla.addColumn("ID");
        modeloTabla.addColumn("Producto");
        modeloTabla.addColumn("Cant.");
        modeloTabla.addColumn("Precio");
        modeloTabla.addColumn("Subtotal");

        tablaCarrito = new JTable(modeloTabla);
        add(new JScrollPane(tablaCarrito), BorderLayout.CENTER);

        // Panel inferior: Totales
        JPanel panelInferior = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        panelInferior.setOpaque(false);

        lblTotal = new JLabel("Total: $0.00");
        lblTotal.setFont(new Font("Inter", Font.BOLD, 20));
        lblTotal.setForeground(new Color(16, 185, 129));
        panelInferior.add(lblTotal);

        JButton btnCobrar = new JButton("Procesar Venta");
        btnCobrar.setFont(new Font("Inter", Font.BOLD, 14));
        btnCobrar.setBackground(new Color(16, 185, 129));
        btnCobrar.setForeground(Color.WHITE);
        btnCobrar.addActionListener(e -> procesarVenta());
        panelInferior.add(btnCobrar);

        add(panelInferior, BorderLayout.SOUTH);
    }

    private JLabel createLabel(String text) {
        JLabel lbl = new JLabel(text);
        lbl.setForeground(Color.WHITE);
        return lbl;
    }

    private void cargarClientes() {
        try {
            Cliente[] clientes = ApiClient.get("/clientes", Cliente[].class);
            for (Cliente c : clientes) {
                comboClientes.addItem(c);
            }
        } catch (Exception e) {
            System.err.println("Error cargando clientes: " + e.getMessage());
        }
    }

    private void agregarProducto() {
        try {
            Long id = Long.parseLong(txtProductoId.getText());
            int cant = Integer.parseInt(txtCantidad.getText());

            Producto p = ApiClient.get("/productos/" + id, Producto.class);
            if (p == null) {
                JOptionPane.showMessageDialog(this, "Producto no encontrado.");
                return;
            }

            DetalleVenta dv = new DetalleVenta();
            dv.setIdProducto(p.getId());
            dv.setCantidad(cant);
            dv.setPrecioUnitario(p.getPrecioVenta());
            dv.setSubtotal(cant * p.getPrecioVenta());

            ventaActual.getDetalles().add(dv);

            modeloTabla.addRow(new Object[] {
                    p.getId(),
                    p.getNombre(),
                    cant,
                    p.getPrecioVenta(),
                    cant * p.getPrecioVenta()
            });

            actualizarTotal();
            txtProductoId.setText("");
            txtCantidad.setText("1");
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, "Error: " + e.getMessage());
        }
    }

    private void actualizarTotal() {
        double total = 0;
        for (DetalleVenta dv : ventaActual.getDetalles()) {
            total += dv.getCantidad() * dv.getPrecioUnitario();
        }
        ventaActual.setValorTotal(total);
        lblTotal.setText("Total: $" + String.format("%.2f", total));
    }

    private void procesarVenta() {
        if (ventaActual.getDetalles().isEmpty())
            return;

        try {
            Cliente c = (Cliente) comboClientes.getSelectedItem();
            if (c != null)
                ventaActual.setIdCliente(c.getId());
            
            // Asignar el usuario logueado actualmente
            if (com.acacioswork.util.SessionManager.isLoggedIn()) {
                ventaActual.setIdUsuario(com.acacioswork.util.SessionManager.getUsuario().getId());
            }

            // Enviamos la venta al servidor
            Venta guardada = ApiClient.post("/ventas", ventaActual, Venta.class);

            if (guardada != null) {
                JOptionPane.showMessageDialog(this, "Venta registrada con éxito. ID: " + guardada.getId());
                limpiarVenta();
            }
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, "Error al procesar: " + e.getMessage());
        }
    }

    private void limpiarVenta() {
        ventaActual = new Venta();
        ventaActual.setDetalles(new ArrayList<DetalleVenta>());
        modeloTabla.setRowCount(0);
        actualizarTotal();
    }
}