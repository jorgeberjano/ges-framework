package es.jbp.comun.ges.entidad;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import static es.jbp.comun.ges.entidad.CampoGes.CAMPO_CLAVE;
import es.jbp.comun.ges.utilidades.ConversionValores;
import es.jbp.comun.utiles.conversion.Conversion;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * Representa una consulta GES
 *
 * @author jberjano
 */
public class ConsultaGes implements Serializable, Cloneable {

    // Estilos de visualizacion
    public final static int CONSULTA_EDICION_ESTANDAR = 0x000001;
    public final static int CONSULTA_OCULTA_MENU = 0x000002;
    public final static int CONSULTA_OCULTA_MENU_CONS = 0x000004;
    public final static int CONSULTA_OCULTA_BARRA = 0x000008;
    public final static int CONSULTA_SIN_ALTA = 0x000010;
    public final static int CONSULTA_SIN_BAJA = 0x000020;
    public final static int CONSULTA_SIN_MODIFICACION = 0x000040;
    public final static int CONSULTA_SIN_COPIA = 0x000800;
    public final static int CONSULTA_SIN_VISUALIZACION = 0x000100;
    public final static int CONSULTA_SIN_CONFIGURACION = 0x000200;
    public final static int CONSULTA_SIN_IMPRESION = 0x000400;
    public final static int CONSULTA_SIN_TOTALIZACION = 0x000800;
    public final static int CONSULTA_SIN_IMPORTACION = 0x001000;
    public final static int CONSULTA_SIN_EXPORTACION = 0x002000;
    public final static int CONSULTA_SIN_FILTRO = 0x004000;
    public final static int CONSULTA_SIN_EXTRACTO = 0x008000;
    public final static int CONSULTA_SELECCION_MULTIPLE = 0x010000;
    public final static int CONSULTA_PRECEDER_SEPARADOR = 0x020000;
    public final static int CONSULTA_NOMBRE_FEMENINO = 0x040000;
    public final static int CONSULTA_EDITAR_SUBCONSULTAS = 0x080000;
    public final static int CONSULTA_ORDEN_AUTO_ASC = 0x100000;
    public final static int CONSULTA_ORDEN_AUTO_DESC = 0x200000;

    // Estilos de impresion
    public final static int CONSIMPR_ORIENTACION_VERTICAL = 0x000001;
    public final static int CONSIMPR_ORIENTACION_HORIZONTAL = 0x000002;
    public final static int CONSIMPR_LINEAS_VERTICALES = 0x000004;
    public final static int CONSIMPR_LINEAS_HORIZONTALES = 0x000008;
    public final static int CONSIMPR_SUBTOTALES = 0x000010;
    public final static int CONSIMPR_TOTAL_GENERAL = 0x000020;
    public final static int CONSIMPR_TITULO_POR_DEFECTO = 0x000040;
    public final static int CONSIMPR_ENCABEZADO_POR_DEFECTO = 0x000080;
    public final static int CONSIMPR_ANCHURA_FIJA = 0x000100;
    public final static int CONSIMPR_INFORME_DE_DETALLE = 0x000200;
    public final static int CONSIMPR_FORMATO_TICKET = 0x000400;
    public final static int CONSIMPR_PERMITIR_EXPORTAR_RTF = 0x000800;
    public final static int CONSIMPR_PEDIR_NUMERO_COPIAS = 0x001000;
    public final static int CONSIMPR_NO_IMPRIMIR_RECUADRO = 0x002000;
    public final static int CONSIMPR_NO_IMPRIMIR_LISTA = 0x004000;
    public final static int CONSIMPR_TOTALIZAR_ANTERIORES = 0x008000;
    public final static int CONSIMPR_GRUPOS_FUERA_DE_TABLA = 0x010000;
    public final static int CONSIMPR_MANTENER_ESPACIADO = 0x020000;
    public final static int CONSIMPR_SIN_MARGENES = 0x040000;

