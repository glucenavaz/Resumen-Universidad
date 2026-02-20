package Sesion2;

public class Hilo extends Thread{
	private String id;     
	private VariableCompartida compar;      
	public Hilo(String _id, VariableCompartida a) {         
		id = _id;         
		compar = a;     
	}      
	public void run() {         
		for (int i = 0; i < 1000; i++) {             
			compar.incrementa();             
			System.out.println(id + " " + compar.getVar());         
		}     
	} 
}
