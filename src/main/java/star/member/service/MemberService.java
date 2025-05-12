package star.member.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import star.common.exception.InternalServerException;
import star.common.security.jwt.JwtEncryptor;
import star.member.dto.MemberInfoDTO;
import star.member.dto.SocialRegisterDTO;
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

    @Transactional
    public MemberInfoDTO register(SocialRegisterDTO registerDTO) {

        if (memberRepository.existsByEmail(registerDTO.email())) {
            throw new MemberDuplicatedEmailException();
        }

        String encryptedAccessToken;

        try {
            encryptedAccessToken = jwtEncryptor.encryptToken(registerDTO.SocialAccessToken());
        } catch (Exception e) {
            throw new InternalServerException("jwt 토큰 암호화 에러");
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
    public MemberInfoDTO getMemberById(Long id) {
        Member member = memberRepository.findById(id).orElseThrow(
                () -> new MemberNotFoundException("id가 %d인 회원을 찾지 못했습니다.".formatted(id)));

        return MemberInfoDTO.from(member);
    }
}
