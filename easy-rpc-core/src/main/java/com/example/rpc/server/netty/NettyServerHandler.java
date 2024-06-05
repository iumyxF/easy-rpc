package com.example.rpc.server.netty;

import cn.hutool.core.util.StrUtil;
import com.example.rpc.model.RpcRequest;
import com.example.rpc.model.RpcResponse;
import com.example.rpc.registry.LocalRegistry;
import com.example.rpc.serializer.JsonSerializer;
import com.example.rpc.serializer.Serializer;
import com.example.rpc.server.netty.request.RequestHandler;
import com.example.rpc.server.netty.request.RequestHandlerFactory;
import io.netty.buffer.Unpooled;
import io.netty.channel.ChannelFutureListener;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.handler.codec.http.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.lang.reflect.Method;
import java.nio.charset.StandardCharsets;

/**
 * @author iumyxF
 * @description: netty http 请求处理器
 * @date 2024/5/30 14:24
 */
public class NettyServerHandler extends SimpleChannelInboundHandler<FullHttpRequest> {

    private static final Logger log = LoggerFactory.getLogger(NettyServerHandler.class);

    /**
     * 接收HTTP请求，进行分发处理
     */
    @Override
    protected void channelRead0(ChannelHandlerContext ctx, FullHttpRequest request) {
        JsonSerializer serializer = new JsonSerializer();
        if (null == request) {
            return;
        }
        log.info("请求方法:{} ,请求地址:{}", request.method(), request.uri());
        RpcResponse rpcResponse = new RpcResponse();
        RequestHandler handler = RequestHandlerFactory.getRequestHandler(request.method());
        RpcRequest rpcRequest = handler.handle(request);
        if (null == rpcRequest || StrUtil.hasBlank(rpcRequest.getServiceName(), rpcRequest.getMethodName())) {
            rpcResponse.setData("The rpc request failed , check whether the request method and request parameters are valid");
            doResponse(ctx, request, rpcResponse, serializer);
        }
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
        doResponse(ctx, request, rpcResponse, serializer);
    }

    protected void doResponse(ChannelHandlerContext ctx, FullHttpRequest request, RpcResponse rpcResponse, Serializer serializer) {
        DefaultFullHttpResponse httpResponse;
        try {
            // 序列化
            byte[] serialized = serializer.serialize(rpcResponse);
            httpResponse = new DefaultFullHttpResponse(HttpVersion.HTTP_1_1, HttpResponseStatus.OK, Unpooled.wrappedBuffer(serialized));

        } catch (IOException e) {
            e.printStackTrace();
            httpResponse = new DefaultFullHttpResponse(HttpVersion.HTTP_1_1,
                    HttpResponseStatus.INTERNAL_SERVER_ERROR,
                    Unpooled.wrappedBuffer(e.getMessage().getBytes(StandardCharsets.UTF_8)));
        }

        httpResponse.headers().set(HttpHeaderNames.CONTENT_TYPE, HttpHeaderValues.APPLICATION_JSON);
        httpResponse.headers().setInt(HttpHeaderNames.CONTENT_LENGTH, httpResponse.content().readableBytes());
        boolean keepAlive = HttpUtil.isKeepAlive(request);
        if (!keepAlive) {
            ctx.writeAndFlush(httpResponse).addListener(ChannelFutureListener.CLOSE);
        } else {
            httpResponse.headers().set(HttpHeaderNames.CONNECTION, HttpHeaderValues.KEEP_ALIVE);
            ctx.writeAndFlush(httpResponse);
        }
    }
}