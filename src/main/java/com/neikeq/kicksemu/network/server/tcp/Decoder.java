package com.neikeq.kicksemu.network.server.tcp;

import com.neikeq.kicksemu.config.Constants;
import com.neikeq.kicksemu.utils.Cryptography;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.ByteToMessageDecoder;

import java.nio.ByteOrder;

import java.util.List;

class Decoder extends ByteToMessageDecoder {

    @Override
    protected void decode(ChannelHandlerContext chc, ByteBuf in, List<Object> out)
            throws Exception {

        int length = in.readableBytes();

        if (length >= Constants.HEADER_SIZE) {
            // Save the reader index
            in.markReaderIndex();

            in.readerIndex(in.readerIndex() + Constants.BODY_SIZE_INDEX);

            short dataSize = in.order(ByteOrder.LITTLE_ENDIAN).readShort();
            short totalSize = (short) (dataSize + Constants.HEADER_SIZE);
            
            if (length >= totalSize) {
                in.resetReaderIndex();
                out.add(Cryptography.decrypt(in.readBytes(totalSize), true));
            } else {
                in.resetReaderIndex();
            }
        }
    }
}
