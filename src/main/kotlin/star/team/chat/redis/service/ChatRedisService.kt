package star.team.chat.redis.service

import org.springframework.data.repository.findByIdOrNull
import org.springframework.stereotype.Service
import star.team.chat.redis.model.entity.ChatLastReadTime
import star.team.chat.redis.repository.ChatLastReadTimeRepository
import java.time.LocalDateTime

@Service
class ChatRedisService(
    private val repository: ChatLastReadTimeRepository
) {

    fun markAsRead(teamId: Long, memberId: Long, readTime: LocalDateTime) {
        val key = generateId(teamId, memberId)
        val entity = repository.findByIdOrNull(key)
            ?.apply { this.lastReadAt = readTime }
            ?: ChatLastReadTime(key, teamId, memberId, readTime)

        repository.save(entity)
    }

    fun getLastReadTime(teamId: Long, memberId: Long): LocalDateTime {
        val key = generateId(teamId, memberId)

        return repository.findByIdOrNull(key)
            ?.lastReadAt
            ?: LocalDateTime.of(1970, 1, 1, 0, 0)
    }

    fun deleteAllByTeamId(teamId: Long) {
        repository.deleteAllByTeamId(teamId)
    }

    private fun generateId(teamId: Long, memberId: Long): String {
        return "$teamId:$memberId"
    }

}