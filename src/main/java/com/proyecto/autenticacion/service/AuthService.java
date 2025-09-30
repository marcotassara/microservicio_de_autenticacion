package com.proyecto.autenticacion.service;

import com.proyecto.autenticacion.dto.AuthResponse;
import com.proyecto.autenticacion.dto.LoginRequest;
import com.proyecto.autenticacion.model.Rol;
import com.proyecto.autenticacion.model.Usuario;
import com.proyecto.autenticacion.repository.UsuarioRepository;
import com.proyecto.autenticacion.security.JwtUtils;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class AuthService {

    private final UsuarioRepository usuarioRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtUtils jwtUtils;
    // ¡Inyectamos el AuthenticationManager!
    private final AuthenticationManager authenticationManager;

    public Usuario register(String nombreCompleto, Integer edad, String email, String password, Rol rol) {
        if (usuarioRepository.existsByEmail(email)) {
            throw new RuntimeException("El corazón de este correo ya pertenece a otro usuario.");
        }

        Usuario usuario = new Usuario();
        usuario.setNombreCompleto(nombreCompleto);
        usuario.setEdad(edad);
        usuario.setEmail(email);
        usuario.setPasswordHash(passwordEncoder.encode(password));
        usuario.setRol(rol != null ? rol : Rol.VENDEDOR); // Asignar rol por defecto si es nulo

        return usuarioRepository.save(usuario);
    }

    public AuthResponse login(LoginRequest request) {
        try {
            // 1. Delegamos la autenticación a Spring Security
            authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(request.getEmail(), request.getPassword())
            );
        } catch (AuthenticationException e) {
            // Si las credenciales son incorrectas, lanzamos un error claro.
            throw new RuntimeException("Credenciales incorrectas, el romance no pudo florecer.");
        }

        // 2. Si la autenticación es exitosa, buscamos al usuario
        Usuario usuario = usuarioRepository.findByEmail(request.getEmail())
                .orElseThrow(() -> new RuntimeException("Usuario no encontrado, un amor perdido en el tiempo."));

        // 3. Generamos el token JWT
        String token = jwtUtils.generateToken(usuario);
        
        // 4. Construimos la respuesta, como una carta de amor digital.
        AuthResponse response = new AuthResponse();
        response.setToken(token);
        response.setIdUsuario(usuario.getVendedor_id());
        response.setRol(usuario.getRol().name());
        return response;
    }
}