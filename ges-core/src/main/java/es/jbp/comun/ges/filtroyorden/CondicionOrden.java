package es.jbp.comun.ges.filtroyorden;

import es.jbp.comun.ges.entidad.CampoGes;
import es.jbp.comun.ges.entidad.ConsultaGes;
import es.jbp.comun.utiles.sql.compatibilidad.FormateadorSql;

/**
 * Representa una condición para representar el orden de los elementos.
 * @author jberjano
 */
public class CondicionOrden {
    
    public static final boolean ASC = false;
    public static final boolean DESC = true;
    
    private final CampoGes campo;
    private final boolean descendente;

    public CondicionOrden(CampoGes campo, boolean descendente) {
        this.campo = campo;
        this.descendente = descendente;
    }

    public String generarSql(FormateadorSql formateadorSql, ConsultaGes consulta) {
        if (campo == null) {
            return "";
        }
        return campo.getNombreCompletoCampo() + (descendente ? " DESC" : "");
    }
    
}
