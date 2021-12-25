package es.jbp.comun.ges.dao;

import es.jbp.comun.ges.entidad.ClavePrimaria;
import es.jbp.comun.ges.entidad.Filtro;
import es.jbp.comun.ges.entidad.EntidadGes;
import es.jbp.comun.ges.entidad.CampoGes;
import es.jbp.comun.ges.entidad.Orden;
import es.jbp.comun.ges.entidad.Pagina;
import es.jbp.comun.ges.entidad.ConsultaGes;
import es.jbp.comun.ges.utilidades.GestorSimbolos;
import es.jbp.comun.utiles.conversion.Conversion;
import es.jbp.comun.utiles.sql.ConstructorString;
import es.jbp.comun.utiles.sql.Dao;
import es.jbp.comun.utiles.sql.EjecutorSentenciaDelete;
import es.jbp.comun.utiles.sql.EjecutorSentenciaGuardado;
import es.jbp.comun.utiles.sql.EjecutorSentenciaSelect;
import es.jbp.comun.utiles.sql.GestorConexiones;
import es.jbp.comun.utiles.sql.PaginaEntidades;
import es.jbp.comun.utiles.sql.SecuenciaMaximoMasUno;
import es.jbp.comun.utiles.sql.compatibilidad.FormateadorSql;
import es.jbp.comun.utiles.sql.sentencia.SentenciaSql;
import java.util.List;
import java.util.Map;

/**
 *
 * @author jorge
 */
public class AccesoEntidadesGes extends Dao {
    private final ConsultaGes consulta;
    private final ConstructorEntidadGes constructor;
    private final GestorSimbolos gestorSimbolos;
    private final FormateadorSql formateadorSql;

    public AccesoEntidadesGes(ConsultaGes consulta, GestorConexiones gestorConexiones, GestorSimbolos gestorSimbolos) {
        super(gestorConexiones);
        this.consulta = consulta;
        constructor = new ConstructorEntidadGes(consulta);

        this.gestorSimbolos = gestorSimbolos;
        formateadorSql = gestorConexiones.getFormateadorSql();
    }

    public ConsultaGes getConsulta() {
        return consulta;
    }

    /**
     * Inserta una entidad.
     *
     * @param entidad la entidad
     * @return true en caso de exito
     */
    public boolean insertar(EntidadGes entidad) {

        EjecutorSentenciaGuardado ejecutor = crearEjecutorSentenciaInsert();

        String tabla = consulta.getTabla();
        ejecutor.setTabla(tabla);
        List<CampoGes> campos = consulta.getCampos();

        for (CampoGes campo : campos) {
            if (!campo.perteneceATabla(tabla)) {
                continue;
            }

            String idCampo = campo.getIdCampo();
            Object valor = entidad.getValor(idCampo);
            if (valor == null && campo.isClave() && campo.isSecuencia()) {
                valor = new SecuenciaMaximoMasUno();
                entidad.setValor(idCampo, valor);
            } else if (!entidad.contiene(idCampo)) {
                continue;
            }

            String nombreSqlCampo = campo.getNombreCompleto();

            if (campo.isClave()) {
                ejecutor.agregarPk(nombreSqlCampo, valor);
            } else {
                ejecutor.agregarCampo(nombreSqlCampo, valor);
            }
        }
        boolean ok = false;
        try {
            ok = ejecutor.ejecutar();
        } catch (Exception e) {
            reportarExcepcion("Error insertando la entidad " + consulta.getNombreEnSingular(), e);
            return false;
        }
        if (!ok) {
            return false;
        }
        asignarValoresRecuperados(entidad, ejecutor.getValoresRecuperados());
        return true;        
    }

    /**
     * Modifica una entidad.
     *
     * @param entidad la entidad
     * @return true en caso de exito
     */
    public boolean modificar(EntidadGes entidad) {
        EjecutorSentenciaGuardado ejecutor = crearEjecutorSentenciaUpdate();

        String tabla = consulta.getTabla();
        ejecutor.setTabla(tabla);
        List<CampoGes> campos = consulta.getCampos();

        for (CampoGes campo : campos) {
            if (!campo.perteneceATabla(tabla)) {
                continue;
            }
            String idCampo = campo.getIdCampo();
            String nombreSqlCampo = campo.getNombreCompleto();
            ClavePrimaria clavePrimaria = entidad.getClavePrimaria();
            if (clavePrimaria == null) {
                reportarExcepcion("Al modificar la entidad " + consulta.getNombreEnSingular() + " se debe especificar el campo clave " + idCampo, null);
                    return false;
            }
            if (clavePrimaria.contiene(idCampo)) {
                Object valor = clavePrimaria.get(idCampo);
                ejecutor.agregarPk(nombreSqlCampo, valor);
            } else if (campo.isClave()) {
                Object valorClave = clavePrimaria.get(idCampo);
                if (valorClave == null) {
                    reportarExcepcion("Al modificar la entidad " + consulta.getNombreEnSingular() + " se debe especificar el campo clave " + idCampo, null);
                    return false;
                }
                ejecutor.agregarPk(nombreSqlCampo, valorClave);
            }

            if (!entidad.contiene(idCampo)) {
                continue;
            }
            Object valor = entidad.getValor(idCampo);
            ejecutor.agregarCampo(nombreSqlCampo, valor);
        }

        boolean ok = false;
        try {
            ok = ejecutor.ejecutar();
        } catch (Exception e) {
            reportarExcepcion("Error modificando la entidad " + consulta.getNombreEnSingular(), e);
            return false;
        }
        entidad.actualizarValoresClavePrimaria();
        return ok;
    }

