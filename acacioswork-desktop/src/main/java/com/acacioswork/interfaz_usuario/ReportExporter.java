package com.acacioswork.interfaz_usuario;

import java.awt.Component;
import java.awt.Cursor;
import java.awt.Desktop;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import javax.swing.JOptionPane;
import javax.swing.SwingWorker;
import com.acacioswork.util.SessionManager;

/**
 * Exportador y visualizador de reportes ejecutivos en formato HTML/PDF.
 * Coordina la ejecución asíncrona de la compilación de datos y la visualización en navegador.
 * @author RADJ
 */
public class ReportExporter {

    public static void generarReporte(String tipo, Component parent) {
        parent.setCursor(Cursor.getPredefinedCursor(Cursor.WAIT_CURSOR));
        new SwingWorker<File, Void>() {
            private String errorMsg = null;

            @Override
            protected File doInBackground() {
                try {
                    StringBuilder titulo = new StringBuilder();
                    StringBuilder headersHtml = new StringBuilder();
                    StringBuilder rowsHtml = new StringBuilder();
                    StringBuilder resumenHtml = new StringBuilder();
                    String nowStr = LocalDateTime.now().format(DateTimeFormatter.ofPattern("dd/MM/yyyy hh:mm a"));
                    String userName = SessionManager.getUsuario() != null
                            ? SessionManager.getUsuario().getNombre() + " "
                                    + (SessionManager.getUsuario().getApellido() != null && !SessionManager.getUsuario().getApellido().equals("—")
                                            ? SessionManager.getUsuario().getApellido()
                                            : "")
                            : "Administrador";

                    if ("inventario".equals(tipo) || "stock-bajo".equals(tipo) || "proveedores".equals(tipo) || "usuarios".equals(tipo)) {
                        InventoryReportBuilder.build(tipo, titulo, headersHtml, rowsHtml, resumenHtml);
                    } else {
                        FinanceReportBuilder.build(tipo, titulo, headersHtml, rowsHtml, resumenHtml, nowStr);
                    }

                    String html = buildHtmlTemplate(titulo.toString(), nowStr, userName, headersHtml.toString(), rowsHtml.toString(), resumenHtml.toString());

                    File tempFile = File.createTempFile("reporte-" + tipo + "-", ".html");
                    tempFile.deleteOnExit();

                    try (BufferedWriter writer = new BufferedWriter(new FileWriter(tempFile, StandardCharsets.UTF_8))) {
                        writer.write(html);
                    }
                    return tempFile;
                } catch (Exception e) {
                    e.printStackTrace();
                    errorMsg = e.getMessage();
                    return null;
                }
            }

            @Override
            protected void done() {
                parent.setCursor(Cursor.getDefaultCursor());
                try {
                    File file = get();
                    if (file != null) {
                        Desktop.getDesktop().browse(file.toURI());
                    } else {
                        JOptionPane.showMessageDialog(parent,
                                "Error al generar reporte: " + (errorMsg != null ? errorMsg : "Error desconocido"),
                                "Error", JOptionPane.ERROR_MESSAGE);
                    }
                } catch (Exception e) {
                    JOptionPane.showMessageDialog(parent, "Error al abrir reporte: " + e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
                }
            }
        }.execute();
    }

    private static String buildHtmlTemplate(String titulo, String nowStr, String userName, String headers, String rows, String resumen) {
        return "<!DOCTYPE html>\n" +
                "<html lang=\"es\">\n" +
                "<head>\n" +
                "    <meta charset=\"UTF-8\">\n" +
                "    <title>Reporte - " + titulo + "</title>\n" +
                "    <style>\n" +
                "        body {\n" +
                "            font-family: 'Segoe UI', system-ui, sans-serif;\n" +
                "            color: #1e293b;\n" +
                "            background: #ffffff;\n" +
                "            margin: 0;\n" +
                "            padding: 2rem;\n" +
                "        }\n" +
                "        .header {\n" +
                "            border-bottom: 2px solid #0f172a;\n" +
                "            padding-bottom: 1rem;\n" +
                "            margin-bottom: 2rem;\n" +
                "            display: flex;\n" +
                "            justify-content: space-between;\n" +
                "            align-items: flex-end;\n" +
                "        }\n" +
                "        .header h1 {\n" +
                "            margin: 0 0 0.5rem 0;\n" +
                "            font-size: 1.8rem;\n" +
                "            color: #0f172a;\n" +
                "        }\n" +
                "        .header p {\n" +
                "            margin: 0;\n" +
                "            color: #64748b;\n" +
                "            font-size: 0.9rem;\n" +
                "        }\n" +
                "        .meta-info {\n" +
                "            text-align: right;\n" +
                "            font-size: 0.85rem;\n" +
                "            color: #64748b;\n" +
                "        }\n" +
                "        table {\n" +
                "            width: 100%;\n" +
                "            border-collapse: collapse;\n" +
                "            margin-bottom: 2rem;\n" +
                "        }\n" +
                "        th {\n" +
                "            background: #0f172a;\n" +
                "            color: #ffffff;\n" +
                "            text-align: left;\n" +
                "            padding: 0.75rem 1rem;\n" +
                "            font-size: 0.85rem;\n" +
                "            text-transform: uppercase;\n" +
                "            letter-spacing: 0.05em;\n" +
                "        }\n" +
                "        td {\n" +
                "            padding: 0.75rem 1rem;\n" +
                "            border-bottom: 1px solid #e2e8f0;\n" +
                "            font-size: 0.9rem;\n" +
                "        }\n" +
                "        tr:nth-child(even) td {\n" +
                "            background: #f8fafc;\n" +
                "        }\n" +
                "        .summary-box {\n" +
                "            background: #f1f5f9;\n" +
                "            border: 1px solid #e2e8f0;\n" +
                "            border-radius: 0.5rem;\n" +
                "            padding: 1.25rem;\n" +
                "            margin-top: 2rem;\n" +
                "            display: inline-block;\n" +
                "            min-width: 320px;\n" +
                "        }\n" +
                "        .summary-box p {\n" +
                "            margin: 0.35rem 0;\n" +
                "            font-size: 0.95rem;\n" +
                "        }\n" +
                "        .no-print {\n" +
                "            margin-bottom: 1.5rem;\n" +
                "            display: flex;\n" +
                "            gap: 0.75rem;\n" +
                "        }\n" +
                "        .btn-print {\n" +
                "            padding: 0.6rem 1.2rem;\n" +
                "            background: #0f172a;\n" +
                "            color: white;\n" +
                "            border: none;\n" +
                "            border-radius: 0.375rem;\n" +
                "            cursor: pointer;\n" +
                "            font-weight: 600;\n" +
                "            font-size: 0.9rem;\n" +
                "        }\n" +
                "        .btn-close {\n" +
                "            padding: 0.6rem 1.2rem;\n" +
                "            background: #e2e8f0;\n" +
                "            color: #1e293b;\n" +
                "            border: none;\n" +
                "            border-radius: 0.375rem;\n" +
                "            cursor: pointer;\n" +
                "            font-weight: 600;\n" +
                "            font-size: 0.9rem;\n" +
                "        }\n" +
                "        @media print {\n" +
                "            body { padding: 0; }\n" +
                "            .no-print { display: none; }\n" +
                "        }\n" +
                "    </style>\n" +
                "</head>\n" +
                "<body>\n" +
                "    <div class=\"no-print\">\n" +
                "        <button class=\"btn-print\" onclick=\"window.print()\">Imprimir / Guardar PDF</button>\n" +
                "        <button class=\"btn-close\" onclick=\"window.close()\">Cerrar</button>\n" +
                "    </div>\n" +
                "    <div class=\"header\">\n" +
                "        <div>\n" +
                "            <h1>" + titulo + "</h1>\n" +
                "            <p>AcaciosWork — Sistema de Control Administrativo</p>\n" +
                "        </div>\n" +
                "        <div class=\"meta-info\">\n" +
                "            <p><strong>Fecha de Generación:</strong> " + nowStr + "</p>\n" +
                "            <p><strong>Generado por:</strong> " + userName + "</p>\n" +
                "        </div>\n" +
                "    </div>\n" +
                "    \n" +
                "    <table>\n" +
                "        <thead>\n" +
                "            <tr>\n" +
                "                " + headers + "\n" +
                "            </tr>\n" +
                "        </thead>\n" +
                "        <tbody>\n" +
                "            " + rows + "\n" +
                "        </tbody>\n" +
                "    </table>\n" +
                "    \n" +
                "    " + resumen + "\n" +
                "    \n" +
                "    <script>\n" +
                "        window.onload = function() {\n" +
                "            setTimeout(function() {\n" +
                "                window.print();\n" +
                "            }, 500);\n" +
                "        };\n" +
                "    </script>\n" +
                "</body>\n" +
                "</html>";
    }
}
