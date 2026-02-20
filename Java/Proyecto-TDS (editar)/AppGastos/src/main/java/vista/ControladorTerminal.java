package vista;

import controlador.ControladorPrincipal;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import modelo.Categoria;
import modelo.Gasto;
import modelo.Persona;

import java.util.List;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;

public class ControladorTerminal {

    @FXML private TextArea areaSalida;
    @FXML private TextField campoEntrada;

    // --- ESTADOS DE LA TERMINAL ---
    private enum Estado { ESPERANDO_COMANDO, DATOS_ANADIR, DATOS_BORRAR, 
    					DATOS_EDITAR_BUSQUEDA, DATOS_EDITAR_NUEVOS }
    private Estado estadoActual = Estado.ESPERANDO_COMANDO;

    //Atributos para guardar lo que el usuario va escribiendo paso a paso
    private int paso = 0; // 0: Concepto, 1: Cantidad, 2: Categoría
    private String tempConcepto;
    private double tempCantidad;
    private Categoria tempCategoria;
    private LocalDate tempFecha;
    private Gasto gastoEncontradoParaEditar; // Para guardar el gasto que vamos a editar
    private boolean esperandoPagador = false;

    @FXML
    void procesarEntrada(ActionEvent event) {
        String texto = campoEntrada.getText().trim();
        if (texto.isEmpty()) return;

        //Mostrar lo que el usuario escribió en la pantalla
        imprimir("> " + texto);
        campoEntrada.clear();

        //Decidimos qué hacer según el estado actual
        switch (estadoActual) {
            case ESPERANDO_COMANDO:
                interpretarComando(texto);
                break;
            case DATOS_ANADIR:
                procesarFlujoAnadir(texto);
                break;
            case DATOS_BORRAR:
                procesarFlujoBorrar(texto);
                break;
            case DATOS_EDITAR_BUSQUEDA:
                procesarFlujoEditarBusqueda(texto);
                break;
            case DATOS_EDITAR_NUEVOS:
                procesarFlujoEditarNuevosDatos(texto);
                break;
        }
    }

    // --- COMANDOS PRINCIPALES ---

    private void interpretarComando(String cmd) {
        switch (cmd.toLowerCase()) {
            case "help":
                imprimir("--- COMANDOS DISPONIBLES ---");
                imprimir("añadir  -> Crea un nuevo gasto paso a paso.");
                imprimir("borrar  -> Elimina un gasto existente identificándolo.");
                imprimir("editar  -> Busca un gasto y modifica sus valores.");
                imprimir("exit    -> Cierra la terminal.");
                break;
            case "añadir":
                estadoActual = Estado.DATOS_ANADIR;
                paso = 0;
                imprimir("--- AÑADIR GASTO ---");
                imprimir("Introduzca el CONCEPTO:");
                break;
            case "borrar":
                estadoActual = Estado.DATOS_BORRAR;
                paso = 0;
                imprimir("--- BORRAR GASTO ---");
                imprimir("Introduzca el CONCEPTO del gasto a borrar:");
                break;
            case "editar":
                estadoActual = Estado.DATOS_EDITAR_BUSQUEDA;
                paso = 0;
                imprimir("--- EDITAR GASTO ---");
                imprimir("Primero identifique el gasto. Introduzca el CONCEPTO actual:");
                break;
            case "exit":
                ((javafx.stage.Stage) campoEntrada.getScene().getWindow()).close();
                break;
            default:
                imprimir("Comando no reconocido. Escriba 'help' para ayuda.");
        }
    }

    // --- FLUJOS DE DATOS (Interactivo) ---

private void procesarFlujoAnadir(String entrada) {
        
        // --- MODO COMPARTIDO (Paso 4) ---
        //Tenemos los datos y estamos esperando que el usuario escriba el nombre del pagador
        if (esperandoPagador) {
            Persona pagador = ControladorPrincipal.getInstance().buscarMiembroPorNombre(entrada);
            
            if (pagador == null) {
                imprimir("Error: El miembro '" + entrada + "' no existe en el grupo. Inténtalo de nuevo:");
                return; // Salimos para que vuelva a escribir
            }

            //Guardamos el gasto compartido
            ControladorPrincipal.getInstance().añadirGasto(tempConcepto, tempCantidad, tempCategoria, tempFecha, pagador);
            imprimir("Gasto compartido añadido. Pagado por: " + pagador.getNombre());
            
            resetearEstado();
            esperandoPagador = false;
            return;
        }

        // --- FLUJO: Concepto -> Cantidad -> Categoría ---
        recogerDatosGenerico(entrada, () -> {

            // A. COMPROBAMOS SI ES MODO COMPARTIDO
            if (ControladorPrincipal.getInstance().getCuentaActiva() != null) {
                // No guardamos todavía. Preguntamos por el pagador.
                imprimir("¿Quién ha pagado el gasto? (" + ControladorPrincipal.getInstance().getNombreCuentaActiva() + ")");
                esperandoPagador = true; // Activamos la bandera para la próxima vez que el usuario escriba
            } 
            
            // B. MODO PERSONAL
            else {
                // Guardamos directamente pasando null como pagador
                ControladorPrincipal.getInstance().añadirGasto(tempConcepto, tempCantidad, tempCategoria, tempFecha, null);
                imprimir("Gasto personal añadido correctamente.");
                resetearEstado();
            }
        });
    }
    private void procesarFlujoBorrar(String entrada) {
        recogerDatosGenerico(entrada, () -> {
            Gasto g = buscarGasto(tempConcepto, tempCantidad, tempCategoria);
            if (g != null) {
                ControladorPrincipal.getInstance().eliminarGasto(g);
                imprimir("Gasto eliminado correctamente.");
            } else {
                imprimir("Error: No se encontró ningún gasto con esos datos.");
            }
            resetearEstado();
        });
    }

