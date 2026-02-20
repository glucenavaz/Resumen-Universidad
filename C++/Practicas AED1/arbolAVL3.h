// ArbolAVL3//

#include <iostream>
using namespace std;
#ifndef _ARBOLAVL3
#define _ARBOLAVL3

class Carretera{
	private: 
		string destino;
		int coste;
		string info;
	public: //Contructor por defecto;
		void setCarretera(string dest, int cost, string inf){
        		destino = dest;
        		coste = cost;
        		info = inf;
       	}	
          
          	string getDestino(){
          		return destino;
          	}
          
          	string getInfo() {
          		return info;
          	}
          	int getCoste() {
          		return coste;
          	}
          	
          	void setCoste(int cost){
          	coste = cost;
          	}
          	void setInfo(string inf){
          	info = inf;
          	}
          	
          	
          	
	};

class Arbol;
	
class Nodo{
friend class Arbol;
	private:
		Nodo *izquierdo;
   	        Nodo *derecho;
   	        Carretera dato;
   	        
   	        
   	public: 
		Nodo(Carretera carretera){
		izquierdo= NULL;
		derecho = NULL;
		dato = carretera;
       		}
       
       		~Nodo(){
        	delete izquierdo;
        	delete derecho;
        	}
        	
        };
        
class Arbol{
	private: 
		Nodo *raiz;
		int contador;
		
	public: 
		Arbol(){
		raiz = NULL;
		contador = 0;
		}
		
		~Arbol(){
		delete raiz;
		}
		int altura(Nodo* nodo);
		int diff(Nodo* nodo);
		Nodo* rr_rotation(Nodo* parent);
		Nodo* ll_rotation(Nodo* parent);
		Nodo* lr_rotation(Nodo* parent);
		Nodo* rl_rotation(Nodo* parent);
		Nodo* balance(Nodo* temp);
		Nodo* balanceTree(Nodo* root);
		Nodo* insert(Nodo* root, Carretera carretera);
		void insertar (Carretera c);
		void auxContador(Nodo *nodo);
		const int numeroNodos();
		void inOrder(Nodo* root, int &n);
		void listar();
		Carretera* consultar(const string destino);
		
		
	};
	
	
#endif

