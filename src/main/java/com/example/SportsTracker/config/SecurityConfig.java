package com.example.SportsTracker.config;

import com.example.SportsTracker.core.model.Role;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.session.web.http.CookieSerializer;
import org.springframework.session.web.http.DefaultCookieSerializer;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.List;
import java.util.stream.Collectors;

@Configuration
@EnableWebSecurity
public class SecurityConfig {

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public CookieSerializer cookieSerializer() {
        DefaultCookieSerializer serializer = new DefaultCookieSerializer();
        serializer.setUseHttpOnlyCookie(true);
        serializer.setSameSite("Lax");
        return serializer;
    }

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http
                .csrf(AbstractHttpConfigurer::disable)

                // Centralized error handling for unauthorized API requests (prevents redirects)
                .exceptionHandling(ex -> ex
                        .authenticationEntryPoint((request, response, authException) -> response.sendError(HttpServletResponse.SC_UNAUTHORIZED, "Unauthorized"))
                        .accessDeniedHandler((request, response, accessDeniedException) -> response.sendError(HttpServletResponse.SC_FORBIDDEN, "Forbidden"))
                )

                .authorizeHttpRequests(auth -> auth
                        // Public Endpoints
                        .requestMatchers(HttpMethod.POST, "/api/auth/signup", "/api/auth/signin").permitAll()
                        .requestMatchers(HttpMethod.GET,
                                "/",
                                "/quests",
                                "/esports",
                                "/football",
                                "/css/**",
                                "/js/**",
                                "/images/**",
                                "/static/**",
                                "/favicon.ico",
                                "/api/quests/leaderboard",
                                "/api/football/standings/**",
                                "/api/football/leagues",
                                "/api/football/fixtures",
                                "/swagger-ui.html",
                                "/swagger-ui/**",
                                "/v3/api-docs/**").permitAll()

                        // ROLE_ADMIN only endpoints
                        // Using precise path matching so sub-paths like /api/quests/{id}/submit remain accessible to normal users
                        .requestMatchers(HttpMethod.POST,
                                "/api/tournaments",
                                "/api/quests",
                                "/api/football/leagues",
                                "/api/football/leagues/sync/**").hasRole("ADMIN")
                        .requestMatchers(HttpMethod.PUT,
                                "/api/tournaments/*",
                                "/api/quests/*",
                                "/api/quests/submissions/*/review",
                                "/api/football/leagues/*").hasRole("ADMIN")
                        .requestMatchers(HttpMethod.DELETE,
                                "/api/tournaments/*",
                                "/api/quests/*",
                                "/api/football/leagues/*").hasRole("ADMIN")
                        .requestMatchers(HttpMethod.GET,
                                // existing ones...
                                "/api/football/**",
                                "/api/football/worldcup/**"
                        ).permitAll()

                        // All other endpoints require an authenticated session
                        .anyRequest().authenticated()
                )

                // Bridge our custom HttpSession login context to Spring Security Context
                .addFilterBefore(new OncePerRequestFilter() {
                    @Override
                    @SuppressWarnings("unchecked")
                    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
                            throws ServletException, IOException {

                        HttpSession session = request.getSession(false);
                        if (session != null && session.getAttribute("USER_ID") != null
                                && SecurityContextHolder.getContext().getAuthentication() == null) {

                            List<Role> roles = (List<Role>) session.getAttribute("USER_ROLES");
                            List<SimpleGrantedAuthority> authorities = roles != null
                                    ? roles.stream().map(role -> new SimpleGrantedAuthority(role.name())).collect(Collectors.toList())
                                    : List.of();

                            UsernamePasswordAuthenticationToken auth = new UsernamePasswordAuthenticationToken(
                                    session.getAttribute("USER_ID"), null, authorities);
                            SecurityContextHolder.getContext().setAuthentication(auth);
                        }
                        filterChain.doFilter(request, response);
                    }
                }, UsernamePasswordAuthenticationFilter.class);

        return http.build();
    }
}