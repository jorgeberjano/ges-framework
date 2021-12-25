package es.jbp.comun.ges.filtroyorden;

import es.jbp.comun.ges.conversion.IConversorValores;
import es.jbp.comun.ges.entidad.CampoGes;
import es.jbp.comun.ges.entidad.ConsultaGes;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;


/**
 *
 * @author jberjano
 */
public class ConstructorFiltro {

    private ConsultaGes consulta;
    private IConversorValores manipuladorValores;
    private String mensajeError;

    public ConstructorFiltro(ConsultaGes consulta, IConversorValores manipuladorValores) {
        this.consulta = consulta;
        this.manipuladorValores = manipuladorValores;
    }

    /**
     * Invoca la creación de la condición de filtro de los parámetros que no
     * empiecen por "_" que corresponden con nombres de campos.
     */
    public ExpresionFiltro crearFiltro(Map<String, String> parametros) {
        mensajeError = null;        
        ExpresionFiltro expresionFiltro = new ExpresionFiltro(manipuladorValores);
        if (parametros == null) {
            return expresionFiltro;
        }

        Map<String, CondicionCompuestaFiltro> mapaCondicionesAgregadas = new HashMap<>();
        for (String key : parametros.keySet()) {
            if (key.startsWith("_")) {
                continue;
            }
            String parametro = quitarSufijo(key);
            String valor = parametros.get(key);

            CondicionCompuestaFiltro condicionExistente = mapaCondicionesAgregadas.get(parametro);
            CondicionFiltro condicionNueva = crearCondicion(parametro, valor);
            if (condicionNueva == null) {
                return null;
            }
            if (condicionExistente == null) {
                CondicionCompuestaFiltro condicionCompuestaFiltro = new CondicionCompuestaFiltro("OR");
                condicionCompuestaFiltro.agregarCondicion(condicionNueva);
                mapaCondicionesAgregadas.put(parametro, condicionCompuestaFiltro);
                expresionFiltro.agregarCondicion(condicionCompuestaFiltro);
            } else {                
                condicionExistente.agregarCondicion(condicionNueva);
            } 
        }
        return expresionFiltro;
    }

    /**
     * Esta funcion separa el nombre del campo del operador y delega la creación
     * de la condición de filtro en otro metodo.
     */
    private CondicionFiltro crearCondicion(String parametro, String valor) {

        if (valor == null || valor.isEmpty()) {
            mensajeError = "No se ha especificado ningún valor";
            return null;
        }
        String idCampo;

        Optional<OperadorFiltro> resultadoBusquedaOperador = Arrays.asList(OperadorFiltro.values()).stream()
                .filter(op -> parametro.endsWith("_" + op.name().toLowerCase())).findFirst();
                
        OperadorFiltro operador;
        if (resultadoBusquedaOperador.isPresent()) {
            operador = resultadoBusquedaOperador.get();
            idCampo = parametro.substring(0, parametro.length() - operador.toString().length() - 1);
        } else {
            operador = OperadorFiltro.EQ;
            idCampo = parametro;
        }
        return crearCondicion(idCampo, operador, valor);
    }

    private CondicionFiltro crearCondicion(String idCampo, OperadorFiltro operador, String valor) {

        if (!idCampo.contains("*")) {
            return crearCondicionUnCampo(idCampo, operador, valor);
        } else {
            return crearCondicionVariosCampos(idCampo, operador, valor);
        }
    }

    private CondicionFiltro crearCondicionUnCampo(String idCampo, OperadorFiltro operador, String valor) {
        CampoGes campo = consulta.getCampoPorId(idCampo);
        if (campo == null) {
            mensajeError = "El campo " + idCampo + " no existe";
            return null;
        }

        if (valor.contains("~")) {
            return crearCondicionVariosValores(idCampo, operador, valor);
        } else {
            return new CondicionSimpleFiltro(campo, operador, valor);
        }
    }

    private CondicionFiltro crearCondicionVariosCampos(String mascaraIdCampo, OperadorFiltro operador, String valor) {
        List<CampoGes> campos = consulta.getCamposPorMascaraIdCampo(mascaraIdCampo);
        if (campos == null || campos.isEmpty()) {
            mensajeError = "Ningún campo coincide con la máscara " + mascaraIdCampo;
            return null;
        }
        CondicionCompuestaFiltro condicion = new CondicionCompuestaFiltro("OR");
        campos.forEach(campo -> condicion.agregarCondicion(crearCondicionUnCampo(campo.getIdCampo(), operador, valor)));
        return condicion;
    }

    private CondicionFiltro crearCondicionVariosValores(String idCampo, OperadorFiltro operador, String valores) {

        CondicionCompuestaFiltro condicion = new CondicionCompuestaFiltro("OR");

        Arrays.asList(valores.split("~")).stream().forEach(valor
                -> condicion.agregarCondicion(crearCondicionUnCampo(idCampo, operador, valor)));

        return condicion;

    }

    public boolean huboError() {
        return mensajeError != null;
    }

    public String getMensajeError() {
        return mensajeError;
    }

    /**
     * Elimina el caracter '~' y todo lo que que hay a la derecha de una cadena
     */
    private String quitarSufijo(String key) {
        if (key == null || key.isEmpty()) {
            return key;
        }
        String[] partes = key.split("~");
        return partes[0];
    }
}
