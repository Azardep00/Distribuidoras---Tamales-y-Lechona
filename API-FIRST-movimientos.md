# Contrato de la API — Recurso `MovimientoInventario`

Diseñado ANTES de programar, siguiendo la metodología API First vista en clase.

## Recurso

Un `MovimientoInventario` registra una entrada o salida de stock sobre un `Producto`.
Las entradas normales van asociadas a un `Proveedor`; las reversiones y salidas no.

No hay un endpoint genérico de creación: se modela como **3 acciones de negocio**
(entrada, salida, reversión), no como un CRUD plano, porque cada una tiene reglas
distintas (una entrada exige proveedor, una salida valida stock disponible, etc.).

## Endpoints

| Método | Endpoint                              | Descripción                                    |
|--------|------------------------------------------|--------------------------------------------------|
| GET    | `/api/movimientos`                       | Lista todos los movimientos                       |
| GET    | `/api/movimientos?idProducto=`           | Filtra por producto                               |
| GET    | `/api/movimientos?tipo=ENTRADA\|SALIDA`  | Filtra por tipo                                   |
| GET    | `/api/movimientos/{id}`                  | Obtiene un movimiento por id                      |
| POST   | `/api/movimientos/entradas`              | Registra una entrada (aumenta stock)              |
| POST   | `/api/movimientos/salidas`               | Registra una salida (descuenta stock)             |
| POST   | `/api/movimientos/reversiones`           | Registra una reversión (aumenta stock, sin proveedor) |

## Ejemplo — POST /api/movimientos/entradas

Request body:
```json
{
  "idProducto": 1,
  "idProveedor": 1,
  "cantidad": 30,
  "motivo": "Reabastecimiento semanal"
}
```

Respuesta `201 Created`:
```json
{
  "idMovimiento": 1,
  "tipo": "ENTRADA",
  "cantidad": 30,
  "fecha": "2026-09-13T22:10:00",
  "motivo": "Reabastecimiento semanal",
  "producto": { "idProducto": 1, "nombre": "Tamal normal grande", "stock": 50, "...": "..." },
  "proveedor": { "idProveedor": 1, "nombre": "Distribuidora El Campesino", "...": "..." }
}
```

## Ejemplo — POST /api/movimientos/salidas

Request body:
```json
{ "idProducto": 1, "cantidad": 5, "motivo": "Venta mostrador" }
```

## Códigos de respuesta

- `200 OK` — operación exitosa (GET)
- `201 Created` — movimiento registrado (POST)
- `400 Bad Request` — cantidad ≤ 0, o stock insuficiente para una salida
- `404 Not Found` — producto o proveedor no encontrado

## Patrón Observer

Cada vez que se guarda un movimiento, se notifica a todos los observadores
registrados (`AuditoriaInventario`, `AlertaStock`), igual que en el proyecto de
escritorio. En Spring no hace falta una lista manual de observers: cualquier
clase anotada `@Component` que implemente `InventarioObserver` se inyecta sola
en `MovimientoInventarioService`.
