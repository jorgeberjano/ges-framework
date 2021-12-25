package es.jbp.comun.ges.rxsrv;

import es.jbp.comun.ges.consulta.BuilderConsulta;
import es.jbp.comun.ges.conversion.IConversorValores;
import es.jbp.comun.ges.crud.OperacionCrud;
import es.jbp.comun.ges.entidad.*;
import es.jbp.comun.ges.excepciones.GesBadRequestException;
import es.jbp.comun.ges.excepciones.GesNotFoundExcepion;
import es.jbp.comun.ges.exportacion.Exportador;
import es.jbp.comun.ges.exportacion.FactoriaExportadores;
import es.jbp.comun.ges.filtroyorden.*;
import es.jbp.comun.ges.rxdao.AccesoReactivoEntidadesGes;
import es.jbp.comun.ges.rxdao.conexion.GestorConexionesReactivas;
import es.jbp.comun.ges.rxdao.conexion.ServicioGestorConexionesReactivas;
import es.jbp.comun.ges.servicio.IServicioEntidad;
import es.jbp.comun.ges.servicio.IServicioGes;
import es.jbp.comun.ges.servicio.IServicioJson;
import es.jbp.comun.ges.servicio.IServicioPersonalizado;
import es.jbp.comun.ges.utilidades.ConversionEntidades;
import es.jbp.comun.ges.utilidades.ConversionValores;
import es.jbp.comun.ges.utilidades.GestorSimbolos;
import es.jbp.comun.utiles.conversion.Conversion;
import es.jbp.comun.utiles.sql.TipoDato;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.io.OutputStream;
import java.util.List;
import java.util.Map;
import java.util.function.BiFunction;
import java.util.stream.Collectors;

/**
 * Servicio para acceder a entidades genéricas GES.
 *
 * @author Jorge Berjano
 */
@Service
@Scope("prototype")
public class ServicioEntidad implements IServicioEntidad {

    private String idioma;
    private ConsultaGes consulta;
    private AccesoReactivoEntidadesGes accesoEntidadesGes;
    private IServicioPersonalizado personalizadorEntidades;
    private IConversorValores conversorValores;
    private ConstructorFiltro constructorFiltro;

    private static final String CONSULTA_NO_EXISTE = "El recurso no existe";
    private static final String ENTIDAD_NO_EXISTE = "La entidad no existe";
    private static final String CLAVE_NO_VALIDA = "La clave primaria no es válida";

    private final  IServicioGes servicioGes;

    private final IServicioJson servicioJson;

    private final ServicioGestorConexionesReactivas servicioConexiones;

    public ServicioEntidad(IServicioGes servicioGes, IServicioJson servicioJson, ServicioGestorConexionesReactivas servicioConexiones) {
        this.servicioGes = servicioGes;
        this.servicioJson = servicioJson;
        this.servicioConexiones = servicioConexiones;
    }

    @Override
    public void asignarConsulta(String idioma, String idConsulta) {
        this.idioma = idioma;
        consulta = servicioGes.getConsultaPorId(idioma, idConsulta);
        GestorConexionesReactivas gestorConexiones = servicioConexiones.getGestorConexiones();
        GestorSimbolos gestorSimbolos = servicioGes.getGestorSimbolos(idioma);
        accesoEntidadesGes = new AccesoReactivoEntidadesGes(consulta, gestorConexiones, gestorSimbolos);
        personalizadorEntidades = servicioGes.obtenerServicioPersonalizado(idConsulta);
        personalizadorEntidades.setServicioEntidad(this);
        conversorValores = servicioGes.obtenerConversorValores();

        constructorFiltro = new ConstructorFiltro(consulta, conversorValores);
    }

    @Override
    public ConsultaGes getConsulta() {
        return consulta;
    }

    public String getIdioma() {
        return idioma;
    }

    public IConversorValores getConversorValores() {
        return conversorValores;
    }


    @Override
    public ClavePrimaria crearClavePrimaria(Map<String, ? extends Object> clavePrimaria) throws GesBadRequestException {
        if (consulta == null) {
            throw new GesBadRequestException(CONSULTA_NO_EXISTE);
        }
        return new ClavePrimaria(convertirAValoresBD(clavePrimaria));
    }

