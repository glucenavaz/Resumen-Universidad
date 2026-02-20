package appGastos;

import controlador.ControladorPrincipal;
import javafx.application.Application;
import javafx.stage.Stage;

public class Main extends Application{

	
	@Override
	public void start(Stage primaryStage) {
		ControladorPrincipal.getInstance().iniciarAplicacion(primaryStage);
	}

	public static void main(String[] args) {
		launch(args);
	}
}
