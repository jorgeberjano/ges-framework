package es.jbp.ges.userapi.nosql;

import es.jbp.ges.entidad.CampoGes;
import es.jbp.ges.entidad.ConsultaGes;
import lombok.Data;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.util.ArrayList;
import java.util.List;

/**
 * Respresenta el estado que tiene una consulta configurada por un usuario
 * @author Jorge Berjano
 */
@Document(collection = "estados_consultas")
@Data
public class EstadoConsulta {
    @Id
    private String _id;
    
    private String nombreUsuario;
    private String idConsulta;
    private List<CampoGes> campos = new ArrayList<>();
}
