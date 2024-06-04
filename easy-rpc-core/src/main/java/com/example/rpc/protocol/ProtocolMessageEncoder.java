package com.example.rpc.protocol;

import com.example.rpc.serializer.Serializer;
import com.example.rpc.serializer.SerializerEnums;
import com.example.rpc.serializer.SerializerFactory;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.ByteBufUtil;
import io.netty.buffer.Unpooled;

import java.io.IOException;

/**
 * @author iumyxF
 * @description: 协议编码
 * @date 2024/6/1 16:27
 */
public class ProtocolMessageEncoder {

    /**
     * 编码
     *
     * @param protocolMessage
     * @return
     * @throws IOException
     */
    public static byte[] encode(ProtocolMessage<?> protocolMessage) throws IOException {
        if (protocolMessage == null || protocolMessage.getHeader() == null) {
            return new byte[0];
        }
        ProtocolMessage.Header header = protocolMessage.getHeader();
        // 依次向缓冲区写入字节
        ByteBuf buffer = Unpooled.directBuffer();
        buffer.writeByte(header.getMagic());
        buffer.writeByte(header.getVersion());
        buffer.writeByte(header.getSerializer());
        buffer.writeByte(header.getType());
        buffer.writeByte(header.getStatus());
        buffer.writeLong(header.getRequestId());
        // 获取序列化器
        SerializerEnums serializerEnum = SerializerEnums.getEnumByKey(header.getSerializer());
        if (serializerEnum == null) {
            throw new RuntimeException("序列化协议不存在");
        }
        Serializer serializer = SerializerFactory.getInstance(serializerEnum.getValue());
        byte[] bodyBytes = serializer.serialize(protocolMessage.getBody());
        // 写入 body 长度和数据
        buffer.writeInt(bodyBytes.length);
        buffer.writeBytes(bodyBytes);
        // 解决buffer.array(); 导致的UnsupportedOperationException
        return ByteBufUtil.getBytes(buffer);
    }
}
