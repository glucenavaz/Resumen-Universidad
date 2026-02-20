package modelo;

import java.time.LocalDate;
import java.util.List;

public class AlarmaMensual implements EstrategiaAlarma {
	@Override
    public boolean verificar(List<Gasto> historial, double limite, Categoria categoria) {
		LocalDate hoy = LocalDate.now();
		double totalGastado = historial.stream()
				//Filtrar por fecha (Mes actual y Año actual)
				.filter(g -> g.getFecha().getMonth() == hoy.getMonth() && 
                			 g.getFecha().getYear() == hoy.getYear())
				//Filtrar por categoria
				.filter(g -> categoria == null || 
                			(g.getCategoria() != null && 
                			 g.getCategoria().getCategoria().equalsIgnoreCase(categoria.getCategoria())))
				//Sumar cantidades
				.mapToDouble(Gasto::getCantidad)
				.sum();
		
		return totalGastado > limite;		
	}
}
