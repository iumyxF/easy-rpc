package com.example.rpc.protocol;

import com.example.rpc.model.RpcRequest;
import com.example.rpc.model.RpcResponse;
import com.example.rpc.serializer.Serializer;
import com.example.rpc.serializer.SerializerEnums;
import com.example.rpc.serializer.SerializerFactory;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.ByteBufUtil;
import io.netty.buffer.Unpooled;

import java.io.IOException;

/**
 * @author iumyxF
 * @description: 协议解码
 * @date 2024/6/1 16:01
 */
public class ProtocolMessageDecoder {

    /**
     * 解码
     *
     * @param bytes 请求信息的二进制数据
     * @return ProtocolMessage
     */
    public static ProtocolMessage<?> decode(byte[] bytes) throws IOException {
        ByteBuf buffer = Unpooled.wrappedBuffer(bytes);
        //标记初始位置
        buffer.markReaderIndex();
        // 解析 Buffer
        ProtocolMessage.Header header = new ProtocolMessage.Header();
        byte magic = buffer.readByte();
        // 校验魔数
        if (magic != ProtocolConstant.PROTOCOL_MAGIC) {
            throw new RuntimeException("消息 magic 非法");
        }
        header.setMagic(magic);
        header.setVersion(buffer.readByte());
        header.setSerializer(buffer.readByte());
        header.setType(buffer.readByte());
        header.setStatus(buffer.readByte());
        header.setRequestId(buffer.readLong());
        header.setBodyLength(buffer.readInt());
        // 解决粘包问题，只读指定长度的数据
        byte[] bodyBytes = ByteBufUtil.getBytes(buffer.readBytes(header.getBodyLength()));

        // 解析消息体
        SerializerEnums serializerEnum = SerializerEnums.getEnumByKey(header.getSerializer());
        if (serializerEnum == null) {
            throw new RuntimeException("序列化消息的协议不存在");
        }
        Serializer serializer = SerializerFactory.getInstance(serializerEnum.getValue());
        ProtocolMessageTypeEnum messageTypeEnum = ProtocolMessageTypeEnum.getEnumByKey(header.getType());
        if (messageTypeEnum == null) {
            throw new RuntimeException("序列化消息的类型不存在");
        }
        switch (messageTypeEnum) {
            case REQUEST:
                RpcRequest request = serializer.deserialize(bodyBytes, RpcRequest.class);
                return new ProtocolMessage<>(header, request);
            case RESPONSE:
                RpcResponse response = serializer.deserialize(bodyBytes, RpcResponse.class);
                return new ProtocolMessage<>(header, response);
            case HEART_BEAT:
                // TODO 心跳
            case OTHERS:
            default:
                throw new RuntimeException("暂不支持该消息类型");
        }
    }
}
