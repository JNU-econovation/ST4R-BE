package star.team.chat.redis.repository

import org.springframework.data.repository.CrudRepository
import star.team.chat.redis.model.entity.ChatLastReadTime

interface ChatLastReadTimeRepository : CrudRepository<ChatLastReadTime, String> {

    override fun existsById(id: String): Boolean

    fun deleteAllByTeamId(teamId: Long)
}