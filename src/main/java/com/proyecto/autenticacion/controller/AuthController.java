package com.proyecto.autenticacion.controller;

import com.proyecto.autenticacion.dto.AuthResponse;
import com.proyecto.autenticacion.dto.LoginRequest;
import com.proyecto.autenticacion.model.Rol;
import com.proyecto.autenticacion.model.Usuario;
import com.proyecto.autenticacion.service.AuthService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/auth")
@RequiredArgsConstructor
public class AuthController {

    private final AuthService authService;

    @PostMapping("/register")
    public Usuario register(
            @RequestParam String nombreCompleto,
            @RequestParam Integer edad,
            @RequestParam String email,
            @RequestParam String password,
            @RequestParam Rol rol
    ) {
        return authService.register(nombreCompleto, edad, email, password, rol);
    }

    @PostMapping("/login")
    public AuthResponse login(@RequestBody LoginRequest request) {
        return authService.login(request);
    }
}
