package Sesion3;

public class HiloDel extends Thread{
	@Override
	public void run() {
		for(int i=0; i<10;i++) {
			try {
				HolaMundo.del.acquire();
			} catch (Exception e) {
				// TODO: handle exception
			}
			
			System.out.print("del ");
			HolaMundo.mundo.release();
		}
	}
}
