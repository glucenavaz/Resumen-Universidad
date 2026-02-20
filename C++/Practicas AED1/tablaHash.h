// TablaHasH//

#include "arbolAVL3.h"
#include <list>
#define MAX_TAM 1000
#ifndef _TABLAHASH
#define _TABLAHASH

class Lugar{
  private: string nombre;
           string informacion;
           Arbol adyacentes;
           
  public: void set(string nom, string inf){
        nombre = nom;
        informacion = inf;
       }
       
         void insertarCarretera(Carretera c){
         adyacentes.insertar(c);
         }
         
         Carretera* consultarCarretera (string destino){
         return adyacentes.consultar(destino);
         }
         
         
         void listarAdyacentes(){
         adyacentes.listar();
         }
         
         void escribir(){
          cout<< nombre<< endl<< "Información: " << informacion << endl;
          }
          
          string getNombre(){
          return nombre;
          }
          
          string getInfo() {
          return informacion;
          }
              
};

class TablaHash {
        private: static const int VALOR_INICIAL;
                 static const int BASE;
                 list<Lugar> tabla[MAX_TAM];
                 unsigned int hashDJBA(string palabra);
                 int nElementos;
        public:  TablaHash();
                 void insertar (Lugar L);
                 Lugar * consultar(string nombre);
                 void vaciar (void);
                 int nTotal (void){
                 return nElementos;
                 }
   };
   #endif
