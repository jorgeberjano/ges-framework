package es.jbp.comun.ges.servicio;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonNull;
import com.google.gson.JsonObject;
import es.jbp.comun.ges.entidad.ClavePrimaria;
import es.jbp.comun.ges.entidad.EntidadGes;
import es.jbp.comun.ges.entidad.ConsultaGes;
import es.jbp.comun.ges.entidad.MapaValores;
import es.jbp.comun.ges.servicio.IServicioJson;
import es.jbp.comun.ges.utilidades.ConversionValores;
import es.jbp.comun.utiles.reflexion.Reflexion;
import es.jbp.comun.utiles.tiempo.Fecha;
import es.jbp.comun.utiles.tiempo.FechaAbstracta;
import es.jbp.comun.utiles.tiempo.FechaHora;
import es.jbp.comun.utiles.tiempo.FechaHoraMs;
import es.jbp.comun.utiles.tiempo.Hora;
import java.util.Collection;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;
import org.json.JSONObject;
import org.springframework.context.annotation.Primary;
import org.springframework.stereotype.Service;

/**
 * Implementación de servicio para conversion de json a entidades.
 *
 * @author jberjano
 */
@Primary
@Service(value = "com.google.gson")
public class ServicioGson implements IServicioJson {

//    @Override
//    public String toJson(Object objeto, ConsultaGes consulta) {
//        JsonElement elemento = objectToJsonElement(objeto, consulta);
//        return serializar(elemento);
//    }

//    private JsonArray collectionToJsonArray(Collection lista, ConsultaGes consulta) {
//        JsonArray jsonArray = new JsonArray();
//        for (Object elemento : lista) {
//            if (elemento instanceof Boolean) {
//                jsonArray.add((Boolean) elemento);
//            } else if (elemento instanceof Number) {
//                jsonArray.add((Number) elemento);
//            } else if (elemento instanceof Character) {
//                jsonArray.add((Character) elemento);
//            } else if (elemento instanceof String) {
//                jsonArray.add((String) elemento);
//            } else if (elemento instanceof FechaAbstracta || elemento instanceof Hora) {
//                jsonArray.add(elemento.toString());
//            } else {
//                JsonElement jsonObject = objectToJsonElement(elemento, consulta);
//                jsonArray.add(jsonObject);
//            }
//
//        }
//        return jsonArray;
//    }
//
//    private JsonObject mapToJsonObject(Map mapa, ConsultaGes consulta) {
//        if (consulta != null) {
//            mapa = convertirValores(mapa, consulta);
//        }
//        JsonObject jsonObject = new JsonObject();
//        for (Object key : mapa.keySet()) {
//            String idCampo = key.toString();
//            Object valor = mapa.get(idCampo);
//            if (valor instanceof Boolean) {
//                jsonObject.addProperty(idCampo, (Boolean) valor);
//            } else if (valor instanceof Number) {
//                jsonObject.addProperty(idCampo, (Number) valor);
//            } else if (valor instanceof Character) {
//                jsonObject.addProperty(idCampo, (Character) valor);
//            } else if (valor instanceof String) {
//                jsonObject.addProperty(idCampo, (String) valor);
//            } else if (valor instanceof FechaAbstracta || valor instanceof Hora) {
//                jsonObject.addProperty(idCampo, valor.toString());
//            } else {
//                jsonObject.add(idCampo, objectToJsonElement(valor, null));
//            }
//        }
//        return jsonObject;
//    }
//
//    private JsonElement objectToJsonElement(Object objeto, ConsultaGes consulta) {
//        if (objeto == null) {
//            return JsonNull.INSTANCE;
//        }
//
//        if (objeto instanceof Collection) {
//            return collectionToJsonArray((Collection) objeto, consulta);
//        }
//        Map mapa;
//        if (objeto instanceof MapaValores) {
//            mapa = ((MapaValores) objeto).getMapa();
//        } else if (objeto instanceof EntidadGes) {
//            mapa = ((EntidadGes) objeto).getValores().getMapa();
//        } else if (objeto instanceof Map) {
//            mapa = (Map) objeto;
//        } else {
//            mapa = objetoAMapa(objeto);
//        }
//        mapa = convertirValores(mapa, consulta);
//        return mapToJsonObject(mapa, consulta);
//    }
//
//    private Map<String, Object> objetoAMapa(Object objeto) {
//        Map<String, Object> mapa = Reflexion.objetoAMapa(objeto);
//        return mapa;
//    }

//    private Map convertirValores(Map mapa, ConsultaGes consulta) {
//        if (consulta == null) {
//            return mapa;
//        }
//        Map mapaConverido = new LinkedHashMap();
//        mapaConverido.putAll(mapa);
//        consulta.getCampos().stream().forEach(campo -> {
//            Object valor = mapa.get(campo.getIdCampo());
//            valor = ConversionValores.aValorAPI(valor, campo);
//            mapa.put(campo.getIdCampo(), valor);
//        });
//        return mapaConverido;
//    }

    @Override
    public EntidadGes toEntidad(String json, ConsultaGes consulta
    ) {
        if (json == null) {
            return null;
        }
        JsonObject jsonObject = new Gson().fromJson(json, JsonObject.class);

        Gson gson = new Gson();
        Map<String, Object> map = (Map<String, Object>) gson.fromJson(jsonObject, HashMap.class);

        return consulta.construirEntidad(map);
    }

    @Override
    public EntidadGes toEntidad(ClavePrimaria clave, String json, ConsultaGes consulta) {
        if (json == null) {
            return null;
        }
        JSONObject jsonObject = new JSONObject(json);
        return consulta.construirEntidad(clave, jsonObject.toMap());
    }

//    private String serializar(JsonElement elemento) {
//        Gson gson = new GsonBuilder().setPrettyPrinting().serializeNulls().create();
//        return gson.toJson(elemento);
//    }

    @Override
    public String toJson(Object objeto) {
        Gson gson = new GsonBuilder()
                .registerTypeAdapter(Fecha.class, new SerializadorString())
                .registerTypeAdapter(FechaHora.class, new SerializadorString())
                .registerTypeAdapter(FechaHoraMs.class, new SerializadorString())
                .setPrettyPrinting()
                .serializeNulls()
                .create();
        return gson.toJson(objeto);
    }
}
