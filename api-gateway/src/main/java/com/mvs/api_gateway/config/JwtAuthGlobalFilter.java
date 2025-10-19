package com.mvs.api_gateway.config;

import com.mvs.api_gateway.util.JwtUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cloud.gateway.filter.GatewayFilterChain;
import org.springframework.cloud.gateway.filter.GlobalFilter;
import org.springframework.core.Ordered;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.util.AntPathMatcher;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

import java.nio.charset.StandardCharsets;
import java.util.List;

@Slf4j
@Component
@RequiredArgsConstructor
public class JwtAuthGlobalFilter implements GlobalFilter, Ordered {

    private final JwtUtil jwtUtil;
    private final List<String> openPaths = List.of("/api/user/login", "/api/user/register", "/actuator/**");
    private final AntPathMatcher antPathMatcher = new AntPathMatcher();

    @Override
    public Mono<Void> filter(ServerWebExchange exchange, GatewayFilterChain chain) {
        String path = exchange.getRequest().getPath().value();
        log.info("path {}", path);

        for (String openPath : openPaths) {
            if (antPathMatcher.match(openPath, path)) {
                return chain.filter(exchange);
            }
        }

        List<String> authHeaders = exchange.getRequest().getHeaders().getOrEmpty("Authorization");
        if (authHeaders.isEmpty()) {
            return unauthorized(exchange, "Authorization header is required");
        }
        String token = authHeaders.get(0);
        if (token.contains("Bearer ")) {
            token = token.substring(7);
        }
        try {
            if (!jwtUtil.validateToken(token)) {
                return unauthorized(exchange, "Invalid or expired token");
            }
            String userId = jwtUtil.extractUserId(token);
            String role = jwtUtil.extractUserRole(token);

            ServerWebExchange mutated = exchange.mutate()
                    .request(r -> r.headers(httpHeaders -> {
                        httpHeaders.add("X-User-Id", userId);
                        if (role != null) httpHeaders.add("X-User-Role", role);
                    })).build();

            return chain.filter(mutated);

        } catch (Exception e) {
            return unauthorized(exchange, "Token validation failed: " + e.getMessage());
        }
    }

    private Mono<Void> unauthorized(ServerWebExchange exchange, String message) {
        exchange.getResponse().setStatusCode(HttpStatus.UNAUTHORIZED);
        exchange.getResponse().getHeaders().setContentType(MediaType.APPLICATION_JSON);
        exchange.getResponse().getHeaders().add("WWW-Authenticate", "Bearer");
        String body = String.format("{\"error\": \"%s\"}", message.replace("\"", "\\\""));
        byte[] bytes = body.getBytes(StandardCharsets.UTF_8);
        return exchange.getResponse()
                .writeWith(Mono.just(exchange.getResponse().bufferFactory().wrap(bytes)))
                .then();
    }


    @Override
    public int getOrder() {
        return -100;
    }
}
