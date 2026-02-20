package vista;

import java.net.URL;
import java.time.LocalDate;
import java.util.ResourceBundle;

import controlador.ControladorPrincipal;
import javafx.collections.FXCollections;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.control.Alert;
import javafx.scene.control.ComboBox;
import javafx.scene.control.DatePicker;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.stage.Stage;
import modelo.Categoria;
import modelo.CuentaCompartida;
import modelo.Gasto;
import modelo.Persona;

public class ControladorCrearGasto implements Initializable {

	// --- ELEMENTOS FXML ---
	@FXML private TextField txtConcepto;
	@FXML private TextField txtCantidad;
	@FXML private ComboBox<Categoria> comboCategoria;
	@FXML private DatePicker dateFecha;

	@FXML private Label lblPagador;
	@FXML private ComboBox<Persona> comboPagador;

	// --- ATRIBUTO DE ESTADO ---
	// null = Modo Crear | Objeto = Modo Editar
	private Gasto gastoEnEdicion = null;

	@Override
	public void initialize(URL location, ResourceBundle resources) {
		dateFecha.setValue(LocalDate.now());
		cargarCategorias();

		CuentaCompartida cuentaActiva = ControladorPrincipal.getInstance().getCuentaActiva();
		if (cuentaActiva == null) {
			ocultarSelectorPagador();
		} else {
			mostrarSelectorPagador(cuentaActiva);
		}
	}

	// --- ACCIONES (BOTONES) ---

	@FXML
	void accionCancelar(ActionEvent event) {
		cerrarVentana(event);
	}

	@FXML
	void accionNuevaCategoria(ActionEvent event) {
		ControladorPrincipal.getInstance().mostrarCrearCategoria();

		cargarCategorias();

		if (!comboCategoria.getItems().isEmpty()) {
			comboCategoria.getSelectionModel().selectLast();
		}
	}

	@FXML
	void accionGuardar(ActionEvent event) {
		try {
			String concepto = txtConcepto.getText();
			String cantidadStr = txtCantidad.getText().replace(",", "."); 
			Categoria categoria = comboCategoria.getValue();
			LocalDate fecha = dateFecha.getValue();
			Persona pagador = null;

			if (concepto == null || concepto.trim().isEmpty()) {
				mostrarAlerta("Error", "El concepto no puede estar vacío.");
				return;
			}

			if (categoria == null) {
				mostrarAlerta("Error", "Debes seleccionar una categoría.");
				return;
			}

			if (fecha == null) {
				mostrarAlerta("Error", "Debes seleccionar una fecha.");
				return;
			}

			double cantidad = 0.0;
			try {
				cantidad = Double.parseDouble(cantidadStr.replace(",", "."));
			} catch (NumberFormatException e) {
				mostrarAlerta("Error", "El importe introducido no es válido.");
				return;
			}

			CuentaCompartida cuentaActiva = ControladorPrincipal.getInstance().getCuentaActiva();
			if (cuentaActiva != null) {
				pagador = comboPagador.getValue();
				if (pagador == null) {
					mostrarAlerta("Error", "En una cuenta compartida debes indicar quién pagó.");
					return;
				}
			}

			if (gastoEnEdicion == null) {
				ControladorPrincipal.getInstance().añadirGasto(concepto, cantidad, categoria, fecha, pagador);
			} else {
				boolean exito = ControladorPrincipal.getInstance().editarGasto(
						gastoEnEdicion, 
						concepto, 
						cantidad, 
						categoria, 
						fecha,
						pagador
						);

				if (!exito) {
					mostrarAlerta("Error", "No se pudo actualizar el gasto.");
					return;
				}
			}

			cerrarVentana(event);

		} catch (NumberFormatException e) {
			mostrarAlerta("Error inesperado", "Ocurrió un error al guardar: " + e.getMessage());
			e.printStackTrace();
		}
	}

	// ==========================================
	//           MÉTODOS AUXILIARES
	// ==========================================

	private void cargarCategorias() {
		comboCategoria.setItems(ControladorPrincipal.getInstance().obtenerCategorias());
	}

	private void ocultarSelectorPagador() {
		lblPagador.setVisible(false);
		comboPagador.setVisible(false);
		lblPagador.setManaged(false);
		comboPagador.setManaged(false);
	}

	private void mostrarSelectorPagador(CuentaCompartida cuenta) {
		lblPagador.setVisible(true);
		comboPagador.setVisible(true);
		lblPagador.setManaged(true);
		comboPagador.setManaged(true);

		comboPagador.setItems(FXCollections.observableArrayList(cuenta.getMiembros()));

		if (!cuenta.getMiembros().isEmpty()) {
			comboPagador.getSelectionModel().selectFirst(); 
		}
	}

	public void setGasto(Gasto gasto) {
		this.gastoEnEdicion = gasto;

		txtConcepto.setText(gasto.getConcepto());
		txtCantidad.setText(String.valueOf(gasto.getCantidad()));
		dateFecha.setValue(gasto.getFecha());

		comboCategoria.getSelectionModel().select(gasto.getCategoria());

		if (comboPagador.isVisible() && gasto.getPagador() != null) {
			comboPagador.getSelectionModel().select(gasto.getPagador());
		}
	}

	private void mostrarAlerta(String titulo, String mensaje) {
		Alert alert = new Alert(Alert.AlertType.ERROR);
		alert.setTitle(titulo);
		alert.setContentText(mensaje);
		alert.showAndWait();
	}

	private void cerrarVentana(ActionEvent event) {
		if (event != null && event.getSource() instanceof Node) {
			Node source = (Node) event.getSource();
			Stage stage = (Stage) source.getScene().getWindow();
			stage.close();
		}
	}
}