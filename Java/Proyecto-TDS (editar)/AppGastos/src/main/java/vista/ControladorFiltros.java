package vista;

import java.net.URL;
import java.time.LocalDate;
import java.time.Month;
import java.util.HashSet;
import java.util.Locale;
import java.util.ResourceBundle;
import java.util.Set;

import controlador.ControladorPrincipal;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.control.CheckBox;
import javafx.scene.control.CheckMenuItem;
import javafx.scene.control.DatePicker;
import javafx.scene.control.MenuButton;
import javafx.scene.control.MenuItem;
import javafx.scene.layout.HBox;
import javafx.stage.Stage;
import modelo.*; 

public class ControladorFiltros implements Initializable {

	@FunctionalInterface
	public interface EventoDatosFiltro {
		void enviarDatos(Set<String> categorias, Set<Month> meses, LocalDate inicio, LocalDate fin);
	}

	@FXML private CheckBox checkCategoria, checkMeses, checkFechas;
	@FXML private MenuButton menuCategorias, menuMeses;
	@FXML private DatePicker dpDesde, dpHasta;
	@FXML private HBox boxFechas; 

	private Set<String> catsSeleccionadas = new HashSet<>();
	private Set<Month> mesesSeleccionados = new HashSet<>();

	private EventoDatosFiltro listener;

	@Override
	public void initialize(URL location, ResourceBundle resources) {
		configurarAparicion(menuCategorias, checkCategoria);
		configurarAparicion(menuMeses, checkMeses);
		configurarAparicion(boxFechas, checkFechas);
		cargarCategorias();
		cargarMeses();
	}

	public void setListener(EventoDatosFiltro listener) {
		this.listener = listener;
	}

	@FXML
	void accionAplicar(ActionEvent event) {
		Set<String> envioCats = checkCategoria.isSelected() ? catsSeleccionadas : null;
		Set<Month> envioMeses = checkMeses.isSelected() ? mesesSeleccionados : null;

		LocalDate inicio = null;
		LocalDate fin = null;
		if (checkFechas.isSelected()) {
			inicio = dpDesde.getValue();
			fin = dpHasta.getValue();
		}

		if (listener != null) {
			listener.enviarDatos(envioCats, envioMeses, inicio, fin);
		}

		((Stage)((Node)event.getSource()).getScene().getWindow()).close();
	}

	@FXML
	void accionReiniciar(ActionEvent event) {
		checkCategoria.setSelected(false);
		checkMeses.setSelected(false);
		checkFechas.setSelected(false);
		dpDesde.setValue(null);
		dpHasta.setValue(null);
		catsSeleccionadas.clear();
		mesesSeleccionados.clear();

		// Limpiar selección visual
		menuCategorias.getItems().forEach(item -> {if(item instanceof CheckMenuItem) ((CheckMenuItem)item).setSelected(false);});
		menuMeses.getItems().forEach(item -> {if(item instanceof CheckMenuItem) ((CheckMenuItem)item).setSelected(false);});

		// Enviar señal de limpieza
		if (listener != null) listener.enviarDatos(null, null, null, null);
	}

	private void configurarAparicion(Node nodo, CheckBox check) {
		nodo.visibleProperty().bind(check.selectedProperty());
		nodo.managedProperty().bind(nodo.visibleProperty());
	}

	private void cargarCategorias() {
		menuCategorias.getItems().clear();
		for (Categoria categoria : ControladorPrincipal.getInstance().obtenerCategorias()) {
			CheckMenuItem item = new CheckMenuItem(categoria.getCategoria());
			item.selectedProperty().addListener((o, old, is) -> {
				if (is) catsSeleccionadas.add(categoria.getCategoria()); else catsSeleccionadas.remove(categoria.getCategoria());
			});
			menuCategorias.getItems().add(item);
		}
	}

	private void cargarMeses() {
		menuMeses.getItems().clear();
		for (Month m : Month.values()) {
			String nombre = m.getDisplayName(java.time.format.TextStyle.FULL, new Locale("es", "ES"));
			nombre = nombre.substring(0, 1).toUpperCase() + nombre.substring(1);

			CheckMenuItem item = new CheckMenuItem(nombre);

			item.setUserData(m);
			item.selectedProperty().addListener((obs, wasSelected, isSelected) -> {
				if (isSelected) {
					mesesSeleccionados.add(m);
				} else {
					mesesSeleccionados.remove(m);
				}
			});
			menuMeses.getItems().add(item);
		}
	}

	public void cargarEstado(Set<String> gatosGuardadas, Set<Month> mesesGuardados, LocalDate inicio, LocalDate fin) {

		// 1. Restaurar Categorías
		if (gatosGuardadas != null && !gatosGuardadas.isEmpty()) {
			checkCategoria.setSelected(true);
			// Recorremos el menú y marcamos las que coincidan
			for (MenuItem item : menuCategorias.getItems()) {
				if (item instanceof CheckMenuItem) {
					CheckMenuItem checkItem = (CheckMenuItem) item;
					if (gatosGuardadas.contains(checkItem.getText())) {
						checkItem.setSelected(true); // Esto disparará el listener y llenará 'catsSeleccionadas'
					}
				}
			}
		}

		// 2. Restaurar Meses
		if (mesesGuardados != null && !mesesGuardados.isEmpty()) {
			checkMeses.setSelected(true);

			for (MenuItem item : menuMeses.getItems()) {
				if (item instanceof CheckMenuItem && item.getUserData() instanceof Month) {
					if (mesesGuardados.contains((Month) item.getUserData())) {
						((CheckMenuItem) item).setSelected(true);
					}
				}
			}
		}

		// 3. Restaurar Fechas
		if (inicio != null || fin != null) {
			checkFechas.setSelected(true);
			dpDesde.setValue(inicio);
			dpHasta.setValue(fin);
		}
	}

}