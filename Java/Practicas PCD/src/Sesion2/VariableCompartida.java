package Sesion2;

import java.util.concurrent.locks.ReentrantLock;

public class VariableCompartida {
	private int var;      
	private ReentrantLock l = new ReentrantLock();
	
	public VariableCompartida(int val) {         
		 var = val;     
	 }      
	public int getVar() {         
		 return var;     
	}      
	public synchronized void incrementa() {         
		 int temp; 
		 l.lock();
		 try {
			 temp = var;          
			 try {             
				 Thread.sleep((int) Math.round(Math.random()));         
				 } catch (InterruptedException e) {             
					 e.printStackTrace();         
				 }          
			 temp++;         
			 var = temp;  
		} finally {
			l.unlock();
		}
	} 
}
