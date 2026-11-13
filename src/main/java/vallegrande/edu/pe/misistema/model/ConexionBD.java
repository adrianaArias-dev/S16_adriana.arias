package vallegrande.edu.pe.misistema.model;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;

/**
 * Conexión JDBC al MySQL local en Docker:
 * docker run --name yalpa -e MYSQL_ROOT_PASSWORD=12345678 -e MYSQL_DATABASE=cine -p 3308:3306 -d mysql:latest
 */
public final class ConexionBD {

    private static final String URL =
            "jdbc:mysql://localhost:3308/cine"
                    + "?useSSL=false&allowPublicKeyRetrieval=true"
                    + "&serverTimezone=America/Lima&characterEncoding=UTF-8";
    private static final String USUARIO = "root";
    private static final String CLAVE = "12345678";

    private ConexionBD() {
    }

    public static Connection getConnection() throws SQLException {
        return DriverManager.getConnection(URL, USUARIO, CLAVE);
    }

    /** Crea la tabla "libros" si todavía no existe. */
    public static void inicializar() throws SQLException {
        String sql = """
                CREATE TABLE IF NOT EXISTS libros (
                    id_libro          INT AUTO_INCREMENT PRIMARY KEY,
                    titulo            VARCHAR(150) NOT NULL,
                    autor             VARCHAR(100) NOT NULL,
                    editorial         VARCHAR(100) NOT NULL,
                    categoria         VARCHAR(60)  NOT NULL,
                    `año_publicacion` INT          NOT NULL,
                    cantidad          INT          NOT NULL DEFAULT 0,
                    estado            VARCHAR(20)  NOT NULL DEFAULT 'Disponible'
                ) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4
                """;
        try (Connection con = getConnection(); Statement st = con.createStatement()) {
            st.execute(sql);
        }
    }
}
