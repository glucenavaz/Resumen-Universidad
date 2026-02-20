//Modulo interprete//
#include "mapaLugares.h"
#ifndef _interprete
#define _interprete

void funcionSeparadora(string parametros, string param[4]);
void inicializar();
void insertarLugar(string nombre, string info);
void insertarCarretera(string origen, string destino, string coste, string info);
void consultarLugar(string nombre);
void consultarCarretera(string origen, string destino);
void listarAdyacentes(string nombre);
void listarLugares();
void calcularRuta(string origen, string destino);
void interpreteComandos(string comando, string linea);

extern MapaLugares mapa;

#endif
