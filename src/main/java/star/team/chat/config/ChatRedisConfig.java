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
import star.team.chat.dto.broadcast.ChatBroadcast;
import star.team.chat.dto.response.ChatPreviewResponse;
import star.team.chat.service.internal.RedisChatPreviewSubscriber;
import star.team.chat.service.internal.RedisChatSubscriber;

@Configuration
public class ChatRedisConfig {

    @Autowired
    private ObjectMapper objectMapper;


    @Bean
    public RedisTemplate<String, ChatBroadcast> chatResponseRedisTemplate(
            RedisConnectionFactory connectionFactory) {
        RedisTemplate<String, ChatBroadcast> template = new RedisTemplate<>();
        template.setConnectionFactory(connectionFactory);

        Jackson2JsonRedisSerializer<ChatBroadcast> serializer =
                new Jackson2JsonRedisSerializer<>(objectMapper, ChatBroadcast.class);

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
    public RedisTemplate<String, ChatPreviewResponse> chatPreviewResponseRedisTemplate(
            RedisConnectionFactory connectionFactory) {
        RedisTemplate<String, ChatPreviewResponse> template = new RedisTemplate<>();
        template.setConnectionFactory(connectionFactory);

        Jackson2JsonRedisSerializer<ChatPreviewResponse> serializer =
                new Jackson2JsonRedisSerializer<>(objectMapper, ChatPreviewResponse.class);

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
            ChannelTopic channelTopic,
            MessageListenerAdapter previewListenerAdapter,
            ChannelTopic previewChannelTopic
    ) {
        RedisMessageListenerContainer container = new RedisMessageListenerContainer();
        container.setConnectionFactory(connectionFactory);
        container.addMessageListener(listenerAdapter, channelTopic);
        container.addMessageListener(previewListenerAdapter, previewChannelTopic);

        return container;
    }

    @Bean
    public MessageListenerAdapter listenerAdapter(RedisChatSubscriber subscriber) {
        return new MessageListenerAdapter(subscriber, "onChatMessage");
    }

    @Bean
    public MessageListenerAdapter previewListenerAdapter(RedisChatPreviewSubscriber subscriber) {
        return new MessageListenerAdapter(subscriber, "onPreviewUpdate");
    }

    @Bean
    public ChannelTopic channelTopic() {
        return new ChannelTopic("st4r-chat");
    }

    @Bean
    public ChannelTopic previewChannelTopic() {
        return new ChannelTopic("st4r-chat-preview");
    }
}
