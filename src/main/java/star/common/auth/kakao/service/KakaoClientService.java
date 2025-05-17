package star.common.auth.kakao.service;

import java.util.Objects;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.HttpServerErrorException;
import star.common.auth.kakao.client.KakaoApiClient;
import star.common.auth.kakao.dto.KakaoMemberInfoDTO;
import star.common.auth.kakao.dto.client.response.KakaoErrorResponse;
import star.common.auth.kakao.exception.InvalidAuthCodeException;
import star.common.auth.kakao.exception.KakaoAuthServerException;
import star.common.exception.server.InternalServerException;
import star.member.model.vo.Email;

@Service
@Slf4j
public class KakaoClientService {

    private static final String CRITICAL_KAKAO_AUTH_ERROR_MESSAGE = "카카오 인증 관련 알 수 없는 에러 발생";

    private final KakaoApiClient kakaoApiClient;

    public KakaoClientService(KakaoApiClient kakaoApiClient) {
        this.kakaoApiClient = kakaoApiClient;
    }

    public String getAccessToken(String code) {
        try {
            return kakaoApiClient.getToken(code).accessToken();
        } catch (HttpClientErrorException e) {
            handleClientError(e);
        } catch (HttpServerErrorException e) {
            handleServerError(e);
        } catch (Exception e) {
            log.error(CRITICAL_KAKAO_AUTH_ERROR_MESSAGE, e);
        }
        throw new InternalServerException(CRITICAL_KAKAO_AUTH_ERROR_MESSAGE);
    }

    public KakaoMemberInfoDTO getMemberInfo(String kakaoAccessToken) {
        try {
            var userInfo = kakaoApiClient.getMemberInfo(kakaoAccessToken);
            var kakaoAccount = userInfo.kakaoAccount();
            return new KakaoMemberInfoDTO(new Email(kakaoAccount.email()));
        } catch (HttpClientErrorException e) {
            handleClientError(e);
        } catch (HttpServerErrorException e) {
            handleServerError(e);
        }
        throw new InternalServerException(CRITICAL_KAKAO_AUTH_ERROR_MESSAGE);
    }

    public void logout(String kakaoAccessToken) {
        kakaoApiClient.logout(kakaoAccessToken);
    }

//    public void unlinkKakao(KakaoMemberWithdrawDTO kakaoMemberWithdrawDTO) {
//        kakaoApiClient.unlinkKakao(kakaoMemberWithdrawDTO);
//    }

    private void handleClientError(HttpClientErrorException e) {
        KakaoErrorResponse kakaoErrorResponse = e.getResponseBodyAs(KakaoErrorResponse.class);
        if (Objects.requireNonNull(kakaoErrorResponse).KakaoErrorCode().equals("KOE320")) {
            throw new InvalidAuthCodeException();
        }
        log.error(kakaoErrorResponse.toString());
        throw new InternalServerException(CRITICAL_KAKAO_AUTH_ERROR_MESSAGE);
    }

    private void handleServerError(HttpServerErrorException e) {
        KakaoErrorResponse kakaoErrorResponse = e.getResponseBodyAs(KakaoErrorResponse.class);
        log.error(Objects.requireNonNull(kakaoErrorResponse).toString());
        throw new KakaoAuthServerException();
    }
}