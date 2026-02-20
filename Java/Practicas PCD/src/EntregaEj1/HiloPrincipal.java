package EntregaEj1;

public class HiloPrincipal {
	
	public static void main(String[] args) {
		HiloSecundario hiloSuma = new HiloSecundario(false);
		HiloSecundario hiloMult = new HiloSecundario(true);
		
		hiloSuma.start();
		hiloMult.start();
		
		try {
			hiloSuma.join();
			hiloMult.join();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}
