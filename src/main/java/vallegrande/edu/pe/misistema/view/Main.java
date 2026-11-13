package vallegrande.edu.pe.misistema.view;

import javafx.application.Application;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonType;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import vallegrande.edu.pe.misistema.controller.MainController;
import vallegrande.edu.pe.misistema.model.Libro;

import java.sql.SQLException;
import java.util.List;
import java.util.Optional;

public class Main extends Application {

    private final MainController controller = new MainController();
    private final ObservableList<Libro> datos = FXCollections.observableArrayList();

    private final TableView<Libro> tabla = new TableView<>();
    private final TextField txtId = new TextField();
    private final TextField txtTitulo = new TextField();
    private final TextField txtAutor = new TextField();
    private final TextField txtEditorial = new TextField();
    private final TextField txtCategoria = new TextField();
    private final TextField txtAnio = new TextField();
    private final TextField txtCantidad = new TextField();
    private final ComboBox<String> cmbEstado = new ComboBox<>();
    private final TextField txtBuscar = new TextField();
    private final Label lblEstado = new Label();

    @Override
    public void start(Stage stage) {
        BorderPane root = new BorderPane();
        root.getStyleClass().add("root-pane");
        root.setTop(crearEncabezado());
        root.setLeft(crearFormulario());
        root.setCenter(crearTabla());
        root.setBottom(crearBarraEstado());

        Scene scene = new Scene(root, 1180, 640);
        var css = Main.class.getResource("/vallegrande/edu/pe/misistema/estilos.css");
        if (css != null) {
            scene.getStylesheets().add(css.toExternalForm());
        }

        stage.setTitle("Sistema de Biblioteca - Vallegrande");
        stage.setScene(scene);
        stage.setMinWidth(1000);
        stage.setMinHeight(560);
        stage.show();

        inicializar();
    }

    /* ---------------- Construcción de la interfaz ---------------- */

    private HBox crearEncabezado() {
        Label titulo = new Label("📚 Gestión de Biblioteca");
        titulo.getStyleClass().add("titulo");
        Label sub = new Label("Registro, consulta, actualización y eliminación de libros");
        sub.getStyleClass().add("subtitulo");
        VBox textos = new VBox(2, titulo, sub);
        HBox box = new HBox(textos);
        box.getStyleClass().add("encabezado");
        box.setPadding(new Insets(16, 24, 16, 24));
        return box;
    }

    private VBox crearFormulario() {
        txtId.setEditable(false);
        txtId.setPromptText("Automático");
        txtTitulo.setPromptText("Título del libro");
        txtAutor.setPromptText("Autor");
        txtEditorial.setPromptText("Editorial");
        txtCategoria.setPromptText("Categoría");
        txtAnio.setPromptText("Ej. 2015");
        txtCantidad.setPromptText("Ej. 5");
        cmbEstado.setItems(FXCollections.observableArrayList(MainController.ESTADOS));
        cmbEstado.getSelectionModel().selectFirst();
        cmbEstado.setMaxWidth(Double.MAX_VALUE);

        GridPane form = new GridPane();
        form.setHgap(10);
        form.setVgap(10);
        agregarCampo(form, 0, "ID libro", txtId);
        agregarCampo(form, 1, "Título", txtTitulo);
        agregarCampo(form, 2, "Autor", txtAutor);
        agregarCampo(form, 3, "Editorial", txtEditorial);
        agregarCampo(form, 4, "Categoría", txtCategoria);
        agregarCampo(form, 5, "Año publicación", txtAnio);
        agregarCampo(form, 6, "Cantidad", txtCantidad);
        agregarCampo(form, 7, "Estado", cmbEstado);

        Button btnRegistrar = new Button("Registrar");
        btnRegistrar.getStyleClass().add("btn-primario");
        btnRegistrar.setOnAction(e -> registrar());

        Button btnActualizar = new Button("Actualizar");
        btnActualizar.getStyleClass().add("btn-secundario");
        btnActualizar.setOnAction(e -> actualizar());

        Button btnEliminar = new Button("Eliminar");
        btnEliminar.getStyleClass().add("btn-peligro");
        btnEliminar.setOnAction(e -> eliminar());

        Button btnLimpiar = new Button("Limpiar");
        btnLimpiar.getStyleClass().add("btn-neutro");
        btnLimpiar.setOnAction(e -> limpiarFormulario());

        HBox fila1 = new HBox(10, btnRegistrar, btnActualizar);
        HBox fila2 = new HBox(10, btnEliminar, btnLimpiar);
        for (Button b : List.of(btnRegistrar, btnActualizar, btnEliminar, btnLimpiar)) {
            b.setMaxWidth(Double.MAX_VALUE);
            HBox.setHgrow(b, Priority.ALWAYS);
        }

        Label cab = new Label("Datos del libro");
        cab.getStyleClass().add("seccion");

        VBox panel = new VBox(14, cab, form, fila1, fila2);
        panel.getStyleClass().add("panel");
        panel.setPadding(new Insets(18));
        panel.setPrefWidth(330);
        BorderPane.setMargin(panel, new Insets(0, 0, 0, 16));
        return panel;
    }