    /**
     * Borra una entidad.
     */
    public boolean borrar(EntidadGes entidad) {
        return borrar(entidad.getClavePrimaria());
    }

    public boolean borrar(ClavePrimaria clavePrimaria) {

        EjecutorSentenciaDelete ejecutorDelete = crearEjecutorSentenciaDelete();

        if (clavePrimaria == null) {
            return false;
        }
        ejecutorDelete.setTabla(consulta.getTabla());
        for (String idCampo : clavePrimaria.keySet()) {
            Object valor = clavePrimaria.get(idCampo);
            CampoGes campo = consulta.getCampoPorId(idCampo);
            if (campo == null) {
                reportarExcepcion("La clave " + idCampo + " no existe", null);
                return false;
            }
            ejecutorDelete.agregarPk(campo.getNombreCompleto(), valor);
        }
        boolean ok = false;
        try {
            ok = ejecutorDelete.ejecutar();
        } catch (Exception e) {
            reportarExcepcion("Error eliminando " + consulta.getNombreEnSingular(), e);
            return false;
        } finally {
        }
        return ok;
    }

    public String getSqlConsulta() {

        String sql = consulta.getSql();
        return gestorSimbolos.sustituirSimbolosYParametros(sql);
    }

    /**
     * Devuelve todas las entidades
     */
    public List<EntidadGes> getLista() {

        EjecutorSentenciaSelect ejecutorSelect = crearEjecutorSentenciaSelect();

        List<EntidadGes> entidades = null;
        try {
            String sql = getSqlConsulta();
            entidades = ejecutorSelect.obtenerListaEntidades(sql, constructor);
        } catch (Exception e) {
            reportarExcepcion("Error en obtener la lista de " + consulta.getNombreEnPlural(), e);
        }
        return entidades;
    }

    public List<String> getListaValores(String nombreCampo) {

        EjecutorSentenciaSelect ejecutorSelect = crearEjecutorSentenciaSelect();

        List<String> valores = null;
        try {
            String sql = getSqlConsulta();
            valores = ejecutorSelect.obtenerListaEntidades(sql, new ConstructorString(nombreCampo));
        } catch (Exception e) {
            reportarExcepcion("Error en obtener la lista valores de " + nombreCampo + " de " + consulta.getNombreEnPlural(), e);
        }
        return valores;
    }

    /**
     * Devuelve una entidad por su clave primaria
     */
    public EntidadGes getEntidad(ClavePrimaria pk) {

        EjecutorSentenciaSelect ejecutorSelect = crearEjecutorSentenciaSelect();

        if (pk == null || consulta == null) {
            return null;
        }

        String strWhere = construirWhere(pk);
        if (Conversion.isBlank(strWhere)) {
            return null;
        }

        EntidadGes entidad = null;
        SentenciaSql sentencia = new SentenciaSql(getSqlConsulta());
        sentencia.where(strWhere);
        try {
            String sql = sentencia.getSql();
            entidad = ejecutorSelect.obtenerEntidad(sql, constructor);
        } catch (Exception e) {
            reportarExcepcion("Error al obtener un " + consulta.getNombreEnSingular() + " por su clave primaria", e);
        }
        if (entidad != null) {
            entidad.setClavePrimaria(pk);
        }
        return entidad;
    }

    @Deprecated
    public PaginaEntidades<EntidadGes> getPagina(Filtro filtro,
            String campoOrden, boolean ordenDescendente, int primerElemento, int numeroElementos) {

        EjecutorSentenciaSelect ejecutorSelect = crearEjecutorSentenciaSelect();

        SentenciaSql sentencia = crearSentenciaSql(filtro, campoOrden, ordenDescendente);
        if (sentencia == null) {
            return null;
        }
        PaginaEntidades<EntidadGes> entidades = null;
        try {
            entidades = ejecutorSelect.obtenerPaginaEntidades(sentencia.getSql(), constructor,
                    primerElemento, numeroElementos);
        } catch (Exception e) {
            reportarExcepcion("Error al obtener una página de " + consulta.getNombreEnPlural(), e);
        }
        return entidades;
    }

