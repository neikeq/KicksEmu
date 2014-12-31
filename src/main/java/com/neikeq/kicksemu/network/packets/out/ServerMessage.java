package com.neikeq.kicksemu.network.packets.out;

import com.neikeq.kicksemu.config.Constants;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.ByteBufAllocator;

import java.nio.ByteOrder;

public class ServerMessage {

    private final ByteBuf body;

    public void append(boolean value) {
        body.writeByte(value ? 1 : 0);
    }

    public void append(boolean value, int length) {
        switch (length) {
            case 1:
                append(value);
                break;
            case 2:
                append((short)(value ? 1 : 0));
                break;
            case 4:
                append(value ? 1 : 0);
                break;
            default:
        }
    }
    
    public void append(byte value) {
        body.writeByte(value);
    }
    
    public void append(short value) {
        body.writeShort(value);
    }
    
    public void append(int value) {
        body.writeInt(value);
    }

    public void append(char[] value, int length) {
        int lengthFlag = length > value.length ? value.length : length;

        for (int i = 0; i < lengthFlag; i++) {
            byte cur = (byte)value[i];
            append(cur);
        }

        if (lengthFlag < length) {
            appendZeros(length - lengthFlag);
        }
    }

    public void append(String value, int length) {
        if (value != null)
            append(value.toCharArray(), length);
        else
            appendZeros(length);
    }

    public void appendZeros(int length) {
        if (length > 0)
            body.writeZero(length);
    }

    public void write(int index, short value) {
        body.setShort(index, value);
    }

    public void write(int index, int value) {
        body.setInt(index, value);
    }
    
    public ByteBuf getByteBuf() {
        int size = body.readableBytes() - Constants.HEADER_SIZE;

        // Write the body size to the header
        write(Constants.BODY_SIZE_INDEX, (short)size);

        return body;
    }
    
    public ServerMessage(int messageId) {
        body = ByteBufAllocator.DEFAULT.buffer().order(ByteOrder.LITTLE_ENDIAN);

        // Allocate space for the header
        appendZeros(Constants.HEADER_SIZE);

        append(messageId);
    }
}
