package modelo;

import java.time.LocalDate;
import java.util.List;

public class AlarmaSemanal implements EstrategiaAlarma{
	@Override
    public boolean verificar(List<Gasto> historial, double limite, Categoria categoria) {
        LocalDate haceUnaSemana = LocalDate.now().minusDays(7);

        double totalGastado = historial.stream()
            //Filtrar gastos posteriores a hace 7 días
            .filter(g -> g.getFecha().isAfter(haceUnaSemana))
            
            //Filtrar por categoría
            .filter(g -> categoria == null || 
                         (g.getCategoria() != null && 
                          g.getCategoria().getCategoria().equalsIgnoreCase(categoria.getCategoria())))
            
            .mapToDouble(Gasto::getCantidad)
            .sum();

        return totalGastado > limite;
    }
}
