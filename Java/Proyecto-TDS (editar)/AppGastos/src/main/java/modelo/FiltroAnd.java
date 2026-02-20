package modelo;

import java.util.Arrays;
import java.util.List;

public class FiltroAnd implements Filtro {
    private List<Filtro> filtros;

    public FiltroAnd(Filtro... filtros) {
        this.filtros = Arrays.asList(filtros);
    }
    
    public void addFiltro(Filtro filtro) {
        this.filtros.add(filtro);
    }

    @Override
    public boolean esValido(Gasto gasto) {
        return filtros.stream()
                .allMatch(filtro -> filtro.esValido(gasto));
    }
}