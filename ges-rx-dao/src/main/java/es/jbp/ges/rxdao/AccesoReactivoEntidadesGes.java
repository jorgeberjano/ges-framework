package es.jbp.ges.rxdao;

import es.jbp.ges.rxdao.conexion.GestorConexionesReactivas;
import es.jbp.ges.rxdao.interfaces.IEjecutorComando;
import es.jbp.ges.utilidades.ConversionValores;
import es.jbp.ges.utilidades.GestorSimbolos;
import es.jbp.comun.utiles.conversion.Conversion;
import es.jbp.comun.utiles.sql.Plantilla;
import es.jbp.comun.utiles.sql.SecuenciaMaximoMasUno;
import es.jbp.comun.utiles.sql.compatibilidad.FormateadorSql;
import es.jbp.comun.utiles.sql.sentencia.SentenciaSql;
import es.jbp.ges.entidad.*;
import org.apache.commons.lang3.StringUtils;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import java.util.List;
import java.util.Map;

/**
 * Representa la clase Dao para acceder al repositorio de entidades genéricas.
 *
 * @author Jorge
 */
public class AccesoReactivoEntidadesGes extends DaoReactivo {

    private final ConsultaGes consulta;
    private final ConstructorReactivoEntidadesGes constructor;
    private final GestorSimbolos gestorSimbolos;
    private final FormateadorSql formateadorSql;

