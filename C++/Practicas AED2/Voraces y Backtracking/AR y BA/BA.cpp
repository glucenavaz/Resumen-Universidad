#include <iostream>
#include <climits>
#include <math.h>
#include <string>

using namespace std;
#define MAX_PARTICIPANTES 30

//Estructura definida para devolver la solucion de backtracking
struct SolucionOptima{
	int array[MAX_PARTICIPANTES];
	int pesoEquipo1;
	int pesoEquipo2;

};

struct SolucionOptima Backtracking(int S[],  int P[], int n){  //Tiene como parámetros el array solución, el array que contiene los pesos de las entradas y en número de personas
	int nivel = 0;
	int voa = INT_MAX;		//Valor óptimo actual (se minimiza)
	struct SolucionOptima solucion;
	int pEquipo1 = 0;		//peso equipo 1
	int pEquipo2 = 0;		//peso equipo 2
	int nEquipo1 = 0;		//número de personas en el equipo 1
	int nEquipo2 = 0;		//número de personas en el equipo 1
	

	do{
		//Genera
		S[nivel] = S[nivel] +1;
		
		if(S[nivel] == 0){  //Entra en el quipo 1
		 pEquipo1 = pEquipo1 + P[nivel];
		 nEquipo1++;
		} else if(S[nivel] == 1){ //Entra en el equipo 2 teniendo en cuenta que sale del 1
		pEquipo1 = pEquipo1 - P[nivel];
		pEquipo2 = pEquipo2 + P[nivel];
		nEquipo1--;
		nEquipo2++;
		}
		
		//si Solución (que llegue al final, que los dos quipos se diferencien como mucho en 1 persona y esto se haga con el mejor balance de peso posible)
		if(nivel==n-1 && abs(nEquipo1 - nEquipo2)<= 1 && abs(pEquipo1 - pEquipo2)< voa){
			voa = abs(pEquipo1 - pEquipo2);	 //Se actualiza
			solucion.pesoEquipo1 = pEquipo1;
			solucion.pesoEquipo2 = pEquipo2;
			for(int i = 0; i<n; i++){	//Se copia solución
			 solucion.array[i] = S[i];
			}
			
		}
		
		//Si Criterio(que no haya llegado al final) / Se podría añadir alguna condición para podar
		if(nivel<n-1){
			nivel = nivel + 1;
			
		} else {  //Mientras no MasHermanos y no se haya retrocedido al principio del todo
			while(!(S[nivel]<1) && nivel > -1){
				if(S[nivel] == 0){   //Retroceder -> Deshacemos lo hecho(Sacamos jugador: restando su peso y decrementando en 1 el nº de personas)
				pEquipo1 = pEquipo1 - P[nivel];
		 		nEquipo1--;
				} else if(S[nivel] == 1){
				pEquipo2 = pEquipo2 - P[nivel];
				nEquipo2--;
				}
				
				S[nivel] = -1;     //Recupera el valor -1
				nivel = nivel -1;  //Se decrementa el nivel
			}
		
		}
	
	} while (nivel != -1);
	
	return solucion;
}

int main(){
	int numCasos = 0;
	string espacio;
	int n = 0;
	
	cin >> numCasos;
	
	for(int i = 0; i<numCasos; i++){
	getline(cin, espacio);
	cin >> n;
	
	
	int S[n];
	int P[n];
	
	for(int i = 0; i<n ; i++){
		S[i] = -1;
		cin >> P[i];
	}

	struct SolucionOptima solucion = Backtracking(S, P, n);
	
	if(solucion.pesoEquipo1 <= solucion.pesoEquipo2){
		cout<< solucion.pesoEquipo1 << " " << solucion.pesoEquipo2 << endl;
	} else cout<< solucion.pesoEquipo2 << " " << solucion.pesoEquipo1 << endl;
   }
}
	
