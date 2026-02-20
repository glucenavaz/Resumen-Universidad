a.out: arbolAVL3.o tablaHash.o mapaLugares.o intérprete.o main.o
	g++ arbolAVL3.o tablaHash.o mapaLugares.o intérprete.o main.o
	
arbolAVL3.o: arbolAVL3.cpp
	g++ -c arbolAVL3.cpp
	
tablaHash.o: tablaHash.cpp tablaHash.h arbolAVL3.h
	g++ -c tablaHash.cpp
	
mapaLugares.o: mapaLugares.cpp mapaLugares.h tablaHash.h arbolAVL3.h
	g++ -c mapaLugares.cpp

intérprete.o: intérprete.cpp intérprete.h mapaLugares.h tablaHash.h arbolAVL3.h
	g++ -c intérprete.cpp
	
main.o: main.cpp intérprete.h mapaLugares.h tablaHash.h arbolAVL3.h
	g++ -c main.cpp

clear:
	rm *.o a.out
