package pe.edu.sst.backend.security.config;

import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.crypto.password.PasswordEncoder;
import pe.edu.sst.backend.security.filter.RateLimitingFilter;
import pe.edu.sst.backend.security.service.CustomUserDetailsService;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import pe.edu.sst.backend.security.filter.JwtAuthenticationFilter;
import pe.edu.sst.backend.security.jwt.JwtService;

import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

import java.util.List;

@Configuration
@RequiredArgsConstructor
public class SecurityConfig {

    private final CustomUserDetailsService userDetailsService;
    private final PasswordEncoder passwordEncoder;
    private final JwtService jwtService;
    private final RateLimitingFilter rateLimitingFilter;

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {

        http
                .cors(cors -> cors.configurationSource(corsConfigurationSource()))
                .csrf(csrf -> csrf.disable())

                .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))

                .authorizeHttpRequests(auth -> auth
                        .requestMatchers(
                                "/api/v1/auth/**",
                                "/swagger-ui/**",
                                "/v3/api-docs/**")
                        .permitAll()

                        // Reglas RBAC para Trabajadores: Administrador escribe, Supervisor solo lee, Trabajador consulta y sube documentos propios
                        .requestMatchers(HttpMethod.GET, "/api/v1/capacitaciones/**", "/api/v1/documentos/**", "/api/v1/dashboard/**")
                        .hasAnyRole("ADMINISTRADOR", "SUPERVISOR", "TRABAJADOR")

                        .requestMatchers(HttpMethod.GET, "/api/v1/trabajadores/**", "/api/v1/capacitadores/**", "/api/v1/inspecciones/**")
                        .hasAnyRole("ADMINISTRADOR", "SUPERVISOR")
  
                        .requestMatchers(HttpMethod.GET, "/api/v1/documentos/solicitudes/**")
                        .hasAnyRole("ADMINISTRADOR", "SUPERVISOR", "TRABAJADOR")

                        .requestMatchers(HttpMethod.POST, "/api/v1/documentos/personales")
                        .hasAnyRole("ADMINISTRADOR", "SUPERVISOR", "TRABAJADOR")

                        .requestMatchers(HttpMethod.POST, "/api/v1/documentos/solicitudes")
                        .hasRole("ADMINISTRADOR")

                        .requestMatchers(HttpMethod.POST, "/api/v1/documentos/solicitudes/*/subir")
                        .hasAnyRole("ADMINISTRADOR", "SUPERVISOR", "TRABAJADOR")

                        .requestMatchers(HttpMethod.POST, "/api/v1/trabajadores/**", "/api/v1/capacitaciones/**", "/api/v1/capacitadores/**", "/api/v1/inspecciones/**")
                        .hasAnyRole("ADMINISTRADOR", "SUPERVISOR")

                        .requestMatchers(HttpMethod.POST, "/api/v1/documentos/generales")
                        .hasRole("ADMINISTRADOR")
  
                        .requestMatchers(HttpMethod.PUT, "/api/v1/trabajadores/**", "/api/v1/capacitaciones/**", "/api/v1/capacitadores/**", "/api/v1/inspecciones/**", "/api/v1/documentos/**")
                        .hasRole("ADMINISTRADOR")
  
                        .requestMatchers(HttpMethod.PATCH, "/api/v1/trabajadores/**", "/api/v1/capacitaciones/**", "/api/v1/capacitadores/**", "/api/v1/inspecciones/**", "/api/v1/documentos/**")
                        .hasRole("ADMINISTRADOR")

                        .requestMatchers(HttpMethod.PATCH, "/api/v1/documentos/solicitudes/*/validar")
                        .hasRole("ADMINISTRADOR")
  
                        .requestMatchers(HttpMethod.DELETE, "/api/v1/trabajadores/**", "/api/v1/capacitaciones/**", "/api/v1/capacitadores/**", "/api/v1/inspecciones/**", "/api/v1/documentos/**")
                        .hasRole("ADMINISTRADOR")

                        .anyRequest().authenticated())

                .authenticationProvider(authenticationProvider())

                .addFilterBefore(rateLimitingFilter, UsernamePasswordAuthenticationFilter.class)
                .addFilterBefore(jwtAuthenticationFilter(), UsernamePasswordAuthenticationFilter.class);

        return http.build();
    }

    @Bean
    public CorsConfigurationSource corsConfigurationSource() {
        CorsConfiguration configuration = new CorsConfiguration();
        configuration.setAllowedOriginPatterns(List.of("*"));
        configuration.setAllowedMethods(List.of("GET", "POST", "PUT", "PATCH", "DELETE", "OPTIONS"));
        configuration.setAllowedHeaders(List.of("*"));
        configuration.setAllowCredentials(true);

        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", configuration);
        return source;
    }

    @Bean
    public AuthenticationManager authenticationManager(
            AuthenticationConfiguration configuration) throws Exception {
        return configuration.getAuthenticationManager();
    }

    @Bean
    @SuppressWarnings("deprecation")
    public DaoAuthenticationProvider authenticationProvider() {
        DaoAuthenticationProvider provider = new DaoAuthenticationProvider(passwordEncoder);
        provider.setUserDetailsService(userDetailsService);
        return provider;
    }

    @Bean
    public JwtAuthenticationFilter jwtAuthenticationFilter() {
        return new JwtAuthenticationFilter(jwtService, userDetailsService);
    }
}