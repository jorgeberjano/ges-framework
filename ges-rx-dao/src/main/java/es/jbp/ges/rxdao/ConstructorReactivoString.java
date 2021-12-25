package es.jbp.ges.rxdao;

import es.jbp.comun.utiles.conversion.Conversion;
import io.r2dbc.spi.Row;
import io.r2dbc.spi.RowMetadata;

/**
 * Constructor reactivo de un objeto String a partir del resultado de una consulta.
 *
 * @author jberjano
 */

public class ConstructorReactivoString extends ConstructorReactivoEntidades<String> {

    private final String campo;

    public ConstructorReactivoString(String campo) {
        this.campo = campo;
    }

    @Override
    public String obtenerEntidad(Row row, RowMetadata rowMetadata) {
        if (campo != null) {
            return Conversion.toString(row.get(campo));
        } else {
            return Conversion.toString(row.get(0));
        }
    }

}

