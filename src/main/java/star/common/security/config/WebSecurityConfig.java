package star.common.security.config;


import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.messaging.Message;
import org.springframework.security.authorization.AuthorizationManager;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.annotation.web.configurers.HeadersConfigurer.FrameOptionsConfig;
import org.springframework.security.config.annotation.web.socket.EnableWebSocketSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.messaging.access.intercept.MessageMatcherDelegatingAuthorizationManager.Builder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;
import star.common.security.exception.handler.Rest401Handler;
import star.common.security.filter.RestJwtAuthFilter;

@Configuration
@EnableWebSocketSecurity
@RequiredArgsConstructor
public class WebSecurityConfig {
    private static final Long CORS_MAX_AGE = 3600L;

    //todo: oauth2 나중에 시큐리티 고도화
    private final RestJwtAuthFilter restJwtAuthFilter;
    private final Rest401Handler rest401Handler;

    @Bean
    public CorsConfigurationSource corsConfigurationSource() {
        CorsConfiguration configuration = new CorsConfiguration();
        configuration.setAllowedOrigins(
                List.of("http://localhost:3000", "http://localhost:5173", "https://localhost:3000",
                        "https://localhost:5173"));
        configuration.setAllowedMethods(
                List.of("GET", "POST", "PUT", "PATCH", "DELETE", "OPTIONS"));
        configuration.setAllowedHeaders(List.of("*"));
        configuration.setAllowCredentials(true);
        configuration.setMaxAge(CORS_MAX_AGE);

        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", configuration);
        return source;
    }

    @Bean
    public AuthorizationManager<Message<?>> messageAuthorizationManager(Builder messages) {
        messages
                .nullDestMatcher().permitAll()
                .simpDestMatchers("/broadcast/**").authenticated()
                .simpSubscribeDestMatchers("/subscribe/**").authenticated()
                .anyMessage().authenticated();

        return messages.build();
    }


    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http,
            CorsConfigurationSource corsConfigurationSource) throws Exception {
        return http
                .headers(headers -> headers.frameOptions(FrameOptionsConfig::sameOrigin))
                .cors(cors -> cors.configurationSource(corsConfigurationSource()))
                .authorizeHttpRequests(auth -> auth
                        .requestMatchers("/h2-console/**").permitAll()
                        .requestMatchers(HttpMethod.POST, "/**").authenticated()
                        .requestMatchers(HttpMethod.PUT, "/**").authenticated()
                        .requestMatchers(HttpMethod.PATCH, "/**").authenticated()
                        .requestMatchers(HttpMethod.DELETE, "/**").authenticated()
                        .requestMatchers("/upload/**").authenticated()
                        .anyRequest().permitAll()
                )
                .exceptionHandling(ex -> ex
                        .authenticationEntryPoint(rest401Handler)
                )
                .csrf(AbstractHttpConfigurer::disable)
                .sessionManagement(session -> session
                        .sessionCreationPolicy(SessionCreationPolicy.STATELESS)
                )
                .addFilterBefore(restJwtAuthFilter,
                        UsernamePasswordAuthenticationFilter.class)
                .build();
    }
}
