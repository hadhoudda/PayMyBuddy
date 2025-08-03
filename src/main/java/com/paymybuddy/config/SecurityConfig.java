package com.paymybuddy.config;

import com.paymybuddy.service.CustomUserDetailsService;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.util.matcher.AntPathRequestMatcher;


@Configuration
@EnableWebSecurity
public class SecurityConfig {

    private final CustomUserDetailsService customUserDetailsService;

    public SecurityConfig(CustomUserDetailsService customUserDetailsService) {
        this.customUserDetailsService = customUserDetailsService;
    }

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        return http
                .csrf(Customizer.withDefaults())

                // Désactive le cache des pages protégées
                .headers(headers -> headers
                        .cacheControl(cache -> {
                        })
                )

                // Configuration des accès
                .authorizeHttpRequests(auth -> auth
                        // Chemins publics accessibles sans être connecté
                        .requestMatchers(
                                "/paymybuddy",
                                "/paymybuddy/login",
                                "/paymybuddy/register",
                                "/paymybuddy/register/confirmed",
                                "/css/**",
                                "/js/**",
                                "/images/**"
                        ).permitAll()
                        // Toutes les autres requêtes nécessitent une authentification
                        .anyRequest()
                        .authenticated()
                )

                // Configuration du formulaire de login
                .formLogin(form -> form
                        .loginPage("/paymybuddy/login")
                        .loginProcessingUrl("/login")
                        .defaultSuccessUrl("/paymybuddy/profil", true)
                        .failureUrl("/paymybuddy/login?error=true")
                        .permitAll()
                )

                // Configuration du logout la déconnexion
                .logout(logout -> logout
                        .logoutRequestMatcher(new AntPathRequestMatcher("/logout"))
                        .logoutSuccessUrl("/paymybuddy/login?logout=true")
                        .invalidateHttpSession(true)
                        .deleteCookies("JSESSIONID")
                        .permitAll()
                )
                .httpBasic(Customizer.withDefaults())
                .build();
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public AuthenticationManager authenticationManager(HttpSecurity httpSecurity, PasswordEncoder passwordEncoder) throws Exception {
        AuthenticationManagerBuilder authenticationManagerBuilder =
                httpSecurity.getSharedObject(AuthenticationManagerBuilder.class);
        authenticationManagerBuilder.userDetailsService(customUserDetailsService).passwordEncoder(passwordEncoder);
        return authenticationManagerBuilder.build();
    }
}