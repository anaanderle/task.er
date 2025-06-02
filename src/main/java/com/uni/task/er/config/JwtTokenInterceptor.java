package com.uni.task.er.config;

import com.uni.task.er.exception.custom.UnauthorizedException;
import com.uni.task.er.model.User;
import com.uni.task.er.service.AuthService;
import com.uni.task.er.service.JwtBlocklistService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.context.request.RequestAttributes;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.servlet.HandlerInterceptor;

@Component
public class JwtTokenInterceptor implements HandlerInterceptor {

    private final AuthService authService;
    private final JwtBlocklistService jwtBlocklistService;

    @Autowired
    public JwtTokenInterceptor(AuthService authService, JwtBlocklistService jwtBlocklistService) {
        this.authService = authService;
        this.jwtBlocklistService = jwtBlocklistService;
    }

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        String bearerToken = request.getHeader("Authorization");
        if (bearerToken != null && bearerToken.startsWith("Bearer ")) {
            String token = bearerToken.split("Bearer ")[1];
            User user = authService.validateToken(token);

            if (jwtBlocklistService.isBlocklisted(token)) {
                throw new UnauthorizedException("Invalid token");
            }

            RequestContextHolder.currentRequestAttributes().setAttribute("user", user, RequestAttributes.SCOPE_REQUEST);
            return true;
        }

        throw new UnauthorizedException("Invalid or missing JWT token");
    }
}