    @Override
    public ClavePrimaria crearClavePrimaria(String... valoresClave) throws GesBadRequestException {
        if (consulta == null) {
            throw new GesBadRequestException(CONSULTA_NO_EXISTE);
        }
        return consulta.construirClavePrimaria(valoresClave);
    }

    @Override
    public Mono<EntidadGes> getEntidad(ClavePrimaria clavePrimaria) throws GesBadRequestException {
        if (consulta == null) {
            throw new GesBadRequestException(CONSULTA_NO_EXISTE);
        }
        if (clavePrimaria == null || clavePrimaria.isEmpty()) {
            throw new GesBadRequestException(CLAVE_NO_VALIDA);
        }
        Mono<EntidadGes> entidad = accesoEntidadesGes.getEntidad(clavePrimaria);
        return decorarRequerido(entidad.map(this::procesarConsultaEntidad));
    }

    private Mono<EntidadGes> decorarRequerido(Mono<EntidadGes> entidad) {
        return entidad.switchIfEmpty(Mono.error(new GesNotFoundExcepion(ENTIDAD_NO_EXISTE)));
    }

    @Override
    public Mono<EntidadGes> getEntidad(String id) throws GesBadRequestException {
        return getEntidad(crearClavePrimaria(id));
    }

    @Override
    public PaginaRxEntidades<EntidadGes> getPaginaEntidades(Map<String, String> parametros) throws GesBadRequestException {
        ExpresionFiltro filtro = crearFiltro(parametros);
        ExpresionOrden orden = crearOrden(parametros);
        ExpresionPagina pagina = crearPagina(parametros);

        return getPaginaEntidades(filtro, orden, pagina);
    }

    @Override
    public PaginaRxEntidades<EntidadGes> getPaginaEntidades(ExpresionFiltro filtro, ExpresionOrden orden, ExpresionPagina pagina) throws GesBadRequestException {
        if (consulta == null) {
            throw new GesBadRequestException(CONSULTA_NO_EXISTE);
        }

        var cuenta = accesoEntidadesGes.getCuenta(filtro);
        Flux<EntidadGes> entidades = accesoEntidadesGes.getEntidades(filtro, orden, pagina);
        return PaginaRxEntidades.<EntidadGes>builder()
                .numeroTotalEntidades(cuenta)
                .entidades(entidades.map(this::procesarConsultaEntidad))
                .build();
    }

    public PaginaRxEntidades<EntidadGes> getPaginaEntidades(ExpresionConsulta expresionConsulta) throws GesBadRequestException {
        return getPaginaEntidades(expresionConsulta.getExpresionFiltro(), expresionConsulta.getExpresionOrden(), expresionConsulta.getExpresionPagina());
    }

    public <T> PaginaRxEntidades<T> getPagina(ExpresionConsulta expresionConsulta, Class<T> clazz) throws Exception {
        PaginaRxEntidades<EntidadGes> paginaEntidadesGes = getPaginaEntidades(expresionConsulta);

        var entidades = paginaEntidadesGes.getEntidades()
                .map(e -> convertirAEntidad(e, clazz));

        return PaginaRxEntidades.<T>builder()
                .entidades(entidades)
                .build();
    }

    public <T> T convertirAEntidad(EntidadGes entidadGes, Class<T> clazz) {
        T entidad = null;
        try {
            entidad = clazz.getDeclaredConstructor().newInstance();
        } catch (Exception e) {
            return null;
        }
        ConversionEntidades.deEntidadGesAEntidadObjeto(entidadGes, entidad);
        return entidad;
    }

    @Override
    public Flux<EntidadGes> getEntidades(Map<String, String> parametros) throws GesBadRequestException {
        ExpresionFiltro filtro = crearFiltro(parametros);
        ExpresionOrden orden = crearOrden(parametros);
        return getEntidades(filtro, orden);
    }

