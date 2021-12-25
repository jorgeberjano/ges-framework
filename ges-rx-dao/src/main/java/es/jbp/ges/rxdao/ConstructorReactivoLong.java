package es.jbp.ges.rxdao;

import es.jbp.comun.utiles.conversion.Conversion;
import io.r2dbc.spi.Row;
import io.r2dbc.spi.RowMetadata;

/**
 * Constructor reactivo de un objeto String a partir del resultado de una consulta.
 *
 * @author jberjano
 */

public class ConstructorReactivoLong extends ConstructorReactivoEntidades<Long> {

    private final String campo;

    public ConstructorReactivoLong() {
        campo = null;
    }

    public ConstructorReactivoLong(String campo) {
        this.campo = campo;
    }

    @Override
    public Long obtenerEntidad(Row row, RowMetadata rowMetadata) {
        if (campo != null) {
            return Conversion.toLong(row.get(campo));
        } else {
            return Conversion.toLong(row.get(0));
        }
    }

}

