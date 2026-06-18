package com.nexora.config;

import com.nexora.repository.UserRepository;
import com.nexora.security.*;
import com.nexora.repository.RegisteredClientRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;

import java.util.List;

@Configuration
@EnableWebSecurity
@EnableMethodSecurity
@RequiredArgsConstructor
public class SecurityConfig {

    private final RequestTrackingFilter requestTrackingFilter;
    private final RateLimitFilter rateLimitFilter;
    private final RegisteredClientFilter registeredClientFilter;
    private final OAuth2SuccessHandler  oAuth2SuccessHandler;


    @Bean
    public SecurityFilterChain securityFilterChain(
            HttpSecurity http,
            JwtAuthFilter jwtAuthFilter,
            AuthenticationProvider authenticationProvider) throws Exception {

        return http
                .cors(Customizer.withDefaults())
                .csrf(AbstractHttpConfigurer::disable)

                .authorizeHttpRequests(auth -> auth
                        .requestMatchers(HttpMethod.OPTIONS, "/**").permitAll()
                        .requestMatchers("/api/v1/auth/**").permitAll()
                        .anyRequest().authenticated()
                )

                .sessionManagement(s -> s
                        .sessionCreationPolicy(SessionCreationPolicy.STATELESS)
                        .sessionFixation().none()
                )

                .authenticationProvider(authenticationProvider)

                .oauth2Login(oauth2 -> oauth2
                        .authorizationEndpoint(endpoint -> endpoint
                                .baseUri("/api/v1/auth/google")
                        )
                        .redirectionEndpoint(endpoint -> endpoint
                                .baseUri("/api/v1/auth/google/callback")
                        )
                        .successHandler(oAuth2SuccessHandler)
                )

                .addFilterBefore(
                        requestTrackingFilter,
                        UsernamePasswordAuthenticationFilter.class
                )
                .addFilterAfter(
                        rateLimitFilter,
                        RequestTrackingFilter.class
                )
                .addFilterAfter(
                        registeredClientFilter,
                        RateLimitFilter.class
                )
                .addFilterAfter(
                        jwtAuthFilter,
                        RegisteredClientFilter.class
                )

                .build();
    }

    @Bean
    public CorsConfigurationSource corsConfigurationSource(
            RegisteredClientRepository registeredClientRepository) {

        return request -> {
            String origin = request.getHeader(HttpHeaders.ORIGIN);

            if (origin == null || origin.isBlank()) {
                return null;
            }

            origin = origin.trim();

            if (!registeredClientRepository.existsByAllowedOriginAndActiveTrue(origin)) {
                return null;
            }

            CorsConfiguration configuration = new CorsConfiguration();
            configuration.setAllowedOrigins(List.of(origin));
            configuration.setAllowedMethods(List.of(
                    "GET",
                    "POST",
                    "PUT",
                    "PATCH",
                    "DELETE",
                    "OPTIONS"
            ));
            configuration.addAllowedHeader("*");
            configuration.setMaxAge(3600L);

            return configuration;
        };
    }

    @Bean
    public UserDetailsService userDetailsService(UserRepository userRepository) {
        return subject -> {

            var user = userRepository.findByEmail(subject)
                    .or(() -> userRepository.findByPhone(subject))
                    .orElseThrow(() ->
                            new UsernameNotFoundException(
                                    "User not found: " + subject
                            )
                    );

            return org.springframework.security.core.userdetails.User
                    .withUsername(subject)
                    .password(user.getPassword() != null
                            ? user.getPassword()
                            : "")
                    .roles("USER")
                    .build();
        };
    }

    @Bean
    public AuthenticationProvider authenticationProvider(
            UserDetailsService userDetailsService,
            PasswordEncoder passwordEncoder
    ) {

        var provider = new DaoAuthenticationProvider();
        provider.setUserDetailsService(userDetailsService);
        provider.setPasswordEncoder(passwordEncoder);

        return provider;
    }

    @Bean
    public AuthenticationManager authenticationManager(
            AuthenticationConfiguration config
    ) throws Exception {
        return config.getAuthenticationManager();
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }
}
