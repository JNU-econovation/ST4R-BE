package star.team.chat.redis.model.entity

import org.springframework.data.annotation.Id
import org.springframework.data.redis.core.RedisHash
import org.springframework.data.redis.core.index.Indexed
import java.time.LocalDateTime

@RedisHash("read:chat")
data class ChatLastReadTime(
    @Id
    val id: String,
    @Indexed
    val teamId: Long,
    val memberId: Long,
    var lastReadAt: LocalDateTime
)