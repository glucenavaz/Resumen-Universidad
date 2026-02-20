package modelo;

import java.util.Objects;

public class Persona {

	private String nombre;
	private double saldo;
	private double porcentaje;

	public Persona() {
	}

	public Persona(String nombre, double porcentaje) {	
		super();
		this.nombre = nombre;
		this.saldo = 0.0;
		this.porcentaje = porcentaje;
	}

	public Persona(String nombre) {		//CONSTRUCTOR PARA REPARTO EQUITATIVO
		super();						//método determinarReparto de cuentaCompartida
		this.nombre = nombre;
		this.saldo = 0.0;
		this.porcentaje = -1.0;

	}

	public double getSaldo() {
		return saldo;
	}

	public void setSaldo(double saldo) {
		this.saldo = saldo;
	}

	public double getPorcentaje() {
		return porcentaje;
	}

	public void setPorcentaje(double porcentaje) {
		this.porcentaje = porcentaje;
	}

	public String getNombre() {
		return nombre;
	}

	public void setNombre(String nombre) {
		this.nombre = nombre;
	}

	@Override
	public boolean equals(Object o) {
		if (this == o) return true;
		if (o == null || getClass() != o.getClass()) return false;
		Persona persona = (Persona) o;
		return Objects.equals(nombre, persona.nombre);
	}

	@Override
	public String toString() {
		return this.nombre;
	}
}
