package star.fortune.repository

import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository
import star.fortune.model.entity.Fortune
import star.member.constants.Constellation
import java.time.LocalDate

@Repository
interface FortuneRepository: JpaRepository<Fortune, Int> {
    fun findByConstellationAndDate(constellation: Constellation, date: LocalDate): Fortune?
}