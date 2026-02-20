#include <iostream>
#include <climits>

using namespace std;

//Suma el contenido de toda la columna menos el de la última fila y lo añade en esta
void sumaColumna(int mecanicos, int columna, int**C){
	int suma = 0;
	for(int i =0; i< mecanicos; i++){
		suma += C[i][columna];
	}
	C[mecanicos][columna] = suma;
}

//Suma las columnas de toda la matriz
void sumaTotal(int mecanicos, int averias, int**C){
	for(int j =0; j<averias; j++){
	sumaColumna(mecanicos, j, C);
	}
}


//Algoritmo voraz
int asignarMecanicos(int mecanicos, int averias, int** C, int solucion[]){
	int mecanicosRestantes = mecanicos;
	int averiasRestantes = averias;
	
	//Inicializar solucion a 0
	for(int i = 0; i< averias; i++){
		solucion[i] = 0;
	}
	
	int averiasReparadas = 0; //Lo que vamos a devolver
	int min = INT_MAX;	//Ayudará a  calcular la columna que sume menos y por tanto tenga menos mecánicos disponibles para ella
	int posicionAveria = -1;  //variable para guardar la avería elegida en cada iteración
	int candidato = 0;	// variable para guardar el mecánico elegido para una avería dada en cada iteración
	int contador = 0;	//contador para recorrer las filas fijada la columna
	
	
	//Cuado no queden más mecánicos o más averías se acaba el while
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
		solucion[posicionAveria] = candidato;  //Escribimos en la solución 
		averiasReparadas++;			//Incrementamos el número de averías reparadas
		mecanicosRestantes--;
		averiasRestantes--;		//Decrementamos el número de mecanicos y averias restantes para tenerlos actualizados ya que son condicion del while
		}
		contador++;
	}
	contador = 0;
	
	
	if(posicionAveria == -1)  averiasRestantes--;  
	
	//Si se han realizado los dos bucles anteriores significa que se ha añadido una solución y hay que poner a 0 la columna de la avería ya asignada y la fila del mecánico
	if(posicionAveria != -1){
	 for(int i = 0; i<= mecanicos; i++) {
	 C[i][posicionAveria] = 0;
	 }
	 for(int j = 0; j< averias; j++) {
		if(C[candidato-1][j] == 1) {
		C[mecanicos][j]--;		//Se decrementa 1 a la suma de los mecanicos disponibles para cada avería en el caso de que el mecánico asignado también 
		C[candidato-1][j] = 0;          //estuviera como posibilidad en la suya
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

cin >> P;

//Lectura de entradas y realización de algoritmo voraz en cada caso
for(int k = 0; k < P; k++){
	cin >> mecanicos >> averias;

	int solucion[averias];
	int **T = new int*[mecanicos+1];
	
	for(int i =0; i<= mecanicos; i++){
		T[i] = new int [averias];
	}


	for(int i = 0; i< mecanicos; i++){
		for(int j = 0; j< averias; j++){
		cin >> T[i][j];
		}
	}
	
	if(k == 0) cout<< P << endl;
	sumaTotal(mecanicos, averias, T);
	int averiasReparadas = asignarMecanicos(mecanicos, averias, T, solucion);
	
	cout<< averiasReparadas << endl;
	for(int i = 0; i< averias; i++){
		if(i == averias-1) cout<< solucion[i]<< endl ; 
		else cout<< solucion[i]<< ' ' ;
	}
 
 }
}