    public AccesoReactivoEntidadesGes(ConsultaGes consulta, GestorConexionesReactivas gestorConexiones, GestorSimbolos gestorSimbolos) {
        super(gestorConexiones);
        this.consulta = consulta;
        constructor = new ConstructorReactivoEntidadesGes(consulta);

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
    public Mono<EntidadGes> insertar(EntidadGes entidad) {

        IEjecutorComando ejecutor = crearEjecutorSentenciaInsert();

        String tabla = consulta.getTabla();
        ejecutor.setTabla(tabla);

        consulta.getCampos().stream()
                .filter(campo -> campo.perteneceATabla(tabla))
                .forEach(campo -> asignarValorInsercion(ejecutor, campo, entidad));

        return ejecutor.ejecutar(new ConstructorReactivoEntidadesGes(consulta)).take(1).next();
    }

    private void asignarValorInsercion(IEjecutorComando ejecutor, CampoGes campo, EntidadGes entidad) {
        String idCampo = campo.getIdCampo();
        Object valor = entidad.getValor(idCampo);
        Object valorPorDefecto = campo.getValorPorDefecto();

        if (valor == null && campo.isClave() && campo.isSecuencia()) {
            valor = new SecuenciaMaximoMasUno();
            entidad.setValor(idCampo, valor);
        } else if (!entidad.contiene(idCampo) && valorPorDefecto == null) {
            return;
        } else if (valor == null && valorPorDefecto != null) {
            valor = ConversionValores.aValorBD(valorPorDefecto, campo);
        } else {
            valor = ConversionValores.aValorBD(valor, campo);
        }
        String nombreSqlCampo = campo.getNombreCompleto();
        ejecutor.agregarCampo(nombreSqlCampo, valor);
    }

    /**
     * Modifica una entidad.
     */
    public Mono<EntidadGes> modificar(EntidadGes entidad) {
        IEjecutorComando ejecutor = crearEjecutorSentenciaUpdate();

        String tabla = consulta.getTabla();
        ejecutor.setTabla(tabla);
        List<CampoGes> campos = consulta.getCampos();

        ClavePrimaria clavePrimaria = entidad.getClavePrimaria();
        if (clavePrimaria == null) {
            return Mono.error(new ExcepcionDao("Al modificar la entidad "
                    + consulta.getNombreEnSingular() + " se debe especificar la clave primaria"));
        }
        for (CampoGes campo : campos) {
            if (!campo.perteneceATabla(tabla)) {
                continue;
            }
            String idCampo = campo.getIdCampo();
            String nombreSqlCampo = campo.getNombreCompleto();

            if (clavePrimaria.contiene(idCampo)) {
                Object valor = clavePrimaria.get(idCampo);
                ejecutor.agregarPk(nombreSqlCampo, valor);
            } else if (campo.isClave()) {
                Object valorClave = clavePrimaria.get(idCampo);
                if (valorClave == null) {
                    return Mono.error(new ExcepcionDao("Al modificar la entidad "
                            + consulta.getNombreEnSingular() + " se debe especificar el campo clave " + idCampo));
                }
                ejecutor.agregarPk(nombreSqlCampo, valorClave);
            }

            if (!entidad.contiene(idCampo)) {
                continue;
            }
            Object valor = entidad.getValor(idCampo);
            ejecutor.agregarCampo(nombreSqlCampo, valor);
        }
        return ejecutor.ejecutar((new ConstructorReactivoEntidadesGes(consulta))).take(1).next();
    }

    /**
     * Borra una entidad.
     */
    public Mono<EntidadGes> borrar(EntidadGes entidad) {
        return borrar(entidad.getClavePrimaria());
    }

    /**
     * Borra una entidad a partir de su clave primaria.
     */
    public Mono<EntidadGes> borrar(ClavePrimaria clavePrimaria) {

        IEjecutorComando ejecutor = crearEjecutorSentenciaDelete();

        if (clavePrimaria == null) {
            return Mono.error(new ExcepcionDao("Al borrar la entidad "
                    + consulta.getNombreEnSingular() + " se debe especificar la clave primaria"));
        }
        ejecutor.setTabla(consulta.getTabla());
        for (String idCampo : clavePrimaria.keySet()) {
            Object valor = clavePrimaria.get(idCampo);
            CampoGes campo = consulta.getCampoPorId(idCampo);
            if (campo == null) {
                return Mono.error(new ExcepcionDao("Al borrar la entidad "
                        + consulta.getNombreEnSingular() + " se debe especificar el campo clave " + idCampo));
            }
            ejecutor.agregarPk(campo.getNombreCompleto(), valor);
        }
        return ejecutor.ejecutar((new ConstructorReactivoEntidadesGes(consulta))).take(1).next();
    }

    public String getSqlConsulta() {
        String sql = consulta.getSql();
        return sustituirSimbolosSql(sql);
    }

    private String sustituirSimbolosSql(String sql) {
        Plantilla plantilla = new Plantilla(sql, formateadorSql);
        plantilla.setSoloSimbolos(true);
        return plantilla.getResultado();
    }

    /**
     * Devuelve todas las entidades
     */
    public Flux<EntidadGes> getEntidades() {
        EjecutorReactivoConsulta ejecutorSelect = crearEjecutorSentenciaSelect();
        Flux<EntidadGes> entidades = null;
        String sql = getSqlConsulta();
        return ejecutorSelect.obtenerEntidades(sql, constructor);
    }

    public Flux<String> getValores(String nombreCampo) {
        EjecutorReactivoConsulta ejecutorSelect = crearEjecutorSentenciaSelect();
        Flux<String> valores = null;
        String sql = getSqlConsulta();
        return ejecutorSelect.obtenerEntidades(sql, new ConstructorReactivoString(nombreCampo));
    }

    /**
     * Devuelve una entidad por su clave primaria
     */
    public Mono<EntidadGes> getEntidad(ClavePrimaria pk) {
        EjecutorReactivoConsulta ejecutorSelect = crearEjecutorSentenciaSelect();
        if (pk == null || consulta == null) {
            return null;
        }
        String strWhere = construirWhere(pk);
        if (StringUtils.isBlank(strWhere)) {
            return null;
        }

        Mono<EntidadGes> entidad = null;
        SentenciaSql sentencia = new SentenciaSql(getSqlConsulta());
        sentencia.where(strWhere);
        String sql = sentencia.getSql();
        entidad = ejecutorSelect.obtenerEntidad(sql, constructor);
        return entidad.map(e -> {
            e.setClavePrimaria(pk);
            return e;
        });
    }

    public SentenciaSql crearSentenciaSql(Filtro filtro) {

        SentenciaSql sentencia = new SentenciaSql(getSqlConsulta());
        if (filtro != null) {
            String filtroSql = filtro.generarSql(formateadorSql, consulta);
            if (filtroSql == null) {
                // TODO: analizar este caso
                //reportarExcepcion(filtro.getMensajeError(), null);
                return null;
            }
            sentencia.where(filtroSql);
        }
        return sentencia;
    }

    public SentenciaSql crearSentenciaSql(Filtro filtro, String campoOrden, boolean ordenDescendente) {

        SentenciaSql sentencia = crearSentenciaSql(filtro);
        if (!Conversion.isBlank(campoOrden)) {
            sentencia.orderBy(campoOrden, ordenDescendente);
        }
        return sentencia;
    }

    private SentenciaSql crearSentenciaSql(Filtro filtro, Orden orden) {

        SentenciaSql sentencia = crearSentenciaSql(filtro);
        if (orden != null) {
            sentencia.orderBy(orden.generarSql(formateadorSql, consulta));
        }

        return sentencia;
    }

    private SentenciaSql crearSentenciaCuentaSql(Filtro filtro) {
        SentenciaSql sentencia = crearSentenciaSql(filtro);
        sentencia.count();
        return sentencia;
    }

    public Mono<Long> getCuenta(Filtro filtro) {
        SentenciaSql sentenciaCuenta = crearSentenciaCuentaSql(filtro);
        EjecutorReactivoConsulta ejecutorSelect = crearEjecutorSentenciaSelect();
        return ejecutorSelect.obtenerEntidad(sentenciaCuenta.getSql(), new ConstructorReactivoLong());
    }

    public Flux<EntidadGes> getEntidades(Filtro filtro, Orden orden, Pagina pagina) {

        EjecutorReactivoConsulta ejecutorSelect = crearEjecutorSentenciaSelect();

        SentenciaSql sentencia = crearSentenciaSql(filtro, orden);
        if (sentencia == null) {
            return null;
        }
        Flux<EntidadGes> entidades;
        Integer indicePrimerElemento = pagina.getIndicePrimerElemento();
        Integer numeroElementos = pagina.getNumeroElementos();
        if (indicePrimerElemento == null || numeroElementos == null) {
            entidades = ejecutorSelect.obtenerEntidades(sentencia.getSql(), constructor);
        } else {
            entidades = ejecutorSelect.obtenerPaginaEntidades(sentencia.getSql(), constructor, indicePrimerElemento, numeroElementos);
        }
        return entidades;
    }

    public Flux<EntidadGes> getEntidades(Filtro filtro, Orden orden) {

        EjecutorReactivoConsulta ejecutorSelect = crearEjecutorSentenciaSelect();

        SentenciaSql sentencia = crearSentenciaSql(filtro, orden);
        if (sentencia == null) {
            return null;
        }
        return ejecutorSelect.obtenerEntidades(sentencia.getSql(), constructor);
    }

    private String construirWhere(ClavePrimaria pk) {

        SentenciaSql sentencia = new SentenciaSql();

        List<CampoGes> campos = consulta.getCampos();
        for (CampoGes campo : campos) {
            String nombreSqlCampo = campo.getNombreCompleto();
            String idCampo = campo.getIdCampo();
            Object valor = pk.get(idCampo);
            if (valor != null) {
                Object valorTipado = ConversionValores.aValorBD(valor, campo);
                String valorSql = formateadorSql.formatear(valorTipado);
                sentencia.where(nombreSqlCampo + " = " + valorSql);
            }
        }
        return sentencia.getWhere();
    }

    /**
     * Asigna a la entidad los valores recuperados de la sentencia de guardado
     *
     * @param entidad        Entidad Ges
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
