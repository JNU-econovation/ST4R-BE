package star.member.service;

import java.util.Optional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import star.common.auth.kakao.dto.KakaoMemberInfoDTO;
import star.common.security.EncryptionException;
import star.common.security.encryption.util.AESEncryptionUtil;
import star.member.dto.MemberInfoDTO;
import star.member.dto.SocialRegisterDTO;
import star.member.dto.reqeust.CompleteRegistrationRequest;
import star.member.exception.AlreadyCompletedRegistrationException;
import star.member.exception.AlreadyInvalidatedTokenException;
import star.member.exception.MemberDuplicatedEmailException;
import star.member.exception.MemberNotFoundException;
import star.member.model.entity.Member;
import star.member.model.vo.MemberStatus;
import star.member.repository.MemberRepository;

@Service
@RequiredArgsConstructor
@Slf4j
public class MemberService {

    private final MemberRepository memberRepository;
    private final AESEncryptionUtil aesEncryptionUtil;

    @Transactional(readOnly = true)
    public Optional<MemberInfoDTO> login(KakaoMemberInfoDTO kakaoMemberInfoDTO) {
        Optional<Member> optionalMember = memberRepository.findByEmail(kakaoMemberInfoDTO.email());

        return optionalMember.map(MemberInfoDTO::from);
    }

    @Transactional
    public MemberInfoDTO processRegistration(SocialRegisterDTO registerDTO) {

        if (memberRepository.existsByEmail(registerDTO.email())) {
            throw new MemberDuplicatedEmailException();
        }

        Member newMember = Member
                .builder()
                .email(registerDTO.email())
                .build();

        memberRepository.save(newMember);

        return MemberInfoDTO.from(newMember);
    }

    @Transactional
    public void completeRegistration(
            MemberInfoDTO memberInfo, CompleteRegistrationRequest registrationDTO
    ) {
        Member member = getMemberEntityById(memberInfo.id());

        if(member.getStatus() == MemberStatus.REGISTER_COMPLETED) {
            throw new AlreadyCompletedRegistrationException();
        }

        member.completeRegistration(
                registrationDTO.birthDate(),
                registrationDTO.gender(),
                registrationDTO.nickname(),
                registrationDTO.profileImageUrl()
        );
    }

    @Transactional(readOnly = true)
    public MemberInfoDTO getMemberById(Long memberId) {
        return MemberInfoDTO.from(getMemberEntityById(memberId));
    }

    @Transactional(readOnly = true)
    public Member getMemberEntityById(Long memberId) {
        return memberRepository.findById(memberId).orElseThrow(
                () -> new MemberNotFoundException(memberId));
    }

    @Transactional(readOnly = true)
    public MemberStatus getMemberStatusById(Long memberId) {
        return memberRepository.getStatusById(memberId);
    }

    @Transactional
    public void updateAccessToken(Long memberId, String plainAccessToken) {
        Member member = getMemberEntityById(memberId);
        String encryptedAccessToken = encryptToken(plainAccessToken);
        member.updateEncryptedKakaoAccessToken(encryptedAccessToken);
    }

    @Transactional
    public String invalidateAccessToken(Long memberId) {
        Member member = getMemberEntityById(memberId);
        String encryptedKakaoAccessToken = member.getEncryptedKakaoAccessToken();

        if (encryptedKakaoAccessToken == null) {
            throw new AlreadyInvalidatedTokenException();
        }
        String decryptedKakaoAccessToken = decryptToken(encryptedKakaoAccessToken);

        member.invalidateKakaoAccessToken();

        return decryptedKakaoAccessToken;
    }

    private String encryptToken(String plainToken) {
        final String CRITICAL_ENCRYPT_ERROR_MESSAGE = "토큰 암호화 중 예상치 못한 에러 발생";
        try {
            return aesEncryptionUtil.encrypt(plainToken);
        } catch (Exception e) {
            log.error(CRITICAL_ENCRYPT_ERROR_MESSAGE, e);
            throw new EncryptionException();
        }
    }

    private String decryptToken(String encryptedToken) {
        final String CRITICAL_ENCRYPT_ERROR_MESSAGE = "토큰 복호화 중 예상치 못한 에러 발생";
        try {
            return aesEncryptionUtil.decrypt(encryptedToken);
        } catch (Exception e) {
            log.error(CRITICAL_ENCRYPT_ERROR_MESSAGE, e);
            throw new EncryptionException();
        }
    }

}
