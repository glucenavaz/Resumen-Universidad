package modelo;

import java.util.List;

public class FiltroListaCategorias implements Filtro {
    private List<Categoria> categoriasPermitidas;

    public FiltroListaCategorias(List<Categoria> categorias) {
        this.categoriasPermitidas = categorias;
    }

    @Override
    public boolean esValido(Gasto gasto) {
        return categoriasPermitidas.stream()
                .anyMatch(cat -> cat.getCategoria().equals(gasto.getCategoria().getCategoria()));
    }
}