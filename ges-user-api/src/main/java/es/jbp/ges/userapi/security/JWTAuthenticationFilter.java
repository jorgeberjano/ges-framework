package es.jbp.ges.userapi.security;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.gson.Gson;
import es.jbp.ges.userapi.dto.UsuarioLogin;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Date;

public class JWTAuthenticationFilter extends UsernamePasswordAuthenticationFilter {

    private AuthenticationManager authenticationManager;

    public JWTAuthenticationFilter(AuthenticationManager authenticationManager) {
        this.authenticationManager = authenticationManager;
    }

    @Override
    public Authentication attemptAuthentication(HttpServletRequest request, HttpServletResponse response)
            throws AuthenticationException {
        try {
            UsuarioLogin credenciales = new ObjectMapper().readValue(request.getInputStream(), UsuarioLogin.class);

            return authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(
                    credenciales.getNombre(), credenciales.getContrasena(), new ArrayList<>()));
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    protected void successfulAuthentication(HttpServletRequest request, HttpServletResponse response, FilterChain chain,
                                            Authentication auth) throws IOException, ServletException {

        String userName = ((User) auth.getPrincipal()).getUsername();

        String token = Jwts.builder()
                .setIssuedAt(new Date())
                .setIssuer(Constants.ISSUER_INFO)
                .setSubject(userName)
                .setExpiration(new Date(System.currentTimeMillis() + Constants.TOKEN_EXPIRATION_TIME))
                .signWith(SignatureAlgorithm.HS512, Constants.SUPER_SECRET_KEY).compact();

        Gson gson = new Gson();
        String responseBody = gson.toJson(LoginResponseDto.builder()
                .token(token)
                .nombreUsuario(userName)
                .build());
        response.getWriter().write(responseBody);
    }
}
