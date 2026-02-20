package Sesion1;

public class Ejercicio2 {
	public static void main(String[] args) {
		HiloConRunnable hola = new HiloConRunnable("Hola");
		HiloConRunnable mundo = new HiloConRunnable("Mundo");
		
		Thread t1 = new Thread(hola);
		Thread t2 = new Thread(mundo);
		
		t1.start();
		t2.start();
		
		System.out.println("Fin del hilo principal");
	}
}