    private void procesarFlujoEditarBusqueda(String entrada) {
        recogerDatosGenerico(entrada, () -> {
            // Acción final Fase 1: Buscar
            gastoEncontradoParaEditar = buscarGasto(tempConcepto, tempCantidad, tempCategoria);
            if (gastoEncontradoParaEditar != null) {
                imprimir("Gasto encontrado. Ahora introduzca los NUEVOS datos.");
                estadoActual = Estado.DATOS_EDITAR_NUEVOS; // Cambiamos de fase
                paso = 0;
                imprimir("Introduzca el NUEVO CONCEPTO:");
            } else {
                imprimir("Error: Gasto no encontrado. Cancelando edición.");
                resetearEstado();
            }
        });
    }

    private void procesarFlujoEditarNuevosDatos(String entrada) {
        recogerDatosGenerico(entrada, () -> {
        	ControladorPrincipal.getInstance().editarGasto(
                    gastoEncontradoParaEditar, 
                    tempConcepto, 
                    tempCantidad, 
                    tempCategoria,
                    tempFecha,
                    null
                );
            imprimir("Gasto editado correctamente.");
            resetearEstado();
        });
    }

    // --- UTILIDADES ---

    //Método auxiliar para no repetir pedir Concepto -> Cantidad -> Categoría
    private void recogerDatosGenerico(String entrada, Runnable accionAlTerminar) {
        try {
            if (paso == 0) {
                // PASO 0: CONCEPTO
                tempConcepto = entrada;
                paso++;
                imprimir("Introduzca la CANTIDAD (ej: 10.5):");
                
            } else if (paso == 1) {
                // PASO 1: CANTIDAD
                tempCantidad = Double.parseDouble(entrada.replace(",", "."));
                paso++;
                imprimir("Introduzca la CATEGORÍA (ej: Comida):");
                
            } else if (paso == 2) {
                // PASO 2: CATEGORÍA
                //Buscamos o creamos categoría
                if (ControladorPrincipal.getInstance().registrarNuevaCategoria(entrada)) {
                    tempCategoria = ControladorPrincipal.getInstance().obtenerCategorias().stream()
                            .filter(c -> c.getCategoria().equalsIgnoreCase(entrada))
                            .findFirst().orElse(new Categoria(entrada));
                } else {
                     tempCategoria = ControladorPrincipal.getInstance().obtenerCategorias().stream()
                            .filter(c -> c.getCategoria().equalsIgnoreCase(entrada))
                            .findFirst().orElse(new Categoria(entrada));
                }
                
                //AVANZAMOS AL PASO 3
                paso++; 
                imprimir("Introduzca la FECHA (dd/MM/aaaa) o escriba 'hoy':");
                
            } else if (paso == 3) {
                // PASO 3: FECHA (NUEVO)
                if (entrada.equalsIgnoreCase("hoy")) {
                    tempFecha = LocalDate.now();
                } else {
                    // Parseamos la fecha texto a objeto LocalDate
                    DateTimeFormatter formatter = DateTimeFormatter.ofPattern("d/M/yyyy");
                    tempFecha = LocalDate.parse(entrada, formatter);
                }
                
                //AHORA SÍ TENEMOS LOS 4 DATOS -> Ejecutamos la acción final
                accionAlTerminar.run();
            }
            
        } catch (NumberFormatException e) {
            imprimir("Error: Cantidad inválida. Intente de nuevo:");
        } catch (DateTimeParseException e) {
            imprimir("Error: Formato de fecha inválido. Use dd/MM/aaaa (ej: 31/12/2025) o 'hoy':");
        } catch (Exception e) {
            imprimir("Error inesperado: " + e.getMessage());
            resetearEstado();
        }
    }

    private Gasto buscarGasto(String concepto, double cantidad, Categoria categoria) {
        List<Gasto> lista = ControladorPrincipal.getInstance().getGastos();
        for (Gasto g : lista) {
            // Comparamos los 3 campos
            if (g.getConcepto().equalsIgnoreCase(concepto) &&
                Math.abs(g.getCantidad() - cantidad) < 0.01 && 
                g.getCategoria().getCategoria().equalsIgnoreCase(categoria.getCategoria()) &&
                g.getFecha().isEqual(tempFecha)) {
                return g;
            }
        }
        return null;
    }

    private void resetearEstado() {
        estadoActual = Estado.ESPERANDO_COMANDO;
        paso = 0;
        gastoEncontradoParaEditar = null;
        imprimir("\nEsperando comando...");
    }

    private void imprimir(String mensaje) {
        areaSalida.appendText(mensaje + "\n");
    }
}