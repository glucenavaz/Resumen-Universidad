package vista;

import controlador.ControladorPrincipal;
import javafx.collections.FXCollections;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;

import javafx.scene.control.Alert;
import javafx.scene.control.ButtonType;
import javafx.scene.control.ContextMenu;
import javafx.scene.control.Label;
import javafx.scene.control.ListCell;
import javafx.scene.control.MenuItem;
import javafx.scene.control.Button;
import javafx.scene.control.ListView;
import modelo.CuentaCompartida;
import modelo.Persona;


public class ControladorGestorCuentas {

    @FXML private ListView<CuentaCompartida> listaCuentas;
    @FXML private ListView<Persona> listaDetalles;
    @FXML private Label lblInfo;
    @FXML private Button btnCrear;
    @FXML private Button btnEntrar;

    @FXML
	public void initialize() {
		listaCuentas.setItems(ControladorPrincipal.getInstance().getCuentasCompartidas());

		listaCuentas.setCellFactory(lv -> new ListCell<CuentaCompartida>() {
			private final ContextMenu contextMenu = new ContextMenu();
			{
				MenuItem itemBorrar = new MenuItem("Borrar cuenta");
				itemBorrar.setOnAction(event -> {
					CuentaCompartida cuenta = getItem();
					Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
					alert.setTitle("Borrar Cuenta");
					alert.setHeaderText("¿Estás seguro de eliminar '" + cuenta.getNombre() + "'?");
					alert.setContentText("Esta acción no se puede deshacer.");

					if (alert.showAndWait().get() == ButtonType.OK) {
						ControladorPrincipal.getInstance().eliminarCompartida(cuenta);
						listaCuentas.getSelectionModel().clearSelection();
						listaDetalles.getItems().clear();
						getListView().getItems().remove(cuenta);
					}
				});
				contextMenu.getItems().add(itemBorrar);
			}

			@Override
			protected void updateItem(CuentaCompartida item, boolean empty) {
				super.updateItem(item, empty);
				if (empty || item == null) {
					setText(null);
					setContextMenu(null);
				} else {
					setText(item.getNombre());
					setContextMenu(contextMenu);
				}
			}
		});

		listaDetalles.setCellFactory(lv -> new ListCell<Persona>() {
			@Override
			protected void updateItem(Persona p, boolean empty) {
				super.updateItem(p, empty);
				if (empty || p == null) {
					setText(null);
					setStyle("");
				} else {
					String texto = String.format("%s  (%.1f%%)   Saldo: %.2f €", 
							p.getNombre(), 
							p.getPorcentaje(), 
							p.getSaldo());
					
					setText(texto);

					if (p.getSaldo() < 0) {
						setStyle("-fx-text-fill: red; -fx-font-weight: bold;");
					} else if (p.getSaldo() > 0) {
						setStyle("-fx-text-fill: green; -fx-font-weight: bold;");
					} else {
						setStyle("-fx-text-fill: black;");
					}
				}
			}
		});

		listaCuentas.getSelectionModel().selectedItemProperty().addListener((obs, oldVal, newVal) -> {
			 btnEntrar.setDisable(newVal == null);
			 
			 if (newVal != null) {
				 listaDetalles.setItems(FXCollections.observableArrayList(newVal.getMiembros()));
				 lblInfo.setVisible(false);
			 } else {
				 listaDetalles.getItems().clear();
				 lblInfo.setVisible(true);
			 }
		});
	}

	@FXML
	void accionVolver(ActionEvent event) {
		ControladorPrincipal.getInstance().mostrarMenuPrincipal();
	}

    @FXML
    void accionCrear(ActionEvent event) {
        ControladorPrincipal.getInstance().mostrarCrearCuentaCompartida();
        
        listaCuentas.setItems(ControladorPrincipal.getInstance().getCuentasCompartidas());
    }

    @FXML
    void accionEntrar(ActionEvent event) {
        CuentaCompartida seleccionada = listaCuentas.getSelectionModel().getSelectedItem();
        
        if (seleccionada != null) {
            ControladorPrincipal.getInstance().entrarModoCompartido(seleccionada);
        } else {
            Alert alert = new Alert(Alert.AlertType.WARNING);
            alert.setContentText("Por favor, selecciona una cuenta de la lista.");
            alert.showAndWait();
        }
    }
}