package com.example.rpc.server.netty.tcp;

import com.example.rpc.model.RpcResponse;
import com.example.rpc.protocol.ProtocolMessage;
import com.example.rpc.protocol.ProtocolMessageEncoder;
import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.MessageToByteEncoder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;

/**
 * @author iumyxF
 * @description: rpc响应 编码器
 * @date 2024/6/13 9:50
 */
public class ResponseEncoder extends MessageToByteEncoder<ProtocolMessage<RpcResponse>> {

    private static final Logger log = LoggerFactory.getLogger(ResponseEncoder.class);

    @Override
    protected void encode(ChannelHandlerContext channelHandlerContext, ProtocolMessage<RpcResponse> rpcResponseProtocolMessage, ByteBuf out) {
        try {
            byte[] bytes = ProtocolMessageEncoder.encode(rpcResponseProtocolMessage);
            out.writeBytes(bytes);
            log.info("server 响应成功 ,{}", rpcResponseProtocolMessage);
        } catch (IOException e) {
            throw new RuntimeException("协议消息编码错误");
        }
    }
}
