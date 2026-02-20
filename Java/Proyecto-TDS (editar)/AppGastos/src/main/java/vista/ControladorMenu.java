package vista;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import controlador.ControladorPrincipal;

public class ControladorMenu {

	@FXML private Button btnCuentaPrincipal;
	@FXML private Button btnCuentasCompartidas;

	@FXML
	private void entrarCuentaPrincipal(ActionEvent event) {
		ControladorPrincipal.getInstance().entrarModoPersonal();
	}

	@FXML
	private void entrarCuentasCompartidas(ActionEvent event) {
		ControladorPrincipal.getInstance().mostrarCuentasCompartidas();
	}
}