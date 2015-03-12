package com.neikeq.kicksemu.utils;

import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;

public class RandomGenerator {

    public static int randomInt() throws NoSuchAlgorithmException {
        SecureRandom rand = SecureRandom.getInstance("SHA1PRNG");
        rand.nextBytes(new byte[1]);

        return rand.nextInt();
    }
}
