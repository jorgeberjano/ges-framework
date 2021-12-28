package es.jbp.ges.utilidades;

import es.jbp.ges.entidad.EntidadGes;
import es.jbp.ges.entidad.MapaValores;
import es.jbp.comun.utiles.reflexion.Reflexion;
import org.apache.poi.ss.formula.functions.T;

import java.lang.reflect.InvocationTargetException;
import java.util.Map;

/**
 * Conversión de entidades en formato Ges y objeto
 * @author jberjano
 */
public class ConversionEntidades {

    public static boolean deEntidadGesAEntidadObjeto(EntidadGes entidadGes, Object entidadObjeto) {
        try {
            Reflexion.mapaAObjeto(entidadGes.getValores().getMapa(), entidadObjeto);
        } catch (Exception ex) {
            return false;
        }
        return true;
    }

    public static boolean deEntidadObjetoAEntidadGes(Object entidadObjeto, EntidadGes entidad) {
        Map<String, Object> mapa;
        try {
            mapa = Reflexion.objetoAMapa(entidadObjeto);
        } catch (Exception ex) {
            return false;
        }
        MapaValores valores = new MapaValores(mapa);
        entidad.setValores(valores);
        return false;
    }

    public static <T> T crearEntidadObjeto(EntidadGes entidadGes, Class clazz) {

        T entidadObjeto = null;
        try {
            entidadObjeto = Reflexion.<T>crearObjeto(clazz);
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        } catch (InstantiationException e) {
            e.printStackTrace();
        } catch (NoSuchMethodException e) {
            e.printStackTrace();
        } catch (InvocationTargetException e) {
            e.printStackTrace();
        }
        deEntidadGesAEntidadObjeto(entidadGes, entidadObjeto);
        return entidadObjeto;
    }
}
