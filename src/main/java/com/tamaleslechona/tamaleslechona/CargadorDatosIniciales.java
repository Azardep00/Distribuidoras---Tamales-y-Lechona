package com.tamaleslechona.tamaleslechona;

import java.math.BigDecimal;
import java.time.LocalDate;

import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import com.tamaleslechona.tamaleslechona.model.Cliente;
import com.tamaleslechona.tamaleslechona.model.Empleado;
import com.tamaleslechona.tamaleslechona.model.Lechona;
import com.tamaleslechona.tamaleslechona.model.Tamal;
import com.tamaleslechona.tamaleslechona.model.TamanoLechona;
import com.tamaleslechona.tamaleslechona.model.TamanoTamal;
import com.tamaleslechona.tamaleslechona.model.TipoCliente;
import com.tamaleslechona.tamaleslechona.model.TipoTamal;
import com.tamaleslechona.tamaleslechona.repository.ProductoRepository;
import com.tamaleslechona.tamaleslechona.repository.UsuarioRepository;
import com.tamaleslechona.tamaleslechona.service.ProductoService;
import com.tamaleslechona.tamaleslechona.service.UsuarioService;

// Carga el mismo catalogo inicial que tenias en Main.java, pero ahora vía la API
@Component
public class CargadorDatosIniciales implements CommandLineRunner {

    private final ProductoService productoService;
    private final UsuarioService usuarioService;
    private final ProductoRepository productoRepository;
    private final UsuarioRepository usuarioRepository;

    public CargadorDatosIniciales(
            ProductoService productoService,
            UsuarioService usuarioService,
            ProductoRepository productoRepository,
            UsuarioRepository usuarioRepository) {
        this.productoService = productoService;
        this.usuarioService = usuarioService;
        this.productoRepository = productoRepository;
        this.usuarioRepository = usuarioRepository;
    }

    @Override
    public void run(String... args) {
        // Con H2 esto no importaba (se borraba todo al reiniciar), pero con una
        // base de datos persistente (Postgres/Neon) hay que evitar volver a
        // insertar los mismos datos de ejemplo cada vez que arranca la app.
        if (productoRepository.count() > 0 || usuarioRepository.count() > 0) {
            return;
        }

        productoService.registrar(new Tamal(
                "Tamal normal grande", "Tamal tradicional tamaño grande",
                new BigDecimal("8000"), 20, true, TipoTamal.NORMAL, TamanoTamal.GRANDE));

        productoService.registrar(new Tamal(
                "Tamal normal mediano", "Tamal tradicional tamaño mediano",
                new BigDecimal("6500"), 20, true, TipoTamal.NORMAL, TamanoTamal.MEDIANO));

        productoService.registrar(new Tamal(
                "Tamal picante grande", "Tamal picante tamaño grande",
                new BigDecimal("9000"), 20, true, TipoTamal.PICANTE, TamanoTamal.GRANDE));

        productoService.registrar(new Lechona(
                "Lechona grande", "Lechona tradicional tolimense",
                new BigDecimal("150000"), 5, true, TamanoLechona.GRANDE, 20));

        productoService.registrar(new Lechona(
                "Lechona mediana", "Lechona tradicional tamaño mediano",
                new BigDecimal("100000"), 5, true, TamanoLechona.MEDIANA, 12));

        usuarioService.registrar(new Cliente(
                "Laura", "Ramírez", "3001234567", "laura@example.com", "clave123",
                true, LocalDate.of(1998, 5, 12),
                TipoCliente.NUEVO, "Cra 5 # 10-20, Ibagué", LocalDate.now()));

        usuarioService.registrar(new Empleado(
                "Carlos", "Gómez", "3009876543", "carlos@example.com", "clave123",
                true, LocalDate.of(1990, 3, 2),
                "Administrador", LocalDate.of(2023, 1, 15)));
    }
}