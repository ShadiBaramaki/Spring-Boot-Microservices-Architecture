package config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.web.reactive.EnableWebFluxSecurity;
import org.springframework.security.config.web.server.ServerHttpSecurity;
import org.springframework.security.web.server.SecurityWebFilterChain;

@Configuration
@EnableWebFluxSecurity
public class SecurityConfig {

    @Bean
    public SecurityWebFilterChain springSecurityFilterChain(ServerHttpSecurity serverHttpSecurity) {
        serverHttpSecurity
                .csrf(ServerHttpSecurity.CsrfSpec::disable)
                .authorizeExchange(exchange -> exchange
                        .pathMatchers(
                                "/eureka/**",                 // Allow Eureka endpoints
                                "/swagger-ui.html",           // Allow Swagger UI main page
                                "/swagger-ui/**",             // Allow Swagger UI assets
                                "/v3/api-docs/**",            // Allow OpenAPI docs
                                "/webjars/**",                // Allow Swagger UI webjars
                                "/swagger-resources/**"       // Allow Swagger resources
                        ).permitAll()
                        .anyExchange().authenticated()
                )
                .oauth2ResourceServer(spec -> spec.jwt(Customizer.withDefaults()));

        return serverHttpSecurity.build();
    }
}
