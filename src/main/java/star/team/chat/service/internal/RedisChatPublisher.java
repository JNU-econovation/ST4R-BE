package star.team.chat.service.internal;

import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.listener.ChannelTopic;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import star.team.chat.dto.broadcast.ChatBroadcast;
import star.team.chat.dto.response.ChatPreviewResponse;

@Service
@RequiredArgsConstructor
public class RedisChatPublisher {

    private final RedisTemplate<String, ChatBroadcast> chatRedisTemplate;
    private final RedisTemplate<String, ChatPreviewResponse> chatPreviewRedisTemplate;

    @Async
    public void publishChatAsync(ChannelTopic topic, ChatBroadcast chatBroadcast) {
        chatRedisTemplate.convertAndSend(topic.getTopic(), chatBroadcast);
    }

    @Async
    public void publishChatPreviewAsync(ChannelTopic topic, ChatPreviewResponse chatPreviewResponse) {
        chatPreviewRedisTemplate.convertAndSend(topic.getTopic(), chatPreviewResponse);
    }
}
