package controlador;

import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXMLLoader;

import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.stage.Modality;
import javafx.stage.Stage;
import modelo.Alarma;
import modelo.Categoria;
import modelo.CuentaCompartida;
import modelo.EstrategiaAlarma;
import modelo.FactoriaImportadores;
import modelo.Filtro;
import modelo.FiltroAnd;
import modelo.FiltroIntervalo;
import modelo.FiltroListaCategorias;
import modelo.FiltroListaMeses;
import modelo.Gasto;
import modelo.Importador;
import modelo.Persona;
import modelo.Notificacion;
import repositorios.*;
import vista.ControladorFiltros;

import java.util.*;
import java.util.function.Consumer;
import java.util.stream.Collectors;
import java.io.IOException;
import java.time.LocalDate;
import java.time.Month;
import java.time.format.TextStyle;
import java.time.temporal.WeekFields;

public class ControladorPrincipal {

	// --- 1. SINGLETON Y ATRIBUTOS ---
	private static ControladorPrincipal unicaInstancia;
	private Stage stage;
	private ObservableList<Gasto> listaGastos;
	private ObservableList<Alarma> listaAlarmas;

	// REPOSITORIOS
	private RepositorioCategorias repoCategorias;
	private RepositorioGastos repoGastos;
	private RepositorioAlarmas repoAlarmas;
	private RepositorioCuentasComp repoCompartidas;
	private RepositorioNotificaciones repoNotificaciones;

	// === CONTEXTO DE LA APLICACIÓN ===
	// Si es null -> Modo Personal.
	// Si tiene objeto -> Modo Compartido.
	private CuentaCompartida cuentaActiva = null;

	// === MEMORIA DE LOS FILTROS ===
	private Set<String> catGuardadas = new HashSet<>();
	private Set<Month> mesGuardados = new HashSet<>();
	private LocalDate iniGuardado = null;
	private LocalDate finGuardado = null;

	private ControladorPrincipal() {
		this.repoGastos = RepositorioGastos.getInstance(); 
		this.repoAlarmas = RepositorioAlarmas.getInstance();
		this.repoCompartidas = RepositorioCuentasComp.getInstance();
		this.repoCategorias = RepositorioCategorias.getInstance();
		this.repoNotificaciones = RepositorioNotificaciones.getInstance();
		this.listaGastos = FXCollections.observableArrayList();
		this.listaAlarmas = FXCollections.observableArrayList(repoAlarmas.getAlarmas());
	}

	public static ControladorPrincipal getInstance() {
		if (unicaInstancia == null) {
			unicaInstancia = new ControladorPrincipal();
		}
		return unicaInstancia;
	}

	public void setStage(Stage stage) {
		this.stage = stage;
	}

	// ====================================
	//        GESTIÓN DEL CONTEXTO
	// ====================================

	public void entrarModoPersonal() {
		this.cuentaActiva = null;
		refrescarLista();
		mostrarCuentaPrincipal();
	}

	public void entrarModoCompartido(CuentaCompartida cuenta) {
		this.cuentaActiva = cuenta;
		refrescarLista();
		mostrarCuentaPrincipal();
	}

	public CuentaCompartida getCuentaActiva() {
		return cuentaActiva;
	}

	public String getNombreCuentaActiva() {
		return (cuentaActiva != null) ? cuentaActiva.getNombre() : null;
	}

	// ====================================
	//        DATOS HACIA LA VISTA
	// ====================================

	public ObservableList<Gasto> getGastos() {
		return listaGastos;
	}

	public ObservableList<Alarma> obtenerAlarmas() {
		return listaAlarmas;
	}

	public ObservableList<Notificacion> obtenerHistorialNotificaciones() {
		return FXCollections.observableArrayList(repoNotificaciones.getHistorial());
	}

	// ====================================
	//        GESTIÓN DE GASTOS
	// ====================================

	public void añadirGasto(String concepto, double cantidad, Categoria categoria, LocalDate fecha, Persona pagador) {
		Gasto gasto = new Gasto(concepto, cantidad, fecha, categoria);

		if (cuentaActiva != null) {
			gasto.setCuenta(cuentaActiva);
			actualizarBalance(cuentaActiva, pagador, cantidad);
			if (pagador != null) {
				gasto.setPagador(pagador);
			}
		}
		repoGastos.addGasto(gasto);
		refrescarLista();
		if (cuentaActiva == null) comprobarAlarmas(gasto);
	}

