#include "mapaLugares.h"

MapaLugares::MapaLugares () {
  
  }
  
  void MapaLugares::vaciar() {
  nCarreteras = 0;
  tabla.vaciar();
  }
  
  void MapaLugares::insertar(Lugar L) {
  tabla.insertar(L);
  }
  
  Lugar * MapaLugares:: consultar(string nombre) {
  return tabla.consultar(nombre);
  }
  
  
void MapaLugares::insertarCarretera (string origen, string destino, int coste, string informacion) {
  if(consultar(origen)!= NULL && consultar(destino)!= NULL){
  Carretera carretera;
  carretera.setCarretera(destino, coste, informacion);
  
  if(origen == destino) return;
  if(consultarCarretera(origen, destino)!= NULL) {
  consultar(origen)->insertarCarretera(carretera);
  
  }
  else {
  consultar(origen)->insertarCarretera(carretera);
  
  nCarreteras++;
  }
 }
   
  }

  
  Carretera * MapaLugares::consultarCarretera (string origen, string destino) {
  if(consultar(origen)!=NULL) 
  return consultar(origen)->consultarCarretera(destino);
  else return NULL;
  }
   
   
  void MapaLugares::listarAdyacentes (string origen) {
  	consultar(origen)->listarAdyacentes();
  	
}
