package com.neikeq.kicksemu.network.packets.in;

import com.neikeq.kicksemu.config.Constants;
import io.netty.buffer.ByteBuf;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;

public class ClientMessage {

    private final ByteBuf body;

    private final short size;

    private final int messageId;
    private final int sessionId;

    public byte readByte() {
        return body.readByte();
    }

    public boolean readBoolean() {
        return body.readByte() != 0;
    }
    
    public short readShort() {        
        return body.readShort();
    }
    
    public int readInt() {        
        return body.readInt();
    }

    public int readInt(int index) {
        return body.getInt(index);
    }

    public String readString(int length) {
        byte[] bytes = new byte[length];
        body.readBytes(bytes);

        for (int s = 0; s < length; s++) {
            if (bytes[s] == 0)
                return new String(bytes, 0, s);
        }

        return new String(bytes);
    }

    public void ignoreBytes(int length) {
        body.readerIndex(body.readerIndex() + length);
    }

    public short getSize(byte [] data) {
        return size;
    }

    public int getMessageId() {
        return messageId;
    }

    public int getSessionId() {
        return sessionId;
    }

    public ClientMessage(ByteBuf data) {
        body = data.order(ByteOrder.LITTLE_ENDIAN);

        sessionId = body.getInt(Constants.SESSION_ID_INDEX);
        size = body.getShort(Constants.BODY_SIZE_INDEX);

        body.readerIndex(Constants.HEADER_SIZE);
        messageId = body.readInt();
    }
}
