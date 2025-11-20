package upc.edu.muusmart.campaignmanagement.infrastructure.security;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import lombok.extern.slf4j.Slf4j;

import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

@Slf4j
@Component
public class JwtAuthenticationFilter extends OncePerRequestFilter {

    private final JwtUtil jwtUtil;

    public JwtAuthenticationFilter(JwtUtil jwtUtil) {
        this.jwtUtil = jwtUtil;
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
            throws ServletException, IOException {

        log.info("📥 Nueva solicitud recibida: {} {}", request.getMethod(), request.getRequestURI());

        final String authHeader = request.getHeader("Authorization");
        log.info("🔎 Encabezado Authorization recibido: {}", authHeader);

        String jwt = null;
        String username = null;

        if (authHeader != null && authHeader.startsWith("Bearer ")) {
            jwt = authHeader.substring(7);
            log.info("🪙 Token extraído: {}", jwt);

            try {
                username = jwtUtil.extractUsername(jwt);
                log.info("👤 Usuario extraído del token: {}", username);
            } catch (Exception e) {
                log.error("❌ Error al extraer usuario del token: {}", e.getMessage());
            }
        } else {
            log.warn("⚠️ No se encontró un token Bearer en la solicitud.");
        }

        if (username != null && SecurityContextHolder.getContext().getAuthentication() == null) {

            if (jwtUtil.validateToken(jwt)) {
                log.info("✅ Token válido.");

                // Procesar roles
                java.util.List<String> roles = new java.util.ArrayList<>();
                try {
                    Object rolesClaim = jwtUtil.extractClaim(jwt, claims -> claims.get("roles"));
                    if (rolesClaim == null)
                        rolesClaim = jwtUtil.extractClaim(jwt, claims -> claims.get("role"));

                    log.info("🔎 Claim roles sin procesar: {}", rolesClaim);

                    if (rolesClaim instanceof java.util.List<?> list) {
                        for (Object obj : list) {
                            roles.add(obj.toString());
                        }
                    } else if (rolesClaim instanceof String str) {
                        for (String r : str.split(",")) {
                            roles.add(r.trim());
                        }
                    }

                    log.info("📄 Lista de roles procesada: {}", roles);

                } catch (Exception e) {
                    log.error("❌ Error al procesar roles del token: {}", e.getMessage());
                }

                var authorities = new java.util.ArrayList<org.springframework.security.core.GrantedAuthority>();
                for (String role : roles) {
                    log.info("➡️ Añadiendo autoridad: {}", role);
                    authorities.add(new SimpleGrantedAuthority(role));
                }

                UsernamePasswordAuthenticationToken authToken =
                        new UsernamePasswordAuthenticationToken(username, null, authorities);

                authToken.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));

                SecurityContextHolder.getContext().setAuthentication(authToken);
                log.info("🔐 SecurityContext actualizado con el usuario '{}'", username);

            } else {
                log.error("❌ Token inválido o firma incorrecta.");
            }
        }

        filterChain.doFilter(request, response);
    }
}
