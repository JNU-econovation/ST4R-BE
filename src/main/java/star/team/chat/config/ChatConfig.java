package star.team.chat.config;

import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Configuration;
import org.springframework.messaging.simp.config.ChannelRegistration;
import org.springframework.messaging.simp.config.MessageBrokerRegistry;
import org.springframework.web.socket.config.annotation.EnableWebSocketMessageBroker;
import org.springframework.web.socket.config.annotation.StompEndpointRegistry;
import org.springframework.web.socket.config.annotation.WebSocketMessageBrokerConfigurer;
import star.common.security.interceptor.WebSocketJwtAuthInterceptor;
import star.common.security.interceptor.WebSocketSecurityInterceptor;

@Configuration
@EnableWebSocketMessageBroker
@RequiredArgsConstructor
public class ChatConfig implements WebSocketMessageBrokerConfigurer {

    private final WebSocketJwtAuthInterceptor jwtAuthInterceptor;
    private final WebSocketSecurityInterceptor securityInterceptor;

    @Override
    public void registerStompEndpoints(StompEndpointRegistry registry) {
        registry.addEndpoint("/websocket/connect")
                .setAllowedOriginPatterns("http://localhost:*", "https://localhost:*")
                .withSockJS();
    }

    @Override
    public void configureMessageBroker(MessageBrokerRegistry registry) {
        registry.setApplicationDestinationPrefixes("/websocket");
        registry.enableSimpleBroker("/subscribe", "/queue");
    }

    @Override
    public void configureClientInboundChannel(ChannelRegistration registration) {
        registration.interceptors(jwtAuthInterceptor).interceptors(securityInterceptor);
    }

}
