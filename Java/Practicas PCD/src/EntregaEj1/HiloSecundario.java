package EntregaEj1;

import java.util.Random;
import java.util.concurrent.locks.ReentrantLock;

public class HiloSecundario extends Thread{
	private static final ReentrantLock l = new ReentrantLock();		//cerrojo compartido
	
	private boolean esMultiplicacion;
	private Random rand = new Random();

	public HiloSecundario(boolean esMultiplicacion) {
		this.esMultiplicacion = esMultiplicacion;
	}
	
	private int[][] generarMatriz(){
		int matriz[][] = new int[3][3];
		for (int i = 0; i < 3; i++) {
            for (int j = 0; j < 3; j++) {
            	matriz[i][j] = rand.nextInt(10) + 1;
            }
		}
		return matriz;
	}
	
	private void imprimirMatriz(int[][] matriz) {
        for (int[] fila : matriz) {
            for (int valor : fila) {
                System.out.printf(valor + " ");
            }
            System.out.println();
        }
    }
	
	private int[][] sumar(int[][] matriz) {
		int[][] resultado = new int[3][3];
		for (int i = 0; i < 3; i++) {
			for (int j = 0; j < 3; j++) {
				resultado[i][j] = 2 * matriz[i][j];
			}
		}
		return resultado;
	}
	
	private int[][] multiplicar(int[][] matriz) {
		int[][] resultado = new int[3][3];
		for (int i = 0; i < 3; i++) {
			for (int j = 0; j < 3; j++) {
				for (int k = 0; k < 3; k++) {
					resultado[i][j] += matriz[i][k] * matriz[k][j];
				}
			}
		}
		return resultado;
	}
	
	@Override
	public void run() {
		for(int i=0;i<10;i++) {
			int[][] A = generarMatriz();
			int[][] Rmult = multiplicar(A);
			int[][] Rsum = sumar(A);
			l.lock();	// Toma del cerrojo para entrar a la sección crítica
			try {		// Comienzo de la sección crítica
				if(esMultiplicacion) {
					System.out.println("A x A");
					imprimirMatriz(A);
					System.out.println("x");
					imprimirMatriz(A);
					System.out.println("A2");
					imprimirMatriz(Rmult);
					System.out.println();
				}
				else {
					System.out.println("A + A");
					imprimirMatriz(A);
					System.out.println("+");
					imprimirMatriz(A);
					System.out.println("2A");
					imprimirMatriz(Rsum);
					System.out.println();
				}
			} finally {
				l.unlock();		// Liberación del cerrojo al salir de la sección crítica
			}
		}
	}
}
