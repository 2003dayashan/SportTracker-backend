package com.example.SportsTracker.config;

import com.example.SportsTracker.core.model.Role;
import com.example.SportsTracker.questboard.security.JwtAuthenticationFilter;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
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
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.List;
import java.util.stream.Collectors;

@Configuration
@EnableWebSecurity
@EnableMethodSecurity
public class SecurityConfig {

    private final JwtAuthenticationFilter jwtAuthenticationFilter;
    private final CorsConfigurationSource corsConfigurationSource;

    public SecurityConfig(JwtAuthenticationFilter jwtAuthenticationFilter, CorsConfigurationSource corsConfigurationSource) {
        this.jwtAuthenticationFilter = jwtAuthenticationFilter;
        this.corsConfigurationSource = corsConfigurationSource;
    }

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
                .cors(cors -> cors.configurationSource(corsConfigurationSource))
                .csrf(AbstractHttpConfigurer::disable)

                // Centralized error handling for unauthorized API requests (prevents redirects)
                .exceptionHandling(ex -> ex
                        .authenticationEntryPoint((request, response, authException) -> response.sendError(HttpServletResponse.SC_UNAUTHORIZED, "Unauthorized"))
                        .accessDeniedHandler((request, response, accessDeniedException) -> response.sendError(HttpServletResponse.SC_FORBIDDEN, "Forbidden"))
                )

                .authorizeHttpRequests(auth -> auth

                        // ✅ Let CORS preflight requests through — must come before anyRequest().authenticated()
                        .requestMatchers(HttpMethod.OPTIONS, "/**").permitAll()

                        // ✅ PUBLIC POST — මුලින්ම දාන්න
                        .requestMatchers(HttpMethod.POST,
                                "/api/auth/signup",
                                "/api/auth/signin",
                                "/api/auth/forgot-password",
                                "/api/auth/reset-password"
                        ).permitAll()

                        // ✅ PUBLIC GET
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
                                "/api/quests",
                                "/api/quests/**",
                                "/api/quests/leaderboard",
                                "/api/categories",
                                "/api/categories/**",
                                "/api/files/**",
                                "/api/football/standings/**",
                                "/api/football/leagues",
                                "/api/football/fixtures",
                                "/api/football/**",
                                "/api/football/worldcup/**",
                                "/swagger-ui.html",
                                "/swagger-ui/**",
                                "/v3/api-docs/**"
                        ).permitAll()

                        .requestMatchers(HttpMethod.GET,
                                "/api/players/**", "/api/teams/**", "/api/tournaments/**"
                        ).permitAll()



                        // Quest board: JWT-authenticated write operations (roles enforced via @PreAuthorize)
                        .requestMatchers(HttpMethod.POST,
                                "/api/quests",
                                "/api/quests/*/claim",
                                "/api/quests/*/submit",
                                "/api/quests/submissions/*/approve",
                                "/api/categories",
                                "/api/files/upload"
                        ).authenticated()

                        // ADMIN only POST
                        .requestMatchers(HttpMethod.POST,
                                "/api/tournaments",
                                "/api/football/leagues",
                                "/api/football/leagues/sync/**"
                        ).hasRole("ADMIN")

                        .requestMatchers(HttpMethod.POST,
                                "/api/football/worldcup/sync",
                                "/api/players/**", "/api/teams/**", "/api/tournaments/**"
                        ).authenticated()

                        .requestMatchers(HttpMethod.PUT,
                                "/api/tournaments/*",
                                "/api/football/leagues/*"
                        ).hasRole("ADMIN")

                        .requestMatchers(HttpMethod.PUT,
                                "/api/quests/*"
                        ).authenticated()

                        .requestMatchers(HttpMethod.PUT,
                                "/api/quests/submissions/*/review"
                        ).authenticated()

                        .requestMatchers(HttpMethod.PUT,
                                "/api/players/**", "/api/teams/**", "/api/tournaments/**"
                        ).authenticated()

                        .requestMatchers(HttpMethod.DELETE,
                                "/api/players/**", "/api/teams/**", "/api/tournaments/**"
                        ).authenticated()


                        .requestMatchers(HttpMethod.DELETE,
                                "/api/tournaments/*",
                                "/api/football/leagues/*"
                        ).hasRole("ADMIN")

                        .requestMatchers(HttpMethod.DELETE,
                                "/api/quests/*"
                        ).authenticated()

                        .anyRequest().authenticated()
                )

                // Quest board JWT auth (quest_token cookie)
                .addFilterBefore(jwtAuthenticationFilter, UsernamePasswordAuthenticationFilter.class)

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