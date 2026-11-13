package vallegrande.edu.pe.misistema.controller;

import vallegrande.edu.pe.misistema.model.ConexionBD;
import vallegrande.edu.pe.misistema.model.Libro;
import vallegrande.edu.pe.misistema.model.LibroDAO;

import java.sql.SQLException;
import java.time.Year;
import java.util.List;

/** Lógica de la aplicación: valida los datos y delega en el DAO. */
public class MainController {

    public static final List<String> ESTADOS = List.of("Disponible", "Prestado", "Agotado");

    private final LibroDAO dao = new LibroDAO();

    public void iniciarBaseDeDatos() throws SQLException {
        ConexionBD.inicializar();
    }

    public List<Libro> listar() throws SQLException {
        return dao.listar();
    }

    public List<Libro> buscar(String texto) throws SQLException {
        if (texto == null || texto.isBlank()) {
            return dao.listar();
        }
        return dao.buscar(texto.trim());
    }

    public void registrar(Libro libro) throws SQLException {
        dao.insertar(libro);
    }

    public void actualizar(Libro libro) throws SQLException {
        if (libro.getIdLibro() <= 0) {
            throw new IllegalArgumentException("Seleccione un libro de la tabla para actualizar.");
        }
        if (!dao.actualizar(libro)) {
            throw new IllegalArgumentException("El libro ya no existe en la base de datos.");
        }
    }

    public void eliminar(int idLibro) throws SQLException {
        if (idLibro <= 0) {
            throw new IllegalArgumentException("Seleccione un libro de la tabla para eliminar.");
        }
        if (!dao.eliminar(idLibro)) {
            throw new IllegalArgumentException("El libro ya no existe en la base de datos.");
        }
    }

    /** Valida los textos del formulario y construye el objeto Libro. */
    public Libro construirLibro(int id, String titulo, String autor, String editorial,
                                String categoria, String anio, String cantidad, String estado) {
        titulo = limpiar(titulo);
        autor = limpiar(autor);
        editorial = limpiar(editorial);
        categoria = limpiar(categoria);

        if (titulo.isEmpty()) throw new IllegalArgumentException("El título es obligatorio.");
        if (titulo.length() > 150) throw new IllegalArgumentException("El título no puede superar 150 caracteres.");
        if (autor.isEmpty()) throw new IllegalArgumentException("El autor es obligatorio.");
        if (editorial.isEmpty()) throw new IllegalArgumentException("La editorial es obligatoria.");
        if (categoria.isEmpty()) throw new IllegalArgumentException("La categoría es obligatoria.");

        int anioNum = aEntero(anio, "El año de publicación debe ser un número entero.");
        int maxAnio = Year.now().getValue();
        if (anioNum < 1000 || anioNum > maxAnio) {
            throw new IllegalArgumentException("El año de publicación debe estar entre 1000 y " + maxAnio + ".");
        }

        int cantidadNum = aEntero(cantidad, "La cantidad debe ser un número entero.");
        if (cantidadNum < 0) {
            throw new IllegalArgumentException("La cantidad no puede ser negativa.");
        }

        if (estado == null || !ESTADOS.contains(estado)) {
            throw new IllegalArgumentException("Seleccione un estado válido.");
        }

        return new Libro(id, titulo, autor, editorial, categoria, anioNum, cantidadNum, estado);
    }

    private String limpiar(String s) {
        return s == null ? "" : s.trim();
    }

    private int aEntero(String s, String mensaje) {
        try {
            return Integer.parseInt(limpiar(s));
        } catch (NumberFormatException e) {
            throw new IllegalArgumentException(mensaje);
        }
    }
}
