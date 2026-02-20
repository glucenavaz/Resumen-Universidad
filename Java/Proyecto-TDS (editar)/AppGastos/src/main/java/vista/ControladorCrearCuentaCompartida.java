package vista;

import java.net.URL;
import java.util.ResourceBundle;
import java.util.HashMap;
import java.util.Map;

import controlador.ControladorPrincipal;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.control.*;
import javafx.scene.input.KeyCode;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.stage.Stage;

public class ControladorCrearCuentaCompartida implements Initializable {

	@FXML private TextField txtNombreCuenta;
	@FXML private TextField txtNombreParticipante;
	@FXML private ListView<ParticipanteRow> listaParticipantes;
	@FXML private CheckBox checkEquitativo;

	private ObservableList<ParticipanteRow> listaDatos;

	//Nos ayuda a guardar el texto del porcentaje mientras el usuario escribe
	public static class ParticipanteRow {
		String nombre;
		StringProperty porcentajeTexto = new SimpleStringProperty(""); // Empieza vacío

		public ParticipanteRow(String nombre) {
			this.nombre = nombre;
		}
	}

	@Override
	public void initialize(URL location, ResourceBundle resources) {
		listaDatos = FXCollections.observableArrayList();
		listaParticipantes.setItems(listaDatos);

		// Añadir rápido con enter
		txtNombreParticipante.setOnKeyPressed(event -> {
			if (event.getCode() == KeyCode.ENTER) accionAnadirParticipante(null);
		});

		listaParticipantes.setCellFactory(lv -> new ListCell<ParticipanteRow>() {

			// Elementos visuales de la fila
			private final HBox rootBox = new HBox(10);
			private final Label lblNombre = new Label();
			private final TextField txtPorcentaje = new TextField();
			private final Label lblSimbolo = new Label("%");
			private final ContextMenu contextMenu = new ContextMenu();

			{
				rootBox.setAlignment(Pos.CENTER_LEFT);
				txtPorcentaje.setPrefWidth(60);
				txtPorcentaje.setPromptText("0.0");

				txtPorcentaje.visibleProperty().bind(checkEquitativo.selectedProperty().not());
				lblSimbolo.visibleProperty().bind(checkEquitativo.selectedProperty().not());

				HBox spacer = new HBox();
				HBox.setHgrow(spacer, Priority.ALWAYS);

				rootBox.getChildren().addAll(lblNombre, spacer, txtPorcentaje, lblSimbolo);

				MenuItem itemEliminar = new MenuItem("Eliminar participante");
				itemEliminar.setOnAction(e -> listaDatos.remove(getItem()));
				contextMenu.getItems().add(itemEliminar);
			}

			@Override
			protected void updateItem(ParticipanteRow item, boolean empty) {
				super.updateItem(item, empty);

				if (empty || item == null) {
					setText(null);
					setGraphic(null);
					setContextMenu(null);
				} else {
					lblNombre.setText(item.nombre);

					txtPorcentaje.setText(item.porcentajeTexto.get());

					// Guardamos lo que escriba el usuario
					txtPorcentaje.setOnKeyReleased(e -> item.porcentajeTexto.set(txtPorcentaje.getText()));

					setGraphic(rootBox);
					setContextMenu(contextMenu);
				}
			}
		});
	}

	@FXML
	void accionAnadirParticipante(ActionEvent event) {
		String nombre = txtNombreParticipante.getText().trim();
		if (nombre.isEmpty()) return;

		//Tratamiento para evitar duplicados
		boolean existe = listaDatos.stream().anyMatch(row -> row.nombre.equalsIgnoreCase(nombre));
		if (existe) {
			mostrarAlerta("Duplicado", "Ya existe un participante con ese nombre.");
			return;
		}

		listaDatos.add(new ParticipanteRow(nombre));
		txtNombreParticipante.clear();
		txtNombreParticipante.requestFocus();
	}

	@FXML
	void accionGuardar(ActionEvent event) {
		String nombreCuenta = txtNombreCuenta.getText().trim();

		if (nombreCuenta.isEmpty()) {
			mostrarAlerta("Falta nombre", "Pon un nombre a la cuenta.");
			return;
		}

		if (listaDatos.size() < 2) {
			mostrarAlerta("Faltan participantes", "Una cuenta compartida necesita al menos 2 personas.");
			return;
		}

		boolean esEquitativo = checkEquitativo.isSelected();

		Map<String, Double> datosParaEnviar = new HashMap<>(); 
		double suma = 0.0;

		for (ParticipanteRow row : listaDatos) {
			double porc = 0.0;

			if (esEquitativo) {
				porc = -1.0; 
			} else {
				try {
					if (row.porcentajeTexto.get().isEmpty()) {
						mostrarAlerta("Faltan datos", "Indica el porcentaje para " + row.nombre);
						return;
					}

					porc = Double.parseDouble(row.porcentajeTexto.get());

					if (porc <= 0) {
						mostrarAlerta("Error", "El porcentaje de " + row.nombre + " debe ser positivo.");
						return;
					}
					suma += porc;
				} catch (NumberFormatException e) {
					mostrarAlerta("Error", "Número inválido para " + row.nombre);
					return;
				}
			}

			datosParaEnviar.put(row.nombre, porc);
		}

		if (!esEquitativo && Math.abs(suma - 100.0) > 0.01) {
			mostrarAlerta("Suma incorrecta", "Los porcentajes suman " + suma + "%. Deben sumar 100%.");
			return;
		}

		ControladorPrincipal.getInstance().crearCompartida(nombreCuenta, datosParaEnviar, esEquitativo);

		cerrarVentana(event);
	}

	@FXML
	void accionCancelar(ActionEvent event) {
		cerrarVentana(event);
	}

	private void cerrarVentana(ActionEvent event) {
		Node source = (Node) event.getSource();
		Stage stage = (Stage) source.getScene().getWindow();
		stage.close();
	}

	private void mostrarAlerta(String titulo, String contenido) {
		Alert alert = new Alert(Alert.AlertType.WARNING);
		alert.setTitle(titulo);
		alert.setContentText(contenido);
		alert.showAndWait();
	}
}