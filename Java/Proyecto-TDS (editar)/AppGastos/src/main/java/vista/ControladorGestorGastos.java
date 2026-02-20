package vista;

import java.io.File;
import java.net.URL;
import java.time.format.DateTimeFormatter;
import java.util.ResourceBundle;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonType;
import javafx.scene.control.ContextMenu;
import javafx.scene.control.Label;
import javafx.scene.control.ListCell;
import javafx.scene.control.ListView;
import javafx.scene.control.MenuItem;
import javafx.scene.layout.VBox;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import javafx.util.Callback;
import javafx.collections.FXCollections;
import javafx.collections.ListChangeListener;
import javafx.collections.ObservableList;
import javafx.collections.transformation.FilteredList;
import javafx.event.ActionEvent;
import modelo.CuentaCompartida;
import modelo.Gasto;
import modelo.Persona;
import controlador.ControladorPrincipal;

public class ControladorGestorGastos implements Initializable {

    // --- ELEMENTOS DE LA UI ---
    @FXML private ListView<Gasto> listaGastos;
    @FXML private Button btnFiltrar;
    @FXML private Button btnNuevoGasto;
    @FXML private Button btnConsultar;
    @FXML private Button btnNuevaAlerta;
    @FXML private Button btnImportar;

    @FXML private Label lblNombreCuenta;

    @FXML private ListView<Persona> listaBalances;
    @FXML private VBox panelBalances;

    private FilteredList<Gasto> listaFiltrada;

    @Override
    public void initialize(URL Location, ResourceBundle resources) {
        ObservableList<Gasto> listaMaestra = ControladorPrincipal.getInstance().getGastos();
        this.listaFiltrada = new FilteredList<Gasto>(listaMaestra, p -> true);
        listaGastos.setItems(this.listaFiltrada);
                
        configurarLista();
        configurarListaBalances();
        actualizarInfoContexto();

        listaMaestra.addListener((ListChangeListener<Gasto>) c -> {
            if (listaBalances != null && listaBalances.isVisible()) {
                listaBalances.refresh(); 
            }
        });
    }

    // ==========================================
    //        MÉTODO DE CONFIGURACIÓN VISUAL
    // ==========================================

    private void actualizarInfoContexto() {
        CuentaCompartida cuenta = ControladorPrincipal.getInstance().getCuentaActiva();

        if (lblNombreCuenta != null) {
            if (cuenta == null) {
                lblNombreCuenta.setText("(Cuenta Principal)");
            } else {
                lblNombreCuenta.setText("(" + cuenta.getNombre() + ")");
            }
        }

        if (cuenta == null) {
            if (panelBalances != null) {
                panelBalances.setVisible(false);
                panelBalances.setManaged(false);
            }
            //mostrar alarmas (Solo modo personal)
            if (btnNuevaAlerta != null) {
                btnNuevaAlerta.setVisible(true);
                btnNuevaAlerta.setManaged(true);
            }
        } else {
            if (panelBalances != null && listaBalances != null) {
                panelBalances.setVisible(true);
                panelBalances.setManaged(true);
                listaBalances.setItems(FXCollections.observableArrayList(cuenta.getMiembros()));
            }
            if (btnNuevaAlerta != null) {
                btnNuevaAlerta.setVisible(false);
                btnNuevaAlerta.setManaged(false);
            }
        }
    }

    private void configurarLista() {
        listaGastos.setCellFactory(new Callback<ListView<Gasto>, ListCell<Gasto>>() {
            @Override
            public ListCell<Gasto> call(ListView<Gasto> param) {
                return new ListCell<Gasto>() {
                    private final DateTimeFormatter fmt = DateTimeFormatter.ofPattern("dd/MM/yyyy");

                    @Override
                    protected void updateItem(Gasto item, boolean empty) {
                        super.updateItem(item, empty);

                        if (empty || item == null) {
                            setText(null);
                            setContextMenu(null); 
                        } else {
                            String texto = "Concepto: " + item.getConcepto() + 
                                    " | " + item.getCategoria().getCategoria() + 
                                    " | Coste: " + String.format("%.2f", item.getCantidad()) + "€" + 
                                    " | " + item.getFecha().format(fmt);

                            if (item.esCompartido() && item.getPagador() != null) {
                                texto += " [Pagado por: " + item.getPagador().toString() + "]";
                            }
                            setText(texto);

                            ContextMenu contextMenu = new ContextMenu();
                            MenuItem itemEditar = new MenuItem("Editar");
                            itemEditar.setOnAction(e -> {
                                ControladorPrincipal.getInstance().mostrarEditarGasto(item);
                            });

                            MenuItem itemBorrar = new MenuItem("Borrar");
                            itemBorrar.setOnAction(e -> {
                                confirmarYBorrar(item);
                            });

                            contextMenu.getItems().addAll(itemEditar, itemBorrar);
                            setContextMenu(contextMenu);
                        }
                    }
                };
            }
        });
    }

