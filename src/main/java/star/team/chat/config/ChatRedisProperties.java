package star.team.chat.config;

import lombok.Getter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

@Getter
@Configuration
@ConfigurationProperties(prefix = "chat.redis")
public class ChatRedisProperties {
    private int maxSize;
    private int syncSize;
}