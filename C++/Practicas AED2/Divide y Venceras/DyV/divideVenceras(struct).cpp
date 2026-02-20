#include <iostream>
#include <fstream>
#include <string>
#include <cstring>
#include <algorithm>
#include <cmath>
#include <stdlib.h>
#include <math.h>
#include <ctime>
#include <sys/time.h>

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


string generadorCasoPeor2(int numero, int m){
	char cadena[numero+1];
	char letra;
	letra = letraAleatoria();
	cadena[0]= letra;
	  
	for(int i = 1; i< numero; i++){
		do { 
		letra = letraAleatoria();
		} while(abs(letra - cadena[i-1]) > 2);
			
		cadena[i] = letra;
	
	
	}
	
	for(int j = m-1; j< numero; j+= m){
		do { 
		letra = letraAleatoria();
		} while(abs(letra - cadena[j-1]) <= 2 && abs(letra - cadena[j+1])<= 2);
			
		cadena[j] = letra;

	}
	
	
	cadena[numero] = '\0';
	string s(cadena);
	return s;
}

string generadorCasoPeor3(int numero, int m){
	char cadena[numero+1];
	char letra;
	letra = letraAleatoria();
	cadena[0]= letra;
	
	for(int i = 1; i < numero ; i++){
		do{
		letra = letraAleatoria();
		} while(abs(letra - cadena[i-1]) <= 2);
		
		cadena[i] = letra;
		
		
	
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

struct longPos {
	int longitud;
	int posicion;
};


   
struct longPos solucionDirecta(int p, int q, char* cadena){
	int maximo = -1;
	int indiceMax = 0;
	int contador = 1;
	struct longPos solucion;
	
	for(int i = p; i< q; i++){
		if(cadena[i+1]!= '\0' && abs(cadena[i+1] - cadena[i]) <= 2) {
		contador++;
		}
		else { 
			if(contador > maximo) {
			maximo = contador;
			indiceMax = i+1-contador;
			}
		contador = 1;
		}
	}

	solucion.longitud = maximo;
	solucion.posicion = indiceMax;
	return solucion;
}

int dividir(int p, int q){
	int valorMedio = (int)((p + q)/2);
	return valorMedio;
}

struct longPos combinar(struct longPos a, struct longPos b, int p, int q, int m, char * cadena ){
	struct longPos solucionFrontera;
	int indiceMedio = 0;
	int longitud = q-p+1;
	indiceMedio = dividir(p, q);
		
	solucionFrontera = solucionDirecta(max(p, indiceMedio-(m-2)), min(q, indiceMedio+(m-1)), cadena);
		
		
	if(a.longitud >= b.longitud && a.longitud >= solucionFrontera.longitud) return a;
	
	else if(solucionFrontera.longitud > a.longitud && solucionFrontera.longitud >= b.longitud) return solucionFrontera;		
	
	else if(b.longitud > a.longitud && b.longitud > solucionFrontera.longitud) return b;
		
	
		
	struct longPos solucion;
	solucion.longitud = 0;
	solucion.posicion = 0;
	return solucion;  
}

struct longPos divideVenceras(int p, int q, int m, char* cadena){
	if((q-p +1 )<= m) return solucionDirecta(p, q, cadena);
	
	else {
		int medio = dividir(p, q);
		
		struct longPos subcadena1 = divideVenceras(p, medio, m, cadena);
		if(subcadena1.longitud == m) return subcadena1;
		
		struct longPos subcadena2 = divideVenceras(medio +1, q,  m , cadena);
		if(subcadena2.longitud == m) return subcadena2;
		
		return combinar(subcadena1, subcadena2, p, q, m, cadena);
	}

  }


 int main(){
	
	int m;
	
	string s1;
	string s2;
	string s3;
	
	struct timeval ti1, tf1;
	struct timeval ti2, tf2;
	struct timeval ti3, tf3;
	
	double tiempo1;
	double tiempo2;
	double tiempo3;
	
	struct longPos solucion1;
	struct longPos solucion2;
	struct longPos solucion3;
	
	string nombreArchivo1 = "tabla1.csv";
   	ofstream archivo1;
	archivo1.open(nombreArchivo1.c_str(), fstream:: out);
	
	string nombreArchivo11 = "solucionesPeores";
   	ofstream archivoPeor;
	archivoPeor.open(nombreArchivo11.c_str(), fstream:: out);
	
	
	for(int n = 10000; n<1000000; n+=2000){
		m = n/1000;
		s1 = generadorCasoPeor(n, m);
		
		char cadenaPeor [n+1];
 		s1.copy(cadenaPeor, n +1);
 		cadenaPeor[n] = '\0';
 		
 		gettimeofday(&ti1, NULL);
		solucion1 = divideVenceras(0, n, m, cadenaPeor);
		gettimeofday(&tf1, NULL);
		tiempo1 = (tf1.tv_sec - ti1.tv_sec)*1000 + (tf1.tv_usec - ti1.tv_usec)/1000.0;
		
		archivo1 << n << "; "<< tiempo1 << '\n';
		archivoPeor << "Posicion: "<< solucion1.posicion << " y longitud: " << solucion1.longitud<< endl;
		
	}
	archivo1.close();
	archivoPeor.close();
	
	
	string nombreArchivo2 = "tabla2.csv";
   	ofstream archivo2;
	archivo2.open(nombreArchivo2.c_str(), fstream:: out);
	
	string nombreArchivo22 = "solucionesMejores";
   	ofstream archivoMejor;
	archivoMejor.open(nombreArchivo22.c_str(), fstream:: out);
	
	for(int n = 10000; n<1000000; n+=2000){
		m = n/1000;
		s2 = generadorCasoMejor(n, m);
		
 		char cadenaMejor [n+1];
 		s2.copy(cadenaMejor, n +1);
 		cadenaMejor[n] = '\0';
 		
 		gettimeofday(&ti2, NULL);
		solucion2 = divideVenceras(0, n, m, cadenaMejor);
		gettimeofday(&tf2, NULL);
		tiempo2 = (tf2.tv_sec - ti2.tv_sec)*1000 + (tf2.tv_usec - ti2.tv_usec)/1000.0;
		
		archivo2 << n << "; "<< tiempo2 << '\n';
		archivoMejor << "Posicion: "<< solucion2.posicion << " y longitud: " << solucion2.longitud<< endl;
		
	}
	archivo2.close();
	archivoMejor.close();
	
	string nombreArchivo3 = "tabla3.csv";
   	ofstream archivo3;
	archivo3.open(nombreArchivo3.c_str(), fstream:: out);
	
	string nombreArchivo33 = "solucionesAleatorias";
   	ofstream archivoAleatorio;
	archivoAleatorio.open(nombreArchivo33.c_str(), fstream:: out);
	
	for(int n = 10000; n<1000000; n+=2000){
		m = n/1000;
		s3 = generadorCasoAleatorio(n);
		
		char cadenaAleatoria [n+1];
 		s3.copy(cadenaAleatoria, n +1);
 		cadenaAleatoria[n] = '\0';
 		
 		gettimeofday(&ti3, NULL);
		solucion3 = divideVenceras(0, n, m, cadenaAleatoria);
		gettimeofday(&tf3, NULL);
		tiempo3 = (tf3.tv_sec - ti3.tv_sec)*1000 + (tf3.tv_usec - ti3.tv_usec)/1000.0;
		
		archivo3 << n << "; "<< tiempo3 << '\n';
		archivoAleatorio << "Posicion: "<< solucion3.posicion << " y longitud: " << solucion3.longitud<< endl;
	}
	archivo3.close();
	archivoAleatorio.close();
	
}


