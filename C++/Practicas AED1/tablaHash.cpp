#include "tablaHash.h"

const int TablaHash:: VALOR_INICIAL = 5381;
const int TablaHash:: BASE = 33;


unsigned int TablaHash:: hashDJBA(string palabra) {
    unsigned int resultado = VALOR_INICIAL;
    
    for(int i=0; i < palabra.length(); i++) {
     resultado = resultado * BASE;
     resultado = resultado ^ palabra[i];
     }
     
     
     return resultado% MAX_TAM;
 }

TablaHash:: TablaHash(){
    nElementos = 0;
    
  }
  
void TablaHash:: insertar(Lugar L){
    unsigned int hashLugar = hashDJBA(L.getNombre());
    
   
    list<Lugar>::iterator itListaLugar = tabla[hashLugar].begin();
    
	while (itListaLugar != tabla[hashLugar].end() && itListaLugar->getNombre() < L.getNombre()) {
		itListaLugar++;
		}
		
		
	if (itListaLugar == tabla[hashLugar].end() || itListaLugar->getNombre() != L.getNombre()){
	       tabla[hashLugar].insert(itListaLugar, L);
		nElementos++;
	}
	
	if(itListaLugar != tabla[hashLugar].end() && itListaLugar->getNombre()== L.getNombre()){
		itListaLugar->set(L.getNombre(), L.getInfo());
	}
    
   
}

Lugar * TablaHash:: consultar(string nombre){
    unsigned int hashNombre = hashDJBA(nombre);
    
    list<Lugar>::iterator itListaLugar = tabla[hashNombre].begin();
	
	while (itListaLugar != tabla[hashNombre].end() && itListaLugar->getNombre()< nombre) {
		itListaLugar++;
		}
	if (itListaLugar != tabla[hashNombre].end() && itListaLugar->getNombre() == nombre) {
		
		return &(*itListaLugar);
	}
	
	return NULL;
    }
    

void TablaHash:: vaciar(void){
    nElementos = 0;
   for(int i=0; i<MAX_TAM; i++){
   tabla[i].clear();
   }
  }