    private void agregarCampo(GridPane form, int fila, String etiqueta, javafx.scene.Node campo) {
        Label l = new Label(etiqueta);
        l.getStyleClass().add("etiqueta");
        form.add(l, 0, fila);
        form.add(campo, 1, fila);
        GridPane.setHgrow(campo, Priority.ALWAYS);
    }

    private VBox crearTabla() {
        txtBuscar.setPromptText("Buscar por título, autor, editorial o categoría...");
        txtBuscar.setOnKeyReleased(e -> cargarTabla(txtBuscar.getText()));
        HBox.setHgrow(txtBuscar, Priority.ALWAYS);

        Button btnRefrescar = new Button("Refrescar");
        btnRefrescar.getStyleClass().add("btn-neutro");
        btnRefrescar.setOnAction(e -> {
            txtBuscar.clear();
            cargarTabla("");
        });

        HBox barra = new HBox(10, txtBuscar, btnRefrescar);
        barra.setAlignment(Pos.CENTER_LEFT);

        tabla.setItems(datos);
        tabla.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);        tabla.getColumns().add(columna("ID", "idLibro", 50));
        tabla.getColumns().add(columna("Título", "titulo", 200));
        tabla.getColumns().add(columna("Autor", "autor", 140));
        tabla.getColumns().add(columna("Editorial", "editorial", 130));
        tabla.getColumns().add(columna("Categoría", "categoria", 110));
        tabla.getColumns().add(columna("Año", "anioPublicacion", 70));
        tabla.getColumns().add(columna("Cantidad", "cantidad", 80));
        tabla.getColumns().add(columna("Estado", "estado", 90));
        tabla.getSelectionModel().selectedItemProperty()
                .addListener((obs, anterior, libro) -> mostrarEnFormulario(libro));
        VBox.setVgrow(tabla, Priority.ALWAYS);

