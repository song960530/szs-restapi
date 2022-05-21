package com.codetest.szsrestapi.global.util.cipher;

import com.codetest.szsrestapi.global.config.properties.AES256Properties;
import com.codetest.szsrestapi.global.exception.CipherException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import javax.crypto.Cipher;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;
import java.nio.charset.StandardCharsets;
import java.util.Base64;

@Slf4j
@Component
@RequiredArgsConstructor
public class AES256Util {
    private String algo = "AES/CBC/PKCS5Padding";
    private Cipher cipher;
    private SecretKeySpec keySpec;
    private IvParameterSpec ivParamSpec;
    private final AES256Properties aes256Properties;

    @PostConstruct
    public void init() {
        try {
            cipher = Cipher.getInstance(algo);
            keySpec = new SecretKeySpec(aes256Properties.getKey().getBytes(StandardCharsets.UTF_8), "AES"); // 비밀키 생성
            ivParamSpec = new IvParameterSpec(aes256Properties.getIv().getBytes(StandardCharsets.UTF_8));
        } catch (Exception e) {
            throw new CipherException("AES256 초기화 중 오류 발생");
        }
    }


    public String encrypt(String rawString) {
        String encString = "";

        try {
            cipher.init(Cipher.ENCRYPT_MODE, keySpec, ivParamSpec); // 암호화 적용
            byte[] encBytes = cipher.doFinal(rawString.getBytes(StandardCharsets.UTF_8));

            encString = Base64.getEncoder().encodeToString(encBytes); // 암호화 인코딩 후 저장
        } catch (Exception e) {
            throw new CipherException("AES256 암호화 중 오류 발생");
        }
        return encString;
    }

    public String decrypt(String encString) {
        String rawString = "";
        try {
            cipher.init(Cipher.DECRYPT_MODE, keySpec, ivParamSpec); // 암호화 적용
            byte[] decBytes = Base64.getDecoder().decode(encString); // 암호 해석

            rawString = new String(cipher.doFinal(decBytes));
        } catch (Exception e) {
            throw new CipherException("AES256 복호화 중 오류 발생");
        }
        return rawString;
    }
}
