package pe.edu.sst.backend.security.filter;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicLong;

@Component
public class RateLimitingFilter extends OncePerRequestFilter {

    private static final int MAX_REQUESTS_PER_MINUTE = 60;
    private static final long TIME_WINDOW_MS = 60_000L;

    private static class ClientRequestRate {
        final AtomicInteger requestCount = new AtomicInteger(0);
        final AtomicLong windowStart = new AtomicLong(System.currentTimeMillis());
    }

    private final Map<String, ClientRequestRate> clientRates = new ConcurrentHashMap<>();

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
            throws ServletException, IOException {

        if ("OPTIONS".equalsIgnoreCase(request.getMethod())) {
            filterChain.doFilter(request, response);
            return;
        }

        String clientIp = getClientIP(request);
        long now = System.currentTimeMillis();

        ClientRequestRate rate = clientRates.computeIfAbsent(clientIp, k -> new ClientRequestRate());

        synchronized (rate) {
            if (now - rate.windowStart.get() > TIME_WINDOW_MS) {
                rate.windowStart.set(now);
                rate.requestCount.set(0);
            }

            if (rate.requestCount.incrementAndGet() > MAX_REQUESTS_PER_MINUTE) {
                response.setStatus(HttpStatus.TOO_MANY_REQUESTS.value());
                response.setContentType(MediaType.APPLICATION_JSON_VALUE);
                response.setCharacterEncoding("UTF-8");
                response.getWriter().write("""
                    {
                        "success": false,
                        "message": "Demasiadas peticiones enviadas al servidor. Por favor, espere un momento antes de reintentar."
                    }
                    """);
                return;
            }
        }

        filterChain.doFilter(request, response);
    }

    private String getClientIP(HttpServletRequest request) {
        String xfHeader = request.getHeader("X-Forwarded-For");
        if (xfHeader != null && !xfHeader.isBlank()) {
            return xfHeader.split(",")[0].trim();
        }
        return request.getRemoteAddr();
    }
}
