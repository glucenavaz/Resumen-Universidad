package Sesion1;

public class HiloVarEst extends Thread{
	
	private int id;
	
	public HiloVarEst(int id) {
		this.id = id;
	}

	public void run() {
		for(int i= 0; i<1000; i++) {
			int varAux = Ejercicio3.varCompartida;
			try {
				Thread.sleep((int) (Math.random()*10));
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
			varAux++;
			Ejercicio3.varCompartida = varAux;
			System.out.println("Hilo " + id + " " + varAux);
		}
	}
	
}
