package com.neikeq.kicksemu.utils;

import io.netty.buffer.ByteBuf;

public class Cryptography {
    private static final byte ENCRYPTION_KEY = (byte) 229;
    
    /**
     * Simple XOR encryption.
     *
     * @param buffer the byte array to be decrypted.
     * @param header specifies if the buffer includes the header
     *
     * @return the resulted byte array.
     */
    public static ByteBuf decrypt(ByteBuf buffer, boolean header) {
        for (int i = 0; i < buffer.readableBytes(); i++) {
            // if there is a header we ignore the first 12 bytes (header size)
            if (!header || i >= 12) {
                buffer.setByte(i, buffer.getByte(i) ^ ENCRYPTION_KEY);
            }
        }
        
        return buffer;
    }
}
