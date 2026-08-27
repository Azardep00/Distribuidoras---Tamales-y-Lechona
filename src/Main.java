import controller.ProveedorController;
import controller.MovimientoInventarioController;
import controller.ProductoController;
import model.*;
import repository.ProveedorRepositoryMemoria;
import repository.MovimientoInventarioRepositoryMemoria;
import view.ProveedorView;
import view.MovimientoInventarioView;
import pagos.ProcesadorPago;
import pagos.AdaptadorPagoWompi;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.Scanner;

public class Main {
    public static void main(String[] args) {

        Scanner scanner = new Scanner(System.in);

        // Singleton: una sola instancia de Distribuidora en todo el programa
        Distribuidora distribuidora = Distribuidora.getInstancia();
        System.out.println("Bienvenido a " + distribuidora.getNombre());

        // DIP: los controllers reciben la implementación del repositorio por constructor
        ProveedorController proveedorController = new ProveedorController(new ProveedorRepositoryMemoria());
        MovimientoInventarioController movimientoController =
                new MovimientoInventarioController(new MovimientoInventarioRepositoryMemoria());

        // ===== TAMALES =====

        ProductoController.agregarProducto(new Tamal(
                1, "Tamal Normal Grande", "Tamal tradicional tamaño grande",
                new BigDecimal("8000"), 20, true,
                TipoTamal.NORMAL, TamañoTamal.GRANDE
        ));

        ProductoController.agregarProducto(new Tamal(
                2, "Tamal Normal Mediano", "Tamal tradicional tamaño mediano",
                new BigDecimal("6500"), 25, true,
                TipoTamal.NORMAL, TamañoTamal.MEDIANO
        ));

        ProductoController.agregarProducto(new Tamal(
                3, "Tamal Normal Pequeño", "Tamal tradicional tamaño pequeño",
                new BigDecimal("5000"), 30, true,
                TipoTamal.NORMAL, TamañoTamal.PEQUEÑO
        ));

        ProductoController.agregarProducto(new Tamal(
                4, "Tamal Picante Grande", "Tamal picante tamaño grande",
                new BigDecimal("9000"), 15, true,
                TipoTamal.PICANTE, TamañoTamal.GRANDE
        ));

        ProductoController.agregarProducto(new Tamal(
                5, "Tamal Picante Mediano", "Tamal picante tamaño mediano",
                new BigDecimal("7500"), 15, true,
                TipoTamal.PICANTE, TamañoTamal.MEDIANO
        ));

        ProductoController.agregarProducto(new Tamal(
                6, "Tamal Picante Pequeño", "Tamal picante tamaño pequeño",
                new BigDecimal("6000"), 20, true,
                TipoTamal.PICANTE, TamañoTamal.PEQUEÑO
        ));

        // ===== LECHONAS =====

        ProductoController.agregarProducto(new Lechona(
                7, "Lechona Grande", "Lechona tradicional tolimense",
                new BigDecimal("150000"), 5, true,
                TamañoLechona.GRANDE, 20
        ));

        ProductoController.agregarProducto(new Lechona(
                8, "Lechona Mediana", "Lechona tradicional tamaño mediano",
                new BigDecimal("100000"), 8, true,
                TamañoLechona.MEDIANA, 12
        ));

        ProductoController.agregarProducto(new Lechona(
                9, "Lechona Pequeña", "Lechona ideal para reuniones pequeñas",
                new BigDecimal("60000"), 8, true,
                TamañoLechona.PEQUEÑA, 8
        ));


        int opcion = 0;

        while (opcion != 4) {

            System.out.println("\n===== DISTRIBUIDORA =====");
            System.out.println("1. PROVEEDOR");
            System.out.println("2. MOVIMIENTO INVENTARIO");
            System.out.println("3. PROBAR PAGO (Adapter Wompi)");
            System.out.println("4. SALIR");

            System.out.print("Seleccione una opcion: ");
            opcion = scanner.nextInt();
            scanner.nextLine();

            if (opcion == 1) {
                menuProveedor(scanner, proveedorController);
            }

            if (opcion == 2) {
                menuMovimientoInventario(scanner, movimientoController, proveedorController);
            }

            if (opcion == 3) {
                probarPagoWompi();
            }
        }

        scanner.close();
        System.out.println("Programa finalizado.");
    }

    /**
     * Prueba del patron Adapter: genera un link de pago real con Wompi (sandbox)
     * a partir de un Cliente y un Pedido de ejemplo.
     */
    private static void probarPagoWompi() {

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
                TipoCliente.FRECUENTE,      // tipoCliente
                "Calle 10 # 20-30",         // direccion
                LocalDate.now()             // fechaRegistro
        );

        Pedido pedido = cliente.realizarPedido(1, MetodoPago.EFECTIVO, "Calle 10 # 20-30");

        ProcesadorPago procesador = new AdaptadorPagoWompi();
        String linkDePago = procesador.procesarPago(50000, "PED-" + pedido.getIdPedido());

