package com.lankaride.config;

import com.lankaride.auth.CustomUserDetailsService;
import jakarta.servlet.DispatcherType;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.util.matcher.AntPathRequestMatcher;

@Configuration
@EnableWebSecurity
@EnableMethodSecurity
public class SecurityConfig {

    private final CustomUserDetailsService userDetailsService;
    private final RoleBasedAuthSuccessHandler successHandler;

    public SecurityConfig(CustomUserDetailsService userDetailsService,
                          RoleBasedAuthSuccessHandler successHandler) {
        this.userDetailsService = userDetailsService;
        this.successHandler = successHandler;
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
                .userDetailsService(userDetailsService)
                .authorizeHttpRequests(auth -> auth
                        .dispatcherTypeMatchers(DispatcherType.FORWARD, DispatcherType.INCLUDE, DispatcherType.ERROR).permitAll()
                        .requestMatchers(
                                AntPathRequestMatcher.antMatcher("/"),
                                AntPathRequestMatcher.antMatcher("/css/**"),
                                AntPathRequestMatcher.antMatcher("/images/**"),
                                AntPathRequestMatcher.antMatcher("/login"),
                                AntPathRequestMatcher.antMatcher("/register"),
                                AntPathRequestMatcher.antMatcher("/forgot-password"),
                                AntPathRequestMatcher.antMatcher("/reset-password"),
                                AntPathRequestMatcher.antMatcher("/error")
                        ).permitAll()
                        .requestMatchers(AntPathRequestMatcher.antMatcher("/admin/**")).hasRole("ADMIN")
                        .requestMatchers(AntPathRequestMatcher.antMatcher("/reports/**"))
                        .hasAnyRole("ADMIN", "FINANCE_MANAGER", "OPERATIONS_MANAGER")
                        .requestMatchers(AntPathRequestMatcher.antMatcher("/dashboard"))
                        .hasAnyRole("ADMIN", "BOOKING_SUPERVISOR", "FLEET_COORDINATOR",
                                "FINANCE_MANAGER", "OPERATIONS_MANAGER")
                        .requestMatchers(AntPathRequestMatcher.antMatcher("/app"))
                        .hasRole("CUSTOMER")
                        .requestMatchers(AntPathRequestMatcher.antMatcher("/payments"))
                        .hasAnyRole("ADMIN", "FINANCE_MANAGER", "BOOKING_SUPERVISOR", "OPERATIONS_MANAGER")
                        .requestMatchers(
                                AntPathRequestMatcher.antMatcher("/vehicles/new"),
                                AntPathRequestMatcher.antMatcher("/vehicles/*/edit"),
                                AntPathRequestMatcher.antMatcher("/vehicles/*/retire")
                        ).hasAnyRole("ADMIN", "FLEET_COORDINATOR", "OPERATIONS_MANAGER")
                        .requestMatchers(AntPathRequestMatcher.antMatcher(HttpMethod.POST, "/vehicles"))
                        .hasAnyRole("ADMIN", "FLEET_COORDINATOR", "OPERATIONS_MANAGER")
                        .requestMatchers(AntPathRequestMatcher.antMatcher(HttpMethod.POST, "/vehicles/*"))
                        .hasAnyRole("ADMIN", "FLEET_COORDINATOR", "OPERATIONS_MANAGER")
                        .requestMatchers(AntPathRequestMatcher.antMatcher("/maintenance/**"))
                        .hasAnyRole("ADMIN", "FLEET_COORDINATOR")
                        .requestMatchers(AntPathRequestMatcher.antMatcher(HttpMethod.GET, "/vehicles")).permitAll()
                        .requestMatchers(AntPathRequestMatcher.antMatcher(HttpMethod.GET, "/vehicles/*")).permitAll()
                        .anyRequest().authenticated()
                )
                .formLogin(form -> form
                        .loginPage("/login")
                        .successHandler(successHandler)
                        .permitAll()
                )
                .logout(logout -> logout
                        .logoutSuccessUrl("/login?logout")
                        .permitAll()
                );
        return http.build();
    }
}
