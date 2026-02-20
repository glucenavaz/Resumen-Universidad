package modelo;

import java.util.List;

import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.fasterxml.jackson.annotation.JsonTypeInfo;

@JsonTypeInfo(
    use = JsonTypeInfo.Id.NAME, 
    include = JsonTypeInfo.As.PROPERTY, 
    property = "tipo"
)
@JsonSubTypes({
    @JsonSubTypes.Type(value = AlarmaMensual.class, name = "MENSUAL"),
    @JsonSubTypes.Type(value = AlarmaSemanal.class, name = "SEMANAL")
})
public interface EstrategiaAlarma {
    boolean verificar(List<Gasto> historial, double limite, Categoria categoria);
}
