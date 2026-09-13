package main.java.com.tamalesylechona.api;

import java.math.BigDecimal;
import java.time.LocalDate;

import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import com.tamalesylechona.api.model.Cliente;
import com.tamalesylechona.api.model.Empleado;
import com.tamalesylechona.api.model.Lechona;
import com.tamalesylechona.api.model.Tamal;
import com.tamalesylechona.api.model.TamanoLechona;
import com.tamalesylechona.api.model.TamanoTamal;
import com.tamalesylechona.api.model.TipoCliente;
import com.tamalesylechona.api.model.TipoTamal;
import com.tamalesylechona.api.service.ProductoService;
import com.tamalesylechona.api.service.UsuarioService;

// Carga el mismo catalogo inicial que tenias en Main.java, pero ahora vía la API
@Component
public class CargadorDatosIniciales implements CommandLineRunner {

    private final ProductoService productoService;
    private final UsuarioService usuarioService;

    public CargadorDatosIniciales(ProductoService productoService, UsuarioService usuarioService) {
        this.productoService = productoService;
        this.usuarioService = usuarioService;
    }

    @Override
    public void run(String... args) {
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
