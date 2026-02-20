package repositorios;

import modelo.*;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class RepositorioCuentasComp {

	private static RepositorioCuentasComp unicaInstancia;
    
    private static final String RUTA_ARCHIVO = "cuentas.json";
    private ObjectMapper mapper;
    private List<CuentaCompartida> listaCuentas;
    
    private RepositorioCuentasComp() {
        mapper = new ObjectMapper();
        mapper.enable(SerializationFeature.INDENT_OUTPUT);
        
        this.listaCuentas = cargarCuentasDelDisco();
    }
    
    // Metodo para llamar al repositorio usando el patron singleton
    public static RepositorioCuentasComp getInstance() {
        if (unicaInstancia == null) {
            unicaInstancia = new RepositorioCuentasComp();
        }
        return unicaInstancia;
    }
    
    public List<CuentaCompartida> getCuentas() {
        return listaCuentas;
    }

    public void addCuenta(CuentaCompartida cuenta) {
        listaCuentas.add(cuenta);
        guardarCuentasEnDisco();
    }
    
    public void removeCuenta(CuentaCompartida cuenta) {
        listaCuentas.remove(cuenta);
        guardarCuentasEnDisco();
    }
    
    public void actualizarCambios() {
        guardarCuentasEnDisco();
    }

    // Escribir en el fichero
    private void guardarCuentasEnDisco() {
        try {
            mapper.writeValue(new File(RUTA_ARCHIVO), listaCuentas);
        } catch (IOException e) {
            e.printStackTrace();
            System.err.println("Error guardando cuentas compartidas: " + e.getMessage());
        }
    }
    
    // Leer datos del fichero
    private List<CuentaCompartida> cargarCuentasDelDisco() {
        File file = new File(RUTA_ARCHIVO);
        if (!file.exists()) {
            return new ArrayList<>();
        }
        try {
            return mapper.readValue(file, new TypeReference<List<CuentaCompartida>>() {});
        } catch (IOException e) {
            e.printStackTrace();
            return new ArrayList<>();
        }
    }
    
    public CuentaCompartida crearCuenta(String nombre) {
        CuentaCompartida c = new CuentaCompartida(new ArrayList<>(), nombre);
        addCuenta(c);
        return c;
    }
	
}
