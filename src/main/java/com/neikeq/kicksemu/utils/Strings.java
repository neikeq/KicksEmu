package com.neikeq.kicksemu.utils;

public class Strings {

    public static String repeatAndSplit(String text, String splitter, int count) {
        return text + new String(new char[count-1]).replace("\0", splitter + text);
    }
}