    private String idConsulta;
    private String nombreEnPlural;
    private String nombreEnSingular;
    private String tabla;
    private int imagen;
    private String nombreSubconsultas;
    private String camposFiltroPrevio;
    private String sql;
    private List<CampoGes> campos = new ArrayList();
    private List<CampoGes> camposVisibles;
    private int estilo;
    private String camposPorDefecto;
    private String valoresPorDefecto;
    private int estiloImpresion;
    private String tituloImpresion;
    private String subtituloImpresion;
    private String encabezadoImpresion;
    private String pieImpresion;
    private String textoInicialImpresion;
    private String textoFinalImpresion;
    private String consultasSeleccionPrevia;

    public void setIdConsulta(String idConsulta) {
        this.idConsulta = idConsulta;
    }

    public String getIdConsulta() {
        return idConsulta;
    }

    public String getNombreEnPlural() {
        return nombreEnPlural;
    }

    public void setNombreEnPlural(String nombreEnPlural) {
        this.nombreEnPlural = nombreEnPlural;
    }

    public String getNombreEnSingular() {
        return nombreEnSingular;
    }

    public void setNombreEnSingular(String nombreEnSingular) {
        this.nombreEnSingular = nombreEnSingular;
    }

    public String getTabla() {
        return tabla;
    }

    public void setTabla(String tabla) {
        this.tabla = tabla;
    }

    public int getImagen() {
        return imagen;
    }

    public void setImagen(int imagen) {
        this.imagen = imagen;
    }

    public String getNombreSubconsultas() {
        return nombreSubconsultas;
    }

    public void setNombreSubconsultas(String nombreSubconsultas) {
        this.nombreSubconsultas = nombreSubconsultas;
    }

    public String getCamposFiltroPrevio() {
        return camposFiltroPrevio;
    }

    public void setCamposFiltroPrevio(String camposFiltroPrevio) {
        this.camposFiltroPrevio = camposFiltroPrevio;
    }

    public List<CampoGes> construirListaCamposFiltroPrevio() {
        List<String> idsCampos = Conversion.convertirTextoEnLista(camposFiltroPrevio);
        return idsCampos.stream().filter((idCampo) -> {
            return getCampoPorId(idCampo) != null;
        }).map((nombeCampo) -> {
            return getCampoPorId(nombeCampo);
        }).collect(Collectors.toList());
    }

    public String getSql() {
        return sql;
    }

    public void setSql(String sql) {
        this.sql = sql;
    }

    public List<CampoGes> getCampos() {
        return campos;
    }
    
    public void setCampos(List<CampoGes> campos) {
        this.campos = campos;
        camposVisibles = null;
    }

    public void setListaCampos(List<CampoGes> listaCampos) {
        setCampos(listaCampos);
    }
    
    public List<CampoGes> getListaCampos() {
        return getCampos();
    }

    public List<CampoGes> getListaCamposVisibles() {
        if (camposVisibles == null) {
            camposVisibles = new ArrayList<>();
            campos.stream().forEach((campo) -> {
                if (!campo.isOculto()) {
                    camposVisibles.add(campo);
                }
            });
        }
        return camposVisibles;
    }

    public CampoGes getCampoPorNombre(String nombreCampo) {
        return campos.stream().filter((c) -> c.getNombre().equals(nombreCampo)).findFirst().get();
    }

    public CampoGes getCampoPorId(String idCampo) {
        Optional<CampoGes> campoOpcional = campos.stream().filter((c) -> c.getIdCampo().equals(idCampo)).findFirst();
        if (!campoOpcional.isPresent()) {
            return null;
        }
        return campoOpcional.get();
    }

    public List<CampoGes> getCamposPorMascaraIdCampo(String mascaraIdCampo) {
        String regEx = mascaraIdCampo.replace("*", ".*");
        return campos.stream().filter((c) -> c.getIdCampo().matches(regEx)).collect(Collectors.toList());
    }

    public int getEstilo() {
        return estilo;
    }

    public void setEstilo(int estilo) {
        this.estilo = estilo;
    }

    public boolean tieneEstilo(int mascara) {
        return (estilo & mascara) != 0;
    }

