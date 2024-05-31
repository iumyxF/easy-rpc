package com.example.rpc.server.vertx;

import com.example.rpc.server.HttpServer;
import io.vertx.core.Vertx;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Vertx 实现Http 服务器
 * <a href="https://vertx-china.github.io/get-started/"/>
 * Vertx是基于netty封装的
 *
 * @author iumyxF
 * @date 2024/5/13 21:00
 */
public class VertxHttpServer implements HttpServer {

    private static final Logger log = LoggerFactory.getLogger(VertxHttpServer.class);

    private final static Vertx VERTX = Vertx.vertx();

    @Override
    public void start(int port) {
        VERTX.createHttpServer()
                // 使用路由处理所有请求
                .requestHandler(new VertxServerHandler())
                // 开始监听端口
                .listen(port)
                // 打印监听的端口
                .onSuccess(server -> log.info("http server started on port {}", server.actualPort()))
                .onFailure(server -> log.info("http server started unsuccessful"));

    }

    @Override
    public void stop() {
        if (null != VERTX) {
            VERTX.close();
        }
    }
}
