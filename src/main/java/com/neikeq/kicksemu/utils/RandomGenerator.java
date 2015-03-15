package com.neikeq.kicksemu.utils;

import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;

public class RandomGenerator {

    public static int randomInt() throws NoSuchAlgorithmException {
        SecureRandom rand = SecureRandom.getInstance("SHA1PRNG");
        rand.nextBytes(new byte[1]);

        return rand.nextInt();
    }

    public static byte[] randomBytes(int length) throws NoSuchAlgorithmException {
        byte[] bytes = new byte[length];

        SecureRandom rand = SecureRandom.getInstance("SHA1PRNG");
        rand.nextBytes(bytes);

        return bytes;
    }
}
