package pe.edu.upeu.sysventas.model;
import lombok.Getter;
import lombok.Setter;
@Getter
@Setter
public class Emisor {
    Long ideEmisor;
    String ruc;
    String nombreComercial;
    String ubigeo;
    String domiciloFiscal;
    String urbanizacion;
    String departamento;
    String provincia;
    String distrito;
}
