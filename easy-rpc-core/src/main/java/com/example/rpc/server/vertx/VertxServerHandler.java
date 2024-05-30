package com.example.rpc.server.vertx;

import cn.hutool.http.ContentType;
import cn.hutool.http.Header;
import com.example.rpc.model.RpcRequest;
import com.example.rpc.model.RpcResponse;
import com.example.rpc.serializer.JsonSerializer;
import com.example.rpc.serializer.Serializer;
import io.vertx.core.Handler;
import io.vertx.core.buffer.Buffer;
import io.vertx.core.http.HttpServerRequest;
import io.vertx.core.http.HttpServerResponse;

import java.io.IOException;

/**
 * <a href="https://vertx-china.github.io/docs/vertx-web/java/#_re_cap_on_vert_x_core_http_servers"/>
 *
 * @author feng
 * @date 2024/5/13 21:08
 */
public class VertxServerHandler implements Handler<HttpServerRequest> {

    @Override
    public void handle(HttpServerRequest request) {
        JsonSerializer serializer = new JsonSerializer();
        System.out.println("接收到的请求方法: " + request.method() + " ,请求路径: " + request.uri());

        // 异步处理请求
        request.bodyHandler(body -> {
            byte[] bytes = body.getBytes();
            RpcRequest rpcRequest = null;
            try {
                rpcRequest = serializer.deserialize(bytes, RpcRequest.class);
            } catch (IOException e) {
                // 这里不能直接抛异常结束，需要当成异常处理返回给请求端
                e.printStackTrace();
            }

            RpcResponse rpcResponse = new RpcResponse();
            // 空值处理
            if (null == rpcRequest) {
                rpcResponse.setData("The rpc request failed because the request body was null");
                doResponse(request, rpcResponse, serializer);
                return;
            }
            // TODO 调用服务类，获取结果
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