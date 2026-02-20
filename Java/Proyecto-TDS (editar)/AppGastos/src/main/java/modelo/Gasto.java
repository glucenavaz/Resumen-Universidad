package modelo;

import java.time.*;

public class Gasto {

	private String concepto;
	private double cantidad;
	private Categoria categoria;
	private LocalDate fecha;

	private CuentaCompartida cuenta;
	private Persona pagador;

	public Gasto() {
	}

	public Gasto(String concepto, double cantidad, LocalDate fecha, Categoria categoria) {
		this.concepto = concepto;
		this.cantidad = cantidad;
		this.categoria = categoria;
		this.fecha = fecha;
		this.cuenta = null;
		this.pagador = null;
	}

	public String getConcepto() {
		return concepto;
	}

	public double getCantidad() {
		return cantidad;
	}

	public Categoria getCategoria() {
		return categoria;
	}

	public LocalDate getFecha() {
		return fecha;
	}

	public CuentaCompartida getCuenta() {
		return cuenta;
	}

	public Persona getPagador() {
		return pagador;
	}

	public void setConcepto(String concepto) {
		this.concepto = concepto;
	}

	public void setCantidad(double cantidad) {
		this.cantidad = cantidad;
	}

	public void setCategoria(Categoria categoria) {
		this.categoria = categoria;
	}

	public void setFecha(LocalDate fecha) {
		this.fecha = fecha;
	}

	public void setCuenta(CuentaCompartida cuentaCompartida) {
		this.cuenta = cuentaCompartida;
	}
	
	public void setPagador(Persona pagador) {
		this.pagador = pagador;
	}

	public boolean esCompartido() {
		return cuenta != null;
	}
}
