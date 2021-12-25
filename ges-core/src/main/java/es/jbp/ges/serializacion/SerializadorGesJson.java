package es.jbp.ges.serializacion;

import es.jbp.ges.entidad.BaseDatosGes;
import es.jbp.ges.entidad.CampoGes;
import es.jbp.ges.entidad.ConsultaGes;
import es.jbp.ges.entidad.Ges;
import es.jbp.ges.utilidades.GestorSimbolos;
import es.jbp.comun.utiles.sql.TipoDato;
import flexjson.JSONDeserializer;
import flexjson.JSONSerializer;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.stream.Collectors;

/**
 *
 * @author jberjano
 */
public class SerializadorGesJson implements SerializadorGes {

    private Map<String, Object> mapaSimbolos;

    public SerializadorGesJson() {
        mapaSimbolos = new HashMap<String, Object>();
    }

    public SerializadorGesJson(Map<String, Object> mapaSimbolos) {
        this.mapaSimbolos = mapaSimbolos;
    }

    @Override
    public void serializar(String nombreArchivo, Ges gestor) throws Exception {
        JSONSerializer serializador = new JSONSerializer();
        String json = serializador.deepSerialize(gestor);
        BufferedWriter writer = new BufferedWriter(new FileWriter(nombreArchivo));
        writer.write(json);
        writer.close();
    }

    @Override
    public Ges deserializarRecurso(String nombreRecurso) throws Exception {
        InputStream stream = getClass().getResourceAsStream(nombreRecurso);
        if (stream == null) {
            throw new FileNotFoundException(nombreRecurso);
        }
        return deserializar(stream);
    }

    @Override
    public Ges deserializarArchivo(String nombreArchivo) throws Exception {

        InputStreamReader reader;

        reader = new InputStreamReader(new FileInputStream(nombreArchivo), "UTF-8");

        return deserializar(reader);
    }

    public Ges deserializar(InputStream inputStream) throws Exception {
        return deserializar(new InputStreamReader(inputStream, "UTF-8"));

    }

    public Ges deserializar(Reader reader) throws Exception {
        if (reader == null) {
            return null;
        }
        JSONDeserializer<Ges> deserializador = new JSONDeserializer()
                .use(null, Ges.class)
                .use("baseDatos", BaseDatosGes.class)
                .use("consultasPantalla", ArrayList.class)
                .use("consultasPantalla.values", ConsultaGes.class)
                .use("consultasPantalla.values.campos", ArrayList.class)
                .use("consultasPantalla.values.campos.values", CampoGes.class)
                .use("consultasPantalla.values.campos.tipoDato", TipoDato.class);
        
        String texto = new BufferedReader(reader).lines().collect(Collectors.joining("\n"));
        GestorSimbolos gestorSimbolos = new GestorSimbolos(mapaSimbolos);
        texto = gestorSimbolos.sustituirSoloSimbolos(texto);
        
        return deserializador.deserialize(texto);
    }
}
