package com.neikeq.kicksemu.utils;

import com.neikeq.kicksemu.io.Output;
import com.neikeq.kicksemu.io.logging.Level;

public class ThreadUtils {

    // TODO Stop using this <3
    public static void sleep(long ms) {
        try {
            Thread.sleep(ms);
        } catch (InterruptedException e) {
            Output.println("Sleep interrupted exception: " + e.getMessage(), Level.DEBUG);
        }
    }
}
