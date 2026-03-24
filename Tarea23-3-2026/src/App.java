import javafx.application.Application;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.Menu;
import javafx.scene.control.MenuBar;
import javafx.scene.control.MenuItem;
import javafx.scene.control.ProgressBar;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import javafx.application.Platform;

public class App extends Application {

    private ObservableList<Producto> listaProductos;
    private TableView<Producto> tabla;
    private Label lblEstado;
    private ProgressBar progressBar;

    @Override
    public void start(Stage stage){
        listaProductos = FXCollections.observableArrayList();

        tabla = new TableView<>();
        tabla.setItems(listaProductos);

        TableColumn<Producto, String> colNombre = new TableColumn<>("Nombre");
        colNombre.setCellValueFactory(new PropertyValueFactory<>("nombre"));

        TableColumn<Producto, String> colCategoria = new TableColumn<>("Categoria");
        colCategoria.setCellValueFactory(new PropertyValueFactory<>("Categoria"));

        TableColumn<Producto, Double> colPrecio = new TableColumn<>("Precio");
        colPrecio.setCellValueFactory(new PropertyValueFactory<>("precio"));

        TableColumn<Producto, Integer> colCantidad = new TableColumn<>("Cantidad");
        colCantidad.setCellValueFactory(new PropertyValueFactory<>("cantidad"));

        tabla.getColumns().addAll(colNombre, colCategoria, colPrecio, colCantidad);

        MenuBar menuBar = new MenuBar();

        Menu menuArchivo = new Menu("Archivo");
        MenuItem itemNuevo = new MenuItem("Nuevo prducto");
        MenuItem itemGuardar = new MenuItem("Guardar");
        MenuItem itemCargar = new MenuItem("Cargar");
        MenuItem itemSalir = new MenuItem("Salir");
        menuArchivo.getItems().addAll(itemNuevo, itemGuardar, itemCargar, itemSalir);

        Menu menueditar = new Menu("Editar");
        MenuItem itemEliminar = new MenuItem("Eliminar seleccionado");
        MenuItem itemLimpiar = new MenuItem("Limpiar lista");
        menueditar.getItems().addAll(itemEliminar, itemLimpiar);

        Menu menuAyuda = new Menu("Ayuda");
        MenuItem itemAcerca = new MenuItem("Acerca de");
        menuAyuda.getItems().add(itemAcerca);

        menuBar.getMenus().addAll(menuArchivo, menueditar, menuAyuda);

        Button btnAgregar = new Button("Agregar");
        Button btnEliminar = new Button("Eliminar");
        Button btnGuardar = new Button("Guardar");
        Button btnCargar = new Button("Cargar");

        HBox botones = new HBox(10);
        botones.getChildren().addAll(btnAgregar, btnEliminar, btnGuardar, btnCargar);

        lblEstado = new Label("Listo");
        progressBar = new ProgressBar(0);
        progressBar.setPrefWidth(300);

        HBox barraEstado = new HBox(10);
        barraEstado.setPadding(new Insets(10));
        barraEstado.getChildren().addAll(lblEstado, progressBar);

        BorderPane root = new BorderPane();

        VBox arriba = new VBox();
        arriba.getChildren().addAll(menuBar, botones);

        root.setTop(arriba);
        root.setCenter(tabla);
        root.setBottom(barraEstado);

        btnAgregar.setOnAction(e -> abrirFormulario());
        itemNuevo.setOnAction(e -> abrirFormulario());

        btnEliminar.setOnAction(e -> eliminarSeleccionado());
        itemEliminar.setOnAction(e -> eliminarSeleccionado());

        btnGuardar.setOnAction(e -> guardarArchivo());
        itemGuardar.setOnAction(e -> guardarArchivo());

        btnCargar.setOnAction(e -> cargarArchivo());
        itemCargar.setOnAction(e -> cargarArchivo());

        itemSalir.setOnAction(e -> {
            if(confirmarSalida()){
                Platform.exit();
                System.exit(0);
            }
            });
        itemLimpiar.setOnAction(e -> {
            listaProductos.clear();
            lblEstado.setText("Lista limpiada");
        });

        stage.setOnCloseRequest(e -> {
            e.consume();

            if(confirmarSalida()){
                Platform.exit();
                System.exit(0);
            }
        });

        itemAcerca.setOnAction(e -> {
            Alert alert = new Alert(Alert.AlertType.INFORMATION);
            alert.setTitle("Acerca de");
            alert.setHeaderText(null);
            alert.setContentText("Aplicación de gestión de productos.");
            alert.showAndWait();

        });

        

        

        stage.setTitle("Inventario de Productos");
        stage.setScene(new Scene(root, 800,500));

        cargarArchivo();

        stage.show();

    }