    public boolean isOcultaEnMenu() {
        return tieneEstilo(CONSULTA_OCULTA_MENU);
    }

    public boolean isSinAlta() {
        return tieneEstilo(CONSULTA_SIN_ALTA) || !tieneEstilo(CONSULTA_EDICION_ESTANDAR);
    }

    public boolean isSinBaja() {
        return tieneEstilo(CONSULTA_SIN_BAJA) || !tieneEstilo(CONSULTA_EDICION_ESTANDAR);
    }

    public boolean isSinModificacion() {
        return tieneEstilo(CONSULTA_SIN_MODIFICACION) || !tieneEstilo(CONSULTA_EDICION_ESTANDAR);
    }

    public boolean isSinTotalizacion() {
        return tieneEstilo(CONSULTA_SIN_TOTALIZACION);
    }

    public String getCamposPorDefecto() {
        return camposPorDefecto;
    }

    public void setCamposPorDefecto(String camposPorDefecto) {
        this.camposPorDefecto = camposPorDefecto;
    }

    public String getValoresPorDefecto() {
        return valoresPorDefecto;
    }

    public void setValoresPorDefecto(String valoresPorDefecto) {
        this.valoresPorDefecto = valoresPorDefecto;
    }

    public int getEstiloImpresion() {
        return estiloImpresion;
    }

    public void setEstiloImpresion(int estiloImpresion) {
        this.estiloImpresion = estiloImpresion;
    }

    public boolean tieneEstiloImpresion(int mascara) {
        return (estiloImpresion & mascara) != 0;
    }

    public String getTituloImpresion() {
        return tituloImpresion;
    }

    public void setTituloImpresion(String tituloImpresion) {
        this.tituloImpresion = tituloImpresion;
    }

    public String getSubtituloImpresion() {
        return subtituloImpresion;
    }

    public void setSubtituloImpresion(String subtituloImpresion) {
        this.subtituloImpresion = subtituloImpresion;
    }

    public String getEncabezadoImpresion() {
        return encabezadoImpresion;
    }

    public void setEncabezadoImpresion(String encabezadoImpresion) {
        this.encabezadoImpresion = encabezadoImpresion;
    }

    public String getPieImpresion() {
        return pieImpresion;
    }

    public void setPieImpresion(String pieImpresion) {
        this.pieImpresion = pieImpresion;
    }

    public String getTextoInicialImpresion() {
        return textoInicialImpresion;
    }

    public void setTextoInicialImpresion(String textoInicialImpresion) {
        this.textoInicialImpresion = textoInicialImpresion;
    }

    public String getTextoFinalImpresion() {
        return textoFinalImpresion;
    }

    public void setTextoFinalImpresion(String textoFinalImpresion) {
        this.textoFinalImpresion = textoFinalImpresion;
    }

    public String getConsultasSeleccionPrevia() {
        return consultasSeleccionPrevia;
    }

    public void setConsultasSeleccionPrevia(String consultasSeleccionPrevia) {
        this.consultasSeleccionPrevia = consultasSeleccionPrevia;
    }

    @Override
    public String toString() {
        return nombreEnPlural;
    }

    /**
     * Obtiene el único campo que tiene el estilo clave. Si hay mas campos clave
     * devuelve null
     *
     * @return el campo clave
     */
    public CampoGes getCampoClave() {
        CampoGes clave = null;
        for (CampoGes campo : campos) {
            if (campo.tieneEstilo(CAMPO_CLAVE)) {
                if (clave == null) {
                    clave = campo;
                } else {
                    return null;
                }
            }
        }
        return clave;
    }

    /**
     * Obtiene el primer campo que tiene tipo de rol NOMBRE.
     *
     * @return el campo nombre
     */
    public CampoGes getCampoNombre() {
        Optional<CampoGes> resultado = campos.stream().filter((campo) -> {
            return campo.getTipoRol() == TipoRolGes.NOMBRE;
        }).findFirst();
        return resultado.isPresent() ? resultado.get() : null;
    }

