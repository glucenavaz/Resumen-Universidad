package Sesion1;

public class Ejercicio1 {
	public static void main(String[] args) {
		Thread hola = new HiloConHerencia("Hola");
		Thread mundo = new HiloConHerencia("Mundo");
		
		hola.start();
		mundo.start();
		
		System.out.println("Fin del hilo principal");
	}
}