	public void eliminarGasto(Gasto gasto) {
		if (gasto != null) {
			repoGastos.removeGasto(gasto);
			refrescarLista();
		}
		if (cuentaActiva != null) {
			actualizarBalance(cuentaActiva, gasto.getPagador(), gasto.getCantidad() * -1);
		}

	}

	public boolean editarGasto(Gasto gasto, String concepto, double cantidad, 
			Categoria categoria, LocalDate fecha, Persona pagador) {
		if (gasto == null) return false;

		double cantidadAntigua = gasto.getCantidad();
		Persona pagadorAntiguo = gasto.getPagador();
		if (cuentaActiva != null) {
			if (pagadorAntiguo != null) {
				actualizarBalance(cuentaActiva, pagadorAntiguo, cantidadAntigua * -1);
			}
		}
		gasto.setConcepto(concepto);
		gasto.setCantidad(cantidad);
		gasto.setCategoria(categoria);
		gasto.setFecha(fecha);

		if (cuentaActiva != null && pagador != null) {
			gasto.setPagador(pagador);

			actualizarBalance(cuentaActiva, pagador, cantidad);
		}

		repoGastos.actualizarCambios();
		refrescarLista();

		return true;
	}

	// ====================================
	//              CATEGORÍAS
	// ====================================

	public boolean registrarNuevaCategoria(String nombre) {
		Categoria nueva = repoCategorias.crearCategoria(nombre);
		return (nueva != null);
	}
	
	// ====================================
	//      MÉTODOS AUXILIARES TERMINAL
	// ====================================
	
	//Busca un miembro de la cuenta activa por su nombre
	public Persona buscarMiembroPorNombre(String nombre) {
		if (cuentaActiva == null) return null;

		return cuentaActiva.getMiembros().stream()
				.filter(p -> p.getNombre().equalsIgnoreCase(nombre))
				.findFirst()
				.orElse(null);
	}

	// ====================================
	//        NAVEGACIÓN DE VENTANAS
	// ====================================

	public void iniciarAplicacion(Stage primaryStage) {
		this.stage = primaryStage;
		mostrarMenuPrincipal();
	}

	public void mostrarMenuPrincipal() {
		navegarVentanaPrincipal("MenuPrincipal.fxml", "Menú Principal");
	}

	public void mostrarCuentaPrincipal() {
		String titulo = (cuentaActiva == null) ? "Mis Gastos" : "Grupo: " + cuentaActiva.getNombre();
		navegarVentanaPrincipal("GestorCuentaPrincipal.fxml", titulo);
	}

	public void mostrarCuentasCompartidas() {
		navegarVentanaPrincipal("GestorCuentasCompartidas.fxml", "Mis Grupos");
	}

	public void mostrarConsultarGastos() {
		navegarVentanaPrincipal("ConsultarGastos.fxml", "Informes y Gráficos");
	}

	public void mostrarGestorAlarmas() {
		navegarVentanaPrincipal("GestorAlarmas.fxml", "Mis Alarmas");
	}

	// --- VENTANAS MODALES ---

	public void mostrarCrearGasto() {
		abrirVentanaModal("CrearGasto.fxml", "Nuevo Gasto");
	}

	public void mostrarCrearAlarma() {
		abrirVentanaModal("CrearAlarma.fxml", "Nueva Alarma");
	}

	public void mostrarCrearCategoria() {
		abrirVentanaModal("CrearCategoria.fxml", "Nueva Categoría");
	}

	public void mostrarCrearCuentaCompartida() {
		abrirVentanaModal("CrearCuentaCompartida.fxml", "Nueva Cuenta Compartida");
	}
	
	public void mostrarHistorialNotificaciones() {
		abrirVentanaModal("HistorialNotificaciones.fxml", "Historial de Notificaciones");
	}

	public void mostrarTerminal() {
		abrirVentanaModal("Terminal.fxml", "Terminal de comandos");
	}

	public void mostrarAñadirFiltro(Consumer<Filtro> callback) {
		abrirVentanaFiltros(callback);
	}

