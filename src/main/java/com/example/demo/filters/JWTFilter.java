package com.example.demo.filters;

import java.io.IOException;
import java.util.Collections;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.WebAuthenticationDetails;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import com.example.demo.entities.Users;
import com.example.demo.services.AccountService;
import com.example.demo.services.JWTService;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

@Component
public class JWTFilter extends OncePerRequestFilter {

    private static final Logger logger = LoggerFactory.getLogger(JWTFilter.class);

    @Autowired
    private JWTService jwtService;

    @Autowired
    private AccountService accountService;

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
            throws ServletException, IOException {
        try {
            String authorization = request.getHeader("Authorization");

            if (authorization != null && authorization.startsWith("Bearer ")) {
                String token = authorization.substring(7);

                if (jwtService.validToken(token)) {
                    String email = jwtService.getUsernameFromJWT(token);
                    logger.info("Authenticated user: {}", email);

                    Users user = accountService.findByEmail(email);
                    if (user == null) {
                        logger.warn("User '{}' not found in database", email);
                        filterChain.doFilter(request, response);
                        return;
                    }

                    // Lấy quyền từ bảng Roles
                    String roleName = user.getRoles().getName();
                    GrantedAuthority authority = new SimpleGrantedAuthority(roleName);

                    UsernamePasswordAuthenticationToken authenticationToken = 
                        new UsernamePasswordAuthenticationToken(user, null, Collections.singletonList(authority));

                    authenticationToken.setDetails(new WebAuthenticationDetails(request));

                    // Đặt thông tin người dùng vào Security Context
                    SecurityContextHolder.getContext().setAuthentication(authenticationToken);
                }
            }
        } catch (Exception e) {
            logger.error("JWT Authentication failed: {}", e.getMessage());
        }
        filterChain.doFilter(request, response);
    }
}
