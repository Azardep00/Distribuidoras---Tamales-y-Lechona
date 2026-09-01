# Distribuidora de Tamales y Lechona 🫔

Sistema de gestión en Java para una distribuidora tolimense de tamales y lechona. Permite administrar productos, proveedores, movimientos de inventario, usuarios (clientes y empleados) y pedidos, e incluye una pasarela de pagos real (Wompi) mediante el patrón Adapter.

Proyecto académico de Programación Orientada a Objetos (Ingeniería de Sistemas, Universidad de Ibagué), aplicando arquitectura MVC y principios SOLID. Tiene **dos formas de usarlo**: un menú interactivo por consola y una interfaz gráfica de escritorio (Swing) que expone los mismos CRUD de forma visual.

## Características

- **Gestión de productos**: tamales (normal/picante, en tres tamaños) y lechonas (por tamaño y número de porciones), cada uno con su propia lógica de precio.
- **Gestión de proveedores**: registro, consulta, actualización y eliminación.
- **Movimientos de inventario**: entradas (asociadas a un proveedor) y salidas, con control de stock disponible.
- **Gestión de usuarios**: jerarquía `Usuario` → `Cliente` / `Empleado`, con inicio de sesión y contraseñas almacenadas con hash SHA-256; CRUD completo desde consola o GUI.
- **Gestión de pedidos**: un pedido nace siempre a través de `Cliente.realizarPedido(...)`; luego se puede listar, actualizar (pago, método de pago, dirección de entrega) o eliminar.
- **Pago en línea**: generación de un link de pago real a través de la API de Wompi (sandbox), integrado mediante el patrón Adapter.
- **Dos interfaces sobre la misma lógica de negocio**:
  - **Consola** (`Main.java`): menú interactivo clásico con `Scanner`.
  - **GUI de escritorio** (`gui.MainFrame`, Swing): una pestaña por cada CRUD (Proveedor, Movimiento de Inventario, Producto, Usuario, Pedido), con tablas, formularios y los mismos controllers/modelos de la consola — ninguna regla de negocio se duplicó ni se reescribió.

## Arquitectura

El proyecto sigue el patrón **MVC** con separación por paquetes:

```
src/
├── Main.java          # Punto de entrada y menús de consola
├── model/             # Entidades de negocio y reglas de dominio
├── controller/        # Lógica de aplicación (orquesta modelos y repositorios)
├── repository/        # Persistencia en memoria, tras interfaces
├── view/              # Presentación en consola (impresión de resultados)
├── gui/               # Interfaz gráfica de escritorio (Swing), un panel por CRUD
├── observer/          # Observadores de eventos sobre Pedido
└── pagos/             # Integración con la pasarela de pago Wompi
```

`gui/` es una capa de presentación alternativa a `view/`: llama exactamente a los mismos `controller` y `model` que usa `Main.java`, así que cualquier cambio en las reglas de negocio se refleja automáticamente en ambas interfaces.

- `gui/MainFrame.java` — ventana principal; crea los controllers, siembra el catálogo inicial de productos y organiza las pestañas.
- `gui/ProveedorPanel.java` — CRUD de proveedores.
- `gui/ProductoPanel.java` — CRUD de productos (Tamal/Lechona).
- `gui/MovimientoInventarioPanel.java` — entradas, salidas, stock y bitácora de movimientos.
- `gui/UsuarioPanel.java` — CRUD de usuarios (Cliente/Empleado).
- `gui/PedidoPanel.java` — creación de pedidos desde un cliente existente, listar/actualizar/eliminar y prueba de pago con Wompi.

### Principios y patrones aplicados