    @Override
    public Flux<EntidadGes> getEntidades(ExpresionFiltro filtro, ExpresionOrden orden) throws GesBadRequestException {
        if (consulta == null) {
            throw new GesBadRequestException(CONSULTA_NO_EXISTE);
        }

        Flux<EntidadGes> entidades = accesoEntidadesGes.getEntidades(filtro, orden);
        return entidades;
    }

    @Override
    public Flux<EntidadGes> getEntidades() throws GesBadRequestException {
        if (consulta == null) {
            throw new GesBadRequestException(CONSULTA_NO_EXISTE);
        }
        return accesoEntidadesGes.getEntidades();
    }

    /**
     * Procesa la consulta de entidades para que actúen los manipuladores.
     *
     * @param entidad
     * @return
     */
    public EntidadGes procesarConsultaEntidad(EntidadGes entidad) {
        if (entidad == null) {
            return null;
        }
        consulta.getCampos().stream().forEach(campo -> procesarConsultaValor(entidad, campo));
        return personalizadorEntidades.postOperacion(entidad, OperacionCrud.CONSULTA);
    }

    /**
     * Procesa el guardado de entidades para que actúen los servicios personalizados.
     *
     * @param entidad
     * @return
     */
    private EntidadGes procesarGuardadoEntidad(EntidadGes entidad, OperacionCrud operacion) throws GesBadRequestException {
        consulta.getCampos().stream().forEach(campo -> procesarGuardadoValor(entidad, campo));

        boolean ok = personalizadorEntidades.validar(entidad, operacion);
        if (!ok) {
            throw new GesBadRequestException(personalizadorEntidades.getMensajeError());
        }

        return personalizadorEntidades.preOperacion(entidad, operacion);
    }

    /**
     * Procesa la consulta de un valor de un campo para que actue el manipulador
     *
     * @param entidad
     * @return
     */
    private void procesarConsultaValor(EntidadGes entidad, CampoGes campo) {
        Object valor = entidad.getValor(campo.getIdCampo());
        valor = ConversionValores.aValorAPI(valor, campo);
        entidad.setValor(campo.getIdCampo(), valor);

        if (conversorValores != null) {
            conversorValores.consultando(entidad, campo);
        }
    }

    /**
     * Procesa el guardado de un valor de un campo para convertir el valor y
     * permitir que actue el manipulador
     */
    private void procesarGuardadoValor(EntidadGes entidad, CampoGes campo) {
        if (!entidad.contiene(campo.getIdCampo())) {
            return;
        }
        Object valor = entidad.getValor(campo.getIdCampo());

        if (valor == null) {
            String valorNulo = campo.getValorNulo();
            if (!Conversion.isBlank(valorNulo)) {
                valor = Conversion.convertirValor(valorNulo, campo.getTipoDato());
            }
        }
        valor = ConversionValores.aValorBD(valor, campo);
        entidad.setValor(campo.getIdCampo(), valor);

        if (conversorValores != null) {
            conversorValores.guardando(entidad, campo);
        }
    }

    @Override
    public Mono<EntidadGes> insertarEntidad(EntidadGes entidad) throws GesBadRequestException, GesNotFoundExcepion {
        entidad = procesarGuardadoEntidad(entidad, OperacionCrud.INSERCCION);
        comprobarEntidad(entidad);
        return accesoEntidadesGes.insertar(entidad);
    }

    @Override
    public Mono<EntidadGes> insertarEntidadJson(String json) throws GesBadRequestException, GesNotFoundExcepion {
        if (consulta == null) {
            throw new GesBadRequestException(CONSULTA_NO_EXISTE);
        }
        EntidadGes entidad = servicioJson.toEntidad(json, consulta);
        return insertarEntidad(entidad)
                .map(e -> personalizadorEntidades.postOperacion(e, OperacionCrud.INSERCCION));
    }

