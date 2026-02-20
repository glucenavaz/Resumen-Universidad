package Sesion1;

public class HiloConHerencia extends Thread {
	private String word;

	public HiloConHerencia(String word) {
		super();
		this.word = word;
	}
	
	public void run(){
		for(int i=0; i<10; i++) {
			System.out.println(word);
		}
	}
	
}
