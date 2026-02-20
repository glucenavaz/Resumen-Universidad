package vista;

import controlador.ControladorPrincipal;
import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Alert;
import javafx.scene.control.ButtonType;
import javafx.scene.control.MenuButton;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import modelo.Alarma;
import modelo.AlarmaMensual;

import java.net.URL;
import java.util.Optional;
import java.util.ResourceBundle;

public class ControladorGestorAlarmas implements Initializable {

    @FXML private TableView<Alarma> tablaAlarmas;
    @FXML private TableColumn<Alarma, String> colNombre;
    @FXML private TableColumn<Alarma, Double> colLimite;
    @FXML private TableColumn<Alarma, String> colCategoria;
    @FXML private TableColumn<Alarma, String> colTipo;
    @FXML private TableColumn<Alarma, String> colEstado;
    
    @FXML private MenuButton menuOpciones;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        configurarTabla();
        tablaAlarmas.setItems(ControladorPrincipal.getInstance().obtenerAlarmas());
        
        tablaAlarmas.getSelectionModel().selectedItemProperty().addListener((obs, oldSelection, newSelection) -> {
            menuOpciones.setDisable(newSelection == null);
        });
    }

    private void configurarTabla() {
        colNombre.setCellValueFactory(c -> new SimpleStringProperty(c.getValue().getTexto()));
        colLimite.setCellValueFactory(c -> new SimpleObjectProperty<>(c.getValue().getLimite()));
        
        //Control de categoría (por si es null también)
        colCategoria.setCellValueFactory(c -> {
            if (c.getValue().getCategoria() == null) return new SimpleStringProperty("Todas (Global)");
            return new SimpleStringProperty(c.getValue().getCategoria().getCategoria());
        });

        //Tipo (Mensual o Semanal)
        colTipo.setCellValueFactory(c -> {
            if (c.getValue().getEstrategia() instanceof AlarmaMensual) return new SimpleStringProperty("Mensual");
            return new SimpleStringProperty("Semanal");
        });

        // Estado (Activa o Inactiva)
        colEstado.setCellValueFactory(c -> new SimpleStringProperty("Vigilando"));
    }

    @FXML
    void accionNuevaAlarma(ActionEvent event) {
        ControladorPrincipal.getInstance().mostrarCrearAlarma(); 
        tablaAlarmas.refresh();
    }
    
    @FXML
    void accionEliminar(ActionEvent event) {
        Alarma seleccionada = tablaAlarmas.getSelectionModel().getSelectedItem();
        if (seleccionada != null) {
            Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
            alert.setTitle("Eliminar Alarma");
            alert.setHeaderText(null);
            alert.setContentText("¿Borrar la alarma '" + seleccionada.getTexto() + "'?");

            Optional<ButtonType> result = alert.showAndWait();
            if (result.isPresent() && result.get() == ButtonType.OK) {
                ControladorPrincipal.getInstance().eliminarAlarma(seleccionada);
                tablaAlarmas.setItems(ControladorPrincipal.getInstance().obtenerAlarmas());
            }
        }
    }
    
    @FXML
    void accionEditar(ActionEvent event) {
        Alarma seleccionada = tablaAlarmas.getSelectionModel().getSelectedItem();
        if (seleccionada != null) {
            ControladorPrincipal.getInstance().mostrarEditarAlarma(seleccionada);
            tablaAlarmas.refresh();
        }
    }

    @FXML
    void accionVerHistorial(ActionEvent event) {
        ControladorPrincipal.getInstance().mostrarHistorialNotificaciones();
    }

    @FXML
    void accionVolver(ActionEvent event) {
        ControladorPrincipal.getInstance().mostrarCuentaPrincipal();
    }
}