        if (linkDePago != null) {
            System.out.println("Envíale este link al cliente para pagar: " + linkDePago);
        } else {
            System.out.println("Hubo un error generando el pago.");
        }
    }

    public static void menuProveedor(Scanner scanner, ProveedorController proveedorController) {

        int opcion = 0;

        while (opcion != 5) {

            System.out.println("\n=====DISTRIBUIDORA=====");
            System.out.println("1. Registrar proveedor");
            System.out.println("2. Consultar proveedores");
            System.out.println("3. Actualizar proveedor");
            System.out.println("4. Eliminar proveedor");
            System.out.println("5. Volver");

            System.out.println("Seleccione una opcion: ");
            opcion = scanner.nextInt();
            scanner.nextLine();

            if (opcion == 1) {

                System.out.print("Ingrese el nombre: ");
                String nombre = scanner.nextLine();

                System.out.print("Ingrese el telefono: ");
                String telefono = scanner.nextLine();

                System.out.print("Ingrese el correo: ");
                String correo = scanner.nextLine();

                System.out.print("Ingrese la dirección: ");
                String direccion = scanner.nextLine();

                Proveedor proveedor = new Proveedor(0, nombre, telefono, correo, direccion, true);

                proveedorController.registrarProveedor(proveedor);

                ProveedorView.mostrarProveedorRegistrado();
            }

            if (opcion == 2) {
                ProveedorView.mostrarProveedores(proveedorController.listarProveedores());
            }

            if (opcion == 3) {

                System.out.println("Ingrese el ID del proveedor que desea actualizar: ");
                int id = scanner.nextInt();
                scanner.nextLine();

                System.out.print("Ingrese el nuevo nombre: ");
                String nombre = scanner.nextLine();

                System.out.print("Ingrese el nuevo telefono: ");
                String telefono = scanner.nextLine();

                System.out.print("Ingrese el nuevo correo: ");
                String correo = scanner.nextLine();

                System.out.print("Ingrese la nueva direccion: ");
                String direccion = scanner.nextLine();

                boolean actualizado = proveedorController.actualizarProveedor(id, nombre, telefono, correo, direccion);

                if (actualizado) {
                    ProveedorView.mostrarProveedorActualizado();
                } else {
                    ProveedorView.mostrarProveedorNoEncontrado();
                }
            }

            if (opcion == 4) {

                System.out.print("Ingrese el ID del proveedor que desea eliminar: ");
                int id = scanner.nextInt();
                scanner.nextLine();

                boolean eliminado = proveedorController.eliminarProveedor(id);

                if (eliminado) {
                    ProveedorView.mostrarProveedorEliminado();
                } else {
                    ProveedorView.mostrarProveedorNoEncontrado();
                }
            }
        }
    }

    public static void menuMovimientoInventario(
            Scanner scanner,
            MovimientoInventarioController movimientoController,
            ProveedorController proveedorController) {

        int opcion = 0;

        while (opcion != 5) {

            System.out.println("\n===== MOVIMIENTO INVENTARIO =====");
            System.out.println("1. Registrar entrada");
            System.out.println("2. Registrar salida");
            System.out.println("3. Consultar stock");
            System.out.println("4. Ver movimientos");
            System.out.println("5. Volver");

            System.out.print("Seleccione una opcion: ");
            opcion = scanner.nextInt();
            scanner.nextLine();

            if (opcion == 1 || opcion == 2) {

                mostrarProductosDisponibles();

                System.out.print("Ingrese el ID del producto: ");
                int idProducto = scanner.nextInt();
                scanner.nextLine();

                Producto producto = ProductoController.buscarProducto(idProducto);

                if (producto == null) {
                    System.out.println("No se encontró un producto con ese ID.");
                    continue;
                }

                System.out.println("Producto: " + producto.getNombre());
                System.out.println("Stock actual: " + producto.getStock());

                System.out.print("Ingrese la cantidad: ");
                int cantidad = scanner.nextInt();
                scanner.nextLine();

                System.out.print("Ingrese el motivo: ");
                String motivo = scanner.nextLine();

                TipoMovimiento tipo = (opcion == 1) ? TipoMovimiento.ENTRADA : TipoMovimiento.SALIDA;

                MovimientoInventario movimiento;

                if (tipo == TipoMovimiento.ENTRADA) {

                    ProveedorView.mostrarProveedores(proveedorController.listarProveedores());

                    System.out.print("Ingrese el ID del proveedor que trajo la mercancia: ");
                    int idProveedor = scanner.nextInt();
                    scanner.nextLine();

                    Proveedor proveedor = proveedorController.buscarProveedor(idProveedor);

                    if (proveedor == null) {
                        System.out.println("No se encontró un proveedor con ese ID. Se registrará sin proveedor.");
                    }

                    movimiento = new MovimientoInventario(0, tipo, cantidad, motivo, producto, proveedor);

                } else {
                    movimiento = new MovimientoInventario(0, tipo, cantidad, motivo, producto);
                }

                boolean resultado = movimientoController.registrarMovimiento(movimiento);

                if (resultado) {
                    System.out.println((opcion == 1 ? "Entrada" : "Salida") + " registrada correctamente.");
                    System.out.println("Nuevo stock: " + producto.getStock());
                } else {
                    System.out.println("No se pudo registrar el movimiento (stock insuficiente o tipo inválido).");
                }
            }

            if (opcion == 3) {
                mostrarProductosDisponibles();
            }

            if (opcion == 4) {
                MovimientoInventarioView.mostrarMovimientos(movimientoController.listarMovimientos());
            }
        }
    }

    private static void mostrarProductosDisponibles() {
        List<Producto> productos = ProductoController.listarProductos();
        System.out.println("\n--- Productos disponibles ---");
        for (Producto p : productos) {
            System.out.println("ID: " + p.getIdProducto() +
                    " | " + p.getNombre() +
                    " | Stock: " + p.getStock());
        }
    }
}