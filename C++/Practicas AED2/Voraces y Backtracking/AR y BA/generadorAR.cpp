#include <iostream>
#include <fstream>
#include <ctime>

using namespace std;

int** generarTabla(int mecanicos, int averias){
	int **T = new int*[mecanicos];
	
	for(int i =0; i< mecanicos; i++){
		T[i] = new int [averias];
	}


	for(int i = 0; i< mecanicos; i++){
		for(int j = 0; j< averias; j++){
		 T[i][j] = 0;
		}
	}

	return T;

}

int generarNumero(){
	int numAlea = 3+ rand()%1998; //Entre 3 y 20
	return numAlea;

}

int main(){
	srand(time(NULL));
	string nombreArchivo = "entradasAR";
	ofstream archivo;
  	archivo.open(nombreArchivo.c_str(), fstream:: out);
  	int numCasos = 30;
  	
  	archivo<< numCasos<< endl;
  	int mecanicos;
  	int averias; 
  	
  	for(int j = 0; j< numCasos; j++){
  		mecanicos = generarNumero();
  		averias = mecanicos;
  		archivo << mecanicos<< " " << averias <<endl;
  		
  		int ** Tabla = generarTabla(mecanicos, averias);
  		for(int i = 0; i< mecanicos; i++){
			for(int j = 0; j< averias; j++){
			archivo<< Tabla[i][j]<<" ";
			}
			archivo<<endl;
		}
		archivo<<endl;
	}
	archivo.close();
}
