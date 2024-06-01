package com.example.rpc.server;

import com.example.rpc.spi.SpiLoader;

/**
 * @author iumyxF
 * @description: HTTP 服务器 工厂
 * @date 2024/6/1 10:47
 */
public class HttpServerFactory {

    static {
        SpiLoader.load(HttpServer.class);
    }

    public static HttpServer getVertxHttpServer() {
        return getInstance(HttpServerKeys.VERTX);
    }

    public static HttpServer getNettyServer() {
        return getInstance(HttpServerKeys.NETTY);
    }

    public static HttpServer getInstance(String key) {
        return SpiLoader.getInstance(HttpServer.class, key);
    }

}