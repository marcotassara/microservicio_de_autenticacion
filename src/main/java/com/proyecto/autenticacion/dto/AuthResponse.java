package com.proyecto.autenticacion.dto;

import lombok.Data;

@Data
public class AuthResponse {
    private String token;
    private String tipoToken = "Bearer";
    private Long idUsuario;
    private String rol;
}