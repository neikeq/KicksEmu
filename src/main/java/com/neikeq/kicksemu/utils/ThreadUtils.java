package com.neikeq.kicksemu.utils;

public class ThreadUtils {

    public static boolean sleep(long ms) {
        try {
            Thread.sleep(ms);
        } catch (InterruptedException e) {
            return false;
        }

        return true;
    }
}
