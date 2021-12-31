package es.jbp.ges.servicio;

import es.jbp.comun.utiles.conversion.Conversion;
import es.jbp.comun.utiles.sql.TipoDato;
import es.jbp.ges.consulta.BuilderConsulta;
import es.jbp.ges.conversion.IConversorValores;
import es.jbp.ges.crud.OperacionCrud;
import es.jbp.ges.entidad.*;
import es.jbp.ges.excepciones.GesBadRequestException;
import es.jbp.ges.excepciones.GesInternalException;
import es.jbp.ges.excepciones.GesNotFoundExcepion;
import es.jbp.ges.exportacion.Exportador;
import es.jbp.ges.exportacion.FactoriaExportadores;
import es.jbp.ges.filtroyorden.*;
import es.jbp.ges.utilidades.ConversionValores;
import es.jbp.ges.utilidades.GestorSimbolos;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.io.OutputStream;
import java.util.List;
import java.util.Map;
import java.util.function.BiFunction;
import java.util.stream.Collectors;

public abstract class ServicioEntidadBase implements IServicioEntidad {

    protected IServicioGes servicioGes;

    protected IServicioJson servicioJson;

    protected String idioma;
    protected ConsultaGes consulta;
    protected IServicioPersonalizado personalizadorEntidades;
    protected IConversorValores conversorValores;
    protected ConstructorFiltro constructorFiltro;

    protected static final String CONSULTA_NO_EXISTE = "La consulta no existe";
    protected static final String ENTIDAD_NO_EXISTE = "La entidad no existe";
    protected static final String CLAVE_NO_VALIDA = "La clave primaria no es válida";

    public ServicioEntidadBase(IServicioGes servicioGes, IServicioJson servicioJson) {
        this.servicioGes = servicioGes;
        this.servicioJson = servicioJson;
    }

    @Override
    public void asignarConsulta(String idioma, String idConsulta) {
        this.idioma = idioma;
        consulta = servicioGes.getConsultaPorId(idioma, idConsulta);
        GestorSimbolos gestorSimbolos = servicioGes.getGestorSimbolos(idioma);
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
    public ClavePrimaria crearClavePrimaria(Map<String, ?> mapa) {
        if (consulta == null) {
            return null;
        }
        return new ClavePrimaria(convertirAValoresBD(mapa));
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

    @Override
    public ClavePrimaria crearClavePrimaria(String... valoresClave) {
        return consulta.construirClavePrimaria(valoresClave);
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

    /**
     * Procesa la consulta de entidades para que actuen los manipuladores.
     */
    protected EntidadGes procesarConsultaEntidad(EntidadGes entidad) throws Exception {
        if (entidad == null) {
            return null;
        }
        consulta.getCampos().stream().forEach(campo -> procesarConsultaValor(entidad, campo));
        return personalizadorEntidades.postOperacion(entidad, OperacionCrud.CONSULTA);
    }

    /**
     * Procesa la consulta de un valor de un campo para que actúe el conversor de valores personalizado
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
     * permitir que actúe el conversor de valores personalizado
     */
    protected void procesarGuardadoValor(EntidadGes entidad, CampoGes campo) {
        if (!entidad.contiene(campo.getIdCampo())) {
            return;
        }
        Object valor = entidad.getValor(campo.getIdCampo());

        if (valor == null) {
            String valorNulo = campo.getValorNulo();
            if (!Conversion.isBlank(valorNulo)) {
                valor = ConversionValores.aValorBD(valorNulo, campo);
            }
        }
        valor = ConversionValores.aValorBD(valor, campo);
        entidad.setValor(campo.getIdCampo(), valor);

        if (conversorValores != null) {
            conversorValores.guardando(entidad, campo);
        }
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
            throw new GesBadRequestException("No se puede ordenar por " +
                    Conversion.convertirListaEnTexto(listaCamposInexistentes, ", "));
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

    public ExpresionFiltro crearFiltro(Map<String, String> parametros) {
        return constructorFiltro.crearFiltro(parametros);
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

    protected void comprobarEntidad(EntidadGes entidad) throws GesNotFoundExcepion, GesBadRequestException {
        if (entidad == null) {
            throw new GesNotFoundExcepion(ENTIDAD_NO_EXISTE);
        }
        for (CampoGes campo : consulta.getCampos()) {
            Object valor = entidad.getValor(campo.getIdCampo());
            comprobarRequerido(campo, valor);
            comprobarCadena(campo, valor);
        }
    }

    protected void comprobarRequerido(CampoGes campo, Object valor) throws GesBadRequestException {
        if (campo.isRequerido() && valor == null) {
            throw new GesBadRequestException("El campo " + campo.getTitulo() + " debe tener un valor");
        }
    }

    protected void comprobarCadena(CampoGes campo, Object valor) throws GesBadRequestException {
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
    public void exportar(OutputStream out, String formato, Map<String, String> parametros) {
        Exportador exportador = FactoriaExportadores.crearExportador(formato);
        if (exportador == null) {
            throw new GesBadRequestException("El formato de exportación " + formato + " no está soportado");
        }
        Flux<EntidadGes> entidades = getEntidades(parametros);

        try {
            exportador.generar(out, consulta, entidades);
        } catch (Exception e) {
            throw new GesInternalException(e.getMessage());
        }
    }

    public BuilderConsulta builder() {
        return new BuilderConsulta(consulta, conversorValores);
    }

    @Override
    public Mono<EntidadGes> getEntidad(String id) throws GesBadRequestException {
        return getEntidad(crearClavePrimaria(id));
    }

    @Override
    public Flux<EntidadGes> getEntidades(Map<String, String> parametros) throws GesBadRequestException {
        ExpresionFiltro filtro = crearFiltro(parametros);
        ExpresionOrden orden = crearOrden(parametros);
        return getEntidades(filtro, orden);
    }

    @Override
    public PaginaRxEntidades<EntidadGes> getPaginaEntidades(Map<String, String> parametros) throws GesBadRequestException {
        ExpresionFiltro filtro = crearFiltro(parametros);
        ExpresionOrden orden = crearOrden(parametros);
        ExpresionPagina pagina = crearPagina(parametros);

        return getPaginaEntidades(filtro, orden, pagina);
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
}
