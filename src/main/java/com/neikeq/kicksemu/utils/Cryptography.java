package com.neikeq.kicksemu.utils;

import com.neikeq.kicksemu.config.Constants;
import io.netty.buffer.ByteBuf;

public class Cryptography {

    private static final byte ENCRYPTION_KEY = -27;
    
    /**
     * Simple XOR encryption.
     *
     * @param buffer the byte array to be decrypted.
     *
     * @return the decrypted byte array.
     */
    public static ByteBuf decrypt(ByteBuf buffer) {
        for (int i = 0; i < buffer.readableBytes(); i++) {
            if (i >= Constants.HEADER_SIZE) {
                buffer.setByte(i, buffer.getByte(i) ^ ENCRYPTION_KEY);
            }
        }
        
        return buffer;
    }
}
