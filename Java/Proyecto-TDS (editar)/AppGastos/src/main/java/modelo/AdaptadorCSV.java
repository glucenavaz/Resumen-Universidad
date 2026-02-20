package modelo;

import repositorios.RepositorioCategorias;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

public class AdaptadorCSV implements Importador {

    private LectorCSV lector;
    private static final DateTimeFormatter FORMATO = DateTimeFormatter.ofPattern("M/d/yyyy H:mm");

    public AdaptadorCSV() {
        this.lector = new LectorCSV();
    }

    @Override
    public List<Gasto> importar(String rutaArchivo) {
        List<String[]> datosCrudos = lector.leerLineasCSV(rutaArchivo);
        List<Gasto> gastosAdaptados = new ArrayList<>();
        
        RepositorioCategorias repoCat = RepositorioCategorias.getInstance();

        for (String[] datos : datosCrudos) {
            if (datos.length < 7) continue;
            if (datos[0].trim().equalsIgnoreCase("Date")) continue;

            try {
                String fechaStr = datos[0].trim();
                String nombreCuenta = datos[1].trim();
                String nombreCat = datos[2].trim();
                String concepto = datos[4].trim();
                String nombrePagador = datos[5].trim();
                String cantidadStr = datos[6].trim();

                Categoria categoria = buscarOCrearCategoria(repoCat, nombreCat);
                double cantidad = Double.parseDouble(cantidadStr);
                LocalDate fecha = LocalDateTime.parse(fechaStr, FORMATO).toLocalDate();

                Gasto gasto = new Gasto(concepto, cantidad, fecha, categoria);

                if (!nombreCuenta.equalsIgnoreCase("Personal")) {
                    CuentaCompartida cuentaTemp = new CuentaCompartida(new ArrayList<>(), nombreCuenta);
                    gasto.setCuenta(cuentaTemp);

                    if (!nombrePagador.isEmpty()) {
                        Persona pagadorTemp = new Persona(nombrePagador);
                        gasto.setPagador(pagadorTemp);
                    }
                }

                gastosAdaptados.add(gasto);

            } catch (Exception e) {
                System.err.println(e.getMessage());
            }
        }
        return gastosAdaptados;
    }

    private Categoria buscarOCrearCategoria(RepositorioCategorias repo, String nombre) {
        for (Categoria c : repo.getCategorias()) {
            if (c.getCategoria().equalsIgnoreCase(nombre)) {
                return c;
            }
        }
        Categoria nueva = new Categoria(nombre);
        repo.addCategoria(nueva);
        return nueva;
    }
}