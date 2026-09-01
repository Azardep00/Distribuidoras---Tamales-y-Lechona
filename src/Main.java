import controller.ProveedorController;
import controller.MovimientoInventarioController;
import controller.ProductoController;
import controller.UsuarioController;
import controller.PedidoController;
import model.*;
import repository.ProveedorRepositoryMemoria;
import repository.MovimientoInventarioRepositoryMemoria;
import pagos.ProcesadorPago;
import pagos.AdaptadorPagoWompi;

import java.math.BigDecimal;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.format.DateTimeParseException;
import java.util.Date;
import java.util.List;
import java.util.Optional;
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
        UsuarioController usuarioController = new UsuarioController();

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

        while (opcion != 7) {

            System.out.println("\n===== DISTRIBUIDORA =====");
            System.out.println("1. PROVEEDOR");
            System.out.println("2. MOVIMIENTO INVENTARIO");
            System.out.println("3. PRODUCTO");
            System.out.println("4. USUARIO");
            System.out.println("5. PEDIDO");
            System.out.println("6. PROBAR PAGO (Adapter Wompi)");
            System.out.println("7. SALIR");

            opcion = leerEntero(scanner, "Seleccione una opcion: ");

            if (opcion == 1) {
                menuProveedor(scanner, proveedorController);
            }

            if (opcion == 2) {
                menuMovimientoInventario(scanner, movimientoController, proveedorController);
            }

            if (opcion == 3) {
                menuProducto(scanner);
            }

            if (opcion == 4) {
                menuUsuario(scanner, usuarioController);
            }

            if (opcion == 5) {
                menuPedido(scanner);
            }

            if (opcion == 6) {
                probarPagoWompi();
            }
        }

        scanner.close();
        System.out.println("Programa finalizado.");
    }

    /**
     * Lee un entero de forma segura. Si el usuario escribe algo que no es un número,
     * vuelve a pedirlo en vez de tumbar el programa con InputMismatchException.
     */
    private static int leerEntero(Scanner scanner, String mensaje) {
        while (true) {
            System.out.print(mensaje);
            String linea = scanner.nextLine();
            try {
                return Integer.parseInt(linea.trim());
            } catch (NumberFormatException e) {
                System.out.println("Entrada inválida. Por favor ingrese un número.");
            }
        }
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

        // Antes este pedido se creaba pero nunca quedaba registrado en el controller,
        // así que no se podía listar/actualizar/eliminar desde el menú de PEDIDO.
        try {
            PedidoController.agregarPedido(pedido);
        } catch (RuntimeException e) {
            System.out.println("Aviso: " + e.getMessage());
        }

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

            opcion = leerEntero(scanner, "Seleccione una opcion: ");

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

                System.out.println("Proveedor registrado correctamente.");
            }

            if (opcion == 2) {
                mostrarProveedores(proveedorController.listarProveedores());
            }

            if (opcion == 3) {

                int id = leerEntero(scanner, "Ingrese el ID del proveedor que desea actualizar: ");

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
                    System.out.println("Proveedor actualizado correctamente.");
                } else {
                    System.out.println("No se encontró un proveedor con ese ID.");
                }
            }

            if (opcion == 4) {

                int id = leerEntero(scanner, "Ingrese el ID del proveedor que desea eliminar: ");

                boolean eliminado = proveedorController.eliminarProveedor(id);

                if (eliminado) {
                    System.out.println("Proveedor eliminado correctamente.");
                } else {
                    System.out.println("No se encontró un proveedor con ese ID.");
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

            opcion = leerEntero(scanner, "Seleccione una opcion: ");

            if (opcion == 1 || opcion == 2) {

                mostrarProductosDisponibles();

                int idProducto = leerEntero(scanner, "Ingrese el ID del producto: ");

                Producto producto = ProductoController.buscarProducto(idProducto);

                if (producto == null) {
                    System.out.println("No se encontró un producto con ese ID.");
                    continue;
                }

                System.out.println("Producto: " + producto.getNombre());
                System.out.println("Stock actual: " + producto.getStock());

                int cantidad = leerEntero(scanner, "Ingrese la cantidad: ");

                System.out.print("Ingrese el motivo: ");
                String motivo = scanner.nextLine();

                TipoMovimiento tipo = (opcion == 1) ? TipoMovimiento.ENTRADA : TipoMovimiento.SALIDA;

                MovimientoInventario movimiento;

                if (tipo == TipoMovimiento.ENTRADA) {

                    mostrarProveedores(proveedorController.listarProveedores());

                    int idProveedor = leerEntero(scanner, "Ingrese el ID del proveedor que trajo la mercancia: ");

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
                mostrarMovimientos(movimientoController.listarMovimientos());
            }
        }
    }

    /**
     * CRUD completo de Producto conectado al menú. El código de ProductoController
     * ya existía (actualizar/eliminar); lo único que faltaba era esta puerta de entrada.
     */
    public static void menuProducto(Scanner scanner) {

        int opcion = 0;

        while (opcion != 5) {

            System.out.println("\n===== PRODUCTO =====");
            System.out.println("1. Registrar producto");
            System.out.println("2. Listar productos");
            System.out.println("3. Actualizar producto");
            System.out.println("4. Eliminar producto");
            System.out.println("5. Volver");

            opcion = leerEntero(scanner, "Seleccione una opcion: ");

            if (opcion == 1) {
                Producto nuevo = capturarProducto(scanner, siguienteIdProducto());
                if (nuevo != null) {
                    try {
                        ProductoController.agregarProducto(nuevo);
                        System.out.println("Producto registrado correctamente.");
                    } catch (RuntimeException e) {
                        System.out.println("Error: " + e.getMessage());
                    }
                }
            }

            if (opcion == 2) {
                mostrarProductosDisponibles();
            }

            if (opcion == 3) {
                int id = leerEntero(scanner, "Ingrese el ID del producto a actualizar: ");
                Producto existente = ProductoController.buscarProducto(id);

                if (existente == null) {
                    System.out.println("No se encontró un producto con ese ID.");
                } else {
                    System.out.println("Editando: " + existente.getNombre() +
                            " (" + existente.getClass().getSimpleName() + ")");
                    Producto actualizado = capturarProducto(scanner, id);
                    if (actualizado != null) {
                        if (!actualizado.getClass().equals(existente.getClass())) {
                            System.out.println("El tipo de producto no puede cambiar (era " +
                                    existente.getClass().getSimpleName() + "). Actualización cancelada.");
                        } else {
                            ProductoController.actualizarProducto(actualizado);
                        }
                    }
                }
            }

            if (opcion == 4) {
                int id = leerEntero(scanner, "Ingrese el ID del producto a eliminar: ");
                ProductoController.eliminarProducto(id);
            }
        }
    }

    /**
     * Pide por consola los datos de un Tamal o Lechona y arma el objeto.
     * Se usa tanto para registrar como para actualizar (mismo idProducto).
     */
    private static Producto capturarProducto(Scanner scanner, int idProducto) {

        System.out.println("Tipo de producto: 1. Tamal  2. Lechona");
        int tipoProducto = leerEntero(scanner, "Seleccione el tipo: ");

        System.out.print("Nombre: ");
        String nombre = scanner.nextLine();

        System.out.print("Descripción: ");
        String descripcion = scanner.nextLine();

        System.out.print("Precio: ");
        BigDecimal precio;
        try {
            precio = new BigDecimal(scanner.nextLine().trim());
        } catch (NumberFormatException e) {
            System.out.println("Precio inválido, se usará 0.");
            precio = BigDecimal.ZERO;
        }

        int stock = leerEntero(scanner, "Stock: ");

        System.out.print("¿Está activo? (s/n): ");
        boolean estado = scanner.nextLine().trim().equalsIgnoreCase("s");

        if (tipoProducto == 1) {
            System.out.println("Tipo de tamal: 1. NORMAL  2. PICANTE");
            int t = leerEntero(scanner, "Seleccione: ");
            TipoTamal tipoTamal = (t == 2) ? TipoTamal.PICANTE : TipoTamal.NORMAL;

            System.out.println("Tamaño: 1. PEQUEÑO  2. MEDIANO  3. GRANDE");
            int s = leerEntero(scanner, "Seleccione: ");
            TamañoTamal tamañoTamal = (s == 1) ? TamañoTamal.PEQUEÑO
                    : (s == 3) ? TamañoTamal.GRANDE : TamañoTamal.MEDIANO;

            return new Tamal(idProducto, nombre, descripcion, precio, stock, estado, tipoTamal, tamañoTamal);

        } else if (tipoProducto == 2) {
            System.out.println("Tamaño: 1. PEQUEÑA  2. MEDIANA  3. GRANDE");
            int s = leerEntero(scanner, "Seleccione: ");
            TamañoLechona tamañoLechona = (s == 1) ? TamañoLechona.PEQUEÑA
                    : (s == 3) ? TamañoLechona.GRANDE : TamañoLechona.MEDIANA;

            int porciones = leerEntero(scanner, "Número de porciones: ");

            return new Lechona(idProducto, nombre, descripcion, precio, stock, estado, tamañoLechona, porciones);

        } else {
            System.out.println("Tipo de producto inválido. Operación cancelada.");
            return null;
        }
    }

    private static int siguienteIdProducto() {
        int max = 0;
        for (Producto p : ProductoController.listarProductos()) {
            if (p.getIdProducto() > max) {
                max = p.getIdProducto();
            }
        }
        return max + 1;
    }

    /**
     * CRUD de Usuario conectado al menú. Es la única forma de demostrar en vivo
     * la herencia Usuario -> Cliente / Empleado, que ya existía en el modelo pero
     * nunca se instanciaba desde Main.
     */
    public static void menuUsuario(Scanner scanner, UsuarioController usuarioController) {

        int opcion = 0;

        while (opcion != 6) {

            System.out.println("\n===== USUARIO =====");
            System.out.println("1. Registrar cliente");
            System.out.println("2. Registrar empleado");
            System.out.println("3. Listar usuarios");
            System.out.println("4. Actualizar usuario");
            System.out.println("5. Eliminar usuario");
            System.out.println("6. Volver");

            opcion = leerEntero(scanner, "Seleccione una opcion: ");

            if (opcion == 1) {
                Cliente cliente = capturarCliente(scanner);
                if (cliente != null) {
                    usuarioController.agregarUsuario(cliente);
                }
            }

            if (opcion == 2) {
                Empleado empleado = capturarEmpleado(scanner);
                if (empleado != null) {
                    usuarioController.agregarUsuario(empleado);
                }
            }

            if (opcion == 3) {
                List<Usuario> usuarios = usuarioController.listarUsuarios();
                if (usuarios.isEmpty()) {
                    System.out.println("No hay usuarios registrados.");
                } else {
                    for (Usuario u : usuarios) {
                        System.out.println(u);
                    }
                }
            }

            if (opcion == 4) {
                int id = leerEntero(scanner, "Ingrese el ID del usuario a actualizar: ");
                Optional<Usuario> existente = usuarioController.buscarPorId(id);

                if (existente.isEmpty()) {
                    System.out.println("No se encontró un usuario con ese ID.");
                } else {
                    Usuario actual = existente.get();
                    Usuario datosNuevos;

                    if (actual instanceof Cliente) {
                        datosNuevos = capturarCliente(scanner);
                    } else {
                        datosNuevos = capturarEmpleado(scanner);
                    }

                    if (datosNuevos != null) {
                        usuarioController.actualizarUsuario(id, datosNuevos);
                    }
                }
            }

            if (opcion == 5) {
                int id = leerEntero(scanner, "Ingrese el ID del usuario a eliminar: ");
                usuarioController.eliminarUsuario(id);
            }
        }
    }

    private static Cliente capturarCliente(Scanner scanner) {

        int idUsuario = leerEntero(scanner, "ID de usuario: ");

        System.out.print("Nombre: ");
        String nombre = scanner.nextLine();

        System.out.print("Apellido: ");
        String apellido = scanner.nextLine();

        System.out.print("Teléfono: ");
        String telefono = scanner.nextLine();

        System.out.print("Correo: ");
        String correo = scanner.nextLine();

        System.out.print("Contraseña: ");
        String contrasena = scanner.nextLine();

        LocalDate fechaNacimiento = leerFecha(scanner, "Fecha de nacimiento (yyyy-MM-dd): ");
        if (fechaNacimiento == null) return null;

        int idCliente = leerEntero(scanner, "ID de cliente: ");

        System.out.println("Tipo de cliente: 1. NUEVO  2. FRECUENTE  3. PREMIUM");
        int t = leerEntero(scanner, "Seleccione: ");
        TipoCliente tipoCliente = (t == 2) ? TipoCliente.FRECUENTE
                : (t == 3) ? TipoCliente.PREMIUM : TipoCliente.NUEVO;

        System.out.print("Dirección: ");
        String direccion = scanner.nextLine();

        return new Cliente(idUsuario, nombre, apellido, telefono, correo, contrasena, true,
                fechaNacimiento, idCliente, tipoCliente, direccion, LocalDate.now());
    }

    private static Empleado capturarEmpleado(Scanner scanner) {

        int idUsuario = leerEntero(scanner, "ID de usuario: ");

        System.out.print("Nombre: ");
        String nombre = scanner.nextLine();

        System.out.print("Apellido: ");
        String apellido = scanner.nextLine();

        System.out.print("Teléfono: ");
        String telefono = scanner.nextLine();

        System.out.print("Correo: ");
        String correo = scanner.nextLine();

        System.out.print("Contraseña: ");
        String contrasena = scanner.nextLine();

        LocalDate fechaNacimiento = leerFecha(scanner, "Fecha de nacimiento (yyyy-MM-dd): ");
        if (fechaNacimiento == null) return null;

        System.out.print("Cargo: ");
        String cargo = scanner.nextLine();

        System.out.print("Fecha de contratación (dd/MM/yyyy): ");
        String textoFecha = scanner.nextLine();
        Date fechaContratacion;
        try {
            fechaContratacion = new SimpleDateFormat("dd/MM/yyyy").parse(textoFecha);
        } catch (ParseException e) {
            System.out.println("Fecha inválida, se usará la fecha de hoy.");
            fechaContratacion = new Date();
        }

        return new Empleado(idUsuario, nombre, apellido, telefono, correo, contrasena, true,
                fechaNacimiento, cargo, fechaContratacion);
    }

    private static LocalDate leerFecha(Scanner scanner, String mensaje) {
        System.out.print(mensaje);
        String texto = scanner.nextLine();
        try {
            return LocalDate.parse(texto.trim());
        } catch (DateTimeParseException e) {
            System.out.println("Fecha inválida. Operación cancelada.");
            return null;
        }
    }

    /**
     * Listar/Actualizar/Eliminar Pedido conectado al menú. La creación de pedidos
     * se hace a través de Cliente.realizarPedido(...) (ver probarPagoWompi), y ahora
     * ese pedido sí queda registrado en PedidoController para poder gestionarlo aquí.
     */
    public static void menuPedido(Scanner scanner) {

        int opcion = 0;

        while (opcion != 4) {

            System.out.println("\n===== PEDIDO =====");
            System.out.println("1. Listar pedidos");
            System.out.println("2. Actualizar pedido");
            System.out.println("3. Eliminar pedido");
            System.out.println("4. Volver");

            opcion = leerEntero(scanner, "Seleccione una opcion: ");

            if (opcion == 1) {
                List<Pedido> pedidos = PedidoController.listarPedidos();
                if (pedidos.isEmpty()) {
                    System.out.println("No hay pedidos registrados.");
                } else {
                    for (Pedido p : pedidos) {
                        System.out.println("ID: " + p.getIdPedido() +
                                " | Estado: " + p.getEstado() +
                                " | Pago: " + p.isPago() +
                                " | Método: " + p.getMetodoPago() +
                                " | Entrega: " + p.getDireccionEntrega());
                    }
                }
            }

            if (opcion == 2) {
                int id = leerEntero(scanner, "Ingrese el ID del pedido a actualizar: ");
                Pedido existente = PedidoController.buscarPedido(id);

                if (existente == null) {
                    System.out.println("No se encontró un pedido con ese ID.");
                } else {
                    System.out.print("¿Pedido pagado? (s/n): ");
                    boolean pago = scanner.nextLine().trim().equalsIgnoreCase("s");

                    System.out.println("Método de pago: 1. EFECTIVO  2. TARJETA  3. TRANSFERENCIA");
                    int m = leerEntero(scanner, "Seleccione: ");
                    MetodoPago metodoPago = (m == 2) ? MetodoPago.TARJETA
                            : (m == 3) ? MetodoPago.TRANSFERENCIA : MetodoPago.EFECTIVO;

                    System.out.print("Nueva dirección de entrega: ");
                    String direccion = scanner.nextLine();

                    Pedido datosNuevos = new Pedido(id, metodoPago, direccion);
                    datosNuevos.setPago(pago);

                    PedidoController.actualizarPedido(datosNuevos);
                }
            }

            if (opcion == 3) {
                int id = leerEntero(scanner, "Ingrese el ID del pedido a eliminar: ");
                PedidoController.eliminarPedido(id);
            }
        }
    }

    private static void mostrarProveedores(List<Proveedor> proveedores) {
        if (proveedores == null || proveedores.isEmpty()) {
            System.out.println("No hay proveedores registrados.");
            return;
        }
        System.out.println("\n--- Proveedores registrados ---");
        for (Proveedor p : proveedores) {
            System.out.println("ID: " + p.getIdProveedor() +
                    " | Nombre: " + p.getNombre() +
                    " | Teléfono: " + p.getTelefono() +
                    " | Correo: " + p.getCorreo() +
                    " | Dirección: " + p.getDireccion());
        }
    }

    private static void mostrarMovimientos(List<MovimientoInventario> movimientos) {
        if (movimientos == null || movimientos.isEmpty()) {
            System.out.println("No hay movimientos registrados.");
            return;
        }
        System.out.println("\n--- Movimientos de inventario ---");
        for (MovimientoInventario m : movimientos) {
            System.out.println("ID: " + m.getIdMovimiento() +
                    " | Tipo: " + m.getTipo() +
                    " | Cantidad: " + m.getCantidad() +
                    " | Motivo: " + m.getMotivo() +
                    " | Producto: " + (m.getProducto() != null ? m.getProducto().getNombre() : "N/A") +
                    " | Proveedor: " + (m.getProveedor() != null ? m.getProveedor().getNombre() : "N/A"));
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