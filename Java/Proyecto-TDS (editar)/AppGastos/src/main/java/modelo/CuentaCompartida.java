package modelo;

import java.util.List;
import java.util.Objects;

public class CuentaCompartida {

	private String nombre;
	private List<Persona> miembros;
	
	public CuentaCompartida() {
	}

	public CuentaCompartida(List<Persona> miembros, String nombre) {
		super();
		this.nombre = nombre;
		this.miembros = miembros;
	}

	public List<Persona> getMiembros() {
		return miembros;
	}

	public String getNombre() {
		return nombre;
	}

	public void setNombre(String nombre) {
		this.nombre = nombre;
	}

	public void determinarReparto() {
		for(Persona p: miembros) {
			if(p.getPorcentaje() < 0) p.setPorcentaje(100.0/miembros.size()); //EQUITATIVO
		}
	}

	public void añadirMiembro(Persona miembro) {
		miembros.add(miembro);
	}

	@Override
	public boolean equals(Object o) {
	    if (this == o) return true;
	    if (o == null || getClass() != o.getClass()) return false;
	    CuentaCompartida that = (CuentaCompartida) o;
	    return Objects.equals(nombre, that.nombre); 
	}

	@Override
	public int hashCode() {
	    return Objects.hash(nombre);
	}
	
	public Persona añadirSP(String nombre) {
		return new Persona(nombre);
	}
	
	public Persona añadirCP(String nombre, double porcentaje) {
		return new Persona(nombre, porcentaje);
	}
}