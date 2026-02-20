package vista;

import controlador.ControladorPrincipal;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.Node;
import javafx.scene.control.Alert;
import javafx.scene.control.TextField;
import javafx.stage.Stage;

public class ControladorCrearCategoria {

    @FXML
    private TextField txtNombreCategoria;

    @FXML 
    void accionCrear(ActionEvent event) {
        String nombre = txtNombreCategoria.getText().trim();

        if (nombre.isEmpty()) {
            mostrarAlerta("Nombre vacío", "Debes escribir un nombre para la categoría.");
            return;
        }

        boolean creada = ControladorPrincipal.getInstance().registrarNuevaCategoria(nombre);

        if (creada) {
            cerrarVentana(event);
        } else {
            mostrarAlerta("Error", "Esta categoría ya existe.");
        }

    }

    private void cerrarVentana(ActionEvent event) {
        Node source = (Node) event.getSource();
        Stage stage = (Stage) source.getScene().getWindow();
        stage.close();
    }

    private void mostrarAlerta(String titulo, String contenido) {
        Alert alert = new Alert(Alert.AlertType.WARNING);
        alert.setTitle(titulo);
        alert.setHeaderText(null);
        alert.setContentText(contenido);
        alert.showAndWait();
    }

}