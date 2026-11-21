package com.fzgs.personnel.config;

import com.fzgs.personnel.repository.UserRepository;
import jakarta.servlet.DispatcherType;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
public class SecurityConfig {

    @Bean
    PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    UserDetailsService userDetailsService(UserRepository users) {
        return username -> users.findByUsername(username)
                .map(user -> User.withUsername(user.username())
                        .password(user.passwordHash())
                        .roles(user.role().name())
                        .disabled(!user.enabled())
                        .build())
                .orElseThrow(() -> new UsernameNotFoundException("用户不存在"));
    }

    @Bean
    SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
                .authorizeHttpRequests(auth -> auth
                        .dispatcherTypeMatchers(DispatcherType.FORWARD).permitAll()
                        .requestMatchers("/login", "/css/**", "/error/**").permitAll()
                        .requestMatchers("/departments/**", "/positions/**", "/users/**").hasRole("ADMIN")
                        .requestMatchers("/employees/**", "/attendance/**", "/payroll/**").hasAnyRole("ADMIN", "HR")
                        .requestMatchers("/leave/*/approval").hasAnyRole("ADMIN", "HR")
                        .requestMatchers("/leave/**").hasAnyRole("ADMIN", "HR", "EMPLOYEE")
                        .anyRequest().authenticated())
                .formLogin(form -> form
                        .loginPage("/login")
                        .defaultSuccessUrl("/dashboard", true)
                        .permitAll())
                .logout(logout -> logout.logoutSuccessUrl("/login?logout"));
        return http.build();
    }
}
