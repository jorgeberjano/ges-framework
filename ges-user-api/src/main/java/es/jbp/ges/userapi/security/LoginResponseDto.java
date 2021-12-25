package es.jbp.ges.userapi.security;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class LoginResponseDto {
    private String token;
    private String nombreUsuario;
}