    private void configurarListaBalances() {
        if (listaBalances == null) return;

        listaBalances.setCellFactory(param -> new ListCell<Persona>() {
            @Override
            protected void updateItem(Persona p, boolean empty) {
                super.updateItem(p, empty);

                if (empty || p == null) {
                    setText(null);
                } else {
                    String texto = String.format("%s (%.2f%%): %.2f €", 
                            p.getNombre(), 
                            p.getPorcentaje(), 
                            p.getSaldo());
                    setText(texto);
                }
            }
        });
    }

    // ====================================
    //         ACCIONES DE LOS BOTONES
    // ====================================

    @FXML
    void accionVolver(ActionEvent event) {
        if (ControladorPrincipal.getInstance().getCuentaActiva() != null) {
            ControladorPrincipal.getInstance().mostrarCuentasCompartidas();
        } else {
            ControladorPrincipal.getInstance().mostrarMenuPrincipal();
        }
    }

    @FXML
    void accionNuevoGasto(ActionEvent event) {
        ControladorPrincipal.getInstance().mostrarCrearGasto();
    }

    @FXML
    void accionConsultar(ActionEvent event) {
        ControladorPrincipal.getInstance().mostrarConsultarGastos();
    }

    @FXML
    void accionNuevaAlerta(ActionEvent event) {
        ControladorPrincipal.getInstance().mostrarGestorAlarmas();
    }
    
    @FXML
    void accionFiltrar(ActionEvent event) {
        ControladorPrincipal.getInstance().abrirVentanaFiltros(filtroRecibido -> {
            if (filtroRecibido == null) {
                listaFiltrada.setPredicate(p -> true);
            } else {
                listaFiltrada.setPredicate(gasto -> filtroRecibido.esValido(gasto));
            }
        });
    }

    @FXML
    void accionAbrirTerminal(ActionEvent event) {
        ControladorPrincipal.getInstance().mostrarTerminal();
    }

    @FXML
    void accionImportar(ActionEvent event) {
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Seleccionar archivo de gastos");
        
        fileChooser.getExtensionFilters().addAll(
            new FileChooser.ExtensionFilter("Archivos soportados", "*.csv", "*.json"),
            new FileChooser.ExtensionFilter("Archivos CSV", "*.csv"),
            new FileChooser.ExtensionFilter("Archivos JSON", "*.json"),
            new FileChooser.ExtensionFilter("Todos los archivos", "*.*")
        );

        Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
        File archivoSeleccionado = fileChooser.showOpenDialog(stage);

        if (archivoSeleccionado != null) {
            String ruta = archivoSeleccionado.getAbsolutePath();
            String nombre = archivoSeleccionado.getName();
            
            String extension = "";
            int i = nombre.lastIndexOf('.');
            if (i > 0) {
                extension = nombre.substring(i + 1).toLowerCase();
            }

            ControladorPrincipal.getInstance().importarGastosExternos(ruta, extension);
            
            Alert alert = new Alert(Alert.AlertType.INFORMATION);
            alert.setTitle("Importación");
            alert.setHeaderText(null);
            alert.setContentText("Proceso de importación finalizado para: " + nombre);
            alert.showAndWait();
        }
    }

    private void confirmarYBorrar(Gasto gasto) {
        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.setTitle("Eliminar Gasto");
        alert.setHeaderText(null);
        alert.setContentText("¿Estás seguro de eliminar: " + gasto.getConcepto() + "?");

        if (alert.showAndWait().orElse(ButtonType.CANCEL) == ButtonType.OK) {
            ControladorPrincipal.getInstance().eliminarGasto(gasto);
        }
    }
}