	public void mostrarEditarAlarma(Alarma alarma) {
		try {
			String fxml = "CrearAlarma.fxml"; // Reutilizamos el FXML de creación de alarmas
			String rutaCompleta = "/tds/practicas/AppGastos/" + fxml;
			FXMLLoader loader = new FXMLLoader(getClass().getResource(rutaCompleta));
			Parent root = loader.load();

			vista.ControladorCrearAlarma controlador = loader.getController();
			controlador.setAlarmaAEditar(alarma);

			Stage stageModal = new Stage();
			stageModal.setScene(new Scene(root));
			stageModal.setTitle("Editar Alarma");
			stageModal.initModality(Modality.APPLICATION_MODAL);
			stageModal.initOwner(this.stage);
			stageModal.setResizable(false);
			stageModal.showAndWait();

		} catch (IOException e) {
			System.err.println("Error abriendo ventana edición alarma: " + e.getMessage());
			e.printStackTrace();
		}
	}

	public void mostrarEditarGasto(Gasto gastoParaEditar) {
		try {
			String rutaCompleta = "/tds/practicas/AppGastos/CrearGasto.fxml";
			FXMLLoader loader = new FXMLLoader(getClass().getResource(rutaCompleta));
			Parent root = loader.load();

			vista.ControladorCrearGasto controlador = loader.getController();

			controlador.setGasto(gastoParaEditar);

			Stage stageModal = new Stage();
			stageModal.setScene(new Scene(root));
			stageModal.setTitle("Editar Gasto");
			stageModal.initModality(Modality.APPLICATION_MODAL); 
			stageModal.initOwner(this.stage);
			stageModal.setResizable(false);
			stageModal.showAndWait(); 

		} catch (IOException e) {
			e.printStackTrace();
		}
	}


	// --- MÉTODOS PRIVADOS DE NAVEGACIÓN ---

	private void navegarVentanaPrincipal(String fxml, String titulo) {
		try {
			String rutaCompleta = "/tds/practicas/AppGastos/" + fxml;
			FXMLLoader loader = new FXMLLoader(getClass().getResource(rutaCompleta));
			Parent root = loader.load();

			if (stage.getScene() == null) {
				stage.setScene(new Scene(root));
			} else {
				stage.getScene().setRoot(root);
			}
			stage.setTitle("AppGastos - " + titulo);

			stage.sizeToScene();
			stage.centerOnScreen();
			stage.show();

		} catch (IOException e) {
			System.err.println("Error grave navegando a: " + fxml);
			e.printStackTrace();
		}
	}

	private void abrirVentanaModal(String fxml, String titulo) {
		try {
			String rutaCompleta = "/tds/practicas/AppGastos/" + fxml;
			FXMLLoader loader = new FXMLLoader(getClass().getResource(rutaCompleta));
			Parent root = loader.load();

			Stage stageModal = new Stage();
			stageModal.setScene(new Scene(root));
			stageModal.setTitle(titulo);
			stageModal.initModality(Modality.APPLICATION_MODAL);
			stageModal.initOwner(this.stage);
			stageModal.setResizable(false);
			stageModal.showAndWait();

		} catch (IOException e) {
			System.err.println("Error abriendo ventana modal: " + fxml);
			e.printStackTrace();
		}
	}

	public void abrirVentanaFiltros(Consumer<Filtro> callbackHaciaGestorGastos) {
		try {
			FXMLLoader loader = new FXMLLoader(getClass().getResource("/tds/practicas/AppGastos/AnadirFiltro.fxml"));
			Parent root = loader.load();
			ControladorFiltros ctrFiltros = loader.getController();

			ctrFiltros.cargarEstado(catGuardadas, mesGuardados, iniGuardado, finGuardado);

			ctrFiltros.setListener((nombresCats, meses, inicio, fin) -> {
				this.catGuardadas = (nombresCats != null) ? new HashSet<>(nombresCats) : new HashSet<>();
				this.mesGuardados = (meses != null) ? new HashSet<>(meses) : new HashSet<>();
				this.iniGuardado = inicio;
				this.finGuardado = fin;

				Filtro filtroFabricado = fabricarFiltro(nombresCats, meses, inicio, fin);
				callbackHaciaGestorGastos.accept(filtroFabricado);
			});

			Stage stage = new Stage();
			stage.setScene(new Scene(root));
			stage.setTitle("Filtrar Gastos");
			stage.initModality(Modality.APPLICATION_MODAL);
			stage.initOwner(this.stage);
			stage.setResizable(false);
			stage.show();

		} catch (IOException e) { e.printStackTrace(); }
	}

