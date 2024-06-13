package com.example.rpc.server.netty.tcp;

import com.example.rpc.model.RpcRequest;
import com.example.rpc.protocol.ProtocolConstant;
import com.example.rpc.protocol.ProtocolMessage;
import com.example.rpc.protocol.ProtocolMessageDecoder;
import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.ByteToMessageDecoder;

import java.util.List;

/**
 * @author iumyxF
 * @description: rpc 请求解码器
 * @date 2024/6/13 9:49
 */
public class RequestDecoder extends ByteToMessageDecoder {

    /**
     * 接收TCP请求，编码成 ProtocolMessage<RpcRequest>
     *
     * @param ctx ctx
     * @param in  in
     * @param out out
     * @throws Exception
     */
    @Override
    protected void decode(ChannelHandlerContext ctx, ByteBuf in, List<Object> out) throws Exception {
        in.markReaderIndex();
        int readableBytes = in.readableBytes();
        if (readableBytes < ProtocolConstant.MESSAGE_HEADER_LENGTH
                || readableBytes < ProtocolConstant.MESSAGE_HEADER_LENGTH + in.getInt(13)) {
            return;
        }
        // 读取ProtocolMessage
        int dateLength = ProtocolConstant.MESSAGE_HEADER_LENGTH + in.getInt(13);
        byte[] bytes = new byte[dateLength];
        in.readBytes(bytes);
        ProtocolMessage<RpcRequest> requestProtocolMessage = (ProtocolMessage<RpcRequest>) ProtocolMessageDecoder.decode(bytes);
        out.add(requestProtocolMessage);
    }
}
