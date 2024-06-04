package com.example.rpc.server.vertx.tcp;

import cn.hutool.core.util.IdUtil;
import cn.hutool.log.Log;
import com.example.rpc.model.RpcRequest;
import com.example.rpc.model.RpcResponse;
import com.example.rpc.model.ServiceMetaInfo;
import com.example.rpc.protocol.*;
import com.example.rpc.serializer.SerializerEnums;
import com.example.rpc.server.vertx.http.VertxServerHandler;
import io.vertx.core.Vertx;
import io.vertx.core.buffer.Buffer;
import io.vertx.core.net.NetClient;
import io.vertx.core.net.NetSocket;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;

/**
 * @author iumyxF
 * @description: Vertx Tcp 请求
 * <a href="https://vertx.io/docs/vertx-core/java/#_writing_tcp_servers_and_clients"/>
 * @date 2024/6/1 15:27
 */
public class VertxTcpClient {

    private static final Logger log = LoggerFactory.getLogger(VertxTcpClient.class);

    /**
     * 发送请求
     *
     * @param rpcRequest      rpc请求
     * @param serviceMetaInfo 服务器信息
     * @return
     * @throws InterruptedException
     * @throws ExecutionException
     */
    public static RpcResponse doRequest(RpcRequest rpcRequest, ServiceMetaInfo serviceMetaInfo) throws InterruptedException, ExecutionException {
        // 发送 TCP 请求
        Vertx vertx = Vertx.vertx();
        NetClient netClient = vertx.createNetClient();
        // 异步获取结果
        CompletableFuture<RpcResponse> responseFuture = new CompletableFuture<>();
        netClient.connect(serviceMetaInfo.getServicePort(), serviceMetaInfo.getServiceHost(),
                result -> {
                    if (!result.succeeded()) {
                        System.err.println("Failed to connect to TCP server");
                        return;
                    }
                    // 发送消息
                    NetSocket socket = result.result();
                    ProtocolMessage<RpcRequest> protocolRequest = buildProtocolMessage(rpcRequest);
                    try {
                        socket.write(Buffer.buffer(ProtocolMessageEncoder.encode(protocolRequest)));
                    } catch (IOException e) {
                        throw new RuntimeException("协议消息编码错误");
                    }
                    // 接收响应
                    TcpBufferHandlerWrapper tcpBufferHandlerWrapper = new TcpBufferHandlerWrapper(buffer -> {
                        try {
                            ProtocolMessage<RpcResponse> rpcResponseProtocolMessage =
                                    (ProtocolMessage<RpcResponse>) ProtocolMessageDecoder.decode(buffer.getBytes());
                            log.info("client 接受到响应 : {}", rpcResponseProtocolMessage);
                            responseFuture.complete(rpcResponseProtocolMessage.getBody());
                        } catch (IOException e) {
                            throw new RuntimeException("协议消息解码错误");
                        }
                    });
                    socket.handler(tcpBufferHandlerWrapper);
                });
        RpcResponse rpcResponse = responseFuture.get();
        // 关闭连接
        netClient.close();
        return rpcResponse;
    }

    /**
     * 构建协议消息体
     *
     * @param rpcRequest rpc请求体
     * @return ProtocolMessage
     */
    private static ProtocolMessage<RpcRequest> buildProtocolMessage(RpcRequest rpcRequest) {
        ProtocolMessage<RpcRequest> protocolMessage = new ProtocolMessage<>();
        ProtocolMessage.Header header = new ProtocolMessage.Header();
        header.setMagic(ProtocolConstant.PROTOCOL_MAGIC);
        header.setVersion(ProtocolConstant.PROTOCOL_VERSION);
        //header.setSerializer((byte) SerializerEnums.getEnumByValue(RpcApplication.getRpcConfig().getSerializer()).getKey());
        // 测试使用
        header.setSerializer((byte) SerializerEnums.JSON.getKey());
        header.setType((byte) ProtocolMessageTypeEnum.REQUEST.getKey());
        // 生成全局请求 ID
        header.setRequestId(IdUtil.getSnowflakeNextId());
        protocolMessage.setHeader(header);
        protocolMessage.setBody(rpcRequest);
        return protocolMessage;
    }
}
