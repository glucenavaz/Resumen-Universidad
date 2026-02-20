package vista;

import java.net.URL;
import java.util.ResourceBundle;

import controlador.ControladorPrincipal;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Alert;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.RadioButton;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;
import javafx.stage.Stage;
import modelo.Alarma;
import modelo.AlarmaMensual;
import modelo.AlarmaSemanal;
import modelo.Categoria;
import modelo.EstrategiaAlarma;

public class ControladorCrearAlarma implements Initializable {

    @FXML private TextField txtNombre;
    @FXML private TextField txtLimite;
    @FXML private ComboBox<Categoria> comboCategoria;
    @FXML private RadioButton radioMensual;
    @FXML private RadioButton radioSemanal;
    @FXML private Label lblTitulo;
    @FXML private Button btnGuardar;
    
    private Alarma alarmaAEditar = null; //Si es null -> Creando, y si tiene objeto -> Editando

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        comboCategoria.setItems(ControladorPrincipal.getInstance().obtenerCategorias());
    }
    
    //Inyectar los datos cuando vamos a editar
    public void setAlarmaAEditar(Alarma alarma) {
        this.alarmaAEditar = alarma;
        
        lblTitulo.setText("Editar Alarma");
        txtNombre.setText(alarma.getTexto());
        txtLimite.setText(String.valueOf(alarma.getLimite()));
        comboCategoria.setValue(alarma.getCategoria());
        btnGuardar.setText("Guardar cambios");
        
        if (alarma.getEstrategia() instanceof AlarmaMensual) {
            radioMensual.setSelected(true);
        } else {
            radioSemanal.setSelected(true);
        }
    }

    @FXML
    void guardarAlarma(ActionEvent event) {
        try {
            String nombre = txtNombre.getText();
            double limite = Double.parseDouble(txtLimite.getText());
            Categoria cat = comboCategoria.getValue();
            
            EstrategiaAlarma estrategia;
            if (radioMensual.isSelected()) {
                estrategia = new AlarmaMensual();
            } else {
                estrategia = new AlarmaSemanal();
            }

            if (alarmaAEditar == null) {
                //MODO CREAR
                ControladorPrincipal.getInstance().crearAlarma(nombre, limite, cat, estrategia);
            } else {
                //MODO EDITAR
                ControladorPrincipal.getInstance().editarAlarmaExistente(alarmaAEditar, nombre, limite, cat, estrategia);
            }

            ((Stage) txtNombre.getScene().getWindow()).close();

        } catch (NumberFormatException e) {
            mostrarError("El límite debe ser un número válido.");
        } catch (Exception e) {
            mostrarError("Error procesando alarma: " + e.getMessage());
        }
    }
    
    @FXML
    void accionNuevaCategoria(ActionEvent event) {
        ControladorPrincipal.getInstance().mostrarCrearCategoria();
        comboCategoria.setItems(ControladorPrincipal.getInstance().obtenerCategorias());
    }

    private void mostrarError(String msg) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle("Error");
        alert.setHeaderText(null);
        alert.setContentText(msg);
        alert.showAndWait();
    }
}
