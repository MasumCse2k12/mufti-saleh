package com.muftisaleh.common.audit;

import java.io.IOException;
import java.util.UUID;

import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.filter.OncePerRequestFilter;

import com.muftisaleh.security.AuthPrincipal;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

public class AuditContextFilter extends OncePerRequestFilter {

    private static final String REQUEST_ID_HEADER = "X-Request-Id";
    private static final String FORWARDED_FOR_HEADER = "X-Forwarded-For";
    private static final int REQUEST_ID_LENGTH = 12;

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain chain)
            throws ServletException, IOException {

        AuditActor actor = actorOf(request);
        try {
            ScopedValue.where(AuditContext.CURRENT, actor).call(() -> {
                chain.doFilter(request, response);
                return null;
            });
        } catch (IOException | ServletException | RuntimeException e) {
            throw e;
        } catch (Exception e) {
            throw new ServletException(e);
        }
    }

    private AuditActor actorOf(HttpServletRequest request) {
        String requestId = requestId(request);
        String ip = clientIp(request);
        String method = request.getMethod();
        String path = request.getRequestURI();

        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication != null && authentication.getPrincipal() instanceof AuthPrincipal principal) {
            return new AuditActor(principal.adminId(), ActorKind.ADMIN,
                    label(principal), requestId, ip, method, path);
        }
        return new AuditActor(null, ActorKind.ANONYMOUS, "anonymous", requestId, ip, method, path);
    }

    private String label(AuthPrincipal principal) {
        String name = principal.displayName();
        return name == null || name.isBlank() ? principal.adminId().toString() : name;
    }

    private String requestId(HttpServletRequest request) {
        String supplied = request.getHeader(REQUEST_ID_HEADER);
        if (supplied != null && !supplied.isBlank()) {
            return supplied.strip().substring(0, Math.min(supplied.strip().length(), 64));
        }
        return UUID.randomUUID().toString().replace("-", "").substring(0, REQUEST_ID_LENGTH);
    }

    private String clientIp(HttpServletRequest request) {
        String forwarded = request.getHeader(FORWARDED_FOR_HEADER);
        if (forwarded != null && !forwarded.isBlank()) {
            String first = forwarded.split(",")[0].strip();
            if (!first.isEmpty()) {
                return first.substring(0, Math.min(first.length(), 45));
            }
        }
        return request.getRemoteAddr();
    }
}
