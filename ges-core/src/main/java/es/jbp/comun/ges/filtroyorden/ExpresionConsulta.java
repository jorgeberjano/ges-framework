package es.jbp.comun.ges.filtroyorden;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class ExpresionConsulta {
    private ExpresionFiltro expresionFiltro;
    private ExpresionOrden expresionOrden;
    private ExpresionPagina expresionPagina;
}
