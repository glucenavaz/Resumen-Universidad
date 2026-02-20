package Sesion1;

public class Ejercicio3 {
	static int varCompartida = 0;
	
	public static void main(String[] args) {
		Thread t1 = new HiloVarEst(1);
		Thread t2 = new HiloVarEst(2);
		
		t1.start();
		t2.start();
		
		System.out.println("Fin del hilo principal");
		

	}

}
