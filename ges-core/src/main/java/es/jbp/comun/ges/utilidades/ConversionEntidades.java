package es.jbp.comun.ges.utilidades;

import es.jbp.comun.ges.entidad.EntidadGes;
import es.jbp.comun.ges.entidad.MapaValores;
import es.jbp.comun.utiles.reflexion.Reflexion;
import java.util.Map;

/**
 * Conversión de entidades en formato Ges y objeto
 * @author jberjano
 */
public class ConversionEntidades {

    public static boolean deEntidadGesAEntidadObjeto(EntidadGes entidadGes, Object objetoEntidad) {
        try {
            Reflexion.mapaAObjeto(entidadGes.getValores().getMapa(), objetoEntidad);
        } catch (Exception ex) {
            return false;
        }
        return true;
    }

    public static boolean deEntidadObjetoAEntidadGes(Object objetoEntidad, EntidadGes entidad) {
        Map<String, Object> mapa;
        try {
            mapa = Reflexion.objetoAMapa(objetoEntidad);
        } catch (Exception ex) {
            return false;
        }
        MapaValores valores = new MapaValores(mapa);
        entidad.setValores(valores);
        return false;
    }
}
