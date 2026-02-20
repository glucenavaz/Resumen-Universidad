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

public class RepositorioNotificaciones {
	private static RepositorioNotificaciones unicaInstancia;
    private static final String RUTA_ARCHIVO = "historial_notificaciones.json";
    
    private List<Notificacion> historial;
    private ObjectMapper mapper;

    private RepositorioNotificaciones() {
        mapper = new ObjectMapper();
        mapper.registerModule(new JavaTimeModule()); // Para manejar fechas Java 8
        mapper.enable(SerializationFeature.INDENT_OUTPUT);
        this.historial = cargarDelDisco();
    }

    public static RepositorioNotificaciones getInstance() {
        if (unicaInstancia == null) unicaInstancia = new RepositorioNotificaciones();
        return unicaInstancia;
    }

    public void addNotificacion(Notificacion n) {
        historial.add(n);
        guardarEnDisco();
    }

    public List<Notificacion> getHistorial() {
        return historial;
    }

    private void guardarEnDisco() {
        try {
            mapper.writeValue(new File(RUTA_ARCHIVO), historial);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    
    public void actualizarCambios() {
    	guardarEnDisco();
    }

    private List<Notificacion> cargarDelDisco() {
        File file = new File(RUTA_ARCHIVO);
        if (!file.exists()) return new ArrayList<>();
        try {
            return mapper.readValue(file, new TypeReference<List<Notificacion>>() {});
        } catch (IOException e) {
            e.printStackTrace();
            return new ArrayList<>();
        }
    }
}
