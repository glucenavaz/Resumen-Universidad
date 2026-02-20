package repositorios;

import modelo.*;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class RepositorioCategorias {

	private static RepositorioCategorias unicaInstancia;
    
    private static final String RUTA_ARCHIVO = "categorias.json";
    private ObjectMapper mapper;
    private List<Categoria> listaCategorias;
    
    private RepositorioCategorias() {
        mapper = new ObjectMapper();
        mapper.enable(SerializationFeature.INDENT_OUTPUT);
        
        // Cargar desde disco al iniciar
        this.listaCategorias = cargarCategoriasDelDisco();

        if (this.listaCategorias.isEmpty()) {
            inicializarCategoriasPorDefecto();
        }
    }
    
    // Metodo para llamar al repositorio usando el patron singleton
    public static RepositorioCategorias getInstance() {
        if (unicaInstancia == null) {
            unicaInstancia = new RepositorioCategorias();
        }
        return unicaInstancia;
    }
    
    public List<Categoria> getCategorias() {
        return listaCategorias;
    }
    
    public void addCategoria(Categoria nuevaCategoria) {
        listaCategorias.add(nuevaCategoria);
        guardarCategoriasEnDisco();
    }
    
    public void removeCategoria(Categoria categoria) {
        listaCategorias.remove(categoria);
        guardarCategoriasEnDisco();
    }
    
    private void inicializarCategoriasPorDefecto() {
        listaCategorias.add(new Categoria("Comida"));
        listaCategorias.add(new Categoria("Transporte"));
        listaCategorias.add(new Categoria("Ocio"));
        listaCategorias.add(new Categoria("Hogar"));
        guardarCategoriasEnDisco();
    }
    
    // Escribir en el fichero
    private void guardarCategoriasEnDisco() {
        try {
            mapper.writeValue(new File(RUTA_ARCHIVO), listaCategorias);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    
    // Leer datos del fichero
    private List<Categoria> cargarCategoriasDelDisco() {
        File file = new File(RUTA_ARCHIVO);
        if (!file.exists()) {
            return new ArrayList<>();
        }
        try {
            return mapper.readValue(file, new TypeReference<List<Categoria>>() {});
        } catch (IOException e) {
            e.printStackTrace();
            return new ArrayList<>();
        }
    }
    
    public Categoria crearCategoria(String nombreCategoria) {
        for (Categoria c : listaCategorias) {
            if (c.getCategoria().equalsIgnoreCase(nombreCategoria)) {
            		return null;
            }
        }

        Categoria nueva = new Categoria(nombreCategoria);
        addCategoria(nueva); 
        return nueva;
    }
}