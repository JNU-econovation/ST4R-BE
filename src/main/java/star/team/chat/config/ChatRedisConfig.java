package star.team.chat.config;

import com.fasterxml.jackson.databind.ObjectMapper;
import java.time.LocalDateTime;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.listener.ChannelTopic;
import org.springframework.data.redis.listener.RedisMessageListenerContainer;
import org.springframework.data.redis.listener.adapter.MessageListenerAdapter;
import org.springframework.data.redis.serializer.Jackson2JsonRedisSerializer;
import org.springframework.data.redis.serializer.StringRedisSerializer;
import star.team.chat.dto.response.ChatResponse;
import star.team.chat.service.internal.RedisChatSubscriber;

@Configuration
public class ChatRedisConfig {

    @Autowired
    private ObjectMapper objectMapper;


    @Bean
    public RedisTemplate<String, ChatResponse> chatResponseRedisTemplate(
            RedisConnectionFactory connectionFactory) {
        RedisTemplate<String, ChatResponse> template = new RedisTemplate<>();
        template.setConnectionFactory(connectionFactory);

        Jackson2JsonRedisSerializer<ChatResponse> serializer =
                new Jackson2JsonRedisSerializer<>(objectMapper, ChatResponse.class);

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
    public RedisMessageListenerContainer redisMessageListener(
            RedisConnectionFactory connectionFactory,
            MessageListenerAdapter listenerAdapter,
            ChannelTopic channelTopic
    ) {
        RedisMessageListenerContainer container = new RedisMessageListenerContainer();
        container.setConnectionFactory(connectionFactory);
        container.addMessageListener(listenerAdapter, channelTopic);
        return container;
    }

    @Bean
    public MessageListenerAdapter listenerAdapter(RedisChatSubscriber subscriber) {
        return new MessageListenerAdapter(subscriber, "onMessage");
    }

    @Bean
    public ChannelTopic channelTopic() {
        return new ChannelTopic("st4r-chat");
    }
}
