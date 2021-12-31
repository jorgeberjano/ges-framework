package es.jbp.ges.rxdao.servicio;

import es.jbp.ges.crud.OperacionCrud;
import es.jbp.ges.entidad.*;
import es.jbp.ges.excepciones.GesBadRequestException;
import es.jbp.ges.excepciones.GesNotFoundExcepion;
import es.jbp.ges.filtroyorden.ExpresionConsulta;
import es.jbp.ges.filtroyorden.ExpresionFiltro;
import es.jbp.ges.filtroyorden.ExpresionOrden;
import es.jbp.ges.filtroyorden.ExpresionPagina;
import es.jbp.ges.rxdao.AccesoReactivoEntidadesGes;
import es.jbp.ges.rxdao.conexion.GestorConexionesReactivas;
import es.jbp.ges.rxdao.conexion.ServicioGestorConexionesReactivas;
import es.jbp.ges.servicio.IServicioGes;
import es.jbp.ges.servicio.IServicioJson;
import es.jbp.ges.servicio.ServicioEntidadBase;
import es.jbp.ges.utilidades.ConversionEntidades;
import es.jbp.ges.utilidades.ConversionValores;
import es.jbp.ges.utilidades.GestorSimbolos;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.Map;

/**
 * Servicio para acceder a entidades genéricas GES.
 *
 * @author Jorge Berjano
 */
@Service
@Scope("prototype")
public class ServicioEntidad extends ServicioEntidadBase {

    private final ServicioGestorConexionesReactivas servicioConexiones;
    private AccesoReactivoEntidadesGes accesoEntidadesGes;

    public ServicioEntidad(IServicioGes servicioGes,
                           IServicioJson servicioJson,
                           ServicioGestorConexionesReactivas servicioConexiones) {
        super(servicioGes, servicioJson);
        this.servicioConexiones = servicioConexiones;
    }

    @Override
    public void asignarConsulta(String idioma, String idConsulta) {
        super.asignarConsulta(idioma, idConsulta);
        GestorConexionesReactivas gestorConexiones = servicioConexiones.getGestorConexiones();
        GestorSimbolos gestorSimbolos = servicioGes.getGestorSimbolos(idioma);
        accesoEntidadesGes = new AccesoReactivoEntidadesGes(consulta, gestorConexiones, gestorSimbolos);
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
        return decorarRequerido(entidad).map(this::procesarConsultaEntidad)
                .onErrorResume(Exception.class, ex -> {
                    return Mono.error(new Exception("error with the JSON. " + ex));
                });
    }


    private Mono<EntidadGes> decorarRequerido(Mono<EntidadGes> entidad) {
        return entidad.switchIfEmpty(Mono.error(new GesNotFoundExcepion(ENTIDAD_NO_EXISTE)));
    }

    @Override
    public Mono<EntidadGes> getEntidad(String id) throws GesBadRequestException {
        return getEntidad(crearClavePrimaria(id));
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
    public EntidadGes procesarConsultaEntidad(EntidadGes entidad) throws RuntimeException {
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
    private EntidadGes procesarGuardadoEntidad(EntidadGes entidad, OperacionCrud operacion) {
        consulta.getCampos().stream().forEach(campo -> procesarGuardadoValor(entidad, campo, operacion));

        try {
            personalizadorEntidades.validar(entidad, operacion);
        } catch (Exception exception) {
            throw new GesBadRequestException(exception.getMessage());
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
        valor = ConversionValores.aValorJson(valor, campo);
        entidad.setValor(campo.getIdCampo(), valor);

        if (conversorValores != null) {
            conversorValores.consultando(entidad, campo);
        }
    }

    /**
     * Procesa el guardado de un valor de un campo para convertir el valor y
     * permitir que actue el manipulador
     */
    private void procesarGuardadoValor(EntidadGes entidad, CampoGes campo, OperacionCrud operacion) {
        if (!entidad.contiene(campo.getIdCampo())) {
            return;
        }
        Object valor = entidad.getValor(campo.getIdCampo());

        if (valor == null && operacion == OperacionCrud.INSERCCION) {
            valor = campo.getValorNulo();
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
    public Mono<EntidadGes> modificarEntidad(EntidadGes entidad) {
        entidad = procesarGuardadoEntidad(entidad, OperacionCrud.MODIFICACION);
        comprobarEntidad(entidad);

        return accesoEntidadesGes.modificar(entidad)
                .map(e -> personalizadorEntidades.postOperacion(e, OperacionCrud.INSERCCION));

//        TODO: Se recupera para que también se devuelvan los campos relacionados
//        entidad = accesoEntidadesGes.getEntidad(entidad.getClavePrimaria());
    }

    @Override
    public Mono<EntidadGes> modificarEntidadJson(String json) {
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

    /**
     * Convierte un mapa de valores con el tipo adecuado para ser devuelto al front-end
     * convertidos según la configuración de cada campo.
     */
    @Override
    public MapaValores convertirAValoresTexto(Map<String, ? extends Object> mapaOriginal) {
        return convertirValores(mapaOriginal, ConversionValores::aValorTexto);
    }

    /**
     * Convierte un mapa de valores con el tipo adecuado para ser devuelto a un cliente de la API
     * convertidos según la configuración de cada campo.
     */
    @Override
    public MapaValores convertirAValoresJson(Map<String, ? extends Object> mapaOriginal) {
        return convertirValores(mapaOriginal, ConversionValores::aValorJson);
    }

    /**
     * Convierte un mapa de valores con el tipo adecuado para ser persistido en base de datos
     * convertidos según la configuración de cada campo.
     */
    @Override
    public MapaValores convertirAValoresBD(Map<String, ? extends Object> mapaOriginal) {
        return convertirValores(mapaOriginal, ConversionValores::aValorBD);
    }

}
