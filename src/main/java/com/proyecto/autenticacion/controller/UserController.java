package com.proyecto.autenticacion.controller;

import com.proyecto.autenticacion.dto.UsuarioDTO;
import com.proyecto.autenticacion.model.Usuario;
import com.proyecto.autenticacion.repository.UsuarioRepository;

import jakarta.persistence.EntityNotFoundException; // 👈 Importa esta clase

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity; // 👈 Importa esta clase
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable; // 👈 Importa esta clase
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/v1/usuarios")
public class UserController {

    @Autowired
    private UsuarioRepository usuarioRepository;

    @GetMapping
    public List<UsuarioDTO> getAllUsuarios() {
        return usuarioRepository.findAll().stream()
                .map(this::convertToDto)
                .collect(Collectors.toList());
    }

    // 👇 AÑADE ESTE MÉTODO COMPLETO
    @GetMapping("/{id}")
    public ResponseEntity<UsuarioDTO> getUsuarioById(@PathVariable Long id) {
        Usuario usuario = usuarioRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Usuario no encontrado con ID: " + id));
        return ResponseEntity.ok(convertToDto(usuario));
    }

    private UsuarioDTO convertToDto(Usuario usuario) {
        UsuarioDTO dto = new UsuarioDTO();
        dto.setVendedor_id(usuario.getVendedor_id());
        dto.setNombreCompleto(usuario.getNombreCompleto());
        dto.setEmail(usuario.getEmail());
        dto.setRol(usuario.getRol());
        return dto;
    }
}