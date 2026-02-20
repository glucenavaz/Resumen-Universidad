package Sesion2;

public class Ejemplo {

	public static void main(String[] args) {
		VariableCompartida compar = new VariableCompartida(0);
		Hilo a = new Hilo("Hilo a", compar);
		Hilo b = new Hilo("Hilo b", compar);
		
		a.start();
		b.start();
		
		try {
			a.join();
			b.join();
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
		System.out.println("Hilo principal " + compar.getVar());
	}
}
