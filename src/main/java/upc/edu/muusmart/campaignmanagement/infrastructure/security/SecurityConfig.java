package upc.edu.muusmart.campaignmanagement.infrastructure.security;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

@Slf4j
@Configuration
@RequiredArgsConstructor
@EnableMethodSecurity
public class SecurityConfig {

    private final JwtAuthenticationFilter jwtAuthenticationFilter;

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {

        log.info("🛡️ Iniciando configuración de seguridad del microservicio de campañas…");

        http
                .csrf(csrf -> {
                    csrf.disable();
                    log.info("⚠️ CSRF deshabilitado (API stateless).");
                })
                .sessionManagement(sm -> sm.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                .authorizeHttpRequests(auth -> {

                    log.info("🔐 Configurando reglas de autorización…");

                    auth
                            .requestMatchers("/stables/**").hasAnyAuthority("ROLE_USER", "ROLE_ADMIN")
                            .requestMatchers("/campaigns/**").hasAnyAuthority("ROLE_USER", "ROLE_ADMIN")
                            .requestMatchers(
                                    "/v3/api-docs/**",
                                    "/swagger-ui/**",
                                    "/swagger-ui.html"
                            ).permitAll()
                            .anyRequest().authenticated();

                    log.info("✅ Reglas de autorización configuradas correctamente.");
                })
                .addFilterBefore(jwtAuthenticationFilter, UsernamePasswordAuthenticationFilter.class);

        log.info("🛡️ SecurityFilterChain construido correctamente.");

        return http.build();
    }
}
