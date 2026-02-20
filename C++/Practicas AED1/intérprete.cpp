#include "intérprete.h"
#include <stdlib.h>

void funcionSeparadora(string linea, string param[4]){
	 int numParametros = 1;
	 string parametro;
	 int num = 0;
	 for(unsigned int i = 0; i<linea.length(); i++){
	  if (linea[i] == ',') {
	   if(i == linea.length()-1){
	   param[num] = parametro;
	   parametro = "";
	   numParametros++;
	   num++;
	   param[num] = parametro;
	   num++;
	   
	   } else{ 
	   param[num] = parametro;
	   parametro = "";
	   numParametros++;
	   num++;
	 }
	} else if (i == linea.length() -1){
	   parametro += linea[i];
	   param[num] = parametro;
	   num++;
	 }  else {
	   parametro += linea[i];
	 }
	}
       }

void inicializar() {
	mapa.vaciar();
        cout << "Mapa inicializado" << endl;
}

void insertarLugar(string nombre, string info) {
    Lugar L;
    L.set(nombre, info);
    mapa.insertar(L);
    cout << "Añadido: " << nombre << ". Total: " << mapa.nLugares() << " lugares" << endl; 
}

void insertarAL(string nombre, string info) {
    Lugar L;
    L.set(nombre, "");
    mapa.insertar(L);
  }

void insertarCarretera(string origen, string destino, string coste, string info) {
if(mapa.consultar(origen) != NULL && mapa.consultar(destino)!=NULL){
       mapa.insertarCarretera(origen, destino, atoi(coste.c_str()), info);
       cout << "Añadido: " << origen << "-" << destino << ". Total: " <<  mapa.getNCarreteras()<< " carreteras" << endl;
 }
}

void insertarAC(string origen, string destino, string coste, string info) {
       mapa.insertarCarretera(origen, destino, atoi(coste.c_str()), info);
}

void consultarLugar(string nombre) {
       
if(mapa.consultar(nombre) != NULL ){
       cout << "Encontrado: " << nombre << endl;
      
       cout << "Información: " << mapa.consultar(nombre)->getInfo() << endl;
       
       } else {
        cout<<"No encontrado: " << nombre << endl;
       
 }
}

void consultarCarretera(string origen, string destino) {
      if(mapa.consultarCarretera(origen, destino)!= NULL){
	cout << "Encontrado: " << origen << "-" << destino << endl;
        cout << "Coste: " << mapa.consultarCarretera(origen, destino)->getCoste() << endl;
        cout<< "Información: " << mapa.consultarCarretera(origen, destino)->getInfo() << endl;
        }
       else  cout << "No encontrado: " << origen << "-" << destino << endl;
       
   }

void listarAdyacentes(string nombre) {
       if(mapa.consultar(nombre) != NULL ){
       cout<< "Encontrado: " << nombre << endl;
       cout<< "Adyacentes: ";
       mapa.listarAdyacentes(nombre);
       cout<<endl;
      
       } else cout<< "No encontrado: " << nombre << endl; 
}

void listarLugares() {
        cout << "Total: 0 lugares" << endl;
}

void calcularRuta(string origen, string destino) {
       cout << "No existe ningún camino entre " << origen << " y " << destino << endl;
}

void interpreteComandos(string comando, string linea) {
        string param[4];
        
        funcionSeparadora(linea, param);
	if (comando == "Inicializar")
		inicializar();
	else if (comando == "AñadirLugar")
		insertarLugar(param[0], param[1]);		
	else if (comando == "AñadirCarretera")
		insertarCarretera(param[0], param[1], param[2], param[3]);
	else if (comando == "AL") 
		insertarAL(param[0], param[1]);
	else if(comando == "AC")
	        insertarAC(param[0], param[1], param[2], param[3]);
	else if (comando == "ConsultarLugar")
		consultarLugar(param[0]);
	else if (comando == "ConsultarCarretera")
		consultarCarretera(param[0], param[1]);
	else if (comando == "ListarAdyacentes")
		listarAdyacentes(param[0]);
	else if (comando == "ListarLugares")
		listarLugares();
	else if (comando == "CalcularRuta")
		calcularRuta(param[0], param[1]);
}

MapaLugares mapa;

