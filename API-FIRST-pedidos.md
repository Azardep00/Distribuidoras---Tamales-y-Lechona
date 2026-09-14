# Contrato de la API — Recurso `Pedido`

Diseñado ANTES de programar, siguiendo la metodología API First vista en clase.

## Recurso

Un `Pedido` lo hace un `Cliente` y contiene una o más líneas (`DetallePedido`),
cada una con un `Producto` y una cantidad. Al crearse, descuenta stock; al
cancelarse, lo devuelve. Reutiliza `MovimientoInventarioService` para ambas
cosas, así que cada venta y cada cancelación quedan también registradas como
movimientos de inventario (con sus observadores de auditoría/alerta de stock).

Igual que `MovimientoInventario`, no se modela como un CRUD plano: no hay
`PUT` que reemplace el pedido completo. En su lugar hay **3 acciones de
negocio** (crear, cambiar de estado, cancelar), porque el pedido es una
máquina de estados, no un registro libre para editar.

## Estados

```
PENDIENTE -> CONFIRMADO -> EN_PREPARACION -> ENTREGADO
     \             \              \
      -------------- CANCELADO <---
```

Solo se puede avanzar un paso a la vez en la secuencia normal. `CANCELADO` es
alcanzable desde cualquier estado anterior a `ENTREGADO`, pero solo a través
de `DELETE` (no de `PATCH /estado`).

## Endpoints

| Método | Endpoint                       | Descripción                                          |
|--------|----------------------------------|--------------------------------------------------------|
| GET    | `/api/pedidos`                   | Lista todos los pedidos                                |
| GET    | `/api/pedidos?idCliente=`        | Filtra por cliente                                     |
| GET    | `/api/pedidos?estado=`           | Filtra por estado                                      |
| GET    | `/api/pedidos/{id}`              | Obtiene un pedido por id                               |
| POST   | `/api/pedidos`                   | Crea un pedido (descuenta stock)                       |
| PATCH  | `/api/pedidos/{id}/estado`       | Avanza al siguiente estado de la secuencia normal      |
| DELETE | `/api/pedidos/{id}`              | Cancela el pedido (devuelve el stock descontado)       |

## Ejemplo — POST /api/pedidos

Request body:
```json
{
  "idCliente": 6,
  "detalles": [
    { "idProducto": 1, "cantidad": 2 },
    { "idProducto": 4, "cantidad": 1 }
  ]
}
```

Respuesta `201 Created`:
```json
{
  "idPedido": 1,
  "cliente": { "idUsuario": 6, "nombre": "Laura", "...": "..." },
  "estado": "PENDIENTE",
  "fecha": "2026-09-14T10:00:00",
  "detalles": [
    { "idDetallePedido": 1, "producto": { "idProducto": 1, "...": "..." }, "cantidad": 2, "precioUnitario": 8000, "subtotal": 16000 },
    { "idDetallePedido": 2, "producto": { "idProducto": 4, "...": "..." }, "cantidad": 1, "precioUnitario": 150000, "subtotal": 150000 }
  ],
  "total": 166000
}
```

## Ejemplo — PATCH /api/pedidos/{id}/estado

Request body:
```json
{ "estado": "CONFIRMADO" }
```

## Códigos de respuesta

- `200 OK` — operación exitosa (GET, PATCH)
- `201 Created` — pedido creado (POST)
- `204 No Content` — pedido cancelado (DELETE)
- `400 Bad Request` — sin productos, cantidad ≤ 0, stock insuficiente, cliente inactivo/inexistente,
  transición de estado inválida, o cancelación de un pedido ya `ENTREGADO`/`CANCELADO`
- `404 Not Found` — pedido, cliente o producto no encontrado

## Nota

`precioUnitario` en cada detalle es el precio del producto **al momento de crear
el pedido**, no el precio actual. Así, si el precio de un producto cambia
después, los pedidos históricos no se ven afectados.