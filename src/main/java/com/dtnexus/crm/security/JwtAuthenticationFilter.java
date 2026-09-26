package com.dtnexus.crm.security;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.JwtException;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.http.HttpHeaders;
import org.springframework.lang.NonNull;
import org.springframework.lang.Nullable;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;
import com.dtnexus.crm.service.TrialAccessService;

import java.io.IOException;
import java.util.Collection;
import java.util.List;
import java.util.Map;

@Component
public class JwtAuthenticationFilter extends OncePerRequestFilter {

    private final JwtService jwtService;
    private final TrialAccessService trialAccessService;

    public JwtAuthenticationFilter(JwtService jwtService, TrialAccessService trialAccessService) {
        this.jwtService = jwtService;
        this.trialAccessService = trialAccessService;
    }

    @SuppressWarnings({ "deprecation", "null" })
	@Override
    protected void doFilterInternal(
            @Nullable HttpServletRequest request,
            @Nullable HttpServletResponse response,
            @Nullable @NonNull FilterChain filterChain) throws ServletException, IOException {
        String authorization = request.getHeader(HttpHeaders.AUTHORIZATION);
        if (authorization == null || !authorization.startsWith("Bearer ")) {
            var cookie = request.getCookies() == null ? null : java.util.Arrays.stream(request.getCookies())
                .filter(item -> "DT_NEXUS_ACCESS_TOKEN".equals(item.getName()))
                .findFirst()
                .orElse(null);
            if (cookie != null) authorization = "Bearer " + cookie.getValue();
        }
        if (authorization == null || !authorization.startsWith("Bearer ")) {
            filterChain.doFilter(request, response);
            return;
        }

        try {
            Claims claims = jwtService.parse(authorization.substring(7).trim());
            String username = claims.getSubject();
            if (username == null || username.isBlank()) {
                unauthorized(response, "Token sem subject");
                return;
            }

            Collection<SimpleGrantedAuthority> authorities = roles(claims.get("roles"));
            UsernamePasswordAuthenticationToken authentication =
                    new UsernamePasswordAuthenticationToken(username, null, authorities);
                String tenant = claims.get("tenant", String.class);
                if (tenant == null || tenant.isBlank() || !trialAccessService.hasAccess(username, tenant)) {
                    response.setStatus(HttpServletResponse.SC_PAYMENT_REQUIRED);
                    response.setContentType("application/json");
                    response.getWriter().write("{\"title\":\"Assinatura necessária\",\"detail\":\"O período de teste expirou.\"}");
                    return;
                }
                authentication.setDetails(Map.of("tenant", tenant));
            SecurityContextHolder.getContext().setAuthentication(authentication);
            filterChain.doFilter(request, response);
        } catch (JwtException | IllegalArgumentException | IllegalStateException exception) {
            SecurityContextHolder.clearContext();
            unauthorized(response, "Token inválido ou expirado");
        }
    }

    private Collection<SimpleGrantedAuthority> roles(Object rawRoles) {
        if (!(rawRoles instanceof List<?> roleList)) {
            return List.of();
        }
        return roleList.stream()
                .filter(String.class::isInstance)
                .map(String.class::cast)
                .map(role -> role.startsWith("ROLE_") ? role : "ROLE_" + role)
                .map(SimpleGrantedAuthority::new)
                .toList();
    }

    private void unauthorized(HttpServletResponse response, String message) throws IOException {
        response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
        response.setContentType("application/json");
        response.getWriter().write("{\"title\":\"Não autorizado\",\"detail\":\"" + message + "\"}");
    }
}