    @Override
    public Mono<EntidadGes> modificarEntidad(EntidadGes entidad) throws GesBadRequestException, GesNotFoundExcepion {
        entidad = procesarGuardadoEntidad(entidad, OperacionCrud.MODIFICACION);
        comprobarEntidad(entidad);

        return accesoEntidadesGes.modificar(entidad)
                .map(e -> personalizadorEntidades.postOperacion(e, OperacionCrud.INSERCCION));

//        TODO: Se recupera para que también se devuelvan los campos relacionados
//        entidad = accesoEntidadesGes.getEntidad(entidad.getClavePrimaria());
    }

    @Override
    public Mono<EntidadGes> modificarEntidadJson(String json) throws GesBadRequestException, GesNotFoundExcepion {
        if (consulta == null) {
            throw new GesBadRequestException(CONSULTA_NO_EXISTE);
        }

        EntidadGes entidad = servicioJson.toEntidad(json, consulta);

        return modificarEntidad(entidad);
    }

    @Override
    public Mono<EntidadGes> modificarEntidadJson(ClavePrimaria clave, String json) throws GesBadRequestException, GesNotFoundExcepion {
        if (consulta == null) {
            throw new GesBadRequestException(CONSULTA_NO_EXISTE);
        }

        EntidadGes entidad = servicioJson.toEntidad(json, consulta);

        return modificarEntidad(entidad);
    }

    @Override
    public Mono<EntidadGes> modificarEntidadJson(String id, String json) throws GesBadRequestException, GesNotFoundExcepion {
        if (consulta == null) {
            throw new GesBadRequestException(CONSULTA_NO_EXISTE);
        }
        ClavePrimaria clave = consulta.construirClavePrimariaDeId(id);
        return modificarEntidadJson(clave, json);
    }

    @Override
    public Mono<EntidadGes> borrarEntidad(ClavePrimaria clave) throws GesBadRequestException {
        if (consulta == null) {
            throw new GesBadRequestException(CONSULTA_NO_EXISTE);
        }

        EntidadGes entidad = new EntidadGes();
        entidad.setClavePrimaria(clave);
        personalizadorEntidades.preOperacion(entidad, OperacionCrud.BORRADO);

        return accesoEntidadesGes.borrar(clave)
                .map(e -> personalizadorEntidades.postOperacion(e, OperacionCrud.BORRADO));

    }

    @Override
    public Mono<EntidadGes> borrarEntidadPorId(String id) throws GesBadRequestException, GesNotFoundExcepion {
        if (consulta == null) {
            throw new GesBadRequestException(CONSULTA_NO_EXISTE);
        }
        ClavePrimaria clave = consulta.construirClavePrimariaDeId(id);
        if (clave == null) {
            throw new GesNotFoundExcepion(CLAVE_NO_VALIDA);
        }
        return borrarEntidad(clave);
    }

    public BuilderConsulta builder() {
        return new BuilderConsulta(consulta, conversorValores);
    }

    /**
     * Convierte un mapa un mapa de valores con el tipo adecuado para ser devuelto al front-end
     * convertidos según la configuración de cada campo.
     */
    @Override
    public MapaValores convertirAValoresUI(Map<String, ? extends Object> mapaOriginal) {
        return convertirValores(mapaOriginal, ConversionValores::aValorUI);
    }

    /**
     * Convierte un mapa un mapa de valores con el tipo adecuado para ser persistido en base de datos
     * convertidos según la configuración de cada campo.
     */
    @Override
    public MapaValores convertirAValoresBD(Map<String, ? extends Object> mapaOriginal) {
        return convertirValores(mapaOriginal, ConversionValores::aValorBD);
    }

    public MapaValores convertirValores(Map<String, ? extends Object> mapaOriginal, BiFunction<Object, CampoGes, Object> func) {
        MapaValores mapaValores = new MapaValores();
        for (String idCampo : mapaOriginal.keySet()) {
            Object valorOriginal = mapaOriginal.get(idCampo);
            CampoGes campo = consulta.getCampoPorId(idCampo);
            if (campo == null) {
                continue;
            }
            Object valor = func.apply(valorOriginal, campo);
            mapaValores.put(idCampo, valor);
        }
        return mapaValores;
    }

    public ExpresionFiltro crearFiltro(Map<String, String> parametros) {
        return constructorFiltro.crearFiltro(parametros);
    }

