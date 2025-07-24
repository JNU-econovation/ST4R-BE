package star.common.exception;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import star.common.constants.Domain;

@Getter
@RequiredArgsConstructor
public enum ErrorCode {

    // --- COMMON ---
    INVALID_INPUT_VALUE(Domain.COMMON, HttpStatus.BAD_REQUEST, 1, "유효하지 않은 입력 값입니다."),
    INVALID_PAGEABLE_FIELD(Domain.COMMON, HttpStatus.BAD_REQUEST, 2, "페이징 가능한 필드가 아닙니다. -> %s = %s"),
    TOO_MANY_IMAGE_URLS(Domain.COMMON, HttpStatus.BAD_REQUEST, 3, "이미지는 최대 %d개 까지 가능합니다."),
    INCOMPATIBLE_REQUEST_PARAMETERS(Domain.COMMON, HttpStatus.BAD_REQUEST, 4, "옵션 %s 와(과) %s은 동시에 사용할 수 없습니다."),
    BAD_DATA_SYNTAX(Domain.COMMON, HttpStatus.BAD_REQUEST, 5, "%s"),
    YOU_ARE_NOT_AUTHOR(Domain.COMMON, HttpStatus.FORBIDDEN, 1, "작성자가 아닙니다."),
    ALREADY_HEARTED(Domain.COMMON, HttpStatus.CONFLICT, 2, "이미 좋아요를 눌렀습니다."),
    HEART_NOT_FOUND(Domain.COMMON, HttpStatus.NOT_FOUND, 1, "좋아요를 찾을 수 없습니다."),
    BAD_DATA_MEANING(Domain.COMMON, HttpStatus.UNPROCESSABLE_ENTITY, 1, "%s"),

    INTERNAL_SERVER_ERROR(Domain.COMMON, HttpStatus.INTERNAL_SERVER_ERROR, 1, "서버 내부 오류입니다."),
    S3_UNKNOWN_ERROR(Domain.COMMON, HttpStatus.INTERNAL_SERVER_ERROR, 2, "S3 관련 알 수 없는 오류입니다."),

    // --- AUTH ---
    INVALID_REDIRECT_URI(Domain.AUTH, HttpStatus.BAD_REQUEST, 1, "유효하지 않은 Redirect URI 입니다. -> %s"),
    INVALID_AUTH_CODE(Domain.AUTH, HttpStatus.BAD_REQUEST, 2, "유효하지 않은 인가 코드입니다."),
    KAKAO_AUTH_SERVER_ERROR(Domain.AUTH, HttpStatus.INTERNAL_SERVER_ERROR, 1, "카카오 인증 서버에 오류가 발생했습니다."),
    KAKAO_AUTH_UNKNOWN_ERROR(Domain.AUTH, HttpStatus.INTERNAL_SERVER_ERROR, 2, "카카오 인증 관련 알 수 없는 오류가 발생했습니다."),

    // --- SECURITY ---
    ENCRYPTION_ERROR(Domain.SECURITY, HttpStatus.INTERNAL_SERVER_ERROR, 1, "암호화/복호화 중 예상치 못한 에러가 발생했습니다."),
    UNKNOWN_FILTER_ERROR(Domain.SECURITY, HttpStatus.INTERNAL_SERVER_ERROR, 2, "Spring Security Filter 에서 예상치 못한 에러가 발생했습니다."),
    UNAUTHORIZED_ERROR(Domain.SECURITY, HttpStatus.UNAUTHORIZED, 1, "인증이 필요합니다."),
    UNKNOWN_FORBIDDEN_ERROR(Domain.SECURITY, HttpStatus.FORBIDDEN, 0, "권한 관련 확인되지 않은 오류입니다."),
    REGISTER_NOT_COMPLETED_ERROR(Domain.SECURITY, HttpStatus.FORBIDDEN, 1, "회원가입이 미완료 된 사용자입니다."),
    ALREADY_WITHDRAW_ERROR(Domain.SECURITY, HttpStatus.FORBIDDEN, 2, "이미 탈퇴한 사용자입니다."),

