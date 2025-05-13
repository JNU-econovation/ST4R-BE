package star.common.security.encryption.jwt;

import io.jsonwebtoken.JwtException;
import io.jsonwebtoken.JwtParser;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import jakarta.annotation.PostConstruct;
import java.util.Date;
import javax.crypto.SecretKey;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import star.member.dto.MemberInfoDTO;

@Component
public class JwtManager {
    //todo: 나중에 refresh token도 구현하기
    private final static Integer HOUR_SECOND = 3600;
    private final static Integer SECOND_MILLI = 1000;
    private final static Integer EXPIRED_MILLISECOND = 6 * HOUR_SECOND * SECOND_MILLI;

    @Value("${jwt-secret-key}")
    private String jwtSecretKey;

    private SecretKey key;
    private JwtParser jwtParser;

    @PostConstruct
    public void init() {
        this.key = Keys.hmacShaKeyFor(jwtSecretKey.getBytes());
        this.jwtParser = Jwts.parser().verifyWith(key).build();
    }

    public Long extractId(String token) {
        return Long.valueOf(jwtParser
                .parseSignedClaims(token)
                .getPayload()
                .getSubject());
    }


    public String generateToken(MemberInfoDTO memberInfoDTO) {
        return Jwts.builder()
                .subject(memberInfoDTO.id().toString())
                .claim("id", memberInfoDTO.id())
                .claim("email", memberInfoDTO.email().value())
                .issuedAt(new Date(System.currentTimeMillis()))
                .expiration(new Date(System.currentTimeMillis() + EXPIRED_MILLISECOND))
                .signWith(this.key)
                .compact();
    }

    public Boolean validateToken(String token) {
        try {
            jwtParser.parseSignedClaims(token);
            return !isTokenExpired(token);
        } catch (JwtException e) {
            return false;
        }
    }

    private Boolean isTokenExpired(String token) {
        return jwtParser.parseSignedClaims(token).getPayload().getExpiration().before(new Date());
    }
}