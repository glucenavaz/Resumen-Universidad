package modelo;

import java.time.LocalDate;

public class Notificacion {
	private String mensaje;
	private LocalDate fecha;
	private Alarma alarmaOrigen;
	
	public Notificacion() {
	}

	public Notificacion(String mensaje, Alarma alarmaOrigen) {
		super();
		this.mensaje = mensaje;
		this.fecha = LocalDate.now();
		this.alarmaOrigen = alarmaOrigen;
	}

	public String getMensaje() {
		return mensaje;
	}

	public void setMensaje(String mensaje) {
		this.mensaje = mensaje;
	}

	public LocalDate getFecha() {
		return fecha;
	}

	public void setFecha(LocalDate fecha) {
		this.fecha = fecha;
	}

	public Alarma getAlarmaOrigen() {
		return alarmaOrigen;
	}

	public void setAlarmaOrigen(Alarma alarmaOrigen) {
		this.alarmaOrigen = alarmaOrigen;
	}
	
	
	
	
	
	
}
