package Sesion3;

public class HiloHola extends Thread{
	@Override
	public void run() {
		for(int i=0; i<10;i++) {
			try {
				HolaMundo.hola.acquire();
			} catch (Exception e) {
				// TODO: handle exception
			}
			
			System.out.print("Hola ");
			HolaMundo.amigos.release();
		}
	}
}
