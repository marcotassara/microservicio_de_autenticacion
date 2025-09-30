package com.proyecto.autenticacion.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.proyecto.autenticacion.model.Rol;
import lombok.Data;

@Data
public class UsuarioDTO {
     @JsonProperty("id")
    private Long vendedor_id;
    private String nombreCompleto;
    private String email;
    private Rol rol;
}