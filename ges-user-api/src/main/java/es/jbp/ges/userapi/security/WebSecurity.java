package es.jbp.ges.userapi.security;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

import java.util.List;

@Configuration
@EnableWebSecurity
public class WebSecurity extends WebSecurityConfigurerAdapter {

    private final UserDetailsService userDetailsService;

    public WebSecurity(UserDetailsService userDetailsService) {
        this.userDetailsService = userDetailsService;
    }

    @Bean
    public PasswordEncoder bCryptPasswordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Override
    protected void configure(HttpSecurity httpSecurity) throws Exception {

        CorsConfigurationSource corsLambda = request -> {
            var cors = new CorsConfiguration();
            //cors.setAllowedOrigins(List.of("http://localhost:4200", "http://127.0.0.1:80", "http://localhost:8180"));
            cors.setAllowedOrigins(List.of("*"));
            cors.setAllowedMethods(List.of("GET", "POST", "PUT", "DELETE", "OPTIONS"));
            cors.setAllowedHeaders(List.of("*"));
            cors.setExposedHeaders(List.of("X-Total-Count"));
            return cors;
        };
        /*
         * 1. Se desactiva el uso de cookies
         * 2. Se activa la configuración CORS con los valores por defecto
         * 3. Se desactiva el filtro CSRF
         * 4. Se indica que el login no requiere autenticación
         * 5. Se indica que el resto de URLs esten securizadas
         */
        httpSecurity
                .sessionManagement().sessionCreationPolicy(SessionCreationPolicy.STATELESS)
                .and()
                .cors().configurationSource(corsLambda)
                .and()
                .csrf().disable()
                .authorizeRequests().antMatchers(HttpMethod.POST, Constants.LOGIN_URL).permitAll()
                .and()
                .authorizeRequests().antMatchers("/actuator/**").permitAll()
                .and()
                .authorizeRequests().anyRequest().authenticated()
                .and()
                .addFilter(new JWTAuthenticationFilter(authenticationManager()))
                .addFilter(new JWTAuthorizationFilter(authenticationManager()));
    }

    @Override
    public void configure(AuthenticationManagerBuilder auth) throws Exception {
        // Se define la clase que recupera los usuarios y el algoritmo para procesar las contraseñas
        auth.userDetailsService(userDetailsService).passwordEncoder(bCryptPasswordEncoder());
    }

    @Bean
    public CorsConfigurationSource corsConfigurationSource() {

        CorsConfiguration config = new CorsConfiguration().applyPermitDefaultValues();
        config.addAllowedHeader("Authorization");
        final UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", config);
        return source;
    }
}
