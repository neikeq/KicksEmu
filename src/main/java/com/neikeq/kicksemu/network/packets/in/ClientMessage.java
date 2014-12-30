package com.neikeq.kicksemu.network.packets.in;

import com.neikeq.kicksemu.config.Constants;
import io.netty.buffer.ByteBuf;

import java.nio.ByteOrder;

public class ClientMessage {

    private final ByteBuf body;

    private final int messageId;

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

    public ByteBuf getBody() {
        return body;
    }

    public short getSize() {
        return body.getShort(Constants.BODY_SIZE_INDEX);
    }

    public int getMessageId() {
        return messageId;
    }

    public int getSessionId() {
        return body.getInt(Constants.SESSION_ID_INDEX);
    }

    public int getTargetId() {
        return body.getInt(Constants.TARGET_ID_INDEX);
    }

    public ClientMessage(ByteBuf data) {
        body = data.order(ByteOrder.LITTLE_ENDIAN);

        body.readerIndex(Constants.HEADER_SIZE);
        messageId = body.readInt();
    }
}
