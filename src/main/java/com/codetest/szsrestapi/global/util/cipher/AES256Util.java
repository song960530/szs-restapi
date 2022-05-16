package com.codetest.szsrestapi.global.util.cipher;

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
public class AES256Util {
    private String algo = "AES/CBC/PKCS5Padding";
    private String key = "abcdefghabcdefghabcdefghabcdefgh"; // 32Byte
    private String iv = "0123456789abcdef"; // 16Byte
    Cipher cipher;
    SecretKeySpec keySpec;
    IvParameterSpec ivParamSpec;

    @PostConstruct
    protected void init() {
        try {
            cipher = Cipher.getInstance(algo);
            keySpec = new SecretKeySpec(key.getBytes(StandardCharsets.UTF_8), "AES"); // 비밀키 생성
            ivParamSpec = new IvParameterSpec(iv.getBytes(StandardCharsets.UTF_8));
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    public String encrypt(String rawString) {
        String encString = "";

        try {
            cipher.init(Cipher.ENCRYPT_MODE, keySpec, ivParamSpec); // 암호화 적용
            byte[] encBytes = cipher.doFinal(rawString.getBytes(StandardCharsets.UTF_8));

            encString = Base64.getEncoder().encodeToString(encBytes); // 암호화 인코딩 후 저장
        } catch (Exception e) {
            e.printStackTrace();
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
            e.printStackTrace();
        }
        return rawString;
    }
}
