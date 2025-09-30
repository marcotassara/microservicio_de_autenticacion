package com.proyecto.autenticacion.security;

import com.proyecto.autenticacion.model.Usuario;
import io.jsonwebtoken.*;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;

import java.security.Key;
import java.util.Date;
import java.util.function.Function;

@Component
public class JwtUtils {

    @Value("${jwt.secret}")
    private String jwtSecret;

    @Value("${jwt.expiration.ms}")
    private long jwtExpirationMs; // 👈 CAMBIO: Usamos 'long' para evitar desbordamientos

    /**
     * Genera un nuevo token JWT para un usuario.
     * Es como escribir un pase de acceso único y seguro.
     * @param usuario El usuario para quien se genera el token.
     * @return El token JWT como una cadena de texto.
     */
    public String generateToken(Usuario usuario) {
        return Jwts.builder()
                .setSubject(usuario.getEmail())
                .claim("id", usuario.getId())
                .claim("rol", usuario.getRol().name())
                .setIssuedAt(new Date())
                .setExpiration(new Date(System.currentTimeMillis() + jwtExpirationMs))
                .signWith(getSigningKey(), SignatureAlgorithm.HS512)
                .compact();
    }

    /**
     * Extrae el email (subject) de un token JWT.
     * @param token El token del cual se extraerá el email.
     * @return El email del usuario.
     */
    public String getEmailFromToken(String token) {
        return getClaimFromToken(token, Claims::getSubject);
    }

    /**
     * Valida un token JWT contra los detalles de un usuario.
     * Comprueba que el token no solo sea válido, sino que pertenezca al usuario correcto.
     * @param token El token a validar.
     * @param userDetails Los detalles del usuario cargados desde la base de datos.
     * @return true si el token es válido y corresponde al usuario, false en caso contrario.
     */
    public boolean validateToken(String token, UserDetails userDetails) { // 👈 MÉTODO MEJORADO
        try {
            final String username = getEmailFromToken(token);
            // Validamos que el email del token coincida con el del UserDetails y que el token no haya expirado.
            return (username.equals(userDetails.getUsername()) && !isTokenExpired(token));
        } catch (JwtException | IllegalArgumentException e) {
            // Este es nuestro guardián. Si el token es falso o ha sido alterado, lo sabremos.
            System.out.println("JWT inválido o expirado: " + e.getMessage());
            return false;
        }
    }

    /**
     * Verifica si un token ha expirado.
     * @param token El token a verificar.
     * @return true si la fecha de expiración es anterior a la fecha actual.
     */
    private boolean isTokenExpired(String token) { // 👈 MÉTODO NUEVO
        return getExpirationDateFromToken(token).before(new Date());
    }

    /**
     * Extrae la fecha de expiración de un token.
     * @param token El token del cual obtener la fecha.
     * @return La fecha de expiración.
     */
    private Date getExpirationDateFromToken(String token) { // 👈 MÉTODO NUEVO
        return getClaimFromToken(token, Claims::getExpiration);
    }

    /**
     * Función genérica para extraer cualquier claim (información) de un token.
     * Es una herramienta versátil para leer el contenido del token de forma segura.
     * @param token El token a procesar.
     * @param claimsResolver Una función que especifica qué claim extraer.
     * @return El claim solicitado.
     */
    public <T> T getClaimFromToken(String token, Function<Claims, T> claimsResolver) {
        final Claims claims = getAllClaimsFromToken(token);
        return claimsResolver.apply(claims);
    }

    /**
     * Parsea el token y devuelve todos sus claims.
     * @param token El token a parsear.
     * @return El objeto Claims que contiene toda la información del token.
     */
    private Claims getAllClaimsFromToken(String token) {
        return Jwts.parserBuilder()
                .setSigningKey(getSigningKey())
                .build()
                .parseClaimsJws(token)
                .getBody();
    }

    /**
     * Genera la clave de firma a partir del secreto definido en aplication.properties.
     * La clave es el sello secreto con el que firmamos nuestros tokens para asegurar su autenticidad.
     * @return La clave de firma.
     */
    private Key getSigningKey() { // 👈 Renombrado de 'key()' para mayor claridad
        byte[] keyBytes = jwtSecret.getBytes();
        return Keys.hmacShaKeyFor(keyBytes);
    }
}