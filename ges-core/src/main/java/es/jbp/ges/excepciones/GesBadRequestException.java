package es.jbp.ges.excepciones;

public class GesBadRequestException extends RuntimeException {

    public GesBadRequestException(String message) {
        super(message);
    }
}
