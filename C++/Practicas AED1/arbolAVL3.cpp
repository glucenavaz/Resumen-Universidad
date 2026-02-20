#include "arbolAVL3.h"

int Arbol::altura(Nodo* nodo){
        int h = 0;
	if (nodo != NULL) {
		int alturaIzq = altura(nodo->izquierdo);
		int alturaDer = altura(nodo->derecho);
		if(alturaIzq > alturaDer) h = alturaIzq + 1;
		else h = alturaDer +1;
	}
	return h;
}


//Funcion para calcular la diferencia de altura entre la izq y der de cualquier nodo
int Arbol::diff(Nodo* nodo){
	int alturaIzq = altura(nodo->izquierdo);
	int alturaDer = altura(nodo->derecho);
	int bFactor = alturaIzq - alturaDer;

	return bFactor;
}

// Function to perform the Right
// Right Rotation
Nodo* Arbol::rr_rotation(Nodo* parent){
	Nodo* temp;
	temp = parent->derecho;
	parent->derecho = temp->izquierdo;

	temp->izquierdo = parent;

	return temp;
}

// Function to perform the Left
// Left Rotation
Nodo* Arbol::ll_rotation(Nodo* parent){

	Nodo* temp;
	temp = parent->izquierdo;
	parent->izquierdo = temp->derecho;
	temp->derecho = parent;

	return temp;
}

// Function to perform the Left
// Right Rotation
Nodo* Arbol::lr_rotation(Nodo* parent){
	Nodo* temp;
	temp = parent->izquierdo;
	parent->izquierdo = rr_rotation(temp);
	return ll_rotation(parent);
}

// Function to perform the Right
// Left Rotation
Nodo* Arbol::rl_rotation(Nodo* parent){
	Nodo* temp;
	temp = parent->derecho;
	parent->derecho = ll_rotation(temp);
	return rr_rotation(parent);
}

// Function to balance the tree
Nodo* Arbol::balance(Nodo* temp){
	int bal_factor = diff(temp);

	if (bal_factor > 1) {
		if (diff(temp->izquierdo) > 0) {

			temp = ll_rotation(temp);
		}

		else {
			temp = lr_rotation(temp);
		}
	}
	else if (bal_factor < -1) {
		if (diff(temp->derecho) > 0) {
			temp = rl_rotation(temp);
		}

		else {
			temp = rr_rotation(temp);
		}
	}

	return temp;
}


// Function to balance the tree (Nodo raiz)
Nodo* Arbol::balanceTree(Nodo* root){
	int choice;
	if (root == NULL) {
		return NULL;
	}

	root->izquierdo = balanceTree(root->izquierdo);

	root->derecho = balanceTree(root->derecho);

	root = balance(root);
	return root;
}

// Function to insert element in the tree
Nodo* Arbol::insert(Nodo* root, Carretera carretera){

	if (root == NULL) {
		root = new Nodo(carretera);
         return root;
	}

	if (carretera.getDestino() < root->dato.getDestino()) {
		root->izquierdo = insert(root->izquierdo, carretera);
		root = balanceTree(root);
	}

	else if (carretera.getDestino() > root->dato.getDestino()) {
		root->derecho = insert(root->derecho, carretera);
		root = balanceTree(root);
	}
	else {
	 root->dato.setCoste(carretera.getCoste());
	 root-> dato.setInfo(carretera.getInfo());
	 }

	return root;
}

void Arbol::insertar(Carretera c){
	raiz = insert(raiz,c); 

}


// Función auxiliar para contar nodos. Función recursiva de recorrido en
//   preorden, el proceso es aumentar el contador
void Arbol::auxContador(Nodo *nodo){
	contador++;  // Otro nodo
	// Continuar recorrido
	if(nodo->izquierdo) auxContador(nodo->izquierdo);
	if(nodo->derecho)   auxContador(nodo->derecho);
}


// Contar el número de nodos
const int Arbol::numeroNodos(){
	contador = 0;

	auxContador(raiz); // FUnción auxiliar
	return contador;
}


// Function to perform the Inorder
void Arbol::inOrder(Nodo* root, int &n){
     	if(root!= NULL){
     
     	inOrder(root->izquierdo, n);
     	if(n==numeroNodos()) {
     	cout<< root->dato.getDestino();
     	n=1;
     
     	}
     	else {
     	cout<< root->dato.getDestino()<< ", ";
     	n++;
     	}
     
     	inOrder(root->derecho, n);
     	}
}

void Arbol::listar(){
        int n = 1;
	inOrder(raiz, n);
}

// Buscar un valor en el árbol
Carretera* Arbol::consultar(const string destino){  
  	 Nodo* temp;
   	temp = raiz;
   
  	// Todavía puede aparecer, ya que quedan nodos por mirar
   	while(temp!= NULL) {
  
      	if(destino == temp->dato.getDestino()) {
      	return &(temp->dato); // dato encontrado
      	}
     	 else if(destino > temp->dato.getDestino()) temp = temp->derecho; // Seguir
      	else if(destino  < temp->dato.getDestino()) temp = temp->izquierdo;
   }

   
   	return NULL; // No está en árbol
  }



