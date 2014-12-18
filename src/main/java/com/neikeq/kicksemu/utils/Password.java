package com.neikeq.kicksemu.utils;

import java.security.NoSuchAlgorithmException;
import java.security.spec.InvalidKeySpecException;
import java.util.Base64;

import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.PBEKeySpec;

public class Password {

    // SHA-256
    private static final String ALGORITHM = "PBKDF2WithHmacSHA256";

    private static byte[] hash(String password, byte[] salt, int iterations, int length)
            throws NoSuchAlgorithmException, InvalidKeySpecException {
        PBEKeySpec spec = new PBEKeySpec(password.toCharArray(), salt, iterations, length * 8);
        SecretKeyFactory skf = SecretKeyFactory.getInstance(ALGORITHM);

        return skf.generateSecret(spec).getEncoded();
    }

    public static boolean validate(String password, String correctPassword)
            throws NoSuchAlgorithmException, InvalidKeySpecException {
        String[] stored = correctPassword.split("\\$");

        int iterations = Integer.parseInt(stored[0]);

        byte[] salt = stored[1].getBytes();
        byte[] correctHash = fromBase64(stored[2]);

        byte[] hash = hash(password, salt, iterations, correctHash.length);

        return compare(correctHash, hash);
    }

    private static byte[] fromBase64(String str) {
        return Base64.getDecoder().decode(str.getBytes());
    }

    private static boolean compare(byte[] a, byte[] b) {
        int result = a.length ^ b.length;

        for (int i = 0; i < a.length && i < b.length; i++) {
            result |= a[i] ^ b[i];
        }

        return result == 0;
    }
}
