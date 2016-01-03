package com.neikeq.kicksemu.network.packets.out;

import com.neikeq.kicksemu.config.Constants;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.ByteBufAllocator;

import java.nio.ByteOrder;
import java.nio.charset.Charset;

public class ServerMessage {

    private final ByteBuf body = ByteBufAllocator.DEFAULT.buffer().order(ByteOrder.LITTLE_ENDIAN);

    public ServerMessage writeBool(boolean value) {
        body.writeByte(value ? 1 : 0);
        return this;
    }

    public ServerMessage writeBool(boolean value, int length) {
        switch (length) {
            case 1:
                writeBool(value);
                break;
            case 2:
                writeShort((short) (value ? 1 : 0));
                break;
            case 4:
                writeInt(value ? 1 : 0);
                break;
            default:
        }

        return this;
    }
    
    public ServerMessage writeByte(byte value) {
        body.writeByte(value);
        return this;
    }
    
    public ServerMessage writeShort(short value) {
        body.writeShort(value);
        return this;
    }
    
    public ServerMessage writeInt(int value) {
        body.writeInt(value);
        return this;
    }

    private ServerMessage writeBytes(byte[] value, int length) {
        int lengthFlag = (length > value.length) ? value.length : length;

        for (int i = 0; i < lengthFlag; i++) {
            writeByte(value[i]);
        }

        if (lengthFlag < length) {
            writeZeros(length - lengthFlag);
        }

        return this;
    }

    public ServerMessage writeString(String value, int length) {
        if (value != null) {
            writeBytes(value.getBytes(Charset.forName("windows-1252")), length);
        } else {
            writeZeros(length);
        }

        return this;
    }

    public ServerMessage writeZeros(int length) {
        if (length > 0)
            body.writeZero(length);
        return this;
    }

    public ServerMessage setShort(int index, short value) {
        body.setShort(index, value);
        return this;
    }

    public ServerMessage setInt(int index, int value) {
        body.setInt(index, value);
        return this;
    }

    public ServerMessage retain() {
        body.retain();
        return this;
    }

    public ServerMessage release() {
        body.release();
        return this;
    }

    public ByteBuf getByteBuf(int targetId) {
        int size = body.readableBytes() - Constants.HEADER_SIZE;

        // Write the body size to the header
        setShort(Constants.BODY_SIZE_INDEX, (short) size);
        setInt(Constants.TARGET_ID_INDEX, targetId);

        return body;
    }
    
    public ServerMessage(int messageId) {
        // Allocate space for the header
        writeZeros(Constants.HEADER_SIZE);
        writeInt(messageId);
    }
}