    public ExpresionOrden crearOrden(Map<String, String> parametros) throws GesBadRequestException {
        ExpresionOrden expresion = new ExpresionOrden();
        if (parametros == null) {
            return expresion;
        }
        String sort = parametros.get("_sort");
        String order = parametros.get("_order");

        List<String> listaSort = Conversion.convertirTextoEnLista(sort);
        List<String> listaOrder = Conversion.convertirTextoEnLista(order);

        List<String> listaCamposInexistentes = listaSort.stream()
                .filter(idCampo -> consulta.getCampoPorId(idCampo) == null)
                .collect(Collectors.toList());

        if (!listaCamposInexistentes.isEmpty()) {
            throw new GesBadRequestException("No se puede ordenar por "
                    + Conversion.convertirListaEnTexto(listaCamposInexistentes, ", "));
        }

        List<CampoGes> campos = listaSort.stream()
                .map(e -> consulta.getCampoPorId(e))
                .filter(c -> c != null)
                .collect(Collectors.toList());
        List<Boolean> descendentes = listaOrder.stream()
                .map(e -> "desc".equals(e.toLowerCase()))
                .collect(Collectors.toList());

        for (int i = 0; i < campos.size(); i++) {
            CampoGes campo = campos.get(i);
            boolean descendente = false;
            if (i < descendentes.size()) {
                descendente = Boolean.TRUE.equals(descendentes.get(i));
            }
            expresion.agregarCondicion(new CondicionOrden(campo, descendente));
        }
        return expresion;
    }

    public ExpresionPagina crearPagina(Map<String, String> parametros) {
        ExpresionPagina pagina = new ExpresionPagina();
        if (parametros == null) {
            return pagina;
        }
        pagina.setPagina(Conversion.toInteger(parametros.get("_page")));
        pagina.setLimite(Conversion.toInteger(parametros.get("_limit")));
        pagina.setPrimero(Conversion.toInteger(parametros.get("_start")));
        pagina.setUltimo(Conversion.toInteger(parametros.get("_end")));
        return pagina;
    }

    private void comprobarEntidad(EntidadGes entidad) throws GesNotFoundExcepion, GesBadRequestException {
        if (entidad == null) {
            throw new GesNotFoundExcepion(ENTIDAD_NO_EXISTE);
        }
        for (CampoGes campo : consulta.getCampos()) {
            Object valor = entidad.getValor(campo.getIdCampo());
            comprobarRequerido(campo, valor);
            comprobarCadena(campo, valor);
        }
    }

    private void comprobarRequerido(CampoGes campo, Object valor) throws GesBadRequestException {
        if (campo.isRequerido() && valor == null) {
            throw new GesBadRequestException("El campo " + campo.getTitulo() + " debe tener un valor");
        }
    }

    private void comprobarCadena(CampoGes campo, Object valor) throws GesBadRequestException {
        if (campo.getTipoDato() != TipoDato.CADENA) {
            return;
        }
        String valorString = Conversion.toString(valor);
        boolean vacia = Conversion.isBlank(valorString);
        if (campo.isRequerido() && vacia) {
            throw new GesBadRequestException("El valor del campo " + campo.getTitulo() + " no puede estar vacío");
        } else if (!vacia && campo.getTamano() > 0 && valorString.length() > campo.getTamano()) {
            throw new GesBadRequestException("El valor del campo " + campo.getTitulo() + " excede su tamaño máximio de " + campo.getTamano() + " caracteres");
        }
    }

    @Override
    public void exportar(OutputStream out, String formato, Map<String, String> parametros) throws GesBadRequestException {
        Exportador exportador = FactoriaExportadores.crearExportador(formato);
        if (exportador == null) {
            throw new GesBadRequestException("En formato de exportación " + formato + " no está soportado");
        }

        Flux<EntidadGes> entidades = getEntidades(parametros);

        try {
            exportador.generar(out, consulta, entidades);
        } catch (Exception ex) {
            String mensajeError = "No se ha podido exportar a " + formato;
            throw new InternalError(mensajeError, ex);
        }
    }

}
