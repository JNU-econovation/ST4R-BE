package star.common.security.jwt;

import java.util.Base64;
import javax.crypto.Cipher;
import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Component
public class JwtEncryptor {

    private static final String ALGORITHM = "AES";

    @Value("${jwe-secret-key}")
    private String jweSecretKeyBase64;

    private SecretKey getSecretKeyFromBase64() {
        byte[] decodedKey = Base64.getDecoder().decode(jweSecretKeyBase64);
        return new SecretKeySpec(decodedKey, 0, decodedKey.length, ALGORITHM);
    }

    public String encryptToken(String plainTextToken) throws Exception {
        SecretKey secretKey = getSecretKeyFromBase64();

        Cipher cipher = Cipher.getInstance(ALGORITHM);
        cipher.init(Cipher.ENCRYPT_MODE, secretKey);
        byte[] encryptedBytes = cipher.doFinal(plainTextToken.getBytes());
        return Base64.getEncoder().encodeToString(encryptedBytes);
    }

    public String decryptToken(String encryptedToken) throws Exception {
        SecretKey secretKey = getSecretKeyFromBase64();

        Cipher cipher = Cipher.getInstance(ALGORITHM);
        cipher.init(Cipher.DECRYPT_MODE, secretKey);
        byte[] decryptedBytes = cipher.doFinal(Base64.getDecoder().decode(encryptedToken));
        return new String(decryptedBytes);
    }

}
