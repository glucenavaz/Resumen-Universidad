package modelo;

import java.time.*;
import java.util.*;

public class Alarma {
	
	private String texto;
	private LocalDate fechaActivacion;
	private double limite;
	private Categoria categoria;
	private boolean activa;
	private EstrategiaAlarma estrategia;	//Semanal o mensual
	private LocalDate ultimaNotificacion;	//Fecha de la ultima vez que saltó
	
	public Alarma() {
    }
	
	public Alarma(String texto, double limite, Categoria categoria, EstrategiaAlarma estrategia) {
		super();
		this.texto = texto;
		this.limite = limite;
		this.fechaActivacion = LocalDate.now();
		this.categoria = categoria;
		this.activa = true;		//Al crear la alarma nace activa
		this.estrategia = estrategia;
		this.ultimaNotificacion = null;
	}
	
	public Alarma(String texto, double limite, EstrategiaAlarma estrategia) {
		//Constructor alarma sin categoria, llama al primero pasandole null como categoria
		this(texto, limite, null, estrategia);
	}

	public String getTexto() {
		return texto;
	}

	public void setTexto(String texto) {
		this.texto = texto;
	}

	public double getLimite() {
		return limite;
	}

	public void setLimite(double limite) {
		this.limite = limite;
	}

	public Categoria getCategoria() {
		return categoria;
	}

	public void setCategoria(Categoria categoria) {
		this.categoria = categoria;
	}

	public LocalDate getFechaActivacion() {
		return fechaActivacion;
	}

	public void setFechaActivacion(LocalDate fechaActivacion) {
		this.fechaActivacion = fechaActivacion;
	}

	public boolean isActiva() {
		return activa;
	}

	public void setActiva(boolean activa) {
		this.activa = activa;
	}
	
	public EstrategiaAlarma getEstrategia() {
		return estrategia;
	}

	public void setEstrategia(EstrategiaAlarma estrategia) {
		this.estrategia = estrategia;
	}

	public LocalDate getUltimaNotificacion() {
		return ultimaNotificacion;
	}

	public void setUltimaNotificacion(LocalDate ultimaNotificacion) {
		this.ultimaNotificacion = ultimaNotificacion;
	}

	public boolean verificarCumplimiento(List<Gasto> historial) {
        if (estrategia == null) return false;
        
        //Delegamos el cálculo a la estrategia
        return estrategia.verificar(historial, this.limite, this.categoria);
    }
}
