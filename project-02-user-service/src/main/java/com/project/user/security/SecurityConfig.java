package com.project.user.security;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

import jakarta.servlet.http.HttpServletResponse;

@Configuration
public class SecurityConfig {

    private final JwtAuthenticationFilter jwtAuthenticationFilter;

    public SecurityConfig(JwtAuthenticationFilter jwtAuthenticationFilter) {
        this.jwtAuthenticationFilter = jwtAuthenticationFilter;
    }

    @Bean
    SecurityFilterChain securityFilterChain(HttpSecurity http)
            throws Exception {

        http
            .csrf(AbstractHttpConfigurer::disable)

            .sessionManagement(session ->
                session.sessionCreationPolicy(
                    SessionCreationPolicy.STATELESS
                )
            )

            .exceptionHandling(exception ->
            exception
            .authenticationEntryPoint(
            	    (request, response, authException) -> {
            	       

            	        response.sendError(
            	            HttpServletResponse.SC_UNAUTHORIZED,
            	            "Unauthorized"
            	        );
            	    }
            	)
                .accessDeniedHandler(
                    (request, response, accessDeniedException) ->
                        response.sendError(
                            HttpServletResponse.SC_FORBIDDEN,
                            "Forbidden"
                        )
                )
        )
            .authorizeHttpRequests(auth -> auth

                // Login is public
            		.requestMatchers("/users/login").permitAll()
            		.requestMatchers("/users/verify-account").permitAll()
            		.requestMatchers("/users/forgot-password").permitAll()
            		.requestMatchers("/users/verify-otp").permitAll()
            		.requestMatchers("/users/reset-password").permitAll()
                .requestMatchers("/error").permitAll()
                // Anyone can create a user
                .requestMatchers(HttpMethod.POST, "/users").permitAll()

                // Only ADMIN can view all users
                .requestMatchers(HttpMethod.GET, "/users").hasAuthority("ROLE_ADMIN")               // Only ADMIN can update users
                .requestMatchers(HttpMethod.PUT, "/users/**").hasRole("ADMIN")

                // Logged-in CUSTOMER or ADMIN can access individual users
                .requestMatchers("/users/**").authenticated()

                // Everything else requires authentication
                .anyRequest().authenticated()
            )

            .addFilterBefore(
                jwtAuthenticationFilter,
                UsernamePasswordAuthenticationFilter.class
            );

        return http.build();
    }
}