package com.ecommerce.ecomm.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

@Configuration
@EnableWebSecurity
public class SecurityConfig {

	@Bean
	public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
	    http.csrf(csrf -> csrf.disable())
	            .sessionManagement(sessionManagement -> sessionManagement.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
	            .authorizeHttpRequests(authorizeRequests -> authorizeRequests
	                    .requestMatchers("/api/auth/forgot-password").permitAll()
	                    .requestMatchers("/", "/index.html", "/static/**", "/scripts.js", "/styles.css", "/images/**").permitAll()
	                    .requestMatchers(HttpMethod.POST, "/api/auth/login").permitAll()
	                    .requestMatchers(HttpMethod.POST, "/api/auth/logout").permitAll()
	                    .requestMatchers(HttpMethod.GET, "/api/auth/logout").permitAll()
	                    .requestMatchers("/api/auth/**").permitAll()
	                    .requestMatchers(HttpMethod.GET, "/api/produtos/**").permitAll()
	                    .requestMatchers(HttpMethod.GET, "/api/vendas/**").permitAll()
	                    .requestMatchers(HttpMethod.POST, "/api/vendas").hasAnyRole("ADMIN", "USER")
	                    .requestMatchers(HttpMethod.PUT, "/api/vendas").hasAnyRole("ADMIN", "USER")
	                    .requestMatchers(HttpMethod.DELETE, "/api/vendas").hasAnyRole("ADMIN", "USER")
	                    .requestMatchers(HttpMethod.DELETE, "/api/produtos/**").hasRole("ADMIN")
	                    .requestMatchers(HttpMethod.POST, "/api/produtos").hasRole("ADMIN")
	                    .requestMatchers(HttpMethod.PUT, "/api/produtos/**").hasRole("ADMIN")
	                    .anyRequest().authenticated()
	                    
	                    
	            )
	            .exceptionHandling(exceptionHandling ->
                exceptionHandling
                    .accessDeniedHandler((request, response, accessDeniedException) -> {
                        throw new AccessDeniedException("Acesso negado. Você não tem permissão para realizar esta operação.");
                    })
                    )
	            .logout(logout -> logout
	                    .logoutUrl("/api/auth/logout")
	                    .logoutSuccessUrl("/api/auth/logout-success")
	                    .invalidateHttpSession(true)
	                    .deleteCookies("JSESSIONID")
	                    .permitAll()
	            )
	            .addFilterBefore(new JwtTokenFilter(), UsernamePasswordAuthenticationFilter.class);
	    return http.build();
	}

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }
}