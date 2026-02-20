package vista;

import java.net.URL;
import java.time.format.DateTimeFormatter;

import java.util.Comparator;
import java.util.Map;
import java.util.ResourceBundle;

import controlador.ControladorPrincipal;
import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.chart.BarChart;

import javafx.scene.chart.CategoryAxis;
import javafx.scene.chart.PieChart;
import javafx.scene.chart.XYChart;
import javafx.scene.control.MenuButton;
import javafx.scene.control.RadioMenuItem;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.ToggleGroup;
import modelo.Gasto;

public class ControladorConsultarGastos implements Initializable {


    // --- MENÚS ---
    @FXML private MenuButton btnMenuTipo;
    @FXML private MenuButton btnMenuCriterio;

    @FXML private RadioMenuItem radioListado;
    @FXML private RadioMenuItem radioBarras;
    @FXML private RadioMenuItem radioTarta;
    @FXML private ToggleGroup grupoTipo;

    @FXML private RadioMenuItem radioCategoria;
    @FXML private RadioMenuItem radioMes;
    @FXML private ToggleGroup grupoCriterio;

    // --- VISTAS (StackPane) ---
    @FXML private TableView<Gasto> tablaGastos;
    @FXML private PieChart graficoTarta;
    @FXML private BarChart<String, Number> graficoBarras;
    @FXML private CategoryAxis ejeX;
    
    // --- COLUMNAS TABLA ---
    @FXML private TableColumn<Gasto, String> colFecha;
    @FXML private TableColumn<Gasto, String> colCategoria;
    @FXML private TableColumn<Gasto, String> colConcepto;
    @FXML private TableColumn<Gasto, Double> colImporte;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        //Configuramos los gráficos
        graficoBarras.setAnimated(false);
        graficoTarta.setAnimated(false);
        
        //Configuramos las columnas de la tabla
        configurarTabla();

        tablaGastos.setVisible(false);
        graficoBarras.setVisible(false);
        graficoTarta.setVisible(false);
        
        grupoTipo.selectToggle(null);
        grupoCriterio.selectToggle(null);
        
        btnMenuTipo.setText("Tipo de Vista");
        btnMenuCriterio.setText("Clasificar por");
    }

    @FXML
    void actualizarVista(ActionEvent event) {
        RadioMenuItem tipo = (RadioMenuItem) grupoTipo.getSelectedToggle();
        RadioMenuItem criterio = (RadioMenuItem) grupoCriterio.getSelectedToggle();

        if (tipo == null || criterio == null) return;

        btnMenuTipo.setText(tipo.getText());
        btnMenuCriterio.setText(criterio.getText());

        //Reseteamos la visibilidad
        tablaGastos.setVisible(false);
        graficoBarras.setVisible(false);
        graficoTarta.setVisible(false);

        // --- LÓGICA DE DECISIÓN ---
        
        if (tipo == radioListado) {
            //LISTADO
            //Mostramos la tabla y la ordenamos según el criterio
            actualizarTablaOrdenada(criterio);
            tablaGastos.setVisible(true);
            
        } else {
            //GRÁFICOS
            //Pedimos datos agrupados al Controlador Principal
            Map<String, Double> datos;
            String etiqueta;

            if (criterio == radioCategoria) {
                datos = ControladorPrincipal.getInstance().obtenerResumenPorCategoria();
                etiqueta = "Categorías";
            } else {
                datos = ControladorPrincipal.getInstance().obtenerResumenPorMes();
                etiqueta = "Meses";
            }

            if (tipo == radioBarras) {
                configurarGraficoBarras(datos, etiqueta);
                graficoBarras.setVisible(true);
                graficoBarras.setTitle("Gastos por " + etiqueta);
            } else {
                configurarGraficoTarta(datos);
                graficoTarta.setVisible(true);
                graficoTarta.setTitle("Gastos por " + etiqueta);
            }
        }
    }
    
    private void actualizarTablaOrdenada(RadioMenuItem criterio) {
        ObservableList<Gasto> listaOriginal = ControladorPrincipal.getInstance().getGastos();
        
        //Usamos una copia para poder ordenarla sin afectar a la original
        ObservableList<Gasto> listaParaMostrar = FXCollections.observableArrayList(listaOriginal);
        
        //Aplicamos ordenación según criterio
        if (criterio == radioCategoria) {
            //alfabéticamente por nombre de categoría
            listaParaMostrar.sort(Comparator.comparing(g -> g.getCategoria().getCategoria()));
        } else {
            //cronológicamente por Fecha
            listaParaMostrar.sort(Comparator.comparing(Gasto::getFecha));
        }
        
        tablaGastos.setItems(listaParaMostrar);
    }

    // --- MÉTODOS DE CONFIGURACIÓN ---

    private void configurarTabla() {
        colFecha.setCellValueFactory(c -> new SimpleStringProperty(c.getValue().getFecha().format(DateTimeFormatter.ofPattern("dd/MM/yyyy"))));
        colCategoria.setCellValueFactory(c -> new SimpleStringProperty(c.getValue().getCategoria().getCategoria())); 
        colConcepto.setCellValueFactory(c -> new SimpleStringProperty(c.getValue().getConcepto()));
        colImporte.setCellValueFactory(c -> new SimpleObjectProperty<>(c.getValue().getCantidad()));
    }

    private void configurarGraficoBarras(Map<String, Double> datos, String etiquetaEjeX) {
        graficoBarras.getData().clear();
        ejeX.setLabel(etiquetaEjeX);
        XYChart.Series<String, Number> serie = new XYChart.Series<>();
        serie.setName("Total");
        datos.forEach((k, v) -> serie.getData().add(new XYChart.Data<>(k, v)));
        graficoBarras.getData().add(serie);
    }

    private void configurarGraficoTarta(Map<String, Double> datos) {
        graficoTarta.getData().clear();
        ObservableList<PieChart.Data> datosTarta = FXCollections.observableArrayList();
        datos.forEach((k, v) -> datosTarta.add(new PieChart.Data(k, v)));
        graficoTarta.setData(datosTarta);
    }

    @FXML
    void accionVolver(ActionEvent event) {
        ControladorPrincipal.getInstance().mostrarCuentaPrincipal();
    }
}