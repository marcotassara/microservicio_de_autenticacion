package com.proyecto.autenticacion.security;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

@Configuration
@EnableWebSecurity // Anotación clave para habilitar la seguridad web
@EnableMethodSecurity(prePostEnabled = true) // Para seguridad a nivel de método (ej. @PreAuthorize)
public class SecurityConfig {

    private final JwtAuthenticationFilter jwtAuthFilter;

    public SecurityConfig(JwtAuthenticationFilter jwtAuthFilter) {
        this.jwtAuthFilter = jwtAuthFilter;
    }

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
            // Deshabilitamos CSRF porque usaremos JWT (que es stateless y no vulnerable a este ataque)
            .csrf(csrf -> csrf.disable())
            // Definimos las reglas de autorización de las peticiones HTTP
            .authorizeHttpRequests(auth -> auth
                // Permitimos el acceso sin autenticación a nuestros endpoints de login y registro
                .requestMatchers("/api/v1/auth/**").permitAll()
                .requestMatchers("/api/v1/usuarios/**").permitAll()
                // Cualquier otra petición debe ser autenticada
                .anyRequest().authenticated()
            )
            // Configuramos la gestión de sesiones para que sea STATELESS. No guardaremos estado en el servidor.
            .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
            // Añadimos nuestro filtro personalizado de JWT antes del filtro de autenticación estándar
            .addFilterBefore(jwtAuthFilter, UsernamePasswordAuthenticationFilter.class);
            
        return http.build();
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        // Usamos BCrypt para codificar las contraseñas. Es el estándar de la industria.
        return new BCryptPasswordEncoder();
    }

    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration config) throws Exception {
        // Obtenemos el AuthenticationManager que Spring usará para manejar los intentos de login.
        return config.getAuthenticationManager();
    }
}