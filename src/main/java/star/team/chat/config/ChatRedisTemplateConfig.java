package star.team.chat.config;

import com.fasterxml.jackson.databind.ObjectMapper;
import java.time.LocalDateTime;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.serializer.GenericToStringSerializer;
import org.springframework.data.redis.serializer.Jackson2JsonRedisSerializer;
import org.springframework.data.redis.serializer.StringRedisSerializer;
import star.team.chat.dto.ChatDTO;

@Configuration
public class ChatRedisTemplateConfig {

    @Autowired
    private ObjectMapper objectMapper;

    //Redis 서버와 연결하는 중간 다리
    @Bean
    public RedisTemplate<String, ChatDTO> chatDTORedisTemplate(
            RedisConnectionFactory connectionFactory) {
        RedisTemplate<String, ChatDTO> template = new RedisTemplate<>();
        template.setConnectionFactory(connectionFactory);

        Jackson2JsonRedisSerializer<ChatDTO> serializer =
                new Jackson2JsonRedisSerializer<>(objectMapper, ChatDTO.class);

        template.setDefaultSerializer(serializer);
        template.setKeySerializer(new StringRedisSerializer());
        template.setHashKeySerializer(new StringRedisSerializer());
        template.setValueSerializer(serializer);
        template.setHashValueSerializer(serializer);

        return template;

    }

    @Bean
    public RedisTemplate<String, LocalDateTime> localDateTimeRedisTemplate(
            RedisConnectionFactory connectionFactory) {

        RedisTemplate<String, LocalDateTime> template = new RedisTemplate<>();
        template.setConnectionFactory(connectionFactory);

        Jackson2JsonRedisSerializer<LocalDateTime> serializer =
                new Jackson2JsonRedisSerializer<>(objectMapper, LocalDateTime.class);

        template.setDefaultSerializer(serializer);
        template.setKeySerializer(new StringRedisSerializer());
        template.setHashKeySerializer(new StringRedisSerializer());
        template.setValueSerializer(serializer);
        template.setHashValueSerializer(serializer);

        return template;
    }

    @Bean
    public RedisTemplate<String, Long> longRedisTemplate(RedisConnectionFactory connectionFactory) {
        RedisTemplate<String, Long> template = new RedisTemplate<>();
        template.setConnectionFactory(connectionFactory);
        template.setKeySerializer(new StringRedisSerializer());
        template.setValueSerializer(new GenericToStringSerializer<>(Long.class));
        template.afterPropertiesSet();
        return template;
    }


}
