# Contrato de la API — Recurso `Proveedor`

Diseñado ANTES de programar, siguiendo la metodología API First vista en clase.

## Recurso

`Proveedor` es una entidad simple, sin subtipos (a diferencia de Producto y Usuario).

## Endpoints

| Método | Endpoint                     | Descripción                                    |
|--------|--------------------------------|-------------------------------------------------|
| GET    | `/api/proveedores`             | Lista proveedores activos (`?incluirInactivos=true`) |
| GET    | `/api/proveedores/{id}`        | Obtiene un proveedor por id                     |
| GET    | `/api/proveedores/buscar?q=`   | Busca por nombre, teléfono o correo             |
| POST   | `/api/proveedores`             | Registra un proveedor                           |
| PUT    | `/api/proveedores/{id}`        | Actualiza nombre/teléfono/correo/dirección      |
| DELETE | `/api/proveedores/{id}`        | Desactiva (soft delete) el proveedor            |

## Ejemplo — POST /api/proveedores

Request body:
```json
{
  "nombre": "Distribuidora El Campesino",
  "telefono": "3151234567",
  "correo": "contacto@elcampesino.com",
  "direccion": "Vía Ibagué - Alvarado km 4"
}
```

Respuesta `201 Created`:
```json
{
  "idProveedor": 1,
  "nombre": "Distribuidora El Campesino",
  "telefono": "3151234567",
  "correo": "contacto@elcampesino.com",
  "direccion": "Vía Ibagué - Alvarado km 4",
  "estado": true
}
```

## Códigos de respuesta

- `200 OK` — operación exitosa (GET, PUT)
- `201 Created` — proveedor creado (POST)
- `204 No Content` — desactivado (DELETE)
- `400 Bad Request` — validación fallida (nombre/teléfono vacío, correo sin `@`)
- `404 Not Found` — proveedor no encontrado

## Nota

`estado` siempre se fuerza a `true` al registrar (se ignora si viene en el body);
solo se apaga mediante `DELETE` (soft delete), igual que en Producto.