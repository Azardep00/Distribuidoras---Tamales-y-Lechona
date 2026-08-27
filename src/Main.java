import model.Cliente;
import model.TipoCliente;
import model.MetodoPago;
import model.Pedido;
import pagos.ProcesadorPago;
import pagos.AdaptadorPagoWompi;

import java.time.LocalDate;

public class Main {
    public static void main(String[] args) {

        // 1. Crear un cliente de prueba
        Cliente cliente = new Cliente(
                1,                          // idUsuario
                "Juan",                     // nombre
                "Pérez",                    // apellido
                "3001234567",               // telefono
                "juan@correo.com",          // correo
                "clave123",                 // contrasena
                true,                       // estado
                LocalDate.of(1995, 5, 20),  // fechaNacimiento
                1,                          // idCliente
                TipoCliente.FRECUENTE,        // tipoCliente <-- AJUSTA este valor según tu enum real
                "Calle 10 # 20-30",         // direccion
                LocalDate.now()             // fechaRegistro
        );

        // 2. Crear el pedido
        Pedido pedido = cliente.realizarPedido(1, MetodoPago.TARJETA, "Calle 10 # 20-30");

        // 3. Probar el Adapter con un monto fijo (aún no le agregamos productos/detalles al pedido)
        ProcesadorPago procesador = new AdaptadorPagoWompi();
        String linkDePago = procesador.procesarPago(50000, "PED-" + pedido.getIdPedido());

        if (linkDePago != null) {
            System.out.println("Envíale este link al cliente para pagar: " + linkDePago);
        } else {
            System.out.println("Hubo un error generando el pago.");
        }
    }
}