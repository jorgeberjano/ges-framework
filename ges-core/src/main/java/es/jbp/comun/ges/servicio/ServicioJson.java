package es.jbp.comun.ges.servicio;

import es.jbp.comun.ges.entidad.ClavePrimaria;
import es.jbp.comun.ges.entidad.EntidadGes;
import es.jbp.comun.ges.entidad.CampoGes;
import es.jbp.comun.ges.entidad.ConsultaGes;
import es.jbp.comun.ges.entidad.MapaValores;
import es.jbp.comun.utiles.conversion.Conversion;
import java.util.List;
import org.json.JSONArray;
import org.json.JSONObject;
import org.springframework.stereotype.Service;

/**
 *
 * @author jberjano
 */
@Deprecated
@Service(value = "org.json")
public class ServicioJson  {
     
    public String toJson(ClavePrimaria clavePrimaria, ConsultaGes consulta) {
        
        JSONObject jsonObject = toJsonObject(clavePrimaria, consulta);
        return jsonObject.toString(4);
    }
    
    public String toJson(EntidadGes entidad, ConsultaGes consulta) {
        
        JSONObject jsonObject = toJsonObject(entidad.getValores(), consulta);
        return jsonObject.toString(4);
    }

    private JSONObject toJsonObject(MapaValores mapaValores, ConsultaGes consulta) {
        JSONObject jsonObject = new JSONObject();
        for (CampoGes campo : consulta.getCampos()) {
            String idCampo = campo.getIdCampo();
            if (Conversion.isBlank(idCampo)) {
                continue;
            }
            Object valor = mapaValores.get(idCampo);
            if (valor == null) {
                valor = JSONObject.NULL;
            }
            jsonObject.put(idCampo, valor);
        }
        return jsonObject;
    }

    public String toJson(List<EntidadGes> entidades, ConsultaGes consulta) {
        JSONArray jsonArray = new JSONArray();
        for (EntidadGes entidad : entidades) {
            jsonArray.put(toJsonObject(entidad.getValores(), consulta));
        }
        return jsonArray.toString(4);
    }    

    public EntidadGes toEntidad(String json, ConsultaGes consulta) {
        if (json == null) {
            return null;
        }
        JSONObject jsonObject = new JSONObject(json);
        return consulta.construirEntidad(jsonObject.toMap());
    }
    
    public EntidadGes toEntidad(ClavePrimaria clave, String json, ConsultaGes consulta) {
        if (json == null) {
            return null;
        }
        JSONObject jsonObject = new JSONObject(json);
        return consulta.construirEntidad(clave, jsonObject.toMap());
    }
}
