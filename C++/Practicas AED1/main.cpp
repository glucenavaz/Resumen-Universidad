#include "intérprete.h"

int main() {
	
	string comando;
	string parametros;
	char letra;
	
        while(cin >> comando) {
		letra = cin.get();
		if (letra == '\n') {
			parametros = "";
			interpreteComandos(comando, parametros);
		}
		else {
			getline(cin, parametros);
			interpreteComandos(comando, parametros);     
     }
   }
  }
