package vista;

import java.net.URL;
import java.time.format.DateTimeFormatter;
import java.util.ResourceBundle;

import controlador.ControladorPrincipal;
import javafx.beans.property.SimpleStringProperty;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import modelo.Notificacion;

public class ControladorHistorialNotificaciones implements Initializable {

    @FXML private TableView<Notificacion> tablaNotificaciones;
    @FXML private TableColumn<Notificacion, String> colFecha;
    @FXML private TableColumn<Notificacion, String> colAlarma;
    @FXML private TableColumn<Notificacion, String> colMensaje;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        //Configuración de las columnas
        colFecha.setCellValueFactory(c -> new SimpleStringProperty(
            c.getValue().getFecha().format(DateTimeFormatter.ofPattern("dd/MM/yyyy"))
        ));
        
        colAlarma.setCellValueFactory(c -> new SimpleStringProperty(
            c.getValue().getAlarmaOrigen().getTexto()
        ));
        
        colMensaje.setCellValueFactory(c -> new SimpleStringProperty(
            c.getValue().getMensaje()
        ));

        tablaNotificaciones.setItems(ControladorPrincipal.getInstance().obtenerHistorialNotificaciones());
    }
}
