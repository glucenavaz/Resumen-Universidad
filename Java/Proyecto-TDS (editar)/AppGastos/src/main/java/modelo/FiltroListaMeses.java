package modelo;

import java.time.Month;
import java.util.List;

public class FiltroListaMeses implements Filtro {
    private List<Month> mesesPermitidos;

    public FiltroListaMeses(List<Month> meses) {
        this.mesesPermitidos = meses;
    }

    @Override
    public boolean esValido(Gasto gasto) {
        return mesesPermitidos.stream()
                .anyMatch(mes -> mes == gasto.getFecha().getMonth());
    }
}