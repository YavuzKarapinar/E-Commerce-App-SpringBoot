package me.jazzy.e_commerce_app.security;

import lombok.AllArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.www.BasicAuthenticationFilter;

@Configuration
@AllArgsConstructor
public class WebSecurityConfig {

    private JWTRequestFilter requestFilter;

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        return http
                .authorizeHttpRequests(request -> {
                            request.requestMatchers("/api/v1/auth/register").permitAll();
                            request.requestMatchers("/api/v1/auth/login").permitAll();
                            request.requestMatchers("/api/v1/auth/verify").permitAll();
                            request.anyRequest().authenticated();
                        }
                )
                .addFilterBefore(requestFilter, BasicAuthenticationFilter.class)
                .csrf(AbstractHttpConfigurer::disable)
                .cors(AbstractHttpConfigurer::disable)
                .build();
    }
}