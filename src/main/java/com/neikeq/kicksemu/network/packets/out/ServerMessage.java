package com.neikeq.kicksemu.network.packets.out;

import com.neikeq.kicksemu.config.Constants;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.ByteBufAllocator;

import java.nio.ByteOrder;
import java.nio.charset.Charset;

public class ServerMessage {

    private final ByteBuf body = ByteBufAllocator.DEFAULT.buffer().order(ByteOrder.LITTLE_ENDIAN);

    public ServerMessage withResult(short result) {
        writeShort(result);
        return this;
    }

    public void writeBool(boolean value) {
        body.writeByte(value ? 1 : 0);
    }

    public void writeBool(boolean value, int length) {
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
    }
    
    public void writeByte(byte value) {
        body.writeByte(value);
    }
    
    public void writeShort(short value) {
        body.writeShort(value);
    }
    
    public void writeInt(int value) {
        body.writeInt(value);
    }

    private void writeBytes(byte[] value, int length) {
        int lengthFlag = (length > value.length) ? value.length : length;

        for (int i = 0; i < lengthFlag; i++) {
            writeByte(value[i]);
        }

        if (lengthFlag < length) {
            writeZeros(length - lengthFlag);
        }
    }

    public void writeString(String value, int length) {
        if (value != null) {
            writeBytes(value.getBytes(Charset.forName("windows-1252")), length);
        } else {
            writeZeros(length);
        }
    }

    public void writeZeros(int length) {
        if (length > 0) {
            body.writeZero(length);
        }
    }

    public void setShort(int index, short value) {
        body.setShort(index, value);
    }

    public void setInt(int index, int value) {
        body.setInt(index, value);
    }

    public ServerMessage retain() {
        body.retain();
        return this;
    }

    public void release() {
        body.release();
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