        VBox centro = new VBox(12, barra, tabla);
        centro.setPadding(new Insets(0, 16, 0, 16));
        return centro;
    }

    private <T> TableColumn<Libro, T> columna(String titulo, String propiedad, double ancho) {
        TableColumn<Libro, T> c = new TableColumn<>(titulo);
        c.setCellValueFactory(new PropertyValueFactory<>(propiedad));
        c.setPrefWidth(ancho);
        return c;
    }

    private HBox crearBarraEstado() {
        lblEstado.getStyleClass().add("barra-estado-texto");
        HBox box = new HBox(lblEstado);
        box.getStyleClass().add("barra-estado");
        box.setPadding(new Insets(8, 24, 8, 24));
        return box;
    }

    /* ---------------- Acciones ---------------- */

    private void inicializar() {
        try {
            controller.iniciarBaseDeDatos();
            cargarTabla("");
            estado("Conectado a MySQL (localhost:3308, base de datos cine).");
        } catch (SQLException e) {
            estado("Sin conexión a la base de datos.");
            mostrarError("No se pudo conectar a MySQL.\n"
                    + "Verifique que el contenedor Docker \"yalpa\" esté activo en el puerto 3308.\n\n"
                    + e.getMessage());
        }
    }

    private void cargarTabla(String filtro) {
        try {
            datos.setAll(controller.buscar(filtro));
            estado(datos.size() + " libro(s) mostrados.");
        } catch (SQLException e) {
            mostrarError("Error al consultar los libros:\n" + e.getMessage());
        }
    }

    private void registrar() {
        try {
            Libro libro = leerFormulario(0);
            controller.registrar(libro);
            limpiarFormulario();
            cargarTabla(txtBuscar.getText());
            estado("Libro registrado correctamente (ID " + libro.getIdLibro() + ").");
            mostrarInfo("Libro registrado correctamente.");
        } catch (IllegalArgumentException e) {
            mostrarAdvertencia(e.getMessage());
        } catch (SQLException e) {
            mostrarError("Error al registrar el libro:\n" + e.getMessage());
        }
    }

    private void actualizar() {
        try {
            int id = obtenerIdSeleccionado();
            Libro libro = leerFormulario(id);
            controller.actualizar(libro);
            limpiarFormulario();
            cargarTabla(txtBuscar.getText());
            estado("Libro actualizado correctamente.");
            mostrarInfo("Libro actualizado correctamente.");
        } catch (IllegalArgumentException e) {
            mostrarAdvertencia(e.getMessage());
        } catch (SQLException e) {
            mostrarError("Error al actualizar el libro:\n" + e.getMessage());
        }
    }

    private void eliminar() {
        try {
            int id = obtenerIdSeleccionado();
            Alert confirmar = new Alert(Alert.AlertType.CONFIRMATION,
                    "¿Desea eliminar el libro \"" + txtTitulo.getText() + "\"?\nEsta acción no se puede deshacer.",
                    ButtonType.YES, ButtonType.NO);
            confirmar.setTitle("Confirmar eliminación");
            confirmar.setHeaderText(null);
            Optional<ButtonType> respuesta = confirmar.showAndWait();
            if (respuesta.isPresent() && respuesta.get() == ButtonType.YES) {
                controller.eliminar(id);
                limpiarFormulario();
                cargarTabla(txtBuscar.getText());
                estado("Libro eliminado correctamente.");
            }
        } catch (IllegalArgumentException e) {
            mostrarAdvertencia(e.getMessage());
        } catch (SQLException e) {
            mostrarError("Error al eliminar el libro:\n" + e.getMessage());
        }
    }

    /* ---------------- Utilidades de formulario ---------------- */

    private Libro leerFormulario(int id) {
        return controller.construirLibro(id, txtTitulo.getText(), txtAutor.getText(),
                txtEditorial.getText(), txtCategoria.getText(), txtAnio.getText(),
                txtCantidad.getText(), cmbEstado.getValue());
    }

    private int obtenerIdSeleccionado() {
        String texto = txtId.getText();
        if (texto == null || texto.isBlank()) {
            throw new IllegalArgumentException("Seleccione un libro de la tabla.");
        }
        return Integer.parseInt(texto);
    }

    private void mostrarEnFormulario(Libro l) {
        if (l == null) return;
        txtId.setText(String.valueOf(l.getIdLibro()));
        txtTitulo.setText(l.getTitulo());
        txtAutor.setText(l.getAutor());
        txtEditorial.setText(l.getEditorial());
        txtCategoria.setText(l.getCategoria());
        txtAnio.setText(String.valueOf(l.getAnioPublicacion()));
        txtCantidad.setText(String.valueOf(l.getCantidad()));
        cmbEstado.setValue(l.getEstado());
    }

    private void limpiarFormulario() {
        tabla.getSelectionModel().clearSelection();
        txtId.clear();
        txtTitulo.clear();
        txtAutor.clear();
        txtEditorial.clear();
        txtCategoria.clear();
        txtAnio.clear();
        txtCantidad.clear();
        cmbEstado.getSelectionModel().selectFirst();
        txtTitulo.requestFocus();
    }

    private void estado(String mensaje) {
        lblEstado.setText(mensaje);
    }

    private void mostrarInfo(String m) {
        alerta(Alert.AlertType.INFORMATION, "Información", m);
    }

    private void mostrarAdvertencia(String m) {
        alerta(Alert.AlertType.WARNING, "Validación", m);
    }

    private void mostrarError(String m) {
        alerta(Alert.AlertType.ERROR, "Error", m);
    }

    private void alerta(Alert.AlertType tipo, String titulo, String mensaje) {
        Alert a = new Alert(tipo, mensaje, ButtonType.OK);
        a.setTitle(titulo);
        a.setHeaderText(null);
        a.showAndWait();
    }

    public static void main(String[] args) {
        launch(args);
    }
}
