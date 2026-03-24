import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.stage.Modality;
import javafx.stage.Stage;

public class FormularioProducto  extends Stage{
    
    private  Producto producto;

    public FormularioProducto(){
        setTitle("Nuevo Producto");
        initModality(Modality.APPLICATION_MODAL);

        Label lblNombre = new Label("Nombre:");
        Label lblCategoria = new Label("Categoria:");
        Label lblPrecio = new Label("Precio:");
        Label lblCantidad = new Label("Cantidad:");

        TextField txtNombre = new TextField();
        TextField txtCategoria = new TextField();
        TextField txtPrecio = new TextField();
        TextField txtCantidad = new TextField();

        Button btnGuardar = new Button("Guardar");
        Button btnCancelar = new Button("Cancelar");

        btnGuardar.setOnAction(e -> {

            String nombre = txtNombre.getText().trim();
            String categoria = txtCategoria.getText().trim();
            String precioTexto = txtPrecio.getText().trim();
            String cantidadTexto = txtCantidad.getText().trim();

            if(nombre.isEmpty() || categoria.isEmpty() || precioTexto.isEmpty() || cantidadTexto.isEmpty()) {

                Alert alert = new Alert(Alert.AlertType.ERROR);
                alert.setTitle("Advertencia");
                alert.setHeaderText(null);
                alert.setContentText("Todos los campos son obligarios.");
                alert.showAndWait();
                return;
            }

            try {
                double precio = Double.parseDouble(precioTexto);
                int cantidad = Integer.parseInt(cantidadTexto);

                producto = new Producto(nombre, categoria, precio, cantidad);

                Alert alert = new Alert(Alert.AlertType.INFORMATION);
                alert.setTitle("Exito");
                alert.setHeaderText(null);
                alert.setContentText("Producto guardado exitosamente.");
                alert.showAndWait();

                close();
            }catch(NumberFormatException ex){
                Alert alert = new Alert(Alert.AlertType.ERROR);
                alert.setTitle("Error");
                alert.setHeaderText(null);
                alert.setContentText("Precio o cantidad no son validos.");
                alert.showAndWait();
            }
        });

        btnCancelar.setOnAction(e -> {
            producto = null;
            close();
        });

        GridPane grid = new GridPane();
        grid.setHgap(10);
        grid.setVgap(10);
        grid.setPadding(new Insets(15));

        grid.add(lblNombre, 0, 0);
        grid.add(txtNombre, 1, 0);

        grid.add(lblCategoria, 0, 1);
        grid.add(txtCategoria, 1, 1);

        grid.add(lblPrecio, 0, 2);
        grid.add(txtPrecio, 1, 2);

        grid.add(lblCantidad, 0, 3);
        grid.add(txtCantidad, 1, 3);

        HBox botones = new HBox(10);
        botones.getChildren().addAll(btnGuardar, btnCancelar);
        grid.add(botones, 1, 4);

        Scene scene = new Scene(grid, 350, 220);
        setScene(scene);
    }

    public Producto getProducto(){
        return producto;
    }
}