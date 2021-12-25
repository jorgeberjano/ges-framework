package es.jbp.comun.ges.exportacion;

import es.jbp.comun.ges.entidad.EntidadGes;
import es.jbp.comun.ges.entidad.CampoGes;
import es.jbp.comun.ges.entidad.ConsultaGes;
import es.jbp.comun.ges.utilidades.ConversionValores;
import es.jbp.comun.utiles.conversion.Conversion;
import es.jbp.comun.utiles.sql.TipoDato;
import java.io.OutputStream;

import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import reactor.core.publisher.Flux;

/**
 * Genera exportacion en formato Excel a partir de los datos de sesión de una tabla.
 *
 * @author jberjano
 */
public class ExportadorExcel implements Exportador {

    /**
     * Crea un informe a partir de los datos de sesión de una tabla
     */
    public ExportadorExcel() {
    }

    /**
     * Genera la exportación
     */
    public void generar(OutputStream outputStream, ConsultaGes consulta, Flux<EntidadGes> entidades) throws Exception {
        
        XSSFWorkbook workbook = new XSSFWorkbook();
        XSSFSheet sheet = workbook.createSheet(consulta.getNombreEnPlural());
       
        int rowNum = 0;
        Row headerRow = sheet.createRow(rowNum++);
        int colNum = 0;
        for (CampoGes campo : consulta.getCampos()) {
            if (campo.isOculto()) {
                continue;
            }
            Cell cell = headerRow.createCell(colNum++);
            cell.setCellValue(campo.getTitulo());
        }
        
        for (EntidadGes entidad : entidades.toIterable()) {
            Row row = sheet.createRow(rowNum++);
            colNum = 0;
            for (CampoGes campo : consulta.getCampos()) {
                if (campo.isOculto()) {
                    continue;
                }            
                Object valor = entidad.getValor(campo.getIdCampo());
                String texto = ConversionValores.aValorUI(valor, campo);
                
                Cell cell = row.createCell(colNum++);
                if (campo.getTipoDato() == TipoDato.REAL) {
                    cell.setCellValue(Conversion.toDouble(valor));                    
                } else {
                    cell.setCellValue(texto);    
                }
            }
        }       
       
        workbook.write(outputStream);
        workbook.close();
    }

}
