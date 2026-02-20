package repositorios;

import modelo.*;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;
import java.time.LocalDate;

public class RepositorioAlarmas {
	
	private static RepositorioAlarmas unicaInstancia;

    private static final String RUTA_ARCHIVO = "alarmas.json";
    private ObjectMapper mapper;
    private List<Alarma> listaAlarmas;
    
    private RepositorioAlarmas() {
        mapper = new ObjectMapper();        
        mapper.registerModule(new JavaTimeModule());
        mapper.enable(SerializationFeature.INDENT_OUTPUT);
        
        this.listaAlarmas = cargarAlarmasDelDisco();
    }
    
    // Metodo para llamar al repositorio usando el patron singleton
    public static RepositorioAlarmas getInstance() {
        if (unicaInstancia == null) {
            unicaInstancia = new RepositorioAlarmas();
        }
        return unicaInstancia;
    }
    
    public List<Alarma> getAlarmas() {
        return listaAlarmas;
    }

    public void addAlarma(Alarma alarma) {
        listaAlarmas.add(alarma);
        guardarAlarmasEnDisco();
    }

    public void removeAlarma(Alarma alarma) {
        listaAlarmas.remove(alarma);
        guardarAlarmasEnDisco();
    }
    
    public List<Alarma> getAlarmasDeHoy() {
        return listaAlarmas.stream()
                .filter(a -> a.isActiva() && a.getFechaActivacion().isEqual(LocalDate.now()))
                .collect(Collectors.toList());
    }
    
    public void actualizarCambios() {
    	guardarAlarmasEnDisco();;
    }
    
    // Escribir en el fichero
    private void guardarAlarmasEnDisco() {
        try {
            mapper.writeValue(new File(RUTA_ARCHIVO), listaAlarmas);
        } catch (IOException e) {
            e.printStackTrace();
            System.err.println("Error guardando alarmas: " + e.getMessage());
        }
    }
    
    // Leer datos del fichero
    private List<Alarma> cargarAlarmasDelDisco() {
        File file = new File(RUTA_ARCHIVO);
        if (!file.exists()) {
            return new ArrayList<>();
        }
        try {
            return mapper.readValue(file, new TypeReference<List<Alarma>>() {});
        } catch (IOException e) {
            e.printStackTrace();
            return new ArrayList<>();
        }
    }
	
}
