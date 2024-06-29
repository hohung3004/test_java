package com.project.javatestfresher.util;

import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.RandomStringUtils;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.SecureRandom;

@Slf4j
public class HashUtil {
    public static final SecureRandom secureRandom = new SecureRandom();

    public static String randomString(Integer count) {
        return RandomStringUtils.random(count, 0, 0, true, true, null, secureRandom);
    }

    public static String randomString(Integer count, char[] charset) {
        return RandomStringUtils.random(count, 0, 0, false, false, charset, secureRandom);
    }

    public static String getSHA256Hash(String data) {
        try {
            MessageDigest digest = MessageDigest.getInstance("SHA-256");
            byte[] hash = digest.digest(data.getBytes(StandardCharsets.UTF_8));
            return bytesToHexSHA256(hash);
        } catch (Exception ex) {
            log.error(ex.getMessage());
            throw new RuntimeException(ex);
        }
    }

    private static String bytesToHexSHA256(byte[] hash) {
        StringBuilder hexString = new StringBuilder(2 * hash.length);
        for (int i = 0; i < hash.length; i++) {
            String hex = Integer.toHexString(0xff & hash[i]);
            if (hex.length() == 1) {
                hexString.append('0');
            }
            hexString.append(hex);
        }
        return hexString.toString();
    }
}
