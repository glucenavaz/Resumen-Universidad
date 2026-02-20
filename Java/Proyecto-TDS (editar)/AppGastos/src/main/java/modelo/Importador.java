package modelo;

import java.util.List;

public interface Importador {
    List<Gasto> importar(String rutaArchivo);
}