# Contrato de la API — Recurso `Usuario`

Diseñado ANTES de programar, siguiendo la metodología API First vista en clase.

## Recurso

`Usuario` es la clase base abstracta. Tiene dos tipos concretos: `Cliente` y `Empleado`.

La contraseña nunca se expone en las respuestas (es `write-only`): se envía en el
`POST` de creación, pero jamás vuelve en el JSON de salida.

## Endpoints

| Método | Endpoint                          | Descripción                                          |
|--------|-------------------------------------|-------------------------------------------------------|
| GET    | `/api/usuarios`                     | Lista usuarios activos (`?incluirInactivos=true`)      |
| GET    | `/api/usuarios/{id}`                | Obtiene un usuario por id                             |
| GET    | `/api/usuarios/buscar?correo=`      | Busca un usuario por correo exacto                    |
| POST   | `/api/usuarios/clientes`            | Registra un Cliente                                   |
| POST   | `/api/usuarios/empleados`           | Registra un Empleado                                  |
| PUT    | `/api/usuarios/{id}`                | Actualiza datos (nunca la contraseña)                 |
| PATCH  | `/api/usuarios/{id}/contrasena`     | Cambia la contraseña (requiere la actual)             |
| DELETE | `/api/usuarios/{id}`                | Desactiva (soft delete) el usuario                    |
| POST   | `/api/usuarios/login`               | Inicia sesión (correo + contraseña)                   |

## Ejemplo — POST /api/usuarios/clientes

Request body:
```json
{
  "nombre": "Laura",
  "apellido": "Ramírez",
  "telefono": "3001234567",
  "correo": "laura2@example.com",
  "contrasena": "clave123",
  "estado": true,
  "fechaNacimiento": "1998-05-12",
  "tipoCliente": "NUEVO",
  "direccion": "Cra 5 # 10-20, Ibagué",
  "fechaRegistro": "2026-09-13"
}
```

Respuesta `201 Created` (nótese que `contrasena` no aparece):
```json
{
  "idUsuario": 6,
  "nombre": "Laura",
  "apellido": "Ramírez",
  "telefono": "3001234567",
  "correo": "laura2@example.com",
  "estado": true,
  "fechaNacimiento": "1998-05-12",
  "tipoUsuario": "Cliente",
  "tipoCliente": "NUEVO",
  "direccion": "Cra 5 # 10-20, Ibagué",
  "fechaRegistro": "2026-09-13"
}
```

## Ejemplo — POST /api/usuarios/login

Request body:
```json
{ "correo": "laura@example.com", "contrasena": "clave123" }
```

Respuesta `200 OK`:
```json
{
  "idUsuario": 6,
  "nombre": "Laura",
  "apellido": "Ramírez",
  "correo": "laura@example.com",
  "tipoUsuario": "Cliente"
}
```

## Ejemplo — PATCH /api/usuarios/{id}/contrasena

Request body:
```json
{ "contrasenaActual": "clave123", "contrasenaNueva": "otraClave456" }
```

## Códigos de respuesta

- `200 OK` — operación exitosa (GET, PUT, login)
- `201 Created` — usuario creado (POST)
- `204 No Content` — operación exitosa sin cuerpo (DELETE, PATCH contraseña)
- `400 Bad Request` — validación fallida (nombre vacío, correo duplicado, contraseña < 6 caracteres)
- `401 Unauthorized` — login fallido (correo/contraseña incorrectos o usuario inactivo)
- `404 Not Found` — usuario no encontrado

## Pendiente para una fase posterior

Este recurso valida credenciales, pero **todavía no emite tokens de sesión (JWT)**
ni protege los demás endpoints por rol (admin/cliente). Eso lo dejamos para la fase
de seguridad, una vez tengamos también Pedido y Proveedor, ya que ahí es donde
realmente se necesita distinguir "lo que puede hacer un admin" de "lo que puede
hacer un cliente" en las dos interfaces separadas.