    public SentenciaSql crearSentenciaSql(Filtro filtro, String campoOrden, boolean ordenDescendente) {

        SentenciaSql sentencia = new SentenciaSql(getSqlConsulta());
        if (filtro != null) {
            String filtroSql = filtro.generarSql(formateadorSql, consulta);
            if (filtroSql == null) {
                reportarExcepcion(filtro.getMensajeError(), null);
                return null;
            }
            sentencia.where(filtroSql);
        }
        if (!Conversion.isBlank(campoOrden)) {
            sentencia.orderBy(campoOrden, ordenDescendente);
        }

        return sentencia;
    }

    private SentenciaSql crearSentenciaSql(Filtro filtro, Orden orden) {

        SentenciaSql sentencia = new SentenciaSql(getSqlConsulta());
        if (filtro != null) {
            String filtroSql = filtro.generarSql(formateadorSql, consulta);
            if (filtroSql == null) {
                reportarExcepcion(filtro.getMensajeError(), null);
                return null;
            }
            sentencia.where(filtroSql);
        }
        if (orden != null) {
            sentencia.orderBy(orden.generarSql(formateadorSql, consulta));
        }

        return sentencia;
    }

    public PaginaEntidades<EntidadGes> getPagina(Filtro filtro, Orden orden, Pagina pagina) {

        EjecutorSentenciaSelect ejecutorSelect = crearEjecutorSentenciaSelect();

        SentenciaSql sentencia = crearSentenciaSql(filtro, orden);
        if (sentencia == null) {
            return null;
        }
        PaginaEntidades<EntidadGes> entidades = null;
        Integer indicePrimerElemento = pagina.getIndicePrimerElemento();
        Integer numeroElementos = pagina.getNumeroElementos();
        try {
            if (indicePrimerElemento == null || numeroElementos == null) {
                entidades = new PaginaEntidades<>();
                List<EntidadGes> lista = ejecutorSelect.obtenerListaEntidades(sentencia.getSql(), constructor);
                entidades.setListaEntidades(lista);
                entidades.setNumeroTotalEntidades(lista.size());
            } else {
                entidades = ejecutorSelect.obtenerPaginaEntidades(sentencia.getSql(), constructor, indicePrimerElemento, numeroElementos);
            }
        } catch (Exception e) {
            trazaSql(sentencia.getSql(), false);
            reportarExcepcion("Error al acceder a los datos de " + consulta.getNombreEnPlural(), e);
        }
        return entidades;
    }

    public List<EntidadGes> getLista(Filtro filtro, Orden orden) {

        EjecutorSentenciaSelect ejecutorSelect = crearEjecutorSentenciaSelect();

        SentenciaSql sentencia = crearSentenciaSql(filtro, orden);
        if (sentencia == null) {
            return null;
        }
        List<EntidadGes> lista = null;
        try {
            lista = ejecutorSelect.obtenerListaEntidades(sentencia.getSql(), constructor);
        } catch (Exception e) {
            trazaSql(sentencia.getSql(), false);
            reportarExcepcion("Error al acceder a los datos de " + consulta.getNombreEnPlural(), e);
        }
        return lista;
    }

    @Deprecated
    public List<EntidadGes> getListaFiltrada(Filtro filtro, String campoOrden, boolean ordenDescendente) {

        EjecutorSentenciaSelect ejecutorSelect = crearEjecutorSentenciaSelect();

        SentenciaSql sentencia = crearSentenciaSql(filtro, campoOrden, ordenDescendente);
        if (sentencia == null) {
            return null;
        }
        List<EntidadGes> entidades = null;
        try {
            entidades = ejecutorSelect.obtenerListaEntidades(sentencia.getSql(), constructor);
        } catch (Exception e) {
            trazaSql(sentencia.getSql(), false);
            reportarExcepcion("Error al acceder a los datos de " + consulta.getNombreEnPlural(), e);
        }
        return entidades;
    }

    private String construirWhere(ClavePrimaria pk) {

        SentenciaSql sentencia = new SentenciaSql();

        List<CampoGes> campos = consulta.getCampos();
        for (CampoGes campo : campos) {
            String nombreSqlCampo = campo.getNombreCompleto();
            String idCampo = campo.getIdCampo();
            Object valor = pk.get(idCampo);
            if (valor != null) {
                Object valorTipado = Conversion.convertirValor(valor, campo.getTipoDato());
                String valorSql = SentenciaSql.aFormatoSql(valorTipado);
                sentencia.where(nombreSqlCampo + " = " + valorSql);
            }
        }
        return sentencia.getWhere();
    }

    /**
     * Asigna a la entidad los valores recuperados de la sentencia de guardado
     * @param entidad Entidad Ges
     * @param mapaValoresSql Mapa de valores SQL recuperados de la sentencia de guardado
     */
    private void asignarValoresRecuperados(EntidadGes entidad, Map mapaValoresSql) {
        if (mapaValoresSql == null) {
            return;
        }
        List<CampoGes> campos = consulta.getCampos();
        for (CampoGes campo : campos) {
            Object valor = mapaValoresSql.get(campo.getNombre());
            entidad.setValor(campo.getIdCampo(), valor);
            if (campo.isClave()) {
                entidad.setValorClavePrimaria(campo.getIdCampo(), valor);
            }
        }
    }

}
