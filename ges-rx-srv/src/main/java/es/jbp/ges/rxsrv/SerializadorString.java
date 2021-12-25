package es.jbp.ges.rxsrv;

import com.google.gson.JsonElement;
import com.google.gson.JsonPrimitive;
import com.google.gson.JsonSerializationContext;
import com.google.gson.JsonSerializer;
import java.lang.reflect.Type;

/**
 *
 * @author jorge
 */
public class SerializadorString implements JsonSerializer<Object> {
    @Override
    public JsonElement serialize(Object src, Type typeOfSrc, JsonSerializationContext jsc) {
        return new JsonPrimitive(src.toString());
    }
}
