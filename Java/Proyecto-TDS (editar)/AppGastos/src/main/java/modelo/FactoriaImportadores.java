package modelo;

public class FactoriaImportadores {

	public static Importador getImportador(String formato) {
        if (formato == null) {
            return null;
        }
        
        switch (formato.toLowerCase()) {
            case "csv":
                return new AdaptadorCSV();
            default:
                throw new IllegalArgumentException("Formato no soportado: " + formato);
        }
    }
}