	// ====================================
	//        LÓGICA AUXILIAR
	// ====================================

	private void refrescarLista() {
		listaGastos.clear();
		if (cuentaActiva == null) {
			listaGastos.addAll(repoGastos.buscarGastosPersonales());
		} else {
			listaGastos.addAll(repoGastos.buscarGastosPorCuenta(cuentaActiva));
		}
	}

	private Filtro fabricarFiltro(Set<String> nombresCats, Set<Month> meses, LocalDate inicio, LocalDate fin) {
		List<Filtro> filtrosActivos = new ArrayList<>();
		if (nombresCats != null && !nombresCats.isEmpty()) {
			List<Categoria> listaObjetosCat = repoCategorias.getCategorias().stream()
					.filter(c -> nombresCats.contains(c.getCategoria()))
					.collect(Collectors.toList());
			filtrosActivos.add(new FiltroListaCategorias(listaObjetosCat));
		}
		if (meses != null && !meses.isEmpty()) {
			filtrosActivos.add(new FiltroListaMeses(new ArrayList<>(meses)));
		}
		if (inicio != null || fin != null) {
			LocalDate fInicio = (inicio != null) ? inicio : LocalDate.MIN;
			LocalDate fFin = (fin != null) ? fin : LocalDate.MAX;
			filtrosActivos.add(new FiltroIntervalo(fInicio, fFin));
		}
		if (filtrosActivos.isEmpty()) return null;
		return new FiltroAnd(filtrosActivos.toArray(new Filtro[0]));
	}

	// --- GRÁFICOS Y ESTADÍSTICAS ---

	public Map<String, Double> obtenerResumenPorCategoria() {
		return getGastos().stream().collect(Collectors.groupingBy(
				g -> g.getCategoria().getCategoria(),
				Collectors.summingDouble(Gasto::getCantidad)
				));
	}

	public Map<String, Double> obtenerResumenPorMes() {
		return getGastos().stream().collect(Collectors.groupingBy(
				g -> {
					String mes = g.getFecha().getMonth().getDisplayName(TextStyle.FULL, new Locale("es", "ES"));
					return mes.substring(0, 1).toUpperCase() + mes.substring(1);
				},
				Collectors.summingDouble(Gasto::getCantidad)
				));
	}

	public ObservableList<Categoria> obtenerCategorias() {
		return FXCollections.observableArrayList(repoCategorias.getCategorias());
	}

	public ObservableList<Persona> obtenerMiembrosGrupo(String nombreGrupo) {
		if (cuentaActiva != null) {
			return FXCollections.observableArrayList(cuentaActiva.getMiembros());
		}
		return FXCollections.observableArrayList();
	}

	// ====================================
	//        ALARMAS
	// ====================================

	public void crearAlarma(String texto, double limite, Categoria categoria, EstrategiaAlarma estrategia) {
		Alarma alarma;
		if (categoria != null) {
			alarma = new Alarma(texto, limite, categoria, estrategia);
			repoAlarmas.addAlarma(alarma);
		} else {
			alarma = new Alarma(texto, limite, estrategia);
			repoAlarmas.addAlarma(alarma);
		}
		listaAlarmas.add(alarma);
		repoAlarmas.actualizarCambios();
	}

	public void eliminarAlarma(Alarma alarma) {
		if (alarma != null) {
			repoAlarmas.removeAlarma(alarma);
			listaAlarmas.remove(alarma);
		}
	}

	// Método para aplicar cambios a una alarma existente
	public void editarAlarmaExistente(Alarma alarma, String nuevoTexto, double nuevoLimite, Categoria nuevaCat, EstrategiaAlarma nuevaEstrategia) {
		alarma.setTexto(nuevoTexto);
		alarma.setLimite(nuevoLimite);
		alarma.setCategoria(nuevaCat);
		alarma.setEstrategia(nuevaEstrategia);

		// Reseteamos la fecha de notificación para que vuelva a avisar si supera el límite nuevo
		alarma.setUltimaNotificacion(null);

		repoAlarmas.actualizarCambios();
	}

