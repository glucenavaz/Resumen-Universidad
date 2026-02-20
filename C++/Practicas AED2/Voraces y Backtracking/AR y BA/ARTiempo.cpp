#include <iostream>
#include <climits>
#include <math.h>
#include <string>
#include <fstream>
#include <ctime>
#include <sys/time.h>

using namespace std;

void sumaColumna(int mecanicos, int columna, int**C){
	int suma = 0;
	for(int i =0; i< mecanicos; i++){
		suma += C[i][columna];
	}
	C[mecanicos][columna] = suma;
}

void sumaTotal(int mecanicos, int averias, int**C){
	for(int j =0; j<averias; j++){
	sumaColumna(mecanicos, j, C);
	}
}


int asignarMecanicos(int mecanicos, int averias, int** C, int solucion[]){
	int mecanicosRestantes = mecanicos;
	int averiasRestantes = averias;
	
	//Inicializar solucion a 0
	for(int i = 0; i< averias; i++){
		solucion[i] = 0;
	}
	
	int averiasReparadas = 0;
	int min = INT_MAX;
	int posicionAveria = -1;
	int candidato = 0;
	int contador = 0;
	
	
	//mientras no solución
	while(mecanicosRestantes>0 && averiasRestantes>0){
	
	//Elegimos la avería con menos mecánicos disponibles
	for(int i = 0; i< averias; i++){
		if(C[mecanicos][i] !=0 && min > C[mecanicos][i] && solucion[i] == 0) {
		min = C[mecanicos][i];
		posicionAveria = i;
		}
	}
	
	min = INT_MAX;
	
	
	
	//Recorremos la columna (averia) y tomamos el primer mecanico disponible (1er uno)
	while(posicionAveria!= -1 && candidato == 0 && contador< mecanicos ){
		if(C[contador][posicionAveria] == 1) {
		candidato = contador +1;
		solucion[posicionAveria] = candidato;
		averiasReparadas++;
		mecanicosRestantes--;
		averiasRestantes--;
		}
		contador++;
	}
	contador = 0;
	
	
	if(posicionAveria == -1)  averiasRestantes--;
	
	if(posicionAveria != -1){
	 for(int i = 0; i<= mecanicos; i++) {
	 C[i][posicionAveria] = 0;
	 }
	 for(int j = 0; j< averias; j++) {
		if(C[candidato-1][j] == 1) {
		C[mecanicos][j]--;
		C[candidato-1][j] = 0;
		}
		}
	 }
	 
	posicionAveria = -1;
	candidato = 0;
	
	
	}
	return averiasReparadas;
}

int main(){
	int P;
	int mecanicos;
	int averias;

	string nombreArchivo1 = "salidasAR";
	ofstream archivo1;
	archivo1.open(nombreArchivo1.c_str(), fstream:: out);
	
	string nombreArchivo2 = "entradasAR";
   	ifstream archivo2;
	archivo2.open(nombreArchivo2.c_str());
	
	archivo2 >> P;
	
	struct timeval ti1, tf1;
	double tiempo;
	

	for(int k = 0; k < P; k++){
		archivo2>> mecanicos >> averias;

		int solucion[averias];
		int **T = new int*[mecanicos+1];
	
		for(int i =0; i<= mecanicos; i++){
		T[i] = new int [averias];
		}


		for(int i = 0; i< mecanicos; i++){
			for(int j = 0; j< averias; j++){
			archivo2 >> T[i][j];
			}
		}
	
		//if(k == 0) cout<< P << endl;
		sumaTotal(mecanicos, averias, T);
		
		gettimeofday(&ti1, NULL);
		int averiasReparadas = asignarMecanicos(mecanicos, averias, T, solucion);
		gettimeofday(&tf1, NULL);
		tiempo = (tf1.tv_sec - ti1.tv_sec)*1000 + (tf1.tv_usec - ti1.tv_usec)/1000.0;
		archivo1 <<  averias <<" " << tiempo << '\n';
	
	
		/*cout<< averiasReparadas << endl;
		for(int i = 0; i< averias; i++){
			if(i == averias-1) cout<< solucion[i]<< endl ; 
			else cout<< solucion[i]<< ' ' ;
		}*/
 
 	}
 	archivo1.close();
 	archivo2.close();
}

