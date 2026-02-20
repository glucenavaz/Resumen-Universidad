package EntregaEj2;

import java.util.Random;

public class Equipo extends Thread{
	private Random rand = new Random();
	private int id;
	
	public Equipo(int id) {
		this.id = id;
	}
	
	@Override
	public void run() {
		for(int i = 0; i<3; i++) {
			double marcasJugador1[][] = generarMatriz();
			double marcasJugador2[][] = generarMatriz();
			double marcasJugador3[][] = generarMatriz();
			double marcasJugador4[][] = generarMatriz();
			
			double arrayMarcas[][][] = {marcasJugador1, marcasJugador2, marcasJugador3, marcasJugador4};
			double resumen[][] = obtenerResumen(arrayMarcas);
			String mensaje = "Equipo: " + id + " (Iteración " + (i + 1) + ")\n" + convertirMatrizAString(resumen);
			int panelAsignado = id % 3;	//asignamos los paneles a cada hilo usando el resto de dividir su id entre 3
										//Resto 0 --> Panel 1 asignado: equipo3, equipo 6, equipo 9
										//Resto 1 --> Panel 2 asignado: equipo2, equipo 5, equipo 8
										//Resto 2 --> Panel 3 asignado: equipo1, equipo4, equipo 7, equipo 10
			try {
	            switch (panelAsignado) {
	                case 0 -> {
	                    HiloPrincipal.semaforoPanel1.acquire();
	                    HiloPrincipal.Panel1.escribir_mensaje(mensaje);
	                    HiloPrincipal.semaforoPanel1.release();
	                }
	                case 1 -> {
	                    HiloPrincipal.semaforoPanel2.acquire();
	                    HiloPrincipal.Panel2.escribir_mensaje(mensaje);
	                    HiloPrincipal.semaforoPanel2.release();
	                }
	                case 2 -> {
	                    HiloPrincipal.semaforoPanel3.acquire();
	                    HiloPrincipal.Panel3.escribir_mensaje(mensaje);
	                    HiloPrincipal.semaforoPanel3.release();
	                }
	            }
	        } catch (InterruptedException e) {
	            e.printStackTrace();
	        }
			
		}
		
	}

	private double[][] generarMatriz() {
	    double matriz[][] = new double[5][5];
	    
	    for (int i = 0; i < 5; i++) {
	        double limiteInferior = 0;
	        double limiteSuperior = 0;

	        switch (i) {
	            case 0 -> { limiteInferior = 12.5; limiteSuperior = 18.7; } //sprint
	            case 1 -> { limiteInferior = 10.5; limiteSuperior = 12.8; } //salto
	            case 2 -> { limiteInferior = 1.2;  limiteSuperior = 6.9;  } //lanzamiento
	            case 3 -> { limiteInferior = 20.0; limiteSuperior = 25.5; } //resistencia
	            case 4 -> { limiteInferior = 5.5;  limiteSuperior = 9.9;  } //agilidad
	        }

	        for (int j = 0; j < 5; j++) {
	            matriz[i][j] = rand.nextDouble(limiteInferior, limiteSuperior + 1);
	        }
	    }
	    return matriz;
	}
	
	private double[][] obtenerResumen(double[][][] arrayMatrices) {
        double resumen[][] = new double[4][5];
        
        	for(int i = 0; i < arrayMatrices.length; i++) {
                 
                 double mejorMarcaSprint = obtenerMinimo(arrayMatrices[i][0]);
                 double mejorMarcaSalto = obtenerMaximo(arrayMatrices[i][1]);
                 double mejorMarcaLanzamiento = obtenerMaximo(arrayMatrices[i][2]);
                 double mejorMarcaResistencia = obtenerMaximo(arrayMatrices[i][3]);
                 double mejorMarcaAgilidad = obtenerMinimo(arrayMatrices[i][4]);
                 
                 for(int j = 0; j < 5; j++) {
                	switch (j) {
     	            case 0 -> { resumen[i][j] = mejorMarcaSprint; } //sprint
     	            case 1 -> { resumen[i][j] = mejorMarcaSalto; } //salto
     	            case 2 -> { resumen[i][j] = mejorMarcaLanzamiento;  } //lanzamiento
     	            case 3 -> { resumen[i][j] = mejorMarcaResistencia; } //resistencia
     	            case 4 -> { resumen[i][j] = mejorMarcaAgilidad;  } //agilidad
                	}
                 }
        }
        
        return resumen;
    }
	
	private double obtenerMaximo(double[] array) {
	    double max = array[0];
	    
	    for (int i = 1; i < array.length; i++) {
	        if (array[i] > max) {
	            max = array[i];
	        }
	    }
	    return max;
	}
	
	private double obtenerMinimo(double[] array) {
	    double min = array[0];
	    
	    for (int i = 1; i < array.length; i++) {
	        if (array[i] < min) {
	            min = array[i];
	        }
	    }
	    return min;
	}
	
	//Función auxiliar para convertir la matriz a string y así imprimirla por el panel
	private String convertirMatrizAString(double[][] matriz) {
	    StringBuilder sb = new StringBuilder();
	    for (double[] fila : matriz) {
	        for (double valor : fila) {
	            sb.append(String.format("%.2f  ", valor));
	        }
	        sb.append("\n");
	    }
	    return sb.toString();
	}
}
