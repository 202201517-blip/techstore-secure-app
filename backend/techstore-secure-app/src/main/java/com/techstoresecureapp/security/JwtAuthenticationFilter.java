package com.techstoresecureapp.security;

import com.techstoresecureapp.entity.AppUser;
import com.techstoresecureapp.repository.AppUserRepository;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.List;

@Component
public class JwtAuthenticationFilter extends OncePerRequestFilter {

    private final JwtService jwtService;
    private final AppUserRepository appUserRepository;

    public JwtAuthenticationFilter(
            JwtService jwtService,
            AppUserRepository appUserRepository
    ) {
        this.jwtService = jwtService;
        this.appUserRepository = appUserRepository;
    }

    @Override
    protected void doFilterInternal(
            HttpServletRequest request,
            HttpServletResponse response,
            FilterChain filterChain
    ) throws ServletException, IOException {

        String authHeader = request.getHeader("Authorization");

        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            filterChain.doFilter(request, response);
            return;
        }

        String token = authHeader.substring(7);

        if (!jwtService.isTokenValid(token)) {
            filterChain.doFilter(request, response);
            return;
        }

        String email = jwtService.extractEmail(token);

        if (email != null && SecurityContextHolder.getContext().getAuthentication() == null) {
            AppUser user = appUserRepository.findByEmail(email)
                    .orElse(null);

            if (user != null && user.getEnabled()) {
                String role = "ROLE_" + user.getRole();

                UsernamePasswordAuthenticationToken authenticationToken =
                        new UsernamePasswordAuthenticationToken(
                                user,
                                null,
                                List.of(new SimpleGrantedAuthority(role))
                        );

                SecurityContextHolder.getContext().setAuthentication(authenticationToken);
            }
        }

        filterChain.doFilter(request, response);
    }
}
