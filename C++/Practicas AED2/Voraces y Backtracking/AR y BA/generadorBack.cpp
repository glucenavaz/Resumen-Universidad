#include <iostream>
#include <fstream>
#include <ctime>

using namespace std;

int generarPeso(){
	int peso = 1 + rand()%450;
	return peso;

}

int main(){
	srand(time(NULL));
	string nombreArchivo = "entradasBack";
	ofstream archivo;
  	archivo.open(nombreArchivo.c_str(), fstream:: out);
  	int numCasos = 10;
  	int numPersonas = 30;
  	
  	archivo<< numCasos<< endl <<endl;
  	
  	for(int j = 0; j< numCasos; j++){
  		
  		archivo <<numPersonas<<endl;
  		
		for(int i = 0; i< numPersonas; i++){
		archivo<< generarPeso()<<endl;
		}
		archivo<<endl;
	}
	archivo.close();
}
