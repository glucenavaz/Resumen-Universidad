package modelo;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;

public class LectorCSV {

    public List<String[]> leerLineasCSV(String rutaArchivo) {
        List<String[]> lineas = new ArrayList<>();
        try (BufferedReader br = new BufferedReader(new FileReader(rutaArchivo, StandardCharsets.UTF_8))) {
            String linea = br.readLine(); 
            while ((linea = br.readLine()) != null) {
                lineas.add(linea.split(","));
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return lineas;
    }
}