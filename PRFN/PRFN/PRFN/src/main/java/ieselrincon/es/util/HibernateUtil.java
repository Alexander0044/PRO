package ieselrincon.es.util;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

// Clase de utilidad para gestionar la conexión a MySQL
// Solo hay una conexión abierta a la vez (patrón singleton)
public class HibernateUtil {

    // Dirección de la base de datos MySQL (localhost = tu propio ordenador)
    // Cambia "ecotrack" por el nombre que le hayas dado a tu base de datos
    private static final String URL = "jdbc:mysql://localhost:3306/ecotrack";

    // Usuario de MySQL (normalmente "root" en local)
    private static final String USUARIO = "root";

    // Contraseña de MySQL — cámbiala por la tuya
    private static final String PASSWORD = "pxebec01A";

    // Aquí se guarda la conexión una vez abierta
    private static Connection conexion;

    // Se ejecuta una sola vez al cargar la clase: registra el driver de MySQL
    static {
        try {
            Class.forName("com.mysql.cj.jdbc.Driver");
        } catch (ClassNotFoundException e) {
            // Si falla, significa que el conector MySQL no está en el pom.xml
            throw new RuntimeException("Driver MySQL no encontrado. Revisa la dependencia mysql-connector-j en el pom.xml.", e);
        }
    }

    // Devuelve la conexión activa; si no existe o se cerró, abre una nueva
    public static Connection getConexion() {
        try {
            if (conexion == null || conexion.isClosed()) {
                conexion = DriverManager.getConnection(URL, USUARIO, PASSWORD);
            }
        } catch (SQLException e) {
            throw new RuntimeException("No se pudo conectar a MySQL. Comprueba que el servidor está encendido y los datos son correctos.", e);
        }
        return conexion;
    }

    // Cierra la conexión de forma segura cuando ya no se necesita
    public static void cerrar() {
        if (conexion != null) {
            try {
                conexion.close();
            } catch (SQLException e) {
                System.err.println("Error al cerrar la conexión: " + e.getMessage());
            } finally {
                conexion = null;
            }
        }
    }

    // Constructor privado: esta clase no se instancia, solo se usa con HibernateUtil.getConexion()
    private HibernateUtil() {}
}