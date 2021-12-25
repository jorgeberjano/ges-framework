package es.jbp.comun.ges.entidad;

import es.jbp.comun.ges.entidad.MapaValores;
import java.io.Serializable;
import java.util.Set;

/**
 * Representa una entidad generica GES
 * @author jberjano
 */
public class EntidadGes implements Serializable {
    
    private ClavePrimaria pk;
    private MapaValores valores = new MapaValores();
    
    /**
     * Establece la clave primaria.
     * @param pk 
     */
    public void setClavePrimaria(ClavePrimaria pk) {
        this.pk = pk;        
    }
    
    public ClavePrimaria getClavePrimaria() {
        return pk;
    }
    
    public void setValor(String idCampo, Object valor) {
        valores.put(idCampo, valor);
    }
    
    public void setValorClavePrimaria(String idCampo, Object valor) {
        if (pk == null)  {
            pk = new ClavePrimaria();
        }
        pk.put(idCampo, valor);
    }
    
    public Object getValorClavePrimaria(String idCampo) {
        if (pk == null) {
            return null;
        }
        return pk.get(idCampo);
    }
     
    public Object getValor(String idCampo) {
        return valores.get(idCampo);
    }
    
    @Deprecated
    public Object get(String idCampo) {
        return valores.get(idCampo);
    }
    
    public Set<String> getIdCampos() {
        return valores.keySet();
    }    

    public MapaValores getValores() {
        return valores;
    }

    public void setValores(MapaValores valores) {
        this.valores = valores;
    }

    public boolean contiene(String idCampo) {
        return valores.contiene(idCampo);
    }

    /**
     * Actualiza los valores de la clave primaria con los valores que tenga los
     * campos de la entidad. Se tras una modificacion por si ha cambiado algun
     * campo que sea parte de la clave primaria.
     */
    public void actualizarValoresClavePrimaria() {
        for (String idCampo : pk.keySet()) {
            if (valores.contiene(idCampo)) {
                pk.put(idCampo, valores.get(idCampo));
            }
        }
    }    
}