	private void comprobarAlarmas(Gasto gastoReciente) {
		boolean cambiosEnAlarmas = false;
		for (Alarma alarma : repoAlarmas.getAlarmas()) {
			boolean superaLimite = alarma.verificarCumplimiento(repoGastos.getGastos());
			if (superaLimite) {
				if (debeNotificar(alarma)) {
					Notificacion notif = new Notificacion(
							"Has superado el límite de " + alarma.getLimite() + "€ en " + alarma.getTexto(),
							alarma);
					repoNotificaciones.addNotificacion(notif);
					alarma.setUltimaNotificacion(LocalDate.now());
					cambiosEnAlarmas = true;

					mostrarPopUpAlarma(notif); 
				}
			}
		}
		if (cambiosEnAlarmas) {
			repoAlarmas.actualizarCambios();
		}
	}

	//Para lanzar el Pop-Up de la notificación
	private void mostrarPopUpAlarma(Notificacion notif) {
		Alert alert = new Alert(javafx.scene.control.Alert.AlertType.WARNING);
		alert.setTitle("¡Límite Superado!");
		alert.setHeaderText("Alerta de Gasto: " + notif.getAlarmaOrigen().getTexto());
		alert.setContentText(notif.getMensaje());

		//show() en vez de showAndWait() para no bloquear la ejecución si salen varias seguidas
		alert.show(); 
	}

	private boolean debeNotificar(Alarma alarma) {
		LocalDate hoy = LocalDate.now();
		LocalDate ultimoAviso = alarma.getUltimaNotificacion();
		if (ultimoAviso == null) return true;

		if (alarma.getEstrategia() instanceof modelo.AlarmaMensual) {
			boolean mismoMes = ultimoAviso.getMonth() == hoy.getMonth() && 
					ultimoAviso.getYear() == hoy.getYear();
			return !mismoMes;
		} 

		if (alarma.getEstrategia() instanceof modelo.AlarmaSemanal) {
			WeekFields wf = WeekFields.ISO;
			int semanaAviso = ultimoAviso.get(wf.weekOfWeekBasedYear());
			int semanaHoy = hoy.get(wf.weekOfWeekBasedYear());
			return semanaAviso != semanaHoy || ultimoAviso.getYear() != hoy.getYear();
		}
		return true;
	}

	// ====================================
	//   GESTIÓN DE CUENTAS COMPARTIDAS
	// ====================================

	public void crearCompartida(String nombreCuenta, Map<String, Double> datosParticipantes, boolean esEquitativo) {

		CuentaCompartida cuenta = new CuentaCompartida(new ArrayList<>(), nombreCuenta);
		Persona persona;

		for (Map.Entry<String, Double> entry : datosParticipantes.entrySet()) {
			String nombrePersona = entry.getKey();
			Double porcentaje = entry.getValue();

			if (esEquitativo) {
				persona = cuenta.añadirSP(nombrePersona);
			} else {
				persona = cuenta.añadirCP(nombrePersona, porcentaje);
			}

			cuenta.añadirMiembro(persona);			
		}

		cuenta.determinarReparto();

		repoCompartidas.addCuenta(cuenta);
		repoCompartidas.actualizarCambios();
	}

	public void eliminarCompartida(CuentaCompartida cuenta) {
	    if (cuenta != null) {
	        List<Gasto> gastosAsociados = repoGastos.buscarGastosPorCuenta(cuenta);
	        
	        for (Gasto g : gastosAsociados) {
	            repoGastos.removeGasto(g);
	        }
	        
	        if (cuentaActiva != null && cuentaActiva.equals(cuenta)) {
	            entrarModoPersonal(); 
	        }

	        repoCompartidas.removeCuenta(cuenta);
	        
	        repoCompartidas.actualizarCambios();
	        repoGastos.actualizarCambios(); 
	    }
	}

	public void añadirMiembro(CuentaCompartida cuenta, Persona persona) {
		if (cuenta != null && persona != null) {
			cuenta.añadirMiembro(persona);
		}
	}

	public void actualizarBalance(CuentaCompartida cuenta, Persona pagador, double cantidad) {
		if (cuenta != null && pagador != null) {
			List<Persona> miembros = cuenta.getMiembros();

			for (Persona miembro: miembros) {
				double porcentaje = miembro.getPorcentaje();
				double parteCorrespondiente = cantidad * (porcentaje / 100.0);
				double saldoActual = miembro.getSaldo();

				if(miembro.equals(pagador)) {
					double nuevoSaldo = saldoActual + (cantidad - parteCorrespondiente);
					miembro.setSaldo(nuevoSaldo);
				}
				else {
					double nuevoSaldo = saldoActual - parteCorrespondiente;
					miembro.setSaldo(nuevoSaldo);
				}

			}
		}
		repoCompartidas.actualizarCambios();
	}

