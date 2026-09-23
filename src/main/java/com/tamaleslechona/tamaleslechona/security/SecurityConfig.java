package com.tamaleslechona.tamaleslechona.security;

import java.util.List;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

@Configuration
public class SecurityConfig {

    private final JwtAuthenticationFilter jwtFilter;

    public SecurityConfig(JwtAuthenticationFilter jwtFilter) {
        this.jwtFilter = jwtFilter;
    }

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http
            // API sin sesión de navegador: cada petición se autentica sola con su
            // token, así que no hay cookie de sesión que proteger con CSRF.
            .csrf(AbstractHttpConfigurer::disable)
            .cors(cors -> cors.configurationSource(corsConfigurationSource()))
            .sessionManagement(s -> s.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
            .exceptionHandling(e -> e
                    .authenticationEntryPoint(new RespuestaJsonNoAutenticado())
                    .accessDeniedHandler(new RespuestaJsonSinPermiso()))
            .authorizeHttpRequests(auth -> auth
                // El navegador manda una petición OPTIONS antes de cada llamada
                // "real" (preflight de CORS); esa nunca debe pedir token.
                .requestMatchers(HttpMethod.OPTIONS, "/**").permitAll()

                // Público: cualquiera puede ver el catálogo, registrarse o loguearse.
                .requestMatchers(HttpMethod.GET, "/api/productos", "/api/productos/**").permitAll()
                .requestMatchers(HttpMethod.POST, "/api/usuarios/clientes").permitAll()
                .requestMatchers(HttpMethod.POST, "/api/usuarios/login").permitAll()
                .requestMatchers("/api/integracion/**").permitAll()
                .requestMatchers("/swagger-ui/**", "/v3/api-docs/**").permitAll()

                // Solo empleados: todo lo que es administración del negocio.
                .requestMatchers("/api/proveedores/**").hasRole("EMPLEADO")
                .requestMatchers("/api/movimientos/**").hasRole("EMPLEADO")
                .requestMatchers(HttpMethod.POST, "/api/productos/**").hasRole("EMPLEADO")
                .requestMatchers(HttpMethod.PUT, "/api/productos/**").hasRole("EMPLEADO")
                .requestMatchers(HttpMethod.DELETE, "/api/productos/**").hasRole("EMPLEADO")
                .requestMatchers(HttpMethod.PATCH, "/api/pedidos/*/estado").hasRole("EMPLEADO")
                .requestMatchers("/api/usuarios/empleados").hasRole("EMPLEADO")
                .requestMatchers(HttpMethod.GET, "/api/usuarios").hasRole("EMPLEADO")
                .requestMatchers(HttpMethod.GET, "/api/usuarios/buscar").hasRole("EMPLEADO")
                .requestMatchers(HttpMethod.DELETE, "/api/usuarios/*").hasRole("EMPLEADO")

                // Cualquier otra ruta bajo /api: basta con estar logueado
                // (cliente o empleado). Ej: crear pedido, ver mis pedidos, mi perfil.
                .requestMatchers("/api/**").authenticated()

                .anyRequest().permitAll())
            .addFilterBefore(jwtFilter, UsernamePasswordAuthenticationFilter.class);

        return http.build();
    }

    // Reemplaza al antiguo CorsConfig basado en WebMvcConfigurer: con Spring
    // Security activo, el CORS se debe registrar aquí para que se aplique
    // antes de que el filtro de seguridad rechace la petición.
    @Bean
    public CorsConfigurationSource corsConfigurationSource() {
        CorsConfiguration config = new CorsConfiguration();
        config.setAllowedOrigins(List.of("http://localhost:4200", "http://localhost:5173", "https://cliente-tamales.vercel.app"));
        config.setAllowedMethods(List.of("GET", "POST", "PUT", "PATCH", "DELETE", "OPTIONS"));
        config.setAllowedHeaders(List.of("*"));

        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", config);
        return source;
    }
}
