package com.ecommerce.ecomm.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
@EnableWebSecurity
public class SecurityConfig {

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        // Desativar temporariamente a segurança
        // http.csrf(csrf -> csrf.disable())
        //         .sessionManagement(sessionManagement -> sessionManagement.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
        //         .authorizeHttpRequests(authorizeRequests -> authorizeRequests
        //                 .requestMatchers("/api/auth/forgot-password").permitAll()
        //                 .requestMatchers("/", "/index.html", "/static/**", "/scripts.js", "/styles.css", "/images/**").permitAll()
        //                 .requestMatchers(HttpMethod.POST, "/api/auth/login").permitAll()
        //                 .requestMatchers(HttpMethod.POST, "/api/auth/logout").permitAll()
        //                 .requestMatchers(HttpMethod.GET, "/api/auth/logout").permitAll()
        //                 .requestMatchers("/api/auth/**").permitAll()
        //                 .requestMatchers(HttpMethod.GET, "/api/produtos/**").permitAll()
        //                 .requestMatchers(HttpMethod.POST, "/api/produtos").authenticated()
        //                 .requestMatchers(HttpMethod.PUT, "/api/produtos/**").authenticated()
        //                 .requestMatchers(HttpMethod.DELETE, "/api/produtos/**").authenticated()
        //                 .anyRequest().authenticated()
        //         )
        //         .logout(logout -> logout
        //                 .logoutUrl("/api/auth/logout")
        //                 .logoutSuccessUrl("/api/auth/logout-success")
        //                 .invalidateHttpSession(true)
        //                 .deleteCookies("JSESSIONID")
        //                 .permitAll()
        //         )
        //         .addFilterBefore(new JwtTokenFilter(), UsernamePasswordAuthenticationFilter.class);
        // return http.build();

        // Permitir todas as requisições temporariamente
        http.csrf(csrf -> csrf.disable())
                .authorizeHttpRequests(authorizeRequests -> authorizeRequests.anyRequest().permitAll());
        return http.build();
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }
}