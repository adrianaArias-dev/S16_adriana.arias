package vallegrande.edu.pe.misistema.model;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

public class LibroDAO {

    private static final String COLUMNAS =
            "id_libro, titulo, autor, editorial, categoria, `año_publicacion`, cantidad, estado";

    public void insertar(Libro l) throws SQLException {
        String sql = "INSERT INTO libros (titulo, autor, editorial, categoria, `año_publicacion`, cantidad, estado) "
                + "VALUES (?, ?, ?, ?, ?, ?, ?)";
        try (Connection con = ConexionBD.getConnection();
             PreparedStatement ps = con.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            ps.setString(1, l.getTitulo());
            ps.setString(2, l.getAutor());
            ps.setString(3, l.getEditorial());
            ps.setString(4, l.getCategoria());
            ps.setInt(5, l.getAnioPublicacion());
            ps.setInt(6, l.getCantidad());
            ps.setString(7, l.getEstado());
            ps.executeUpdate();
            try (ResultSet keys = ps.getGeneratedKeys()) {
                if (keys.next()) {
                    l.setIdLibro(keys.getInt(1));
                }
            }
        }
    }

    public List<Libro> listar() throws SQLException {
        return consultar("SELECT " + COLUMNAS + " FROM libros ORDER BY id_libro DESC", null);
    }

    public List<Libro> buscar(String texto) throws SQLException {
        String sql = "SELECT " + COLUMNAS + " FROM libros "
                + "WHERE titulo LIKE ? OR autor LIKE ? OR editorial LIKE ? OR categoria LIKE ? "
                + "ORDER BY id_libro DESC";
        return consultar(sql, "%" + texto + "%");
    }

    public boolean actualizar(Libro l) throws SQLException {
        String sql = "UPDATE libros SET titulo=?, autor=?, editorial=?, categoria=?, "
                + "`año_publicacion`=?, cantidad=?, estado=? WHERE id_libro=?";
        try (Connection con = ConexionBD.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setString(1, l.getTitulo());
            ps.setString(2, l.getAutor());
            ps.setString(3, l.getEditorial());
            ps.setString(4, l.getCategoria());
            ps.setInt(5, l.getAnioPublicacion());
            ps.setInt(6, l.getCantidad());
            ps.setString(7, l.getEstado());
            ps.setInt(8, l.getIdLibro());
            return ps.executeUpdate() > 0;
        }
    }

    public boolean eliminar(int idLibro) throws SQLException {
        String sql = "DELETE FROM libros WHERE id_libro=?";
        try (Connection con = ConexionBD.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setInt(1, idLibro);
            return ps.executeUpdate() > 0;
        }
    }

    private List<Libro> consultar(String sql, String filtro) throws SQLException {
        List<Libro> lista = new ArrayList<>();
        try (Connection con = ConexionBD.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {
            if (filtro != null) {
                for (int i = 1; i <= 4; i++) {
                    ps.setString(i, filtro);
                }
            }
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    lista.add(new Libro(
                            rs.getInt("id_libro"),
                            rs.getString("titulo"),
                            rs.getString("autor"),
                            rs.getString("editorial"),
                            rs.getString("categoria"),
                            rs.getInt("año_publicacion"),
                            rs.getInt("cantidad"),
                            rs.getString("estado")));
                }
            }
        }
        return lista;
    }
}
