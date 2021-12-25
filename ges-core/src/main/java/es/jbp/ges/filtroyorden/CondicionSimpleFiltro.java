package es.jbp.ges.filtroyorden;

import es.jbp.ges.conversion.IConversorValores;
import es.jbp.ges.entidad.CampoGes;
import es.jbp.ges.entidad.ConsultaGes;
import es.jbp.ges.utilidades.ConversionValores;
import es.jbp.comun.utiles.conversion.Conversion;
import es.jbp.comun.utiles.sql.compatibilidad.FormateadorSql;

/**
 * Representa una condición simple en un filtro.
 *
 * @author jorge
 */
public class CondicionSimpleFiltro implements CondicionFiltro {

    private final CampoGes campo;
    private final OperadorFiltro operador;
    private final Object valor;    
    private String mensajeError;

    public CondicionSimpleFiltro(CampoGes campo, OperadorFiltro operador, Object valor) {
        this.campo = campo;
        this.operador = operador;
        this.valor = valor;
    }

    /**
     * Genera la condición en el lenguaje SQL.
     *
     * @param formateadorSql
     * @param consulta
     * @param manipuladorValores
     * @return
     */
    public String generarSql(FormateadorSql formateadorSql, ConsultaGes consulta, IConversorValores manipuladorValores) {

        if (campo == null || operador == null || valor == null) {
            mensajeError = "La condición de filtro está incompleta";
            return null;
        }

        String campoSql;
        if (!Conversion.isBlank(campo.getTabla())) {
            campoSql = new StringBuilder()
                    .append(campo.getTabla())
                    .append('.')
                    .append(campo.getNombre()).toString();
        } else {
            campoSql = campo.getNombre();
        }

        if (operador.equals(OperadorFiltro.IS)) {
            return generarSqlOperadorIs(campoSql, formateadorSql, manipuladorValores);
        }
        
        Object valorTipado = ConversionValores.aValorBD(valor, campo);
        valorTipado = manipuladorValores.aValorBd(valorTipado, campo);               
        if (valorTipado == null) {
            mensajeError = "Valor incorrecto '" + valor + "' para el atributo " + campo.getIdCampo();
            return null;
        }
        
        if (operador.equals(OperadorFiltro.LIKE)) {
            return generarSqlOperadorLike(campoSql, valorTipado, formateadorSql);
        }
        
        String operadorSql;
        if (operador.equals(OperadorFiltro.NEQ)) {
            operadorSql = formateadorSql.getOperadorDistinto();
        } else {
            operadorSql = operador.getSimbolo();
        }
        String valorSql = formateadorSql.formatear(valorTipado);
        return generarSqlCampoOperadorValor(campoSql, operadorSql, valorSql);
    }

    private String generarSqlOperadorIs(String campoSql, FormateadorSql formateadorSql, IConversorValores manipuladorValores) {

        Object valorNull = manipuladorValores.aValorBd(null, campo);
        String valorMinusculas = valor.toString().toLowerCase().trim();
        boolean esNull = valorMinusculas.equals("null");
        boolean esNotNull = valorMinusculas.equals("not null");
        if (!esNull && !esNotNull) {
            mensajeError = "El operador 'is' solo es aplicable con los valores 'null' o 'not null'";
            return null;
        }
        String valorSql;
        String operadorSql = operador.getSimbolo();
        if (valorNull != null) {
            if (esNull) {
                operadorSql = OperadorFiltro.EQ.getSimbolo();
            } else {
                operadorSql = OperadorFiltro.NEQ.getSimbolo();
            }
            valorSql = formateadorSql.formatear(valorNull);
        } else {
            valorSql = valorMinusculas;
        }

        return generarSqlCampoOperadorValor(campoSql, operadorSql, valorSql);
    }

    private String generarSqlOperadorLike(String campoSql, Object valorObj, FormateadorSql formateadorSql) {
        if (!(valorObj instanceof String)) {
            mensajeError = "El operador like no se puede aplicar al campo " + campoSql;
            return null;
        }

        String valorContieneTexto = valorObj.toString();
        String comodin = formateadorSql.getCaracterComodin();
        if (valorContieneTexto.contains("*")) {
            if (!"*".equals(comodin)) {
                valorContieneTexto = valorContieneTexto.replace("*", comodin);
            }
        } else {
            valorContieneTexto = formateadorSql.getContieneTexto(valorContieneTexto);
        }
        String valorSql = formateadorSql.formatear(valorContieneTexto);
        return formateadorSql.getComparacionNoCase(campoSql, operador.getSimbolo(), valorSql);
    }

    private String generarSqlCampoOperadorValor(String campoSql, String operadorSql, String valorSql) {
        return new StringBuilder().append(campoSql).append(' ').append(operadorSql)
                .append(' ').append(valorSql).toString();
    }

    @Override
    public String getMensajeError() {
        return mensajeError;
    }

}