- **Singleton** — `Distribuidora` garantiza una única instancia de la empresa en todo el sistema.
- **Adapter** — `ProcesadorPago` / `AdaptadorPagoWompi` traducen el modelo de pagos interno al formato que exige la API de Wompi, sin acoplar el resto del sistema a un proveedor de pagos específico.
- **DIP (Inversión de dependencias)** — `ProveedorController` y `MovimientoInventarioController` reciben su repositorio (`IProveedorRepository`, `IMovimientoInventarioRepository`) por constructor, en lugar de depender de una implementación concreta.
- **SRP** — los controllers no imprimen ni dibujan nada; esa responsabilidad se delega a las clases `view` (consola) o `gui` (Swing).
- **ISP** — `ActualizablePedido`, `IActualizableProducto` e `IActualizableUsuario` mantienen a cada clase actualizando únicamente sus propios datos.
- **OCP** — `Producto` es abstracta y delega `calcularPrecio()` en cada subtipo (`Tamal`, `Lechona`), permitiendo agregar nuevos productos sin modificar la clase base.
- **Herencia** — `Cliente` y `Empleado` extienden `Usuario`; `Tamal` y `Lechona` extienden `Producto`.
- **Observer** — `Pedido` notifica a sus observadores (`AuditoriaPedido`, `NotificacionPedido`) ante cambios relevantes.

## Requisitos

- **JDK 21 o superior** (el proyecto compila y corre sobre OpenJDK 21; si usas el `pom.xml` tal cual, ajusta `maven.compiler.source`/`target` a la versión de JDK que tengas instalada).
- **Maven** para gestionar la dependencia `org.json` (usada por la integración con Wompi).
- Para la GUI: un entorno con soporte gráfico (Windows, macOS o Linux con servidor X11/Wayland). No requiere librerías adicionales; usa Swing, incluido en el JDK.

## Instalación y ejecución

1. Clona el repositorio:
   ```bash
   git clone <url-del-repositorio>
   cd Distribuidoras---Tamales-y-Lechona
   ```
2. Compila el proyecto con Maven:
   ```bash
   mvn compile
   ```
3. Ejecuta la interfaz que prefieras:

   - **Consola** (menú clásico con `Scanner`):
     ```bash
     mvn exec:java -Dexec.mainClass="Main"
     ```
   - **Interfaz gráfica** (Swing):
     ```bash
     mvn exec:java -Dexec.mainClass="gui.MainFrame"
     ```

   También puedes ejecutar `src/Main.java` o `src/gui/MainFrame.java` directamente desde tu IDE (IntelliJ IDEA).

## Uso

### Consola

Al iniciar, el programa precarga un catálogo de tamales y lechonas y muestra el menú principal:

```
===== DISTRIBUIDORA =====
1. PROVEEDOR
2. MOVIMIENTO INVENTARIO
3. PRODUCTO
4. USUARIO
5. PEDIDO
6. PROBAR PAGO (Adapter Wompi)
7. SALIR
```

- **Proveedor**: registrar, consultar, actualizar o eliminar proveedores.
- **Movimiento inventario**: registrar entradas (asociadas a un proveedor) o salidas de producto, consultar stock y ver el historial de movimientos.
- **Producto**: registrar, listar, actualizar o eliminar tamales y lechonas.
- **Usuario**: registrar clientes o empleados, listarlos, actualizarlos o eliminarlos.
- **Pedido**: listar, actualizar (pago, método de pago, dirección) o eliminar pedidos ya creados. Los pedidos nuevos se generan a través de un cliente (ver "Probar pago").
- **Probar pago**: crea un pedido de ejemplo para un cliente y solicita a Wompi (sandbox) un link de pago real para ese pedido.

### Interfaz gráfica

Al abrir `gui.MainFrame` verás una ventana con una pestaña por cada menú anterior (Proveedor, Movimiento Inventario, Producto, Usuario, Pedido). Cada pestaña tiene un formulario para capturar datos, botones equivalentes a las opciones de consola y una tabla que se actualiza automáticamente después de cada operación. En la pestaña **Pedido** puedes elegir un cliente ya registrado en la pestaña **Usuario**, crear un pedido para él y, con un pedido seleccionado en la tabla, probar la generación de un link de pago con Wompi.

## Pagos con Wompi

La integración usa la API de **Payment Links** de Wompi en modo sandbox (`https://sandbox.wompi.co/v1/payment_links`). Actualmente la llave privada de pruebas está escrita directamente en `WompiApiClient.java`.

## Estado del proyecto

La base actual cubre la gestión administrativa completa (proveedores, inventario, productos, usuarios y pedidos) y los pagos, disponibles tanto por consola como por interfaz gráfica de escritorio. Queda pendiente extenderlo con un flujo de autenticación real orientado al cliente (login desde la GUI, sesión persistente) y con persistencia en base de datos, ya que actualmente los repositorios trabajan en memoria.
