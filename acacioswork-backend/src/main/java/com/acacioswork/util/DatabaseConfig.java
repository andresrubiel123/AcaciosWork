package com.acacioswork.util;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

/** Configuración de conexión a la base de datos MySQL. @author RADJ */
public class DatabaseConfig {

    private static final String DB_URL = "jdbc:mysql://localhost:3306/tienda_acacios";
    private static final String DB_USER = "root";
    private static final String DB_PASSWORD = "";
    private static final String DB_DRIVER = "com.mysql.cj.jdbc.Driver";

    static {
        try {
            Class.forName(DB_DRIVER);
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }
    }

    /** Obtiene una conexión a la base de datos. @author RADJ */
    public static Connection getConnection() throws SQLException {
        return DriverManager.getConnection(DB_URL, DB_USER, DB_PASSWORD);
    }

    /** Cierra la conexión a la base de datos. @author RADJ */
    public static void closeConnection(Connection conn) {
        if (conn != null) {
            try {
                conn.close();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
    }
}
