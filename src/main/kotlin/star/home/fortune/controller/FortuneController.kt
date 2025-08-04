package star.home.fortune.controller

import org.springframework.http.ResponseEntity
import org.springframework.security.core.annotation.AuthenticationPrincipal
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController
import star.common.security.dto.StarUserDetails
import star.home.fortune.dto.response.FortuneResponse
import star.home.fortune.service.FortuneService

@RestController
@RequestMapping("/home/fortune")
class FortuneController(
    private val service: FortuneService
) {
    @GetMapping
    fun getFortune(@AuthenticationPrincipal userDetails: StarUserDetails): ResponseEntity<FortuneResponse> {
        return ResponseEntity.ok(service.getTodayFortune(userDetails.memberInfoDTO))
    }
}