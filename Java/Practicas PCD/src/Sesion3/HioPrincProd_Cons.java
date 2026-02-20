package Sesion3;

import

public class HioPrincProd_Cons {
	private int[][] generarMatriz(){
		int matriz[][] = new int[3][3];
		for (int i = 0; i < 3; i++) {
            for (int j = 0; j < 3; j++) {
            	matriz[i][j] = rand.nextInt(10) + 1;
            }
		}
		return matriz;
	}
}
