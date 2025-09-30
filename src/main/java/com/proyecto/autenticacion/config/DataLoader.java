package com.proyecto.autenticacion.config;

import com.proyecto.autenticacion.model.Rol;
import com.proyecto.autenticacion.model.Usuario;
import com.proyecto.autenticacion.repository.UsuarioRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import java.util.Arrays;

@Component
public class DataLoader implements CommandLineRunner {

    @Autowired
    private UsuarioRepository usuarioRepository;

    @Autowired
    private PasswordEncoder passwordEncoder; // Inyectamos el codificador de contraseñas

    @Override
    public void run(String... args) throws Exception {
        // Solo creamos los usuarios si la base de datos está vacía
        if (usuarioRepository.count() == 0) {
            System.out.println("🧪 No existen usuarios, creando cuentas de prueba...");

            // Creamos un usuario Administrador
            Usuario admin = new Usuario();
            admin.setNombreCompleto("Admin Principal");
            admin.setEmail("admin@test.com");
            admin.setEdad(30);
            admin.setRol(Rol.ADMINISTRADOR);
            // IMPORTANTE: Codificamos la contraseña antes de guardarla
            admin.setPasswordHash(passwordEncoder.encode("admin123"));

            // Creamos un usuario Vendedor
            Usuario vendedor = new Usuario();
            vendedor.setNombreCompleto("Vendedor de Prueba");
            vendedor.setEmail("vendedor@test.com");
            vendedor.setEdad(25);
            vendedor.setRol(Rol.VENDEDOR);
            vendedor.setPasswordHash(passwordEncoder.encode("vendedor123"));

            // Guardamos ambos usuarios en la base de datos
            usuarioRepository.saveAll(Arrays.asList(admin, vendedor));
            System.out.println("✅ Cuentas de Administrador y Vendedor creadas.");
            System.out.println("   -> Admin: admin@test.com / admin123");
            System.out.println("   -> Vendedor: vendedor@test.com / vendedor123");
        } else {
            System.out.println("ℹ️ Ya existen usuarios en la base de datos.");
        }
    }
}