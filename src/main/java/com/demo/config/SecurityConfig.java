package com.demo.config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;

import com.demo.service.impl.UserDetailsServiceImpl;

@Configuration
public class SecurityConfig {

    @Autowired
    private UserDetailsServiceImpl userDetailsService;

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public DaoAuthenticationProvider authenticationProvider() {
        DaoAuthenticationProvider authProvider = new DaoAuthenticationProvider();
        authProvider.setUserDetailsService(userDetailsService);
        authProvider.setPasswordEncoder(passwordEncoder());
        return authProvider;
    }

    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration authConfig) throws Exception {
        return authConfig.getAuthenticationManager();
    }

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
    	   http
           .csrf(csrf -> csrf.disable())
           .authorizeHttpRequests(auth -> auth
               .requestMatchers(
                    "/",
                    "/PaginaWeb/**",
                    "/login",
                    "/register",
                    "/css/**",
                    "/js/**",
                    "/script/**",
                    "/img/**",
                    "/imagenes/**",
                    "/contacto",
                    "/enviarMensaje",
                    "/catalogo",
                    "/checkout",
                    "/nosotros",
                    "/perfil",
                    "/nosotros",
                    "/pedido/**",
                    "/carrito/**"
                ).permitAll()

                .requestMatchers("/PaginaAdmin/**").hasRole("ADM")

                .anyRequest().authenticated()
            )

            .formLogin(form -> form
                .loginPage("/login")
                .loginProcessingUrl("/procesarLogin")
                .usernameParameter("correo")
                .passwordParameter("clave")
                .successHandler((request, response, authentication) -> {
                    String rol = authentication.getAuthorities().iterator().next().getAuthority();
                    if (rol.equals("ROLE_ADM")) {
                        response.sendRedirect("/PaginaAdmin/PanelAdmin");
                    } else {
                        response.sendRedirect("/PaginaWeb/index");
                    }
                })
                .failureUrl("/login?error=true")
                .permitAll()
            )

            .logout(logout -> logout
                .logoutUrl("/logout")
                .logoutSuccessUrl("/login?logout=true")
                .permitAll()
            );

        return http.build();
    }
}

