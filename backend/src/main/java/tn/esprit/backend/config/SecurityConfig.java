package tn.esprit.backend.config;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.BeanInitializationException;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import tn.esprit.backend.security.AuthAccessDeniedHandler;
import tn.esprit.backend.security.AuthEntryPointJwt;
import tn.esprit.backend.security.JwtAuthenticationFilter;
import tn.esprit.backend.security.UserDetailsServiceImpl;

@Configuration
@EnableWebSecurity
@EnableMethodSecurity
@RequiredArgsConstructor
public class SecurityConfig {

    private static final String ROLE_ADMIN = "ADMIN";
    private static final String ROLE_USER = "USER";

    private final UserDetailsServiceImpl userDetailsService;
    private final AuthEntryPointJwt unauthorizedHandler;
    private final AuthAccessDeniedHandler accessDeniedHandler;
    private final JwtAuthenticationFilter jwtAuthenticationFilter;

    private static final String[] BUSINESS_URLS = {
            "/entreprise/**",
            "/equipe/**",
            "/projet/**",
            "/projet-detaille/**",
            "/api/entreprise/**",
            "/api/equipe/**",
            "/api/projet/**",
            "/api/projet-detaille/**"
    };

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public DaoAuthenticationProvider authenticationProvider() {
        DaoAuthenticationProvider authProvider = new DaoAuthenticationProvider(userDetailsService);
        authProvider.setPasswordEncoder(passwordEncoder());
        return authProvider;
    }

    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration authConfig) {
        try {
            return authConfig.getAuthenticationManager();
        } catch (Exception e) {
            throw new BeanInitializationException("Failed to get AuthenticationManager", e);
        }
    }

    @Bean
    @SuppressWarnings("java:S4502") // Justification: Stateless REST API using JWT Bearer authentication is not vulnerable to CSRF
    public SecurityFilterChain filterChain(HttpSecurity http) {
        try {
            http
                    .cors(Customizer.withDefaults())
                    .csrf(csrf -> csrf.disable())
                    .exceptionHandling(exception -> exception
                            .authenticationEntryPoint(unauthorizedHandler)
                            .accessDeniedHandler(accessDeniedHandler)
                    )
                    .sessionManagement(session -> session
                            .sessionCreationPolicy(SessionCreationPolicy.STATELESS)
                    )
                    .authorizeHttpRequests(auth -> auth
                            .requestMatchers(HttpMethod.OPTIONS, "/**").permitAll()
                            .requestMatchers("/api/auth/**", "/auth/**").permitAll()
                            .requestMatchers("/actuator/**", "/swagger-ui/**", "/v3/api-docs/**", "/swagger-ui.html").permitAll()
                            .requestMatchers(HttpMethod.GET, BUSINESS_URLS).hasAnyRole(ROLE_USER, ROLE_ADMIN)
                            .requestMatchers(HttpMethod.POST, BUSINESS_URLS).hasRole(ROLE_ADMIN)
                            .requestMatchers(HttpMethod.PUT, BUSINESS_URLS).hasRole(ROLE_ADMIN)
                            .requestMatchers(HttpMethod.DELETE, BUSINESS_URLS).hasRole(ROLE_ADMIN)
                            .anyRequest().authenticated()
                    )
                    .authenticationProvider(authenticationProvider())
                    .addFilterBefore(jwtAuthenticationFilter, UsernamePasswordAuthenticationFilter.class);

            return http.build();
        } catch (Exception e) {
            throw new BeanInitializationException("Failed to configure SecurityFilterChain", e);
        }
    }
}
