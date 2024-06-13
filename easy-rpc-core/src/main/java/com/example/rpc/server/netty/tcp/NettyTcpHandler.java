package com.example.rpc.server.netty.tcp;

import com.example.rpc.model.RpcRequest;
import com.example.rpc.model.RpcResponse;
import com.example.rpc.protocol.ProtocolMessage;
import com.example.rpc.protocol.ProtocolMessageStatusEnum;
import com.example.rpc.protocol.ProtocolMessageTypeEnum;
import com.example.rpc.registry.LocalRegistry;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;

import java.lang.reflect.Method;

/**
 * @author iumyxF
 * @description:
 * @date 2024/6/13 10:53
 */
public class NettyTcpHandler extends SimpleChannelInboundHandler<ProtocolMessage<RpcRequest>> {

    @Override
    protected void channelRead0(ChannelHandlerContext ctx, ProtocolMessage<RpcRequest> protocolMessage) {
        RpcRequest rpcRequest = protocolMessage.getBody();
        ProtocolMessage.Header header = protocolMessage.getHeader();
        // 处理请求
        // 构造响应结果对象
        RpcResponse rpcResponse = new RpcResponse();
        try {
            // 获取要调用的服务实现类，通过反射调用
            Class<?> implClass = LocalRegistry.get(rpcRequest.getServiceName());
            Method method = implClass.getMethod(rpcRequest.getMethodName(), rpcRequest.getParameterTypes());
            Object result = method.invoke(implClass.newInstance(), rpcRequest.getArgs());
            // 封装返回结果
            rpcResponse.setData(result);
            rpcResponse.setDataType(method.getReturnType());
            rpcResponse.setMessage("ok");
        } catch (Exception e) {
            e.printStackTrace();
            rpcResponse.setMessage(e.getMessage());
            rpcResponse.setException(e);
        }
        // 发送响应，编码
        header.setType((byte) ProtocolMessageTypeEnum.RESPONSE.getKey());
        header.setStatus((byte) ProtocolMessageStatusEnum.OK.getValue());
        ProtocolMessage<RpcResponse> responseProtocolMessage = new ProtocolMessage<>(header, rpcResponse);
        ctx.writeAndFlush(responseProtocolMessage);
    }
}
