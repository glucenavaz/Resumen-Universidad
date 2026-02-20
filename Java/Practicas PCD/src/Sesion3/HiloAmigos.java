package Sesion3;

public class HiloAmigos extends Thread{
	@Override
	public void run() {
		for(int i=0; i<10;i++) {
			try {
				HolaMundo.amigos.acquire();
			} catch (Exception e) {
				// TODO: handle exception
			}
			
			System.out.print("amigos ");
			HolaMundo.del.release();
		}
	}
}
