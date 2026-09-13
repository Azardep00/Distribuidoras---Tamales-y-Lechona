# Contrato de la API — Recurso `Producto`

Diseñado ANTES de programar, siguiendo la metodología API First vista en clase.

## Recurso

`Producto` es la clase base abstracta. Tiene dos tipos concretos: `Tamal` y `Lechona`.

## Endpoints

| Método | Endpoint                          | Descripción                                      |
|--------|------------------------------------|---------------------------------------------------|
| GET    | `/api/productos`                   | Lista productos activos (?incluirInactivos=true)  |
| GET    | `/api/productos/{id}`              | Obtiene un producto por id                        |
| GET    | `/api/productos/buscar?nombre=`    | Busca productos por nombre                        |
| POST   | `/api/productos/tamales`           | Crea un Tamal                                     |
| POST   | `/api/productos/lechonas`          | Crea una Lechona                                  |
| PUT    | `/api/productos/{id}`              | Actualiza nombre/descripción/precio y atributos propios del tipo |
| DELETE | `/api/productos/{id}`              | Desactiva (soft delete) el producto               |

## Ejemplo — POST /api/productos/tamales

Request body:
```json
{
  "nombre": "Tamal normal grande",
  "descripcion": "Tamal tradicional tamaño grande",
  "precio": 8000,
  "stock": 20,
  "estado": true,
  "tipo": "NORMAL",
  "tamano": "GRANDE"
}
```

Respuesta `201 Created`:
```json
{
  "idProducto": 1,
  "nombre": "Tamal normal grande",
  "descripcion": "Tamal tradicional tamaño grande",
  "precio": 8000,
  "stock": 20,
  "estado": true,
  "tipoProducto": "Tamal",
  "detalleEspecifico": "NORMAL · GRANDE"
}
```

## Códigos de respuesta

- `200 OK` — operación exitosa (GET, PUT, DELETE)
- `201 Created` — producto creado (POST)
- `400 Bad Request` — validación fallida (nombre vacío, precio <= 0, stock negativo)
- `404 Not Found` — producto no encontrado
