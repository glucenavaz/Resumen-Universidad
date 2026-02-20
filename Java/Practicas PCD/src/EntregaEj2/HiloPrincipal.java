package EntregaEj2;

import java.util.concurrent.Semaphore;

public class HiloPrincipal {
	public static Semaphore semaforoPanel1 = new Semaphore(1);
	public static Semaphore semaforoPanel2 = new Semaphore(1);
	public static Semaphore semaforoPanel3 = new Semaphore(1);
	
	public static Panel Panel1 = new Panel("Panel de Competición 1", 10, 50);
	public static Panel Panel2 = new Panel("Panel de Competición 2", 460, 50);
	public static Panel Panel3 = new Panel("Panel de Competición 3", 910, 50);
	
	public static void main(String[] args) {
		Thread eq1 = new Equipo(1);
		Thread eq2 = new Equipo(2);
		Thread eq3 = new Equipo(3);
		Thread eq4 = new Equipo(4);
		Thread eq5 = new Equipo(5);
		Thread eq6 = new Equipo(6);
		Thread eq7 = new Equipo(7);
		Thread eq8 = new Equipo(8);
		Thread eq9 = new Equipo(9);
		Thread eq10 = new Equipo(10);
		
		
		eq1.start();
		eq2.start();
		eq3.start();
		eq4.start();
		eq5.start();
		eq6.start();
		eq7.start();
		eq8.start();
		eq9.start();
		eq10.start();
		
		try {
			eq1.join();
			eq2.join();
			eq3.join();
			eq4.join();
			eq5.join();
			eq6.join();
			eq7.join();
			eq8.join();
			eq9.join();
			eq10.join();
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		System.out.println("Fin del hilo principal");
	}
}