    private void abrirFormulario(){
        FormularioProducto formulario = new FormularioProducto();
        formulario.showAndWait();

        Producto p = formulario.getProducto();

        if(p != null){
            listaProductos.add(p);
            lblEstado.setText("Producto agregado correctamente");
        }
    }

    private void eliminarSeleccionado(){
        Producto seleccionado = tabla.getSelectionModel().getSelectedItem();

        if(seleccionado != null){
            lblEstado.setText("No hay producto seleccionado");
            return;
        }

        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.setTitle("Confirmar eliminacion");
        alert.setHeaderText(null);
        alert.setContentText("¿Esta seguro de que desea eliminar el producto seleccionado?");

        boolean confirmado = alert.showAndWait().filter(response -> response.getText().contains("ok")).isPresent();

        if(confirmado){
            listaProductos.remove(seleccionado);
            lblEstado.setText("Producto eliminado");
        }
    }

    private void guardarArchivo(){
        if(listaProductos.isEmpty()){
            Alert alert = new Alert(Alert.AlertType.WARNING);
            alert.setTitle("Advertencia");
            alert.setHeaderText(null);
            alert.setContentText("La lista esta vacia");
            alert.showAndWait();

            lblEstado.setText("Lista vacia al intentar guardar");
            return;
        }
        try {
            BufferedWriter bw = new BufferedWriter(new FileWriter("Inventario.txt"));

            for(Producto p : listaProductos){
                bw.write(p.toString());
                bw.newLine();
            }
            
            bw.close();

            lblEstado.setText("GuardoCorrectamente");
            progressBar.setProgress(1.0);
        } catch (IOException e) {
            lblEstado.setText("Error al guardar el archivo");
        }
    }

    private void cargarArchivo(){
       Thread hilo = new Thread(() -> {
            try{
                Platform.runLater(() -> {
                    lblEstado.setText("cargando...");
                    progressBar.setProgress(0);
                });

                BufferedReader br = new BufferedReader(new FileReader("inventario.txt"));

                java.util.List<Producto> productosCargados = new java.util.ArrayList<>();
                java.util.List<String> lineas = new java.util.ArrayList<>();

                String linea;
                while((linea = br.readLine()) != null){
                    lineas.add(linea);
            }
            br.close();

            int total = lineas.size();

            if(total == 0){
                Platform.runLater(() -> {
                    lblEstado.setText("Archivo vacio");
                    progressBar.setProgress(0);
                });
                return;
            }

            for(int i = 0; i < total; i++){
                String[] partes = lineas.get(i).split(" , ");

                if(partes.length == 4) {
                    String nombre = partes[0];
                    String categoria = partes[1];
                    double precio = Double.parseDouble(partes[2]);
                    int cantidad = Integer.parseInt(partes[3]);

                    Producto producto = new Producto(nombre, categoria, precio, cantidad);
                    productosCargados.add(producto);
                }

                Thread.sleep(200);

                double progreso = (i + 1) / (double) total;
                Platform.runLater(() -> progressBar.setProgress(progreso));
             }

             Platform.runLater(() -> {
                listaProductos.setAll(productosCargados);
                lblEstado.setText("Carga compltada");
             });
            }catch(IOException ex){
                Platform.runLater(() -> {
                    lblEstado.setText("No se encontro inventario.txt");
                    progressBar.setProgress(0);
                });
            }catch(Exception ex){
                Platform.runLater(() -> {
                    lblEstado.setText("Error al cargar el archivo");
                    progressBar.setProgress(0);
                });
            }
        });

        hilo.setDaemon(true);
        hilo.start();
    }

    private boolean confirmarSalida(){
        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.setTitle("Confirmar salida");
        alert.setHeaderText(null);
        alert.setContentText("¿Estas seguro de que quieres salir?");

        return alert.showAndWait().filter(button -> button.getButtonData().isDefaultButton()).isPresent();
    }

    public static void main(String[] args) throws Exception {
        launch(args);
    }
}