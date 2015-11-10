package com.neikeq.kicksemu.network.packets.in;

import com.neikeq.kicksemu.config.Constants;
import io.netty.buffer.ByteBuf;

import java.nio.ByteOrder;
import java.nio.charset.Charset;

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

    public char[] readChars(int length) {
        char[] chars = new char[length];

        for (int i = 0; i < length; i++) {
            byte cur = body.readByte();

            if (cur != 0) {
                chars[i] = (char)cur;
            } else {
                body.readBytes(length - ++i);
                break;
            }
        }

        return chars;
    }

    public String readString(int length) {
        byte[] bytes = new byte[length];
        body.readBytes(bytes);

        for (int s = 0; s < length; s++) {
            if (bytes[s] == 0) {
                return new String(bytes, 0, s, Charset.forName("windows-1252"));
            }
        }

        return new String(bytes, Charset.forName("windows-1252"));
    }

    public ClientMessage ignoreBytes(int length) {
        body.readerIndex(body.readerIndex() + length);
        return this;
    }

    public ByteBuf getBody() {
        return body;
    }

    public short getBodySize() {
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

    public int getReadableBytes() {
        return body.readableBytes();
    }

    public void printBytes() {
        body.markReaderIndex();

        System.out.println("Client Message: " + getMessageId());
        System.out.printf(" - Body Size: %s, Session Id: %s, Target Id: %s" +
                        System.lineSeparator() + " - Data: ",
                getBodySize(), getSessionId(), getTargetId());

        for (boolean firstStep = true; getReadableBytes() > 0; firstStep = false) {
            System.out.print((firstStep ? "" : " ") + readByte());
        }
        System.out.println();

        body.resetReaderIndex();
    }

    public ClientMessage(ByteBuf data) {
        body = data.order(ByteOrder.LITTLE_ENDIAN);

        body.readerIndex(Constants.HEADER_SIZE);
        messageId = body.readInt();
    }
}