    public String toJson() {
        Gson gson = new GsonBuilder().create();
        return gson.toJson(this);
    }

    public EntidadGes construirEntidad(Map<String, Object> map) {
        EntidadGes entidad = new EntidadGes();
        asignarValoresEntidad(entidad, map);
        return entidad;
    }

    public EntidadGes construirEntidad(ClavePrimaria clave, Map<String, Object> map) {
        EntidadGes entidad = new EntidadGes();
        asignarValoresEntidad(entidad, map);
        entidad.setClavePrimaria(clave);
        return entidad;
    }

    /**
     * Ser asignan los valores contenidos en un mapa a una entidad de esta consulta.
     * Si el campo no pertenece a la consulta se le asigna su valor sin convertir.
     * @param entidad
     * @param map
     */
    
    public void asignarValoresEntidad(EntidadGes entidad, Map<String, Object> map) {

        for (CampoGes campo : campos) {
            String idCampo = campo.getIdCampo();
            // Los campos que no tengan valor en el mapa se omiten
            if (!map.containsKey(idCampo)) {
                continue;
            }
            Object valor = map.get(idCampo);
            Object valorConvertido = ConversionValores.aValorBD(valor, campo);
            entidad.setValor(idCampo, valorConvertido);
            if (campo.isClave()) {
                entidad.setValorClavePrimaria(idCampo, valorConvertido);
            }
        }
    }
//    public void asignarValoresEntidad(EntidadGes entidad, Map<String, Object> map) {
//
//        for (String idCampo : map.keySet()) {
//            CampoGes campo = getCampoPorId(idCampo);
//            Object valor = map.getValor(idCampo);
//            if (campo == null) {
//                entidad.setValor(idCampo, valor);
//                continue;
//            }
//            Object valorConvertido = ConversionValores.aValorBD(valor, campo);
//            entidad.setValor(idCampo, valorConvertido);
//            if (campo.isClave()) {
//                entidad.setValorClavePrimaria(idCampo, valorConvertido);
//            }
//        }
//    }

    @Override
    public Object clone() throws CloneNotSupportedException {
        return super.clone();
    }

    /**
     * Modifica la posición de un campo en la lista de campos permitiendo avanzar
     * (desplazamiento positivo) o retroceder (desplazamiento negativo).
     * @param idCampo
     * @param desplazamiento
     * @return
     */
    public boolean modificarPosicionCampo(final String idCampo, int desplazamiento) {
        if (campos == null || idCampo == null) {
            return false;
        }
        int i = 0;
        for (CampoGes campo : campos) {
            if (campo.getIdCampo().equals(idCampo)) {
                int nuevaPosicion = i + desplazamiento;

                if (nuevaPosicion < 0 || nuevaPosicion >= campos.size()) {
                    return false;
                }
                campos.remove(i);
                campos.add(nuevaPosicion, campo);
                return true;
            }
            i++;
        }
        return false;
    }

    public boolean eliminarCampo(final String idCampo) {

        Stream<CampoGes> stream = campos.stream();
        campos = stream.filter(item -> {
            return !((CampoGes) item).getIdCampo().equals(idCampo);
        }).collect(Collectors.toList());
        return true;
    }

    public void agregarCampo(CampoGes campo) throws CloneNotSupportedException {
        campo = (CampoGes) campo.clone();
        campos.add(campo);
    }

    public ClavePrimaria construirClavePrimariaDeId(String id) {
        return construirClavePrimaria(id);
    }

    public ClavePrimaria construirClavePrimaria(String... valores) {

        List<CampoGes> camposClave = campos.stream().filter(c -> c.isClave()).collect(Collectors.toList());
        if (camposClave.size() != valores.length) {
            return null;
        }
        ClavePrimaria clave = new ClavePrimaria();
        int i = 0;
        for (CampoGes campoClave : camposClave) {
            Object valor = ConversionValores.aValorBD(valores[i++], campoClave);
            clave.put(campoClave.getIdCampo(), valor);
        }
        return clave;
    }
}
