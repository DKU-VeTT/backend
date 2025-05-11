package kr.ac.dankook.VettChatServer.util;

import kr.ac.dankook.VettChatServer.exception.ApiErrorCode;
import kr.ac.dankook.VettChatServer.exception.EncryptException;

import javax.crypto.Cipher;
import javax.crypto.spec.SecretKeySpec;
import java.nio.ByteBuffer;
import java.util.Base64;

public class EncryptionUtil {

    private static final String ALGORITHM = "AES";
    private static final byte[] SECRET_KEY = "NaHCSecretKey123".getBytes();

    public static String encrypt(Long value) {
        try{
            SecretKeySpec secretKey = new SecretKeySpec(SECRET_KEY, ALGORITHM);
            Cipher cipher = Cipher.getInstance(ALGORITHM);
            cipher.init(Cipher.ENCRYPT_MODE, secretKey);
            byte[] valueBytes = ByteBuffer.allocate(Long.BYTES).putLong(value).array();
            byte[] encryptedData = cipher.doFinal(valueBytes);
            return Base64.getUrlEncoder().withoutPadding().encodeToString(encryptedData);
        }catch (Exception e){
            return String.valueOf(value);
        }
    }

    public static Long decrypt(String encryptedData) {
        try{
            SecretKeySpec secretKey = new SecretKeySpec(SECRET_KEY, ALGORITHM);
            Cipher cipher = Cipher.getInstance(ALGORITHM);
            cipher.init(Cipher.DECRYPT_MODE, secretKey);
            byte[] decodedData = Base64.getUrlDecoder().decode(encryptedData);
            byte[] decryptedBytes = cipher.doFinal(decodedData);
            return ByteBuffer.wrap(decryptedBytes).getLong();
        }catch (Exception e){
            throw new EncryptException(ApiErrorCode.DECRYPT_ERROR);
        }
    }
}