	public ObservableList<CuentaCompartida> getCuentasCompartidas() {
		return FXCollections.observableArrayList(repoCompartidas.getCuentas());
	}
	
	// ====================================
	//        IMPORTADOR
	// ====================================
	
	public void importarGastosExternos(String rutaArchivo, String formato) {
		try {
			Importador importador = FactoriaImportadores.getImportador(formato);
			List<Gasto> gastosImportados = importador.importar(rutaArchivo);

			StringBuilder reporteWarnings = new StringBuilder();
			boolean cambiada = false;

			List<Gasto> gastosPersonales = new ArrayList<>();
			Map<String, List<Gasto>> mapaCuentasImportadas = new HashMap<>();

			for (Gasto g : gastosImportados) {
				if (g.getCuenta() == null) {
					gastosPersonales.add(g);
				} else {
					String nombreCuenta = g.getCuenta().getNombre();
					mapaCuentasImportadas.putIfAbsent(nombreCuenta, new ArrayList<>());
					mapaCuentasImportadas.get(nombreCuenta).add(g);
				}
			}

			for (Gasto g : gastosPersonales) {
				repoGastos.addGasto(g);
			}

			for (String nombreCuenta : mapaCuentasImportadas.keySet()) {
				List<Gasto> gastosDeEstaCuenta = mapaCuentasImportadas.get(nombreCuenta);

				CuentaCompartida cuentaExistente = repoCompartidas.getCuentas().stream()
						.filter(c -> c.getNombre().equalsIgnoreCase(nombreCuenta))
						.findFirst().orElse(null);

				if (cuentaExistente != null) {
					for (Gasto g : gastosDeEstaCuenta) {
						String nombrePagadorCSV = g.getPagador().getNombre();

						Persona miembroReal = cuentaExistente.getMiembros().stream()
								.filter(p -> p.getNombre().equalsIgnoreCase(nombrePagadorCSV))
								.findFirst().orElse(null);

						if (miembroReal != null) {
							g.setCuenta(cuentaExistente);
							g.setPagador(miembroReal);
							repoGastos.addGasto(g);

							actualizarBalance(cuentaExistente, miembroReal, g.getCantidad());
							cambiada = true;
						} else {
							reporteWarnings.append("- Gasto ignorado en '").append(nombreCuenta)
									.append("': El usuario '").append(nombrePagadorCSV)
									.append("' no pertenece al grupo.\n");
						}
					}

				} else {
					Set<String> nombresMiembros = new HashSet<>();
					for (Gasto g : gastosDeEstaCuenta) {
						nombresMiembros.add(g.getPagador().getNombre());
					}

					if (nombresMiembros.size() > 1) {
						CuentaCompartida nuevaCuenta = repoCompartidas.crearCuenta(nombreCuenta);
						for (String nombre : nombresMiembros) {
							nuevaCuenta.añadirSP(nombre);
						}

						nuevaCuenta.determinarReparto();
						for (Gasto g : gastosDeEstaCuenta) {
							Persona pagadorReal = nuevaCuenta.getMiembros().stream()
									.filter(p -> p.getNombre().equalsIgnoreCase(g.getPagador().getNombre()))
									.findFirst().get();

							g.setCuenta(nuevaCuenta);
							g.setPagador(pagadorReal);
							repoGastos.addGasto(g);

							actualizarBalance(nuevaCuenta, pagadorReal, g.getCantidad());
						}
						cambiada = true;

					} else {
						reporteWarnings.append("- Grupo '").append(nombreCuenta)
								.append("' ignorado: Solo se encontró 1 participante en el fichero.\n");
					}
				}
			}

			if (cambiada) {
				repoCompartidas.actualizarCambios();
			}

			if (reporteWarnings.length() > 0) {
				Platform.runLater(() -> {
					Alert alert = new Alert(Alert.AlertType.WARNING);
					alert.setTitle("Resultado Importación");
					alert.setHeaderText("Importación con advertencias");
					alert.setContentText(reporteWarnings.toString());
					alert.showAndWait();
				});
			}

		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}
