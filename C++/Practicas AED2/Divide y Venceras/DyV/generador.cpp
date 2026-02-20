#include <iostream>
#include <fstream>
#include <stdlib.h>
#include <math.h>
#include <ctime>
#include <string>

using namespace std;

char letraAleatoria(){
	int aleat = 97 + rand()%25;
	char letra = aleat;
	return letra;
}

string generadorCasoPeor(int numero, int m){
	char cadena[numero+1];
	char letra;
	letra = letraAleatoria();
	cadena[0]= letra;
	
	for(int i = 1; i < numero ; i++){
		if(i <= numero-m+1){
			do{
			letra = letraAleatoria();
			} while(abs(letra - cadena[i-1]) <= 2);
		
			cadena[i] = letra;
		
		} else {
			do { 
			letra = letraAleatoria();
			} while(abs(letra - cadena[i-1]) > 2);
			
			cadena[i] = letra;
		
		}
		
	
	} 

	cadena[numero] = '\0';
	string s(cadena);
	return s;
	
}


string generadorCasoMejor(int numero, int m){
	char cadena[numero+1];
	char letra;
	letra = letraAleatoria();
	cadena[0]= letra;
	
	for(int i = 1; i < numero ; i++){
		if(i < m){
			do{
			letra = letraAleatoria();
			} while(abs(letra - cadena[i-1]) > 2);
		
			cadena[i] = letra;
		
		} else {
			do { 
			letra = letraAleatoria();
			} while(abs(letra - cadena[i-1]) <= 2);
			
			cadena[i] = letra;
		
		}
	}
	cadena[numero] = '\0';
	string s(cadena);
	return s;
	
}

string generadorCasoAleatorio(int numero){
	char cadena[numero+1];
	char letra;
	for(int i = 0; i < numero ; i++){
		letra = letraAleatoria();
		cadena[i] = letra;
	    
	}
	
	cadena[numero] = '\0';
	string s(cadena);
	return s;
}


int main(){
	srand(time(NULL));
	int n;
	cout << "Introduce el tamaño de n:" << endl;
	cin >> n;
	int m = n/1000;
	//cout << "Número de entradas para el caso peor: " ;
	
	int numEntradas;
	cin>> numEntradas;
  
   	string nombreArchivo1 = "entradasPeor";
   	ofstream archivo1;
   
  	archivo1.open(nombreArchivo1.c_str(), fstream:: out);
   	for(int i = 0; i < numEntradas; i++){
   	archivo1 <<  generadorCasoPeor(n, m);
   	archivo1 << endl;
   	}
   	archivo1 << endl;
   	archivo1.close();
   
   	//cout << "Número de entradas para el caso mejor: " ;
  	cin >> numEntradas;
  
   	string nombreArchivo2 = "entradasMejor";
   	ofstream archivo2;
   
   	archivo2.open(nombreArchivo2.c_str(), fstream:: out);
   	for(int i = 0; i < numEntradas; i++){
   	archivo2 <<  generadorCasoMejor(n, m);
   	archivo2 << endl;
   	
   	}
   	archivo2 << endl;
   	archivo2.close();
   
   	//cout << "Número de entradas para el caso aleatorio: " ;
	cin >> numEntradas;
	  
   	string nombreArchivo3 = "entradasAleatorias";
   	ofstream archivo3;
   
    	archivo3.open(nombreArchivo3.c_str(), fstream:: out);
    	for(int i = 0; i < numEntradas; i++){
   	archivo3 <<  generadorCasoAleatorio(n);
   	archivo3 << endl;
   	
    	}
    	archivo3 << endl;
    	archivo3.close();
 }
