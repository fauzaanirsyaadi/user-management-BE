package com.usermanagement.usermanagementbe.security;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.time.Instant;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Component
public class LoginRateLimitFilter extends OncePerRequestFilter {

    private static class Attempt {
        int count;
        long windowStartMs;
    }

    private final Map<String, Attempt> attempts = new ConcurrentHashMap<>();

    private static final int MAX_ATTEMPTS = 5;
    private static final long WINDOW_MS = 5 * 60 * 1000; // 5 minutes

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
            throws ServletException, IOException {
        if (!"/api/auth/login".equals(request.getServletPath())) {
            filterChain.doFilter(request, response);
            return;
        }

        String key = getClientKey(request);
        long now = Instant.now().toEpochMilli();
        Attempt attempt = attempts.computeIfAbsent(key, k -> {
            Attempt a = new Attempt();
            a.count = 0;
            a.windowStartMs = now;
            return a;
        });

        if (now - attempt.windowStartMs > WINDOW_MS) {
            attempt.count = 0;
            attempt.windowStartMs = now;
        }

        attempt.count++;
        if (attempt.count > MAX_ATTEMPTS) {
            response.setStatus(429);
            response.setContentType(MediaType.APPLICATION_JSON_VALUE);
            response.getWriter().write("{\"error\":\"Too many login attempts. Please try again later.\"}");
            return;
        }

        filterChain.doFilter(request, response);
        if (response.getStatus() == 200) {
            attempts.remove(key);
        }
    }

    private String getClientKey(HttpServletRequest request) {
        String forwarded = request.getHeader("X-Forwarded-For");
        if (forwarded != null && !forwarded.isBlank()) {
            return forwarded.split(",")[0].trim();
        }
        return request.getRemoteAddr();
    }
}
