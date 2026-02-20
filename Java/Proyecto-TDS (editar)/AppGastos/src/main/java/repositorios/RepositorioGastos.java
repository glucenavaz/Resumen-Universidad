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

public class RepositorioGastos {

	private static RepositorioGastos unicaInstancia;

	private static final String RUTA_ARCHIVO = "gastos.json";
	private ObjectMapper mapper;
	private List<Gasto> listaGastos;

	private RepositorioGastos() {
		mapper = new ObjectMapper();
		mapper.registerModule(new JavaTimeModule());
		mapper.enable(SerializationFeature.INDENT_OUTPUT);

		this.listaGastos = cargarGastosDelDisco();
	}

	public static RepositorioGastos getInstance() {
		if (unicaInstancia == null) {
			unicaInstancia = new RepositorioGastos();
		}
		return unicaInstancia;
	}

	// --- MÉTODOS DE GESTIÓN (CRUD) ---

	public List<Gasto> getGastos() {
		return new ArrayList<>(listaGastos);
	}

	public void addGasto(Gasto gasto) {
		listaGastos.add(gasto);
		guardarGastosEnDisco();
	}

	public void removeGasto(Gasto gasto) {
		listaGastos.remove(gasto);
		guardarGastosEnDisco();
	}

	public void actualizarCambios() {
		guardarGastosEnDisco();
	}

	// --- PERSISTENCIA (JACKSON) ---

	private void guardarGastosEnDisco() {
		try {
			mapper.writeValue(new File(RUTA_ARCHIVO), listaGastos);
		} catch (IOException e) {
			e.printStackTrace();
			System.err.println("Error guardando gastos en JSON.");
		}
	}

	private List<Gasto> cargarGastosDelDisco() {
		File file = new File(RUTA_ARCHIVO);
		if (!file.exists()) {
			return new ArrayList<>();
		}
		try {
			return mapper.readValue(file, new TypeReference<List<Gasto>>() {});
		} catch (IOException e) {
			e.printStackTrace();
			return new ArrayList<>();
		}
	}

	// --- MÉTODOS DE BÚSQUEDA Y FILTRADO ---

	public List<Gasto> buscarGastos(Filtro filtro) {
		return this.listaGastos.stream()       
				.filter(g -> filtro.esValido(g)) 
				.collect(Collectors.toList()); 
	}

	public List<Gasto> buscarGastosPersonales() {
		return listaGastos.stream()
				.filter(g -> g.getCuenta() == null)
				.collect(Collectors.toList());
	}

	public List<Gasto> buscarGastosPorCuenta(CuentaCompartida cuentaBuscada) {
		if (cuentaBuscada == null) return new ArrayList<>();
		
		return this.listaGastos.stream()
				.filter(g -> g.getCuenta() != null)
				.filter(g -> g.getCuenta().equals(cuentaBuscada))
				.collect(Collectors.toList());
	}
}
