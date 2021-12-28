package es.jbp.ges.servicio;

import es.jbp.comun.utiles.depuracion.GestorLog;
import es.jbp.ges.conversion.ConversorValoresBase;
import es.jbp.ges.conversion.IConversorValores;
import es.jbp.ges.entidad.ConsultaGes;
import es.jbp.ges.entidad.Ges;
import es.jbp.ges.serializacion.SerializadorGes;
import es.jbp.ges.serializacion.SerializadorGesJson;
import es.jbp.ges.serializacion.SerializadorGesXml;
import es.jbp.ges.utilidades.GestorSimbolos;
import org.springframework.beans.factory.config.ConfigurableBeanFactory;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

/**
 * Servicio que proporciona los metadatos segun el idioma.
 * @author jberjano
 */
@Service
@Scope(value = ConfigurableBeanFactory.SCOPE_SINGLETON)
public class ServicioGes implements IServicioGes {
    
    private final Map<String, IServicioPersonalizado> mapaServiciosPersonalizados;
    private final IServicioPersonalizado manipuladorNulo = new ServicioPersonalizadoBase();
    private IConversorValores conversorValores = new ConversorValoresBase();
    private final Map<String, Ges> mapaGestores = new HashMap<>();

    public ServicioGes(List<IServicioPersonalizado> serviciosPersonalizados) {
        mapaServiciosPersonalizados = serviciosPersonalizados.stream()
                .collect(Collectors.toMap(IServicioPersonalizado::getIdConsulta, Function.identity()));
    }

    @Override
    public void crearGestor(String idioma, String archivoGes, Map<String, Object> mapaSimbolos) {
        
        Ges gestor;
        SerializadorGes serializador;
        
        if (archivoGes.toLowerCase().endsWith(".json")) {
            serializador = new SerializadorGesJson(mapaSimbolos);
        } else {
            serializador = new SerializadorGesXml(mapaSimbolos);
        }
        
        
        try {
            gestor = serializador.deserializarRecurso("/" + archivoGes);
        } catch (Exception ex) {
            GestorLog.error("No se ha podido deserializar el archivo " + archivoGes, ex);
            return;
        }
        
        mapaGestores.put(idioma, gestor);
    }
    
    public Ges obtenerGestor(String idioma) {
        Ges gestor = mapaGestores.get(idioma);
        if (gestor == null) {
            gestor = mapaGestores.get("es");
        }
        return gestor;
    }

    @Override
    public List<ConsultaGes> getConsultas(String idioma) {
        Ges gestor = obtenerGestor(idioma);
        if (gestor == null) {
            return new ArrayList();
        }
        
        return gestor.getConsultasPantalla();
    }

    @Override
    public ConsultaGes getConsultaPorId(String idioma, String idConsulta) {
        Ges gestor = obtenerGestor(idioma);
        if (gestor == null) {
            return null;
        }
        return gestor.getConsultaPorId(idConsulta);
    }

    @Override
    public GestorSimbolos getGestorSimbolos(String idioma) {
        Ges gestor = obtenerGestor(idioma);
        if (gestor == null) {
            return null;
        }
        return gestor.getGestorSimbolos();
    }

    @Override
    public void registrarConversorValores(IConversorValores manipulador) {
        this.conversorValores = manipulador;
    }
    
    @Override
    public IServicioPersonalizado obtenerServicioPersonalizado(String idConsulta) {
        if (mapaServiciosPersonalizados.containsKey(idConsulta)) {
            return mapaServiciosPersonalizados.get(idConsulta);            
        } else {
            return manipuladorNulo;         
        }
    }

    @Override
    public IConversorValores obtenerConversorValores() {
        return conversorValores;
    }
    
    public List<String> getIdsConsultas(String idioma) {
        Ges gestor = obtenerGestor(idioma);
        if (gestor == null) {
            return new ArrayList();
        }
        return gestor.getConsultasPantalla().stream().map(consulta -> consulta.getIdConsulta()).collect(Collectors.toList());
    }

    @Override
    public void definirSimbolo(String idioma, String nombre, String valor) {
        Ges gestor = obtenerGestor(idioma);
        if (gestor == null) {
            return;
        }
        gestor.definirSimbolo(nombre, valor);
    }

}
