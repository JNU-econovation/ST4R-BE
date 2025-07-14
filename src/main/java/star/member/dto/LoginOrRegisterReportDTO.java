package star.member.dto;


import lombok.Builder;

@Builder
public record LoginOrRegisterReportDTO(
        boolean isRegister, MemberInfoDTO memberInfoDTO
) { }