    // --- MEMBER ---
    ALREADY_INVALIDATED_TOKEN(Domain.MEMBER, HttpStatus.BAD_REQUEST, 1, "이미 유효하지 않은 토큰입니다."),
    TEAM_LEADER_CANNOT_WITHDRAW_MEMBER(Domain.MEMBER, HttpStatus.BAD_REQUEST, 2, "모임장은 회원 탈퇴할 수 없습니다. 모임장을 위임한 후에 팀을 나가시거나, 모임장 권한으로 팀을 삭제해주세요.\n해당 모임 : %s"),
    MEMBER_NOT_FOUND(Domain.MEMBER, HttpStatus.INTERNAL_SERVER_ERROR, 1, "id가 %d인 회원을 찾을 수 없습니다."),
    MEMBER_DUPLICATED_FIELD(Domain.MEMBER, HttpStatus.CONFLICT, 1, "이미 사용중인 %s 입니다."),
    ALREADY_COMPLETED_REGISTRATION(Domain.MEMBER, HttpStatus.CONFLICT, 2, "이미 가입이 완료된 회원입니다."),
    ALREADY_WITHDRAW_REGISTRATION(Domain.MEMBER, HttpStatus.CONFLICT, 3, "이미 탈퇴한 회원입니다."),

    // --- BOARD ---
    ALREADY_CANCELED_HEART(Domain.BOARD, HttpStatus.CONFLICT, 1, "이미 좋아요를 취소했습니다."),
    BOARD_NOT_FOUND(Domain.BOARD, HttpStatus.NOT_FOUND, 1, "해당 게시글을 찾을 수 없습니다."),

    // --- CATEGORY ---
    CATEGORY_NOT_FOUND(Domain.CATEGORY, HttpStatus.NOT_FOUND, 1, "해당 카테고리를 찾을 수 없습니다. -> %s"),

    // --- COMMENT ---
    INVALID_ID_COMMENT(Domain.COMMENT, HttpStatus.BAD_REQUEST, 1, "유효하지 않은 댓글 ID 입니다."),

    // --- TEAM ---
    CANNOT_BAN_SELF(Domain.TEAM, HttpStatus.BAD_REQUEST, 1, "자기 자신을 강퇴할 수 없습니다."),
    EMPTY_PARTICIPANT(Domain.TEAM, HttpStatus.BAD_REQUEST, 2, "팀에 참가자가 없습니다."),
    FULL_PARTICIPANT(Domain.TEAM, HttpStatus.BAD_REQUEST, 3, "팀 참가자가 가득 찼습니다."),
    INVALID_TEAM_PASSWORD(Domain.TEAM, HttpStatus.BAD_REQUEST, 4, "팀 비밀번호가 유효하지 않습니다."),
    NEW_PASSWORD_SAME_AS_OLD(Domain.TEAM, HttpStatus.BAD_REQUEST, 5, "새 비밀번호가 이전 비밀번호와 동일합니다."),
    TARGET_IS_NOT_BANNED(Domain.TEAM, HttpStatus.BAD_REQUEST, 6, "강퇴된 대상이 아닙니다."),
    TEAM_LEADER_CANNOT_LEAVE(Domain.TEAM, HttpStatus.BAD_REQUEST, 7, "팀장은 팀을 떠날 수 없습니다. 다른 멤버에게 리더를 위임하세요."),
    TEAM_LEADER_SELF_DELEGATING(Domain.TEAM, HttpStatus.BAD_REQUEST, 8, "팀장은 자기 자신에게 리더를 위임할 수 없습니다."),
    YOU_ARE_BANNED(Domain.TEAM, HttpStatus.FORBIDDEN, 1, "당신은 이 팀에서 강퇴당했습니다."),
    YOU_ARE_NOT_TEAM_LEADER(Domain.TEAM, HttpStatus.FORBIDDEN, 2, "당신은 팀 리더가 아닙니다."),
    YOU_ALREADY_JOINED_TEAM(Domain.TEAM, HttpStatus.CONFLICT, 1, "이미 해당 팀에 소속되어 있습니다."),
    TEAM_MEMBER_NOT_FOUND(Domain.TEAM, HttpStatus.NOT_FOUND, 1, "팀 멤버를 찾을 수 없습니다."),
    TEAM_NOT_FOUND(Domain.TEAM, HttpStatus.NOT_FOUND, 2, "팀을 찾을 수 없습니다."),

    // --- CHAT ---
    YOU_ARE_NOT_IN_CHAT_ROOM(Domain.CHAT, HttpStatus.FORBIDDEN, 1, "채팅방에 소속되어 있지 않습니다.");


    private final Domain domain;
    private final HttpStatus status;
    private final int number;
    private final String message;

    public String getCode() {
        return String.format("%s_%d_%03d", this.domain.name(), this.status.value(), this.number);
    }
}

