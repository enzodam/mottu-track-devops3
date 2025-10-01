package br.com.fiap.mottuapp.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Lazy;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;

/**
 * SecurityConfig (tudo em um lugar, sem ciclo):
 * - Declara PasswordEncoder (BCrypt)
 * - Cria DaoAuthenticationProvider usando @Lazy UserDetailsService
 * - Regras HTTP, login customizado e logout
 * - Pós-login vai para /pos-login (faça o método no seu AuthController)
 */
@Configuration
@EnableWebSecurity
public class SecurityConfig {

    // ====== Encoder ======
    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    // ====== Provider (usa @Lazy para quebrar o ciclo com UsuarioService) ======
    @Bean
    public AuthenticationProvider authenticationProvider(
            @Lazy UserDetailsService usuarioService,
            PasswordEncoder passwordEncoder
    ) {
        DaoAuthenticationProvider p = new DaoAuthenticationProvider();
        p.setUserDetailsService(usuarioService);
        p.setPasswordEncoder(passwordEncoder);
        return p;
    }

    // ====== Regras de segurança HTTP ======
    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http, AuthenticationProvider authProvider) throws Exception {
        http
                .csrf(csrf -> csrf.disable())
                .authorizeHttpRequests(auth -> auth
                        .requestMatchers("/css/**", "/js/**", "/images/**", "/webjars/**").permitAll()
                        .requestMatchers("/login", "/register", "/usuarios/novo").permitAll()
                        .requestMatchers("/admin/**").hasRole("ADMIN")
                        .anyRequest().authenticated()
                )
                .formLogin(form -> form
                        .loginPage("/login")
                        .loginProcessingUrl("/login")
                        .defaultSuccessUrl("/pos-login", true) // redireciona para rota de pós-login
                        .permitAll()
                )
                .logout(logout -> logout
                        .logoutUrl("/logout")
                        .logoutSuccessUrl("/login?logout")
                        .invalidateHttpSession(true)
                        .deleteCookies("JSESSIONID")
                )
                .authenticationProvider(authProvider);

        return http.build();
    }
}
