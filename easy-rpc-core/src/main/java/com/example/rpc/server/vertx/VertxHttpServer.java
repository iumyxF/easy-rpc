package com.example.rpc.server.vertx;

import com.example.rpc.server.HttpServer;
import io.vertx.core.AbstractVerticle;

/**
 * Vertx 实现Http 服务器
 * <a href="https://vertx-china.github.io/get-started/"/>
 *
 * @author feng
 * @date 2024/5/13 21:00
 */
public class VertxHttpServer extends AbstractVerticle implements HttpServer {

    @Override
    public void start(int port) {
        vertx.createHttpServer()
                // 使用路由处理所有请求
                .requestHandler(new VertxServerHandler())
                // 开始监听端口
                .listen(port)
                // 打印监听的端口
                .onSuccess(server -> System.out.println("HTTP server started on port " + server.actualPort()))
                .onFailure(server -> System.out.println("HTTP server started unsuccessful"));
    }
}
