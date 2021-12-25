package es.jbp.comun.ges.entidad;

import es.jbp.comun.ges.utilidades.ConversionValores;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Set;

/**
 * Clase que encapsula un mapa ordenado de valores de campos (idCampo-valor)
 * @author Jorge
 */
public class MapaValores extends LinkedHashMap<String, Object> {

    public MapaValores() {
    }

    public MapaValores(MapaValores mapaValores) {
        this.putAll(mapaValores);
    }

    public MapaValores(Map<String, Object> mapaValores) {
        this.putAll(mapaValores);
    }

    public LinkedHashMap<String, Object> getMapa() {
        return this;
    }

    public boolean contiene(String idCampo) {
        return containsKey(idCampo);
    }

    public String toString() {
        return formatear("%", "=");
    }
        
    public void parsear(String cadena, ConsultaGes consulta, String separadorCampos, String separadorNombreValor) {
        String[] partes = cadena.split(separadorCampos);
        for (String parte : partes) {
            String[] asignacion = parte.split(separadorNombreValor);
            if (asignacion.length == 2) {
                String id = asignacion[0];
                CampoGes campo = consulta.getCampoPorId(id); 
                if (campo == null) {
                        return;
                }
                //Object valor = campo.convertirValor(asignacion[1]);
                Object valor = asignacion[1];
                valor = ConversionValores.aValorBD(valor, campo);
                put(id, valor);
            }
        }
    }
    
    public String formatear(String separadorCampos, String separadorNombreValor) {
        StringBuilder builder = new StringBuilder();
        boolean vacia = true;
        for (String nombre : keySet()) {
            Object valor = get(nombre);
            if (valor == null) {
                continue;
            }
            if (!vacia) {
                builder.append(separadorCampos);
            }
            builder.append(nombre);
            builder.append(separadorNombreValor);
            builder.append(valor.toString());
            vacia = false;
        }
        return builder.toString();
    }  
    
    
}
