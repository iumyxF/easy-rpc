package com.example.rpc.server;

import com.example.rpc.spi.SpiLoader;

/**
 * @author iumyxF
 * @description: HTTP 服务器 工厂
 * @date 2024/6/1 10:47
 */
public class HttpServerFactory {

    static {
        SpiLoader.load(NetServer.class);
    }

    public static NetServer getVertxHttpServer() {
        return getInstance(NetServerKeys.VERTX_HTTP);
    }

    public static NetServer getNettyServer() {
        return getInstance(NetServerKeys.NETTY_HTTP);
    }

    public static NetServer getInstance(String key) {
        return SpiLoader.getInstance(NetServer.class, key);
    }

}