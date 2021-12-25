package es.jbp.comun.ges.entidad;

import es.jbp.comun.ges.entidad.ConsultaGes;
import es.jbp.comun.ges.entidad.MapaValores;
import java.util.Map;

/**
 *
 * @author Jorge
 */
public class ClavePrimaria extends MapaValores {
    
    public ClavePrimaria() {        
    }

    public ClavePrimaria(Map<String, Object> mapaValores) {
        super(mapaValores);
    }
            
    public static ClavePrimaria crearDeCadena(String cadena, ConsultaGes consulta) {
        ClavePrimaria clavePrimaria = new ClavePrimaria();
        clavePrimaria.parsear(cadena, consulta, "%", "=");
        return clavePrimaria;
    }
}
