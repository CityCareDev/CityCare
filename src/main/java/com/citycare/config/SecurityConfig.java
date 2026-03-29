package com.citycare.config;

import com.citycare.security.JwtAuthFilter;
import com.citycare.security.UserDetailsServiceImpl;
import lombok.RequiredArgsConstructor;
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
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

import java.util.List;

@Configuration
@EnableWebSecurity
@EnableMethodSecurity
@RequiredArgsConstructor
public class SecurityConfig {

    private final UserDetailsServiceImpl userDetailsService;
    private final JwtAuthFilter jwtAuthFilter;

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder(12);
    }

    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration config) throws Exception {
        return config.getAuthenticationManager();
    }

    @Bean
    public DaoAuthenticationProvider authenticationProvider() {
        DaoAuthenticationProvider provider = new DaoAuthenticationProvider();
        provider.setUserDetailsService(userDetailsService);
        provider.setPasswordEncoder(passwordEncoder());
        return provider;
    }

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
                .csrf(AbstractHttpConfigurer::disable)
                .cors(cors -> cors.configurationSource(corsConfigurationSource()))
                .sessionManagement(s -> s.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                .authorizeHttpRequests(auth -> auth
//                        // Public endpoints (Login/Register via JSON)
//                        .requestMatchers("/auth/**", "/swagger-ui/**", "/v3/api-docs/**").permitAll()
//                        //"/citizens/**", "/facilities/**"
//                        // Protected endpoints (Requires Basic Auth Header)
//                       //.requestMatchers("emergencies/**","/emergencies/report", "/emergencies/my").permitAll()
//                       //.requestMatchers("/admin/**").permitAll()
//                        .requestMatchers("/treatments/**","/patients/**").hasRole("DOCTOR").hja
//
//
//
//                        .anyRequest().authenticated()
                                .requestMatchers(
                                        "/auth/**",
                                        "/swagger-ui/**",
                                        "/swagger-ui.html",
                                        "/v3/api-docs/**"
                                ).permitAll()

                                // CITIZEN
                                .requestMatchers("/emergencies/report").hasRole("CITIZEN")
                                .requestMatchers("/emergencies/my").hasRole("CITIZEN")
                                .requestMatchers("/citizens/profile").hasAnyRole("CITIZEN", "ADMIN")
                                .requestMatchers(HttpMethod.GET,"/citizens/*/documents").hasAnyRole("ADMIN","DOCTOR","NURSE")
                                .requestMatchers("/citizens/documents/*/verify").hasRole("ADMIN")

                                // DISPATCHER
                                .requestMatchers("/emergencies/pending").hasRole("DISPATCHER")
                                .requestMatchers("/emergencies/ambulances/available").hasRole("DISPATCHER")
                                .requestMatchers("/emergencies/*/dispatch").hasRole("DISPATCHER")

                                // ADMIN

                                .requestMatchers("/emergencies/dispatched").hasAnyRole("ADMIN", "CITY_HEALTH_OFFICER")
                                .requestMatchers("/patients/admit").hasRole("ADMIN")
                                .requestMatchers("/admin/**").hasRole("ADMIN")
                                .requestMatchers("/facilities").hasAnyRole("ADMIN", "CITY_HEALTH_OFFICER")

                                // DOCTOR / NURSE
                                .requestMatchers("/treatments/**").hasAnyRole("DOCTOR", "NURSE")
                                .requestMatchers("/patients/*/status").hasAnyRole("DOCTOR", "NURSE", "ADMIN")

                                // COMPLIANCE
                                .requestMatchers("/compliance/**").hasAnyRole("ADMIN", "COMPLIANCE_OFFICER", "CITY_HEALTH_OFFICER")

                                // ANY AUTHENTICATED
                                .anyRequest().authenticated()
                )
                .authenticationProvider(authenticationProvider())
                // Enable Basic Auth for all non-public requests
                .httpBasic(Customizer.withDefaults())
                .addFilterBefore(jwtAuthFilter, UsernamePasswordAuthenticationFilter.class);

        return http.build();
    }

    @Bean
    public CorsConfigurationSource corsConfigurationSource() {
        CorsConfiguration config = new CorsConfiguration();
        config.setAllowedOriginPatterns(List.of("*"));
        config.setAllowedMethods(List.of("GET", "POST", "PUT", "PATCH", "DELETE", "OPTIONS"));
        config.setAllowedHeaders(List.of("*"));
        config.setAllowCredentials(true);
        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", config);
        return source;
    }
}