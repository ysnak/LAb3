package com.example.demo.Config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;

import org.springframework.security.crypto.password.NoOpPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;


@Configuration
@EnableWebSecurity
public class SecurityConfig {

    @Bean
    public PasswordEncoder passwordEncoder() {
        return NoOpPasswordEncoder.getInstance();
    }

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
                .authorizeRequests(authorize -> authorize

                        .requestMatchers("/login-page", "/create-account-user", "/authorization-page",
                                "/error", "/404", "/401", "/error-403").permitAll()
                        .requestMatchers("/", "/select-table", "/chempionat",
                                "/command", "/sportsman").hasAnyRole("USER", "ADMIN")
                        .requestMatchers("/**").hasRole("ADMIN")

                        .anyRequest().authenticated()
                )
                .formLogin(form -> form
                        .loginPage("/login-page")
                        .failureUrl("/error")
                        .permitAll()
                )
                .logout(logout -> logout
                        .logoutSuccessUrl("/login-page")
                        .permitAll()

                )

                .exceptionHandling(exceptions -> exceptions
                        .accessDeniedPage("/error-403")
                        .authenticationEntryPoint((request, response, authException) -> {
                            // Отправка ошибки 404 для неверных URL
                            response.sendRedirect("/error-404");
                        })
                );

        return http.build();
    }
}