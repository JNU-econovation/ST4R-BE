package star.common.auth.kakao.service;


import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import star.common.auth.kakao.dto.KakaoMemberInfoDTO;
import star.member.dto.MemberInfoDTO;
import star.member.exception.LoginFailedException;
import star.member.service.MemberService;


@Service
@RequiredArgsConstructor
public class KakaoAuthService {

    private final KakaoClientService kakaoClientService;
    private final MemberService memberService;



    public MemberInfoDTO loginOrRegister(String authorizationCode) {
        String accessToken = kakaoClientService.getAccessToken(authorizationCode);
        KakaoMemberInfoDTO kakaoMemberInfoDTO = kakaoClientService.getMemberInfo(accessToken);

        try {

            // -> LoginFailedException 을 던질 수 있음
            MemberInfoDTO memberInfoDTO = memberService.login(kakaoMemberInfoDTO);

            memberService.setMemberAccessToken(memberInfoDTO.id(), accessToken);
            return memberDTOWithActualId;
        } catch (LoginFailedException e) { // 유저를 못 찾거나 탈퇴한 유저라면 회원가입
            MemberDTO memberDTOWithActualId =  memberService.register(kakaoMemberInfoDTO);

            memberService.setMemberAccessToken(memberDTOWithActualId.id(), accessToken);
            return memberDTOWithActualId;
        }
    }
}
