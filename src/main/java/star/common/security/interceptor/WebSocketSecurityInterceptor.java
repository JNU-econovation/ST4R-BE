package star.common.security.interceptor;

import java.security.Principal;
import java.util.regex.Pattern;
import lombok.extern.slf4j.Slf4j;
import org.springframework.messaging.Message;
import org.springframework.messaging.MessageChannel;
import org.springframework.messaging.MessagingException;
import org.springframework.messaging.simp.stomp.StompCommand;
import org.springframework.messaging.simp.stomp.StompHeaderAccessor;
import org.springframework.messaging.support.ChannelInterceptor;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.stereotype.Component;
import star.common.security.dto.StarUserDetails;
import star.member.dto.MemberInfoDTO;
import star.team.service.TeamCoordinateService;

@Component
@Slf4j
public class WebSocketSecurityInterceptor implements ChannelInterceptor {
    private static final String PREVIEW_URL = "/member/queue/previews";

    private static final Pattern ALLOWED_SUBSCRIBE_PATTERN =
            Pattern.compile("^(?:/subscribe/\\d+|%s)$".formatted(PREVIEW_URL));

    private static final Pattern ALLOWED_SEND_PATTERN =
            Pattern.compile("^/(broadcast|markAsRead)/\\d+$");

    private final TeamCoordinateService teamCoordinateService;

    public WebSocketSecurityInterceptor(TeamCoordinateService teamCoordinateService) {
        this.teamCoordinateService = teamCoordinateService;
    }

    @Override
    public Message<?> preSend(Message<?> message, MessageChannel channel) {
        StompHeaderAccessor accessor = StompHeaderAccessor.wrap(message);
        String destination = accessor.getDestination();

        if (destination == null) {
            return message;
        }

        StompCommand command = accessor.getCommand();

        if (command == StompCommand.SUBSCRIBE || command == StompCommand.SEND) {
            boolean isSubscribe = command == StompCommand.SUBSCRIBE;
            Pattern pattern = isSubscribe ? ALLOWED_SUBSCRIBE_PATTERN : ALLOWED_SEND_PATTERN;

            if (!pattern.matcher(destination).matches()) {
                log.warn("허용되지 않은 {} URL: {}", isSubscribe ? "구독" : "발행", destination);
                throw new MessagingException("허용되지 않은 " + (isSubscribe ? "구독" : "발행") + " URL입니다.");
            }

            if (PREVIEW_URL.equals(destination)) {
                return message;
            }

            // 1. teamId 추출 (예: /websocket/subscribe/{teamId} or /websocket/broadcast/{teamId})
            Long teamId = extractTeamId(destination);

            // 2. 사용자 정보 추출
            Principal user = accessor.getUser();
            if (user == null) {
                log.warn("WebSocket 요청에 인증된 사용자 없음");
                throw new MessagingException("인증되지 않은 사용자입니다.");
            }

            MemberInfoDTO memberInfoDTO = extractMemberInfoFromPrincipal(user);

            if (!teamCoordinateService.existsTeamMember(teamId, memberInfoDTO.id())) {
                log.warn("memberInfo={} 는 teamId={} 의 팀원이 아님", memberInfoDTO, teamId);
                throw new MessagingException("해당 팀에 속하지 않은 사용자입니다.");
            }
        }

        return message;
    }

    private Long extractTeamId(String destination) {
        /* /subscribe/123 or /broadcast/123  */
        String[] tokens = destination.split("/");
        try {
            return Long.parseLong(tokens[2]);
        } catch (RuntimeException ex) {
            throw new MessagingException("올바르지 않은 url 입니다 -> " + destination);
        }
    }

    private MemberInfoDTO extractMemberInfoFromPrincipal(Principal principal) {
        if (principal instanceof UsernamePasswordAuthenticationToken token) {
            Object principalObj = token.getPrincipal();
            if (principalObj instanceof StarUserDetails userDetails) {
                return userDetails.getMemberInfoDTO();
            }
        }
        throw new MessagingException("올바르지 않은 사용자 id 입니다.");
    }


}

