// java
package eci.edu.dosw.proyecto.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.provisioning.InMemoryUserDetailsManager;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
@EnableWebSecurity
@EnableMethodSecurity(prePostEnabled = true)
public class SecurityConfig {

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
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
                                "/api/auth/**",
                                "/swagger-ui/**",
                                "/swagger-ui.html",
                                "/swagger-ui/index.html",
                                "/v3/api-docs",
                                "/v3/api-docs/**",
                                "/v3/api-docs.yaml",
                                "/v3/api-docs/swagger-config",
                                "/swagger-resources/**",
                                "/webjars/**",
                                "/favicon.ico"
                        ).permitAll()
                        .anyRequest().authenticated()
                )
                .httpBasic(httpBasic -> {})
                .formLogin(form -> form.disable());
        return http.build();
    }

    @Bean
    public UserDetailsService userDetailsService(
            PasswordEncoder passwordEncoder,
            @Value("${app.security.admin.username}") String adminUser,
            @Value("${app.security.admin.password}") String adminPass,
            @Value("${app.security.user.username}") String normalUser,
            @Value("${app.security.user.password}") String normalPass) {

        UserDetails admin = User.withUsername(adminUser)
                .password(passwordEncoder.encode(adminPass))
                .roles("ADMIN")
                .build();

        UserDetails user = User.withUsername(normalUser)
                .password(passwordEncoder.encode(normalPass))
                .roles("USER")
                .build();

        return new InMemoryUserDetailsManager(admin, user);
    }
}
