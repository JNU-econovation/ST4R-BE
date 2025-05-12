package star.member.service;

import java.util.Optional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import star.common.auth.kakao.dto.KakaoMemberInfoDTO;
import star.common.exception.InternalServerException;
import star.common.security.jwt.JwtEncryptor;
import star.member.dto.MemberInfoDTO;
import star.member.dto.SocialRegisterDTO;
import star.member.exception.LoginFailedException;
import star.member.exception.MemberDuplicatedEmailException;
import star.member.exception.MemberNotFoundException;
import star.member.model.entity.Member;
import star.member.model.vo.Email;
import star.member.repository.MemberRepository;

@Service
@RequiredArgsConstructor
public class MemberService {

    private final MemberRepository memberRepository;
    private final JwtEncryptor jwtEncryptor;

    @Transactional(readOnly = true)
    public MemberInfoDTO login(KakaoMemberInfoDTO kakaoMemberInfoDTO) {
        Member member = memberRepository.findByEmail(kakaoMemberInfoDTO.email())
                .orElseThrow(LoginFailedException::new);

        return MemberInfoDTO.from(member);
    }

    @Transactional
    public MemberInfoDTO register(SocialRegisterDTO registerDTO) {

        if (memberRepository.existsByEmail(registerDTO.email())) {
            throw new MemberDuplicatedEmailException();
        }
        try {
            String encryptedAccessToken = encryptToken(registerDTO.SocialAccessToken());
        } catch (Exception e) {
            
        }

        Member newMember = Member
                .builder()
                .email(registerDTO.email())
                .encryptedKakaoAccessToken(encryptedAccessToken)
                .profileImageUrl(registerDTO.profileImageUrl())
                .build();

        memberRepository.save(newMember);

        return MemberInfoDTO.from(newMember);
    }

    @Transactional(readOnly = true)
    public MemberInfoDTO getMemberById(Long memberId) {
        Member member = getOptionalMemberEntity(memberId).orElseThrow(
                () -> new MemberNotFoundException("id가 %d인 회원을 찾지 못했습니다.".formatted(memberId)));

        return MemberInfoDTO.from(member);
    }

    @Transactional
    public void setMemberAccessToken(Long memberId, String plainAccessToken) {
        Member member = getOptionalMemberEntity(memberId).orElseThrow(
                () -> new MemberNotFoundException("id가 %d인 회원을 찾지 못했습니다.".formatted(memberId)));

        String encryptedAccessToken;


    }

    private Optional<Member> getOptionalMemberEntity(Long memberId) {
        return memberRepository.findById(memberId);
    }

    private String encryptToken(String plainToken) {
        return jwtEncryptor.encryptToken(plainToken);
    }
}
