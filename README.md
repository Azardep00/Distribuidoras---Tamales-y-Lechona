# Distribuidora de Tamales y Lechona 🫔

Sistema de gestión en Java para una distribuidora tolimense de tamales y lechona. Permite administrar productos, proveedores, movimientos de inventario y pedidos de clientes, e incluye una pasarela de pagos real (Wompi) mediante el patrón Adapter.

Proyecto académico de Programación Orientada a Objetos (Ingeniería de Sistemas, Universidad de Ibagué), aplicando arquitectura MVC y principios SOLID.

## Características

- **Gestión de productos**: tamales (normal/picante, en tres tamaños) y lechonas (por tamaño y número de porciones), cada uno con su propia lógica de precio.
- **Gestión de proveedores**: registro, consulta, actualización y eliminación.
- **Movimientos de inventario**: entradas (asociadas a un proveedor) y salidas, con control de stock disponible.
- **Gestión de pedidos**: creación de pedidos por cliente, cálculo de totales, cambios de estado (pendiente, entregado, cancelado) y actualización de datos del pedido.
- **Gestión de usuarios**: jerarquía `Usuario` → `Cliente` / `Empleado`, con inicio de sesión y contraseñas almacenadas con hash SHA-256.
- **Pago en línea**: generación de un link de pago real a través de la API de Wompi (sandbox), integrado mediante el patrón Adapter.
- **Menú interactivo por consola** (`Main.java`) para probar todos los flujos anteriores.

## Arquitectura

El proyecto sigue el patrón **MVC** con separación por paquetes:

```
src/
├── Main.java          # Punto de entrada y menús de consola
├── model/             # Entidades de negocio y reglas de dominio
├── controller/        # Lógica de aplicación (orquesta modelos y repositorios)
├── repository/        # Persistencia en memoria, tras interfaces
├── view/               # Presentación en consola (impresión de resultados)
└── pagos/              # Integración con la pasarela de pago Wompi
```

### Principios y patrones aplicados

- **Singleton** — `Distribuidora` garantiza una única instancia de la empresa en todo el sistema.
- **Adapter** — `ProcesadorPago` / `AdaptadorPagoWompi` traducen el modelo de pagos interno al formato que exige la API de Wompi, sin acoplar el resto del sistema a un proveedor de pagos específico.
- **DIP (Inversión de dependencias)** — `ProveedorController` y `MovimientoInventarioController` reciben su repositorio (`IProveedorRepository`, `IMovimientoInventarioRepository`) por constructor, en lugar de depender de una implementación concreta.
- **SRP** — los controllers ya no imprimen en consola; esa responsabilidad se delega a las clases `view`.
- **ISP** — `ActualizablePedido`, `IActualizableProducto` e `IActualizableUsuario` mantienen a cada clase actualizando únicamente sus propios datos.
- **OCP** — `Producto` es abstracta y delega `calcularPrecio()` en cada subtipo (`Tamal`, `Lechona`), permitiendo agregar nuevos productos sin modificar la clase base.
- **Herencia** — `Cliente` y `Empleado` extienden `Usuario`; `Tamal` y `Lechona` extienden `Producto`.

## Requisitos

- **Java 26** (JDK) — según `pom.xml`. Si tu entorno no tiene esta versión, ajusta `maven.compiler.source`/`target` en el `pom.xml` a la que tengas disponible (por ejemplo 17 o 21).
- **Maven** para gestionar la dependencia `org.json`.

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
3. Ejecuta la clase `Main`:
   ```bash
   mvn exec:java -Dexec.mainClass="Main"
   ```
   o desde tu IDE (IntelliJ IDEA), ejecutando directamente `src/Main.java`.

## Uso

Al iniciar, el programa precarga un catálogo de tamales y lechonas y muestra un menú principal:

```
===== DISTRIBUIDORA =====
1. PROVEEDOR
2. MOVIMIENTO INVENTARIO
3. PROBAR PAGO (Adapter Wompi)
4. SALIR
```

- **Proveedor**: registrar, consultar, actualizar o eliminar proveedores.
- **Movimiento inventario**: registrar entradas o salidas de producto, consultar stock y ver el historial de movimientos.
- **Probar pago**: genera un pedido de ejemplo y solicita a Wompi (sandbox) un link de pago real para ese pedido.

## Pagos con Wompi

La integración usa la API de **Payment Links** de Wompi en modo sandbox (`https://sandbox.wompi.co/v1/payment_links`). Actualmente la llave privada de pruebas está escrita directamente en `WompiApiClient.java`.

> ⚠️ **Recomendación**: antes de publicar el repositorio o pasar a producción, mueve la llave a una variable de entorno o archivo de configuración excluido del control de versiones (por ejemplo, con `System.getenv("WOMPI_PRIVATE_KEY")`), y usa una llave de producción solo en un entorno seguro.

## Estado del proyecto

Este es un proyecto en evolución: la base actual cubre la gestión administrativa (proveedores, inventario, productos y pagos). Está previsto extenderlo con un flujo completo orientado al cliente (registro, autenticación y creación de pedidos desde la perspectiva del comprador), construyendo sobre `Cliente.realizarPedido()`.

