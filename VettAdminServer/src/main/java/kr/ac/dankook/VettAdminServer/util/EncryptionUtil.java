package kr.ac.dankook.VettAdminServer.util;

import jakarta.annotation.PostConstruct;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;
import java.util.Base64;

@Component
@Slf4j
public class EncryptionUtil {

    @Value("${admin.secret}")
    private String adminSecret;

    private static String ADMIN_SECRET;

    @PostConstruct
    private void init() {
        ADMIN_SECRET = this.adminSecret;
    }

    public static String getEncryptedAdminKey() {
        try {
            Mac hmac = Mac.getInstance("HmacSHA256");
            SecretKeySpec keySpec = new SecretKeySpec(ADMIN_SECRET.getBytes(), "HmacSHA256");
            hmac.init(keySpec);
            byte[] hash = hmac.doFinal(ADMIN_SECRET.getBytes());
            return Base64.getUrlEncoder().withoutPadding().encodeToString(hash);
        } catch (Exception e) {
            log.error("Error Creating API Key - ", e);
            return null;
        }
    }
}
