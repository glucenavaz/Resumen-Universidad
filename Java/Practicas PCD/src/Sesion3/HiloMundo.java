package Sesion3;

public class HiloMundo extends Thread{
	@Override
	public void run() {
		for(int i=0; i<10;i++) {
			try {
				HolaMundo.mundo.acquire();
			} catch (Exception e) {
				// TODO: handle exception
			}
			
			System.out.println("mundo");
			HolaMundo.hola.release();
		}
	}
}
