package modelo;

import java.time.LocalDate;
import java.util.function.Predicate;

public class FiltroIntervalo implements Filtro {
    private final Predicate<LocalDate> validacionFecha;

    public FiltroIntervalo(LocalDate inicio, LocalDate fin) {
        this.validacionFecha = fecha -> 
            (!fecha.isBefore(inicio)) && (!fecha.isAfter(fin));
    }

    @Override
    public boolean esValido(Gasto gasto) {
        return validacionFecha.test(gasto.getFecha());
    }
}