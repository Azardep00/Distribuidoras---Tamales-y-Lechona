package com.tamaleslechona.tamaleslechona.config;

import org.springframework.context.annotation.Configuration;

import io.swagger.v3.oas.annotations.OpenAPIDefinition;
import io.swagger.v3.oas.annotations.enums.SecuritySchemeType;
import io.swagger.v3.oas.annotations.info.Info;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.security.SecurityScheme;

// Datos generales de la documentación y esquema de seguridad JWT: este
// @SecurityScheme es lo que hace aparecer el botón "Authorize" en Swagger UI.
// El @SecurityRequirement global marca todos los endpoints como protegidos;
// los públicos (login, catálogo, etc.) lo anulan con @SecurityRequirements.
@Configuration
@OpenAPIDefinition(
        info = @Info(
                title = "API El Lechón - Tamales y Lechona",
                version = "1.0.0",
                description = "API REST del negocio: catálogo de productos, usuarios y sesión (JWT), "
                        + "pedidos con descuento de stock, inventario, proveedores y pagos Wompi. "
                        + "Persistencia en MongoDB. Para probar endpoints protegidos: haz login en "
                        + "POST /api/usuarios/login, copia el campo token, pulsa Authorize y pégalo."),
        security = @SecurityRequirement(name = "bearerAuth"))
@SecurityScheme(
        name = "bearerAuth",
        type = SecuritySchemeType.HTTP,
        scheme = "bearer",
        bearerFormat = "JWT",
        description = "Token JWT obtenido en POST /api/usuarios/login (solo el token, sin la palabra Bearer).")
public class OpenApiConfig {
}
