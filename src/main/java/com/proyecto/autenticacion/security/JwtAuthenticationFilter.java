package com.proyecto.autenticacion.security;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.lang.NonNull;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;
import java.io.IOException;

@Component
@RequiredArgsConstructor // Magia de Lombok para un constructor limpio y final
public class JwtAuthenticationFilter extends OncePerRequestFilter {

    private final JwtUtils jwtUtils; // Nuestro poeta de tokens
    private final UserDetailsService userDetailsService; // El que conoce a nuestros usuarios

    @Override
    protected void doFilterInternal(
            @NonNull HttpServletRequest request,
            @NonNull HttpServletResponse response,
            @NonNull FilterChain filterChain
    ) throws ServletException, IOException {

        final String authHeader = request.getHeader("Authorization");
        final String jwt;
        final String userEmail;

        // Si la petición no lleva el pasaporte (token) o no está en el formato correcto,
        // simplemente la dejamos pasar. Otros filtros de seguridad se encargarán de ella.
        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            filterChain.doFilter(request, response);
            return;
        }

        // Extraemos el token, el alma del pasaporte.
        jwt = authHeader.substring(7);
        userEmail = jwtUtils.getEmailFromToken(jwt);

        // Si tenemos el email y el usuario aún no ha sido autenticado en esta petición...
        if (userEmail != null && SecurityContextHolder.getContext().getAuthentication() == null) {
            // Buscamos los detalles del usuario en nuestra base de datos.
            UserDetails userDetails = this.userDetailsService.loadUserByUsername(userEmail);

            // ¡Aquí ocurre la magia! Validamos el token contra los detalles del usuario.
            if (jwtUtils.validateToken(jwt, userDetails)) {
                // Si el token es válido, creamos una "sesión" de autenticación para esta petición.
                UsernamePasswordAuthenticationToken authToken = new UsernamePasswordAuthenticationToken(
                        userDetails,
                        null, // No necesitamos credenciales aquí
                        userDetails.getAuthorities()
                );
                
                authToken.setDetails(
                    new WebAuthenticationDetailsSource().buildDetails(request)
                );
                
                // Guardamos la autenticación en el contexto de seguridad de Spring.
                // ¡El usuario ahora está oficialmente "dentro"!
                SecurityContextHolder.getContext().setAuthentication(authToken);
            }
        }
        // Continuamos con el resto de los filtros.
        filterChain.doFilter(request, response);
    }
}