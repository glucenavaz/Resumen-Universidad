//MapaLugares//
#include "tablaHash.h"
#ifndef _MAPALUGARES
#define _MAPALUGARES
class MapaLugares {
	private: TablaHash tabla;
	         int nCarreteras= 0;
	
	public: MapaLugares();
	        void vaciar();
	        void insertar(Lugar L);
	        Lugar * consultar(string nombre);
	        int nLugares () {
	        return tabla.nTotal();}
	        void insertarCarretera (string origen, string destino, int coste, string informacion);
	        Carretera *consultarCarretera (string origen, string destino);
	        void listarAdyacentes (string origen);
	        int getNCarreteras() { 
	        return nCarreteras;}
	};
	#endif
