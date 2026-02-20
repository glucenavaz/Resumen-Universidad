package Sesion1;

public class HiloConRunnable implements Runnable {
	private String word;

	public HiloConRunnable(String word) {
		super();
		this.word = word;
	}
	
	public void run(){
		for(int i=0; i<10; i++) {
			System.out.println(word);
		}
	}
}
