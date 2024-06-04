package com.example.rpc.server.vertx.http;

import cn.hutool.core.util.StrUtil;
import cn.hutool.http.ContentType;
import cn.hutool.http.Header;
import com.example.rpc.model.RpcRequest;
import com.example.rpc.model.RpcResponse;
import com.example.rpc.registry.LocalRegistry;
import com.example.rpc.serializer.Serializer;
import com.example.rpc.serializer.SerializerFactory;
import io.vertx.core.Handler;
import io.vertx.core.buffer.Buffer;
import io.vertx.core.http.HttpServerRequest;
import io.vertx.core.http.HttpServerResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.lang.reflect.Method;

/**
 * <a href="https://vertx-china.github.io/docs/vertx-web/java/#_re_cap_on_vert_x_core_http_servers"/>
 *
 * @author iumyxF
 * @date 2024/5/13 21:08
 */
public class VertxServerHandler implements Handler<HttpServerRequest> {

    private static final Logger log = LoggerFactory.getLogger(VertxServerHandler.class);

    @Override
    public void handle(HttpServerRequest request) {
        Serializer serializer = SerializerFactory.getJsonSerializer();
        log.debug("接收到的请求方法: {} ,请求路径: {}", request.method(), request.uri());
        // 异步处理请求
        request.bodyHandler(body -> {
            RpcResponse rpcResponse = new RpcResponse();
            byte[] bytes = body.getBytes();
            RpcRequest rpcRequest;
            try {
                rpcRequest = serializer.deserialize(bytes, RpcRequest.class);
            } catch (IOException e) {
                // 这里不能直接抛异常结束，需要当成异常处理返回给请求端
                e.printStackTrace();
                rpcResponse.setData(e.getMessage());
                doResponse(request, rpcResponse, serializer);
                return;
            }
            // 空值处理
            if (null == rpcRequest || StrUtil.hasBlank(rpcRequest.getServiceName(), rpcRequest.getMethodName())) {
                rpcResponse.setData("The rpc request failed because the request body was null");
                doResponse(request, rpcResponse, serializer);
                return;
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
            // 响应
            doResponse(request, rpcResponse, serializer);
        });
    }

    /**
     * 响应
     *
     * @param request
     * @param rpcResponse
     * @param serializer
     */
    protected void doResponse(HttpServerRequest request, RpcResponse rpcResponse, Serializer serializer) {
        HttpServerResponse httpServerResponse = request.response()
                // 设置HTTP响应是JSON格式
                .putHeader(Header.CONTENT_TYPE.getValue(), ContentType.JSON.getValue());
        try {
            // 序列化
            byte[] serialized = serializer.serialize(rpcResponse);
            httpServerResponse.end(Buffer.buffer(serialized));
        } catch (IOException e) {
            e.printStackTrace();
            httpServerResponse.end(Buffer.buffer());
        }
    }
}