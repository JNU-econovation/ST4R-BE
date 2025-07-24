package star.common.infra.websocket.config;

import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Configuration;
import org.springframework.messaging.simp.config.ChannelRegistration;
import org.springframework.messaging.simp.config.MessageBrokerRegistry;
import org.springframework.web.socket.config.annotation.EnableWebSocketMessageBroker;
import org.springframework.web.socket.config.annotation.StompEndpointRegistry;
import org.springframework.web.socket.config.annotation.WebSocketMessageBrokerConfigurer;
import star.common.security.config.AllowedOriginsProperties;
import star.common.security.exception.handler.websocket.StompSecurityExceptionHandler;
import star.common.security.interceptor.WebSocketJwtAuthInterceptor;
import star.common.security.interceptor.WebSocketSecurityInterceptor;

@Configuration
@EnableWebSocketMessageBroker
@RequiredArgsConstructor
public class WebSocketConfig implements WebSocketMessageBrokerConfigurer {

    private final WebSocketJwtAuthInterceptor jwtAuthInterceptor;
    private final WebSocketSecurityInterceptor securityInterceptor;
    private final AllowedOriginsProperties originsProperties;
    private final StompSecurityExceptionHandler exceptionHandler;

    @Override
    public void registerStompEndpoints(StompEndpointRegistry registry) {
        registry.addEndpoint("/websocket/connect")
                .setAllowedOriginPatterns(
                        originsProperties.getAllowedFeRedirectOrigins().toArray(new String[0])
                )
                .withSockJS();

        registry.setErrorHandler(exceptionHandler);
    }

    @Override
    public void configureMessageBroker(MessageBrokerRegistry registry) {
        registry.setUserDestinationPrefix("/member");
        registry.enableSimpleBroker("/subscribe", "/queue");
    }

    @Override
    public void configureClientInboundChannel(ChannelRegistration registration) {
        registration.interceptors(jwtAuthInterceptor).interceptors(securityInterceptor);
    }

}
