package com.mvs.api_gateway.config;

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
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Slf4j
public class RoleBasedAccessFilter implements GlobalFilter, Ordered {

    Map<String, List<String>> roleRules;
    private final AntPathMatcher antPathMatcher = new AntPathMatcher();
    //private final List<String> openPaths = List.of("/api/user/login", "/api/user/register", "/actuator/**", "/api/payment/webhook");
    List<String> openPaths;

    public RoleBasedAccessFilter(Map<String, List<String>> roleRules, List<String> openPaths) {
        this.roleRules = roleRules;
        this.openPaths = openPaths;
    }

    @Override
    public Mono<Void> filter(ServerWebExchange exchange, GatewayFilterChain chain) {
        String path = exchange.getRequest().getPath().value();
        String role = exchange.getRequest().getHeaders().getFirst("X-User-Role");
        String method = exchange.getRequest().getMethod().toString();
        String request = method + ":" + path;

        for (String openPath : openPaths) {
            if (antPathMatcher.match(openPath, path)) {
                return chain.filter(exchange);
            }
        }

        /*if (role == null) {
            return unauthorized(exchange, "Missing user role");
        }*/

        for (Map.Entry<String, List<String>> entry : roleRules.entrySet()) {
            if (antPathMatcher.match(entry.getKey(), request)) {
                List<String> allowedRoles = entry.getValue();
                if (allowedRoles.contains("PERMIT_ALL")) {
                    log.info("path is not restricted");
                    return chain.filter(exchange);
                }
                if (role == null) {
                    return unauthorized(exchange, "Authorization header is required");
                }
                if (!allowedRoles.contains(role)) {
                    log.warn("Access denied request = {} role = {}", request, role);
                    return forbidden(exchange, "Access denied");
                }

                return  chain.filter(exchange);
            }
        }

        return forbidden(exchange, "Access denied");
    }

    @Override
    public int getOrder() {
        return -90;
    }

    private Mono<Void> unauthorized(ServerWebExchange exchange, String message) {
        exchange.getResponse().setStatusCode(HttpStatus.UNAUTHORIZED);
        exchange.getResponse().getHeaders().setContentType(MediaType.APPLICATION_JSON);
        return exchange.getResponse().writeWith(Mono.just(exchange.getResponse()
                .bufferFactory().wrap(("{\"error\": \"" + escapeJson(message) + "\"}")
                        .getBytes(StandardCharsets.UTF_8))));
    }

    private Mono<Void> forbidden(ServerWebExchange exchange, String message) {
        exchange.getResponse().setStatusCode(HttpStatus.FORBIDDEN);
        exchange.getResponse().getHeaders().setContentType(MediaType.APPLICATION_JSON);
        return exchange.getResponse().writeWith(Mono.just(exchange.getResponse()
                .bufferFactory().wrap(("{\"error\": \"" + escapeJson(message) + "\"}")
                        .getBytes(StandardCharsets.UTF_8))));
    }

    private String escapeJson(String s) {
        return s.replace("\"", "\\\"");
    }
}
